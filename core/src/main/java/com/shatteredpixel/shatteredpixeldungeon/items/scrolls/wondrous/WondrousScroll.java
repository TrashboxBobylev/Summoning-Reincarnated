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

import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror;

import java.util.LinkedHashMap;

//basically pre-0.6.5 spellbook effects, but as items
public abstract class WondrousScroll extends Scroll {

    public static final LinkedHashMap<Class<?extends Scroll>, Class<?extends WondrousScroll>> regToWon = new LinkedHashMap<>();
    public static final LinkedHashMap<Class<?extends WondrousScroll>, Class<?extends Scroll>> wonToReg = new LinkedHashMap<>();
    static {
        regToWon.put(ScrollOfRecharging.class, ScrollOfOvercharge.class);
        wonToReg.put(ScrollOfOvercharge.class, ScrollOfRecharging.class);

        regToWon.put(ScrollOfRage.class, ScrollOfRallying.class);
        wonToReg.put(ScrollOfRallying.class, ScrollOfRage.class);

        regToWon.put(ScrollOfMagicMapping.class, ScrollOfOmniscience.class);
        wonToReg.put(ScrollOfOmniscience.class, ScrollOfMagicMapping.class);

        regToWon.put(ScrollOfTerror.class, ScrollOfPetrification.class);
        wonToReg.put(ScrollOfPetrification.class, ScrollOfTerror.class);

        regToWon.put(ScrollOfTeleportation.class, ScrollOfWarp.class);
        wonToReg.put(ScrollOfWarp.class, ScrollOfTeleportation.class);

        regToWon.put(ScrollOfMirrorImage.class, ScrollOfShadowLegion.class);
        wonToReg.put(ScrollOfShadowLegion.class, ScrollOfMirrorImage.class);

        regToWon.put(ScrollOfLullaby.class, ScrollOfMorpheus.class);
        wonToReg.put(ScrollOfMorpheus.class, ScrollOfLullaby.class);

        regToWon.put(ScrollOfRemoveCurse.class, ScrollOfEnlightement.class);
        wonToReg.put(ScrollOfEnlightement.class, ScrollOfRemoveCurse.class);

        regToWon.put(ScrollOfIdentify.class, ScrollOfDiscovery.class);
        wonToReg.put(ScrollOfDiscovery.class, ScrollOfIdentify.class);


    }

    @Override
    public boolean isKnown() {
        return anonymous || (handler != null && handler.isKnown( wonToReg.get(this.getClass()) ));
    }

    @Override
    public void setKnown() {
        if (!isKnown()) {
            handler.know(wonToReg.get(this.getClass()));
            updateQuickslot();
        }
    }

    @Override
    public void reset() {
        super.reset();
        if (handler != null && handler.contains(wonToReg.get(this.getClass()))) {
            image = handler.image(wonToReg.get(this.getClass())) + 16*21;
            rune = handler.label(wonToReg.get(this.getClass()));
        }
    }
}
