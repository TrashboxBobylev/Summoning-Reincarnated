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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventoryPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class RunicBlade extends MeleeWeapon {

	private static final String AC_ZAP = "ZAP";

	{
		image = ItemSpriteSheet.RUNIC_BLADE;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1f;

		tier = 4;

		defaultAction = AC_ZAP;
		usesTargeting = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (charged) {
			actions.add( AC_ZAP );
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

	public boolean charged = true;

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("charge", charged);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charged = bundle.getBoolean("charge");
	}

	//Essentially it's a tier 4 weapon, with tier 3 base max damage, and tier 5 scaling.
	//equal to tier 4 in damage at +5

	@Override
	public int max(int lvl) {
		int i = 4 * (tier) +                    //16 base, down from 25
				Math.round(lvl * (tier)); //+6 per level, up from +5
		if (!charged) i = 5 * (tier) +
				Math.round(lvl * (tier + 1));
		return i;
	}

	public boolean tryToZap(Hero owner){

		if (owner.buff(MagicImmune.class) != null){
			GLog.warning( Messages.get(this, "no_magic") );
			return false;
		}

		if (!isEquipped(owner)){
			GLog.warning( Messages.get(this, "no_equip") );
			return false;
		}

		if (charged){
			return true;
		} else {
			GLog.warning(Messages.get(this, "fizzles"));
			return false;
		}
	}

	@Override
	public String info() {
		String info = super.info();
		if (!charged){
			RunicCooldown cooldown = Dungeon.hero.buff(RunicCooldown.class);
			if (cooldown != null){
				info += "\n\n" + Messages.get(this, "cooldown", cooldown.cooldown()+1f);
			}
		}
		return info;
	}

	protected static CellSelector.Listener zapper = new  CellSelector.Listener() {

		@Override
		public void onSelect( Integer target ) {

			if (target != null) {

				//FIXME this safety check shouldn't be necessary
				//it would be better to eliminate the curItem static variable.
				final RunicBlade curBlade;
				if (curItem instanceof RunicBlade) {
					curBlade = (RunicBlade) curItem;
				} else {
					return;
				}

				final Ballistica shot = new Ballistica( curUser.pos, target, Ballistica.FRIENDLY_PROJECTILE);
				final int cell = shot.collisionPos;

				if (target == curUser.pos || cell == curUser.pos) {
					GLog.i( Messages.get(Wand.class, "self_target") );
					return;
				}

				curUser.sprite.zap(cell);

				//attempts to target the cell aimed at if something is there, otherwise targets the collision pos.
				if (Actor.findChar(target) != null)
					QuickSlotButton.target(Actor.findChar(target));
				else
					QuickSlotButton.target(Actor.findChar(cell));

				if (curBlade.tryToZap(curUser)) {

					curUser.busy();
					Invisibility.dispel();

					if (curBlade.cursed){
						if (!curBlade.cursedKnown){
							GLog.negative(Messages.get(Wand.class, "curse_discover", curBlade.name()));
						}
					} else {
						Sample.INSTANCE.play(Assets.Sounds.ZAP);
						((MissileSprite) curUser.sprite.parent.recycle(MissileSprite.class)).
								reset(curUser.sprite,
										cell,
										new RunicMissile(),
										new Callback() {
											@Override
											public void call() {
												Char enemy = Actor.findChar( cell );
												if (enemy != null && enemy != curUser) {
													if (Char.hit(curUser, enemy, true)) {
														int dmg = curBlade.damageRoll(curUser);
														enemy.damage(dmg, curBlade);
														curBlade.proc(curUser, enemy, dmg);
														Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC);
													} else {
														enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
													}
												} else {
													Dungeon.level.pressCell(cell);
												}
												Splash.at(cell, 0x38c3c3, 15);
												curBlade.charged = false;
												updateQuickslot();
												int slot = Dungeon.quickslot.getSlot(curBlade);
												if (slot != -1){
													Dungeon.quickslot.clearSlot(slot);
													updateQuickslot();
													Dungeon.quickslot.setSlot( slot, curBlade );
													updateQuickslot();
												}
												Buff.affect(curUser, RunicCooldown.class, 40*curBlade.delayFactor(curUser));
												curUser.spendAndNext(curBlade.delayFactor(curUser));
											}
										});
					}
					curBlade.cursedKnown = true;

				}

			}
		}

		@Override
		public String prompt() {
			return Messages.get(RunicBlade.class, "prompt");
		}
	};

	@Override
	public Emitter emitter() {
		if (!charged) return null;
		Emitter emitter = new Emitter();
		emitter.pos(12f, 1f);
		emitter.fillTarget = false;
		emitter.pour(StaffParticleFactory, 0.1f);
		return emitter;
	}

	public final Emitter.Factory StaffParticleFactory = new Emitter.Factory() {
		@Override
		//reimplementing this is needed as instance creation of new staff particles must be within this class.
		public void emit( Emitter emitter, int index, float x, float y ) {
			StaffParticle c = (StaffParticle)emitter.getFirstAvailable(StaffParticle.class);
			if (c == null) {
				c = new StaffParticle();
				emitter.add(c);
			}
			c.reset(x, y);
		}

		@Override
		//some particles need light mode, others don't
		public boolean lightMode() {
			return true;
		}
	};

	//determines particle effects to use based on wand the staff owns.
	public class StaffParticle extends PixelParticle {

		private float minSize;
		private float maxSize;
		public float sizeJitter = 0;

		public StaffParticle(){
			super();
		}

		public void reset( float x, float y ) {
			revive();

			speed.set(0);

			this.x = x;
			this.y = y;

			color( 0x38c3c3 );
			am = 0.85f;
			setLifespan(3f);
			speed.polar(Random.Float(PointF.PI2), 0.3f);
			setSize( 1f, 2f);
			radiateXY(2.5f);

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

	public void recharge(){
		charged = true;
	}

	public static class RunicCooldown extends FlavourBuff {

		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.266f, 1f, 1f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 40); }

		@Override
		public void detach() {
			RunicBlade runicBlade = Dungeon.hero.belongings.getItem(RunicBlade.class);
			if (runicBlade != null){
				runicBlade.recharge();
				int slot = Dungeon.quickslot.getSlot(runicBlade);
				if (slot != -1){
					Dungeon.quickslot.clearSlot(slot);
					updateQuickslot();
					Dungeon.quickslot.setSlot( slot, runicBlade );
					updateQuickslot();
					if (SPDSettings.interfaceSize() == 2)
						InventoryPane.refresh();
				}
			}
			SpellSprite.show(Dungeon.hero, SpellSprite.CHARGE);
			Sample.INSTANCE.play(Assets.Sounds.LEVELUP);
			super.detach();
		}
	}

	public static class RunicMissile extends Item {
		{
			image = ItemSpriteSheet.RUNIC_SHOT;
		}
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		if (target == null) {
			return;
		}

		Char enemy = Actor.findChar(target);
		if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
			GLog.w(Messages.get(this, "ability_no_target"));
			return;
		}

		//we apply here because of projecting
		RunicSlashTracker tracker = Buff.affect(hero, RunicSlashTracker.class);
		tracker.boost = 3f + 0.50f*buffedLvl();
		hero.belongings.abilityWeapon = this;
		if (!hero.canAttack(enemy)){
			GLog.w(Messages.get(this, "ability_target_range"));
			tracker.detach();
			hero.belongings.abilityWeapon = null;
			return;
		}
		hero.belongings.abilityWeapon = null;

		hero.sprite.attack(enemy.pos, new Callback() {
			@Override
			public void call() {
				beforeAbilityUsed(hero, enemy);
				AttackIndicator.target(enemy);
				if (hero.attack(enemy, 1f, 0, Char.INFINITE_ACCURACY)){
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
					if (!enemy.isAlive()){
						onAbilityKill(hero, enemy);
					}
				}
				tracker.detach();
				Invisibility.dispel();
				hero.spendAndNext(hero.attackDelay());
				afterAbilityUsed(hero);
			}
		});
	}

	@Override
	public String abilityInfo() {
		if (levelKnown){
			return Messages.get(this, "ability_desc", 300+50*buffedLvl());
		} else {
			return Messages.get(this, "typical_ability_desc", 300);
		}
	}

	@Override
	public String upgradeAbilityStat(int level) {
		return "+" + (300+50*level) + "%";
	}


	public static class RunicSlashTracker extends FlavourBuff{

		public float boost = 2f;

	};

}
