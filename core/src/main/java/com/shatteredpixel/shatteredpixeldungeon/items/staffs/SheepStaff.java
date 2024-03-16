/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
 *
 * Summoning Pixel Dungeon Reincarnated
 * Copyright (C) 2023-2024 Trashbox Bobylev
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

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.GrayRat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class SheepStaff extends Staff {
    {
        image = ItemSpriteSheet.WOOLY_STAFF;
        minionType = Sheep.class;
        tier = 2;
        chargeTurns = 575;
        table = new BalanceTable(
                55, 14, 0, 0, 1, 1,
                45, 12, 0, 0, 1, 1,
                93, 24, 0, 0, 1, 1);
    }

    @Override
    public int getChargeTurns() {
        if (rank() == 3){
            return Math.round(super.getChargeTurns()*1.5f);
        }
        return super.getChargeTurns();
    }

    @Override
    public int getRegenerationTurns() {
        if (rank() == 3){
            return Math.round(super.getRegenerationTurns()*1.5f);
        }
        return super.getRegenerationTurns();
    }

    @Override
    public Minion.BehaviorType defaultBehaviorType() {
        return Minion.BehaviorType.AGGRESSIVE;
    }

    @Override
    public void customizeMinion(Minion minion) {
        switch (rank()){
            case 1: minion.minDefense = 2; minion.maxDefense = 6; break;
            case 2: minion.minDefense = 1; minion.maxDefense = 4; break;
            case 3: minion.minDefense = 0; minion.maxDefense = 0; break;
        }
    }
}
