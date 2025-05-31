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

package com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StormCloud;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class PotionOfStormClouds extends ExoticPotion {
	
	{
		icon = ItemSpriteSheet.Icons.POTION_STRMCLOUD;
	}
	
	@Override
	public void shatter(int cell) {

		splash( cell );
		if (Dungeon.level.heroFOV[cell]) {
			identify();

			Sample.INSTANCE.play( Assets.Sounds.SHATTER );
			Sample.INSTANCE.play( Assets.Sounds.GAS );
		}

		int centerVolume = 100;
		for (int i : PathFinder.NEIGHBOURS8){
			if (!Dungeon.level.solid[cell+i]){
				GameScene.add( Blob.seed( cell+i, 100, StormCloud.class ) );
			} else {
				centerVolume += 100;
			}
		}
		
		GameScene.add( Blob.seed( cell, centerVolume, StormCloud.class ) );
	}

	@Override
	public int gooInfuseUses() {
		return 2;
	}

	@Override
	public void gooMinionAttack(Char ch) {
		for (int dir: PathFinder.NEIGHBOURS9){
			if (Random.Int(3) == 0){
				Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);
				Dungeon.level.setCellToWater(true, ch.pos + dir);
				if (fire != null){
					fire.clear(ch.pos + dir);
				}

				if (!ch.isImmune(getClass())
						&& Char.hasProp(ch, Char.Property.FIERY)){
					ch.damage(Dungeon.scalingDepth()/5, this);
				}

				ArrayList<Integer> pushTargets = new ArrayList<>();
				for (int c : PathFinder.NEIGHBOURS8) {
					if (!Dungeon.level.solid[ch.pos + c]) {
						pushTargets.add(ch.pos + c);
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
						Splash.at( DungeonTilemap.tileCenterToWorld( ch.pos ),
								PointF.angle(DungeonTilemap.tileCenterToWorld( ch.pos ), DungeonTilemap.tileCenterToWorld( pushCell)), PointF.PI, 0xb2e9ff, 50, 0.005f);
					}
					WandOfBlastWave.throwChar(ch, new Ballistica(ch.pos, pushCell, Ballistica.MAGIC_BOLT), 1 + Random.Int(3), false, false, this);
				}
			}
		}
	}
}
