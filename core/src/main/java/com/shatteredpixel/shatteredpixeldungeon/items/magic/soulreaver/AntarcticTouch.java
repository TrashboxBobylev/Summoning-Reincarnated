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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostBurn;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.ConjurerSpell;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Callback;

import java.text.DecimalFormat;

public class AntarcticTouch extends ConjurerSpell {

    {
        image = ItemSpriteSheet.SR_OFFENSE;
        usesTargeting = true;
    }

    @Override
    public void effect(Ballistica trajectory) {
        Char ch = Actor.findChar(trajectory.collisionPos);

        if (ch != null){
            Buff.affect(ch, FrostBurn.class).reignite(ch, frostburn(rank()));
            Buff.affect(ch, Minion.ReactiveTargeting.class, 10f);
            Buff.affect(ch, Minion.UniversalTargeting.class, 15f);
            for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                if (mob instanceof Minion && rank() < 3){
                    mob.aggro(ch);
                    mob.beckon(trajectory.collisionPos);
                }
            }
        }
    }

    private float frostburn(int rank){
        switch (rank){
            case 1: return 7f;
            case 2: return 20f;
            case 3: return 40f;
        }
        return 0f;
    }

    @Override
    public int manaCost(int rank) {
        switch (rank){
            case 1: return 15;
            case 2: return 25;
            case 3: return 25;
        }
        return 0;
    }

    public String desc() {
        return Messages.get(this, "desc", new DecimalFormat("#.#").format(frostburn(rank())), manaCost());
    }

    @Override
    public String spellRankMessage(int rank) {
        return Messages.get(this, "rank"+ (rank == 3 ? "3" : ""), new DecimalFormat("#.#").format(frostburn(rank)), manaCost());
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar(curUser.sprite.parent,
                MagicMissile.FROST,
                curUser.sprite,
                bolt.collisionPos,
                callback);
    }
}
