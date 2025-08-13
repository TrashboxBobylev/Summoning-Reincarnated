/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ChickenSprite;

public class Chicken extends Minion {
    {
        spriteClass = ChickenSprite.class;

        properties.add(Property.ANIMAL);
    }

    @Override
    public void updateStats() {
        defenseSkill = (Dungeon.hero.lvl*2+8);
        HT = Math.round(5 * attunement);
        HP = Math.min(HP, HT);
        setDamage(
                Math.round(attunement),
                Math.round(attunement*3));
        enchantment = staff.enchantment;
        augment = staff.augment;
        rank = staff.rank();
        staff.customizeMinion(this);
    }

    @Override
    public float attackDelay() {
        return super.attackDelay()/2f;
    }

    @Override
    public float evasionModifier() {
        return 2f;
    }

    @Override
    public float speed() {
        return super.speed()*2f;
    }
}
