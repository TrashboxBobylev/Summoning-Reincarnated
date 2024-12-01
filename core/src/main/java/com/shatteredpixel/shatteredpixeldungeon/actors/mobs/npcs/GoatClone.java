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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Empowered;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.ManaSource;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfCleansing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.ToyKnife;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GoatCloneSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;

public class GoatClone extends NPC implements ManaSource {
    private int defendingPos;

    {
        spriteClass = GoatCloneSprite.class;
        alignment = Alignment.ALLY;
        intelligentAlly = true;
        WANDERING = new Wandering();
        baseSpeed = 2f;
        actPriority = MOB_PRIO + 1;
        state = WANDERING;
//        properties.add(Property.IGNORE_ARMOR);
    }

    public int healCombo = 0;

    @Override
    public Char chooseEnemy() {

//        if ((Dungeon.hero.buff(CloakOfShadows.cloakStealth.class) != null &&
//                Dungeon.hero.buff(CloakOfShadows.cloakStealth.class).glyph() instanceof Silent)
//        )
//            return null;

        boolean newEnemy = false;
        if ( enemy == null || !enemy.isAlive() || !Actor.chars().contains(enemy) || state == WANDERING) {
            newEnemy = true;
        } else if (enemy.alignment == Alignment.ALLY && !Dungeon.hero.hasTalent(Talent.SPIRITUAL_RESTOCK)) {
            newEnemy = true;
        } else if (enemy.isInvulnerable(getClass()) && enemy.buff(StoneOfAggression.Aggression.class) == null) {
            newEnemy = true;
        } else if (enemy.buff(ToyKnife.SoulGain.class) == null) {
            newEnemy = true;
        }

        if (newEnemy){
            HashMap<Char, Float> enemies = new HashMap<>();

            for (Mob mob : Dungeon.level.mobs)
                if ((mob.alignment == Alignment.ENEMY || Dungeon.hero.hasTalent(Talent.SPIRITUAL_RESTOCK)) && fieldOfView[mob.pos] && mob.buff(ToyKnife.SoulGain.class) != null) {
                        enemies.put(mob, mob.targetPriority());
                    }
            return chooseClosest(enemies);
        } else {
            return enemy;
        }
    }

    @Override
    public float manaModifier(Char source) {
        return 1f;
    }

    @Override
    public int damageRoll() {
        int i = Random.NormalIntRange(Dungeon.hero.lvl / 3, Dungeon.hero.lvl);
        if (Dungeon.isChallenged(Conducts.Conduct.PACIFIST)) i /= 3;
        return i;
    }

    @Override
    public boolean isInvulnerable(Class effect) {
        return true;
    }

    @Override
    public float attackDelay() {
        return super.attackDelay() * 0.3f;
    }

    @Override
    public int attackSkill(Char target) {
        return Dungeon.hero.attackSkill(target);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
//        if (Dungeon.mode != Dungeon.GameMode.HELL)
        if (++healCombo > 5){
            healCombo = 0;
            int hpGain = Dungeon.hero.pointsInTalent(Talent.REJUVENATING_FORCE)+1;
            int manaGain = Dungeon.hero.pointsInTalent(Talent.REJUVENATING_FORCE);
            hpGain = Math.min(Dungeon.hero.HT-Dungeon.hero.HP, hpGain);
            manaGain = Math.min(Dungeon.hero.maxMana()-Dungeon.hero.mana, manaGain);
            Dungeon.hero.HP += hpGain;
            Dungeon.hero.mana += manaGain;
            if (hpGain > 0)
                Dungeon.hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(hpGain), FloatingText.HEALING);
            if (manaGain > 0)
                Dungeon.hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(manaGain), FloatingText.MANA);
        }
        damage = super.attackProc(enemy, damage);
        if (Dungeon.hero.hasTalent(Talent.VIOLENT_OVERCOMING)){
            Buff.affect(enemy, ViolentOvercomingCombo.class, 1.5f).proc(this);
        }
        if (Dungeon.hero.hasTalent(Talent.SPIRITUAL_RESTOCK) && enemy.alignment == Alignment.ALLY){
            Buff.affect(enemy, Adrenaline.class, 8f);
            Buff.affect(enemy, PotionOfCleansing.Cleanse.class, 8f);
            damage *= 1f - 0.25f * Dungeon.hero.pointsInTalent(Talent.SPIRITUAL_RESTOCK);
        }
        if (Dungeon.hero.belongings.weapon != null){
            return Dungeon.hero.belongings.weapon.proc( enemy, this, damage );
        } else {
            return damage;
        }
    }

    public static GoatClone findClone(){
        GoatClone clone = null;

        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
            if (mob instanceof GoatClone){
                if (clone != null){
                    mob.destroy();
                } else {
                    clone = (GoatClone) mob;
                }
            }
        }

        return clone;
    }

    public static void spawnClone(){
        ArrayList<Integer> respawnPoints = new ArrayList<>();

        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
            int p = Dungeon.hero.pos + PathFinder.NEIGHBOURS8[i];
            if ((Actor.findChar( p ) == null || Actor.findChar( p ) == Dungeon.hero) && Dungeon.level.passable[p]) {
                respawnPoints.add( p );
            }
        }

        if (respawnPoints.isEmpty()) return;

        int index = Random.index( respawnPoints );

        GoatClone clone = new GoatClone();

        GameScene.add( clone );
        ScrollOfTeleportation.appear( clone, respawnPoints.get( index ) );
    }

    @Override
    public float speed() {
        float speed = super.speed();

        //moves 2 tiles at a time when returning to the hero
        if (state == WANDERING && defendingPos == -1){
            speed *= 2;
        }

        return speed;
    }

    private static final String HEAL_COMBO = "healCombo";
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(HEAL_COMBO, healCombo);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        healCombo = bundle.getInt(HEAL_COMBO);
    }

    {
        immunities.add(Terror.class);
        immunities.add(Amok.class);
        immunities.add(Charm.class);
    }

    public class Wandering extends Mob.Wandering {

        private Char toFollow(Char start) {
            Char toFollow = start;
            boolean[] passable = Dungeon.level.passable;
            PathFinder.buildDistanceMap(pos, passable, 8*2);//No limit on distance
            for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                if (mob.alignment == alignment && PathFinder.distance[toFollow.pos] > PathFinder.distance[mob.pos] && mob.following(toFollow)) {
                    toFollow = toFollow(mob);//If we find a mob already following the target, ensure there is not a mob already following them. This allows even massive chains of allies to traverse corridors correctly.
                }
            }
            return toFollow;
        }

        @Override
        public boolean act( boolean enemyInFOV, boolean justAlerted ) {

            //Ensure there is direct line of sight from ally to enemy, and the distance is small. This is enforced so that allies don't end up trailing behind when following hero.
            if ( enemyInFOV && Dungeon.level.distance(pos, enemy.pos) < 8) {

                enemySeen = true;

                notice();
                alerted = true;

                state = HUNTING;
                target = enemy.pos;

            } else {

                enemySeen = false;
                Char toFollow = toFollow(Dungeon.hero);
                int oldPos = pos;
                //always move towards the target when wandering
                if (getCloser( target = toFollow.pos )) {
                    if (!Dungeon.level.adjacent(toFollow.pos, pos) && Actor.findChar(pos) == null) {
                        getCloser( target = toFollow.pos );
                    }
                    spend( 1 / speed() );
                    return moveSprite( oldPos, pos );
                } else {
                    spend( TICK );
                }

            }
            return true;
        }

    }

    public static class ViolentOvercomingCombo extends FlavourBuff {
        public int combo = 0;

        private static final String COMBO = "combo";

        public void proc(Char attacker){
            combo++;
            if (combo >= (9 - 2 * Dungeon.hero.pointsInTalent(Talent.VIOLENT_OVERCOMING))){
                Buff.affect(attacker, Empowered.class, 0.33f);
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(COMBO, combo);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            combo = bundle.getInt(COMBO);
        }
    }

}
