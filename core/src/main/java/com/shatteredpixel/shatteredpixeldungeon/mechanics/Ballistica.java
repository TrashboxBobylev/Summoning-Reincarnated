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

package com.shatteredpixel.shatteredpixeldungeon.mechanics;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.List;

import static com.shatteredpixel.shatteredpixeldungeon.actors.Char.Alignment.ALLY;

public class Ballistica {

	//note that the path is the FULL path of the projectile, including tiles after collision.
	//make sure to generate a subPath for the common case of going source to collision.
	public ArrayList<Integer> path = new ArrayList<>();
	public Integer sourcePos = null;
	public Integer collisionPos = null;
	public Integer collisionProperties = null;
	public Integer dist = 0;

	//parameters to specify the colliding cell
	public static final int STOP_TARGET = 1;    //ballistica will stop at the target cell
	public static final int STOP_CHARS = 2;     //ballistica will stop on first char hit
	public static final int STOP_SOLID = 4;     //ballistica will stop on solid terrain
	public static final int IGNORE_SOFT_SOLID = 8; //ballistica will ignore soft solid terrain, such as doors and webs
	public static final int REFLECT = 16; //ballistica will reflect instead of stopping
	public static final int IGNORE_ALLY_CHARS = 32; //ballistica will ignore allies

	public static final int FRIENDLY_PROJECTILE = STOP_TARGET	| STOP_CHARS	| STOP_SOLID | IGNORE_ALLY_CHARS;
	public static final int FRIENDLY_MAGIC      = STOP_CHARS  | STOP_SOLID | IGNORE_ALLY_CHARS;
	public static final int PROJECTILE =  	STOP_TARGET	| STOP_CHARS	| STOP_SOLID;

	public static final int MAGIC_BOLT =    STOP_CHARS  | STOP_SOLID;

	public static final int WONT_STOP =     0;
	public static int REFLECTION;
	public ArrayList<Integer> reflectPositions = new ArrayList<>();
	private int reflectTimes = 0;


	public Ballistica( int from, int to, int params ){
		sourcePos = from;
		collisionProperties = params;
		build(from, to,
				(params & STOP_TARGET) > 0,
				(params & STOP_CHARS) > 0,
				(params & STOP_SOLID) > 0,
				(params & IGNORE_SOFT_SOLID) > 0,
				(params & REFLECT) > 0,
				(params & IGNORE_ALLY_CHARS) > 0);

		if (collisionPos != null) {
			dist = path.indexOf(collisionPos);
		} else if (!path.isEmpty()) {
			collisionPos = path.get(dist = path.size() - 1);
		} else {
			path.add(from);
			collisionPos = from;
			dist = 0;
		}
	}

	private void build( int from, int to, boolean stopTarget, boolean stopChars, boolean stopTerrain, boolean ignoreSoftSolid, boolean reflect, boolean ignoreAllies ) {
		int w = Dungeon.level.width();

		int x0 = from % w;
		int x1 = to % w;
		int y0 = from / w;
		int y1 = to / w;

		int dx = x1 - x0;
		int dy = y1 - y0;

		int stepX = dx > 0 ? +1 : -1;
		int stepY = dy > 0 ? +1 : -1;

		dx = Math.abs(dx);
		dy = Math.abs(dy);

		int stepA;
		int stepB;
		int dA;
		int dB;

		if (dx > dy) {

			stepA = stepX;
			stepB = stepY * w;
			dA = dx;
			dB = dy;

		} else {

			stepA = stepY * w;
			stepB = stepX;
			dA = dy;
			dB = dx;

		}

		int cell = from;

		int err = dA / 2;
		if (reflect) {
			boolean alreadyReflected = false;
			while (Dungeon.level.insideMap(cell)) {
				// Wall case is treated differently by stopping one early
				if (stopTerrain && cell != sourcePos && Dungeon.level.solid[cell]) {
					int cellBeforeWall = path.get(path.size() - 1);

					alreadyReflected = reflect(stopTarget, stopChars, stopTerrain, ignoreSoftSolid, w, x0, y0, stepX, stepY, cell, alreadyReflected, cellBeforeWall);
				}

				path.add(cell);

				cell += stepA;

				err += dB;
				if (err >= dA) {
					err = err - dA;
					cell = cell + stepB;
				}
			}
		} else {
			while (Dungeon.level.insideMap(cell)) {

				//if we're in solid terrain, and there's no char there, collide with the previous cell.
				// we don't use solid here because we don't want to stop short of closed doors.
				if (collisionPos == null
						&& stopTerrain
						&& cell != sourcePos
						&& !Dungeon.level.passable[cell]
						&& !Dungeon.level.avoid[cell]
						&& Actor.findChar(cell) == null) {
					collide(path.get(path.size() - 1));
				}

				path.add(cell);

				if (collisionPos == null && stopTerrain && cell != sourcePos && Dungeon.level.solid[cell]) {
					if (ignoreSoftSolid && (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])) {
						//do nothing
					} else {
						collide(cell);
					}
				}
				if (collisionPos == null && cell != sourcePos && stopChars && Actor.findChar(cell) != null) {
					if (ignoreAllies && Actor.findChar(cell).alignment == ALLY){
						if (cell == to && stopTarget) {
							collide(cell);
						}
					} else {
						collide(cell);
					}
				}
				if (collisionPos == null && cell == to && stopTarget) {
					collide(cell);
				}

				cell += stepA;

				err += dB;
				if (err >= dA) {
					err = err - dA;
					cell = cell + stepB;
				}
			}
		}
	}

	private boolean reflect(boolean stopTarget, boolean stopChars, boolean stopTerrain, boolean ignoreSoftSolid, int w, int sourceX, int sourceY, int targetX, int targetY, int cell, boolean alreadyReflected, int cellBeforeWall) {
		if (!alreadyReflected) {
            targetX = cellBeforeWall % w;
            targetY = cellBeforeWall / w;
            int reflectX = targetX;
            int reflectY = targetY;

            int deltaX = targetX - sourceX;
            int deltaY = targetY - sourceY;

            // right angles would reflect everything right back at ya so they are ignored
            if( deltaX != 0 && deltaY != 0 ){
                boolean horizontWall = Dungeon.level.solid[ cellBeforeWall - ( deltaX > 0 ? 1 : -1 ) ];
                boolean verticalWall = Dungeon.level.solid[ cellBeforeWall - ( deltaY > 0 ? Dungeon.level.width() : -Dungeon.level.width() ) ];

                if( !horizontWall || !verticalWall ) {

                    // convex corners reflect in random direction
                    boolean reflectHorizontally = horizontWall || ( !verticalWall && Random.Int( 2 ) == 0 );

                    if( reflectHorizontally ) {
                        // perform horizontal reflection
                        reflectX += deltaX;
                        reflectY -= deltaY;
                    } else {
                        // perform vertical reflection
                        reflectX -= deltaX;
                        reflectY += deltaY;
                    }
                } else {

                    // concave corners reflect everything by both axes, unless hit from 45 degrees angle
                    if( Math.abs( deltaX ) != Math.abs( deltaY ) ){

                        if( deltaX > 0 == deltaY > 0 ){
                            reflectX -= deltaY;
                            reflectY -= deltaX;
                        } else {
                            reflectX += deltaY;
                            reflectY += deltaX;
                        }
                    }
                }
            }

            reflectX = (int) GameMath.gate( 0, reflectX, Dungeon.level.width() );
            reflectY = (int) GameMath.gate( 0, reflectY, Dungeon.level.height() );
            reflectPositions.add(cellBeforeWall);
            build(cellBeforeWall, reflectX + reflectY * Dungeon.level.width(), stopTarget, stopChars, stopTerrain, ignoreSoftSolid, ++reflectTimes < REFLECTION, false);
		}
		return true;
	}

	//we only want to record the first position collision occurs at.
	private void collide(int cell){
		if (collisionPos == null) {
			collisionPos = cell;
		}
	}

	//returns a segment of the path from start to end, inclusive.
	//if there is an error, returns an empty arraylist instead.
	public List<Integer> subPath(int start, int end){
		try {
			end = Math.min( end, path.size()-1);
			return path.subList(start, end+1);
		} catch (Exception e){
			ShatteredPixelDungeon.reportException(e);
			return new ArrayList<>();
		}
	}
}
