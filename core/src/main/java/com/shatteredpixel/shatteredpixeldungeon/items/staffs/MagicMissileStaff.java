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

package com.shatteredpixel.shatteredpixeldungeon.items.staffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.stationary.MagicMissileMinion;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class MagicMissileStaff extends StationaryStaff {
    {
        image = ItemSpriteSheet.MAGIC_MISSILE_STAFF;
        minionType = MagicMissileMinion.class;
        tier = 3;
        table = new BalanceTable(
                50, 4, 15,
                65, 10, 30,
                25, 3, 12);
    }

    @Override
    public int maxActions(int rank) {
        switch (rank){
            case 1: return 25;
            case 2: return 25;
            case 3: return 3;
        }
        return super.maxActions(rank);
    }

    @Override
    public int getChargeTurns() {
        if (rank() == 3)
            return 75;
        return super.getChargeTurns();
    }
}