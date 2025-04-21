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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ConjurerSet;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class Attunement extends Buff{
    @Override
    public int icon() {
        return BuffIndicator.SOUL_REFUSAL;
    }

    {
        announced = true;
        type = buffType.POSITIVE;
    }

    public static float empowering(){
        float value = 1.0f;
        if (Dungeon.hero.belongings.armor instanceof ConjurerSet){
            switch (((ConjurerSet) Dungeon.hero.belongings.armor).rank()){
                case 1: default:
                    value = 1.5f; break;
                case 2:
                    value = 0f; break;
                case 3:
                    value = 4.5f; break;
            }
        }
        return 1f + (value) * ((Dungeon.hero.HT - Dungeon.hero.HP * 1f) / (Dungeon.hero.HT * 1f));
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", (int)((empowering() - 1f)*100));
    }

    @Override
    public String iconTextDisplay() {
        return (int) ((empowering() - 1f) * 100) + "%";
    }
}
