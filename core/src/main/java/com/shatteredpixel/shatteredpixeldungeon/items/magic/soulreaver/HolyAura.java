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

package com.shatteredpixel.shatteredpixeldungeon.items.magic.soulreaver;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ConstantShielding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ManaStealing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.HolyAuraBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.HolyAuraCD;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.AdHocSpell;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class HolyAura extends AdHocSpell {

    {
        image = ItemSpriteSheet.SR_SUPPORT;
        alignment = Alignment.BENEFICIAL;
    }

    @Override
    public boolean effect(Hero hero) {
        Sample.INSTANCE.play(Assets.Sounds.READ);
        HolyAuraBuff aura = Buff.affect(hero, HolyAuraBuff.class, duration(rank()));
        aura.healingRate = healingRate(rank());
        aura.shieldingRate = shieldingRate(rank());
        aura.cd = cooldown(rank());
        aura.minDamage = minDamage(rank());
        aura.maxDamage = maxDamage(rank());
        aura.manaSteal = manaSteal(rank());
        if (shieldingRate(rank()) != 0) Buff.affect(hero, ConstantShielding.class);
        Buff.affect(hero, ManaStealing.class);
        hero.spendAndNext(1f);
        return true;
    }

    private int shieldingRate(int rank){
        switch (rank){
            case 1: return 4;
            case 2: return 1;
            case 3: return 0;
        }
        return 0;
    }

    private int healingRate(int rank){
        switch (rank){
            case 1: return 5;
            case 2: return 1;
            case 3: return 20;
        }
        return 0;
    }

    private int minDamage(int rank){
        switch (rank){
            case 1: return 10;
            case 2: return 50;
            case 3: return 3;
        }
        return 0;
    }

    private int maxDamage(int rank){
        switch (rank){
            case 1: return 50;
            case 2: return 150;
            case 3: return 25;
        }
        return 0;
    }

    private int manaSteal(int rank){
        switch (rank){
            case 1: return 5;
            case 2: return 2;
            case 3: return 15;
        }
        return 0;
    }

    private int duration(int rank){
        switch (rank){
            case 1: return 60;
            case 2: return 20;
            case 3: return 500;
        }
        return 0;
    }

    private int cooldown(int rank){
        switch (rank){
            case 1: return 300;
            case 2: return 500;
            case 3: return 1000;
        }
        return 0;
    }

    @Override
    public int manaCost(int rank) {
        switch (rank){
            case 1: return 25;
            case 2: return 40;
            case 3: return 20;
        }
        return 0;
    }

    @Override
    public boolean tryToZap(Hero owner) {
        if (owner.buff(HolyAuraCD.class) != null){
            GLog.warning( Messages.get(this, "no_magic") );
            return false;
        }
        return super.tryToZap(owner);
    }

    public String spellDesc() {
        return Messages.get(this, "desc", shieldingRate(rank()), healingRate(rank()), duration(rank()));
    }

    @Override
    public String spellRankMessage(int rank) {
        return Messages.get(this, "rank"+ (rank == 3 ? "3" : ""),
                shieldingRate(rank), healingRate(rank), manaSteal(rank), minDamage(rank), maxDamage(rank), duration(rank), cooldown(rank));
    }
}
