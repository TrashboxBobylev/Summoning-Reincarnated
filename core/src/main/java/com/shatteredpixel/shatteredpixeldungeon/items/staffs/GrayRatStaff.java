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

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.GrayRat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class GrayRatStaff extends Staff {
    {
        image = ItemSpriteSheet.GREY_RAT_STAFF;
        minionType = GrayRat.class;
        tier = 2;
        table = new BalanceTable(
                35, 10, 3, 2, 14, 4,
                30, 7, 3, 1, 11, 3,
                10, 2, 1, 0, 8, 1);
    }

    @Override
    public void onSummoningMinion(Minion minion) {
        if (type() == 3){
            Buff.affect(minion, Adrenaline.class, 7.5f);
        }
    }

    @Override
    public int getChargeTurns() {
        if (type() == 3){
            return 100;
        }
        return super.getChargeTurns();
    }
}
