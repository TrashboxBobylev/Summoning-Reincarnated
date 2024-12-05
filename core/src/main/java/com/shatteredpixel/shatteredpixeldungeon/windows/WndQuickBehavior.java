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
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.staffs.Staff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventorySlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

// I LOVE COPYPASTING, WHEN IT IS ADVANTAGEOUS
public class WndQuickBehavior extends Window {

	private static Staff staff;

	public WndQuickBehavior(Staff staff){
		super(0, 0, Chrome.get(Chrome.Type.TOAST_TR));

		if( WndBag.INSTANCE != null ){
			WndBag.INSTANCE.hide();
		}
		WndBag.INSTANCE = this;

		WndQuickBehavior.staff = staff;

		float width = 0, height = 0;
		int maxWidth = PixelScene.landscape() ? 240 : 135;
		int left = 0;
		int top = 10;

		ArrayList<Item> items = new ArrayList<>();

		final Minion.BehaviorType[] behaviorTypes = staff.availableBehaviorTypes();
		for (final Minion.BehaviorType behaviorType : behaviorTypes){
			Item shadowItem = new Item(){
				{
					image = ItemSpriteSheet.BEHAVIOR_ICONS + behaviorType.ordinal();
					defaultAction = AC_THROW;
				}

				@Override
				public boolean isIdentified() {
					return true;
				}

				@Override
				public void execute(Hero hero) {
					GameScene.cancel();

					Minion minion = staff.minion();
					minion.behaviorType = behaviorType;
					GLog.highlight( Messages.get(staff, "behavior_switch", minion.name(), minion.behaviorType.name()) );
					minion.sprite.emitter().burst(MagicMissile.MagicParticle.FACTORY, 8);
					Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
					updateQuickslot();
				}
			};
			items.add(shadowItem);
		}

		int btnWidth = 16;
		int btnHeight = 20;

		//height of the toolbar and status pane, plus a little extra
		int targetHeight = PixelScene.uiCamera.height - 100;
		int rows = (int)Math.ceil(items.size() / (float)((maxWidth+1) / (btnWidth+1)));
		int expectedHeight = rows * btnHeight + (rows-1);
		while (expectedHeight > targetHeight && btnHeight > 16){
			btnHeight--;
			expectedHeight -= rows;
		}

		for (Item i : items){
			InventorySlot slot = new InventorySlot(i){
				@Override
				protected void onClick() {
					hide();
					item.execute(Dungeon.hero);
				}

				@Override
				protected boolean onLongClick() {
					onClick();
					return true;
				}

				@Override
				protected String hoverText() {
					return null; //no tooltips here
 				}
			};
			slot.showExtraInfo(false);
			slot.setRect(left, top, btnWidth, btnHeight);
			add(slot);

			if (width < slot.right()) width = slot.right();
			if (height < slot.bottom()) height = slot.bottom();

			left += btnWidth+1;

			if (left + btnWidth > maxWidth){
				left = 0;
				top += btnHeight+1;
			}
		}

		RenderedTextBlock txtTitle;
		txtTitle = PixelScene.renderTextBlock( Messages.titleCase(Messages.get(this, "title")), 8 );
		txtTitle.hardlight( TITLE_COLOR );
		if (txtTitle.width() > width) width = txtTitle.width();

		txtTitle.setPos(
				(width - txtTitle.width())/2f,
				(10 - txtTitle.height()) / 2f - 1);
		PixelScene.align(txtTitle);
		add( txtTitle );

		resize((int)width, (int)height);

		int bottom = GameScene.uiCamera.height;

		//offset to be above the toolbar
		offset(0, (int) (bottom/2 - 30 - height/2));

	}

	@Override
	public void hide() {
		super.hide();
		if (WndBag.INSTANCE == this){
			WndBag.INSTANCE = null;
		}
	}
}
