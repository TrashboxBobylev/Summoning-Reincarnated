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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DogSprite;
import com.watabou.utils.Random;

public class Dog extends Mob {

	{
		spriteClass = DogSprite.class;
		
		HP = HT = 9;
		defenseSkill = 12;
		baseSpeed = 2f;
		
		EXP = 4;
		maxLvl = 9;
		
		loot = Generator.random();
		lootChance = 0.1f;
//		properties.add(Property.ANIMAL);
	}

	{
		immunities.add(ChampionEnemy.Blazing.class);
//		immunities.add(ChampionEnemy.Swiftness.class);
		immunities.add(ChampionEnemy.Blessed.class);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 4 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 10;
	}

	@Override
	public float attackDelay() {
//		if (Dungeon.mode == Dungeon.GameMode.DIFFICULT){
//			return 0.7f*super.attackDelay();
//		}
		return super.attackDelay()*0.5f;
	}

//	@Override
//	public int defenseValue() {
//		return 2;
//	}
	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 3);
	}
}
