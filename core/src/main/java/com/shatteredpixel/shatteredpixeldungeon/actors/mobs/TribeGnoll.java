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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TribeGnollSprite;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class TribeGnoll extends Mob {

    {
        spriteClass = TribeGnollSprite.class;

        HP = HT = 13;
        defenseSkill = 6;

        EXP = 2;
        maxLvl = 6;

        loot = RingOfWealth.genConsumableDrop(-3);
        lootChance = 1f;
        properties.add(Property.ANIMAL);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 2, 5 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 13;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(1, 2);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
//        if (Dungeon.mode == Dungeon.GameMode.DIFFICULT){
//            Buff.affect(enemy, Cripple.class, 3f);
//        }
        return super.attackProc(enemy, damage);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        if (Dungeon.level.adjacent( pos, enemy.pos )){
            return true;
        }

        if (Dungeon.level.distance( pos, enemy.pos ) <= 3){
            boolean[] passable = BArray.not(Dungeon.level.solid, null);

            for (Char ch : Actor.chars()) {
                //our own tile is always passable
                passable[ch.pos] = ch == this;
            }

            PathFinder.buildDistanceMap(enemy.pos, passable, 3);

            if (PathFinder.distance[pos] <= 3){
                return true;
            }
        }

        return super.canAttack(enemy);
    }
}
