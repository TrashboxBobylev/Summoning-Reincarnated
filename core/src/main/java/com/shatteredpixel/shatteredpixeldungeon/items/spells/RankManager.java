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

package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Rankable;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.InventoryScroll;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndRankManager;

// it is not an inventory spell code-wise, but it is usable like one;
// we just need for it to behave like enchantment scroll, which means
// creating our own item selector without spending turn right away
// and such
public class RankManager extends Spell {

    {
        image = ItemSpriteSheet.RANK_MANAGER;
        unique = true;
    }

    private static ItemSprite.Glowing TEAL = new ItemSprite.Glowing( 0x00AAFF, 0.75f );

    @Override
    public ItemSprite.Glowing glowing() {
        return TEAL;
    }

    private void confirmCancellation() {
        GameScene.show( new WndOptions(new ItemSprite(this),
                Messages.titleCase(name()),
                Messages.get(RankManager.class, "warning"),
                Messages.get(InventoryScroll.class, "yes"),
                Messages.get(InventoryScroll.class, "no") ) {
            @Override
            protected void onSelect( int index ) {
                switch (index) {
                    case 0:
                        curUser.spendAndNext( 1f );
                        break;
                    case 1:
                        GameScene.selectItem(itemSelector);
                        break;
                }
            }
            public void onBackPressed() {}
        } );
    }

    public void reShowSelector(){
        curItem = this;
        GameScene.selectItem(itemSelector);
    }

    @Override
    protected void onCast(Hero hero) {
        GameScene.selectItem( itemSelector );
    }

    protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

        @Override
        public String textPrompt() {
            return Messages.get(RankManager.class, "inv_title");
        }

        @Override
        public Class<?extends Bag> preferredBag(){
            return Belongings.Backpack.class;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return item instanceof Rankable;
        }

        @Override
        public void onSelect(final Item item) {

            if (item instanceof Rankable){
                GameScene.show(new WndRankManager(((RankManager)curItem), item));
            }
        }
    };

    @Override
    public boolean isFaceProtected() {
        return true;
    }
}
