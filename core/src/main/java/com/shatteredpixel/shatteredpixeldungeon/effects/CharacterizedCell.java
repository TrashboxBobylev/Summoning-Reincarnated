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

package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

// targeted cell variant, that points to entities
public class CharacterizedCell extends Image {

	protected float alpha;

	protected CharSprite owner;

	public CharacterizedCell(CharSprite owner, int color ) {
		super(Icons.get(Icons.TARGET));

		this.owner = owner;

		hardlight(color);

		origin.set( width/2f );

		alpha = 1f;

		x = owner.x;
		y = owner.y;
	}

	@Override
	public void update() {
		if ((alpha -= Game.elapsed/2f) > 0) {
			alpha( alpha );
			scale.set( alpha );
			x = owner.x;
			y = owner.y;
		} else {
			killAndErase();
		}
	}
}
