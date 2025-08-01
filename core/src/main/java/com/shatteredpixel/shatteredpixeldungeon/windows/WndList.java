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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

public class WndList extends Window {
	
	private static final int WIDTH	= 120;
	private static final int MARGIN	= 4;
	private static final int GAP	= 4;
	
	public WndList( String[] items ) {
		
		super();
		
		float pos = MARGIN;
		float dotWidth = 0;
		float maxWidth = 0;
		
		for (int i=0; i < items.length; i++) {
			
			if (i > 0) {
				pos += GAP;
			}
			
			RenderedTextBlock item = PixelScene.renderTextBlock( "-" + items[i], 6 );
			item.setPos( MARGIN, pos );
			item.maxWidth(WIDTH - MARGIN*2);
			add( item );
			
			pos += item.height();
			float w = item.width();
			if (w > maxWidth) {
				maxWidth = w;
			}
		}

		resize( (int)(maxWidth + dotWidth + MARGIN * 2), (int)(pos + MARGIN) );
	}
}
