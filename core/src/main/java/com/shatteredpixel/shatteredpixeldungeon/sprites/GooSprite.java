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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.Emitter.Factory;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class GooSprite extends MobSprite {
	
	private Animation pump;
	private Animation pumpAttack;

	private Emitter spray;
	private ArrayList<Emitter> pumpUpEmitters = new ArrayList<>();

	public GooSprite() {
		super();
		
		texture( Assets.Sprites.GOO );
		
		TextureFilm frames = new TextureFilm( texture, 20, 14 );
		
		idle = new Animation( 10, true );
		idle.frames( frames, 2, 1, 0, 0, 1 );
		
		run = new Animation( 15, true );
		run.frames( frames, 3, 2, 1, 2 );
		
		pump = new Animation( 20, true );
		pump.frames( frames, 4, 3, 2, 1, 0 );

		pumpAttack = new Animation ( 20, false );
		pumpAttack.frames( frames, 4, 3, 2, 1, 0, 7);

		attack = new Animation( 10, false );
		attack.frames( frames, 8, 9, 10 );
		
		die = new Animation( 10, false );
		die.frames( frames, 5, 6, 7 );
		
		play(idle);

		spray = centerEmitter();
		if (spray != null) {
			spray.autoKill = false;
			spray.pour(GooParticle.FACTORY, 0.04f);
			spray.on = false;
		}
	}

	@Override
	public void link(Char ch) {
		super.link(ch);
		if (ch.HP*2 <= ch.HT)
			spray(true);
	}

	public void pumpUp( int warnDist ) {
		if (warnDist == 0){
			clearEmitters();
		} else {
			play(pump);
			Sample.INSTANCE.play( Assets.Sounds.CHARGEUP, 1f, warnDist == 1 ? 0.8f : 1f );
			if (ch.fieldOfView == null || ch.fieldOfView.length != Dungeon.level.length()){
				ch.fieldOfView = new boolean[Dungeon.level.length()];
				Dungeon.level.updateFieldOfView( ch, ch.fieldOfView );
			}
			for (int i = 0; i < Dungeon.level.length(); i++){
				if (ch.fieldOfView != null && ch.fieldOfView[i]
						&& Dungeon.level.distance(i, ch.pos) <= warnDist
						&& new Ballistica( ch.pos, i, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID).collisionPos == i
						&& new Ballistica( i, ch.pos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID).collisionPos == ch.pos){
					Emitter e = CellEmitter.get(i);
					e.pour(GooParticle.FACTORY, 0.04f);
					pumpUpEmitters.add(e);
				}
			}
		}
	}

	public void clearEmitters(){
		for (Emitter e : pumpUpEmitters){
			e.on = false;
		}
		pumpUpEmitters.clear();
	}

	public void triggerEmitters(){
		for (Emitter e : pumpUpEmitters){
			e.burst(ElmoParticle.FACTORY, 10);
		}
		Sample.INSTANCE.play( Assets.Sounds.BURNING );
		pumpUpEmitters.clear();
	}

	public void pumpAttack() { play(pumpAttack); }

	@Override
	public void play(Animation anim) {
		if (anim != pump && anim != pumpAttack){
			clearEmitters();
		}
		super.play(anim);
	}

	@Override
	public int blood() {
		return 0xFF000000;
	}

	public void spray(boolean on){
		spray.on = on;
	}

	@Override
	public void update() {
		super.update();
		if (spray != null) {
			spray.pos(center());
			spray.visible = visible;
		}
	}

	public static class GooParticle extends PixelParticle.Shrinking {

		public static final Emitter.Factory FACTORY = new Factory() {
			@Override
			public void emit( Emitter emitter, int index, float x, float y ) {
				((GooParticle)emitter.recycle( GooParticle.class )).reset( x, y );
			}
		};

		public GooParticle() {
			super();

			color( 0x000000 );
			lifespan = 0.3f;

			acc.set( 0, +50 );
		}

		public void reset( float x, float y ) {
			revive();

			this.x = x;
			this.y = y;

			left = lifespan;

			size = 4;
			speed.polar( -Random.Float( PointF.PI ), Random.Float( 32, 48 ) );
		}

		@Override
		public void update() {
			super.update();
			float p = left / lifespan;
			am = p > 0.5f ? (1 - p) * 2f : 1;
		}
	}

	@Override
	public void onComplete( Animation anim ) {
		super.onComplete(anim);

		if (anim == pumpAttack) {

			triggerEmitters();

			idle();
			ch.onAttackComplete();
		} else if (anim == die) {
			spray.killAndErase();
		}
	}
}
