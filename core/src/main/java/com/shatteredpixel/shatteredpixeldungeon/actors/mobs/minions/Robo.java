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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DM150Sprite;
import com.watabou.utils.Callback;

public class Robo extends Minion{
    {
        spriteClass = DM150Sprite.class;
        minDefense = 8;
        maxDefense = 22;

        properties.add(Property.INORGANIC);
    }

    @Override
    public float attackDelay() {
        float mod = 0;
        switch (rank){
            case 1: mod = 2f; break;
            case 2: mod = 1.5f; break;
            case 3: mod = 1f; break;
        }
        return super.attackDelay() * mod;
    }

    public float evasionModifier(){
        return 0.6f;
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        attemptToHeal(2 + damage / 5);
        return super.defenseProc(enemy, damage);
    }

    private void attemptToHeal(int healAmount) {
        if (rank == 3){
            if (HP < HT) {
                HP = Math.min(HT, HP + healAmount);
                sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(healAmount), FloatingText.HEALING);
                sprite.emitter().burst(Speck.factory(Speck.HEALING), Math.min(10, healAmount));
            }
        }
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        attemptToHeal(1 + damage / 4);
        return super.attackProc(enemy, damage);
    }

    @Override
    public float targetPriority() {
        if (rank == 3){
            return super.targetPriority()*5f;
        }
        return super.targetPriority()*2f;
    }

    @Override
    public int drRoll() {
        if (rank == 3){
            return super.drRoll() / 3;
        }
        return super.drRoll();
    }

    private void chain(int target){
        if (enemy.properties().contains(Property.IMMOVABLE))
            return;

        Ballistica chain = new Ballistica(pos, target, Ballistica.FRIENDLY_PROJECTILE);

        if (chain.collisionPos == enemy.pos
                && chain.path.size() >= 2
                && !Dungeon.level.pit[chain.path.get(1)]) {
                    int newPos = -1;
                    for (int i : chain.subPath(1, chain.dist)){
                        if (!Dungeon.level.solid[i] && Actor.findChar(i) == null){
                            newPos = i;
                            break;
                        }
                    }

            if (newPos != -1) {
                final int newPosFinal = newPos;
                this.target = newPos;
                Actor.addDelayed(new Pushing(enemy, enemy.pos, newPosFinal, new Callback(){
                    public void call() {
                        enemy.pos = newPosFinal;
                        Dungeon.level.occupyCell(enemy);
                        ((Mob)enemy).aggro(Robo.this);
                    }
                }), -1);
            }

        }
    }

    @Override
    public boolean canAttack(Char enemy) {
        if (isChainable(enemy))
            return new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE ).collisionPos == enemy.pos;
        else
            return super.canAttack(enemy);
    }

    private boolean isChainable(Char enemy) {
        return enemy.properties().contains(Property.RANGED) || rank == 2;
    }

    protected boolean doAttack(Char enemy ) {

        if (Dungeon.level.adjacent( pos, enemy.pos )) {

            return super.doAttack( enemy );

        } else if (isChainable(enemy)){

            if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                sprite.zap( enemy.pos );
                return false;
            } else {
                zap();
                return true;
            }
        }
        return true;
    }

    private void zap() {
        spend( 1f );
        chain(enemy.pos);
        next();
    }

    public void onZapComplete() {
        zap();
    }
}
