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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Block;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

import java.text.DecimalFormat;

public class Barrier extends ConjurerSpell {

    {
        image = ItemSpriteSheet.SHIELD;
    }

    private static final int BLOCK_DURATION = 12;

    @Override
    public void effect(Ballistica trajectory) {
        Char ch = Actor.findChar(trajectory.collisionPos);
        if (ch != null && ch.alignment != Char.Alignment.ALLY){
            Sample.INSTANCE.play(Assets.Sounds.ATK_SPIRITBOW);
            if (rank() < 3) {
                int healing = heal(ch, rank());
                Buff.affect(ch, com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier.class).setShield(healing);
            }
            else {
                Buff.affect(ch, Block.class, BLOCK_DURATION);
            }

            ch.sprite.burst(0xFFFFFFFF, buffedLvl() / 2 + 2);
        }
    }

    @Override
    public int manaCost(int rank) {
        switch (rank){
            case 1: return 3;
            case 2: return 15;
            case 3: return 20;
        }
        return 0;
    }

    private int heal(Char ch, int rank){
        switch (rank){
            case 1: return 5 + ch.HT / 4;
            case 2: return 25;
//            case 3: return (int) (40 + ch.HT * 1.25f);
        }
        return 0;
    }

    private int partialHeal(int rank){
        switch (rank){
            case 1: return 25;
            case 2:
            case 3:
                return 0;
        }
        return 0;
    }

    private int intHeal(int rank){
        switch (rank){
            case 1: return 5;
            case 2: return 25;
            case 3: return 0;
        }
        return 0;
    }


    @Override
    public String desc() {
        return Messages.get(this, "desc", intHeal(rank()), partialHeal(rank()), manaCost());
    }

    @Override
    public String spellRankMessage(int rank) {
        if (rank == 3){
            return Messages.get(this, "rank3", BLOCK_DURATION);
        }
        return Messages.get(this, "rank", intHeal(rank), new DecimalFormat("#.##").format( partialHeal(rank)));
    }
}
