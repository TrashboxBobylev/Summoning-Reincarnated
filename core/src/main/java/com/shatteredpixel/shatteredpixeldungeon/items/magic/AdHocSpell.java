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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.knight.Concentration;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

public abstract class AdHocSpell extends ConjurerSpell {

    @Override
    public void effect(Ballistica trajectory) {
        //do nothing
    }

    abstract public boolean effect(Hero hero);

    @Override
    public void execute(final Hero hero, String action ) {

        GameScene.cancel();

        if (action.equals( AC_ZAP )) {

            if (tryToZap(Dungeon.hero)) {
                curUser = Dungeon.hero;
                curItem = this;
                if (effect(curUser)) {
                    curUser.mana -= manaCost();
                    Invisibility.dispel();
                    curUser.busy();
                    if (!(this instanceof Concentration))
                        curUser.spendAndNext(1f);
                }
                updateQuickslot();
            }

        }
    }
}