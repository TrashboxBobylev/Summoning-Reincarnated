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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.generic.FlightBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Ascension extends ArmorAbility {

    {
        baseChargeUse = 75;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        Buff.affect(hero, AscendBuff.class).reset();
        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
        new Flare(12, 128).color(0xFFFFFF, true).show(hero.sprite, 2f);

        armor.charge -= chargeUse(hero);
        armor.updateQuickslot();
        Invisibility.dispel();
        hero.spendAndNext(Actor.TICK);
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.CHARITY, Talent.MALICE, Talent.EGOISM, Talent.HEROIC_ENERGY};
    }

    @Override
    public int icon() {
        return HeroIcon.ASCENSION;
    }

    public static class AscendBuff extends ShieldBuff implements FlightBuff {

        {
            type = buffType.POSITIVE;

            detachesAtZero = true;
        }

        @Override
        public boolean attachTo( Char target ) {
            if (super.attachTo( target )) {
                processFlightStart();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void detach() {
            super.detach();
            processFlightEnd();
        }

        public Char target() {
            return target;
        }

        @Override
        public int icon() {
            return BuffIndicator.ASCENSION;
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(shielding());
        }

        @Override
        public boolean detachesWithinDelay(float delay){
            if (target().buff(Swiftthistle.TimeBubble.class) != null){
                return false;
            }

            if (target().buff(TimekeepersHourglass.timeFreeze.class) != null){
                return false;
            }

            return partialLostShield >= delay;
        }

        @Override
        public void fx(boolean on) {
            if (on) {
                target.sprite.add(CharSprite.State.CONJURER_WINGS);
                target.sprite.add(CharSprite.State.SHIELDED);
            }
            else {
                target.sprite.remove(CharSprite.State.CONJURER_WINGS);
                target.sprite.remove(CharSprite.State.SHIELDED);
            }
        }

        float partialLostShield;

        public void reset(){
            setShield(Dungeon.hero.maxMana() + Dungeon.hero.mana);
            Dungeon.hero.sprite.showStatusWithIcon(CharSprite.NEGATIVE, Integer.toString(Dungeon.hero.mana), FloatingText.MANA);
            Dungeon.hero.mana = 0;
        }

        @Override
        public boolean act() {
            partialLostShield += 0.5f;

            if (partialLostShield >= 1f) {
                absorbDamage(1);
                partialLostShield = 0;
            }

            if (shielding() <= 0){
                detach();
            } else {
                if (target instanceof Hero && ((Hero) target).pointsInTalent(Talent.EGOISM) > 2){
                    //50% of wand and artifact recharging buffs
                    for (Buff b : target.buffs()) {
                        if (b instanceof Artifact.ArtifactBuff) {
                            if (!((Artifact.ArtifactBuff) b).isCursed()) {
                                ((Artifact.ArtifactBuff) b).charge((Hero) target, 0.5f);
                            }
                        }
                        if (b instanceof Wand.Charger){
                            ((Wand.Charger) b).gainCharge(0.125f);
                        }
                    }
                }
            }

            spend(TICK);
            return true;
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", shielding());
        }

        private static final String PARTIAL_LOST_SHIELD = "partial_lost_shield";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(PARTIAL_LOST_SHIELD, partialLostShield);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            partialLostShield = bundle.getFloat(PARTIAL_LOST_SHIELD);
        }
    }
}
