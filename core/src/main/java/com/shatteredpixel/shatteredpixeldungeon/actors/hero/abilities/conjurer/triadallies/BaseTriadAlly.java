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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Bundle;

public abstract class BaseTriadAlly extends Mob {

    {
        spriteClass = BaseSprite.class;

        alignment = Alignment.ALLY;

        properties.add(Property.INORGANIC);

        immunities.add(AllyBuff.class);

        actPriority = MOB_PRIO + 1;

        WANDERING = new Wandering();
        state = WANDERING;

        updateHP();
    }

    public abstract int baseHP();

    public void updateHP(){
        HT = Math.round(baseHP()*(1 + (Dungeon.hero != null ? Dungeon.hero.pointsInTalent(Talent.DURABILITY_OF_FIGHTER) * 0.125f : 0)));
    }

    int left;

    @Override
    public int attackSkill(Char target) {
        return Dungeon.hero != null ? Dungeon.hero.attackSkill(target) : 33; //lvl 24 accuracy
    }

    @Override
    public int defenseSkill(Char target) {
        return Dungeon.hero != null ? Dungeon.hero.attackSkill(target) : 28; //lvl 24 accuracy
    }

    public static int maxDuration(){
        return Math.round(50 * (1 + (Dungeon.hero != null ? Dungeon.hero.pointsInTalent(Talent.INFLUENCE_OF_MAGICIAN) * 0.125f : 0)));
    }

    @Override
    protected boolean act() {
        if (++left > maxDuration()){
            die(null);
            return true;
        }
        updateHP();
        int oldPos = pos;
        boolean result = super.act();
        //partially simulates how the hero switches to idle animation
        if ((pos == target || oldPos == pos) && sprite.looping()){
            sprite.idle();
        }
        return result;
    }

    public abstract int baseDamageRoll();

    @Override
    public int damageRoll() {
        return Math.round(baseDamageRoll()*(1 + (Dungeon.hero != null ? Dungeon.hero.pointsInTalent(Talent.PRECISION_OF_RANGER) * 0.125f : 0)));
    }

    @Override
    public float speed() {
        float speed = super.speed();

        //moves 2 tiles at a time when returning to the hero
        if (state == WANDERING
                && Dungeon.level.distance(pos, Dungeon.hero.pos) > 1){
            speed *= 2;
        }

        return speed;
    }

    public float targetPriority(){
        return 2.0f;
    }

    @Override
    public String description() {
        return Messages.get(this, "desc", maxDuration() - left);
    }

    private static final String LEFT        = "left";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, left);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        left = bundle.getInt(LEFT);
    }

    private class Wandering extends Mob.Wandering {

        @Override
        public boolean act( boolean enemyInFOV, boolean justAlerted ) {
            if ( enemyInFOV && canAttack(enemy)) {

                enemySeen = true;

                notice();
                alerted = true;
                state = HUNTING;
                target = enemy.pos;

            } else {

                enemySeen = false;

                int oldPos = pos;
                target = Dungeon.hero.pos;
                //always move towards the hero when wandering
                if (getCloser( target )) {
                    spend( 1 / speed() );
                    return moveSprite( oldPos, pos );
                } else {
                    spend( TICK );
                }

            }
            return true;
        }

    }

    public static abstract class BaseSprite extends MobSprite {
        public BaseSprite() {
            super();

            setup(heroClass(), heroTier());
        }

        public void setup(HeroClass cls, int tier){
            texture(cls.spritesheet());

            TextureFilm film = new TextureFilm( HeroSprite.tiers(), tier, 12, 15 );

            idle = new Animation( 1, true );
            idle.frames( film, 0, 0, 0, 1, 0, 0, 1, 1 );

            run = new Animation( 20, true );
            run.frames( film, 2, 3, 4, 5, 6, 7 );

            die = new Animation( 20, false );
            die.frames( film, 0 );

            attack = new Animation( 15, false );
            attack.frames( film, 13, 14, 15, 0 );

            zap = attack.clone();

            play(idle, true);
            resetColor();
        }

        abstract HeroClass heroClass();
        abstract int heroTier();

        @Override
        public void link(Char ch) {
            super.link(ch);
            if (ch instanceof BaseTriadAlly){
                setup(heroClass(), heroTier());
            }
        }

        abstract void tintSprite();

        @Override
        public void resetColor() {
            super.resetColor();
            if (ch instanceof BaseTriadAlly){
                alpha(1f - (float) ((BaseTriadAlly) ch).left / BaseTriadAlly.maxDuration() + 0.25f);
            } else {
                alpha(0.8f);
            }
            tintSprite();
            rm = gm = bm = 0;
        }

        @Override
        public void die() {
            super.die();
            emitter().burst(MagicMissile.WhiteParticle.FACTORY, 15);
            emitter().start( Speck.factory( Speck.LIGHT ), 0.2f, 3 );
        }

        @Override
        public void draw() {
            if (ch instanceof BaseTriadAlly){
                alpha(1f - (float) ((BaseTriadAlly) ch).left / BaseTriadAlly.maxDuration() + 0.25f);
            } else {
                alpha(0.8f);
            }
            rm = gm = bm = 0; //always flat and transparent
            super.draw();
        }
    }
}
