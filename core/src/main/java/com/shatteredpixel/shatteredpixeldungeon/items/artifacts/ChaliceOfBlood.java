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
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.generic.GenericEffect;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HolyWard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.SummonElemental;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.damagesource.DamageProperty;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.damagesource.DamageSource;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TormentedSpiritSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.EnumSet;

public class ChaliceOfBlood extends Artifact implements DamageSource {

	{
		image = ItemSpriteSheet.ARTIFACT_CHALICE1;

		levelCap = 10;
	}

	public static final String AC_PRICK = "PRICK";
	public static final String AC_EVOKE = "EVOKE";

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && !cursed && hero.buff(MagicImmune.class) == null){
			if (level() < levelCap && !hero.isInvulnerable(getClass()) && type() != 3)
				actions.add(AC_PRICK);
			if (type() == 3 && charge >= 50)
				actions.add(AC_EVOKE);
		}
		return actions;
	}

	@Override
	public String defaultAction() {
		if (type() == 3 && isEquipped(Dungeon.hero) && !cursed && Dungeon.hero.buff(MagicImmune.class) == null)
			return AC_EVOKE;
		return super.defaultAction();
	}

	@Override
	public void execute(Hero hero, String action ) {
		super.execute(hero, action);

		if (action.equals(AC_PRICK)){

			int minDmg = minPrickDmg();
			int maxDmg = maxPrickDmg();

			int totalHeroHP = hero.HP + hero.shielding();

			float deathChance = 0;

			if (totalHeroHP < maxDmg) {
				deathChance = (maxDmg - totalHeroHP) / (float) (maxDmg - minDmg);
				if (deathChance < 0.5f) {
					deathChance = (float) Math.pow(2 * deathChance, 2) / 2f;
				} else if (deathChance < 1f) {
					deathChance = 1f - deathChance;
					deathChance = (float) Math.pow(2 * deathChance, 2) / 2f;
					deathChance = 1f - deathChance;
				} else {
					deathChance = 1;
				}
			}

			GameScene.show(
				new WndOptions(new ItemSprite(this),
						Messages.titleCase(name()),
						Messages.get(this, "prick_warn", minDmg, maxDmg, Messages.decimalFormat("#.##", 100*deathChance)),
						Messages.get(this, "yes"),
						Messages.get(this, "no")) {
					@Override
					protected void onSelect(int index) {
						if (index == 0) {
							prick(Dungeon.hero);
						}
					}
				}
			);

		} else if (action.equals(AC_EVOKE)) {
			curUser = hero;

			if (!isEquipped( hero )) {
				GLog.i( Messages.get(Artifact.class, "need_to_equip") );

			} else if (charge < 50) {
				GLog.i( Messages.get(this, "no_charge") );

			} else if (cursed) {
				GLog.w( Messages.get(this, "cursed") );
			} else {
				ArrayList<Integer> spawnPoints = new ArrayList<>();

				for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
					int p = hero.pos + PathFinder.NEIGHBOURS8[i];
					if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
						spawnPoints.add( p );
					}
				}

				if (!spawnPoints.isEmpty()){

					for (Char ch : Actor.chars()){
						if (ch instanceof Elemental && ch.buff(SummonElemental.InvisAlly.class) != null){
							ScrollOfTeleportation.appear( ch, Random.element(spawnPoints) );
							((Elemental) ch).state = ((Elemental) ch).HUNTING;
							curUser.spendAndNext(Actor.TICK);
							return;
						}
					}

					LifestealWraith wraith = new LifestealWraith();
					wraith.state = wraith.HUNTING;
					GameScene.add( wraith );
					ScrollOfTeleportation.appear( wraith, Random.element(spawnPoints) );
					CellEmitter.get(wraith.pos).start(Speck.factory(Speck.HEALGAS), 0.05f, 10);
					Invisibility.dispel(curUser);
					charge -= 50;
					curUser.sprite.operate(curUser.pos);
					curUser.spendAndNext(Actor.TICK);
				} else {
					GLog.w(Messages.get(SpiritHawk.class, "no_space"));
				}
			}
		}
	}

	private int minPrickDmg(){
		return (int)Math.ceil(3 + 2.5f*(level()*level()));
	}

	private int maxPrickDmg(){
		return (int)Math.floor(7 + 3.5f*(level()*level()));
	}

	private void prick(Hero hero){
		int damage = Random.NormalIntRange(minPrickDmg(), maxPrickDmg());

		//need to process on-hit effects manually
		Earthroot.Armor armor = hero.buff(Earthroot.Armor.class);
		if (armor != null) {
			damage = armor.absorb(damage);
		}

		if (hero.buff(MagicImmune.class) != null && hero.buff(HolyWard.HolyArmBuff.class) != null){
			damage -= hero.subClass == HeroSubClass.PALADIN ? 3 : 1;
		}

		WandOfLivingEarth.RockArmor rockArmor = hero.buff(WandOfLivingEarth.RockArmor.class);
		if (rockArmor != null) {
			damage = rockArmor.absorb(damage);
		}

		damage -= hero.drRoll();

		hero.sprite.operate( hero.pos );
		hero.busy();
		hero.spend(Actor.TICK);
		GLog.w( Messages.get(this, "onprick") );
		if (damage <= 0){
			damage = 1;
		} else {
			Sample.INSTANCE.play(Assets.Sounds.CURSED);
			hero.sprite.emitter().burst( ShadowParticle.CURSE, 4+(damage/10) );
		}

		hero.damage(damage, this);

		if (!hero.isAlive()) {
			Badges.validateDeathFromFriendlyMagic();
			Dungeon.fail( this );
			GLog.n( Messages.get(this, "ondeath") );
		} else {
			upgrade();
			Catalog.countUse(getClass());
		}
	}

	@Override
	public Item upgrade() {
		Item upgraded = super.upgrade();
		updateSprite();
		return upgraded;
	}

	protected void updateSprite(){
		if (type() == 3){
			if (charge >= 66)
				image = ItemSpriteSheet.ARTIFACT_CHALICE3;
			else if (charge >= 33)
				image = ItemSpriteSheet.ARTIFACT_CHALICE2;
			else
				image = ItemSpriteSheet.ARTIFACT_CHALICE1;
		} else {
			if (level() >= 7)
				image = ItemSpriteSheet.ARTIFACT_CHALICE3;
			else if (level() >= 3)
				image = ItemSpriteSheet.ARTIFACT_CHALICE2;
		}
	}

	@Override
	public void type(int type) {
		if (type() == 3 && type != 3){
			charge = chargeCap = 0;
		}
		super.type(type);
		if (type == 3){
			chargeCap = 100;
		}
		updateSprite();
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		updateSprite();
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new chaliceRegen();
	}
	
	@Override
	public void charge(Hero target, float amount) {
		if (cursed || target.buff(MagicImmune.class) != null) return;

		if (type() == 1) {
			//grants 5 turns of healing up-front, if hero isn't starving
			if (target.isStarving()) return;

			float healDelay = 10f - (1.33f + level() * 0.667f);
			healDelay /= amount;
			float heal = 5f / healDelay;
			//effectively 0.5/1/1.5/2/2.5 HP per turn at +0/+6/+8/+9/+10
			if (Random.Float() < heal % 1) {
				heal++;
			}
			if (heal >= 1f && target.HP < target.HT) {
				target.HP = Math.min(target.HT, target.HP + (int) heal);
				target.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString((int) heal), FloatingText.HEALING);

				if (target.HP == target.HT && target instanceof Hero) {
					((Hero) target).resting = false;
				}
			}
		} else if (type() == 3) {
			if (charge < chargeCap){
				partialCharge += 2*amount;
				while (partialCharge >= 1f){
					charge++;
					partialCharge--;
				}
				if (charge >= chargeCap) {
					charge = chargeCap;
					partialCharge = 0;
				} else if (charge == 50) {
					GLog.p( Messages.get(ChaliceOfBlood.class, "full_charge") );
				}
				updateQuickslot();
			}
		}
	}
	
	@Override
	public String desc() {
		String desc = getTypeBasedString("desc", type());

		if (isEquipped (Dungeon.hero)){
			desc += "\n\n";
			if (cursed)
				desc += getTypeBasedString( "desc_cursed", type());
			else if (type() != 3){
				if (level() == 0)
					desc += Messages.get(this, "desc_1");
				else if (level() < levelCap)
					desc += getTypeBasedString( "desc_2", type());
				else
					desc += Messages.get(this, "desc_3");
			} else {
				desc += Messages.get(this, "desc_recharge");
			}
		}

		return desc;
	}

	public class chaliceRegen extends ArtifactBuff {
		//see Regeneration.class for effect of type 1
		@Override
		public boolean act() {
			spend( TICK );

			if (type() == 3 && charge < chargeCap
					&& !cursed
					&& target.buff(MagicImmune.class) == null
					&& Regeneration.regenOn()) {
				//fully charges in 1000 turns at +0, scaling to 348 turns at +10.
				float chargeGain = (float) (0.1f*(1f/Math.pow(0.9f, level())));
				chargeGain *= RingOfEnergy.artifactChargeMultiplier(target);
				partialCharge += chargeGain;

				while (partialCharge >= 1){
					partialCharge--;
					charge++;
					if (charge >= chargeCap) {
						partialCharge = 0;
					} else if (charge == 50) {
						GLog.p( Messages.get(ChaliceOfBlood.class, "full_charge") );
					}
					updateSprite();
					updateQuickslot();
				}
			}

			return true;
		}

		public void upgrade(int damage){
			exp += damage;
			if (exp >= 3*level()*level() + 5 && level() < levelCap){
				exp -= 3*level()*level() + 5;
				ChaliceOfBlood.this.upgrade();
				GLog.p( Messages.get(ChaliceOfBlood.class, "levelup") );
				Catalog.countUse(ChaliceOfBlood.class);
			}
		}
	}

    @Override
    public EnumSet<DamageProperty> initDmgProperties() {
        return EnumSet.of(DamageProperty.PHYSICAL);
    }

	public static class DefenseDamage extends GenericEffect {
		{
			type = buffType.NEGATIVE;
		}

		public static final int RATE = 10;

		@Override
		public int icon() {return BuffIndicator.DEFENSE_BUFF;}

		@Override
		public void tintIcon(Image icon) {icon.hardlight(1f, 0f, 0f);}

		@Override
		public String desc() {
			return Messages.get(this, "desc", (int)(visualcooldown()/RATE), RATE);
		}
	}

	public static class LifestealWraith extends Wraith {
		{
			spriteClass = TormentedSpiritSprite.class;
			alignment = Alignment.ALLY;
			actPriority = MOB_PRIO+1;
		}

		//50% more damage scaling than regular wraiths
		@Override
		public int damageRoll() {
			return Random.NormalIntRange( 1 + Math.round(1.5f*level)/2, 2 + Math.round(1.5f*level) );
		}

		//50% more accuracy (and by extension evasion) scaling than regular wraiths
		@Override
		public int attackSkill( Char target ) {
			return 10 + Math.round(1.5f*level);
		}

		@Override
		public int attackProc(Char enemy, int damage) {
			damage = super.attackProc(enemy, damage);
			int heal = (int) Math.max(1, 0.25f*damage);
			if (heal > 0 && Dungeon.hero.HP < Dungeon.hero.HT && Regeneration.canSustain()){
				Regeneration.regenerate(Dungeon.hero, heal);
				if (sprite.visible)
					sprite.parent.add(new Beam.HealthRay(sprite.center(), Dungeon.hero.sprite.center()));
			}
			return damage;
		}
	}
}
