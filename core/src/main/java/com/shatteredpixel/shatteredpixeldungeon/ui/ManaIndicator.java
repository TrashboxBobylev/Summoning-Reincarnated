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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;

public class ManaIndicator extends Tag {

    Image manaIcon;
    BitmapText manaCounter;

    private int lastNumber = -1;
    private int lastMaxNumber = -1;

    public ManaIndicator() {
        super(0x959595);

        setSize( SIZE, SIZE );

        visible = true;
    }

    @Override
    protected void createChildren() {
        super.createChildren();

        manaIcon = new HeroIcon(HeroIcon.MANA);
        add( manaIcon );

        manaCounter = new BitmapText( PixelScene.pixelFont);
        add( manaCounter );
    }

    @Override
    protected void layout() {
        super.layout();

        if (!flipped)   manaIcon.x = x + (SIZE - manaIcon.width()) / 2f + 1;
        else            manaIcon.x = x + width - (SIZE + manaIcon.width()) / 2f - 1;
        manaIcon.y = y + (height - manaIcon.height()) / 2f;
        PixelScene.align(manaIcon);

        placeNumber();
    }

    private void placeNumber() {
        if (manaCounter.width() > 16) manaCounter.x = manaIcon.center().x - manaCounter.width()/2f;
        else                        manaCounter.x = manaIcon.center().x + 8 - manaCounter.width();
        manaCounter.y = manaIcon.center().y + 8 - manaCounter.baseLine();
        PixelScene.align(manaCounter);
    }

    @Override
    public void update() {
        if (Dungeon.hero != null) {
            if (Dungeon.hero.mana != lastNumber || Dungeon.hero.maxMana() != lastMaxNumber) {
                lastNumber = Dungeon.hero.mana;
                lastMaxNumber = Dungeon.hero.maxMana();
                manaCounter.text(Dungeon.hero.mana + "/" + Dungeon.hero.maxMana());
                manaCounter.hardlight(CharSprite.DEFAULT);
                manaCounter.measure();
                manaCounter.scale.set(0.75f);
                placeNumber();

                flash();
            }
        }

        super.update();
    }
}
