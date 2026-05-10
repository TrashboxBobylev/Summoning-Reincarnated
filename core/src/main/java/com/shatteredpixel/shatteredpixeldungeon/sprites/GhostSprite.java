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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class GhostSprite extends MobSprite {
	
	public GhostSprite() {
		super();
		
		texture( Assets.Sprites.GHOST );
		
		TextureFilm frames = new TextureFilm( texture, 14, 15 );
		
		idle = new Animation( 5, true );
		idle.frames( frames, 0, 1 );
		
		run = new Animation( 10, true );
		run.frames( frames, 0, 1 );

		attack = new Animation( 10, false );
		attack.frames( frames, 0, 2, 3 );

		die = new Animation( 8, false );
		die.frames( frames, 0, 4, 5, 6, 7 );

		zap = attack.clone();
		
		play( idle );
	}
	
	@Override
	public void draw() {
		Blending.setLightMode();
		super.draw();
		Blending.setNormalMode();
	}

	@Override
	public void onComplete( Animation anim ) {
		if (anim == zap) {
			idle();
		}
		super.onComplete( anim );
	}

	@Override
	public void zap(int cell) {
		super.zap( cell );
		if (ch instanceof DriedRose.GhostHero && ((DriedRose.GhostHero) ch).rose != null){
			DriedRose rose = ((DriedRose.GhostHero) ch).rose;
			Wand wand;
			if (rose.type() == 3 && (wand = rose.ghostWand()) != null){
				final Ballistica shot = new Ballistica( ch.pos, cell, wand.collisionProperties(cell));
				int dest = shot.collisionPos;
				if (Dungeon.isChallenged(Conducts.Conduct.NO_MAGIC)){
					MagicMissile.boltFromChar( ch.sprite.parent,
							MagicMissile.RAINBOW,
							ch.sprite,
							shot.collisionPos,
							() -> {
								Emitter emitter = CellEmitter.center(shot.collisionPos);
								emitter.burst(RainbowParticle.SUPER_BURST, Random.Int(60, 120));
								for (int i : PathFinder.NEIGHBOURS9){
									Char ch = Actor.findChar( shot.collisionPos + i );
									if (ch != null) {
										ch.damage(1 + Dungeon.scalingDepth() / 3, ch);
									}
								}
								Sample.INSTANCE.play( Assets.Sounds.BLAST, 1.0f, 2.0f );
								ch.spend(1f);
							});
					Sample.INSTANCE.play( Assets.Sounds.ZAP );
				} else {
					wand.fx(ch, shot, () -> ((DriedRose.GhostHero) ch).onZapComplete(shot, wand));
				}
			}
		}
	}

	@Override
	public void die() {
		super.die();
		emitter().start( ShaftParticle.FACTORY, 0.3f, 4 );
		emitter().start( Speck.factory( Speck.LIGHT ), 0.2f, 3 );
	}
	
	@Override
	public int blood() {
		return 0xFFFFFF;
	}
}
