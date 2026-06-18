/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Summoning Pixel Dungeon Reincarnated
 * Copyright (C) 2023-2026 Trashbox Bobylev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EffectBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.GameMath;

import java.util.ArrayList;

public class SubtilitasSigil extends Artifact {
    {
        image = ItemSpriteSheet.SIGIL;

        levelCap = 5;

        charge = 0;
        partialCharge = 0;
        chargeCap = 100;

        defaultAction = AC_USE;
    }

    public static final String AC_USE = "USE";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );
        if (isEquipped(hero) && charge >= 50 && hero.buff(MagicImmune.class) == null && !cursed)
            actions.add(AC_USE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (hero.buff(MagicImmune.class) != null) return;

        if (action.equals(AC_USE)){

            curUser = hero;

            if (!isEquipped( hero )) {
                GLog.i( Messages.get(Artifact.class, "need_to_equip") );
                QuickSlotButton.cancel();

            } else if (charge < 50) {
                GLog.i( Messages.get(this, "no_charge") );
                QuickSlotButton.cancel();

            } else if (cursed) {
                GLog.warning( Messages.get(this, "cursed") );
                QuickSlotButton.cancel();

            } else {
                if (type() == 2){
                    charge -= 50;
                    Buff.affect(hero, ForceBuff.class, 50f);
                    hero.sprite.operate(hero.pos);
                    Sample.INSTANCE.play(Assets.Sounds.READ);
                    Enchanting.show(Dungeon.hero, new Item(){
                        @Override
                        public int image() {
                            KindOfWeapon wep = Dungeon.hero.belongings.weapon();
                            if (wep != null)
                                return wep.image();
                            else
                                return ItemSpriteSheet.WEAPON_HOLDER;
                        }

                        @Override
                        public ItemSprite.Glowing glowing() {
                            return new ItemSprite.Glowing(0xFF2A00);
                        }
                    });
                } else {
                    GameScene.selectCell(caster);
                }
                Item.updateQuickslot();
            }

        }
    }

    public String desc() {
        String desc = super.desc();

        if (isEquipped( Dungeon.hero )){
            desc += "\n\n";
            if (cursed)
                desc += getTypeBasedString("desc_cursed", type());
            else {
                desc += getTypeBasedString("desc_equipped", type(), 4 + level()*2, GameMath.printAverage(4 + level()*2, 24 + level()*8));
                if (level() < levelCap){
                    desc += "\n\n" + Messages.get(this, "desc_hint");
                }
            }
        }
        return desc;
    }

    public CellSelector.Listener caster = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            if (cell != null) {
                curUser.spend(1f);
                curUser.sprite.idle();
                curUser.sprite.zap(cell);
                Sample.INSTANCE.play(Assets.Sounds.RAY);

                final Ballistica bolt = new Ballistica(curUser.pos, cell, Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);

                int maxDist = 3 + level();
                int dist = Math.min(bolt.dist, maxDist);

                final ConeAOE cone = new ConeAOE(bolt, dist, 60, Ballistica.STOP_SOLID | Ballistica.STOP_TARGET | Ballistica.IGNORE_SOFT_SOLID);

                //cast to cells at the tip, rather than all cells, better performance.
                for (Ballistica ray : cone.rays) {
                    curUser.sprite.parent.add(
                            new Beam.RedRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(ray.collisionPos)));
                }
                for (int c : cone.cells) {
                    Char ch = Actor.findChar(c);
                    if (ch != null) {

                        Buff.affect(ch, EnrageBuff.class, 3 + level());
                        Buff.affect(ch, Amok.class, 2 + level()*0.75f);
                    }
                }

                curUser.next();
                charge -= 50;
            }

        }

        @Override
        public String prompt() {
            return Messages.get(SubtilitasSigil.class, "prompt");
        }
    };

    @Override
    protected ArtifactBuff passiveBuff() {
        return new Recharge();
    }

    public class Recharge extends ArtifactBuff {

        public void gainExp(int exp){
            SubtilitasSigil.this.exp += exp;
            target.sprite.emitter().burst(FlameParticle.FACTORY, 5);
            if (SubtilitasSigil.this.exp > 5 + (level()+1)*8 && level() < levelCap){
                SubtilitasSigil.this.exp = 0;
                GLog.positive( Messages.get(SubtilitasSigil.class, "level_up") );
                Catalog.countUse(MirrorOfFates.class);
                upgrade();
                updateQuickslot();
            }
        }

        @Override
        public boolean act() {

            spend( TICK );

            LockedFloor lock = target.buff(LockedFloor.class);
            if (charge < chargeCap && !cursed && (lock == null || lock.regenOn())) {
                //600 turns to a full charge
                partialCharge += (1/6f) * RingOfEnergy.artifactChargeMultiplier(target);
                while (partialCharge >= 1){
                    charge++;
                    partialCharge--;
                    if (charge >= chargeCap){
                        charge = chargeCap;
                        partialCharge = 0f;
                        updateQuickslot();
                    }
                }
            }

            updateQuickslot();

            return true;
        }

        public int armedDamageBonus(){
            if (isCursed())
                return -target.HT/10;

            ForceBuff buff = target.buff(ForceBuff.class);
            if (buff != null){
                return 4 + level()*2;
            }

            return 0;
        }

        public int unarmedDamageBonus(){
            if (isCursed())
                return Hero.heroDamageIntRange(1, Math.max((Dungeon.hero.STR()-9)/2, 2));

            ForceBuff buff = target.buff(ForceBuff.class);
            if (buff != null){
                return Hero.heroDamageIntRange(4 + level()*2, 24 + level()*9);
            }

            return 0;
        }
    }

    @Override
    public void charge(Hero target, float amount) {
        if (charge < chargeCap) {
            partialCharge += amount;
            while (partialCharge >= 1){
                partialCharge -= 1;
                charge += 1;
                if (charge == chargeCap) {
                    partialCharge = 0;
                }
            }
        }
    }

    @Override
    public void type(int type) {
        super.type(type);
        if (type == 3){
            cursed = true;
        }
    }

    @Override
    public void uncurse() {
        super.uncurse();
        if (type() == 3){
            type(1);
        }
    }

    @Override
    public boolean canBeCurseInfused() {
        return true;
    }

    public static class EnrageBuff extends EffectBuff {

        {
            announced = true;
            type = Buff.buffType.NEGATIVE;
        }

        @Override
        public int icon() {
            return BuffIndicator.FURY;
        }
    }

    public static class ForceBuff extends EffectBuff {

        {
            announced = true;
            type = buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return BuffIndicator.FURY;
        }
    }
}
