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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.utils.Bundle;

public class AllyDamageTag extends FlavourBuff {
    {
        type = buffType.NEGATIVE;
    }

    private float mult;
    private int flat;

    public AllyDamageTag setMult(float mult){
        float oldMult = this.mult;
        this.mult = mult;
        if (oldMult != this.mult) {
            target.sprite.showStatus(CharSprite.NEUTRAL, target.alignment == Char.Alignment.ENEMY ? "+" : "-" + mult + "x", FloatingText.ALLY_TAG);
        }
        return this;
    }

    public AllyDamageTag setFlat(int flat) {
        int oldFlat = this.flat;
        this.flat = flat;
        if (oldFlat != this.flat) {
            target.sprite.showStatus(CharSprite.NEUTRAL, target.alignment == Char.Alignment.ENEMY ? "x" : "/" + flat, FloatingText.ALLY_TAG);
        }
        return this;
    }

    public int processDamage(int damage){
        if (flat > 0){
            damage += flat;
        }
        if (mult > 0){
            damage *= mult;
        }

        return damage;
    }

    public int processResistance(int damage){
        if (flat > 0){
            damage -= flat;
        }
        if (mult > 0){
            damage /= mult;
        }
        damage = Math.max(0, damage);

        return damage;
    }

    private static final String FLAT = "flat";
    private static final String MULT = "mult";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(FLAT, flat);
        bundle.put(MULT, mult);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        flat = bundle.getInt(FLAT);
        mult = bundle.getFloat(MULT);
    }
}
