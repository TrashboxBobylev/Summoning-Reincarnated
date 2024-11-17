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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.generic.AttunementBooster;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class AttunementBoost extends FlavourBuff implements AttunementBooster {
    {
        type = buffType.POSITIVE;
    }

    private float boost;

    @Override
    public float boost() {
        return boost;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", boost, dispTurns());
    }

    @Override
    public int icon() {
        return BuffIndicator.SOUL_BUFF;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0x00FF00);
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0, (15 - visualcooldown()) / 15);
    }

    private static final String BOOST	    = "boost";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( BOOST, boost );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        boost = bundle.getInt( BOOST );
    }
}
