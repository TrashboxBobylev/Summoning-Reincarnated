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

package com.shatteredpixel.shatteredpixeldungeon.items.staffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.FrostElemental;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class FrostElementalStaff extends Staff {
    {
        image = ItemSpriteSheet.FROST_ELEMENTAL_STAFF;
        minionType = FrostElemental.class;

        tier = 4;

        table = new BalanceTable(75, 15, 25,
                32, 8, 12,
                60, 12, 18);
    }

    @Override
    public int getChargeTurns() {
        if (type() == 2) {
            return 200;
        }
        return super.getChargeTurns();
    }
}
