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

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Wizard;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class WizardStaff extends Staff{
    {
        image = ItemSpriteSheet.WIZARD_STAFF;
        minionType = Wizard.class;
        tier = 4;
        table = new BalanceTable(50, 7, 17,
                40, 2, 6,
                45, 0, 0);
        chargeTurns = 500;
    }

    @Override
    public int getChargeTurns() {
        switch (type){
            case 1: return 600;
            case 2: return 800;
            case 3: return 750;
        }
        return 0;
    }
}
