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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.Emitter;

public class CrowSprite extends MinionSprite {
    public CrowSprite() {
        super();

        texture( Assets.Sprites.CROW );

        TextureFilm frames = new TextureFilm( texture, 15, 15 );

        int c = 0;

        idle = new Animation( 6, true );
        idle.frames( frames, 0, 1 );

        run = new Animation( 8, true );
        run.frames( frames, 0, 1 );

        attack = new Animation( 12, false );
        attack.frames( frames, 2, 3, 0, 1 );

        die = new Animation( 12, false );
        die.frames( frames, 4, 5, 6 );

        play( idle );
    }

    private Emitter smoke;

    @Override
    public void link( Char ch ) {
        super.link( ch );
        renderShadow = false;

        if (smoke == null) {
            smoke = emitter();
            smoke.pour( CityLevel.Smoke.factory, 0.35f );
        }
    }

    @Override
    public void update() {

        super.update();

        if (smoke != null) {
            smoke.visible = visible;
        }
    }

    @Override
    public void kill() {
        super.kill();

        if (smoke != null) {
            smoke.on = false;
        }
    }
}
