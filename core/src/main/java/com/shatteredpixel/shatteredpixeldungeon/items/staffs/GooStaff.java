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

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.GooMinion;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class GooStaff extends Staff {

    {
        image = ItemSpriteSheet.GOO_STAFF;
        minionType = GooMinion.class;
        tier = 5;
        table = new BalanceTable(65, 20, 35,
                80, 12, 25,
                25, 4, 15);
    }

    @Override
    public int getChargeTurns() {
        if (rank() == 3) {
            return super.getChargeTurns() / 2;
        }
        return super.getChargeTurns();
    }

    @Override
    public Minion.BehaviorType defaultBehaviorType() {
        if (rank() == 3)
            return Minion.BehaviorType.AGGRESSIVE;
        else
            return super.defaultBehaviorType();
    }

    private final Minion.BehaviorType[] behaviorTypes = {Minion.BehaviorType.AGGRESSIVE};

    @Override
    public Minion.BehaviorType[] availableBehaviorTypes() {
        if (rank() == 3)
            return behaviorTypes;
        else
            return super.availableBehaviorTypes();
    }
}
