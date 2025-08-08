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

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Empowered;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulSparking;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.generic.AttunementBooster;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.conjurer.Ascension;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Chicken;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.items.AttunementItem;
import com.shatteredpixel.shatteredpixeldungeon.items.ChargingItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Rankable;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.WeaponEnchantable;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuickBehavior;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public abstract class Staff extends Item implements AttunementItem, ChargingItem, Rankable, WeaponEnchantable {

    {
        stackable = false;
        defaultAction = AC_SUMMON;
    }

    protected Minion minion = null;
    protected int minionID = 0;

    public int getMinionID(){
        return minionID;
    }

    public BalanceTable table;
    public Class<? extends Minion> minionType;
    public int tier;
    public Weapon.Augment augment = Weapon.Augment.NONE;
    public Weapon.Enchantment enchantment;
    public boolean masteryPotionBonus = false;

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

    public void charge( Char owner, float chargeScaleFactor ) {
        charge(owner);
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
            return currentMinionStatus();
        }
    }

    public String currentMinionStatus(){
        return ((minion.HP*100) / minion.HT) + "%";
    }

    public Minion.BehaviorType defaultBehaviorType(){
        return Minion.BehaviorType.REACTIVE;
    }

    public Minion.BehaviorType[] availableBehaviorTypes(){
        return Minion.BehaviorType.values();
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

    public Minion minion(){
        return minion;
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
    public float ATUReq() {
        float atuReq = ATUReq(0);
        if (minion != null){
            for (AttunementBooster boost : minion.genericBuffs(AttunementBooster.class)){
                atuReq -= boost.boost();
            }
        }
        return atuReq;
    }

    @Override
    public int ATUReq(int lvl) {
        int i = tier;
        if (masteryPotionBonus)
            i -= 2;
        return i;
    }

    @Override
    public boolean hasMastery() {
        return masteryPotionBonus;
    }

    @Override
    public void giveMastery() {
        masteryPotionBonus = true;
    }

    public float powerLevel(){
        return Math.max(0, (Dungeon.hero == null ? 0 : Dungeon.hero.ATU()) - ATUReq());
    }

    @Override
    public int level() {
        return (int)powerLevel();
    }

    @Override
    public int visiblyUpgraded() {
        return 0;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    public int hp(int lvl){
        switch (lvl) {
            case 1: return Math.round(table.hp1 + table.hpInc1 * powerLevel());
            case 2: return Math.round(table.hp2 + table.hpInc2 * powerLevel());
            case 3: return Math.round(table.hp3 + table.hpInc3 * powerLevel());
        }
        return 0;
    }

    public int minionMin() {
        return Math.round(minionMin(rank()));
    }

    public int minionMin(int lvl) {
        int dmg = 0;
        switch (lvl) {
            case 1: dmg = Math.round(table.min1 + table.minInc1 * powerLevel()); break;
            case 2: dmg = Math.round(table.min2 + table.minInc2 * powerLevel()); break;
            case 3: dmg = Math.round(table.min3 + table.minInc3 * powerLevel()); break;
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
            case 1: dmg = Math.round(table.max1 + table.maxInc1 * powerLevel()); break;
            case 2: dmg = Math.round(table.max2 + table.maxInc2 * powerLevel()); break;
            case 3: dmg = Math.round(table.max3 + table.maxInc3 * powerLevel()); break;
        }
        if (Dungeon.isChallenged(Conducts.Conduct.PACIFIST)) dmg /= 3;
        return dmg;
    }

    @Override
    public String getRankMessage(int rank){
        String rankMessage = generalRankMessage(rank);
        if (!minionDescription(rank).startsWith("!!"))
            rankMessage += "\n\n" + minionDescription(rank);
        return rankMessage;
    }

    protected String generalRankMessage(int rank) {
        return Messages.get(this, "rank" + rank,
                hp(rank),
                GameMath.printAverage(minionMin(rank), minionMax(rank))
        );
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

        if (hero.buff(MagicImmune.class) != null){
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
                    GameScene.show( new WndQuickBehavior( this ) );
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
            if (!Dungeon.isChallenged(Conducts.Conduct.NO_MAGIC)) {
                minion = minionType.getDeclaredConstructor().newInstance();
            } else {
                minion = new Chicken();
            }
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
            this.onSummoningMinion(minion);
            minion.enchantment = enchantment;
            minion.augment = augment;
            minion.rank = rank();
            minion.behaviorType = defaultBehaviorType();
            minion.setMaxHP(hp(rank()));
            this.customizeMinion(minion);
            if (owner.hasTalent(Talent.NEWBORN_MOTIVATION)){
                Buff.affect(minion, Empowered.class, 25 * owner.pointsInTalent(Talent.NEWBORN_MOTIVATION));
            }
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

        Catalog.countUse(getClass());
    }

    public void onSummoningMinion(Minion minion){ }
    public void customizeMinion(Minion minion){ }

    protected int chargeTurns = 400;

    public int getChargeTurns() {
        return chargeTurns;
    }

    // same as charging, unless minion heals faster than staff charges up
    public int getRegenerationTurns() {
        return getChargeTurns();
    }

    @Override
    public String info() {
        String info = desc();

        if (isIdentified()) {
            float robeBonus = 1f;
            if (Dungeon.hero.buff(SoulSparking.class) != null) robeBonus = SoulSparking.empowering();
            info += "\n\n" + Messages.get(this, "stats_known",
                    tier,
                    ATUReq(0),
                    GameMath.printAverage(
                        augment.damageFactor(Math.round(minionMin()*robeBonus)),
                        augment.damageFactor(Math.round(minionMax()*robeBonus))
                    ),
                    (hp(rank())));
        } else {
            info += "\n\n" + Messages.get(this, "stats_unknown", tier, ATUReq(0), GameMath.printAverage(minionMin(1), minionMax(1)), hp(1));
        }
        if (Dungeon.hero != null && ATUReq() > Dungeon.hero.ATU()) {
            info += "\n\n" + Messages.get(Staff.class, "too_heavy_uh");
        }

        String statsInfo = statsDesc();
        if (!statsInfo.equals("")) info += "\n\n" + statsInfo;

        if (minion != null && minion.isAlive())
            info += "\n\n" + Messages.get(this, "behavior", minion.behaviorType.name());

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

    protected String statsDesc() {
        return Messages.get(this, "stats_desc");
    }

    @Override
    public String name() {
        return enchantment != null && (cursedKnown || !enchantment.curse()) ? enchantment.name( super.name() ) : super.name();
    }

    @Override
    public Item random() {
        rank(Random.Int(1, 4));

        //25% chance to be cursed
        //10% chance to be enchanted
        float effectRoll = Random.Float();
        if (effectRoll < 0.25f) {
            enchant(Weapon.Enchantment.randomCurse());
            cursed = true;
        } else if (effectRoll >= 0.9f){
            enchant();
        }

        return this;
    }

    @Override
    public int value() {
        int price = 30 * (tier-1);
        if (hasGoodEnchant()) {
            price *= 1.5;
        }
        if (cursedKnown && (cursed || hasCurseEnchant())) {
            price /= 2;
        }
        if (price < 1) {
            price = 1;
        }
        return price;
    }

    @Override
    public Weapon.Enchantment getEnchantment() {
        return enchantment;
    }

    @Override
    public void setEnchantment(Weapon.Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    public ItemSprite.Glowing glowing() {
        return enchantment != null && (cursedKnown || !enchantment.curse()) ? enchantment.glowing() : null;
    }

    private static final String CUR_CHARGES = "cur_changes";
    private static final String PARTIAL_CHARGE = "partial_change";
    private static final String MINION_ID = "minionID";
    private static final String RANK = "rank";
    private static final String AUGMENT	        = "augment";
    private static final String ENCHANTMENT	    = "enchantment";
    private static final String MASTERY_POTION_BONUS = "mastery_potion_bonus";


    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CUR_CHARGES, curCharges);
        bundle.put(PARTIAL_CHARGE, partialCharge);
        bundle.put(MINION_ID, minionID);
        bundle.put(RANK, rank);
        bundle.put(ENCHANTMENT, enchantment );
        bundle.put(AUGMENT, augment );
        bundle.put( MASTERY_POTION_BONUS, masteryPotionBonus );
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        curCharges = bundle.getInt(CUR_CHARGES);
        partialCharge = bundle.getFloat(PARTIAL_CHARGE);
        minionID = bundle.getInt(MINION_ID);
        rank = bundle.getInt(RANK);
        augment = bundle.getEnum(AUGMENT, Weapon.Augment.class);
        enchantment = (Weapon.Enchantment)bundle.get( ENCHANTMENT );
        if (bundle.contains(MASTERY_POTION_BONUS))
            masteryPotionBonus = bundle.getBoolean(MASTERY_POTION_BONUS);
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
                    float healing = (float) minion.HT / getRegenerationTurns();
                    if (target.buff(Ascension.AscendBuff.class) != null && Dungeon.hero.hasTalent(Talent.CHARITY)){
                        healing *= 2;
                    }
                    partialCharge += healing;
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
            this.hp1 = hp1; this.hpInc1 = hp1 / 5; this.min1 = min1; this.max1 = max1; this.minInc1 = min1 / 4; this.maxInc1 = max1 / 4;
            this.hp2 = hp2; this.hpInc2 = hp2 / 5; this.min2 = min2; this.max2 = max2; this.minInc2 = min2 / 4; this.maxInc2 = max2 / 4;
            this.hp3 = hp3; this.hpInc3 = hp3 / 5; this.min3 = min3; this.max3 = max3; this.minInc3 = min3 / 4; this.maxInc3 = max3 / 4;
        }
    }
}
