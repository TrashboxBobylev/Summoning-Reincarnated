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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class EctoCharge extends Buff {
    public static final float MAXIMUM = 2.5f;
    public static final int RATE      = 40;

    {
        type = buffType.NEGATIVE;
        actPriority = BUFF_PRIO - 10; // after all other buffs
    }

    int level = 0;

    @Override
    public int icon() {
        return BuffIndicator.INVISIBLE;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0x35adff);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(level);
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0, level / MAXIMUM*RATE);
    }

    public void increment(){
        level += RATE;
        target.sprite.showStatusWithIcon(CharSprite.NEGATIVE, "+" + RATE, FloatingText.KARMA);
        if (level > MAXIMUM*RATE){
            target.damage(999, this);
            Badges.validateDeathFromGrimOrDisintTrap();
            Dungeon.fail( this );
            GLog.n( Messages.get(this, "ondeath") );
        }
    }

    @Override
    public boolean act() {
        if (level > 0){
            target.sprite.showStatusWithIcon(CharSprite.POSITIVE, "-1", FloatingText.KARMA);
            if (--level <= 0){
                detach();
            }
            spend(TICK);
        } else {
            detach();
        }
        return true;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", level, Math.round(MAXIMUM*RATE));
    }

    private static final String LEVEL	= "level";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( LEVEL, level );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        level = bundle.getInt( LEVEL );
    }
}
