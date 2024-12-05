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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FroggitSprite;
import com.watabou.utils.Bundle;

public class Froggit extends Minion {
    {
        spriteClass = FroggitSprite.class;
        maxDefense = 1;
    }

    int counter;

    @Override
    public float attackDelay() {
        float attackMod = 1f;
        if (rank == 2)
            attackMod = 0.25f;
        return super.attackDelay()*attackMod;
    }

    @Override
    public float speed() {
        float speedMod = 1f;
        if (rank == 3)
            speedMod = 4f;
        return super.speed()*speedMod;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        if (rank == 2)
            damage += enemy.drRoll();
        if (rank == 3){
            if (++counter == 4){
                counter = 0;
                Dungeon.hero.mana = Math.min(Dungeon.hero.mana + 1, Dungeon.hero.maxMana());
                Dungeon.hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(1), FloatingText.MANA);
            }
        }
        return super.attackProc(enemy, damage);
    }

    private static final String COUNTER = "counter";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(COUNTER, counter);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        counter = bundle.getInt(COUNTER);
    }

    @Override
    public float targetPriority() {
        if (rank == 3)
            return super.targetPriority()*0.5f;
        return super.targetPriority();
    }
}
