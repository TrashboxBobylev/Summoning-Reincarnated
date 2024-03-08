/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
 *
 * Summoning Pixel Dungeon Reincarnated
 * Copyright (C) 2023-2024 Trashbox Bobylev
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

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.AttunementItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Rankable;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.Guidebook;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public abstract class Staff extends Item implements AttunementItem, Rankable {

    {
        stackable = false;
        defaultAction = AC_SUMMON;
    }

    private Minion minion = null;
    private int minionID = 0;

    public int getMinionID(){
        return minionID;
    }

    public BalanceTable table;
    public Class<? extends Minion> minionType;
    public int tier;
    public Weapon.Augment augment = Weapon.Augment.NONE;
    public Weapon.Enchantment enchantment;

    public int curCharges = 1;
    public float partialCharge = 0f;

    public Charger charger;

    public static final String AC_SUMMON = "SUMMON";
    public static final String AC_BEHAVIOR = "BEHAVIOR";

    @Override
    public boolean collect( Bag container ) {
        if (super.collect( container )) {
            if (container.owner != null) {
                charge( container.owner );
                identify();
            }
            return true;
        } else {
            return false;
        }
    }

    public void charge(Char owner ) {
        if (charger == null) charger = new Charger();
        charger.attachTo( owner );
    }

    @Override
    public void onDetach( ) {
        stopCharging();
    }

    public void stopCharging() {
        if (charger != null) {
            charger.detach();
            charger = null;
        }
    }

    @Override
    public String status() {
        updateOnMinion();
        if (minion == null || !minion.isAlive()){
            if (charger != null){
                return Messages.format( "%d%%", curCharges < 1 ? Math.round(partialCharge*100) : 100 );
            }
            return super.status();
        } else {
            return ((minion.HP*100) / minion.HT) + "%";
        }
    }

    public Minion.BehaviorType defaultBehaviorType(){
        return Minion.BehaviorType.REACTIVE;
    }

    private void updateOnMinion() {
        if (minion == null && minionID != 0){
            try {
                Actor a = Actor.findById(minionID);
                if (a != null) {
                    minion = (Minion) a;
                } else {
                    minionID = 0;
                }
            } catch ( ClassCastException e ){
                ShatteredPixelDungeon.reportException(e);
                minionID = 0;
            }
        }
    }

    @Override
    public int icon() {
        updateOnMinion();
        if (minion != null && minion.isAlive()){
            return minion.behaviorType.icon;
        } else return -1;
    }

    int rank = 1;

    @Override
    public int rank() {
        return rank;
    }

    @Override
    public void rank(int rank) {
        this.rank = rank;
    }

    @Override
    public int ATUReq() {
        return ATUReq(0);
    }

    @Override
    public int ATUReq(int lvl) {
        return tier;
    }

    @Override
    public int level() {
        return Math.max(0, (Dungeon.hero == null ? 0 : Dungeon.hero.ATU()) - ATUReq());
    }

    @Override
    public int visiblyUpgraded() {
        return 0;
    }

    public int hp(int lvl){
        switch (lvl) {
            case 1: return table.hp1 + table.hpInc1 * level();
            case 2: return table.hp2 + table.hpInc1 * level();
            case 3: return table.hp3 + table.hpInc1 * level();
        }
        return 0;
    }

    public int minionMin() {
        return Math.round(minionMin(rank()));
    }

    public int minionMin(int lvl) {
        int dmg = 0;
        switch (lvl) {
            case 1: dmg = table.min1 + table.minInc1 * level(); break;
            case 2: dmg = table.min2 + table.minInc2 * level(); break;
            case 3: dmg = table.min3 + table.minInc3 * level(); break;
        }
        if (Dungeon.isChallenged(Conducts.Conduct.PACIFIST)) dmg /= 3;
        return dmg;
    }

    public int minionMax() {
        return Math.round(minionMax(rank()));
    }

    public int minionMax(int lvl) {
        int dmg = 0;
        switch (lvl) {
            case 1: dmg = table.max1 + table.maxInc1 * level(); break;
            case 2: dmg = table.max2 + table.maxInc2 * level(); break;
            case 3: dmg = table.max3 + table.maxInc3 * level(); break;
        }
        if (Dungeon.isChallenged(Conducts.Conduct.PACIFIST)) dmg /= 3;
        return dmg;
    }

    @Override
    public String getRankMessage(int rank){
        String rankMessage = Messages.get(this, "rank" + rank,
                hp(rank),
                minionMin(rank),
                minionMax(rank)
        );
        if (!minionDescription(rank).equals(Messages.NO_TEXT_FOUND))
            rankMessage += "\n\n" + minionDescription(rank);
        return rankMessage;
    }

    public String minionDescription(int rank){
        return Messages.get(this, "minion_desc" + rank);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );
        if (curCharges > 0) {
            actions.add( AC_SUMMON );
        } else if (minion != null && minion.isAlive()){
            actions.add( AC_BEHAVIOR );
        }
        return actions;
    }

    @Override
    public String defaultAction() {
        if (minion != null && minion.isAlive())
            return AC_BEHAVIOR;
        else
            return AC_SUMMON;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (hero.buff(MagicImmune.class) != null || Dungeon.isChallenged(Conducts.Conduct.NO_MAGIC)){
            GLog.warning( Messages.get(Staff.class, "no_magic") );
            return;
        }

        if (action.equals(AC_SUMMON) || action.equals(AC_BEHAVIOR)) {

            curUser = hero;
            try {
                updateOnMinion();
                if (minion == null || !minion.isAlive()) {
                    if (curCharges > 0) summon(hero);
                    else {
                        GLog.warning(Messages.get(Staff.class, "fizzles"));
                    }
                } else {
                    Minion.BehaviorType[] behaviorTypes = Minion.BehaviorType.values();
                    int nextTypeID = minion.behaviorType.ordinal()+1;
                    nextTypeID = nextTypeID > behaviorTypes.length-1 ? 0 : nextTypeID;
                    minion.behaviorType = behaviorTypes[nextTypeID];
                    GLog.highlight( Messages.get(Staff.class, "behavior_switch", minion.name(), minion.behaviorType.name()) );
                    minion.sprite.emitter().burst(MagicMissile.MagicParticle.FACTORY, 8);
                    curUser.spendAndNext(Actor.TICK);
                    Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
                    updateQuickslot();
                }
            } catch (Exception e) {
                ShatteredPixelDungeon.reportException(e);
                GLog.warning( Messages.get(Wand.class, "fizzles") );
            }

        }
    }

    public void summon(Hero owner) throws InstantiationException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        //searching for available space
        ArrayList<Integer> spawnPoints = new ArrayList<Integer>();

        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
            int p = owner.pos + PathFinder.NEIGHBOURS8[i];
            if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
                spawnPoints.add( p );
            }
        }

        if (spawnPoints.size() == 0){
            owner.sprite.zap(0);
            GLog.i( Messages.get(Staff.class, "no_space") );
            return;
        }

        int strength = 1;
        if (ATUReq() > owner.ATU())  strength += ATUReq(level()) - owner.ATU();
        if (cursed) strength *= 3;
        if (strength > 1) cursedKnown = true;

        //if anything is met, spawn minion
        //if hero do not have enough strength, summoning might fail
        if (strength == 1 || Random.Float() < 1 / (float) (strength * 2)) {
            minion = minionType.getDeclaredConstructor().newInstance();
            minionID = minion.id();
            minion.init(this);
            GameScene.add(minion);
            ScrollOfTeleportation.appear(minion, spawnPoints.get(Random.index(spawnPoints)));
            minion.setDamage(
                    minionMin(),
                    minionMax());
//            Statistics.summonedMinions++;
//            if (Statistics.summonedMinions != 0 && !Document.ADVENTURERS_GUIDE.isPageRead("Summoning")){
//                GLog.positive(Messages.get(Guidebook.class, "hint"));
//                GameScene.flashForDocument(GUIDE_SUMMONING);
//            }
            minion.setAttunement(ATUReq());
            this.customizeMinion(minion);
            minion.enchantment = enchantment;
            minion.augment = augment;
            minion.rank = rank();
            minion.behaviorType = defaultBehaviorType();
            minion.setMaxHP(hp(rank()));
        } else GLog.warning( Messages.get(Staff.class, "fizzles") );
        staffUsed();
    }

    protected void staffUsed() {
        if (!isIdentified()) {
            identify();
            Badges.validateItemLevelAquired( this );
        }

        curCharges -= 1;

        updateQuickslot();

        curUser.spendAndNext( 1f );
    }

    public void customizeMinion(Minion minion){ }

    protected int chargeTurns = 400;

    public int getChargeTurns() {
        return chargeTurns;
    }

    @Override
    public String info() {
        String info = desc();

        if (isIdentified()) {
            float robeBonus = 1f;
//            if (Dungeon.hero.buff(Attunement.class) != null) robeBonus = Attunement.empowering();
            info += "\n\n" + Messages.get(Staff.class, "stats_known",
                    tier,
                    ATUReq(),
                    augment.damageFactor(Math.round(minionMin()*robeBonus)),
                    augment.damageFactor(Math.round(minionMax()*robeBonus)),
                    (hp(rank())));
            if (ATUReq() > Dungeon.hero.ATU()) {
                info += " " + Messages.get(Staff.class, "too_heavy_uh");
            }
        } else {
            info += "\n\n" + Messages.get(Staff.class, "stats_unknown", tier, ATUReq(0), minionMin(0), minionMax(0), hp(0));
            if (ATUReq(0) > Dungeon.hero.ATU()) {
                info += " " + Messages.get(AttunementItem.class, "probably_too_heavy");
            }
        }

        String statsInfo = Messages.get(this, "stats_desc");
        if (!statsInfo.equals("")) info += "\n\n" + statsInfo;

        if (minion != null && minion.isAlive())
            info += "\n\n" + Messages.get(Staff.class, "behavior", minion.behaviorType.name());

        switch (augment) {
            case SPEED:
                info += "\n\n" + Messages.get(Staff.class, "faster");
                break;
            case DAMAGE:
                info += "\n\n" + Messages.get(Staff.class, "stronger");
                break;
            case NONE:
        }

        if (enchantment != null && (cursedKnown || !enchantment.curse())){
            info += "\n\n" + Messages.get(Weapon.class, "enchanted", enchantment.name());
            info += " " + Messages.get(enchantment, "desc");
        }

        if (cursedKnown && cursed) {
            info += "\n\n" + Messages.get(Weapon.class, "cursed");
        } else if (!isIdentified() && cursedKnown){
            info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
        }

        return info;
    }

    private static final String CUR_CHARGES = "cur_changes";
    private static final String PARTIAL_CHARGE = "partial_change";
    private static final String MINION_ID = "minionID";


    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CUR_CHARGES, curCharges);
        bundle.put(PARTIAL_CHARGE, partialCharge);
        bundle.put(MINION_ID, minionID);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        curCharges = bundle.getInt(CUR_CHARGES);
        partialCharge = bundle.getFloat(PARTIAL_CHARGE);
        minionID = bundle.getInt(MINION_ID);
    }

    public class Charger extends Buff {

        private static final float CHARGE_BUFF_BONUS = 0.025f;

        @Override
        public boolean attachTo( Char target ) {
            super.attachTo( target );

            return true;
        }

        @Override
        public boolean act() {

            updateOnMinion();

            if (curCharges < 1 && (minion == null || !minion.isAlive()))
                recharge();

            if (minion != null && minion.isAlive() && minion.behaviorType == Minion.BehaviorType.PASSIVE && target.buff(MagicImmune.class) == null){
                if (minion.HP < minion.HT && Regeneration.regenOn()) {
                    partialCharge += ((float) minion.HT / getChargeTurns());
                    updateQuickslot();

                    if (partialCharge > 1) {
                        minion.HP++;
                        partialCharge--;
                        updateQuickslot();
                    }
                } else {
                    partialCharge = 0;
                }

                spend( TICK );

                return true;
            }

            while (partialCharge >= 1 && curCharges < 1) {
                partialCharge--;
                curCharges++;
            }

            if (curCharges == 1){
                partialCharge = 0;
            }

            spend( TICK );

            return true;
        }

        private void recharge(){

            float mod = 1f;

            if (Regeneration.regenOn())
                partialCharge += (1f / getChargeTurns()) * mod;
            updateQuickslot();

            for (Recharging bonus : target.buffs(Recharging.class)){
                if (bonus != null && bonus.remainder() > 0f) {
                    partialCharge += CHARGE_BUFF_BONUS * bonus.remainder();
                }
            }
        }

        public Staff staff(){
            return Staff.this;
        }

        public void gainCharge(float charge) {
            partialCharge += charge/5f;
            while (partialCharge >= 1f) {
                curCharges++;
                partialCharge--;
            }
            curCharges = Math.min(curCharges, 1);
            updateQuickslot();
        }
    }

    public static class BalanceTable {
        int min1; int min2; int min3; int minInc1; int minInc2; int minInc3;
        int max1; int max2; int max3; int maxInc1; int maxInc2; int maxInc3;
        int hp1; int hp2; int hp3; int hpInc1; int hpInc2; int hpInc3;

        BalanceTable(int hp1, int hpInc1, int min1, int minInc1, int max1, int maxInc1,
                     int hp2, int hpInc2, int min2, int minInc2, int max2, int maxInc2,
                     int hp3, int hpInc3, int min3, int minInc3, int max3, int maxInc3){
            this.hp1 = hp1; this.hpInc1 = hpInc1; this.min1 = min1; this.max1 = max1; this.minInc1 = minInc1; this.maxInc1 = maxInc1;
            this.hp2 = hp2; this.hpInc2 = hpInc2; this.min2 = min2; this.max2 = max2; this.minInc2 = minInc2; this.maxInc2 = maxInc2;
            this.hp3 = hp3; this.hpInc3 = hpInc3; this.min3 = min3; this.max3 = max3; this.minInc3 = minInc3; this.maxInc3 = maxInc3;
        }

        BalanceTable(int hp1, int min1, int max1,
                     int hp2, int min2, int max2,
                     int hp3, int min3, int max3){
            this.hp1 = hp1; this.hpInc1 = hp1 / 3; this.min1 = min1; this.max1 = max1; this.minInc1 = min1 / 3; this.maxInc1 = max1 / 3;
            this.hp2 = hp2; this.hpInc2 = hp2 / 3; this.min2 = min2; this.max2 = max2; this.minInc2 = min2 / 3; this.maxInc2 = max2 / 3;
            this.hp3 = hp3; this.hpInc3 = hp3 / 3; this.min3 = min3; this.max3 = max3; this.minInc3 = min3 / 3; this.maxInc3 = max3 / 3;
        }
    }
}
