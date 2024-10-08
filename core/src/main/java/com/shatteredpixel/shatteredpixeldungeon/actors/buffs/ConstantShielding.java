/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2022 Evan Debenham
 *
 * Summoning Pixel Dungeon
 * Copyright (C) 2019-2022 TrashboxBobylev
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

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.HolyAuraBuff;

public class ConstantShielding extends ShieldBuff{

	@Override
	public boolean act() {
		if (target.buff(HolyAuraBuff.class) != null) {
			incShield (1);
			spend( target.buff(HolyAuraBuff.class).shieldingRate );
			
		} else {
			Buff.affect(target, Barrier.class).setShield(shielding());
			setShield(0);
			detach();
		}
		
		return true;
	}

	@Override
	//logic edited slightly as buff should not detach
	public int absorbDamage(int dmg) {
		if (shielding() >= dmg){
			decShield(dmg);
			dmg = 0;
		} else {
			dmg -= shielding();
			setShield(0);
		}
		return dmg;
	}
}
