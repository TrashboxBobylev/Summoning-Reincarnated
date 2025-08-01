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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.RatSkull;
import com.shatteredpixel.shatteredpixeldungeon.levels.AbyssLevel;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;

public class MobSpawner extends Actor {
	{
		actPriority = BUFF_PRIO; //as if it were a buff.
	}

	@Override
	protected boolean act() {

		if (Dungeon.level.mobCount() < Dungeon.level.mobLimit()) {

			if (Dungeon.level.spawnMob(12)){
				spend(Dungeon.level.respawnCooldown());
			} else {
				//try again in 1 turn
				spend(TICK);
			}

		} else {
			spend(Dungeon.level.respawnCooldown());
		}

		return true;
	}

	public void resetCooldown(){
		spend(-cooldown());
		spend(Dungeon.level.respawnCooldown());
	}

	public static ArrayList<Class<? extends Mob>> getMobRotation(int depth ){
		ArrayList<Class<? extends Mob>> mobs = standardMobRotation( depth );
		swapMobVariantAlts(mobs);
		addRareMobs(depth, mobs);
		swapMobRareAlts(mobs);
		Random.shuffle(mobs);
		return mobs;
	}

	//returns a rotation of standard mobs, unshuffled.
	private static ArrayList<Class<? extends Mob>> standardMobRotation( int depth ){
		if (Dungeon.branch == AbyssLevel.BRANCH){
			return new ArrayList<>(Arrays.asList(
					SpectreRat.class, DarkestElf.class, GhostChicken.class, Phantom.class, BlinkingMan.class, Trappet.class
			));
		}
		if (Dungeon.mode == Dungeon.GameMode.CHAOS){
			if (Dungeon.depth > 0 && Dungeon.depth < Dungeon.chapterSize()) {
				return new ArrayList<>(Arrays.asList(Rat.class, Snake.class, Gnoll.class, Swarm.class, Crab.class, Slime.class));
			}
			if (Dungeon.depth > Dungeon.chapterSize() && Dungeon.depth < Dungeon.chapterSize()*2) {
				return new ArrayList<>(Arrays.asList(Skeleton.class, Thief.class, Guard.class, DM100.class, Necromancer.class));
			}
			if (Dungeon.depth > Dungeon.chapterSize()*2 && Dungeon.depth < Dungeon.chapterSize()*3) {
				return new ArrayList<>(Arrays.asList(Bat.class, Brute.class,
						Spinner.class, Shaman.random(),
						DM200.class));
			}
			if (Dungeon.depth > Dungeon.chapterSize()*3 && Dungeon.depth < Dungeon.chapterSize()*4) {
				return new ArrayList<>(Arrays.asList(Ghoul.class, Monk.class,
						Elemental.random(),
						Warlock.class, Golem.class));
			}
			if (Dungeon.depth > Dungeon.chapterSize()*4 && Dungeon.depth < Dungeon.chapterSize()*5) {
				return new ArrayList<>(Arrays.asList(Succubus.class, Eye.class, Scorpio.class));
			}
		}
		if (Dungeon.mode == Dungeon.GameMode.SMALL){
			switch(depth){

				// Sewers
				case 1: default:
					//3x rat, 1x snake
					return new ArrayList<>(Arrays.asList(
							Rat.class, Rat.class, Rat.class,
							Snake.class));
				case 2:
					//4x rat, 2x snake, 4x gnoll, 1x swarm
					return new ArrayList<>(Arrays.asList(Rat.class, Rat.class, Rat.class, Rat.class,
							Snake.class, Snake.class,
							Gnoll.class, Gnoll.class, Gnoll.class, Gnoll.class,
							Swarm.class));
				case 3: case 4:
					//1x snake, 1x gnoll, 1x swarm, 1x crab, 1x slime
					return new ArrayList<>(Arrays.asList(Snake.class,
							Gnoll.class,
							Swarm.class,
							Crab.class,
							Slime.class));

				// Prison
				case 5:
					//4x skeleton, 2x thief, 1x DM-100
					return new ArrayList<>(Arrays.asList(Skeleton.class, Skeleton.class, Skeleton.class, Skeleton.class,
							Thief.class, Thief.class,
							DM100.class));
				case 6:
					//2x skeleton, 2x thief, 2x DM-100, 1x guard, 1x necromancer
					return new ArrayList<>(Arrays.asList(Skeleton.class, Skeleton.class,
							Thief.class, Thief.class,
							DM100.class, DM100.class,
							Guard.class,
							Necromancer.class));
				case 7: case 8:
					//1x skeleton, 1x thief, 1x DM-100, 1x guard, 1x necromancer
					return new ArrayList<>(Arrays.asList(Skeleton.class,
							DM100.class,
							Guard.class,
							Necromancer.class));

				// Caves
				case 9:
					//5x bat, 3x brute, 2x shaman, 1x spinner
					return new ArrayList<>(Arrays.asList(
							Bat.class, Bat.class, Bat.class, Bat.class, Bat.class,
							Brute.class, Brute.class, Brute.class,
							Shaman.random(), Shaman.random(),
							Spinner.class));
				case 10:
					//2x bat, 2x brute, 2x shaman, 2x spinner, 1x DM-200
					return new ArrayList<>(Arrays.asList(
							Bat.class, Bat.class,
							Brute.class, Brute.class,
							Shaman.random(), Shaman.random(),
							Spinner.class, Spinner.class,
							DM200.class));
				case 11: case 12:
					//1x bat, 3x brute, 3x shaman, 3x spinner, 2x DM-200
					return new ArrayList<>(Arrays.asList(
							Bat.class,
							Brute.class, Brute.class, Brute.class,
							Shaman.random(), Shaman.random(), Shaman.random(),
							Spinner.class, Spinner.class, Spinner.class,
							DM200.class, DM200.class));

				// City
				case 13:
					//4x ghoul, 2x elemental, 2x warlock, 1x monk
					return new ArrayList<>(Arrays.asList(
							Ghoul.class, Ghoul.class, Ghoul.class, Ghoul.class,
							Elemental.random(), Elemental.random(),
							Warlock.class, Warlock.class,
							Monk.class));
				case 14:
					//2x ghoul, 2x elemental, 2x warlock, 2x monk, 1x golem
					return new ArrayList<>(Arrays.asList(
							Ghoul.class, Ghoul.class,
							Elemental.random(), Elemental.random(),
							Warlock.class, Warlock.class,
							Monk.class, Monk.class,
							Golem.class));
				case 15: case 16:
					//1x elemental, 1x warlock, 1x monk, 1x golem
					return new ArrayList<>(Arrays.asList(
							Elemental.random(),
							Warlock.class,
							Monk.class,
							Golem.class));

				// Halls
				case 17:
					//3x succubus, 2x evil eye
					return new ArrayList<>(Arrays.asList(
							Succubus.class, Succubus.class, Succubus.class,
							Eye.class, Eye.class));
				case 18:
					//1x succubus, 2x evil eye, 1x scorpio
					return new ArrayList<>(Arrays.asList(
							Succubus.class,
							Eye.class, Eye.class,
							Scorpio.class));
				case 19: case 20: case 21:
					//1x succubus, 3x evil eye, 4x scorpio
					return new ArrayList<>(Arrays.asList(
							Succubus.class,
							Eye.class, Eye.class, Eye.class,
							Scorpio.class, Scorpio.class, Scorpio.class, Scorpio.class));
			}
		}
		if (Dungeon.mode == Dungeon.GameMode.BIGGER || Dungeon.mode == Dungeon.GameMode.GAUNTLET){
			switch(depth){
				// Sewers
				case 1: default:
					//3x rat, 1x snake
					return new ArrayList<>(Arrays.asList(
							Rat.class, Rat.class, Rat.class,
							Snake.class));
				case 2:
					//2x rat, 1x snake, 2x gnoll
					return new ArrayList<>(Arrays.asList(Rat.class, Rat.class,
							Snake.class,
							Gnoll.class, Gnoll.class));
				case 3:
					//1x rat, 1x snake, 3x gnoll, 1x swarm, 1x crab
					return new ArrayList<>(Arrays.asList(Rat.class,
							Snake.class,
							Gnoll.class, Gnoll.class, Gnoll.class,
							Swarm.class,
							Crab.class));
				case 4:
					//1x gnoll, 1x swarm, 2x crab, 2x slime
					return new ArrayList<>(Arrays.asList(Gnoll.class,
							Swarm.class,
							Crab.class, Crab.class,
							Slime.class, Slime.class));
				case 5: case 6:
					//1x swarm, 3x crab, 2x slime
					return new ArrayList<>(Arrays.asList(Gnoll.class,
							Swarm.class,
							Crab.class, Crab.class, Crab.class,
							Slime.class, Slime.class));

				// Prison
				case 7:
					//3x skeleton, 1x thief, 1x swarm
					return new ArrayList<>(Arrays.asList(Skeleton.class, Skeleton.class, Skeleton.class,
							Thief.class,
							Swarm.class));
				case 8:
					//3x skeleton, 1x thief, 1x DM-100, 1x guard
					return new ArrayList<>(Arrays.asList(Skeleton.class, Skeleton.class, Skeleton.class,
							Thief.class,
							DM100.class,
							Guard.class));
				case 9:
					//2x skeleton, 1x thief, 2x DM-100, 2x guard, 1x necromancer
					return new ArrayList<>(Arrays.asList(Skeleton.class, Skeleton.class,
							Thief.class,
							DM100.class, DM100.class,
							Guard.class, Guard.class,
							Necromancer.class));
				case 10:
					//1x skeleton, 1x thief, 2x DM-100, 2x guard, 2x necromancer
					return new ArrayList<>(Arrays.asList(Skeleton.class,
							Thief.class,
							DM100.class, DM100.class,
							Guard.class, Guard.class,
							Necromancer.class, Necromancer.class));
				case 11: case 12:
					//1x skeleton, 2x DM-100, 3x guard, 3x necromancer
					return new ArrayList<>(Arrays.asList(Skeleton.class,
							DM100.class, DM100.class,
							Guard.class, Guard.class, Guard.class,
							Necromancer.class, Necromancer.class, Necromancer.class));

				// Caves
				case 13:
					//3x bat, 1x brute, 1x shaman
					return new ArrayList<>(Arrays.asList(
							Bat.class, Bat.class, Bat.class,
							Brute.class,
							Shaman.random()));
				case 14:
					//2x bat, 2x brute, 1x shaman, 1x spinner
					return new ArrayList<>(Arrays.asList(
							Bat.class, Bat.class,
							Brute.class, Brute.class,
							Shaman.random(),
							Spinner.class));
				case 15:
					//1x bat, 2x brute, 2x shaman, 2x spinner, 1x DM-200
					return new ArrayList<>(Arrays.asList(
							Bat.class,
							Brute.class, Brute.class,
							Shaman.random(), Shaman.random(),
							Spinner.class, Spinner.class,
							DM200.class));
				case 16:
					//1x bat, 1x brute, 2x shaman, 2x spinner, 2x DM-300
					return new ArrayList<>(Arrays.asList(
							Bat.class,
							Brute.class,
							Shaman.random(), Shaman.random(),
							Spinner.class, Spinner.class,
							DM200.class, DM200.class));
				case 17: case 18:
					//1x brute, 2x shaman, 3x spinner, 3x DM-300
					return new ArrayList<>(Arrays.asList(
							Brute.class,
							Shaman.random(), Shaman.random(),
							Spinner.class, Spinner.class, Spinner.class,
							DM200.class, DM200.class, DM200.class));

				// City
				case 19:
					//3x ghoul, 1x elemental, 1x warlock
					return new ArrayList<>(Arrays.asList(
							Ghoul.class, Ghoul.class, Ghoul.class,
							Elemental.random(),
							Warlock.class));
				case 20:
					//1x ghoul, 2x elemental, 1x warlock, 1x monk
					return new ArrayList<>(Arrays.asList(
							Ghoul.class,
							Elemental.random(), Elemental.random(),
							Warlock.class,
							Monk.class));
				case 21:
					//1x ghoul, 1x elemental, 2x warlock, 2x monk, 1x golem
					return new ArrayList<>(Arrays.asList(
							Ghoul.class,
							Elemental.random(),
							Warlock.class, Warlock.class,
							Monk.class, Monk.class,
							Golem.class));
				case 22:
					//1x elemental, 2x warlock, 2x monk, 3x golem
					return new ArrayList<>(Arrays.asList(
							Elemental.random(),
							Warlock.class, Warlock.class,
							Monk.class, Monk.class,
							Golem.class, Golem.class, Golem.class));
				case 23: case 24:
					//1x warlock, 1x monk, 3x golem
					return new ArrayList<>(Arrays.asList(
							Warlock.class,
							Monk.class,
							Golem.class, Golem.class, Golem.class));

				// Halls
				case 25:
					//2x succubus, 1x evil eye
					return new ArrayList<>(Arrays.asList(
							Succubus.class, Succubus.class,
							Eye.class));
				case 26:
					//1x succubus, 1x evil eye
					return new ArrayList<>(Arrays.asList(
							Succubus.class,
							Eye.class));
				case 27:
					//1x succubus, 2x evil eye, 1x scorpio
					return new ArrayList<>(Arrays.asList(
							Succubus.class,
							Eye.class, Eye.class,
							Scorpio.class));
				case 28:
					//1x succubus, 2x evil eye, 3x scorpio
					return new ArrayList<>(Arrays.asList(
							Succubus.class,
							Eye.class, Eye.class,
							Scorpio.class, Scorpio.class, Scorpio.class));
				case 29: case 30: case 31:
					//1x evil eye, 2x scorpio
					return new ArrayList<>(Arrays.asList(
							Eye.class,
							Scorpio.class, Scorpio.class));
			}
		}
		switch(depth){

			// Sewers
			case 1: default:
				//3x rat, 1x snake
				return new ArrayList<>(Arrays.asList(
						Rat.class, Rat.class, Rat.class,
						Snake.class));
			case 2:
				//2x rat, 1x snake, 2x gnoll
				return new ArrayList<>(Arrays.asList(Rat.class, Rat.class,
						Snake.class,
						Gnoll.class, Gnoll.class));
			case 3:
				//1x rat, 1x snake, 3x gnoll, 1x swarm, 1x crab
				return new ArrayList<>(Arrays.asList(Rat.class,
						Snake.class,
						Gnoll.class, Gnoll.class, Gnoll.class,
						Swarm.class,
						Crab.class));
			case 4: case 5:
				//1x gnoll, 1x swarm, 2x crab, 2x slime
				return new ArrayList<>(Arrays.asList(Gnoll.class,
						Swarm.class,
						Crab.class, Crab.class,
						Slime.class, Slime.class));

			// Prison
			case 6:
				//3x skeleton, 1x thief, 1x swarm
				return new ArrayList<>(Arrays.asList(Skeleton.class, Skeleton.class, Skeleton.class,
						Thief.class,
						Swarm.class));
			case 7:
				//3x skeleton, 1x thief, 1x DM-100, 1x guard
				return new ArrayList<>(Arrays.asList(Skeleton.class, Skeleton.class, Skeleton.class,
						Thief.class,
						DM100.class,
						Guard.class));
			case 8:
				//2x skeleton, 1x thief, 2x DM-100, 2x guard, 1x necromancer
				return new ArrayList<>(Arrays.asList(Skeleton.class, Skeleton.class,
						Thief.class,
						DM100.class, DM100.class,
						Guard.class, Guard.class,
						Necromancer.class));
			case 9: case 10:
				//1x skeleton, 1x thief, 2x DM-100, 2x guard, 2x necromancer
				return new ArrayList<>(Arrays.asList(Skeleton.class,
						Thief.class,
						DM100.class, DM100.class,
						Guard.class, Guard.class,
						Necromancer.class, Necromancer.class));

			// Caves
			case 11:
				//3x bat, 1x brute, 1x shaman
				return new ArrayList<>(Arrays.asList(
						Bat.class, Bat.class, Bat.class,
						Brute.class,
						Shaman.random()));
			case 12:
				//2x bat, 2x brute, 1x shaman, 1x spinner
				return new ArrayList<>(Arrays.asList(
						Bat.class, Bat.class,
						Brute.class, Brute.class,
						Shaman.random(),
						Spinner.class));
			case 13:
				//1x bat, 2x brute, 2x shaman, 2x spinner, 1x DM-200
				return new ArrayList<>(Arrays.asList(
						Bat.class,
						Brute.class, Brute.class,
						Shaman.random(), Shaman.random(),
						Spinner.class, Spinner.class,
						DM200.class));
			case 14: case 15:
				//1x bat, 1x brute, 2x shaman, 2x spinner, 2x DM-300
				return new ArrayList<>(Arrays.asList(
						Bat.class,
						Brute.class,
						Shaman.random(), Shaman.random(),
						Spinner.class, Spinner.class,
						DM200.class, DM200.class));

			// City
			case 16:
				//3x ghoul, 1x elemental, 1x warlock
				return new ArrayList<>(Arrays.asList(
						Ghoul.class, Ghoul.class, Ghoul.class,
						Elemental.random(),
						Warlock.class));
			case 17:
				//1x ghoul, 2x elemental, 1x warlock, 1x monk
				return new ArrayList<>(Arrays.asList(
						Ghoul.class,
						Elemental.random(), Elemental.random(),
						Warlock.class,
						Monk.class));
			case 18:
				//1x ghoul, 1x elemental, 2x warlock, 2x monk, 1x golem
				return new ArrayList<>(Arrays.asList(
						Ghoul.class,
						Elemental.random(),
						Warlock.class, Warlock.class,
						Monk.class, Monk.class,
						Golem.class));
			case 19: case 20:
				//1x elemental, 2x warlock, 2x monk, 3x golem
				return new ArrayList<>(Arrays.asList(
						Elemental.random(),
						Warlock.class, Warlock.class,
						Monk.class, Monk.class,
						Golem.class, Golem.class, Golem.class));

			// Halls
			case 21:
				//2x succubus, 1x evil eye
				return new ArrayList<>(Arrays.asList(
						Succubus.class, Succubus.class,
						Eye.class));
			case 22:
				//1x succubus, 1x evil eye
				return new ArrayList<>(Arrays.asList(
						Succubus.class,
						Eye.class));
			case 23:
				//1x succubus, 2x evil eye, 1x scorpio
				return new ArrayList<>(Arrays.asList(
						Succubus.class,
						Eye.class, Eye.class,
						Scorpio.class));
			case 24: case 25: case 26:
				//1x succubus, 2x evil eye, 3x scorpio
				return new ArrayList<>(Arrays.asList(
						Succubus.class,
						Eye.class, Eye.class,
						Scorpio.class, Scorpio.class, Scorpio.class));
		}

	}

	//has a chance to add a rarely spawned mobs to the rotation
	public static void addRareMobs( int depth, ArrayList<Class<?extends Mob>> rotation ){

        ArrayList<Class<? extends Mob>> rareMobs = new ArrayList<>(Arrays.asList(Thief.class, Bat.class, Ghoul.class, Succubus.class));
		for (int i = 0; i < rareMobs.size(); i++){
			if (depth == Dungeon.chapterSize()*(i+1)-1 && Random.Float() < (Dungeon.mode == Dungeon.GameMode.BIGGER ? 0.05f : 0.025f))
				rotation.add(rareMobs.get(i));
		}
	}

	//switches out regular mobs for their alt versions when appropriate
	private static void swapMobRareAlts(ArrayList<Class<?extends Mob>> rotation) {
		float altChance = 1 / 50f * RatSkull.exoticChanceMultiplier();
		if (Dungeon.branch == AbyssLevel.BRANCH && Dungeon.depth % 5 == 0)
			altChance *= 20f;
		if (Dungeon.mode == Dungeon.GameMode.CHAOS){
			altChance = 1 / 2f;
		}
		for (int i = 0; i < rotation.size(); i++) {
			if (Random.Float() < altChance) {
				Class<? extends Mob> cl = rotation.get(i);
				if (cl == Rat.class)                cl = Albino.class;
				else if (cl == Gnoll.class)         cl = GnollExile.class;
				else if (cl == Crab.class)          cl = HermitCrab.class;
				else if (cl == Slime.class)         cl = CausticSlime.class;

				else if (cl == Thief.class)         cl = Bandit.class;
				else if (cl == Necromancer.class)   cl = SpectralNecromancer.class;

				else if (cl == Brute.class)         cl = ArmoredBrute.class;
				else if (cl == DM200.class)         cl = DM201.class;

				else if (cl == Monk.class)          cl = Senior.class;
				//chaos elemental spawning happens in Elemental.Random

				else if (cl == Scorpio.class)       cl = Acidic.class;
                else if (cl == GhostChicken.class || cl == DarkestElf.class){
                    cl = AbyssalNightmare.class;
                } else if (cl == BlinkingMan.class || cl == Trappet.class){
                    cl = Dragon.class;
                } else if (cl == Phantom.class || cl == SpectreRat.class){
                    cl = LostSpirit.class;
                }

				rotation.set(i, cl);
			}
		}
	}

	private static void swapMobVariantAlts(ArrayList<Class<?extends Mob>> rotation){
		for (int i = 0; i < rotation.size(); i++) {
			Class<? extends Mob> mobVariant;
			if ((mobVariant = Dungeon.MobVariants.getCurrentReplacement(rotation.get(i))) != null){
				rotation.set(i, mobVariant);
			}
		}
	}
}
