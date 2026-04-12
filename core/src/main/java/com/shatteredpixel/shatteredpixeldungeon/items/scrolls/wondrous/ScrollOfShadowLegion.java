/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Summoning Pixel Dungeon Reincarnated
 * Copyright (C) 2023-2026 Trashbox Bobylev
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

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls.wondrous;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class ScrollOfShadowLegion extends WondrousScroll {
    {
        icon = ItemSpriteSheet.Icons.SCROLL_SHADOW_LEGION;
    }

    @Override
    public void doRead() {
        detach(curUser.belongings.backpack);
        //spawns 2 images right away, delays 3 of them, 5 total.
        new DelayedImageSpawner(5 - ScrollOfMirrorImage.spawnImages(curUser, 2), 1, 2).attachTo(curUser);

        setKnown();

        Sample.INSTANCE.play( Assets.Sounds.READ );
        Invisibility.dispel();

        readAnimation();
    }

    public static class DelayedImageSpawner extends Buff {

        public DelayedImageSpawner(){
            this(2, 2, 1);
        }

        public DelayedImageSpawner( int total, int perRound, float delay){
            super();
            totImages = total;
            imPerRound = perRound;
            this.delay = delay;
        }

        private int totImages;
        private int imPerRound;
        private float delay;

        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target)){
                spend(delay);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean act() {

            int spawned = ScrollOfMirrorImage.spawnImages((Hero)target,  Math.min(totImages, imPerRound));

            totImages -= spawned;

            if (totImages <0){
                detach();
            } else {
                spend( delay );
            }

            return true;
        }

        private static final String TOTAL = "images";
        private static final String PER_ROUND = "per_round";
        private static final String DELAY = "delay";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put( TOTAL, totImages );
            bundle.put( PER_ROUND, imPerRound );
            bundle.put( DELAY, delay );
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            totImages = bundle.getInt( TOTAL );
            imPerRound = bundle.getInt( PER_ROUND );
            delay = bundle.getFloat( DELAY );
        }
    }
}
