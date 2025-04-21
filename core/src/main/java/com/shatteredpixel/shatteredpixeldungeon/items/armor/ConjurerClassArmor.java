/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.armor;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Rankable;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class ConjurerClassArmor extends ClassArmor implements Rankable, ConjurerSet {
    {
        image = ItemSpriteSheet.ARMOR_T6CONJURER;

        tier = 1;
    }

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.remove(AC_DROP);
        actions.remove(AC_THROW);
        actions.remove(AC_TRANSFER);
        return actions;
    }

    @Override
    public int DRMax(int lvl) {
        return (int) (super.DRMax(lvl)*defenseLevel(rank()-1));
    }

    @Override
    public int DRMin(int lvl) {
        return (int) (super.DRMin(lvl)*defenseLevel(rank()-1));
    }

    @Override
    public int rank() {
        return level()+1;
    }

    @Override
    public void rank(int rank) {
        level(rank-1);
    }

    @Override
    public int visiblyUpgraded() {
        return 0;
    }

    @Override
    public int level() {
        return Dungeon.hero == null ? 0 : Dungeon.hero.ATU()-1;
    }

    @Override
    public int STRReq(int lvl) {
        return 9;
    }

    @Override
    public boolean keptThroughLostInventory() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }


    public String getRankMessage(int rank) {
        return Messages.get(this, "rank" + rank, DRMin(rank), DRMax(rank));
    }
}
