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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.shop;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Wound;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Stabber extends MeleeWeapon {
    {
        tier = 4;
        image = ItemSpriteSheet.STABBER;
    }

    @Override
    public int min(int lvl) {
        return tier - 3 + lvl;
    }

    @Override
    public int max(int lvl) {
        return 3 * (tier - 2) + (tier - 2) * lvl; //6, +2
    }

    @Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            Hero hero = (Hero)owner;
            Char enemy = hero.attackTarget();
            if (enemy instanceof Mob && ((Mob) enemy).state != ((Mob) enemy).HUNTING && ((Mob) enemy).surprisedBy(owner)) {
                //grims non-hunting targets
                Weapon weapon = new Weapon() {
                    @Override
                    public int STRReq(int lvl) {
                        return 0;
                    }

                    @Override
                    public int min(int lvl) {
                        return 0;
                    }

                    @Override
                    public int max(int lvl) {
                        return 0;
                    }
                };
                weapon.upgrade(90);
                weapon.enchant(new Grim());
                weapon.proc(curUser, enemy, enemy.HP - 1);
                Wound.hit(enemy);
                return 0;
            }
        }
        return super.damageRoll(owner);
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    public boolean useTargeting(){
        return false;
    }

    @Override
    public void duelistAbility(Hero hero, Integer target) {
        Dagger.sneakAbility(hero, target, 2, 4+buffedLvl()*2, this);
    }

    @Override
    public String abilityInfo() {
        if (levelKnown){
            return Messages.get(this, "ability_desc", 4+buffedLvl()*2);
        } else {
            return Messages.get(this, "typical_ability_desc", 4);
        }
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return Integer.toString(4+level*2);
    }

//    @Override
//    public int warriorAttack(int damage, Char enemy) {
//        //steal satiety
//        Hunger.adjustHunger((damage * 8f / enemy.HT * 1f));
//        SpellSprite.show(enemy, SpellSprite.FOOD);
//        return super.warriorAttack(damage, enemy);
//    }
}
