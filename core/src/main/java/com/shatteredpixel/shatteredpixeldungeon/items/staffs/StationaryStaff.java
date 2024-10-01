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

package com.shatteredpixel.shatteredpixeldungeon.items.staffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.stationary.StationaryMinion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Objects;

public class StationaryStaff extends Staff {
    //because of precise strategy, I need to rewrite some of Staff methods to allow exact placing
    {
        chargeTurns = 550;
    }

    public int maxActions(int rank){
        return 1;
    }

    public static final String AC_PLACE = "PLACE";
    public static final String AC_SUMMON = "SAMMON";

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_SUMMON) || action.equals(AC_PLACE)) {

            curUser = hero;
            curItem = this;
            GameScene.selectCell(placer);

        }
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.remove(Staff.AC_SUMMON);
        actions.add(AC_SUMMON);
        if (minion != null && minion.isAlive()){
            actions.add( AC_PLACE );
        }
        return actions;
    }

    @Override
    public String defaultAction() {
        if (minion != null && minion.isAlive())
            return AC_PLACE;
        else
            return AC_SUMMON;
    }

    @Override
    protected String generalRankMessage(int rank) {
        return Messages.get(this, "rank" + rank,
                hp(rank),
                minionMin(rank),
                minionMax(rank),
                maxActions(rank)
        );
    }

    @Override
    public String currentMinionStatus() {
        DecayTracker resource = minion.buff(DecayTracker.class);
        if (resource == null)
            return super.currentMinionStatus();
        else {
            return Messages.format("%d/%d", resource.decay, resource.maxDecay);
        }
    }

    @Override
    public Minion.BehaviorType defaultBehaviorType() {
        return Minion.BehaviorType.AGGRESSIVE;
    }

    private final Minion.BehaviorType[] stationaryBehaviors = {Minion.BehaviorType.AGGRESSIVE, Minion.BehaviorType.PASSIVE};

    @Override
    public Minion.BehaviorType[] availableBehaviorTypes() {
        return stationaryBehaviors;
    }

    protected static CellSelector.Listener placer = new  CellSelector.Listener() {

        @Override
        public void onSelect( Integer target ) {

            if (target != null) {

                final StationaryStaff staff = (StationaryStaff)curItem;

                if (Actor.findChar( target ) != null || !Dungeon.level.passable[target] || !Dungeon.level.heroFOV[target]){
                    curUser.sprite.zap(0);
                    GLog.i( Messages.get(StationaryStaff.class, "no_space") );
                    return;
                }

                if (target == curUser.pos) {
                    GLog.i( Messages.get(Wand.class, "self_target") );
                    return;
                }

                curUser.sprite.zap(target);

                //okay, this is incredible mess
                //basically it's copy-paste from various wand classes
                //ZAP from summon staff doesn't do damage and serves only as targetting tool for your minions

                if (staff.tryToZap(curUser, target)){
//                    curUser.busy();
                    Invisibility.dispel();
                        try {
                            if (staff.summon(curUser, target)) {
                                Sample.INSTANCE.play( Assets.Sounds.ZAP );
                                staff.staffUsed();
                            }
                        } catch (Exception e) {
                            ShatteredPixelDungeon.reportException(e);
                            GLog.warning( Messages.get(Wand.class, "fizzles") );
                        }
                    curItem.cursedKnown = true;
                }
            }
        }

        @Override
        public String prompt() {
            if (Objects.equals(curItem.defaultAction(), AC_PLACE))
                return Messages.get(StationaryStaff.class, "prompt_place");
            return Messages.get(StationaryStaff.class, "prompt_summon");
        }
    };

    public boolean tryToZap(Hero owner, int target ){

        if (owner.buff(MagicImmune.class) != null){
            GLog.warning( Messages.get(Wand.class, "no_magic") );
            return false;
        }

        if ( curCharges > 0 || (minion != null && minion.isAlive() && target != minion.pos)){
            if (minion != null && minion.isAlive() && minion.buff(DecayTracker.class).amount() <= 1) {
                GLog.warning(Messages.get(StationaryStaff.class, "cannot_move"));
                return false;
            }
            return true;
        } else {
            GLog.warning(Messages.get(Wand.class, "fizzles"));
            return false;
        }
    }

    //summoning logic
    public boolean summon(Hero owner, int target) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        //searching for available space
        //did it before summoning

        int strength = 1;
        if (ATUReq() > owner.ATU())  strength += ATUReq(level()) - owner.ATU();

        //if anything is met, spawn minion
        //if hero do not have enough strength, summoning might fail
        if (strength == 1 || Random.Float() < 1 / (float) (strength * 2)) {
            boolean firstSummon = false;
            if (minion == null || !minion.isAlive()){
                firstSummon = true;
                minion = minionType.getDeclaredConstructor().newInstance();
                minionID = minion.id();
                minion.init(this);
                GameScene.add(minion);
            }
            ScrollOfTeleportation.appear(minion, target);
            minion.setDamage(
                    minionMin(),
                    minionMax());
//            Statistics.summonedMinions++;
            minion.setAttunement(ATUReq());
            this.customizeMinion(minion);
            minion.enchantment = enchantment;
            minion.augment = augment;
            minion.rank = rank();
            StationaryStaff.DecayTracker resource = Buff.affect(minion, StationaryStaff.DecayTracker.class);
            if (firstSummon) {
                minion.behaviorType = defaultBehaviorType();
                minion.setMaxHP(hp(rank()));
                resource.init(maxActions(rank()), maxActions(rank()));
            } else {
                minion.HT = hp(rank());
                ((StationaryMinion)minion).useResource(1);
            }
        } else GLog.warning( Messages.get(Wand.class, "fizzles") );
        return true;
    }

    public static class DecayTracker extends Buff {

        private int decay; private int maxDecay;

        @Override
        public int icon() {
            return BuffIndicator.DECAY;
        }

        public void init(int decay, int maxDecay){
            this.decay = decay;
            this.maxDecay = maxDecay;
        }

        public int amount(){
            return decay;
        }

        public void use(){
            modify(-1);
        }

        public void use(int amount){
            modify(-amount);
        }

        public void heal(){
            modify(1);
        }

        public void heal(int amount){
            modify(amount);
        }

        public void modify(int amount){
            decay = Math.min(decay + amount, maxDecay);
            if (decay <= 0){
                detach();
            }
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (float)(decay) / ((float)maxDecay));
        }

        @Override
        public String iconTextDisplay() {
            return Messages.format("%d/%d", decay, maxDecay);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", decay, maxDecay);
        }

        private static final String DECAY = "decay";
        private static final String MAX_DECAY = "maxDecay";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(DECAY, decay);
            bundle.put(MAX_DECAY, maxDecay);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            decay = bundle.getInt(DECAY);
            maxDecay = bundle.getInt(MAX_DECAY);
        }
    }
}
