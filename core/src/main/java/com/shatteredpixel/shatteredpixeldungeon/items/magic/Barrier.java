/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2022 Evan Debenham
 *
 * Summoning Pixel Dungeon
 * Copyright (C) 2019-2022 TrashboxBobylev
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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.WardingWraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class Barrier extends ConjurerSpell {

    {
        image = ItemSpriteSheet.SHIELD;
    }

    @Override
    public void effect(Ballistica trajectory) {
        Char ch = Actor.findChar(trajectory.collisionPos);
        if (ch instanceof Minion || ch instanceof DriedRose.GhostHero || ch instanceof WandOfLivingEarth.EarthGuardian ||
                ch instanceof WandOfWarding.Ward || (ch instanceof WardingWraith && ch.alignment == Char.Alignment.ALLY)){
            Sample.INSTANCE.play(Assets.Sounds.ATK_SPIRITBOW);
            if (level() < 2) {
                int healing = heal(ch);
                Buff.affect(ch, com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier.class).setShield(healing);
            }
            else {
                Buff.affect(ch, Block.class, 12f);
            }

            ch.sprite.burst(0xFFFFFFFF, buffedLvl() / 2 + 2);
        }
    }

    @Override
    public int manaCost() {
        switch (level()){
            case 1: return 15;
            case 2: return 20;
        }
        return 3;
    }

    private int heal(Char ch){
        switch (level()){
            case 1: return 25;
            case 2: return (int) (40 + ch.HT * 1.25f);
        }
        return 5 + ch.HT / 4;
    }

    private int partialHeal(){
        switch (level()){
            case 1:
            case 2:
                return 0;
        }
        return 25;
    }

    private int intHeal(){
        switch (level()){
            case 1: return 25;
            case 2: return 0;
        }
        return 5;
    }


    @Override
    public String desc() {
        return Messages.get(this, "desc", intHeal(), partialHeal(), manaCost());
    }
}
