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

package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.utils.PointF;

public class AscendedWings extends Group {

    private Wing leftWing;
    private Wing rightWing;

    private CharSprite target;

    public AscendedWings(CharSprite target) {
        super();

        this.target = target;

        add(leftWing = new Wing(false));

        add(rightWing = new Wing(true));

        updateWings();
    }

    public void updateWings(){
        PointF origin = target.center();

        leftWing.point(new PointF(origin.x - leftWing.width, origin.y - leftWing.height*0.75f));
        rightWing.point(new PointF(origin.x, origin.y - rightWing.height*0.5f));
    }

    @Override
    public synchronized void update() {
        super.update();
        updateWings();
    }

    @Override
    public void draw() {
        Blending.setLightMode();
        super.draw();
        Blending.setNormalMode();
    }

    public static class Wing extends Image {

        public Wing(boolean right) {
            super(Effects.get(right ? Effects.Type.CONJURER_WING_RIGHT : Effects.Type.CONJURER_WING_LEFT));
            float interval = (Game.timeTotal % 9 ) /3f;
            tint(interval > 2 ? interval - 2 : Math.max(0, 1 - interval),
                    interval > 1 ? Math.max(0, 2-interval): interval,
                    interval > 2 ? Math.max(0, 3-interval): interval-1, 0.5f);
        }

        @Override
        public void update() {
            super.update();
            float interval = (Game.timeTotal % 9 ) /3f;
            tint(interval > 2 ? interval - 2 : Math.max(0, 1 - interval),
                    interval > 1 ? Math.max(0, 2-interval): interval,
                    interval > 2 ? Math.max(0, 3-interval): interval-1, 0.5f);
        }
    }
}
