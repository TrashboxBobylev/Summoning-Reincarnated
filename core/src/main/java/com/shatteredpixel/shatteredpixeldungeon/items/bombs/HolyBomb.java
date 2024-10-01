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
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class HolyBomb extends Bomb.ConjuredBomb {

	{
		image = ItemSpriteSheet.HOLY_BOMB;
		fuseDelay = 0;
	}

	@Override
	public boolean explodesDestructively() {
		return false;
	}

	@Override
	public void explode(int cell) {
		super.explode(cell);

		if (Dungeon.level.heroFOV[cell]) {
			new Flare(10, 64).show(Dungeon.hero.sprite.parent, DungeonTilemap.tileCenterToWorld(cell), 2f);
		}

		Camera.main.shake( 4, 0.175f );

		ArrayList<Char> affected = new ArrayList<>();

		int[] area = new int[]{-Dungeon.level.width(), -1, +1, +Dungeon.level.width()};
		for (int i : area) {
			Char ch = Actor.findChar(cell + i);
			if (ch != null) {
				affected.add(ch);
			}
		}

		for (Char ch : affected){
			int damage = Math.round(damageRoll() * 0.8f);
			if (ch.properties().contains(Char.Property.UNDEAD) || ch.properties().contains(Char.Property.DEMONIC)){
				ch.sprite.emitter().start( ShadowParticle.UP, 0.05f, 10 );

				//bomb deals an additional 20% damage to unholy enemies in a 5x5 range
				damage *= 1.2f;
			}
			ch.damage(damage, new HolyDamage());
		}

		Sample.INSTANCE.play( Assets.Sounds.READ );
	}

	public static class HolyDamage{}

	@Override
	public String desc() {
		String desc_fuse = Messages.get(this, "desc",
				Math.round(minDamage()*0.64), Math.round(maxDamage()*0.64))+ "\n\n" + Messages.get(this, "desc_fuse");
		if (fuse != null){
			desc_fuse = Messages.get(this, "desc",
					Math.round(minDamage()*0.64), Math.round(maxDamage()*0.64)) + "\n\n" + Messages.get(this, "desc_burning");
		}

		return desc_fuse;
	}

	@Override
	public int value() {
		//prices of ingredients
		return quantity * (20 + 30);
	}
}
