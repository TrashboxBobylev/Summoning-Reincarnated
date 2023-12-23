package com.shatteredpixel.shatteredpixeldungeon.items.armor;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class ScoutArmor extends Armor {

    protected static final String AC_SPECIAL = "SPECIAL";
    protected static SpiritBow bow;

    {
        levelKnown = true;
        cursedKnown = true;
        defaultAction = AC_SPECIAL;
        image = ItemSpriteSheet.ARMOR_SCOUT;

        bones = false;

        usesTargeting = true;
    }

    public ScoutArmor() {
        super( 1 );
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_SPECIAL);
        return actions;
    }

    public static class ScoutCooldown extends FlavourBuff {
        public int icon() { return BuffIndicator.TIME; }
        public void tintIcon(Image icon) { icon.hardlight(0x2e92a7); }
        public float iconFadePercent() { return Math.max(0, 1 - (visualcooldown() / 25)); }
    };

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_SPECIAL)){
            usesTargeting = true;

            if (hero.belongings.armor != this){
                GLog.warning( Messages.get(ScoutArmor.class, "not_equipped"));
                usesTargeting = false;
                return;
            }

            if (hero.buff(ScoutCooldown.class) != null){
                GLog.warning( Messages.get(ScoutArmor.class, "not_ready"));
                usesTargeting = false;
                return;
            }
            for (Item item : hero.belongings.backpack){
                if (item instanceof SpiritBow){
                    curUser = hero;
                    bow = (SpiritBow) item;
                    GameScene.selectCell(shooter);
                    return;
                }
            }
            GLog.warning( Messages.get(ScoutArmor.class, "no_bow"));
            usesTargeting = false;
        }
    }

    @Override
    public int DRMax(int lvl){
        if (Dungeon.isChallenged(Challenges.NO_ARMOR)){
            return 1 + tier + lvl + augment.defenseFactor(lvl);
        }

        int max = 3 + lvl + augment.defenseFactor(lvl);
        if (lvl > max){
            return ((lvl - max)+1)/2;
        } else {
            return max;
        }
    }

    @Override
    public int DRMin(int lvl){
        if (Dungeon.isChallenged(Challenges.NO_ARMOR)){
            return 0;
        }

        int max = DRMax(lvl);
        if (lvl >= max){
            return (lvl - max) + 1;
        } else {
            return lvl + 1;
        }
    }

//    @Override
//    public float speedFactor(Char owner, float speed) {
//        float speedFactor = super.speedFactor(owner, speed);
//        if (owner instanceof Hero) {
//            if (((Hero) owner).belongings.armor instanceof ScoutArmor &&
//                    ((Hero) owner).belongings.armor.level() == 2) {
//                speedFactor *= 2;
//            }
//        }
//        return speedFactor;
//    }

    private CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target != null && bow != null) {
                SpiritBow.superShot = true;
                bow.knockArrow().cast(curUser, target);
                float duration = 25f;
//                switch (level()){
//                    case 1: duration = 13.3f; break;
//                }
                Buff.affect(curUser, ScoutCooldown.class, duration);
            }
        }
        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };


}
