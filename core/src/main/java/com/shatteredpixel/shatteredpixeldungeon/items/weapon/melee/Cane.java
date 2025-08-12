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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyDamageTag;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.effects.ShieldHalo;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class Cane extends MeleeWeapon {
    {
        image = ItemSpriteSheet.CANE;
        hitSound = Assets.Sounds.HIT_CRUSH;
        hitSoundPitch = 1.25f;

        tier = 2;
        RCH = 2;
    }

    @Override
    public int max(int lvl) {
        return  5*(tier) +    //10 base, down from 15
                Math.round(lvl*(tier+1)/2f); //+1.5 per level, down from +3
    }

    public int tagValue(){
        return tagValue(buffedLvl());
    }

    public int tagValue(int level){
        return 3 + level;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        ShieldHalo shield;
        GameScene.effect(shield = new ShieldHalo(defender.sprite));
        shield.putOut();

        for (Mob mob : Dungeon.level.mobs) {
            if (mob.paralysed <= 0
                    && Dungeon.level.distance(defender.pos, mob.pos) <= 4
                    && mob.alignment == Char.Alignment.ALLY) {
                mob.beckon(defender.pos);
                mob.aggro(defender);
                Buff.affect(defender, Minion.UniversalTargeting.class, 4f);
            }
        }

        applyTag(defender);

        return super.proc(attacker, defender, damage);
    }

    protected void applyTag(Char target){
        Buff.affect(target, AllyDamageTag.class, 4f).setFlat(tagValue());
    }

    public String statsInfo(){
        if (isIdentified()){
            return Messages.get(this, "stats_desc", tagValue());
        } else {
            return Messages.get(this, "typical_stats_desc", tagValue(0));
        }
    }

    @Override
    public String abilityInfo() {
        if (levelKnown){
            return Messages.get(this, "ability_desc", tagValue());
        } else {
            return Messages.get(this, "typical_ability_desc", tagValue(0));
        }
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public void duelistAbility(Hero hero, Integer target) {
        Cane.minionDefenseBoost(hero, target, tagValue(), 0, this);
    }

    public static void minionDefenseBoost(Hero hero, Integer target, int flat, float mult, MeleeWeapon wep){
        if (target == null) {
            return;
        }

        Char ally = Actor.findChar(target);
        if (ally == null || ally == hero || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(wep, "ability_no_target"));
            return;
        }

        hero.belongings.abilityWeapon = wep;
        if (!hero.canAttack(ally) || ally.alignment != Char.Alignment.ALLY){
            GLog.w(Messages.get(wep, "ability_target_range"));
            hero.belongings.abilityWeapon = null;
            return;
        }
        hero.belongings.abilityWeapon = null;

        hero.sprite.attack(ally.pos, new Callback() {
            @Override
            public void call() {
                wep.beforeAbilityUsed(hero, ally);
                Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                Sample.INSTANCE.play(Assets.Sounds.CHARGEUP, 1.0f, 1.35f);

                Invisibility.dispel();

                Buff.affect(ally, AllyDamageTag.class, 10f).setFlat(flat).setMult(mult);

                wep.afterAbilityUsed(hero);
            }
        });
    }
}
