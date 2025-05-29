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

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chungus;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TimedShrink;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.ConjurerSpell;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class TransmogrificationWand extends ConjurerSpell {

    {
        image = ItemSpriteSheet.SR_MAGICAL;
        alignment = Alignment.BENEFICIAL;
        usesTargeting = true;
    }

    @Override
    public void effect(Ballistica trajectory) {
        Char ch = Actor.findChar(trajectory.collisionPos);
        if (ch != null && !(ch instanceof Hero)) {
            CellEmitter.center( trajectory.collisionPos ).burst( MagicMissile.WardParticle.UP, Random.IntRange( 8, 15 ) );
            if (ch.alignment == Char.Alignment.ALLY) {
                Buff.affect(ch, Chungus.class, enlargement(rank()));
            } else if (ch.alignment == Char.Alignment.ENEMY) {
                Buff.affect(ch, TimedShrink.class, shrinking(rank()));
            }
        }
    }

    @Override
    public Alignment alignment(int rank) {
        switch (rank){
            case 1: return Alignment.NEUTRAL;
            case 2: return Alignment.BENEFICIAL;
            case 3: return Alignment.OFFENSIVE;
        }
        return super.alignment(rank);
    }

    private int enlargement(int rank){
        if (isEmpowered()){
            switch (rank){
                case 1: return 6;
                case 2: return 16;
                case 3: return 4;
            }
        }
        switch (rank){
            case 1: return 5;
            case 2: return 13;
            case 3: return 5;
        }
        return 0;
    }

    private int shrinking(int rank){
        if (isEmpowered()){
            switch (rank){
                case 1: return 6;
                case 2: return 5;
                case 3: return 19;
            }
        }
        switch (rank){
            case 1: return 5;
            case 2: return 7;
            case 3: return 15;
        }
        return 0;
    }

    @Override
    public int manaCost(int rank) {
        switch (rank){
            case 1: return 8;
            case 2: return 12;
            case 3: return 15;
        }
        return 0;
    }

    @Override
    public String empowermentDesc() {
        return Messages.get(this, "desc_empower_" + alignment().name());
    }

    public String spellDesc() {
        return Messages.get(this, "desc", shrinking(rank()), enlargement(rank()));
    }

    @Override
    public String spellRankMessage(int rank) {
        return Messages.get(this, "rank", shrinking(rank), enlargement(rank));
    }

}
