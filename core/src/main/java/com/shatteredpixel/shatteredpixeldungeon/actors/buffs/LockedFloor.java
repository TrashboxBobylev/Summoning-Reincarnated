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
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class LockedFloor extends Buff {

	//the amount of turns remaining before beneficial passive effects turn off
	//starts at 50 turns normally, 20 with badder bosses
	private float left = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 20 : 50;

	@Override
	public boolean act() {
		spend(TICK);

		if (!Dungeon.level.locked)
			detach();

		if (left >= 1)
			left --;

		return true;
	}

	public void addTime(float time){
		left += time;
		left = Math.min(left, 50); //cannot build to more than 50
	}

	public void removeTime(float time){
		left -= time; //can go negative!
	}

	public boolean regenOn(){
		return left >= 1;
	}

	private final String LEFT = "left";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( LEFT, left );
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		left = bundle.getFloat( LEFT );
	}

	@Override
	public int icon() {
		return BuffIndicator.LOCKED_FLOOR;
	}
}
