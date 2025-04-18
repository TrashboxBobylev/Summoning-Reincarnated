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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MagicMissileSprite;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.Game;
import com.watabou.noosa.Halo;
import com.watabou.utils.PointF;

public class MagicMissileMinion extends StationaryMinion {
    {
        spriteClass = MagicMissileSprite.class;
        properties.add(Property.RANGED);
        maxDefense = 12;
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        return new Ballistica( pos, enemy.pos, Ballistica.FRIENDLY_MAGIC).collisionPos == enemy.pos;
    }


    public void onZapComplete() {
        zap();
        next();
    }

    @Override
    protected boolean doAttack(Char enemy) {
        boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
        if (visible) {
            sprite.zap( enemy.pos );
        } else {
            zap();
        }

        return !visible;
    }

    public void zap() {
        spend(1f);
        int accuracyMod = 2;
        if (rank == 3) accuracyMod = INFINITE_ACCURACY / 10;
        if (rank == 2) accuracyMod = 1;

        if (hit(this, enemy, accuracyMod, rank != 2)) {
            int dmg = damageRoll();
            if (rank == 2)
                dmg -= enemy.drRoll()*2;
            enemy.damage(dmg, this);
            EffectHalo shield = new EffectHalo(enemy.sprite);
            GameScene.effect(shield);
            shield.putOut();
            useResource(1);
            if (rank == 3){
                for (Wand.Charger c : Dungeon.hero.buffs(Wand.Charger.class)){
                    c.gainCharge(1.0f / 3.0f);
                }
            }
        } else {
            enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
        }
    }

    public static class EffectHalo extends Halo {

        private CharSprite target;

        private float phase;

        public EffectHalo( CharSprite sprite ) {

            //rectangular sprite to circular radius. Pythagorean theorem
            super( (float)Math.sqrt(Math.pow(sprite.width()/3f, 2) + Math.pow(sprite.height()/3f, 2)), 0xBBAACC, 1f );

            am = -0.33f;
            aa = +0.33f;

            target = sprite;

            phase = 1;
        }

        @Override
        public void update() {
            super.update();

            if (phase < 1) {
                if ((phase -= Game.elapsed) <= 0) {
                    killAndErase();
                } else {
                    scale.set( (2 - phase) * radius / RADIUS );
                    am = phase * (-1);
                    aa = phase * (+1);
                }
            }

            if (visible = target.visible) {
                PointF p = target.center();
                point( p.x, p.y );
            }
        }

        @Override
        public void draw() {
            Blending.setLightMode();
            super.draw();
            Blending.setNormalMode();
        }

        public void putOut() {
            phase = 0.999f;
        }

    }
}
