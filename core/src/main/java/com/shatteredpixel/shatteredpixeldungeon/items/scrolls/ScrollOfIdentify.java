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

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.effects.Identification;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class ScrollOfIdentify extends InventoryScroll {

	{
		icon = ItemSpriteSheet.Icons.SCROLL_IDENTIFY;

		bones = true;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return !item.isIdentified();
	}

	@Override
	protected void onItemSelected( Item item ) {
		
		curUser.sprite.parent.add( new Identification( curUser.sprite.center().offset( 0, -16 ) ) );

		IDItem(item);
	}

	public static void IDItem( Item item ){
		if (ShardOfOblivion.passiveIDDisabled()) {
			if (item instanceof Weapon){
				((Weapon) item).setIDReady();
				GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready"), item.name());
				return;
			} else if (item instanceof Armor){
				((Armor) item).setIDReady();
				GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready"), item.name());
				return;
			} else if (item instanceof Ring){
				((Ring) item).setIDReady();
				GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready"), item.name());
				return;
			} else if (item instanceof Wand){
				((Wand) item).setIDReady();
				GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready"), item.name());
				return;
			}
		}

		item.identify();
		GLog.i(Messages.get(ScrollOfIdentify.class, "it_is", item.title()));
		Badges.validateItemLevelAquired( item );
	}
	
	@Override
	public int value() {
		return isKnown() ? 30 * quantity : super.value();
	}
}
