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

package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.EffectTarget;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.BArray;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

import java.util.HashSet;

public class ChaoticBomb extends Bomb {

    {
        image = ItemSpriteSheet.CHAOS_BOMB;
        fuseDelay = 1;
        harmless = true;
    }

    @Override
    protected int explosionRange() {
        return 2;
    }

    @Override
    public void explode(int cell) {
        super.explode(cell);

        final HashSet<Callback> callbacks = new HashSet<>();
        final EffectTarget effectTarget = new EffectTarget(cell){
            {
                actPriority = HERO_PRIO+1;
            }
        };

        Dungeon.hero.busy();

        PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), explosionRange() );
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                Char ch = Actor.findChar(i);
                if (ch != null){
                    final Ballistica bolt = new Ballistica(cell, ch.pos, Ballistica.STOP_TARGET);
                    final CursedWand.CursedEffect effect = CursedWand.randomValidEffect(ChaoticBomb.this, effectTarget, bolt, false);
                    Callback callback = new Callback() {
                        @Override
                        public void call() {
                            effect.effect(ChaoticBomb.this, effectTarget, bolt, false);
                            callbacks.remove(this);
                            if (callbacks.isEmpty()) {
                                Dungeon.hero.spendAndNext(Dungeon.hero.cooldown());
                            }
                        }
                    };
                    effect.FX(ChaoticBomb.this, effectTarget, bolt, callback);
                    callbacks.add(callback);
                }
            }
        }
    }
}
