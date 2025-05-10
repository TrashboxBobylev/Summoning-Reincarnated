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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.conjurer;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.conjurer.triadallies.BaseTriadAlly;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.conjurer.triadallies.TriadFighter;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.conjurer.triadallies.TriadMagician;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.conjurer.triadallies.TriadRanger;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class TriadOfPower extends ArmorAbility {

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target != null){
            if (!Dungeon.level.mapped[target] && !Dungeon.level.visited[target]){
                GLog.w( Messages.get(TriadOfPower.class, "invalid_pos") );
                return;
            }

            PathFinder.buildDistanceMap(target, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
            if (Dungeon.level.pit[target] ||
                    (Dungeon.level.solid[target] && !Dungeon.level.passable[target]) ||
                    !(Dungeon.level.passable[target] || Dungeon.level.avoid[target]) ||
                    PathFinder.distance[hero.pos] == Integer.MAX_VALUE){
                GLog.w( Messages.get(TriadOfPower.class, "invalid_pos") );
                return;
            }

            for (AllyVariant variant : AllyVariant.values()){
                if (variant.isValidTarget(target)){
                    ArrayList<Integer> spawnPoints = new ArrayList<>();
                    for (int i = 0; i < PathFinder.NEIGHBOURS9.length; i++) {
                        int p = target + PathFinder.NEIGHBOURS9[i];
                        if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
                            spawnPoints.add(p);
                        }
                    }

                    if (!spawnPoints.isEmpty()){
                        BaseTriadAlly ally = null;
                        boolean newAlly = false;
                        for (Mob mob : Dungeon.level.mobs){
                            if (mob.getClass().isAssignableFrom(variant.allyType)){
                                ally = (BaseTriadAlly) mob;
                                break;
                            }
                        }

                        if (ally == null) {
                            armor.charge -= chargeUse(hero);
                            ally = Reflection.newInstance(variant.allyType);
                            newAlly = true;
                        }
                        armor.updateQuickslot();

                        ally.pos = spawnPoints.contains(target) ? target : Random.element(spawnPoints);
                        ally.updateHP();
                        if (newAlly) {
                            GameScene.add(ally);
                            ally.HP = ally.HT;
                        }

                        ScrollOfTeleportation.appear(ally, ally.pos);
                        Dungeon.observe();

                        hero.sprite.zap(target);
                        Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);

                        Invisibility.dispel();
                        hero.spendAndNext(Actor.TICK);

                    } else {
                        GLog.w(Messages.get(TriadOfPower.class, "no_space"));
                    }
                }
            }
        }
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.DURABILITY_OF_FIGHTER, Talent.INFLUENCE_OF_MAGICIAN, Talent.PRECISION_OF_RANGER, Talent.HEROIC_ENERGY};
    }

    public enum AllyVariant {
        FIGHTER(TriadFighter.class){
            @Override
            boolean isValidTarget(int cell) {
                Char target;
                return (target = Actor.findChar(cell)) != null && target.alignment == Char.Alignment.ENEMY;
            }
        },
        MAGICIAN(TriadMagician.class){
            @Override
            boolean isValidTarget(int cell) {
                Char target;
                return (target = Actor.findChar(cell)) != null && target.alignment == Char.Alignment.ALLY;
            }
        },
        RANGER(TriadRanger.class){
            @Override
            boolean isValidTarget(int cell) {
                return Actor.findChar(cell) == null;
            }
        };

        Class<? extends BaseTriadAlly> allyType;

        AllyVariant(Class<? extends BaseTriadAlly> allyType){
            this.allyType = allyType;
        }

        abstract boolean isValidTarget(int cell);
    }
}
