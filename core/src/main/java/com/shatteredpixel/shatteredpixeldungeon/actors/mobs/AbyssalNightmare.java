/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2022 Evan Debenham
 *
 * Summoning Pixel Dungeon
 * Copyright (C) 2019-2022 TrashboxBobylev
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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blizzard;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.FrostFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Inferno;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Regrowth;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SmokeScreen;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StenchGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StormCloud;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostBurn;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Shrink;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TimedShrink;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfPrismaticLight;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfTransfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Kinetic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Vampiric;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AbyssalSprite;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class AbyssalNightmare extends AbyssalMob {

	{
		spriteClass = AbyssalSprite.class;

		HP = HT = 320;
		defenseSkill = 0;

		EXP = 50;

		flying = true;
		baseSpeed = 0.5f;

		loot = new PotionOfHealing();
		lootChance = 0.1667f; //by default, see rollToDropLoot()

		properties.add(Property.INORGANIC);
		properties.add(Property.UNDEAD);
		properties.add(Property.DEMONIC);
		properties.add(Property.BOSS);
		properties.add(Property.LARGE);
	}

	private static final float SPLIT_DELAY	= 1f;

	int generation	= 0;

	private static final String GENERATION	= "generation";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( GENERATION, generation );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		generation = bundle.getInt( GENERATION );
		if (generation > 0) EXP = 0;
	}

	@Override
	protected boolean act() {
		if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
			fieldOfView = new boolean[Dungeon.level.length()];
		}
		Dungeon.level.updateFieldOfView( this, fieldOfView );

		HP = Math.min(HP+Random.IntRange(2, 3), HT);

		boolean justAlerted = alerted;
		alerted = false;

		if (justAlerted){
			sprite.showAlert();
		} else {
			sprite.hideAlert();
			sprite.hideLost();
		}

		if (paralysed > 0) {
			enemySeen = false;
			spend( TICK );
			return true;
		}

		enemy = chooseEnemy();

		boolean enemyInFOV = enemy != null && enemy.isAlive() && enemy.invisible <= 0;

		return state.act( enemyInFOV, justAlerted );
	}

	@Override
	public boolean canSee(int pos) {
		return true;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 20 + abyssLevel()*6, 36 + abyssLevel()*14 );
	}

	@Override
	public int drRoll() {
		return super.drRoll();
	}

	@Override
	public int attackSkill( Char target ) {
		return 70 + abyssLevel()*3;
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
//		if (Dungeon.isChallenged(Challenges.NO_LEVELS))
//			new PotionOfExperience().apply(Dungeon.hero);
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(4) == 0 && generation == 0){
			ArrayList<Integer> candidates = new ArrayList<>();
			boolean[] solid = Dungeon.level.solid;

			int[] neighbours = {pos + 1, pos - 1, pos + Dungeon.level.width(), pos - Dungeon.level.width()};
			for (int n : neighbours) {
				if (!solid[n] && Actor.findChar( n ) == null) {
					candidates.add( n );
				}
			}

			if (candidates.size() > 0) {

				AbyssalNightmare clone = split();
				clone.HP = (int) (HP * 0.8f);
				clone.pos = Random.element( candidates );
				clone.state = clone.HUNTING;

				Dungeon.level.occupyCell(clone);

				GameScene.add( clone, SPLIT_DELAY );
				Actor.addDelayed( new Pushing( clone, pos, clone.pos ), -1 );
			}
		}
		return super.attackProc(enemy, damage);
	}

	private AbyssalNightmare split() {
		AbyssalNightmare clone = new AbyssalNightmare();
		clone.EXP = EXP/2;
		clone.generation = generation + 1;
		if (buff(Corruption.class ) != null) {
			Buff.affect( clone, Corruption.class);
		}
		return clone;
	}

	@Override
	public void damage(int dmg, Object src) {
		dmg *= GameMath.gate(0.33f, (1f - ((float)(HT-HP)/HT))*3, 3f );
		super.damage(dmg, src);
	}

	@Override
	public Item createLoot(){
		int rolls = 30;
		((RingOfWealth)(new RingOfWealth().upgrade(10))).buff().attachTo(this);
		ArrayList<Item> bonus = RingOfWealth.tryForBonusDrop(this, rolls);
		if (bonus != null && !bonus.isEmpty()) {
			for (Item b : bonus) Dungeon.level.drop(b, pos).sprite.drop();
			RingOfWealth.showFlareForBonusDrop(sprite);
		}
		return null;
	}

	@Override
	public float resistanceValue(Class effect) {
		return super.resistanceValue(effect)/2f;
	}

	@Override
	protected boolean getCloser(int target) {
		if (super.getCloser(target)){
			return true;
		} else {

//			if (buff(ChampionEnemy.Paladin.class) != null){
//				return true;
//			}

			if (target == pos || Dungeon.level.adjacent(pos, target)) {
				return false;
			}

			int bestpos = pos;
			for (int i : PathFinder.NEIGHBOURS8){
				PathFinder.buildDistanceMap(pos+i, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
				if (PathFinder.distance[pos+i] == Integer.MAX_VALUE){
					continue;
				}
				if (Actor.findChar(pos+i) == null &&
						Dungeon.level.trueDistance(bestpos, target) > Dungeon.level.trueDistance(pos+i, target)){
					bestpos = pos+i;
				}
			}
			if (bestpos != pos){

				for (int i : PathFinder.CIRCLE8){
					if ((Dungeon.level.map[pos+i] == Terrain.WALL || Dungeon.level.map[pos+i] == Terrain.WALL_DECO ||
							Dungeon.level.map[pos+i] == Terrain.DOOR || Dungeon.level.map[pos+i] == Terrain.SECRET_DOOR)){
						Level.set(pos+i, Terrain.EMPTY);
						if (Dungeon.level.insideMap(pos+i) && Dungeon.level.heroFOV[pos+i]){
							CellEmitter.bottom(pos+i).burst(SmokeParticle.FACTORY, 12);
						}
						GameScene.updateMap(pos+i);
					}
				}
				Dungeon.level.cleanWalls();
				Dungeon.observe();

				bestpos = pos;
				for (int i : PathFinder.NEIGHBOURS8){
					if (Actor.findChar(pos+i) == null && Dungeon.level.openSpace[pos+i] &&
							Dungeon.level.trueDistance(bestpos, target) > Dungeon.level.trueDistance(pos+i, target)){
						bestpos = pos+i;
					}
				}

				if (bestpos != pos) {
					move(bestpos);
				}

				return true;
			}

			return false;
		}
	}

	{
		resistances.add( Blizzard.class );
		resistances.add( ConfusionGas.class );
		resistances.add( CorrosiveGas.class );
		resistances.add( Electricity.class );
		resistances.add( Fire.class );
		resistances.add( Freezing.class );
		resistances.add( Inferno.class );
		resistances.add( ParalyticGas.class );
		resistances.add( Regrowth.class );
		resistances.add( SmokeScreen.class );
		resistances.add( StenchGas.class );
		resistances.add( StormCloud.class );
		resistances.add( ToxicGas.class );
		resistances.add( Web.class );
		resistances.add( FrostFire.class);


		resistances.add( Burning.class );
		resistances.add( Charm.class );
		resistances.add( Chill.class );
		resistances.add( Frost.class );
		resistances.add( Ooze.class );
		resistances.add( Paralysis.class );
		resistances.add( Poison.class );
		resistances.add( Corrosion.class );
		resistances.add( Weakness.class );
		resistances.add( FrostBurn.class);
		resistances.add( Shrink.class);
		resistances.add( TimedShrink.class);
		resistances.add( MagicalSleep.class);
		resistances.add( Vertigo.class);
		resistances.add( Terror.class);
		resistances.add( Vulnerable.class);
		resistances.add( Slow.class);
		resistances.add( Blindness.class);
		resistances.add( Cripple.class);
		resistances.add( Doom.class);
		resistances.add( Drowsy.class);
		resistances.add( Hex.class);
		resistances.add( Sleep.class);

		resistances.add( DisintegrationTrap.class );
		resistances.add( GrimTrap.class );

		resistances.add( WandOfBlastWave.class );
		resistances.add( WandOfDisintegration.class );
		resistances.add( WandOfFireblast.class );
		resistances.add( WandOfFrost.class );
		resistances.add( WandOfLightning.class );
		resistances.add( WandOfLivingEarth.class );
		resistances.add( WandOfMagicMissile.class );
		resistances.add( WandOfPrismaticLight.class );
		resistances.add( WandOfTransfusion.class );
		resistances.add( WandOfWarding.Ward.class );

		resistances.add( Shaman.EarthenBolt.class );
		resistances.add( Warlock.DarkBolt.class );
		resistances.add( Eye.DeathGaze.class );
		resistances.add( AttunementConstruct.class);
		resistances.add( SpectreRat.DarkBolt.class);

		resistances.add(Grim.class);
		resistances.add(Kinetic.class);
		resistances.add(Blazing.class);
		resistances.add(Shocking.class);
		resistances.add(Vampiric.class);
	}
}
