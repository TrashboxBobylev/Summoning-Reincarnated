/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class StormCloud extends Blob {
	
	@Override
	protected void evolve() {
		super.evolve();
		
		int cell;

		Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);
		for (int i = area.left; i < area.right; i++){
			for (int j = area.top; j < area.bottom; j++){
				cell = i + j*Dungeon.level.width();
				if (cur[cell] > 0) {
					Dungeon.level.setCellToWater(true, cell);
					if (fire != null){
						fire.clear(cell);
					}

					//fiery enemies take damage as if they are in toxic gas
					Char ch = Actor.findChar(cell);
					if (ch != null){
						if (!ch.isImmune(getClass())
							&& Char.hasProp(ch, Char.Property.FIERY)) {
							ch.damage(1 + Dungeon.scalingDepth() / 5, this);
						}

						if (!(ch instanceof Hero)) {
							ArrayList<Integer> pushTargets = new ArrayList<>();
							for (int c : PathFinder.NEIGHBOURS8) {
								if (!Dungeon.level.solid[cell + c]) {
									pushTargets.add(cell + c);
								}
							}

							if (!pushTargets.isEmpty()) {
								int pushCell = Random.element(pushTargets);
								int ce = Random.element(pushTargets);
								// attempts to push things away from player more frequently
								if (Dungeon.level.distance(Dungeon.hero.pos, ce) > Dungeon.level.distance(Dungeon.hero.pos, pushCell)) {
									pushCell = ce;
								}
								if (ch.sprite != null && ch.sprite.visible){
									Splash.at( DungeonTilemap.tileCenterToWorld( cell ),
											PointF.angle(DungeonTilemap.tileCenterToWorld( cell ), DungeonTilemap.tileCenterToWorld( pushCell)), PointF.PI, 0xb2e9ff, 50, 0.005f);
								}
								WandOfBlastWave.throwChar(ch, new Ballistica(cell, pushCell, Ballistica.MAGIC_BOLT), 1 + Random.Int(3), false, false, this);
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		emitter.pour( Speck.factory( Speck.STORM ), 0.4f );
	}
	
	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
	
}
