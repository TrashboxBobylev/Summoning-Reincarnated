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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.SaltCube;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class WellFed extends Buff {

	{
		type = buffType.POSITIVE;
		announced = true;
	}
	
	int left;
	
	@Override
	public boolean act() {
		left --;
		if (left < 0){
			detach();
			if (target instanceof Hero) {
				((Hero) target).resting = false;
			}
			return true;
		} else if (left % 18 == 0 && target.HP < target.HT){
			target.HP += 1;
			target.sprite.showStatusWithIcon(CharSprite.POSITIVE, "1", FloatingText.HEALING);

			if (target.HP == target.HT && target instanceof Hero) {
				((Hero) target).resting = false;
			}
		}

		//salt cube does slow this buff down, but doesn't lessen the bonus health
		spend(TICK / SaltCube.hungerGainMultiplier());
		return true;
	}
	
	public void reset(){
		//heals one HP every 18 turns for 1000 turns
		//55 HP healed in total
		left = (int)Hunger.STARVING;
		if (Dungeon.isChallenged(Challenges.NO_FOOD)){
			//150 turns if on diet is enabled
			left /= 3;
		}
	}

	public void extend( float duration ) {
		left += duration;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.WELL_FED;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (Hunger.STARVING - left) / Hunger.STARVING);
	}

	@Override
	public String iconTextDisplay() {
		int visualLeft = (int)(left / SaltCube.hungerGainMultiplier());
		return Integer.toString(visualLeft+1);
	}
	
	@Override
	public String desc() {
		int visualLeft = (int)(left / SaltCube.hungerGainMultiplier());
		return Messages.get(this, "desc", visualLeft + 1);
	}
	
	private static final String LEFT = "left";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEFT, left);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		left = bundle.getInt(LEFT);
	}
}
