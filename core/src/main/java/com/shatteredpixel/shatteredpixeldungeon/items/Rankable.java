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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;

import java.util.Collections;

public interface Rankable {
    default String getRankMessage(int rank) {
        return Messages.get(this, "rank" + rank);
    }

    int rank();

    void rank(int rank);

    static int getRankColor(int rank){
        switch (rank){
            default: return 0xFFFFFF;

            case 1: return ItemSlot.RANK1;
            case 2: return ItemSlot.RANK2;
            case 3: return ItemSlot.RANK3;
        }
    }

    static String getRankString(int rank){
        return String.join("", Collections.nCopies(rank, "I"));
    }
}
