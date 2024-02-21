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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Minion extends Mob {
    {
        alignment = Alignment.ALLY;
        intelligentAlly = true;

        immunities.add(AllyBuff.class);
    }

    public Weapon.Enchantment enchantment;
    public Weapon.Augment augment = Weapon.Augment.NONE;
    public int rank;
    public int attunement;

    @Override
    public int attackProc(Char enemy, int damage) {
        if (enchantment != null && buff(MagicImmune.class) == null) {
            damage = enchantment.proc(  this, enemy, damage );
        }
        return super.attackProc(enemy, damage);
    }

    @Override
    public float attackDelay() {
        float delay = super.attackDelay();
        return augment.delayFactor(delay);
    }

    @Override
    public String name() {
        return enchantment != null ? enchantment.name( super.name() ) : super.name();
    }

    public static Char whatToFollow(Char follower, Char start) {
        Char toFollow = start;
        boolean[] passable = Dungeon.level.passable.clone();
        PathFinder.buildDistanceMap(follower.pos, passable, Integer.MAX_VALUE);//No limit on distance
        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
            if (mob.alignment == follower.alignment &&
                    PathFinder.distance[toFollow.pos] > PathFinder.distance[mob.pos] &&
                    mob.following(toFollow)) {
                toFollow = whatToFollow(follower, mob);
            }
            else {
                return start;
            }
        }
        return toFollow;
    }

    public void onLeaving(){}

    public class TargetBuff extends FlavourBuff {

    }

    public class Wandering extends Mob.Wandering implements AiState{

        @Override
        public boolean act( boolean enemyInFOV, boolean justAlerted ) {

            //Ensure there is direct line of sight from ally to enemy, and the distance is small. This is enforced so that allies don't end up trailing behind when following hero.
            if ( enemyInFOV ) {

                enemySeen = true;

                notice();
                alerted = true;

                state = HUNTING;
                target = enemy.pos;

            } else {

                enemySeen = false;
                Char toFollow = whatToFollow(Minion.this, Dungeon.hero);
                int oldPos = pos;
                target = toFollow.pos;
                //always move towards the target when wandering
                if (getCloser( target)) {
                    spend( 1 / speed() );
                    return moveSprite( oldPos, pos );
                } else {
                    //if it can't move closer to defending pos, then give up and defend current position
                    spend( TICK );
                }

            }
            return true;
        }

    }
}
