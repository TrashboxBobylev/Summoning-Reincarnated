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

package com.shatteredpixel.shatteredpixeldungeon.items.magic.knight;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.ConjurerSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.ToyKnife;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.text.DecimalFormat;

public class DirectingPulse extends ConjurerSpell {

    {
        image = ItemSpriteSheet.PUNCH;
        alignment = Alignment.OFFENSIVE;
        usesTargeting = true;
    }

    @Override
    public void effect(Ballistica trajectory) {
        Char ch = Actor.findChar(trajectory.collisionPos);
        if (ch != null && (ch.alignment == Char.Alignment.ENEMY || Dungeon.hero.hasTalent(Talent.SPIRITUAL_RESTOCK))) {
            Buff.affect(ch, ToyKnife.SoulGain.class, buff(rank()));
            ch.sprite.burst(0xFFFFFFFF, buffedLvl() / 2 + 2);
            Buff.affect(ch, Minion.ReactiveTargeting.class, 10f);
            if (isEmpowered()){
                Buff.affect(ch, Vulnerable.class, buff(rank()) / 2);
            }
        }
    }

    @Override
    public int manaCost(int rank) {
        switch (rank){
            case 1: return 5;
            case 2: return 3;
            case 3: return 0;
        }
        return 0;
    }

    private float buff(int rank){
        switch (rank){
            case 1: return 12.0f;
            case 2: return 6.0f;
            case 3: return 2f;
        }
        return 0f;
    }

    @Override
    public String spellDesc() {
        return Messages.get(this, "desc", new DecimalFormat("#.#").format(buff(rank())));
    }

    @Override
    public String empowermentDesc() {
        return Messages.get(this, "desc_empower", new DecimalFormat("#.#").format(buff(rank())/2));
    }

    @Override
    public String spellRankMessage(int rank) {
        return Messages.get(this, "rank", new DecimalFormat("#.#").format(buff(rank)));
    }

    @Override
    public String empowermentRankDesc(int rank) {
        return Messages.get(this, "rank_empower", new DecimalFormat("#.#").format(buff(rank)));
    }
}
