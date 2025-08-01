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

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class CirclePitRoom extends StandardRoom {

	@Override
	public int minWidth() {
		return Math.max(8, super.minWidth());
	}

	@Override
	public int minHeight() {
		return Math.max(8, super.minHeight());
	}

	@Override
	public float[] sizeCatProbs() {
		return new float[]{4, 2, 1};
	}

	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );

		Painter.fillEllipse( level, this, 1 , Terrain.EMPTY );

		for (Door door : connected.values()) {
			door.set( Door.Type.REGULAR );
			if (door.x == left || door.x == right){
				Painter.drawInside(level, this, door, width()/2, Terrain.EMPTY);
			} else {
				Painter.drawInside(level, this, door, height()/2, Terrain.EMPTY);
			}
		}

		Painter.fillEllipse( level, this, 3 , Terrain.CHASM );

		//50/100% chance based on size
		if (sizeCat != SizeCategory.NORMAL && Random.Int(4-sizeFactor()) == 0) {
			while (true){
				//draw line from center (plus or minus one in each DIR) to side
				Point center = center();
				center.x += Random.IntRange(-1, 1);
				center.y += Random.IntRange(-1, 1);

				Point edge = new Point(center);
				switch (Random.Int(4)){
					case 0:
						edge.x = left;
						break;
					case 1:
						edge.y = top;
						break;
					case 2:
						edge.x  = right;
						break;
					case 3:
						edge.y = bottom;
						break;
				}

				boolean valid = true;
				for (Point door : connected.values()){
					if (door.equals(edge)){
						valid = false;
					}
				}

				if (valid) {
					Painter.drawLine(level, edge, center, Terrain.REGION_DECO_ALT);
					Painter.drawInside(level, this, edge, 1, Terrain.EMPTY_SP);
					Painter.set(level, edge, Terrain.WALL);

					//TODO pick a random cell to make empty_sp?
				}
				break;
			}
		}
	}
}
