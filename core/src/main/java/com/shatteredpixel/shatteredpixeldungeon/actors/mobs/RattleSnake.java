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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RattleSnakeSprite;
import com.watabou.utils.Random;

public class RattleSnake extends Mob {
	
	{
		spriteClass = RattleSnakeSprite.class;
		
		HP = HT = 15;
		defenseSkill = 45;
		viewDistance = Light.DISTANCE;
		
		EXP = 9;
		maxLvl = 18;
		
		loot = new Dart();
		lootChance = 0.15f;
		properties.add(Property.RANGED);
//		properties.add(Property.ANIMAL);
	}

	{
        resistances.addAll(AntiMagic.RESISTS);
    }
	
	@Override
	public int damageRoll() {
        int i = Random.NormalIntRange(0, 12);
        if (Dungeon.level.adjacent(pos, enemy.pos)) i = Random.NormalIntRange(2, 7);
        return i;
    }

	@Override
	public float attackDelay() {
		return super.attackDelay()/2f;
	}

	@Override
	public int attackSkill( Char target ) {
		return 18;
	}

	@Override
	public int attackProc(Char enemy, int damage) {
//		if (Dungeon.mode == Dungeon.GameMode.DIFFICULT && !Dungeon.level.adjacent(pos, enemy.pos)){
//			damage = new Unstable().proc(new Weapon() {
//				@Override
//				public int STRReq(int lvl) {
//					return 0;
//				}
//
//				@Override
//				public int min(int lvl) {
//					return 5;
//				}
//
//				@Override
//				public int max(int lvl) {
//					return 16;
//				}
//
//				@Override
//				public int level() {
//					return 6;
//				}
//			}, this, enemy, damage);
//		}
		return super.attackProc(enemy, damage);
	}

//	@Override
//	public int defenseValue() {
//		return 8;
//	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 7);
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
		return attack.collisionPos == enemy.pos;
	}
	
}
