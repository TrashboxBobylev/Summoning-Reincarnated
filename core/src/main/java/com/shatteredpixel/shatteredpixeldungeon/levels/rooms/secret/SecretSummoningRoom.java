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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrapMechanism;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.watabou.utils.Point;

public class SecretSummoningRoom extends SecretRoom {
	
	//minimum of 3x3 traps, max of 6x6 traps
	
	@Override
	public int maxWidth() {
		return 8;
	}
	
	@Override
	public int maxHeight() {
		return 8;
	}
	
	@Override
	public void paint(Level level) {
		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.SECRET_TRAP);
		
		Point center = center();
		level.drop(Challenges.process(Generator.random()), level.pointToCell(center)).setHauntedIfCursed().type = Heap.Type.SKELETON;

		float revealedChance = TrapMechanism.revealHiddenTrapChance();
		float revealInc = 0;
		for (Point p : getPoints()){
			int cell = level.pointToCell(p);
			if (level.map[cell] == Terrain.SECRET_TRAP){
				revealInc += revealedChance;
				if (revealInc >= 1) {
					level.setTrap(new SummoningTrap().reveal(), cell);
					Painter.set(level, cell, Terrain.TRAP);
					revealInc--;
				} else {
					level.setTrap(new SummoningTrap().hide(), cell);
				}
			}
		}
		
		entrance().set(Door.Type.HIDDEN);
	}
	
}
