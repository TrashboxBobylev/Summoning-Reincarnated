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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Cleaver extends MeleeWeapon {

    {
        image = ItemSpriteSheet.CLEAVER;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 0.75f;

        tier = 3;
        ACC = 0.8f;
        DLY = 1.5f;
    }

    @Override
    public int min(int lvl) {
        return  tier-2  //1 base, down from 3
                ;    //no level scaling
    }

    @Override
    public int max(int lvl) {
        return  5*(tier+3) +    //30 base, up from 20
                lvl*(tier+3);   //+6 per level, up from +4
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (defender.HP >= defender.HT) {
            attacker.sprite.emitter().burst( Speck.factory( Speck.STAR), 8 );
            damage *= 2;
        }
        return super.proc(attacker, defender, damage);
    }
}
