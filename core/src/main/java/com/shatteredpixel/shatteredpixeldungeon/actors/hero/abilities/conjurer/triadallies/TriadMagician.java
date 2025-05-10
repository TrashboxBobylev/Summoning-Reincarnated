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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.conjurer.triadallies;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;

public class TriadMagician extends BaseTriadAlly {

    {
        spriteClass = Sprite.class;
    }

    @Override
    public int baseHP() {
        return 60;
    }

    // does not deal any damage
    @Override
    public int baseDamageRoll() {
        return -1;
    }

    public static class Sprite extends BaseSprite {

        @Override
        HeroClass heroClass() {
            return HeroClass.MAGE;
        }

        @Override
        int heroTier() {
            return 3;
        }

        @Override
        void tintSprite() {
            tint(0x15b3f2, 0.75f);
        }
    }
}
