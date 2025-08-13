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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyDamageTag;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Bolas extends MissileWeapon {
	
	{
		image = ItemSpriteSheet.BOLAS;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;
	}

    public float min(float lvl, int rank) {
        switch (rank){
            case 1: return 2 + lvl;
            case 2: return 1 + lvl/2f;
            case 3: return 2 + lvl;
        }
        return 0;
    }

    public float max(float lvl, int rank) {
        switch (rank){
            case 1: return 5 + lvl*2f;
            case 2: return 4 + lvl*2.25f;
            case 3: return 5 + lvl*1.75f;
        }
        return 0;
    }

    public float baseUses(float lvl, int rank){
        switch (rank){
            case 1: return 6 + lvl*1.5f;
            case 2: return 4 + lvl*1f;
            case 3: return 6 + lvl*1.5f;
        }
        return 1;
    }
	
	@Override
	public int proc( Char attacker, Char defender, int damage ) {
        int dmg = super.proc(attacker, defender, damage);
        if (rank() == 1){
            Buff.prolong( defender, Cripple.class, Cripple.DURATION );
        } else if (rank() == 2){
            for (Mob mob : Dungeon.level.mobs) {
                if (mob.paralysed <= 0
                        && Dungeon.level.distance(defender.pos, mob.pos) <= 4
                        && mob.alignment == Char.Alignment.ALLY) {
                    mob.beckon(defender.pos);
                    mob.aggro(defender);
                    Buff.affect(defender, Minion.UniversalTargeting.class, 4f);
                }
            }
            Buff.affect(defender, AllyDamageTag.class, 4f).setFlat(dmg);
        } else if (rank() == 3){
            Buff.prolong( defender, Vertigo.class, Cripple.DURATION );
            Buff.prolong( defender, Haste.class, Cripple.DURATION );
        }
        return dmg;
	}
}
