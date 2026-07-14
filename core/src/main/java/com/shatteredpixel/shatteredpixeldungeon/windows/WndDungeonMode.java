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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;

public class WndDungeonMode extends Window {
    private static final int WIDTH_P = 120;
    private static final int WIDTH_L = 160;

    private static final int MARGIN  = 2;

    private ScrollPane modeList;
    private Dungeon.GameMode chosenGameMode;
    private Dungeon.GameMode[] possibleModes;
    private float timer;
    private RenderedTextBlock modeTitle;
    private RenderedTextBlock info;
    private RenderedTextBlock score;
    private RedButton btnSetMode;
    private RenderedTextBlock title;

    private Component descContainer;

    public WndDungeonMode( ){
        this(Dungeon.GameMode.values());
    }

    public WndDungeonMode(Dungeon.GameMode... possibleModes){
        super();

        chosenGameMode = SPDSettings.mode();
        this.possibleModes = possibleModes;

        layout();
    }

    private void layout() {
        int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

        float pos = MARGIN;
        title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(this, "title")), 9);
        title.hardlight(TITLE_COLOR);
        title.setPos((width- title.width())/2, pos);
        title.maxWidth(width - MARGIN * 2);
        add(title);

        if (descContainer != null){
            descContainer.clear();
        } else {
            descContainer = new Component();
            add(descContainer);
        }
        if (modeList == null) {
            modeList = new ScrollPane(new Component());
            add(modeList);
        }

        pos = title.bottom() - MARGIN*4;
        Component content = modeList.content();
        if (content.length == 0) {
            float xItem = 0, yItem = pos;

            for (Dungeon.GameMode mode : possibleModes) {
                Image ic = Icons.get(mode.icon);

                ModeButton moveBtn = new ModeButton(mode);
                moveBtn.icon(ic);
                moveBtn.setSize(width, moveBtn.reqHeight());
                moveBtn.setRect(xItem, yItem, 20, 20);
                moveBtn.enable(true);
                content.add(moveBtn);
                yItem += moveBtn.height() + MARGIN;
            }
            content.setSize(20, yItem);
        }

        modeTitle = PixelScene.renderTextBlock( "_" + chosenGameMode.toString() + "_", 9 );
        modeTitle.maxWidth((int) (width - MARGIN * 3 - content.width()));
        modeTitle.setPos(((width-(content.width()+MARGIN*2)- modeTitle.width())/2)+content.width()+MARGIN*2, pos + 20);
        descContainer.add( modeTitle );

        pos += modeTitle.height() + MARGIN*3 + 20;
        addLine(pos, descContainer);

        info = PixelScene.renderTextBlock( chosenGameMode.desc(), 7 );
        info.maxWidth((int) (width - MARGIN * 3 - content.width()));
        info.setPos(content.width() + MARGIN*2, pos + MARGIN*2);
        descContainer.add( info );

        pos += info.height() + MARGIN*4;
        addLine(pos, descContainer);

        score = PixelScene.renderTextBlock( chosenGameMode.score(), 7 );
        score.maxWidth((int) (width - MARGIN * 3 - content.width()));
        score.setPos(content.width() + MARGIN*2, pos + MARGIN*2);
        descContainer.add( score );

        pos += score.height() + MARGIN*5;

        btnSetMode = new RedButton(Messages.get(this, "change_mode")){
            @Override
            protected void onClick() {
                hide();
                SPDSettings.mode(chosenGameMode);
                ((HeroSelectScene)Game.scene()).startBtn.icon(chosenGameMode.icon.get());
            }
        };
        btnSetMode.setRect(content.width() + MARGIN*2, pos, width - MARGIN*3 - content.width(), btnSetMode.reqHeight()*1.5f);
        descContainer.add(btnSetMode);

        pos += btnSetMode.height() + MARGIN*2 + 40;

        resize(width, (int) pos);

        modeList.setRect(0, title.bottom() + MARGIN*2, content.width() + 2, height - MARGIN*5.5f);
        descContainer.setRect(0, 0, length, height);
    }

    @Override
    public synchronized void update() {
        super.update();
//        if (chosenGameMode != null){
//            if ((timer += Game.elapsed) > 0.2f){
//                hide();
//
//                if (GamesInProgress.selectedClass == null) return;
//
//                Dungeon.mode = chosenGameMode;
//                Dungeon.hero = null;
//                Dungeon.daily = Dungeon.dailyReplay = false;
//                Dungeon.initSeed();
//                ActionIndicator.clearAction();
//                InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
//
//                Game.switchScene( InterlevelScene.class );
//
//            }
//        }
    }

    private void addLine( float y, Group content ){
        ColorBlock line = new ColorBlock((PixelScene.landscape() ? WIDTH_L : WIDTH_P) - 20 - MARGIN*3, 1, 0xBBFFFFFF);
        line.x = 20 + MARGIN*2;
        line.y = y;
        content.add(line);
    }

    public class ModeButton extends RedButton {

        Dungeon.GameMode mode;

        public ModeButton(Dungeon.GameMode m){
            super("", 6);
            hotArea.blockLevel = PointerArea.NEVER_BLOCK;

            mode = m;
        }

        @Override
        public void onClick(){
            chosenGameMode = mode;
            WndDungeonMode.this.layout();
        }
    }
}
