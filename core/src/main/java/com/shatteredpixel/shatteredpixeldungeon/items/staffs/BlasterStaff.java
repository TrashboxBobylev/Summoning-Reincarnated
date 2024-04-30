/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2022 Evan Debenham
 *
 * Summoning Pixel Dungeon
 * Copyright (C) 2019-2022 TrashboxBobylev
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

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.stationary.GasterBlaster;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class BlasterStaff extends StationaryStaff {
    {
        image = ItemSpriteSheet.GASTER_STAFF;
        minionType = GasterBlaster.class;
        tier = 5;
        table = new BalanceTable(85, 20, 50,
                75, 6, 14,
                100, 30, 75);
    }

    @Override
    public int getChargeTurns() {
        if (rank == 3)
            return 700;
        return super.getChargeTurns();
    }

    @Override
    public int maxActions(int rank) {
        switch (rank){
            case 1: return 18;
            case 2: return 50;
            case 3: return 12;
        }
        return super.maxActions(rank);
    }
}
