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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.NecromancyCD;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.NecromancyStat;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class Necro extends ConjurerSpell {

    {
        image = ItemSpriteSheet.CLONE;
    }

    @Override
    public void effect(Ballistica trajectory) {
        Char ch = Actor.findChar(trajectory.collisionPos);
        if (ch != null && ch.alignment == Char.Alignment.ALLY
                    && Dungeon.hero.buff(NecromancyCD.class) == null && ch.isAlive()){
            Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
            int healing = heal(rank());
            ch.sprite.emitter().burst(Speck.factory(Speck.STEAM), 20);
            Buff.affect(ch, NecromancyStat.class, 1000f).level = healing;
            Buff.affect(Dungeon.hero, NecromancyCD.class, cd(rank()));

            ch.sprite.burst(0xFFFFFFFF, buffedLvl() / 2 + 2);
        }
    }

    @Override
    public int manaCost(int rank) {
        switch (rank){
            case 1: return 9;
            case 2: return 12;
            case 3: return 30;
        }
        return 0;
    }

    private int heal(int rank){
        switch (rank){
            case 1: return 4;
            case 2: return 10;
            case 3: return 21;
        }
        return 0;
    }

    private int cd(int rank){
        switch (rank){
            case 1: return 200;
            case 2: return 640;
            case 3: return 800;
        }
        return 0;
    }


    @Override
    public String desc() {
        return Messages.get(this, "desc", heal(rank()), cd(rank()), manaCost());
    }

    @Override
    public String spellRankMessage(int rank) {
        return Messages.get(this, "rank", heal(rank), cd(rank));
    }
}
