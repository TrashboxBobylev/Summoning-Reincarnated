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

package com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ToxicImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PoisonParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfCorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ElixirOfToxicEssence extends Elixir {
	
	{
		image = ItemSpriteSheet.ELIXIR_TOXIC;
	}
	
	@Override
	public void apply(Hero hero) {
		Buff.affect(hero, ToxicImbue.class).set(ToxicImbue.DURATION);
		hero.sprite.emitter().burst(PoisonParticle.SPLASH, 10);
	}

    @Override
	public void gooMinionAttack(Char ch) {
		if (ch.alignment == Char.Alignment.ENEMY){
			new PotionOfToxicGas().gooMinionAttack(ch);
		} else if (ch.alignment == Char.Alignment.ALLY) {
			Buff.affect(ch, ToxicImbue.class).set(ToxicImbue.DURATION/4);
			if (Dungeon.level.heroFOV[ch.pos]) {
				ch.sprite.emitter().burst(PoisonParticle.SPLASH, 6);
			}
		}
	}
	
	public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {
		
		{
			inputs =  new Class[]{PotionOfCorrosiveGas.class};
			inQuantity = new int[]{1};
			
			cost = 8;
			
			output = ElixirOfToxicEssence.class;
			outQuantity = 1;
		}
		
	}
	
}
