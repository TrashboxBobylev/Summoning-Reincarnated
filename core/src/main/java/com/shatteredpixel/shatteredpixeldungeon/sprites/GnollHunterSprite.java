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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.GnollHunter;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;

public class GnollHunterSprite extends MinionSprite {

	protected Animation cast;

	public GnollHunterSprite() {
		super();

		texture( Assets.Sprites.GNOLL );

		TextureFilm frames = new TextureFilm( texture, 12, 15 );

        int c = 42;

        idle = new MovieClip.Animation( 2, true );
        idle.frames( frames, 0+c, 0+c, 0+c, 1+c, 0+c, 0+c, 1+c, 1+c );

        run = new MovieClip.Animation( 12, true );
        run.frames( frames, 4+c, 5+c, 6+c, 7+c );

        attack = new MovieClip.Animation( 12, false );
        attack.frames( frames, 2+c, 3+c, 0+c );

        cast = attack.clone();

        die = new Animation( 12, false );
        die.frames( frames, 8+c, 9+c, 10+c );

        play( idle );
	}

	//blinding dart instead of paralytic
	@Override
	public void attack( int cell ) {
		if (!Dungeon.level.adjacent(cell, ch.pos)) {

			((MissileSprite)parent.recycle( MissileSprite.class )).
					reset( ch.pos, cell, new GnollHunter.GnollShot(), new Callback() {
						@Override
						public void call() {
							ch.onAttackComplete();
						}
					} );

			play( cast );
			turnTo( ch.pos , cell );

		} else {

			super.attack( cell );

		}
	}
}
