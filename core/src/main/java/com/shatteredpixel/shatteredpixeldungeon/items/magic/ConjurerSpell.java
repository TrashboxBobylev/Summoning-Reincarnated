/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * Summoning Pixel Dungeon Reincarnated
 * Copyright (C) 2023-2025 Trashbox Bobylev
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

package com.shatteredpixel.shatteredpixeldungeon.items.magic;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttunementBoost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.conjurer.Ascension;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.ShieldHalo;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.TypedItem;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ConjurerBook;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.staffs.Staff;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public abstract class ConjurerSpell extends Item implements TypedItem, ManaSource {

    public enum Alignment {
        OFFENSIVE, BENEFICIAL, NEUTRAL
    }

    public static final String AC_ZAP	    = "ZAP";
    public static final String AC_FAVORITE	= "FAVORITE";
    protected int collision = Ballistica.FRIENDLY_PROJECTILE;
    public Alignment alignment = Alignment.NEUTRAL;

    {
        defaultAction = AC_ZAP;
        unique = true;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    public abstract void effect(Ballistica trajectory);

    protected void fx( Ballistica bolt, Callback callback ) {
        MagicMissile.boltFromChar( Dungeon.hero.sprite.parent,
                MagicMissile.MAGIC_MISSILE,
                Dungeon.hero.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play( Assets.Sounds.ZAP );
    }

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions(hero);
        actions.add( AC_ZAP );
        actions.add( AC_FAVORITE );
        actions.remove( AC_DROP );
        actions.remove( AC_THROW );
        return actions;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public int type() {
        return level()+1;
    }

    @Override
    public void type(int type) {
        level(type -1);
    }

    public int manaCost() {
        int manaCost = manaCost(type());
        if (Dungeon.hero != null && Dungeon.hero.buff(Ascension.AscendBuff.class) != null && Dungeon.hero.pointsInTalent(Talent.MALICE) > 3){
            manaCost *= 0.67f;
        }
        return manaCost;
    }

    public int manaCost(int type){
        return 0;
    }

    public Alignment alignment(){
        return alignment(type());
    }

    public Alignment alignment(int type){
        return alignment;
    }

    public boolean isEmpowered(){
        if (Dungeon.hero != null && Dungeon.hero.buff(Ascension.AscendBuff.class) != null){
            switch (alignment()){
                case OFFENSIVE: return Dungeon.hero.pointsInTalent(Talent.MALICE) > 2;
                case BENEFICIAL: return Dungeon.hero.pointsInTalent(Talent.CHARITY) > 2;
            }
        }
        return false;
    }

    @Override
    public void execute( final Hero hero, String action ) {

        GameScene.cancel();

        if (action.equals( AC_ZAP )) {

            if (tryToZap(Dungeon.hero)) {
                curUser = Dungeon.hero;
                curItem = this;
                GameScene.selectCell(targeter);
            }
        } else if (action.equals( AC_FAVORITE )){

            GLog.positive(Messages.get(this, "set_favorite", trueName()));
            hero.belongings.getItem(ConjurerBook.class).setQuickSpell(this);
            hero.sprite.operate(hero.pos);
        } else
            super.execute(hero, action);
    }

    public boolean tryToZap( Hero owner){

        if (owner.buff(MagicImmune.class) != null){
            GLog.warning( Messages.get(Wand.class, "no_magic") );
            return false;
        }
//        if (owner.buff(SoulWeakness.class) != null){
//            GLog.warning(Messages.get(this, "fizzles"));
//            return false;
//        }

        if ( manaCost() <= (Dungeon.hero.buff(Ascension.AscendBuff.class) != null
                ? Dungeon.hero.buff(Ascension.AscendBuff.class).shielding() : Dungeon.hero.mana)){
            return true;
        } else {
            GLog.warning(Messages.get(this, "fizzles"));
            return false;
        }
    }

    protected void afterZap(Hero owner, Ballistica shot){
        Char thing = shot != null ? Actor.findChar(shot.collisionPos) : null;
        if (owner.hasTalent(Talent.ENERGY_BREAK) && thing != null){
            if (thing.isAlive() && thing.alignment == Char.Alignment.ENEMY){
                Buff.prolong(thing, Talent.EnergyBreakTracker.class, 5f);
            }
        }

        if (owner.hasTalent(Talent.ENERGIZED_SUPPORT) && alignment() == Alignment.BENEFICIAL){
            for (Staff.Charger charger : owner.buffs(Staff.Charger.class)){
                charger.gainCharge((1 + owner.pointsInTalent(Talent.ENERGIZED_SUPPORT))*50f / (float) charger.staff().getChargeTurns());
            }
            Item.updateQuickslot();
        }

        if (owner.hasTalent(Talent.COMBINED_REFILL)){
            Talent.CombinedRefillTracker tracker = owner.buff(Talent.CombinedRefillTracker.class);
            if (tracker == null || tracker.weapon == getClass() || tracker.weapon == null || (!ConjurerSpell.class.isAssignableFrom(tracker.weapon))) {
                Buff.affect(owner, Talent.CombinedRefillTracker.class).weapon = getClass();
            } else {
                tracker.detach();

                ShieldHalo shield;
                GameScene.effect(shield = new ShieldHalo(owner.sprite));
                shield.hardlight(0xEBEBEB);
                shield.putOut();

                int gain = (manaCost() + ((ConjurerSpell)Reflection.newInstance(tracker.weapon)).manaCost());
                Dungeon.hero.changeMana(Math.round(gain*(0.15f*owner.pointsInTalent(Talent.COMBINED_REFILL))));

                ScrollOfRecharging.charge(owner);
            }
        }


        if (owner.hasTalent(Talent.CONCENTRATED_SUPPORT) && thing instanceof Minion){
            float duration = AttunementBoost.DURATION * (alignment == Alignment.OFFENSIVE ? 2f : 1f);
            Buff.prolong(thing, AttunementBoost.class, duration).boost((1 + owner.pointsInTalent(Talent.CONCENTRATED_SUPPORT))*0.5f);
        }
    }

    @Override
    public int targetingPos(Hero user, int dst) {
        if (validateCell(dst))
            return new Ballistica( user.pos, dst, collision).collisionPos;
        return user.pos;
    }

    public boolean validateCell(int pos){
        return true;
    }

    @Override
    public float manaModifier(Char source) {
        return 0.5f;
    }

    public String manaCostDesc(){
        return Messages.get(this,  manaCost() == 0 ? "desc_mana_zero_cost" : "desc_mana_some_cost",
                Messages.get(this, "alignment_" + alignment().name()), manaCost());
    }

    @Override
    public String desc() {
        return fullSpellDesc() + "\n\n" + manaCostDesc();
    }

    public String spellDesc() {
        return Messages.get(this, "desc");
    }

    public String fullSpellDesc(){
        String desc = spellDesc();
        if (!empowermentDesc().startsWith("!!") && Dungeon.hero != null && Dungeon.hero.armorAbility instanceof Ascension)
            desc += "\n\n" + empowermentDesc();
        return desc;
    }

    public String empowermentDesc(){
        return Messages.get(this, "desc_empower");
    }

    @Override
    public String getTypeMessage(int type) {
        String message = Messages.get(this, "mana_cost", manaCost(type)) + "\n" + spellTypeMessage(type);
        if (!empowermentTypeDesc(type).startsWith("!!") && Dungeon.hero != null && Dungeon.hero.armorAbility instanceof Ascension)
            message += "\n\n" + empowermentTypeDesc(type);
        return message;
    }

    public String spellTypeMessage(int type){
        return Messages.get(this, "type" + type);
    }

    public String empowermentTypeDesc(int type){
        return Messages.get(this, "type_empower");
    }

    private  static CellSelector.Listener targeter = new  CellSelector.Listener(){

        @Override
        public void onSelect( Integer target ) {

            if (target != null) {

                //FIXME this safety check shouldn't be necessary
                //it would be better to eliminate the curItem static variable.
                final ConjurerSpell curSpell;
                if (curItem instanceof ConjurerSpell) {
                    curSpell = (ConjurerSpell)curItem;
                } else {
                    return;
                }

                final Ballistica shot = new Ballistica( curUser.pos, target, curSpell.collision);
                int cell = shot.collisionPos;
                if (curSpell.validateCell(cell)) {

                    curUser.sprite.zap(cell);

                    //attempts to target the cell aimed at if something is there, otherwise targets the collision pos.
                    if (Actor.findChar(target) != null)
                        QuickSlotButton.target(Actor.findChar(target));
                    else
                        QuickSlotButton.target(Actor.findChar(cell));

                    curUser.busy();

                    int manaCost = curSpell.manaCost();
                    curUser.changeMana(-manaCost);

                    curSpell.fx(shot, new Callback() {
                        public void call() {
                            curSpell.effect(shot);
                            Invisibility.dispel();
                            curSpell.updateQuickslot();
                            curUser.spendAndNext(1f);
                            if (manaCost != 0)
                                curSpell.afterZap(curUser, shot);
                        }
                    });
                }

            }

        }

        @Override
        public String prompt() {
            return Messages.get(ConjurerSpell.class, "prompt");
        }
    };
}
