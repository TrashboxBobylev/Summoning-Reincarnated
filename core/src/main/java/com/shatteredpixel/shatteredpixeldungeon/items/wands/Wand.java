/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LostInventory;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ScrollEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.DivineSense;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.GuidingLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HolyWeapon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.ShieldHalo;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.AttunementItem;
import com.shatteredpixel.shatteredpixeldungeon.items.ChargingItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Rankable;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.WondrousResin;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.ToyKnife;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.text.DecimalFormat;
import java.util.ArrayList;

public abstract class Wand extends Weapon implements ChargingItem, AttunementItem, Rankable {

	public static final String AC_ZAP	= "ZAP";

	private static final float TIME_TO_ZAP	= 1f;
	
	public int maxCharges = initialCharges();
	public int curCharges = maxCharges;
	public float partialCharge = 0f;
	
	protected Charger charger;
	
	public boolean curChargeKnown = false;
	
	public boolean curseInfusionBonus = false;
	public int resinBonus = 0;

	private static final int USES_TO_ID = 10;
	private float usesLeftToID = USES_TO_ID;
	private float availableUsesToID = USES_TO_ID/2f;

	protected int collisionProperties = Ballistica.FRIENDLY_MAGIC;
	
	{
        hitSound = Assets.Sounds.HIT;
        hitSoundPitch = 1.1f;

		defaultAction = AC_ZAP;
		usesTargeting = true;
		bones = true;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (curCharges > 0 || !curChargeKnown) {
			actions.add( AC_ZAP );
		}
        if (hero.heroClass != HeroClass.MAGE){
            actions.remove(AC_EQUIP);
            actions.remove(AC_UNEQUIP);
        }

		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_ZAP )) {
			
			curUser = hero;
			curItem = this;
			GameScene.selectCell( zapper );
			
		}
	}

	@Override
	public int targetingPos(Hero user, int dst) {
		if (cursed && cursedKnown){
			return new Ballistica(user.pos, dst, Ballistica.MAGIC_BOLT).collisionPos;
		} else {
			return new Ballistica(user.pos, dst, collisionProperties).collisionPos;
		}
	}

	public abstract void onZap(Ballistica attack);

	public abstract void onHit( Char attacker, Char defender, int damage);

	//not affected by enchantment proc chance changers
	public static float procChanceMultiplier( Char attacker ){
		if (attacker.buff(Talent.EmpoweredStrikeTracker.class) != null){
			return 1f + ((Hero)attacker).pointsInTalent(Talent.EMPOWERED_STRIKE)/2f;
		}
		return 1f;
	}

    @Override
    public int STRReq() {
        return Dungeon.hero != null ? Dungeon.hero.STR(): 10;
    }

    @Override
    public int STRReq(int lvl) {
        return Dungeon.hero != null ? Dungeon.hero.STR(): 10;
    }

    @Override
    public float ATUReq() {
        return 1;
    }

    @Override
    public int ATUReq(int lvl) {
        return 1;
    }

    @Override
    public int min(int lvl) {
        boolean isGame = Dungeon.hero != null;
        float base = isGame ? Dungeon.hero.ATU() - 1 : 0;
        return Math.round(1 + base);
    }

    @Override
    public int max(int lvl) {
        boolean isGame = Dungeon.hero != null;
        float base = isGame ? Dungeon.hero.ATU() - 1 : 0;
        return Math.round(7 + base*2);
    }

    @Override
    public boolean showAttunement() {
        return false;
    }

    public boolean tryToZap(Hero owner, int target ){

		if (owner.buff(WildMagic.WildMagicTracker.class) == null && (owner.buff(MagicImmune.class) != null)){
			GLog.w( Messages.get(this, "no_magic") );
			return false;
		}

		//if we're using wild magic, then assume we have charges
		if ( owner.buff(WildMagic.WildMagicTracker.class) != null || curCharges >= chargesPerCast()){
			return true;
		} else {
			GLog.w(Messages.get(this, "fizzles"));
			return false;
		}
	}

	@Override
	public boolean collect( Bag container ) {
		if (super.collect( container )) {
			if (container.owner != null) {
				if (container instanceof MagicalHolster)
					charge( container.owner, ((MagicalHolster) container).HOLSTER_SCALE_FACTOR );
				else
					charge( container.owner );
			}
			return true;
		} else {
			return false;
		}
	}

    @Override
    public void activate(Char ch) {
        charge(ch, 1.6f);
    }

    public float rechargeModifier(int rank){
        switch (rank){
            case 1: return 1.0f;
            case 2: return 1.0f;
            case 3: return 1.0f;
        }
        return 0f;
    }

    public float rechargeModifier(){
        float rechargeModifier = rechargeModifier(rank());
//        if (Dungeon.hero.belongings.armor instanceof ClothArmor){
//            ClothArmor armor = (ClothArmor) Dungeon.hero.belongings.armor;
//            if (armor.level() == 1){
//                rechargeModifier *= 0.667f;
//            }
//        }
        return rechargeModifier;
    }

	public void gainCharge( float amt ){
		gainCharge( amt, false );
	}

	public void gainCharge( float amt, boolean overcharge ){
		partialCharge += amt/rechargeModifier();
		while (partialCharge >= 1) {
			if (overcharge) curCharges = Math.min(maxCharges+(int)amt, curCharges+1);
			else curCharges = Math.min(maxCharges, curCharges+1);
			partialCharge--;
			updateQuickslot();
		}
	}
	
	public void charge( Char owner ) {
		if (charger == null) charger = new Charger();
		charger.attachTo( owner );
	}

	public void charge( Char owner, float chargeScaleFactor ){
		charge( owner );
		charger.setScaleFactor( chargeScaleFactor );
	}

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (attacker instanceof Hero && ((Hero) attacker).hasTalent(Talent.MYSTICAL_CHARGE)){
            Hero hero = (Hero) attacker;
            ArtifactRecharge.chargeArtifacts(hero, hero.pointsInTalent(Talent.MYSTICAL_CHARGE)/2f);
        }

        Talent.EmpoweredStrikeTracker empoweredStrike = attacker.buff(Talent.EmpoweredStrikeTracker.class);
        if (empoweredStrike != null){
            damage = Math.round( damage * (1f + Dungeon.hero.pointsInTalent(Talent.EMPOWERED_STRIKE)/6f));
        }

        if (attacker instanceof Hero && ((Hero)attacker).subClass == HeroSubClass.BATTLEMAGE) {
            if (curCharges < maxCharges) partialCharge += 0.5f/rechargeModifier(rank());
            ScrollOfRecharging.charge((Hero)attacker);
            onHit(attacker, defender, damage);
        }

        if (empoweredStrike != null){
            if (!empoweredStrike.delayedDetach) empoweredStrike.detach();
            if (!(defender instanceof Mob) || !((Mob) defender).surprisedBy(attacker)){
                Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG, 0.75f, 1.2f);
            }
        }
        return super.proc(attacker, defender, damage);
    }

    @Override
    public int reachFactor(Char owner) {
        int reach = super.reachFactor(owner);
        if (owner instanceof Hero
                && this instanceof WandOfDisintegration
                && ((Hero)owner).subClass == HeroSubClass.BATTLEMAGE){
            reach += Math.round(Wand.procChanceMultiplier(owner));
        }
        return reach;
    }

	protected void wandProc(Char target, int chargesUsed){
		wandProc(target, power(), chargesUsed);
	}

	//TODO Consider externalizing char awareness buff
	protected static void wandProc(Char target, float wandLevel, int chargesUsed){
		if (Dungeon.hero.hasTalent(Talent.ARCANE_VISION)) {
			int dur = 5 + 5*Dungeon.hero.pointsInTalent(Talent.ARCANE_VISION);
			Buff.append(Dungeon.hero, TalismanOfForesight.CharAwareness.class, dur).charID = target.id();
		}

		if (target != Dungeon.hero &&
				Dungeon.hero.subClass == HeroSubClass.WARLOCK &&
				//standard 1 - 0.92^x chance, plus 7%. Starts at 15%
				Random.Float() > (Math.pow(0.92f, (wandLevel*chargesUsed)+1) - 0.07f)){
			SoulMark.prolong(target, SoulMark.class, SoulMark.DURATION + wandLevel);
		}

		if (target != Dungeon.hero) {
			Buff.affect(target, Minion.ReactiveTargeting.class, 10f);

		if (Dungeon.hero.subClass == HeroSubClass.PRIEST && target.buff(GuidingLight.Illuminated.class) != null) {
			target.buff(GuidingLight.Illuminated.class).detach();
			target.damage(Dungeon.hero.lvl+5, GuidingLight.INSTANCE);
		}

		if (target.alignment != Char.Alignment.ALLY
				&& Dungeon.hero.heroClass != HeroClass.CLERIC
				&& Dungeon.hero.hasTalent(Talent.SEARING_LIGHT)
				&& Dungeon.hero.buff(Talent.SearingLightCooldown.class) == null){
			Buff.affect(target, GuidingLight.Illuminated.class);
			Buff.affect(Dungeon.hero, Talent.SearingLightCooldown.class, 20f);
		}

		if (target.alignment != Char.Alignment.ALLY
				&& Dungeon.hero.heroClass != HeroClass.CLERIC
				&& Dungeon.hero.hasTalent(Talent.SUNRAY)){
			// 15/25% chance
			if (Random.Int(20) < 1 + 2*Dungeon.hero.pointsInTalent(Talent.SUNRAY)){
				Buff.prolong(target, Blindness.class, 4f);
			}
		}

			if (Dungeon.hero.hasTalent(Talent.ENERGY_BREAK) && Dungeon.hero.heroClass != HeroClass.CONJURER &&
				target.alignment == Char.Alignment.ENEMY){
				Buff.affect(target, Talent.EnergyBreakTracker.class, 5f);
			}

            Buff.detach(target, Talent.FightingWizardryTracker.class);
		}
	}

	public void stopCharging() {
		if (charger != null) {
			charger.detach();
			charger = null;
		}
	}

	@Override
	protected void onThrow(int cell) {
		super.onThrow(cell);
		ToyKnife.processSoulsBurst(this, cell);
	}
	
	@Override
	public Item identify( boolean byHero ) {
		
		curChargeKnown = true;
		super.identify(byHero);
		
		updateQuickslot();
		
		return this;
	}

	public void setIDReady(){
		usesLeftToID = -1;
	}

	public boolean readyToIdentify(){
		return !isIdentified() && usesLeftToID <= 0;
	}
	
	public void onHeroGainExp( float levelPercent, Hero hero ){
		levelPercent *= Talent.itemIDSpeedFactor(hero, this);
		if (!isIdentified() && availableUsesToID <= USES_TO_ID/2f) {
			//gains enough uses to ID over 1 level
			availableUsesToID = Math.min(USES_TO_ID/2f, availableUsesToID + levelPercent * USES_TO_ID/2f);
		}
	}

	@Override
	public String info() {
		String desc = super.info();

		desc += "\n\n" + statsDesc();

        if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.MAGE){
            desc += "\n\n" + Messages.get(Wand.class, "melee", GameMath.printAverage(augment.damageFactor(min()), augment.damageFactor(max())));
        }

        switch (augment) {
            case SPEED:
                desc += " " + Messages.get(Weapon.class, "faster");
                break;
            case DAMAGE:
                desc += " " + Messages.get(Weapon.class, "stronger");
                break;
            case NONE:
        }

		if (resinBonus == 1){
			desc += "\n\n" + Messages.get(Wand.class, "resin_one");
		} else if (resinBonus > 1){
			desc += "\n\n" + Messages.get(Wand.class, "resin_many", resinBonus);
		}

		if (cursed && cursedKnown) {
			desc += "\n\n" + Messages.get(Wand.class, "cursed");
		} else if (!isIdentified() && cursedKnown){
			desc += "\n\n" + Messages.get(Wand.class, "not_cursed");
		}

        if (isEquipped(Dungeon.hero) && !hasCurseEnchant() && Dungeon.hero.buff(HolyWeapon.HolyWepBuff.class) != null
                && (Dungeon.hero.subClass != HeroSubClass.PALADIN || enchantment == null)){
            desc += "\n\n" + Messages.capitalize(Messages.get(Weapon.class, "enchanted", Messages.get(HolyWeapon.class, "ench_name", Messages.get(Enchantment.class, "enchant"))));
            desc += " " + Messages.get(HolyWeapon.class, "ench_desc");
        } else if (enchantment != null && (cursedKnown || !enchantment.curse())){
            desc += "\n\n" + Messages.capitalize(Messages.get(Weapon.class, "enchanted", enchantment.name()));
            if (enchantHardened) desc += " " + Messages.get(Weapon.class, "enchant_hardened");
            desc += " " + enchantment.desc();
        } else if (enchantHardened){
            desc += "\n\n" + Messages.get(Weapon.class, "hardened_no_enchant");
        }

        if (power() > (Dungeon.hero != null ? Dungeon.hero.ATU() - 1 : 0)){
            desc += "\n\n" + Messages.get(Wand.class, "boost",
                    Messages.decimalFormat("#.##", power() - (Dungeon.hero != null ? Dungeon.hero.ATU() - 1 : 0)));
        }

		if (Dungeon.hero != null && Dungeon.hero.subClass == HeroSubClass.BATTLEMAGE){
			desc += "\n\n" + Messages.get(this, "bmage_desc");
		}

        if (charger != null && curCharges < maxCharges){
            desc += "\n\n" + Messages.get(Wand.class, "recharge",
                    new DecimalFormat("#.##").format(
                            charger.getTurnsToCharge() - partialCharge*charger.getTurnsToCharge()));
        }

		return desc;
	}

	public String statsDesc(){
		return Messages.get(this, "stats_desc");
	}

	public String upgradeStat1(int level){
		return null;
	}

	public String upgradeStat2(int level){
		return null;
	}

	public String upgradeStat3(int level){
		return null;
	}
	
	@Override
	public boolean isIdentified() {
		return super.isIdentified() && curChargeKnown;
	}
	
	@Override
	public String status() {
		if (levelKnown) {
			return (curChargeKnown ? curCharges : "?") + "/" + maxCharges;
		} else {
			return null;
		}
	}
	
	@Override
	public Item upgrade() {

		super.upgrade();

		if (Random.Int(3) == 0) {
			cursed = false;
		}

		if (resinBonus > 0){
			resinBonus--;
		}

		updateLevel();
		curCharges = Math.min( curCharges + 1, maxCharges );
		updateQuickslot();
		
		return this;
	}
	
	@Override
	public Item degrade() {
		super.degrade();
		
		updateLevel();
		updateQuickslot();
		
		return this;
	}

    public float power(){
        boolean isGame = Dungeon.hero != null;
        float base = isGame ? Dungeon.hero.ATU() - 1 : 0;
        if (isGame && isEquipped(Dungeon.hero) && Dungeon.hero.buff(Talent.FightingWizardryTracker.class) != null){
            base += Dungeon.hero.buff(Talent.FightingWizardryTracker.class).powerBoost();
        }
        if (charger != null && charger.target != null) {
            if (charger.target.buff(ScrollEmpower.class) != null){
                base += 1.5f;
            }
            if (curCharges == 1 && charger.target instanceof Hero && ((Hero)charger.target).hasTalent(Talent.DESPERATE_POWER)){
                base += 2/3f*((Hero)charger.target).pointsInTalent(Talent.DESPERATE_POWER);
            }
            if (charger.target.buff(WildMagic.WildMagicTracker.class) != null){
                base += 1 + 0.5f*((Hero)charger.target).pointsInTalent(Talent.WILD_POWER); // +2/+2.5/+3/+3.5/+4 at 0/1/2/3/4 talent points
            }
            WandOfMagicMissile.MagicCharge buff = charger.target.buff(WandOfMagicMissile.MagicCharge.class);
            if (buff != null && buff.wandJustApplied() != this){
                return (base+1)*buff.wandJustApplied().powerModifier()-1;
            }
        }
        return base;
    }

    public float powerModifier(int rank){
        switch (rank){
            case 1: return 1.0f;
            case 2: return 1.0f;
            case 3: return 1.0f;
        }
        return 0f;
    }

    public float powerModifier(){
        return powerModifier(rank());
    }

	public void updateLevel() {
		curCharges = Math.min( curCharges, maxCharges );
	}
	
	public int initialCharges() {
		return 5;
	}

	protected int chargesPerCast() {
		return 1;
	}

    @Override
    public int level() {
        return (int)power();
    }

    @Override
    public int visiblyUpgraded() {
        return 0;
    }

    @Override
    public boolean isUpgradable() {
        return false;
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
    public String getRankMessage(int rank) {
        return Messages.get(this, "rank" + rank,
                getRechargeInfo(rank)
        );
    }

    public String getRechargeInfo(int rank) {
        return new DecimalFormat("#.##").format(
                charger == null ? Charger.BASE_CHARGE_DELAY*rechargeModifier(rank) : charger.getTurnsToCharge(rank));
    }

    public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar( curUser.sprite.parent,
				MagicMissile.MAGIC_MISSILE,
				curUser.sprite,
				bolt.collisionPos,
				callback);
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
	}

	public void staffFx(WandParticle particle ){
		particle.color(0xFFFFFF); particle.am = 0.3f;
		particle.setLifespan( 1f);
		particle.speed.polar( Random.Float(PointF.PI2), 2f );
		particle.setSize( 1f, 2f );
		particle.radiateXY(0.5f);
	}

    @Override
    public Emitter emitter() {
        if (!isEquipped(Dungeon.hero)) return null;
        Emitter emitter = new Emitter();
        emitter.pos(11.5f, 1.5f);
        emitter.fillTarget = false;
        emitter.pour(WandParticleFactory, 0.1f);
        return emitter;
    }

    public float timeToZap(){
        return TIME_TO_ZAP;
    }

	public void wandUsed() {
		if (!isIdentified()) {
			float uses = Math.min( availableUsesToID, Talent.itemIDSpeedFactor(Dungeon.hero, this) );
			availableUsesToID -= uses;
			usesLeftToID -= uses;
			if (usesLeftToID <= 0 || Dungeon.hero.pointsInTalent(Talent.SCHOLARS_INTUITION) == 2) {
				if (ShardOfOblivion.passiveIDDisabled()){
					if (usesLeftToID > -1){
						GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready"), name());
					}
					setIDReady();
				} else {
					identify();
					GLog.p(Messages.get(Wand.class, "identify"));
					Badges.validateItemLevelAquired(this);
				}
			}
			if (ShardOfOblivion.passiveIDDisabled()){
				Buff.prolong(curUser, ShardOfOblivion.WandUseTracker.class, 50f);
			}
		}

		//inside staff
		if (charger != null && charger.target == Dungeon.hero && !Dungeon.hero.belongings.contains(this)){
			if (Dungeon.hero.hasTalent(Talent.EXCESS_CHARGE) && curCharges >= maxCharges){
				int shieldToGive = Math.round(buffedLvl()*0.67f*Dungeon.hero.pointsInTalent(Talent.EXCESS_CHARGE));
				Buff.affect(Dungeon.hero, Barrier.class).setShield(shieldToGive);
				Dungeon.hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shieldToGive), FloatingText.SHIELDING);
			}
		}
		
		curCharges -= cursed ? 1 : chargesPerCast();

		//remove magic charge at a higher priority, if we are benefiting from it are and not the
		//wand that just applied it
		WandOfMagicMissile.MagicCharge buff = curUser.buff(WandOfMagicMissile.MagicCharge.class);
		if (buff != null
				&& buff.wandJustApplied() != this){
			buff.detach();
		} else {
			ScrollEmpower empower = curUser.buff(ScrollEmpower.class);
			if (empower != null){
				empower.use();
			}
		}

		//If hero owns wand but it isn't in belongings it must be in the staff
		if (Dungeon.hero.hasTalent(Talent.EMPOWERED_STRIKE)
				&& charger != null && charger.target == Dungeon.hero
				&& !Dungeon.hero.belongings.contains(this)){

			Buff.prolong(Dungeon.hero, Talent.EmpoweredStrikeTracker.class, 10f);
		}

		if (Dungeon.hero.hasTalent(Talent.LINGERING_MAGIC)
				&& charger != null && charger.target == Dungeon.hero){

			Buff.prolong(Dungeon.hero, Talent.LingeringMagicTracker.class, 5f);
		}

		if (Dungeon.hero.hasTalent(Talent.COMBINED_REFILL) && Dungeon.hero.heroClass != HeroClass.CONJURER){
			Talent.CombinedRefillTracker tracker = Dungeon.hero.buff(Talent.CombinedRefillTracker.class);
			if (tracker == null || tracker.weapon == getClass() || tracker.weapon == null || (!Wand.class.isAssignableFrom(tracker.weapon))) {
				Buff.affect(Dungeon.hero, Talent.CombinedRefillTracker.class).weapon = getClass();
			} else {
				tracker.detach();

				ShieldHalo shield;
				GameScene.effect(shield = new ShieldHalo(Dungeon.hero.sprite));
				shield.hardlight(0xEBEBEB);
				shield.putOut();

				Dungeon.hero.belongings.charge((0.125f*Dungeon.hero.pointsInTalent(Talent.COMBINED_REFILL)));

				ScrollOfRecharging.charge(Dungeon.hero);
			}
		}

		if (Dungeon.hero.heroClass != HeroClass.CLERIC
				&& Dungeon.hero.hasTalent(Talent.DIVINE_SENSE)){
			Buff.prolong(Dungeon.hero, DivineSense.DivineSenseTracker.class, Dungeon.hero.cooldown()+1);
		}

		// 10/20/30%
		if (Dungeon.hero.heroClass != HeroClass.CLERIC
				&& Dungeon.hero.hasTalent(Talent.CLEANSE)
				&& Random.Int(10) < Dungeon.hero.pointsInTalent(Talent.CLEANSE)){
			boolean removed = false;
			for (Buff b : Dungeon.hero.buffs()) {
				if (b.type == Buff.buffType.NEGATIVE
						&& !(b instanceof LostInventory)) {
					b.detach();
					removed = true;
				}
			}
			if (removed) new Flare( 6, 32 ).color(0xFF4CD2, true).show( Dungeon.hero.sprite, 2f );
		}

		Invisibility.dispel();
		updateQuickslot();

		curUser.spendAndNext( timeToZap() );
	}
	
	@Override
	public Item random() {
		rank(Random.Int(1, 4));
		
		//30% chance to be cursed
		if (Random.Float() < 0.3f) {
			cursed = true;
		}

		if (Dungeon.isChallenged(Conducts.Conduct.CURSE)){
			cursed = true;
		}

		return this;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		if (resinBonus == 0) return null;

		return new ItemSprite.Glowing(0xFFFFFF, 1f/(float)resinBonus);
	}

	@Override
	public int value() {
		int price = 75;
		if (cursed && cursedKnown) {
			price /= 2;
		}
		if (levelKnown) {
			if (level() > 0) {
				price *= (level() + 1);
			} else if (level() < 0) {
				price /= (1 - level());
			}
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}
	
	private static final String USES_LEFT_TO_ID     = "uses_left_to_id";
	private static final String AVAILABLE_USES      = "available_uses";
	private static final String CUR_CHARGES         = "curCharges";
	private static final String CUR_CHARGE_KNOWN    = "curChargeKnown";
	private static final String PARTIALCHARGE       = "partialCharge";
	private static final String CURSE_INFUSION_BONUS= "curse_infusion_bonus";
	private static final String RESIN_BONUS         = "resin_bonus";
    private static final String RANK = "rank";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( USES_LEFT_TO_ID, usesLeftToID );
		bundle.put( AVAILABLE_USES, availableUsesToID );
		bundle.put( CUR_CHARGES, curCharges );
		bundle.put( CUR_CHARGE_KNOWN, curChargeKnown );
		bundle.put( PARTIALCHARGE , partialCharge );
		bundle.put( CURSE_INFUSION_BONUS, curseInfusionBonus );
		bundle.put( RESIN_BONUS, resinBonus );
        bundle.put(RANK, rank);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		usesLeftToID = bundle.getInt( USES_LEFT_TO_ID );
		availableUsesToID = bundle.getInt( AVAILABLE_USES );
		curseInfusionBonus = bundle.getBoolean(CURSE_INFUSION_BONUS);
		resinBonus = bundle.getInt(RESIN_BONUS);

		updateLevel();

		curCharges = bundle.getInt( CUR_CHARGES );
		curChargeKnown = bundle.getBoolean( CUR_CHARGE_KNOWN );
		partialCharge = bundle.getFloat( PARTIALCHARGE );
        rank = bundle.getInt(RANK);
	}
	
	@Override
	public void reset() {
		super.reset();
		usesLeftToID = USES_TO_ID;
		availableUsesToID = USES_TO_ID/2f;
	}

	public int collisionProperties(int target){
		if (cursed)     return Ballistica.MAGIC_BOLT;
		else            return collisionProperties;
	}

	public static class PlaceHolder extends Wand {

		{
			image = ItemSpriteSheet.WAND_HOLDER;
		}

		@Override
		public boolean isSimilar(Item item) {
			return item instanceof Wand;
		}

		@Override
		public void onZap(Ballistica attack) {}
		public void onHit(Char attacker, Char defender, int damage) {}

		@Override
		public String info() {
			return "";
		}
	}
	
	protected static CellSelector.Listener zapper = new  CellSelector.Listener() {
		
		@Override
		public void onSelect( Integer target ) {
			
			if (target != null) {
				
				//FIXME this safety check shouldn't be necessary
				//it would be better to eliminate the curItem static variable.
				final Wand curWand;
				if (curItem instanceof Wand) {
					curWand = (Wand) Wand.curItem;
				} else {
					return;
				}
                if (curWand instanceof WandOfDisintegration)
                    Ballistica.REFLECTION = 4;

				final Ballistica shot = new Ballistica( curUser.pos, target, curWand.collisionProperties(target));
				int cell = shot.collisionPos;
				
				if (target == curUser.pos || cell == curUser.pos) {
					if (target == curUser.pos && curUser.hasTalent(Talent.SHIELD_BATTERY)){

						if (curUser.buff(MagicImmune.class) != null){
							GLog.w( Messages.get(Wand.class, "no_magic") );
							return;
						}

						if (curWand.curCharges == 0){
							GLog.w( Messages.get(Wand.class, "fizzles") );
							return;
						}

						float shield = curUser.HT * (0.04f*curWand.curCharges);
						if (curUser.pointsInTalent(Talent.SHIELD_BATTERY) == 2) shield *= 1.5f;
						Buff.affect(curUser, Barrier.class).setShield(Math.round(shield));
						curUser.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(Math.round(shield)), FloatingText.SHIELDING);
						curWand.curCharges = 0;
						curUser.sprite.operate(curUser.pos);
						Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
						ScrollOfRecharging.charge(curUser);
						updateQuickslot();
						curUser.spendAndNext(Actor.TICK);
						return;
					}
					GLog.i( Messages.get(Wand.class, "self_target") );
					return;
				}

				curUser.sprite.zap(cell);

				//attempts to target the cell aimed at if something is there, otherwise targets the collision pos.
				if (Actor.findChar(target) != null)
					QuickSlotButton.target(Actor.findChar(target));
				else
					QuickSlotButton.target(Actor.findChar(cell));
				
				if (curWand.tryToZap(curUser, target)) {
					
					curUser.busy();

					//backup barrier logic
					//This triggers before the wand zap, mostly so the barrier helps vs skeletons
					if (curUser.hasTalent(Talent.BACKUP_BARRIER)
							&& curWand.curCharges == curWand.chargesPerCast()
							&& curWand.charger != null && curWand.charger.target == curUser){

						//regular. If hero owns wand but it isn't in belongings it must be in the staff
						if (curUser.heroClass == HeroClass.MAGE && curUser.belongings.weapon() == curWand){
							//grants 3/5 shielding
							int shieldToGive = 1 + 2 * Dungeon.hero.pointsInTalent(Talent.BACKUP_BARRIER);
							Buff.affect(Dungeon.hero, Barrier.class).setShield(shieldToGive);
							Dungeon.hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shieldToGive), FloatingText.SHIELDING);

						//metamorphed. Triggers if wand is highest level hero has
						} else if (curUser.heroClass != HeroClass.MAGE) {
							boolean highest = true;
							for (Item i : curUser.belongings.getAllItems(Wand.class)){
								if (i.level() > curWand.level()){
									highest = false;
								}
							}
							if (highest){
								//grants 3/5 shielding
								int shieldToGive = 1 + 2 * Dungeon.hero.pointsInTalent(Talent.BACKUP_BARRIER);
								Buff.affect(Dungeon.hero, Barrier.class).setShield(shieldToGive);
								Dungeon.hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shieldToGive), FloatingText.SHIELDING);
							}
						}
					}
					
					if (curWand.cursed){
						if (!curWand.cursedKnown){
							GLog.n(Messages.get(Wand.class, "curse_discover", curWand.name()));
						}
						CursedWand.cursedZap(curWand,
								curUser,
								new Ballistica(curUser.pos, target, Ballistica.MAGIC_BOLT),
								new Callback() {
									@Override
									public void call() {
										curWand.wandUsed();
									}
								});
					} else {
						if (Dungeon.isChallenged(Conducts.Conduct.NO_MAGIC)){
							MagicMissile.boltFromChar( curUser.sprite.parent,
									MagicMissile.RAINBOW,
									curUser.sprite,
									shot.collisionPos,
									() -> {
										Emitter emitter = CellEmitter.center(shot.collisionPos);
										emitter.burst(RainbowParticle.SUPER_BURST, Random.Int(60, 120));
										for (int i : PathFinder.NEIGHBOURS9){
											Char ch = Actor.findChar( shot.collisionPos + i );
											if (ch != null) {
												ch.damage(1 + Dungeon.scalingDepth() / 3, curUser);
											}
										}
										Sample.INSTANCE.play( Assets.Sounds.BLAST, 1.0f, 2.0f );
										Wand.wondrousProc(curWand, shot.collisionPos);
									});
							Sample.INSTANCE.play( Assets.Sounds.ZAP );
						} else {
							curWand.fx(shot, new Callback() {
								public void call() {
									curWand.onZap(shot);
									wondrousProc(curWand, target);
								}
							});
						}
					}
					curWand.cursedKnown = true;

				}

			}
		}

		@Override
		public String prompt() {
			return Messages.get(Wand.class, "prompt");
		}
	};

	protected static void wondrousProc(Wand curWand, Integer target) {
		if (Random.Float() < WondrousResin.extraCurseEffectChance()) {
			WondrousResin.forcePositive = true;
			CursedWand.cursedZap(curWand,
					curUser,
					new Ballistica(curUser.pos, target, Ballistica.MAGIC_BOLT), new Callback() {
						@Override
						public void call() {
							WondrousResin.forcePositive = false;
							curWand.wandUsed();
						}
					});
		} else {
			curWand.wandUsed();
		}
	}

	public class Charger extends Buff {
		
		private static final float BASE_CHARGE_DELAY = 40f;
		private static final float NORMAL_SCALE_FACTOR = 1f;

		private static final float CHARGE_BUFF_BONUS = 0.25f;

		float scalingFactor = NORMAL_SCALE_FACTOR;

		@Override
		public boolean attachTo( Char target ) {
			if (super.attachTo( target )) {
				//if we're loading in and the hero has partially spent a turn, delay for 1 turn
				if (target instanceof Hero && Dungeon.hero == null && cooldown() == 0 && target.cooldown() > 0) {
					spend(TICK);
				}
				return true;
			}
			return false;
		}
		
		@Override
		public boolean act() {
			if (curCharges < maxCharges && target.buff(MagicImmune.class) == null)
				recharge();
			
			while (partialCharge >= 1 && curCharges < maxCharges) {
				partialCharge--;
				curCharges++;
				updateQuickslot();
			}
			
			if (curCharges == maxCharges){
				partialCharge = 0;
			}
			
			spend( TICK );
			
			return true;
		}

		private void recharge(){
			float turnsToCharge = getTurnsToCharge();

			partialCharge += (1f/turnsToCharge);

			for (Recharging bonus : target.buffs(Recharging.class)){
				if (bonus != null && bonus.remainder() > 0f) {
					partialCharge += CHARGE_BUFF_BONUS * bonus.remainder()/rechargeModifier(rank());
				}
			}
		}

        public float getTurnsToCharge() {
            return getTurnsToCharge(rank());
        }

        public float getTurnsToCharge(int rank){
            float charge = BASE_CHARGE_DELAY * rechargeModifier(rank) / scalingFactor;
            if (Regeneration.regenOn())
                charge /= RingOfEnergy.wandChargeMultiplier(target);
            return charge;
        }
		
		public Wand wand(){
			return Wand.this;
		}

		public void gainCharge(float charge){
			if (curCharges < maxCharges) {
				partialCharge += charge/rechargeModifier();
				while (partialCharge >= 1f) {
					curCharges++;
					partialCharge--;
				}
				if (curCharges >= maxCharges){
					partialCharge = 0;
					curCharges = maxCharges;
				}
				updateQuickslot();
			}
		}

		private void setScaleFactor(float value){
			this.scalingFactor = value;
		}
	}

    private final Emitter.Factory WandParticleFactory = new Emitter.Factory() {
        @Override
        //reimplementing this is needed as instance creation of new staff particles must be within this class.
        public void emit( Emitter emitter, int index, float x, float y ) {
            WandParticle c = (WandParticle)emitter.getFirstAvailable(WandParticle.class);
            if (c == null) {
                c = new WandParticle();
                emitter.add(c);
            }
            c.reset(x, y);
        }

        @Override
        //some particles need light mode, others don't
        public boolean lightMode() {
            return !((Wand.this instanceof WandOfDisintegration)
                    || (Wand.this instanceof WandOfCorruption)
                    || (Wand.this instanceof WandOfCorrosion)
                    || (Wand.this instanceof WandOfRegrowth)
                    || (Wand.this instanceof WandOfLivingEarth));
        }
    };

    //determines particle effects to use based on wand the staff owns.
    public class WandParticle extends PixelParticle {

        private float minSize;
        private float maxSize;
        public float sizeJitter = 0;

        public WandParticle(){
            super();
        }

        public void reset( float x, float y ) {
            revive();

            speed.set(0);

            this.x = x;
            this.y = y;

            staffFx( this );

        }

        public void setSize( float minSize, float maxSize ){
            this.minSize = minSize;
            this.maxSize = maxSize;
        }

        public void setLifespan( float life ){
            lifespan = left = life;
        }

        public void shuffleXY(float amt){
            x += Random.Float(-amt, amt);
            y += Random.Float(-amt, amt);
        }

        public void radiateXY(float amt){
            float hypot = (float)Math.hypot(speed.x, speed.y);
            this.x += speed.x/hypot*amt;
            this.y += speed.y/hypot*amt;
        }

        @Override
        public void update() {
            super.update();
            size(minSize + (left / lifespan)*(maxSize-minSize) + Random.Float(sizeJitter));
        }
    }
}
