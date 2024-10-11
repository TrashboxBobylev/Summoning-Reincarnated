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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Point;

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
    public Minion.BehaviorType defaultBehaviorType() {
        return Minion.BehaviorType.AGGRESSIVE;
    }

    public Point minDefense(int rank){
        switch (rank){
            case 1: return new Point(2, 2);
            case 2: return new Point(1, 1);
            case 3: return new Point(0, 0);
        }
        return new Point();
    }

    public Point maxDefense(int rank){
        switch (rank){
            case 1: return new Point(6, 3);
            case 2: return new Point(4, 2);
            case 3: return new Point(0, 0);
        }
        return new Point();
    }

    @Override
    public void customizeMinion(Minion minion) {
        int difference = (Dungeon.hero != null ? Dungeon.hero.ATU() : 0) - minion.attunement;
        Point minDefense = minDefense(rank());
        Point maxDefense = maxDefense(rank());
        minion.minDefense = minDefense.x + minDefense.y*difference;
        minion.maxDefense = maxDefense.x + maxDefense.y*difference;
    }

    public String minionDescription(int rank){
        int difference = Math.max(0, (Dungeon.hero != null ? Dungeon.hero.ATU() : 0) - ATUReq());
        Point minDefense = minDefense(rank);
        Point maxDefense = maxDefense(rank);
        return Messages.get(this, "minion_desc" + rank, minDefense.x + minDefense.y*difference, maxDefense.x + maxDefense.y*difference);
    }
}
