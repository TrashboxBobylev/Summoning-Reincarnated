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

package com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.AttunementItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfMastery;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class ElixirOfMysticProwess extends Elixir {
    {
        image = ItemSpriteSheet.ATT_MASTERY;

        unique = true;
    }

    @Override
    public void apply(Hero hero) {

    }

    @Override
    //need to override drink so that time isn't spent right away
    protected void drink(final Hero hero) {
        GameScene.selectItem(itemSelector);
    }

    @Override
    public int value() {
        return quantity * 75;
    }

    @Override
    public boolean isFaceProtected() {
        return true;
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{ElixirOfAttunement.class};
            inQuantity = new int[]{1};

            cost = 10;

            output = ElixirOfMysticProwess.class;
            outQuantity = 1;
        }

    }

    protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

        @Override
        public String textPrompt() {
            return Messages.get(PotionOfMastery.class, "prompt");
        }

        @Override
        public boolean itemSelectable(Item item) {
            return item instanceof AttunementItem && !((AttunementItem) item).hasMastery();
        }

        @Override
        public void onSelect(Item item) {

            if (item == null){
                GameScene.show(new WndOptions(new ItemSprite(ElixirOfMysticProwess.this),
                        Messages.titleCase(name()),
                        Messages.get(ExoticPotion.class, "warning"),
                        Messages.get(ExoticPotion.class, "yes"),
                        Messages.get(ExoticPotion.class, "no") ) {
                    @Override
                    protected void onSelect( int index ) {
                        switch (index) {
                            case 0:
                                curUser.spendAndNext(1f);
                                break;
                            case 1:
                                GameScene.selectItem(itemSelector);
                                break;
                        }
                    }
                    public void onBackPressed() {}
                } );
            } else if (item != null) {

                ((AttunementItem)item).giveMastery();
                GLog.p( Messages.get(ElixirOfMysticProwess.class, "att_easier") );
                updateQuickslot();

                Sample.INSTANCE.play( Assets.Sounds.DRINK );
                curUser.sprite.operate(curUser.pos);

                curItem.detach(curUser.belongings.backpack);

                if (!anonymous) {
                    Catalog.countUse(ElixirOfMysticProwess.class);
                    if (Random.Float() < talentChance) {
                        Talent.onPotionUsed(curUser, curUser.pos, talentFactor);
                    }
                }
            }

        }
    };
}
