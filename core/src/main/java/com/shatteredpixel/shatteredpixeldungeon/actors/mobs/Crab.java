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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.ChaosSaber;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CrabSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Crab extends Mob {

	{
		spriteClass = CrabSprite.class;
		
		HP = HT = 15;
		if (Dungeon.mode == Dungeon.GameMode.DIFFICULT){
			HP = HT = 9;
		}
		defenseSkill = 5;
		baseSpeed = 2f;
		
		EXP = 4;
		maxLvl = 9;
		
		loot = MysteryMeat.class;
		lootChance = 0.167f;

        properties.add(Property.ANIMAL);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 7 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 12;
	}

	@Override
	protected boolean act() {
		boolean act = super.act();
		if (Dungeon.mode == Dungeon.GameMode.DIFFICULT && !isSuppressed()) {
			if (canSee(Dungeon.hero.pos) && Dungeon.hero == enemy){
				int bestPos = -1;
				for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
					int p = pos + PathFinder.NEIGHBOURS8[i];
					if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
						if (bestPos == -1 || Dungeon.level.trueDistance(p, pos) < Dungeon.level.trueDistance(bestPos, pos)){
							bestPos = p;
						}
					}
				}
				int count = 0;
				for (Char ch : Actor.chars()){
					if (ch instanceof ChaosSaber && ch.alignment == Alignment.ENEMY){
						count++;
					}
				}
				if (bestPos != -1 && count < 2) {
					ChaosSaber sword = new ChaosSaber();
					sword.level = 0;
					sword.alignment = Alignment.ENEMY;
					sword.state = sword.HUNTING;
					GameScene.add(sword, 1);
					ScrollOfTeleportation.appear(sword, bestPos);
				}
				spend(1f);
			}
		}
		return act;
	}
	
	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 4);
	}
}
