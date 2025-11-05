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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.stationary;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.damagesource.DamageProperty;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.damagesource.DamageSource;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlasterSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Random;

import java.util.EnumSet;

public class GasterBlaster extends StationaryMinion {
    {
        spriteClass = BlasterSprite.class;
        maxDefense = 12;
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        Ballistica ballistica = new Ballistica(pos, enemy.pos, Ballistica.STOP_SOLID);
        if (ballistica.subPath(1, ballistica.dist).contains(enemy.pos) && (enemy.buffs(Karma.class).isEmpty() || rank != 3)) return true;
        return false;
    }

    @Override
    public boolean canBeIgnored(Char ch){
        if (rank == 3 && !ch.buffs(Karma.class).isEmpty())
            return true;
        else
            return super.canBeIgnored(ch);
    }

    @Override
    public float attackDelay() {
        float mod = 0;
        switch (rank){
            case 1: mod = 2f; break;
            case 2: mod = 1/3f; break;
            case 3: mod = 2.5f; break;
        }
        float v = super.attackDelay() * mod;
//        if (buff(MagicPower.class) != null) v *= 1.75;
        return v;
    }

    @Override
    protected boolean doAttack(Char enemy) {
        spend(attackDelay());
        boolean rayVisible = false;
        Ballistica ballistica = new Ballistica(pos, enemy.pos, Ballistica.STOP_SOLID);
        for (int c : ballistica.subPath(1, Integer.MAX_VALUE)) {
            if (Dungeon.level.heroFOV[c]) rayVisible = true;
        }
        ((BlasterSprite)sprite).hit(2.0f / attackDelay());

        if (rayVisible){
            sprite.attack(ballistica.collisionPos);
        } else {
            attock(enemy.pos);
        }
        return !rayVisible;
    }


    public void attock(int posision) {
        Ballistica ballistica = new Ballistica(pos, posision, Ballistica.STOP_SOLID);
        boolean hit = false;
        int hitAmount = 0;
        for (int c : ballistica.subPath(1, ballistica.dist)) {
            if (!hit) hit = true;
            Char ch = Actor.findChar(c);
            if (ch == null || ch.alignment == Alignment.ALLY || ch instanceof Hero) continue;
            if (hit(this, ch, true)){
                hitAmount++;
                int damage = damageRoll();
                if (rank == 2 && hitAmount > 1){
                    damage *= 1.25f * (hitAmount-1);
                }
//                if (buff(MagicPower.class) != null) damage *= Random.NormalFloat(1.5f, 3.4f);
                ch.damage(damage, (rank == 3 ? new Karma() : this));
                if (rank == 3)
                    Buff.affect(ch, Karma.class);

                if (Dungeon.level.heroFOV[ch.pos]){
                    ch.sprite.flash();
                    CellEmitter.center(ch.pos).burst(MagicMissile.WhiteParticle.FACTORY, Random.NormalIntRange(3, 8));
                }
            } else {
                ch.sprite.showStatus(CharSprite.NEUTRAL, ch.defenseVerb());
            }
        }
        if (hit)
            useResource(1);
    }

    @Override
    public EnumSet<DamageProperty> initDmgProperties() {
        EnumSet<DamageProperty> damageProperties = super.initDmgProperties();
        damageProperties.remove(DamageProperty.PHYSICAL);
        damageProperties.add(DamageProperty.MAGICAL);
        return damageProperties;
    }

    public static class Karma extends Buff implements DamageSource {
        {
            type = buffType.NEGATIVE;
            announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.KARMA;
        }

        @Override
        public boolean act() {
            spend(Actor.TICK);
            return true;
        }
    }
}
