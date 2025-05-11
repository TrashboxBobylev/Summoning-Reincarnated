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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAccuracy;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfElements;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEvasion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfTenacity;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.watabou.utils.Random;

public class TriadFighter extends BaseTriadAlly {

    {
        spriteClass = Sprite.class;
    }

    @Override
    public int baseHP() {
        int health = 100;
        if (Dungeon.hero.pointsInTalent(Talent.DURABILITY_OF_FIGHTER) > 3){
            health *= RingOfMight.HTMultiplier(Dungeon.hero);
        }
        return health;
    }

    @Override
    public int baseDamageRoll() {
        int str = Dungeon.hero.STR();
        int damage = Math.round(Random.NormalFloat(str * 0.75f, str * 1.25f));
        if (Dungeon.hero.pointsInTalent(Talent.DURABILITY_OF_FIGHTER) > 1){
            Weapon wep = (Weapon) Dungeon.hero.belongings.weapon();
            if (wep != null){
                if (Dungeon.hero.pointsInTalent(Talent.DURABILITY_OF_FIGHTER) > 2){
                    damage = wep.proc(this, enemy, wep.damageRoll(Dungeon.hero));
                } else {
                    damage = Math.round(wep.damageRoll(Dungeon.hero) / wep.delayFactor(Dungeon.hero));
                }
            }
        }
        if (Dungeon.hero.pointsInTalent(Talent.DURABILITY_OF_FIGHTER) > 3){
            damage += RingOfForce.armedDamageBonus(Dungeon.hero);
        }
        return damage;
    }

    @Override
    public float attackDelay() {
        float delay = super.attackDelay();
        if (Dungeon.hero.pointsInTalent(Talent.DURABILITY_OF_FIGHTER) > 2){
            Weapon wep = (Weapon) Dungeon.hero.belongings.weapon();
            if (wep != null){
                delay *= wep.delayFactor(Dungeon.hero);
            }
        }
        if (Dungeon.hero.pointsInTalent(Talent.DURABILITY_OF_FIGHTER) > 3){
            delay /= RingOfFuror.attackSpeedMultiplier(Dungeon.hero);
        }
        return delay;
    }

    @Override
    public int drRoll() {
        int str = Dungeon.hero.STR();
        return Math.round((super.drRoll() + Random.NormalFloat(str*0.5f, str*1.25f))*(1 + (Dungeon.hero != null ? Dungeon.hero.pointsInTalent(Talent.DURABILITY_OF_FIGHTER) * 0.15f : 0)));
    }

    @Override
    public int attackSkill(Char target) {
        int accuracy = super.attackSkill(target);
        if (Dungeon.hero.pointsInTalent(Talent.DURABILITY_OF_FIGHTER) > 3){
            accuracy *= RingOfAccuracy.accuracyMultiplier(Dungeon.hero);
        }
        if (Dungeon.hero.pointsInTalent(Talent.DURABILITY_OF_FIGHTER) > 2){
            Weapon wep = (Weapon) Dungeon.hero.belongings.weapon();
            if (wep != null){
                accuracy *= wep.accuracyFactor(Dungeon.hero, target);
            }
        }
        return accuracy;
    }

    @Override
    public int defenseSkill(Char target) {
        int evasion = super.defenseSkill(target);
        if (Dungeon.hero.pointsInTalent(Talent.DURABILITY_OF_FIGHTER) > 3){
            evasion *= RingOfEvasion.evasionMultiplier(Dungeon.hero);
        }
        return evasion;
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        int dmg = super.defenseProc(enemy, damage);
        if (Dungeon.hero.hasTalent(Talent.DURABILITY_OF_FIGHTER)){
            if (Dungeon.hero.belongings.armor() != null) {
                dmg = Dungeon.hero.belongings.armor().proc( enemy, this, dmg );
            }
        }
        return dmg;
    }

    @Override
    public float speed() {
        float speed = super.speed();
        if (Dungeon.hero.pointsInTalent(Talent.DURABILITY_OF_FIGHTER) > 3){
            speed *= RingOfHaste.speedMultiplier(Dungeon.hero);
        }
        return speed;
    }

    @Override
    public void damage(int dmg, Object src) {
        if (Dungeon.hero.pointsInTalent(Talent.DURABILITY_OF_FIGHTER) > 3){
            dmg *= (int)Math.ceil(dmg * RingOfTenacity.damageMultiplier( Dungeon.hero ));
        }
        super.damage(dmg, src);
    }

    @Override
    public float resist(Class effect) {
        float resist = super.resist(effect);
        if (Dungeon.hero.pointsInTalent(Talent.DURABILITY_OF_FIGHTER) > 3){
            resist *= RingOfElements.resist(Dungeon.hero, effect);
        }
        return resist;
    }

    public static class Sprite extends BaseSprite {

        @Override
        HeroClass heroClass() {
            return HeroClass.WARRIOR;
        }

        @Override
        int heroTier() {
            return 6;
        }

        @Override
        void tintSprite() {
            tint(0xffe79d, 0.75f);
        }
    }
}
