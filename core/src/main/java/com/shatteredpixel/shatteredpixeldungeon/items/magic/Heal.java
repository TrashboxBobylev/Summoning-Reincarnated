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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

import java.text.DecimalFormat;

public class Heal extends ConjurerSpell {

    {
        image = ItemSpriteSheet.HEAL;
    }

    @Override
    public void effect(Ballistica trajectory) {
        Char ch = Actor.findChar(trajectory.collisionPos);
        if (ch != null && ch.alignment == Char.Alignment.ALLY){
            Sample.INSTANCE.play(Assets.Sounds.DRINK);
            int healing = heal(ch, rank());

            Regeneration.regenerate(ch, healing, true, false);

            ch.sprite.emitter().burst(Speck.factory(Speck.STEAM), 5);
            ch.sprite.burst(0xFFFFFFFF, buffedLvl() / 2 + 2);
        }
    }

    @Override
    public int manaCost(int rank) {
        switch (rank){
            case 1: return 1;
            case 2: return 4;
            case 3: return 8;
        }
        return 0;
    }

    private int heal(Char ch, int rank){
        if (ch.buff(Shocker.NoHeal.class) != null) return 0;
        switch (rank){
            case 1: return intHeal(rank) + ch.HT / 15;
            case 2: return intHeal(rank) + ch.HT / 6;
            case 3: return intHeal(rank) + ch.HT / 5;
        }
        return 0;
    }

    private int intHeal(int rank){
        switch (rank){
            case 1: return 5;
            case 2: return 10;
            case 3: return 14;
        }
        return 0;
    }

    private float partialHeal(int rank){
        switch (rank){
            case 1: return 6.6f;
            case 2: return 16.6f;
            case 3: return 20f;
        }
        return 0;
    }


    @Override
    public String desc() {
        return Messages.get(this, "desc", intHeal(rank()), new DecimalFormat("#.##").format( partialHeal(rank())), manaCost());
    }

    @Override
    public String spellRankMessage(int rank) {
        return Messages.get(this, "rank", intHeal(rank), new DecimalFormat("#.##").format( partialHeal(rank)));
    }
}
