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
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ConjurerBook;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.BeamOfAffection;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.EnergizedRenewal;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.StarBlazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentIcon;
import com.watabou.noosa.Image;

import java.util.EnumSet;

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
    },
    EQUIPPABLE_WANDS(0xffff00){
        @Override
        public Image icon() {
            return new ItemSprite(ItemSpriteSheet.MAGES_STAFF);
        }
    },
    HUNTRESS_BOW(0x99ffff){
        @Override
        public Image icon() {
            return new ItemSprite(ItemSpriteSheet.SPIRIT_BOW);
        }

        @Override
        public void onInitialization(Hero hero) {
            SpiritBow bow = new SpiritBow();
            bow.identify().collect();
        }
    },
    FURROWED_GRASS(0x005826){
        @Override
        public Image icon() {
            return Icons.GRASS.get();
        }
    },
    CONJURER_MAGIC(0x4c51ad){
        @Override
        public Image icon() {
            return new ItemSprite(ItemSpriteSheet.BEHAVIOR_PASSIVE);
        }

        @Override
        public void onInitialization(Hero hero) {
            ConjurerBook book = new ConjurerBook();
            book.collect();
            StarBlazing star = new StarBlazing();
            star.collect();
            EnergizedRenewal energizedRenewal = new EnergizedRenewal();
            energizedRenewal.collect();
            new BeamOfAffection().collect();
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

    public static EnumSet<HeroPerk> omniheroPerks(){
        return EnumSet.of(
            WARRIOR_SEAL,
            EQUIPPABLE_WANDS,
            WEAPON_ABILITIES,
            HUNTRESS_BOW,
            CONJURER_MAGIC
        );
    }
}
