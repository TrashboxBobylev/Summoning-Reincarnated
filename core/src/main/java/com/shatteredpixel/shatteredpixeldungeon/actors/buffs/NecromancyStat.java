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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.DreemurrsNecromancy;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PointF;

public class NecromancyStat extends Buff {

    {
        actPriority = MOB_PRIO + 1;
    }

    @Override
    public boolean act() {
        if (target.HP <= 0) {
            if (!target.isImmune(NecromancyAlly.class) && target.alignment != Char.Alignment.ALLY) {
                AllyBuff.affectAndLoot((Mob) target, Dungeon.hero, NecromancyAlly.class);
            } else if (target.alignment != Char.Alignment.ALLY) {
                detach();
                return true;
            }

            partialPassiveDrain += DreemurrsNecromancy.passiveManaDrain(level);
            if (partialPassiveDrain > 1) {
                int drain = (int) partialPassiveDrain;
                partialPassiveDrain -= drain;
                if (drain > Dungeon.hero.mana) {
                    detach();
                    return true;
                } else {
                    Dungeon.hero.mana -= drain;
                    Dungeon.hero.sprite.showStatusWithIcon(CharSprite.NEGATIVE, Integer.toString(drain), FloatingText.MANA);
                }
            }
        }

        spend(TICK);

        return true;
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)){
            target.deathRefusal = Char.DeathRefusals.DREEMURR_NECROMANCY;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void detach() {
        super.detach();
        target.deathRefusal = null;
        if (!target.isAlive()){
            if (target.sprite.visible){
                PointF c = target.sprite.center();
                Splash.at( c, PointF.angle( c, c ), 3.1415926f / 2, 0xFFEEEEEE, 15);
            }
            target.sprite.flash();
            Sample.INSTANCE.play(Assets.Sounds.BURNING);
            Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
            target.die(this);
        }
    }

    @Override
    public int icon() {
        return BuffIndicator.NECROMANCY;
    }

    public int level = 1;
    public float partialPassiveDrain;
    public float partialActiveDrain;

    private static final String LEVEL = "level";
    private static final String PASSIVE_DRAIN = "passiveDrain";
    private static final String ACTIVE_DRAIN = "activeDrain";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEVEL, level);
        bundle.put(PASSIVE_DRAIN, partialPassiveDrain);
        bundle.put(ACTIVE_DRAIN, partialActiveDrain);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        level = bundle.getInt(LEVEL);
        partialPassiveDrain = bundle.getFloat(PASSIVE_DRAIN);
        partialActiveDrain = bundle.getFloat(ACTIVE_DRAIN);
    }

    public static class NecromancyAlly extends AllyBuff {}
}
