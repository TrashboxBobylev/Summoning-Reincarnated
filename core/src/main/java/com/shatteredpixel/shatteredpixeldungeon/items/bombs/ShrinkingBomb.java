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

package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Shrink;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TimedShrink;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Point;

import java.util.ArrayList;

public class ShrinkingBomb extends Bomb {

	{
		image = ItemSpriteSheet.WOOLY_BOMB;
		harmless = true;
		fuseDelay = 0;
	}

	@Override
	public void explode(int cell) {
		super.explode(cell);
		GameScene.flash(0xA09d309f);
		boolean[] FOV = new boolean[Dungeon.level.length()];
		Point c = Dungeon.level.cellToPoint(cell);
		ShadowCaster.castShadow(c.x, c.y, Dungeon.level.width(), FOV, Dungeon.level.losBlocking, Dungeon.level.viewDistance);

		ArrayList<Char> affected = new ArrayList<>();

		for (int i = 0; i < FOV.length; i++) {
			if (FOV[i]) {
				Char ch = Actor.findChar(i);
				if (ch != null) {
					affected.add(ch);
				}
			}
		}

		for (Char ch : affected) {
			if (ch != null && ch != Dungeon.hero && ch.alignment != Char.Alignment.ALLY && !Char.hasProp(ch, Char.Property.BOSS)) {
				if (Char.hasProp(ch, Char.Property.MINIBOSS))
					Buff.affect(ch, TimedShrink.class, 8f);
				else
					Buff.affect(ch, Shrink.class);
			}
		}

		Sample.INSTANCE.play(Assets.Sounds.PUFF);

	}
	
	@Override
	public int value() {
		//prices of ingredients
		return quantity * (20 + 30);
	}
}
