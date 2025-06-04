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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyDamageTag;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CrowSprite;
import com.watabou.utils.Bundle;

public class Crow extends Minion {
    {
        spriteClass = CrowSprite.class;
        minDefense = 1;
        maxDefense = 2;

        WANDERING = new Wandering();

        flying = true;
    }

    @Override
    public float speed() {
        return super.speed()*2f;
    }

    @Override
    protected boolean canAttack(Char enemy) {
        if (rank == 1){
            return super.canAttack(enemy) && enemy.buff(AllyDamageTag.class) == null;
        }
        if (rank == 2){
            return false;
        }
        return super.canAttack(enemy);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        if (rank == 1){
            Buff.affect(enemy, AllyDamageTag.class, 5f).setMult(1.25f);
        }
        if (rank == 3){
            WandOfCorruption corruption = new WandOfCorruption();
            corruption.level(3 + Math.max(0, (int)(attunement - Dungeon.hero.ATU())));
            corruption.onZap(new Ballistica(pos, enemy.pos, Ballistica.STOP_TARGET));
            if (enemy.sprite != null)
                enemy.sprite.emitter().burst(ShadowParticle.CURSE, 20);
        }
        return super.attackProc(enemy, damage);
    }

    @Override
    public float evasionModifier() {
        switch (rank){
            case 1:
                return 3f;
            case 2:
                return 5f;
            case 3:
                return 2.5f;
        }
        return super.evasionModifier();
    }

    @Override
    protected boolean act() {
        if (rank == 2) {
            if (fieldOfView != null)
                Dungeon.level.updateFieldOfView(this, fieldOfView);
            GameScene.updateFog(pos, viewDistance + (int) Math.ceil(speed()));
        }
        return super.act();
    }

    @Override
    public void destroy() {
        super.destroy();
        Dungeon.observe();
        GameScene.updateFog();
    }

    // makes it a directable ally, sorta
    protected int defendingPos = -1;

    public void defendPos( int cell ){
        defendingPos = cell;
        aggro(null);
        state = WANDERING;
    }

    public void clearDefensingPos(){
        defendingPos = -1;
    }

    public void followHero(){
        defendingPos = -1;
        aggro(null);
        state = WANDERING;
    }

    public void directTocell( int cell ){
        if (!Dungeon.level.heroFOV[cell]
                || Actor.findChar(cell) == null
                || (Actor.findChar(cell) != Dungeon.hero && Actor.findChar(cell).alignment != Char.Alignment.ENEMY)){
            defendPos( cell );
            return;
        }

        if (Actor.findChar(cell) == Dungeon.hero){
            followHero();
        } else {
            clearDefensingPos();
        }
    }

    private static final String DEFEND_POS = "defend_pos";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DEFEND_POS, defendingPos);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(DEFEND_POS)) defendingPos = bundle.getInt(DEFEND_POS);
    }

    public class Wandering extends Mob.Wandering implements AiState{

        @Override
        public boolean act( boolean enemyInFOV, boolean justAlerted ) {

            //Ensure there is direct line of sight from ally to enemy, and the distance is small. This is enforced so that allies don't end up trailing behind when following hero.
            if ( enemyInFOV ) {

                enemySeen = true;

                notice();
                alerted = true;

                state = HUNTING;
                target = enemy.pos;

            } else {

                enemySeen = false;
                int oldPos = pos;
                target = defendingPos != -1 && rank == 2 ? defendingPos : Dungeon.hero.pos;
                //always move towards the target when wandering
                if (getCloser( target)) {
                    spend( 1 / speed() );
                    return moveSprite( oldPos, pos );
                } else {
                    //if it can't move closer to defending pos, then give up and defend current position
                    spend( TICK );
                }

            }
            return true;
        }

    }
}
