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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArcaneArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.ArmoredShielding;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.ConjurerSpell;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Defense extends ConjurerSpell {

    {
        image = ItemSpriteSheet.SR_DEFENSE;
    }

    @Override
    public void effect(Ballistica trajectory) {
        Char ch = Actor.findChar(trajectory.collisionPos);
        if (ch instanceof Minion) {
            if (ch.buff(ArmoredShielding.class) == null) {
                Buff.affect(ch, ArmoredShielding.class, 1000000f);
                Buff.affect(ch, Barkskin.class).set(defenseEarthValue(rank()), defenseEarthTime(rank()));
                Buff.affect(ch, ArcaneArmor.class).set(defenseArcaneValue(rank()), defenseArcaneTime(rank()));
            } else {
                ch.buff(ArmoredShielding.class).detach();
                Buff.affect(ch, Barkskin.class).detach();
                Buff.affect(ch, ArcaneArmor.class).detach();
                Dungeon.hero.mana = Math.min(Dungeon.hero.maxMana(), Dungeon.hero.mana + manaCost());
            }
        }
    }

    private int defenseEarthTime(int rank){
        switch (rank){
            case 1: return 30;
            case 2: return 8;
            case 3: return 100;
        }
        return 0;
    }

    private int defenseArcaneTime(int rank){
        switch (rank){
            case 1: return 40;
            case 2: return 12;
            case 3: return 120;
        }
        return 0;
    }

    private int defenseEarthValue(int rank){
        switch (rank){
            case 1: return 6 + Dungeon.hero.lvl*3/4;
            case 2: return 10 + Dungeon.hero.lvl;
            case 3: return 5;
        }
        return 0;
    }

    private int defenseArcaneValue(int rank){
        switch (rank){
            case 1: return 3 + Dungeon.hero.lvl/3;
            case 2: return 7 + Dungeon.hero.lvl/2;
            case 3: return 3;
        }
        return 0;
    }

    @Override
    public int manaCost(int rank) {
        switch (rank){
            case 1: return 30;
            case 2: return 35;
            case 3: return 5;
        }
        return 0;
    }

    public String desc() {
        return Messages.get(this, "desc", defenseEarthValue(rank()), defenseArcaneValue(rank()), manaCost());
    }

    @Override
    public String spellRankMessage(int rank) {
        return Messages.get(this, "rank", defenseEarthValue(rank), defenseEarthTime(rank), defenseArcaneValue(rank), defenseArcaneTime(rank));
    }
}
