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
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfStamina;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class TimekeepersHourglass extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_HOURGLASS;

		levelCap = 5;

		charge = 5+level();
		partialCharge = 0;
		chargeCap = 5+level();

		defaultAction = AC_ACTIVATE;
	}

	@Override
	public void resetForTrinity(int visibleLevel) {
		super.resetForTrinity(visibleLevel);
		charge = visibleLevel/2 - 1; //grants 4-10 turns of time freeze
	}

	public static final String AC_ACTIVATE = "ACTIVATE";
	public static final String AC_FEED     = "FEED";

	//keeps track of generated sandbags.
	public int sandBags = 0;

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped( hero )
				&& !cursed
				&& hero.buff(MagicImmune.class) == null
				&& (charge > 0 || activeBuff != null)) {
			actions.add(AC_ACTIVATE);
			if (type() == 3 && charge < chargeCap){
				actions.add(AC_FEED);
			}
		}
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute(hero, action);

		if (hero.buff(MagicImmune.class) != null) return;

		if (action.equals(AC_ACTIVATE)){

			if (!isEquipped( hero ))        GLog.i( Messages.get(Artifact.class, "need_to_equip") );
			else if (activeBuff != null) {
				if (activeBuff instanceof timeStasis) { //do nothing
				} else {
					activeBuff.detach();
					GLog.i( Messages.get(this, "deactivate") );
				}
			} else if (charge <= 0)         GLog.i( Messages.get(this, "no_charge") );
			else if (cursed)                GLog.i( Messages.get(this, "cursed") );
			else GameScene.show(
						new WndOptions(new ItemSprite(this),
								Messages.titleCase(name()),
								getTypeBasedString("prompt", type()),
								getTypeBasedString("stasis", type()),
								getTypeBasedString("freeze", type())) {
							@Override
							protected void onSelect(int index) {
								if (index == 0) {
									GLog.i( Messages.get(TimekeepersHourglass.class, "onstasis") );
									GameScene.flash(0x80FFFFFF);

									if (type() == 2) {
										int usedCharge = Math.min(charge, 2);
										for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
											if (Dungeon.level.heroFOV[mob.pos]) {
												Ballistica trajectory = new Ballistica(hero.pos, mob.pos, Ballistica.STOP_TARGET);
												trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
												WandOfBlastWave.throwChar(mob, trajectory, 1+usedCharge, true, false, hero);
												Buff.affect(mob, Paralysis.class, 2f*usedCharge);
												artifactProc(mob, visiblyUpgraded(), usedCharge);
											}
										}
										Sample.INSTANCE.play(Assets.Sounds.PUFF);
									} else {
										Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
									}

									activeBuff = new timeStasis();
									Talent.onArtifactUsed(Dungeon.hero);
									activeBuff.attachTo(Dungeon.hero);
								} else if (index == 1) {

									//This might be really good...
									for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
										if (Dungeon.level.heroFOV[mob.pos]) {
											artifactProc(mob, visiblyUpgraded(), 1);
										}
									}

									GLog.i( Messages.get(TimekeepersHourglass.class, "onfreeze") );
									GameScene.flash(0x80FFFFFF);
									Sample.INSTANCE.play(Assets.Sounds.TELEPORT);

									Invisibility.dispel(Dungeon.hero);
									activeBuff = new timeFreeze();
									Talent.onArtifactUsed(Dungeon.hero);
									activeBuff.attachTo(Dungeon.hero);
									charge--;
									((timeFreeze)activeBuff).processTime(0f);
								}
							}
						}
				);
		} else if (action.equals(AC_FEED)){
			if (!isEquipped( hero ))        GLog.i( Messages.get(Artifact.class, "need_to_equip") );
			if (charge == chargeCap)         GLog.i( Messages.get(this, "too_much_charge") );
			else if (cursed)                GLog.i( Messages.get(this, "cursed") );
			else if (type() == 3){
				GameScene.selectItem(itemSelector);
			}
		}
	}

	@Override
	public void activate(Char ch) {
		super.activate(ch);
		if (activeBuff != null)
			activeBuff.attachTo(ch);
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (super.doUnequip(hero, collect, single)){
			if (activeBuff != null){
				activeBuff.detach();
				activeBuff = null;
			}
			return true;
		} else
			return false;
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new hourglassRecharge();
	}

	public float rechargeModifier(){
		return rechargeModifier(type());
	}

	public float rechargeModifier(int type){
		switch (type){
			case 1:
				return 1.0f;
			case 2:
				return 0.666f;
			case 3:
				return 0f;
		}
		return 1.0f;
	}

	public int chargeCap(){
		return chargeCap(type());
	}

	public int chargeCap(int type){
		switch (type){
			case 1:
				return 5 + level();
			case 2:
				return 3 + (level()+1)/2;
			case 3:
				return 25 + level()*5;
		}
		return 0;
	}
	
	@Override
	public void charge(Hero target, float amount) {
		if (charge < chargeCap && !cursed && target.buff(MagicImmune.class) == null){
			partialCharge += 0.25f*amount*rechargeModifier();
			while (partialCharge >= 1){
				partialCharge--;
				charge++;
			}
			if (charge >= chargeCap){
				partialCharge = 0;
			}
			updateQuickslot();
		}
	}

	@Override
	public Item upgrade() {
		Item upgraded = super.upgrade();

		chargeCap = chargeCap();
		if (type() == 3){
			charge = Math.min(charge + 5, chargeCap);
		}

		//for artifact transmutation.
		while (level() > sandBags)
			sandBags ++;

		return upgraded;
	}

	@Override
	public void type(int type) {
		super.type(type);
		chargeCap = chargeCap();
		charge = chargeCap;
	}

	@Override
	public String desc() {
		String desc = super.desc();

		if (isEquipped( Dungeon.hero )){
			if (!cursed) {
				if (level() < levelCap )
					desc += "\n\n" + Messages.get(this, "desc_hint");

			} else
				desc += "\n\n" + Messages.get(this, "desc_cursed");
		}
		return desc;
	}

	@Override
	public String getTypeMessage(int type) {
		return Messages.get(this, "type",
				Math.round(100*rechargeModifier(type)),
				chargeCap(type)) + "\n\n" + super.getTypeMessage(type);
	}


	private static final String SANDBAGS =  "sandbags";
	private static final String BUFF =      "buff";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( SANDBAGS, sandBags );

		if (activeBuff != null)
			bundle.put( BUFF , activeBuff );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		sandBags = bundle.getInt( SANDBAGS );

		//these buffs belong to hourglass, need to handle unbundling within the hourglass class.
		if (bundle.contains( BUFF )){
			Bundle buffBundle = bundle.getBundle( BUFF );

			if (buffBundle.contains( timeFreeze.PRESSES ))
				activeBuff = new timeFreeze();
			else
				activeBuff = new timeStasis();

			activeBuff.restoreFromBundle(buffBundle);
		}
	}

	public class hourglassRecharge extends ArtifactBuff {
		@Override
		public boolean act() {

			if (charge < chargeCap
					&& !cursed
					&& target.buff(MagicImmune.class) == null
					&& Regeneration.regenOn()) {
				//90 turns to charge at full, 60 turns to charge at 0/10
				float chargeGain = 1 / (90f - (chargeCap - charge)*3f);
				chargeGain *= RingOfEnergy.artifactChargeMultiplier(target);
				chargeGain *= rechargeModifier();
				partialCharge += chargeGain;

				while (partialCharge >= 1) {
					partialCharge --;
					charge ++;

					if (charge == chargeCap){
						partialCharge = 0;
					}
				}
			} else if (cursed && Random.Int(10) == 0)
				((Hero) target).spend( TICK );

			updateQuickslot();

			spend( TICK );

			return true;
		}
	}

	public class timeStasis extends ArtifactBuff {
		
		{
			type = buffType.POSITIVE;
			actPriority = BUFF_PRIO-3; //acts after all other buffs, so they are prevented
		}

		@Override
		public boolean attachTo(Char target) {

			if (super.attachTo(target)) {

				Invisibility.dispel();

				int usedCharge = Math.min(charge, 2);
				//buffs always act last, so the stasis buff should end a turn early.
				int timeSkip = 5;
				if (type() == 2){
					timeSkip = 1;
				}
				spend(timeSkip*usedCharge);

				if (type() == 3) {
					for (Buff b : target.buffs()) {
						if (b instanceof Artifact.ArtifactBuff) {
							if (b instanceof timeStasis) {
								continue;
							}
							if (!((Artifact.ArtifactBuff) b).isCursed()) {
								((Artifact.ArtifactBuff) b).charge((Hero) target, 3f * usedCharge);
							}
						}
					}
				}

				//shouldn't punish the player for going into stasis frequently
				Hunger hunger = Buff.affect(target, Hunger.class);
				if (hunger != null && !hunger.isStarving()) {
					hunger.satisfy(5 * usedCharge);
				}

				charge -= usedCharge;

				target.invisible++;
				target.paralysed++;
				target.next();

				updateQuickslot();

				if (Dungeon.hero != null) {
					Dungeon.observe();
				}

				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean act() {
			detach();
			return true;
		}

		@Override
		public void detach() {
			if (target.invisible > 0) target.invisible--;
			if (target.paralysed > 0) target.paralysed--;
			super.detach();
			activeBuff = null;
			Dungeon.observe();
		}

		@Override
		public void fx(boolean on) {
			if (on) target.sprite.add( CharSprite.State.PARALYSED );
			else {
				if (target.paralysed == 0) target.sprite.remove( CharSprite.State.PARALYSED );
				if (target.invisible == 0) target.sprite.remove( CharSprite.State.INVISIBLE );
			}
		}
	}

	public class timeFreeze extends ArtifactBuff {
		
		{
			type = buffType.POSITIVE;
		}

		float turnsToCost = 2f;

		ArrayList<Integer> presses = new ArrayList<>();

		public void processTime(float time){
			turnsToCost -= time;

			//use 1/1,000 to account for rounding errors
			while (turnsToCost < -0.001f){
				turnsToCost += 2f;
				charge --;
				if (type() == 3) {
					for (Buff b : target.buffs()) {
						if (b instanceof Artifact.ArtifactBuff) {
							if (b instanceof timeStasis) {
								continue;
							}
							if (!((Artifact.ArtifactBuff) b).isCursed()) {
								((Artifact.ArtifactBuff) b).charge((Hero) target, 3f);
							}
						}
					}
				}
			}

			updateQuickslot();

			if (charge < 0 || charge == 0 && turnsToCost <= 0){
				charge = 0;
				detach();
			}

		}

		public void setDelayedPress(int cell){
			if (!presses.contains(cell))
				presses.add(cell);
		}

		public void triggerPresses(){
			ArrayList<Integer> toTrigger = presses;
			presses = new ArrayList<>();
			Actor.add(new Actor() {
				{
					actPriority = VFX_PRIO;
				}

				@Override
				protected boolean act() {
					for (int cell : toTrigger){
						Plant p = Dungeon.level.plants.get(cell);
						if (p != null){
							p.trigger();
						}
						Trap t = Dungeon.level.traps.get(cell);
						if (t != null){
							t.trigger();
						}
					}
					Actor.remove(this);
					return true;
				}
			});
		}

		public void disarmPresses(){
			for (int cell : presses){
				Plant p = Dungeon.level.plants.get(cell);
				if (p != null && !(p instanceof Rotberry)) {
					Dungeon.level.uproot(cell);
				}
				Trap t = Dungeon.level.traps.get(cell);
				if (t != null && t.disarmedByActivation) {
					t.disarm();
				}
			}

			presses = new ArrayList<>();
		}

		@Override
		public void detach(){
			updateQuickslot();
			super.detach();
			activeBuff = null;
			triggerPresses();
			target.next();
		}

		@Override
		public void fx(boolean on) {
			if (!(target instanceof Hero)) return;
			Emitter.freezeEmitters = on;
			if (on){
				for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
					if (mob.sprite != null) mob.sprite.add(CharSprite.State.PARALYSED);
				}
			} else {
				for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
					if (mob.paralysed <= 0) mob.sprite.remove(CharSprite.State.PARALYSED);
				}
			}
		}

		@Override
		public int icon() {
			return BuffIndicator.TIME;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(1f, 0.5f, 0);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (2f - turnsToCost) / 2f);
		}

		@Override
		public String iconTextDisplay() {
			return Integer.toString((int)(turnsToCost + 0.001f));
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", Messages.decimalFormat("#.##", Math.max(0, turnsToCost)));
		}

		private static final String PRESSES = "presses";
		private static final String TURNSTOCOST = "turnsToCost";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);

			int[] values = new int[presses.size()];
			for (int i = 0; i < values.length; i ++)
				values[i] = presses.get(i);
			bundle.put( PRESSES , values );

			bundle.put( TURNSTOCOST , turnsToCost);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);

			int[] values = bundle.getIntArray( PRESSES );
			for (int value : values)
				presses.add(value);

			turnsToCost = bundle.getFloat( TURNSTOCOST );
		}
	}

	public static class sandBag extends Item {

		{
			image = ItemSpriteSheet.SANDBAG;
		}

		@Override
		public boolean doPickUp(Hero hero, int pos) {
			Catalog.setSeen(getClass());
			Statistics.itemTypesDiscovered.add(getClass());
			TimekeepersHourglass hourglass = hero.belongings.getItem( TimekeepersHourglass.class );
			if (hourglass != null && !hourglass.cursed) {
				hourglass.upgrade();
				Catalog.countUses(hourglass.getClass(), 2);
				Sample.INSTANCE.play( Assets.Sounds.DEWDROP );
				if (hourglass.level() == hourglass.levelCap)
					GLog.p( Messages.get(this, "maxlevel") );
				else
					GLog.i( Messages.get(this, "levelup") );
				GameScene.pickUp(this, pos);
				hero.spendAndNext(pickupDelay());
				return true;
			} else {
				GLog.w( Messages.get(this, "no_hourglass") );
				return false;
			}
		}

		@Override
		public int value() {
			return 30;
		}

		@Override
		public boolean isUpgradable() {
			return false;
		}

		@Override
		public boolean isIdentified() {
			return true;
		}
	}

	protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(TimekeepersHourglass.class, "feed_prompt");
		}

		@Override
		public Class<?extends Bag> preferredBag(){
			return VelvetPouch.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return (item instanceof Swiftthistle.Seed || item instanceof PotionOfHaste || item instanceof PotionOfStamina) && item.isIdentified();
		}

		@Override
		public void onSelect(Item item) {
			if (itemSelectable(item)){
				Hero hero = Dungeon.hero;
				hero.sprite.operate( hero.pos );
				Sample.INSTANCE.play( Assets.Sounds.PLANT );
				hero.busy();
				hero.spend( Actor.TICK );
				charge += (int) (item.energyVal()*1.5f);
				item.detach(hero.belongings.backpack);
				GLog.w( Messages.get(TimekeepersHourglass.class, "feed", item.trueName(), (int)(item.energyVal()*1.5f)) );
			}
		}
	};
}
