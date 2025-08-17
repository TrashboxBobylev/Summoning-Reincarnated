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

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WandEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.GameMath;

//for wands that directly damage a target
//wands with AOE or circumstantial direct damage count here (e.g. fireblast, transfusion), but wands with indirect damage do not (e.g. corrosion)
public abstract class DamageWand extends Wand{

	public float magicMin(){
		return magicMin(power())*powerModifier();
	}

	public abstract float magicMin(float lvl);

	public float magicMax(){
		return magicMax(power())*powerModifier();
	}

	public abstract float magicMax(float lvl);

	public int damageRoll(){
		return damageRoll(power());
	}

	public int damageRoll(float lvl){
		int dmg = Hero.heroDamageIntRange((int) (magicMin(lvl)*powerModifier(rank())), (int) (magicMax(lvl)*powerModifier(rank())));
		WandEmpower emp = Dungeon.hero.buff(WandEmpower.class);
		if (emp != null){
			dmg += emp.dmgBoost;
			emp.left--;
			if (emp.left <= 0) {
				emp.detach();
			}
			Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG, 0.75f, 1.2f);
		}
		return dmg;
	}

	@Override
	public String statsDesc() {
		if (levelKnown)
			return Messages.get(this, "stats_desc", GameMath.printAverage((int) magicMin(), (int) magicMax()));
		else
			return Messages.get(this, "stats_desc", GameMath.printAverage((int) magicMin(0), (int) magicMax(0)));
	}

	@Override
	public String upgradeStat1(int level) {
		return GameMath.printAverage((int)magicMin(level), (int)magicMax(level));
	}

    @Override
    public String generalRankDescription(int rank){
        return Messages.get(this, "rank" + rank,
                GameMath.printAverage(
                        Math.round(magicMin(power())*powerModifier(rank)),
                        Math.round(magicMax(power())*powerModifier(rank))
                ),
                getRechargeInfo(rank)
        );
    }
}
