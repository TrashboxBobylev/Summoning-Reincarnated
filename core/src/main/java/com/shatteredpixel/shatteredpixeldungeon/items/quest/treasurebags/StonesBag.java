/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Summoning Pixel Dungeon Reincarnated
 * Copyright (C) 2023-2026 Trashbox Bobylev
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

package com.shatteredpixel.shatteredpixeldungeon.items.quest.treasurebags;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDetectMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class StonesBag extends TreasureBag {
    {
        image = ItemSpriteSheet.BAG_STONES;
    }

    @Override
    protected ArrayList<Item> items() {
        ArrayList<Item> items = new ArrayList<>();
        int amount = 7;
        if (Dungeon.mode == Dungeon.GameMode.GAUNTLET)
            amount = 4;
        for(int i = 0; i < amount; i++) {
            Item stone;
            do {
                stone = Generator.random(Generator.Category.STONE).identify();
            } while (stone instanceof StoneOfDetectMagic || stone instanceof StoneOfIntuition);
            items.add(stone);
        }
        return items;
    }

    @Override
    public int value() {
        if (Dungeon.mode == Dungeon.GameMode.GAUNTLET)
            return 65 * quantity;
        return 25 * quantity;
    }
}
