/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.potions.brews;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.FrostFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostBurn;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class FrostburnBrew extends Brew {
	
	{
		image = ItemSpriteSheet.BREW_FROSTBURN;
	}
	
	@Override
	public void shatter(int cell) {

		splash( cell );
		if (Dungeon.level.heroFOV[cell]) {
			identify();

			Sample.INSTANCE.play( Assets.Sounds.SHATTER );
			Sample.INSTANCE.play( Assets.Sounds.BURNING );
		}

		for (int offset : PathFinder.NEIGHBOURS9){
			if (!Dungeon.level.solid[cell+offset]) {

				GameScene.add(Blob.seed(cell + offset, 2, FrostFire.class));

			}
		}
	}

	@Override
	public void gooMinionAttack(Char ch) {
		if (!ch.isImmune(Burning.class) && ch.alignment == Char.Alignment.ENEMY){
			int damage = Random.NormalIntRange( 1, 3 + Dungeon.scalingDepth()/4 );
			float modifier = 3f + 5f*(((float) ch.HP) / ch.HT);
			Buff.detach( ch, Chill.class);
			ch.damage(Math.round(damage*modifier), new FrostBurn());
			if (Dungeon.level.water[ch.pos])
				Buff.affect(ch, Chill.class, 15);
			else
				Buff.affect(ch, Chill.class, 9);
			if (Dungeon.level.heroFOV[ch.pos]){
				ch.sprite.centerEmitter().burst( Speck.factory( Speck.FROSTBURN, true ), Math.round(8*modifier));
			}
		}
	}
	
	@Override
	public int value() {
		//prices of ingredients
		return quantity * (30 + 30)/2*3;
	}
	
	public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {
		
		{
			inputs =  new Class[]{PotionOfLiquidFlame.class, PotionOfFrost.class};
			inQuantity = new int[]{1, 1};
			
			cost = 14;
			
			output = FrostburnBrew.class;
			outQuantity = 2;
		}
		
	}
}
