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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.GoatClone;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.AdHocSpell;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class EnergizedBlast extends AdHocSpell {

    {
        image = ItemSpriteSheet.BOOM;
        alignment = Alignment.OFFENSIVE;
    }

    @Override
    public boolean effect(Hero hero) {
        GoatClone clone = GoatClone.findClone();
        if (clone != null){
            hero.spendAndNext(1f);
            Sample.INSTANCE.play(Assets.Sounds.BLAST);
            ArrayList<Integer> cells = new ArrayList<>();
            PathFinder.buildDistanceMap(clone.pos, BArray.not( Dungeon.level.solid, null ), isEmpowered() ? 2 : 1 );
            for (int i = 0; i < PathFinder.distance.length; i++) {
                if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                    cells.add(i);
                }
            }
            for (int pos: cells){
                CellEmitter.get(pos).burst(MagicMissile.WhiteParticle.FACTORY, 12);
                Char ch = Actor.findChar(pos);
                if (ch != null && ch != Dungeon.hero){
                    ch.damage((int) (Bomb.damageRoll()*damage(type())), clone);
                    Buff.affect(ch, Minion.ReactiveTargeting.class, 10f);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private float damage(int rank){
        switch (rank){
            case 1: return 1f;
            case 2: return 0.5f;
            case 3: return 0.25f;
        }
        return 0f;
    }

    @Override
    public int manaCost(int type) {
        switch (type){
            case 1: return 30;
            case 2: return 18;
            case 3: return 10;
        }
        return 0;
    }

    public String spellDesc() {
        return Messages.get(this, "desc", GameMath.printAverage((int)(damage(type())*Bomb.minDamage()), (int)(damage(type())*Bomb.maxDamage())));
    }

    @Override
    public String spellTypeMessage(int type) {
        return Messages.get(this, "type", GameMath.printAverage((int)(damage(type)*Bomb.minDamage()), (int)(damage(type)*Bomb.maxDamage())));
    }
}
