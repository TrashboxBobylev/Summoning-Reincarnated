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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SheepTankSprite;
import com.watabou.utils.Random;

public class Sheep extends Minion {
    {
        spriteClass = SheepTankSprite.class;

        properties.add(Property.ANIMAL);
    }

    @Override
    protected boolean act() {
        if (rank == 3){
            Buff.affect(this, StoneOfAggression.Aggression.class, 2f);
        }
        return super.act();
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        if (rank == 2){
            int effectiveDamage = Random.NormalIntRange(0, (int) Math.floor(damage*3/4f));

            if (enemy.sprite != null) {
                hitSound(Random.Float(0.87f, 1.15f));
                enemy.sprite.bloodBurstA(sprite.center(), effectiveDamage);
                enemy.sprite.flash();
            }
        }
        return super.defenseProc(enemy, damage);
    }

    @Override
    public float targetPriority() {
        return super.targetPriority()*2f;
    }
}
