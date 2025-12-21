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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostBurn;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ElementalSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class FrostElemental extends Minion {
    {
        spriteClass = ElementalSprite.FrostAlly.class;

        flying = true;

        properties.add( Property.ICY );

        minDefense = 4;
        maxDefense = 15;
    }

    protected int rangedCooldown = Random.NormalIntRange( 3, 5 );

    @Override
    protected boolean canAttack( Char enemy ) {
        if (super.canAttack(enemy)){
            return true;
        } else {
            return rangedCooldown < 0 && new Ballistica( pos, enemy.pos, Ballistica.FRIENDLY_MAGIC ).collisionPos == enemy.pos;
        }
    }

    @Override
    public boolean add( Buff buff ) {
        if (buff instanceof Burning) {
            damage( Random.NormalIntRange( HT/2, HT * 3/5 ), (Burning)buff );
            return false;
        } else {
            return super.add( buff );
        }
    }

    @Override
    protected boolean act() {
        if (type == 2){
            rangedCooldown = Integer.MAX_VALUE;
        } else if (rangedCooldown == Integer.MAX_VALUE){
            rangedCooldown = Random.NormalIntRange( 3, 5 );
        }
        if (state == HUNTING){
            rangedCooldown--;
        }
        return super.act();
    }

    protected boolean doAttack(Char enemy ) {

        if (Dungeon.level.adjacent( pos, enemy.pos )
                || rangedCooldown > 0
                || new Ballistica( pos, enemy.pos, Ballistica.FRIENDLY_MAGIC ).collisionPos != enemy.pos) {

            return super.doAttack( enemy );

        } else {

            if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                sprite.zap( enemy.pos );
                return false;
            } else {
                zap();
                return true;
            }
        }
    }

    @Override
    public int attackProc( Char enemy, int damage ) {
        damage = super.attackProc( enemy, damage );
        if (type == 2) {
            Freezing.freeze(enemy.pos);
        } else if (type == 3) {
            Buff.affect(enemy, FrostBurn.class).reignite(enemy);
        }
        if (type == 2){
            damage += enemy.drRoll();
        }

        return damage;
    }

    @Override
    public float attackDelay() {
        if (type == 2){
            return super.attackDelay()/2f;
        }
        return super.attackDelay();
    }

    protected void zap() {
        spend( 1f );

        Invisibility.dispel(this);
        Char enemy = this.enemy;
        if (hit( this, enemy, true )) {

            if (type != 3) {
                Freezing.freeze(enemy.pos);
            } else {
                Buff.affect(enemy, FrostBurn.class).reignite(enemy);
            }

        } else {
            enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
        }

        rangedCooldown = Random.NormalIntRange( 3, 5 );
    }

    public void onZapComplete() {
        zap();
        next();
    }

    private static final String COOLDOWN = "cooldown";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( COOLDOWN, rangedCooldown );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        rangedCooldown = bundle.getInt( COOLDOWN );
    }
}
