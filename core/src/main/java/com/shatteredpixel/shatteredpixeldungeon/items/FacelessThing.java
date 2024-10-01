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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

public class FacelessThing extends Item {
    {
        image = ItemSpriteSheet.FACELESS_ITEM;

        stackable = true;
    }

    public int moneyValue = 0;
    public int energyValue = 0;

    public static FacelessThing init(Item item){
        return init(item.value(), item.energyVal());
    }

    public static FacelessThing init(int money, int energy){
        FacelessThing thing = new FacelessThing();
        thing.moneyValue = money;
        thing.energyValue = energy;
        return thing;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public int value() {
        return moneyValue;
    }

    @Override
    public int energyVal() {
        return energyValue;
    }

    @Override
    public boolean isSimilar(Item item) {
        if (item instanceof FacelessThing) {
            FacelessThing other = (FacelessThing) item;
            return super.isSimilar(item) && this.moneyValue == other.moneyValue && this.energyValue == other.energyValue;
        } else {
            return super.isSimilar(item);
        }
    }

    private static final String ENERGY = "energy";
    private static final String MONEY  = "money";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ENERGY, energyValue);
        bundle.put(MONEY, moneyValue);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        energyValue = bundle.getInt(ENERGY);
        moneyValue = bundle.getInt(MONEY);
    }
}
