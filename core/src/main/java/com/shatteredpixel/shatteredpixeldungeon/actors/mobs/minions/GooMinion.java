/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2022 Evan Debenham
 *
 * Summoning Pixel Dungeon
 * Copyright (C) 2019-2022 TrashboxBobylev
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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.GooWarn;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.staffs.GooStaff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GooMinionSprite;
import com.watabou.noosa.Game;
import com.watabou.utils.*;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;

import java.lang.reflect.InvocationTargetException;

public class GooMinion extends Minion {
    {
        spriteClass = GooMinionSprite.class;

        properties.add(Property.DEMONIC);
        properties.add(Property.ACIDIC);

        minDefense = 5;
        maxDefense = 20;
    }

    private int pumpedUp = 0;
    private boolean pumping = false;

    public Class<? extends Potion> infusedPotion = null;
    public int potionUses = 0;
    public int potionColor = 0x000000;

    @Override
    public int attackSkill( Char target ) {
        int attack = super.attackSkill(target);
        if (pumpedUp > 0) attack *= 2;
        return attack;
    }

    private int chargeTurns(){
        switch (rank){
            case 1: return 2;
            case 2: return 1;
            case 3: return 2;
        }
        return 2;
    }

    @Override
    public boolean act() {

        if (Dungeon.level.water[pos] && HP < HT) {
            sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
            HP++;
        }
        if (pumpedUp < 0){
            pumping = false;
            pumpedUp = 0;
        }

        return super.act();
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        return (pumpedUp > 0) ? distance( enemy ) <= 2 : super.canAttack(enemy);
    }

    @Override
    public int attackProc( Char enemy, int damage ) {
        damage = super.attackProc( enemy, damage );
        if (Random.Int( 3 ) == 0) {
            Buff.affect( enemy, Ooze.class ).set( 20f );
            enemy.sprite.burst( 0x000000, 5 );
        }

        return damage;
    }

    @Override
    protected boolean doAttack(Char enemy) {

        if (pumping){
            pumpedUp--;
            if (pumpedUp == 0){
                ((GooMinionSprite) sprite).pumpAttack();
                pumping = false;
                return false;
            }
            spend(attackDelay());
            return true;
        }
        else {
            if (Random.Int(3) <= 1 && rank != 3)
                return super.doAttack(enemy);
            else {
                pumpedUp = chargeTurns();
                pumping = true;
                ((GooMinionSprite) sprite).pumpUp();
                spend(attackDelay());
                PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), elementalRange());
                for (int i = 0; i < PathFinder.distance.length; i++) {
                    if (PathFinder.distance[i] < Integer.MAX_VALUE)
                        GameScene.add(Blob.seed(i, chargeTurns()+1, GooWarn.class));
                }

                return true;
            }
        }
    }

    public void elementalAttack(){
        pumpedUp = 0;
        int min = 0, max = 0;
        switch (rank) {
            case 1:
                min = Math.round(minDamage*1.5f);
                max = Math.round(maxDamage*1.5f);
                break;
            case 3:
                min = Math.round(Bomb.minDamage()*0.66f);
                max = Math.round(Bomb.maxDamage()*0.66f);
                break;
        }

        float multiplier = 1f;
//        if (Dungeon.hero.buff(Attunement.class) != null) multiplier *= Attunement.empowering();

        min *= multiplier; max *= multiplier;

        PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), elementalRange());
        for (int i = 0; i < PathFinder.distance.length; i++) {

            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                CellEmitter.get(i).burst(ElmoParticle.FACTORY, 10);

                Char ch = Actor.findChar(i);
                if (ch != null && ch != Dungeon.hero) {
                    if (rank != 2) {
                        if (ch.alignment == Alignment.ENEMY)
                            ch.damage(Random.NormalIntRange(min, max), Dungeon.hero);
                    }
                    else {
                        Potion potion = Reflection.newInstance(infusedPotion);
                        if (potion != null)
                            potion.gooMinionAttack(ch);
                    }
                }
            }
        }

        Sample.INSTANCE.play(Assets.Sounds.BURNING);
        Camera.main.shake(2f, 0.4f);
        spend(attackDelay());
        next();
        if (infusedPotion != null){
            if (--potionUses <= 0){
                infusedPotion = null;
                potionUses = 0;
                potionColor = 0x000000;
                ((GooMinionSprite)sprite).spray(potionColor);
            }
        }
        if (rank == 3){
            die(null);
        }
    }

    private int elementalRange() {
        int range = 2;
        if (rank == 2)
            range = 3;
        return range;
    }

    @Override
    public String description() {
        if (rank == 2 && infusedPotion != null){
            return super.description() + "\n\n" +
                    Messages.get(this, "potion_desc",
                            Reflection.newInstance(infusedPotion).name(),
                            potionUses,
                            Messages.get(infusedPotion, "goo_minion_effect"));
        }
        return super.description();
    }

    @Override
    public boolean attack( Char enemy, float dmgMulti, float dmgBonus, float accMulti ) {
        if (pumping && pumpedUp != 0) return false;
        boolean result = super.attack(enemy, dmgMulti, dmgBonus, accMulti);
        pumpedUp = 0;
        return result;
    }

    @Override
    protected boolean getCloser( int target ) {
        if (pumpedUp == 0) return super.getCloser( target );
        else {
            if (pumping) {
                pumpedUp = 0;
                return false;
            }
        }
        return super.getCloser( target );
    }

    @Override
    public void damage(int dmg, Object src) {
        if (rank == 3){
            damage(dmg/10, src);
        } else {
            if (!(src instanceof Viscosity.DeferedDamage)) {
                float deferedDmgMulti = 0.5f;
                if (pumping) {
                    dmg *= 2;
                    deferedDmgMulti = 1f;
                }
                Viscosity.DeferedDamage deferred = Buff.affect(this, Viscosity.DeferedDamage.class);
                deferred.prolong((int) (dmg * deferedDmgMulti));
                dmg = (int) Math.min(dmg - dmg * deferedDmgMulti, 0);
            }
            super.damage(dmg, src);
        }
    }

    private final String PUMPEDUP = "pumpedup";
    private final String PUMPING = "pumping";
    private final String POTION = "infused_potion";
    private final String POTION_USES = "potion_uses";
    private final String POTION_COLOR = "potion_color";

    @Override
    public void storeInBundle( Bundle bundle ) {

        super.storeInBundle( bundle );

        bundle.put( PUMPEDUP , pumpedUp );
        bundle.put( PUMPING, pumping);
        bundle.put( POTION, infusedPotion);
        bundle.put( POTION_USES, potionUses);
        bundle.put( POTION_COLOR, potionColor);
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {

        super.restoreFromBundle( bundle );

        pumpedUp = bundle.getInt( PUMPEDUP );
        pumping = bundle.getBoolean(PUMPING);
        infusedPotion = (Class<? extends Potion>) bundle.getClass(POTION);
        potionUses = bundle.getInt(POTION_USES);
        potionColor = bundle.getInt(POTION_COLOR);
    }
}
