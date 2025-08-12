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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;

public class Cleaver extends MeleeWeapon {

    {
        image = ItemSpriteSheet.CLEAVER;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 0.75f;

        tier = 3;
        ACC = 0.8f;
        DLY = 1.5f;
    }

    @Override
    public int min(int lvl) {
        return  tier-2  //1 base, down from 3
                ;    //no level scaling
    }

    @Override
    public int max(int lvl) {
        return  5*(tier+3) +    //30 base, up from 20
                lvl*(tier+3);   //+6 per level, up from +4
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (defender.HP >= defender.HT) {
            defender.sprite.emitter().burst( Speck.factory( Speck.STAR), 8 );
            damage *= 2;
        }
        return super.proc(attacker, defender, damage);
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public void duelistAbility(Hero hero, Integer target) {
        if (hero.HP / (float)hero.HT >= 0.5f){
            GLog.w(Messages.get(this, "ability_cant_use"));
            return;
        }

        if (target == null) {
            return;
        }

        Char enemy = Actor.findChar(target);
        if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(this, "ability_no_target"));
            return;
        }

        hero.belongings.abilityWeapon = this;
        if (!hero.canAttack(enemy)){
            GLog.w(Messages.get(this, "ability_target_range"));
            hero.belongings.abilityWeapon = null;
            return;
        }
        hero.belongings.abilityWeapon = null;

        hero.sprite.attack(enemy.pos, new Callback() {
            @Override
            public void call() {
                beforeAbilityUsed(hero, enemy);
                AttackIndicator.target(enemy);

                //+(10+(lvl)) damage
                int dmgBoost = augment.damageFactor(10 + buffedLvl());

                if (hero.attack(enemy, 1, dmgBoost, Char.INFINITE_ACCURACY)){
                    Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                }

                Invisibility.dispel();
                if (!enemy.isAlive()){
                    hero.next();
                    onAbilityKill(hero, enemy);
                } else {
                    hero.spendAndNext(hero.attackDelay());
                }
                afterAbilityUsed(hero);
            }
        });
    }

    @Override
    public String abilityInfo() {
        int dmgBoost = levelKnown ? 10 + buffedLvl() : 10;
        if (levelKnown){
            return Messages.get(this, "ability_desc", GameMath.printAverage(augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost)));
        } else {
            return Messages.get(this, "typical_ability_desc", GameMath.printAverage(min(0)+dmgBoost, max(0)+dmgBoost));
        }
    }

    public String upgradeAbilityStat(int level){
        int dmgBoost = 10 + level;
        return GameMath.printAverage(augment.damageFactor(min(level)+dmgBoost), augment.damageFactor(max(level)+dmgBoost));
    }
}
