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

public class WndMessage extends Window {

	private static final int WIDTH_MIN = 120;
	private static final int WIDTH_MAX = 220;
	private static final int MARGIN = 4;
	
	public WndMessage( String text ) {
		
		super();

		int width = WIDTH_MIN;
		
		RenderedTextBlock info = PixelScene.renderTextBlock( text, 6 );
		info.maxWidth(width - MARGIN * 2);
		info.setPos(MARGIN, MARGIN);
		add( info );

		while (PixelScene.landscape()
				&& info.height() > 120
				&& width < WIDTH_MAX){
			width += 20;
			info.maxWidth(width - MARGIN * 2);
		}

		resize(
			(int)info.width() + MARGIN * 2,
			(int)info.height() + MARGIN * 2 );
	}
}
