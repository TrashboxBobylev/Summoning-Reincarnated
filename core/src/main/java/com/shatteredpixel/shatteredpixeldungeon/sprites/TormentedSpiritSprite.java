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
import com.watabou.noosa.TextureFilm;

public class TormentedSpiritSprite extends MobSprite {

	public TormentedSpiritSprite() {
		super();

		texture( Assets.Sprites.WRAITH );

		TextureFilm frames = new TextureFilm( texture, 14, 15 );

		int c = 9;

		idle = new Animation( 5, true );
		idle.frames( frames, c+0, c+1 );

		run = new Animation( 10, true );
		run.frames( frames, c+0, c+1 );

		attack = new Animation( 10, false );
		attack.frames( frames, c+0, c+2, c+3 );

		die = new Animation( 8, false );
		die.frames( frames, c+0, c+4, c+5, c+6, c+7 );

		play( idle );
	}

	@Override
	public int blood() {
		return 0x88BB0000;
	}
}
