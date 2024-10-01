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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;

public interface WeaponEnchantable {
    Weapon.Enchantment getEnchantment();
    void setEnchantment(Weapon.Enchantment enchantment);

    default WeaponEnchantable enchant(Weapon.Enchantment ench ){
        setEnchantment(ench);
        Item.updateQuickslot();
        return this;
    }

    default WeaponEnchantable enchant() {
        Class<? extends Weapon.Enchantment> oldEnchantment = getEnchantment() != null ? getEnchantment().getClass() : null;
        Weapon.Enchantment ench = Weapon.Enchantment.random( oldEnchantment );

        return enchant( ench );
    }

    default boolean hasEnchant(Class<? extends Weapon.Enchantment> type, Char owner){
        return getEnchantment() != null && getEnchantment().getClass() == type && owner.buff(MagicImmune.class) == null;
    }

    //these are not used to process specific enchant effects, so magic immune doesn't affect them
    default boolean hasGoodEnchant(){
        return getEnchantment() != null && !getEnchantment().curse();
    }

    default boolean hasCurseEnchant(){
        return getEnchantment() != null && getEnchantment().curse();
    }
}
