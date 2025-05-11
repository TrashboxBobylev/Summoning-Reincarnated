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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.conjurer.triadallies;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class TriadRanger extends BaseTriadAlly {

    {
        spriteClass = Sprite.class;

        properties.add(Property.IMMOVABLE);
    }

    @Override
    public int baseHP() {
        return 80;
    }

    @Override
    public int baseDamageRoll() {
        int acc = Dungeon.hero.attackSkill(this);
        return (int) Random.NormalFloat(acc*0.67f, acc);
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        return super.canAttack(enemy) || new Ballistica( pos, enemy.pos,
                Dungeon.hero.pointsInTalent(Talent.PRECISION_OF_RANGER) > 3 ? Ballistica.STOP_CHARS | Ballistica.STOP_TARGET | Ballistica.IGNORE_ALLY_CHARS :
                        Ballistica.FRIENDLY_PROJECTILE).collisionPos == enemy.pos;
    }

    @Override
    public int attackProc( Char enemy, int damage ) {
        damage = super.attackProc( enemy, damage );
        if (Dungeon.hero.hasTalent(Talent.PRECISION_OF_RANGER)) {
            Buff.affect( enemy, Cripple.class, 3 );
        }
        if (Dungeon.hero.pointsInTalent(Talent.PRECISION_OF_RANGER) > 1){
            Buff.affect( enemy, Vulnerable.class, 3);
        }

        return damage;
    }

    @Override
    protected boolean getCloser(int target) {
        return false;
    }

    @Override
    protected boolean getFurther(int target) {
        return false;
    }

    public static class Sprite extends BaseSprite {
        @Override
        HeroClass heroClass() {
            return HeroClass.HUNTRESS;
        }

        @Override
        int heroTier() {
            return 1;
        }

        @Override
        void tintSprite() {
            tint(0x82ca9c, 0.75f);
        }

        private int cellToAttack;

        @Override
        public void attack( int cell ) {
            cellToAttack = cell;
            zap(cell);
        }

        @Override
        public void onComplete( Animation anim ) {
            if (anim == zap) {

                idle();

                MissileSprite missileSprite = (MissileSprite) parent.recycle(MissileSprite.class);
                missileSprite.
                        reset(this, cellToAttack, new RangedShot(), new Callback() {
                            @Override
                            public void call() {
                                ch.onAttackComplete();
                            }
                        });
                missileSprite.hardlight(0x106f93);
            } else {
                super.onComplete( anim );
            }
        }

        public class RangedShot extends Item {
            {
                image = ItemSpriteSheet.JAVELIN;
            }
        }
    }
}
