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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;

public class CharHealthIndicator extends HealthBar {
	
	private static final int HEIGHT = 1;
	
	private Char target;
	
	public CharHealthIndicator( Char c ){
		target = c;
		GameScene.add(this);
	}
	
	@Override
	protected void createChildren() {
		super.createChildren();
		height = HEIGHT;
	}
	
	@Override
	public void update() {
		super.update();
		
		if (target != null && target.isAlive() && target.isActive() && target.sprite.visible) {
			CharSprite sprite = target.sprite;
			width = sprite.width()*(4/6f);
			x = sprite.x + sprite.width()/6f;
			y = sprite.y - 2;
			level( target );
			visible = target.HP < target.HT || target.shielding() > 0;
		} else {
			visible = false;
		}
	}
	
	public void target( Char ch ) {
		if (ch != null && ch.isAlive() && ch.isActive()) {
			target = ch;
		} else {
			target = null;
		}
	}
	
	public Char target() {
		return target;
	}
}
