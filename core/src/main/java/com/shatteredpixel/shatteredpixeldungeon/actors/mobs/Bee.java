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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BeeSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

//FIXME the AI for these things is becoming a complete mess, should refactor
public class Bee extends Mob {
	
	{
		spriteClass = BeeSprite.class;
		
		viewDistance = 4;

		EXP = 0;
		
		flying = true;
		state = WANDERING;
		
		//only applicable when the bee is charmed with elixir of honeyed healing
		intelligentAlly = true;
	}

	private int level;

	//-1 refers to a pot that has gone missing.
	private int potPos;
	//-1 for no owner
	private int potHolder;
	
	private static final String LEVEL	    = "level";
	private static final String POTPOS	    = "potpos";
	private static final String POTHOLDER	= "potholder";
	private static final String ALIGMNENT   = "alignment";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LEVEL, level );
		bundle.put( POTPOS, potPos );
		bundle.put( POTHOLDER, potHolder );
		bundle.put( ALIGMNENT, alignment);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		spawn( bundle.getInt( LEVEL ) );
		potPos = bundle.getInt( POTPOS );
		potHolder = bundle.getInt( POTHOLDER );
		if (bundle.contains(ALIGMNENT)) alignment = bundle.getEnum( ALIGMNENT, Alignment.class);
	}

	@Override
	public void die(Object cause) {
		flying = false;
		super.die(cause);
	}
	
	public void spawn( int level ) {
		this.level = level;
		
		HT = (2 + level) * 4;
		defenseSkill = 9 + level;
	}

	public void setPotInfo(int potPos, Char potHolder){
		this.potPos = potPos;
		if (potHolder == null)
			this.potHolder = -1;
		else
			this.potHolder = potHolder.id();
	}
	
	public int potPos(){
		return potPos;
	}
	
	public int potHolderID(){
		return potHolder;
	}
	
	@Override
	public int attackSkill( Char target ) {
		return defenseSkill;
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( HT / 10, HT / 4 );
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		if (enemy instanceof Mob) {
			((Mob)enemy).aggro( this );
		}
		return damage;
	}

	@Override
	public boolean add(Buff buff) {
		if (super.add(buff)) {
			//TODO maybe handle honeyed bees with their own ally buff?
			if (buff instanceof AllyBuff) {
				intelligentAlly = false;
				setPotInfo(-1, null);
			}
			return true;
		}
		return false;
	}

	@Override
	protected Char chooseEnemy() {
		//if the pot is no longer present, default to regular AI behaviour
		if (alignment == Alignment.ALLY || (potHolder == -1 && potPos == -1)){
			return super.chooseEnemy();
		
		//if something is holding the pot, target that
		}else if (Actor.findById(potHolder) != null){
			return (Char) Actor.findById(potHolder);
			
		//if the pot is on the ground
		}else {
			
			//try to find a new enemy in these circumstances
			if (enemy == null || !enemy.isAlive() || !Actor.chars().contains(enemy) || state == WANDERING
					|| Dungeon.level.distance(enemy.pos, potPos) > 3
					|| (alignment == Alignment.ALLY && enemy.alignment == Alignment.ALLY)
					|| (buff( Amok.class ) == null && enemy.isInvulnerable(getClass()))){
				
				//target closest potential enemy near the pot
				Char closest = null;
				for (Mob mob : Dungeon.level.mobs) {
					if (!(mob == this)
							&& Dungeon.level.distance(mob.pos, potPos) <= 3
							&& mob.alignment != Alignment.NEUTRAL
							&& !mob.isInvulnerable(getClass())
							&& !(alignment == Alignment.ALLY && mob.alignment == Alignment.ALLY)) {
						if (closest == null || Dungeon.level.distance(closest.pos, pos) > Dungeon.level.distance(mob.pos, pos)){
							closest = mob;
						}
					}
				}
				
				if (closest != null){
					return closest;
				} else {
					if (alignment != Alignment.ALLY && Dungeon.level.distance(Dungeon.hero.pos, potPos) <= 3){
						return Dungeon.hero;
					} else {
						return null;
					}
				}
				
			} else {
				return enemy;
			}

			
		}
	}

	@Override
	protected boolean getCloser(int target) {
		if (alignment == Alignment.ALLY && enemy == null && buffs(AllyBuff.class).isEmpty()) {
			target = Dungeon.hero.pos;
		} else if (enemy != null && Actor.findById(potHolder) == enemy) {
			target = enemy.pos;
		} else if (potPos != -1 && (state == WANDERING || Dungeon.level.distance(target, potPos) > 3)) {
			if (!Dungeon.level.insideMap(potPos)){
				potPos = -1;
			} else {
				this.target = target = potPos;
			}
		}
		return super.getCloser( target );
	}
	
	@Override
	public String description() {
		if (alignment == Alignment.ALLY && buffs(AllyBuff.class).isEmpty()){
			return Messages.get(this, "desc_honey");
		} else {
			return super.description();
		}
	}
}