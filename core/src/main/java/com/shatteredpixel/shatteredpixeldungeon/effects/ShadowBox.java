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

package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.gltextures.SmartTexture;
import com.watabou.noosa.NinePatch;

public class ShadowBox extends NinePatch {

	public static final float SIZE	= 16;
	
	public ShadowBox() {
		super( Assets.Interfaces.SHADOW, 1 );

		//If this is the first time the texture is generated, set the filtering
		if (texture.id == -1)
			texture.filter( SmartTexture.LINEAR, SmartTexture.LINEAR );
		
		scale.set( SIZE, SIZE );
	}
	
	@Override
	public void size(float width, float height) {
		super.size( width / SIZE, height / SIZE );
	}

	public void boxRect( float x, float y, float width, float height ) {
		this.x = x - SIZE;
		this.y = y - SIZE;
		size( width + SIZE * 2, height + SIZE * 2 );
	}
}
