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

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.RatSkull;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PiranhaSprite;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Piranha extends Mob {
	
	{
		spriteClass = PiranhaSprite.class;

		baseSpeed = 2f;
		
		EXP = 0;
		
		loot = MysteryMeat.class;
		lootChance = 1f;
		
		SLEEPING = new Sleeping();
		WANDERING = new Wandering();
		HUNTING = new Hunting();
		
		state = SLEEPING;

	}
	
	public Piranha() {
		super();
		
		HP = HT = 10 + Dungeon.depth * 5;
		defenseSkill = 10 + Dungeon.depth * 2;
	}
	
	@Override
	protected boolean act() {
		
		if (!Dungeon.level.water[pos] || flying) {
			if (sprite != null && buff(Levitation.class) != null){
				sprite.emitter().burst(Speck.factory( Speck.JET ), 10);
			}
			dieOnLand();
			return true;
		} else {
			return super.act();
		}
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( Dungeon.depth, 4 + Dungeon.depth * 2 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 20 + Dungeon.depth * 2;
	}
	
	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, Dungeon.depth);
	}

	@Override
	public boolean surprisedBy(Char enemy, boolean attacking) {
		if (enemy == Dungeon.hero && (!attacking || ((Hero)enemy).canSurpriseAttack())){
			if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
				fieldOfView = new boolean[Dungeon.level.length()];
				Dungeon.level.updateFieldOfView( this, fieldOfView );
			}
			return state == SLEEPING || !fieldOfView[enemy.pos] || enemy.invisible > 0;
		}
		return super.surprisedBy(enemy, attacking);
	}

	public void dieOnLand(){
		die( null );
	}

	@Override
	public void die( Object cause ) {
		super.die( cause );
		
		Statistics.piranhasKilled++;
		Badges.validatePiranhasKilled();
	}

	@Override
	public float spawningWeight() {
		return 0;
	}

	@Override
	public boolean reset() {
		return true;
	}
	
	@Override
	protected boolean getCloser( int target ) {
		
		if (rooted) {
			return false;
		}
		
		int step = Dungeon.findStep( this, target, BArray.and(Dungeon.level.water, Dungeon.level.passable, null), fieldOfView, Dungeon.PATHBLOCK_NORMAL );
		if (step != -1) {
			move( step );
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected boolean getFurther( int target ) {
		int step = Dungeon.flee( this, target, BArray.and(Dungeon.level.water, Dungeon.level.passable, null), fieldOfView, true );
		if (step != -1) {
			move( step );
			return true;
		} else {
			return false;
		}
	}
	
	{
		for (Class c : new BlobImmunity().immunities()){
			if (c != Electricity.class && c != Freezing.class){
				immunities.add(c);
			}
		}
		immunities.add( Burning.class );
	}
	
	//if there is not a path to the enemy, piranhas act as if they can't see them
	private class Sleeping extends Mob.Sleeping{
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (enemyInFOV) {
				PathFinder.buildDistanceMap(enemy.pos, Dungeon.level.water, viewDistance);
				enemyInFOV = PathFinder.distance[pos] != Integer.MAX_VALUE;
			}
			
			return super.act(enemyInFOV, justAlerted);
		}
	}
	
	private class Wandering extends Mob.Wandering{
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (enemyInFOV) {
				PathFinder.buildDistanceMap(enemy.pos, Dungeon.level.water, viewDistance);
				enemyInFOV = PathFinder.distance[pos] != Integer.MAX_VALUE;
			}
			
			return super.act(enemyInFOV, justAlerted);
		}
	}
	
	private class Hunting extends Mob.Hunting{
		
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (enemyInFOV) {
				PathFinder.buildDistanceMap(enemy.pos, Dungeon.level.water, viewDistance);
				enemyInFOV = PathFinder.distance[pos] != Integer.MAX_VALUE;
			}
			
			return super.act(enemyInFOV, justAlerted);
		}
	}

	public static Piranha random(){
		float altChance = 1/50f * RatSkull.exoticChanceMultiplier();
		if (Random.Float() < altChance){
			return new PhantomPiranha();
		} else {
			return new Piranha();
		}
	}
}
