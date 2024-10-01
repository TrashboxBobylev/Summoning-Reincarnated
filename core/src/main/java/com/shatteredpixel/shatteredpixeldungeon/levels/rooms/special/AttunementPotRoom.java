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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfAttunement;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class AttunementPotRoom extends SpecialRoom {
    @Override
    public int minWidth() { return 7; }
    public int minHeight() { return 7; }

    @Override
    public void paint(Level level) {
        Painter.fill( level, this, Terrain.WALL );
        Painter.fill( level, this, 1, Terrain.BOOKSHELF );

        Point c = center();
        Door door = entrance();
        if (door.x == left || door.x == right) {
            if (door.y == c.y) c.y += Random.Int(2) == 0 ? -1 : +1;
            Point p = Painter.drawInside( level, this, door, Math.abs( door.x - c.x ) - 2, Terrain.EMPTY_SP );
            for (; p.y != c.y; p.y += p.y < c.y ? +1 : -1) {
                Painter.set( level, p, Terrain.EMPTY_SP );
            }
        } else {
            if (door.x == c.x) c.x += Random.Int(2) == 0 ? -1 : +1;
            Point p = Painter.drawInside( level, this, door, Math.abs( door.y - c.y ) - 2, Terrain.EMPTY_SP );
            for (; p.x != c.x; p.x += p.x < c.x ? +1 : -1) {
                Painter.set( level, p, Terrain.EMPTY_SP );
            }
        }

        ElixirMarker vis = new ElixirMarker();
        vis.pos(c.x - 1, c.y - 1);

        level.customTiles.add(vis);

        Painter.fill(level, c.x-1, c.y-1, 3, 3, Terrain.CUSTOM_DECO_EMPTY);

        level.drop( new ElixirOfAttunement(), level.pointToCell(c) );

        door.set( Door.Type.LOCKED );
        level.addItemToSpawn( new IronKey( Dungeon.depth ) );
    }

    public static class ElixirMarker extends CustomTilemap {

        {
            texture = Assets.Environment.ATTUNEMENT_ROOM;

            tileW = tileH = 3;
        }

        final int TEX_WIDTH = 48;

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            v.map(mapSimpleImage(0, 0, TEX_WIDTH), 3);
            return v;
        }

        @Override
        public String name(int tileX, int tileY) {
            if (tileX == 1 && tileY == 1)
                return Messages.get(this, "pedestal_name");
            return Messages.get(this, "sp_name");
        }

        @Override
        public String desc(int tileX, int tileY) {
            if (tileX == 1 && tileY == 1)
                return Messages.get(this, "pedestal_desc");
            return Messages.get(this, "sp_desc");
        }
    }
}
