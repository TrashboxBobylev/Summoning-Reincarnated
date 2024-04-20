/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
 *
 * Summoning Pixel Dungeon Reincarnated
 * Copyright (C) 2023-2024 Trashbox Bobylev
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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Transmuting;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollTricksterSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class GnollHunter extends Minion {
    {
        spriteClass = GnollHunterSprite.class;
        maxDefense = 4;
//        independenceRange = 12;

        properties.add(Property.RANGED);
//        properties.add(Property.IGNORE_ARMOR);
//        properties.add(Property.ANIMAL);
    }

    //he is ranged minion
    @Override
    protected boolean canAttack( Char enemy ) {
        Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.FRIENDLY_PROJECTILE);
        return !Dungeon.level.adjacent( pos, enemy.pos ) && attack.collisionPos == enemy.pos;
    }

    //run away when getting closer
    @Override
    protected boolean getCloser( int target ) {
        if (state == HUNTING) {
            return enemySeen && getFurther( target );
        } else {
            return super.getCloser( target );
        }
    }

    @Override
    public float speed() {
        if (enemy != null && Dungeon.level.adjacent( pos, enemy.pos ))
            return super.speed()*2;
        return super.speed();
    }

    @Override
    public float attackDelay() {
        float delay = super.attackDelay();
        switch (rank){
            case 1: return delay * 1f;
            case 2: return delay / 3f;
            case 3: return delay * (buff(GnollSnipingCooldown.class) != null ? 1f : 2f);
        }
        return delay;
    }



    @Override
    public int damageRoll() {
        if (buff(GnollSnipingCooldown.class) != null)
            return super.damageRoll() / 5;
        return super.damageRoll();
    }

    @Override
    public int attackSkill(Char target) {
        if (rank == 3){
            return Math.round(super.attackSkill(target)*2.5f);
        }
        return super.attackSkill(target);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        if (rank == 3){
            if (buff(GnollSnipingCooldown.class) == null) {
                Talent.Cooldown.affectChar(this, GnollSnipingCooldown.class);
                Ballistica trajectory = new Ballistica(pos, enemy.pos, Ballistica.STOP_TARGET);
                trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
                WandOfBlastWave.throwChar(enemy,
                        trajectory,
                        2,
                        true,
                        true,
                        this);
                Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                Transmuting.show(this, new GnollShot(), new Dart());
            }
        }
        return super.attackProc(enemy, damage);
    }

    public static final int SNIPER_CD = 25;

    public static class GnollSnipingCooldown extends Talent.Cooldown {
        public float duration() {
            return SNIPER_CD;
        }
        public int icon() { return BuffIndicator.TIME; }
        public void tintIcon(Image icon) { icon.hardlight(0x812e09); }
    }

    public static class GnollShot extends Item {
        {
            image = ItemSpriteSheet.BLINDING_DART;
        }
    }

    public static class GnollHunterSprite extends GnollTricksterSprite {
        //blinding dart instead of paralytic
        @Override
        public void attack( int cell ) {
            if (!Dungeon.level.adjacent(cell, ch.pos)) {

                ((MissileSprite)parent.recycle( MissileSprite.class )).
                        reset( ch.pos, cell, new GnollShot(), new Callback() {
                            @Override
                            public void call() {
                                ch.onAttackComplete();
                            }
                        } );

                play( cast );
                turnTo( ch.pos , cell );

            } else {

                super.attack( cell );

            }
        }
    }
}
