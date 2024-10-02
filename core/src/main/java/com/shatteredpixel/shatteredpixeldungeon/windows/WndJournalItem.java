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

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Rankable;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.WndChangesTabbed;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;

public class WndJournalItem extends WndTitledMessage {

	public WndJournalItem(Image icon, String title, String message ) {
		super(icon, title, message);
		if (blockThing()){
			PointerArea blocker = new PointerArea(0, 0, PixelScene.uiCamera.width, PixelScene.uiCamera.height) {
				@Override
				protected void onClick(PointerEvent event) {
					onBackPressed();
				}
			};
			blocker.camera = PixelScene.uiCamera;
			add(blocker);
		}

	}

	boolean blockThing(){return true;}

	public static class Ranked extends WndJournalItem {

		@Override
		boolean blockThing() {
			return false;
		}

		public Ranked(Image icon, String title, String message, Item item) {
			super(icon, title, message);

			float y = height;

			RedButton btn = new RedButton(Messages.upperCase(Messages.get(WndJournalItem.class, "ranks")), 8 ) {
				@Override
				protected void onClick() {
					if (ShatteredPixelDungeon.scene() instanceof GameScene) {
						GameScene.show(new WndChangesTabbed(
								new ItemSprite(item),
								Messages.titleCase(Messages.get(WndJournalItem.class, "ranks")),
								((Rankable) item).getRankMessage(1),
								((Rankable) item).getRankMessage(2),
								((Rankable) item).getRankMessage(3)));
					} else {
						ShatteredPixelDungeon.scene().addToFront(new WndChangesTabbed(
								new ItemSprite(item),
								Messages.titleCase(Messages.get(WndJournalItem.class, "ranks")),
								((Rankable) item).getRankMessage(1),
								((Rankable) item).getRankMessage(2),
								((Rankable) item).getRankMessage(3)));
					}
				}
			};

			btn.setSize( width, 16 );
			btn.setPos( 0, height + 2);
			add( btn );
			resize( width, (int)btn.bottom() + 2 );
		}
	}

}
