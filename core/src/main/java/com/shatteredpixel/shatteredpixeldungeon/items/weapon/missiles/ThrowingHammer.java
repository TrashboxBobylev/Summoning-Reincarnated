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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ThrowingHammer extends MissileWeapon {
	
	{
		image = ItemSpriteSheet.THROWING_HAMMER;
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 0.8f;

		sticky = false;
	}

    @Override
    public float castDelay(Char user, int cell) {
        float delay = super.castDelay(user, cell);
        if (rank() == 3) {
            delay /= 3f;
        }
        return delay;
    }

    public float min(float lvl, int rank) {
        switch (rank){
            case 1: return 4 + lvl;
            case 2: return 1 + lvl*0.4f;
            case 3: return 0 + lvl*0.2f;
        }
        return 0;
    }

    public float max(float lvl, int rank) {
        switch (rank){
            case 1: return 8 + lvl*3.5f;
            case 2: return 5 + lvl*2.5f;
            case 3: return 3 + lvl*1.75f;
        }
        return 0;
    }

    public float baseUses(float lvl, int rank){
        switch (rank){
            case 1: return 12 + lvl*2.5f;
            case 2: return 16 + lvl*3.25f;
            case 3: return 20 + lvl*4f;
        }
        return 1;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (rank() == 2){
            Buff.affect(defender, Daze.class, 5f);
        }
        if (rank() == 3){
            Buff.prolong(defender, Paralysis.class, 2f);
            damage += defender.drRoll();
        }
        return super.proc(attacker, defender, damage);
    }
}
