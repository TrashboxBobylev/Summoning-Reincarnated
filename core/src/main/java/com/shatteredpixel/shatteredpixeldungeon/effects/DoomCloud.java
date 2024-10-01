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

package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;

public class DoomCloud extends Image {

    private static final float TIME_TO_FADE = 18f;

    private float time;

    public DoomCloud() {
        super(Effects.get(Effects.Type.DOOM_CLOUD));
        origin.set(width / 2, height / 2);
        scale.x = 4f;
        scale.y = 4f;
    }

    public void reset(int p) {
        revive();

        x = (p % Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - width) / 2;
        y = (p / Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - height) / 2;

        time = TIME_TO_FADE;
    }

    public void reset(Visual v) {
        revive();

        point(v.center(this));

        time = TIME_TO_FADE;
    }

    @Override
    public void update() {
        super.update();

        if ((time -= Game.elapsed) <= 0) {
            kill();
        } else {
            float p = time / TIME_TO_FADE;
            alpha(p);
            scale.y = 1 + p/2;
        }
    }

    public static void hit(Char ch) {
        hit(ch, 0);
    }

    public static void hit(Char ch, float angle) {
        if (ch.sprite.parent != null) {
            DoomCloud s = (DoomCloud) ch.sprite.parent.recycle(DoomCloud.class);
            ch.sprite.parent.bringToFront(s);
            s.reset(ch.sprite);
            s.angle = angle;
        }
    }

    public static void hit(int pos) {
        hit(pos, 0);
    }

    public static void hit(int pos, float angle) {
        Group parent = Dungeon.hero.sprite.parent;
        DoomCloud s = (DoomCloud) parent.recycle(DoomCloud.class);
        parent.bringToFront(s);
        s.reset(pos);
        s.angle = angle;
    }
}
