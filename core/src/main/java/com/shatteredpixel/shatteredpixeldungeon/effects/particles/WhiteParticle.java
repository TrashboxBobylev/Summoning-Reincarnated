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

package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.Emitter.Factory;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.ColorMath;
import com.watabou.utils.Random;

public class WhiteParticle extends PixelParticle.Shrinking {

	public static final Factory UP = new Factory() {
		@Override
		public void emit( Emitter emitter, int index, float x, float y ) {
			((WhiteParticle)emitter.recycle( WhiteParticle.class )).resetUp( x, y );
		}
	};
	
	public void reset( float x, float y ) {
		revive();
		
		this.x = x;
		this.y = y;
		
		speed.set( Random.Float( -5, +5 ), Random.Float( -5, +5 ) );
		
		size = 6;
		left = lifespan = 0.5f;
	}

	public void resetUp( float x, float y ) {
		revive();
		
		speed.set( Random.Float( -8, +8 ), Random.Float( -32, -48 ) );
		this.x = x;
		this.y = y;
		
		size = 6;
		left = lifespan = 1f;
	}
	
	@Override
	public void update() {
		super.update();
		
		float p = left / lifespan;
		// alpha: 0 -> 1 -> 0; size: 6 -> 0; color: 0x660044 -> 0x000000
		color( ColorMath.interpolate( 0xE0E0E0, 0xFFFFFF, p ) );
		am = p < 0.5f ? p * p * 4 : (1 - p) * 2;
	}
}