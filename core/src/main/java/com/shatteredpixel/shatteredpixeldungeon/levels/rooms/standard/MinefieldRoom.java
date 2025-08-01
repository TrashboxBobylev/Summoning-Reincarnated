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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrapMechanism;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class MinefieldRoom extends StandardRoom {

	@Override
	public float[] sizeCatProbs() {
		return new float[]{4, 1, 0};
	}

	@Override
	public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
		int cell = l.pointToCell(pointInside(p, 1));
		return l.map[cell] == Terrain.EMPTY;
	}

	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY );
		for (Door door : connected.values()) {
			door.set( Door.Type.REGULAR );
		}

		int mines = (int)Math.round(Math.sqrt(square()));

		switch (sizeCat){
			case NORMAL:
				mines -= 3;
				break;
			case LARGE:
				mines += 3;
				break;
			case GIANT:
				mines += 9;
				break;
		}

		float revealedChance = TrapMechanism.revealHiddenTrapChance();
		float revealInc = 0;
		for (int i = 0; i < mines; i++ ){
			int pos;
			do {
				pos = level.pointToCell(random(1));
			} while (level.traps.get(pos) != null);

			//randomly places some embers around the mines
			for (int j = 0; j < 8; j ++){
				int c = PathFinder.NEIGHBOURS8[Random.Int(8)];
				if (level.traps.get(pos+c) == null && level.map[pos+c] == Terrain.EMPTY){
					Painter.set(level, pos+c, Terrain.EMBERS);
				}
			}

			revealInc += revealedChance;
			if (revealInc >= 1) {
				Painter.set(level, pos, Terrain.TRAP);
				level.setTrap(new ExplosiveTrap().reveal(), pos);
				revealInc--;
			} else {
				Painter.set(level, pos, Terrain.SECRET_TRAP);
				level.setTrap(new ExplosiveTrap().hide(), pos);
			}

		}

	}
}
