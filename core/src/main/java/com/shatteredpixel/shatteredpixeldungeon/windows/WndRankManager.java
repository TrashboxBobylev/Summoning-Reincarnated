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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Rankable;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.RankManager;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class WndRankManager extends WndTabbed {

    protected static final int WIDTH_MIN    = 120;
    protected static final int WIDTH_MAX    = 220;
    protected static final int GAP	= 2;
    protected static final int BUTTON_HEIGHT = 16;

    private ArrayList<RenderedTextBlock> texts = new ArrayList<>();
    private RedButton btnSwitch;

    private static Item rankedItem;
    private static Item ranker;
    protected static int selectedRank;

    public WndRankManager(RankManager rankManager, Item item){
        super();

        rankedItem = item;
        ranker = rankManager;

        int width = WIDTH_MIN;

        IconTitle titlebar = new IconTitle( new ItemSprite(item), Messages.titleCase(Messages.get(this, "rank", item.trueName())) );
        titlebar.setRect( 0, 0, width, 0 );
        add(titlebar);

        RenderedTextBlock largest = null;
        for (int i = 0; i < 3; i++){
            RenderedTextBlock text = PixelScene.renderTextBlock( 6 );
            text.text( Messages.get(RankManager.class, "change_desc") + "\n\n"
                    + ((Rankable)rankedItem).getRankMessage(i+1), width );
            text.setPos( titlebar.left(), titlebar.bottom() + 2*GAP );
            add( text );
            texts.add(text);

            if (largest == null || text.height() > largest.height()){
                largest = text;
            }

            int finalI = i;
            add(new LabeledTab(Messages.get(Rankable.class, "rank" + (finalI + 1))){
                @Override
                protected void select(boolean value) {
                    super.select( value );
                    texts.get(finalI).visible = value;
                }
            });
        }

        while (PixelScene.landscape()
                && largest.bottom() > (PixelScene.MIN_HEIGHT_L - 20 - BUTTON_HEIGHT)
                && width < WIDTH_MAX){
            width += 20;
            titlebar.setRect(0, 0, width, 0);

            largest = null;
            for (RenderedTextBlock text : texts){
                text.setPos( titlebar.left(), titlebar.bottom() + 2*GAP );
                text.maxWidth(width);
                if (largest == null || text.height() > largest.height()){
                    largest = text;
                }
            }
        }

        bringToFront(titlebar);

        btnSwitch = new RedButton( Messages.get(this, "switch_rank") ) {
            @Override
            protected void onClick() {
                hide();
                ((Rankable)rankedItem).rank(selectedRank);
                GLog.p(Messages.get(RankManager.class, "switch", Rankable.getRankString(selectedRank)));
                Invisibility.dispel();
                Dungeon.hero.spend( 1f );
                Dungeon.hero.busy();
                ((HeroSprite)Dungeon.hero.sprite).read();

                Sample.INSTANCE.play( Assets.Sounds.READ );
                Dungeon.hero.sprite.burst(Rankable.getRankColor(selectedRank), 10);
                ranker.detach(Dungeon.hero.belongings.backpack);
                Enchanting.show(Dungeon.hero, new Item(){
                    @Override
                    public int image() {
                        return rankedItem.image();
                    }

                    @Override
                    public ItemSprite.Glowing glowing() {
                        return new ItemSprite.Glowing(Rankable.getRankColor(selectedRank));
                    }
                });
                Item.updateQuickslot();

                rankedItem = null;
                ranker = null;
            }
        };
        btnSwitch.setRect(0, largest.bottom()+2*GAP, width, BUTTON_HEIGHT);
        add(btnSwitch);

        btnSwitch.icon(new ItemSprite(ItemSpriteSheet.RANK_MANAGER));
        btnSwitch.enable(Dungeon.hero.ready);

        resize( width, (int)btnSwitch.bottom() + 2 );

        layoutTabs();
        selectedRank = ((Rankable) rankedItem).rank();
        btnSwitch.enable(false);
        select(selectedRank-1);
    }

    @Override
    public void select(Tab tab) {
        super.select(tab);
        selectedRank = tabs.indexOf(tab)+1;
        btnSwitch.enable(((Rankable)rankedItem).rank() != selectedRank);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ((RankManager)ranker).reShowSelector();
    }
}
