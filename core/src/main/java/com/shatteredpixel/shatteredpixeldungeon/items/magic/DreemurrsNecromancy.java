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

package com.shatteredpixel.shatteredpixeldungeon.items.magic;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.NecromancyStat;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

import java.text.DecimalFormat;

public class DreemurrsNecromancy extends ConjurerSpell {

    {
        image = ItemSpriteSheet.CLONE;
        alignment = Alignment.BENEFICIAL;
    }

    @Override
    public void effect(Ballistica trajectory) {
        Char ch = Actor.findChar(trajectory.collisionPos);
        if (ch != null && ch.isAlive() && ch.alignment != Char.Alignment.NEUTRAL){
            Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
            ch.sprite.emitter().burst(Speck.factory(Speck.STEAM), 20);
            Buff.affect(ch, NecromancyStat.class).level = rank();

            ch.sprite.burst(0xFFFFFFFF, buffedLvl() / 2 + 2);
        }
    }

    @Override
    public int manaCost(int rank) {
        switch (rank){
            case 1: return 12;
            case 2: return 24;
            case 3: return 0;
        }
        return 0;
    }

    public static float passiveManaDrain(int rank){
        switch (rank){
            case 1:
                return 0.75f;
            case 2:
                return 0.2f;
            case 3:
                return 2f;
        }
        return 0;
    }

    public static float activeManaDrain(int rank){
        switch (rank){
            case 2:
                return 0.1f;
            case 3:
                return 0.2f;
        }
        return 0;
    }


    @Override
    public String spellDesc() {
        return Messages.get(this, "desc" + (rank() == 3 ? "3" : ""), new DecimalFormat("#.##").format(passiveManaDrain(rank())), new DecimalFormat("#.##").format(activeManaDrain(rank())));
    }

    @Override
    public String spellRankMessage(int rank) {
        return Messages.get(this, "rank" + (rank == 3 ? "3" : ""), new DecimalFormat("#.##").format(passiveManaDrain(rank)), new DecimalFormat("#.##").format(activeManaDrain(rank)));
    }

    @Override
    public String empowermentRankDesc(int rank) {
        return Messages.get(this, "rank_empower");
    }
}
