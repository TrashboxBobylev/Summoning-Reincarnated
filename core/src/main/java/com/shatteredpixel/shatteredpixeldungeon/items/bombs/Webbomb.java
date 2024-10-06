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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WebParticle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;

public class Webbomb extends Bomb {

    {
        image = ItemSpriteSheet.WEB_BOMB;
        harmless = true;
        fuseDelay = 0;
    }

    @Override
    public void explode(int cell) {
        super.explode(cell);

        PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), 3 );
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                if (Dungeon.level.pit[i])
                    GameScene.add(Blob.seed(i, Math.round(5/**Bomb.nuclearBoost()*/), Web.class));
                else
                    GameScene.add(Blob.seed(i, Math.round(30/**Bomb.nuclearBoost()*/), Web.class));
                if (Actor.findChar(i) != null){
                    //66% of poison dart's damage
                    Buff.affect(Actor.findChar(i), Poison.class).extend(5 + Math.round(Dungeon.scalingDepth()*0.4356f));
                }
                CellEmitter.get(i).burst(WebParticle.FACTORY, 3);
            }
        }
        Sample.INSTANCE.play(Assets.Sounds.PUFF);
    }

    @Override
    public int value() {
        //prices of ingredients
        return quantity * (35 + 40);
    }
}
