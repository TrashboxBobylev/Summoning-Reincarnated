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

package com.shatteredpixel.shatteredpixeldungeon.items.magic;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.GonerField;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class SubNullFieldLighter extends ConjurerSpell {

    {
        image = ItemSpriteSheet.GONER;
    }

    @Override
    public void effect(Ballistica trajectory) {
        if (Dungeon.level.heroFOV[trajectory.collisionPos]) {
            Sample.INSTANCE.play( Assets.Sounds.SHATTER );
            Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
        }
        ArrayList<Integer> cells = new ArrayList<>();
        PathFinder.buildDistanceMap( trajectory.collisionPos, BArray.not( Dungeon.level.solid, null ), 3 );
            for (int i = 0; i < PathFinder.distance.length; i++) {
                if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                    cells.add(i);
            }
        }
        for (int i: cells)
            GameScene.add(Blob.seed(i, resource(rank()) / cells.size(), GonerField.class));
    }

    @Override
    public int manaCost(int rank) {
        switch (rank){
            case 1: return 20;
            case 2: return 30;
            case 3: return 40;
        }
        return 0;
    }

    public int resource(int rank) {
        switch (rank){
            case 1: return 250;
            case 2: return 333;
            case 3: return 425;
        }
        return 0;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", resource(rank()), manaCost());
    }

    @Override
    public String spellRankMessage(int rank) {
        return Messages.get(this, "rank", resource(rank));
    }
}
