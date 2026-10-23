/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Summoning Pixel Dungeon Reincarnated
 * Copyright (C) 2023-2026 Trashbox Bobylev
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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentIcon;
import com.watabou.noosa.Image;

public enum HeroPerk {
    WEAPON_ABILITIES(0xcc33ff){
        @Override
        public Image icon() {
            return new TalentIcon(Talent.SWIFT_EQUIP);
        }
    },
    WARRIOR_SEAL(0x9a0000){
        @Override
        public Image icon() {
            return new ItemSprite(ItemSpriteSheet.SEAL);
        }

        @Override
        public void onInitialization(Hero hero) {
            if (hero.belongings.armor != null){
                hero.belongings.armor.affixSeal(new BrokenSeal());
                Catalog.setSeen(BrokenSeal.class); //as it's not added to the inventory
            }
        }
    }
    ;

    public int color = -1;

    HeroPerk(int color){
        this.color = color;
    }

    public Image icon(){
        Image icon = Icons.get(Icons.TALENT);
        if (color != -1)
            icon.hardlight(color);
        return icon;
    }

    public String desc(){
        return Messages.get(this, name() + ".desc");
    }

    public void onInitialization(Hero hero){

    }

    public static boolean menuAvailable(){
        return Dungeon.mode == Dungeon.GameMode.RANDOM_HERO;
    }
}
