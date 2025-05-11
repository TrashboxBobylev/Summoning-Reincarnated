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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Block;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chungus;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Empowered;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class TriadMagician extends BaseTriadAlly implements Callback {

    {
        spriteClass = Sprite.class;
    }

    @Override
    public int baseHP() {
        return 60;
    }

    // does not deal any damage
    @Override
    public int baseDamageRoll() {
        return -1;
    }

    @Override
    public boolean canBeIgnored(Char ch) {
        if (ch instanceof BaseTriadAlly){
            return super.canBeIgnored(ch) || Dungeon.hero.pointsInTalent(Talent.INFLUENCE_OF_MAGICIAN) < 3;
        }
        return super.canBeIgnored(ch) || ch.alignment != Alignment.ALLY || ch instanceof Hero;
    }

    @Override
    public boolean canTargetAlliesAsAlly() {
        return true;
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        return super.canAttack(enemy)
                || new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
    }

    protected boolean doAttack( Char enemy ) {

        if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
            sprite.zap( enemy.pos );
            return false;
        } else {
            zap();
            return true;
        }
    }

    @Override
    public float attackDelay() {
        return super.attackDelay()*2f;
    }

    enum PossibleEffect {

        ADRENALINE(Adrenaline.class, 5f),
        HASTE(Haste.class, 2f),
        BLESS(Bless.class, 5f),
        ENLARGE(Chungus.class, 5f),
        MAGIC_IMMUNE(MagicImmune.class, 5f),
        BLOCK(Block.class, 5f)
        ;

        Class<? extends FlavourBuff> buffClass;
        float duration;

        PossibleEffect(Class<? extends FlavourBuff> buffClass, float duration) {
            this.buffClass = buffClass;
            this.duration = duration;
        }
    }

    protected void zap(){
        spend(attackDelay());

        PossibleEffect effect = Random.element(PossibleEffect.values());
        Buff.affect(enemy, effect.buffClass, effect.duration + Math.max(Dungeon.hero.ATU() - 5, 0)*1.5f);

        if (Dungeon.hero.hasTalent(Talent.INFLUENCE_OF_MAGICIAN)){
            Regeneration.regenerate(enemy, 7);

            if (Dungeon.hero.pointsInTalent(Talent.INFLUENCE_OF_MAGICIAN) > 1){
                Buff.affect(enemy, Empowered.class, 3f);
            }
            if (Dungeon.hero.pointsInTalent(Talent.INFLUENCE_OF_MAGICIAN) > 3){
                Buff.affect(enemy, Swiftthistle.TimeBubble.class).add(1);
            }
        }
    }

    public void onZapComplete() {
        zap();
        next();
    }

    @Override
    public void call() {
        next();
    }

    @Override
    public float targetPriority() {
        return super.targetPriority()*0.33f;
    }

    public static class Sprite extends BaseSprite {

        @Override
        HeroClass heroClass() {
            return HeroClass.MAGE;
        }

        @Override
        int heroTier() {
            return 3;
        }

        @Override
        void tintSprite() {
            tint(0x15b3f2, 0.75f);
        }

        public void zap( int cell ) {

            super.zap( cell );

            MagicMissile.boltFromChar( parent,
                    MagicMissile.STAR,
                    this,
                    cell,
                    new Callback() {
                        @Override
                        public void call() {
                            ((TriadMagician)ch).onZapComplete();
                        }
                    } );
            Sample.INSTANCE.play( Assets.Sounds.ZAP );
        }

        @Override
        public void onComplete( Animation anim ) {
            if (anim == zap) {
                idle();
            }
            super.onComplete( anim );
        }
    }
}
