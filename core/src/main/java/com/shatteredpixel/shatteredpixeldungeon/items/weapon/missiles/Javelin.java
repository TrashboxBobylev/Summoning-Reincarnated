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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Javelin extends MissileWeapon {

	{
		image = ItemSpriteSheet.JAVELIN;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1f;
	}

    public float min(float lvl, int type) {
        switch (type){
            case 1: return 4 + lvl;
            case 2: return 3 + lvl*0.67f;
            case 3: return 8 + lvl*2;
        }
        return 0;
    }

    public float max(float lvl, int type) {
        switch (type){
            case 1: return 10 + lvl*4.5f;
            case 2: return 7 + lvl*3;
            case 3: return 20 + lvl*6;
        }
        return 0;
    }

    public float baseUses(float lvl, int type){
        switch (type){
            case 1: return 8 + lvl*1.75f;
            case 2: return 6 + lvl*1.5f;
            case 3: return 3 + lvl*0.5f;
        }
        return 1;
    }

    @Override
    public float accuracyFactor(Char owner, Char target) {
        float accuracyFactor = super.accuracyFactor(owner, target);
        if (type() == 3){
            accuracyFactor *= 2f;
        }
        return accuracyFactor;
    }

    @Override
    public float castDelay(Char user, int cell) {
        float delay = super.castDelay(user, cell);
        if (type() == 3){
            delay *= 3f;
        }
        return delay;
    }

    @Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            Hero hero = (Hero)owner;
            Char enemy = hero.attackTarget();
            if (type() == 2 && Char.hasProp(enemy, Char.Property.ANIMAL)) {
                return super.damageRoll(owner)*2;
            }
            if (type() == 3 && enemy != null){
                //as distance increases so does damage, capping at 3x:
                //1.20x|1.35x|1.52x|1.71x|1.92x|2.16x|2.43x|2.74x|3.00x
                int distance = Dungeon.level.distance(owner.pos, enemy.pos) - 1;
                float multiplier = Math.min(3f, 1.2f * (float)Math.pow(1.125f, distance));
                return (int) (super.damageRoll(owner) * multiplier);
            }
        }
        return super.damageRoll(owner);
    }
}
