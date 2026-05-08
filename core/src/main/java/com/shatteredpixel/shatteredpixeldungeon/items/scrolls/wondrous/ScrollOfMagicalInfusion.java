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

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls.wondrous;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.WeaponEnchantable;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class ScrollOfMagicalInfusion extends WondrousScroll {
    {
        icon = ItemSpriteSheet.Icons.SCROLL_MAGINFUSION;
    }

    @Override
    public void doRead() {
        detach(curUser.belongings.backpack);

        for (Item item : Dungeon.hero.belongings) {
            if (item instanceof EquipableItem && item.isEquipped(Dungeon.hero) && ScrollOfEnchantment.enchantable(item)){
                item.cursed = false;
                item.cursedKnown = true;
                if (item instanceof WeaponEnchantable) {

                    ((WeaponEnchantable)item).enchant();

                } else if (item instanceof Stylus.Inscribable && ((Stylus.Inscribable) item).isInscribable()) {

                    ((Stylus.Inscribable)item).inscribe();

                }
                Dungeon.hero.sprite.emitter().start( Speck.factory( Speck.LIGHT ), 0.1f, 5 );
                Enchanting.show( Dungeon.hero, item );

                if (item instanceof WeaponEnchantable) {
                    GLog.p(Messages.get(StoneOfEnchantment.class, "weapon"));
                } else if (item instanceof CloakOfShadows) {
                    GLog.positive(Messages.get(StoneOfEnchantment.class, "cloak"));
                } else {
                    GLog.p(Messages.get(StoneOfEnchantment.class, "armor"));
                }
            }
        }

        identify();
        readAnimation();
    }
}
