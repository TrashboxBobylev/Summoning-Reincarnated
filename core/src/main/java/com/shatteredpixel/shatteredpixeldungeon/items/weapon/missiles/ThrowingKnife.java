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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ThrowingKnife extends MissileWeapon {
	
	{
		image = ItemSpriteSheet.THROWING_KNIFE;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.2f;
		
		bones = false;
	}

    public float min(float lvl, int rank) {
        switch (rank){
            case 1: return 2 + lvl/2;
            case 2: return 4 + lvl;
            case 3: return 8 + lvl*2;
        }
        return 0;
    }

    public float max(float lvl, int rank) {
        switch (rank){
            case 1: return 6 + lvl*2f;
            case 2: return 10 + lvl*3.33f;
            case 3: return 16 + lvl*6.5f;
        }
        return 0;
    }

    public float baseUses(float lvl, int rank){
        switch (rank){
            case 1: return 6 + lvl*1.25f;
            case 2: return 1 + lvl*0.5f;
            case 3: return 3 + lvl*1f;
        }
        return 1;
    }

    @Override
    public float castDelay(Char user, int cell) {
        if (rank() == 3){
            return super.castDelay(user, cell)*2;
        }
        return super.castDelay(user, cell);
    }

    @Override
    protected void decrementDurability() {
        if (rank() == 2 && Dungeon.hero.attackTarget() != null && !((Mob)Dungeon.hero.attackTarget()).enemySeen){

        } else {
            super.decrementDurability();
        }
    }

    @Override
	public int damageRoll(Char owner) {
		if (owner instanceof Hero) {
			Hero hero = (Hero)owner;
			Char enemy = hero.attackTarget();
			if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
                if (rank() != 3) {
                    //deals 75% toward max to max on surprise, instead of min to max.
                    int diff = max() - min();
                    int damage = augment.damageFactor(Hero.heroDamageIntRange(
                            min() + Math.round(diff * 0.75f),
                            max()));
                    int exStr = hero.STR() - STRReq();
                    if (exStr > 0) {
                        damage += Hero.heroDamageIntRange(0, exStr);
                    }
                    return damage;
                } else {
                    //scales from 0 - 67% based on how low hp the enemy is
                    float maxChance = 0.67f;

                    //we defer logic using an actor here so we can know the true final damage
                    //see Char.damage
                    Buff.affect(enemy, Grim.GrimTracker.class).maxChance = maxChance;

                    if (enemy.buff(Grim.GrimTracker.class) != null){
                        enemy.buff(Grim.GrimTracker.class).qualifiesForBadge = true;
                    }
                }
			}
		}
		return super.damageRoll(owner);
	}
}
