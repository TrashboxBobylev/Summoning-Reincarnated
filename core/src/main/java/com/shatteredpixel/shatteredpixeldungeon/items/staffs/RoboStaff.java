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

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Robo;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RoboStaff extends Staff {
    {
        image = ItemSpriteSheet.ROBO_STAFF;
        minionType = Robo.class;
        tier = 4;
        table = new BalanceTable(135, 20, 40,
                120, 15, 25,
                230, 5, 15);
    }

    @Override
    public Minion.BehaviorType defaultBehaviorType() {
        return Minion.BehaviorType.AGGRESSIVE;
    }

    @Override
    public int getChargeTurns() {
        switch (level()){
            case 0: return 600;
            case 1: return 800;
            case 2: return 1500;
        }
        return 0;
    }

    @Override
    public int getRegenerationTurns() {
        if (type() == 3){
            return super.getRegenerationTurns()*2;
        }
        return super.getRegenerationTurns();
    }
}
