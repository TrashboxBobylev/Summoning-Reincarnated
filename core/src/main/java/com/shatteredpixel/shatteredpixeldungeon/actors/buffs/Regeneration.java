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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ChaoticCenser;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.SaltCube;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;

public class Regeneration extends Buff {
	
	{
		//unlike other buffs, this one acts after the hero and takes priority against other effects
		//healing is much more useful if you get some of it off before taking damage
		actPriority = HERO_PRIO - 1;
	}
	
	private static final float REGENERATION_DELAY = 10;

	public boolean canHeal(){
		if (target instanceof Hero){
			return !((Hero)target).isStarving()/* && ((Hero) target).subClass != HeroSubClass.OCCULTIST*/;
		} else if (target.alignment == Char.Alignment.ENEMY && Dungeon.isChallenged(Conducts.Conduct.REGENERATION)){
			return true;
		}
		return false;
	}

	public static void regenerate(Char ch, int amount){
		regenerate(ch, amount, true, false);
	}

	public static void regenerate(Char ch, int amount, boolean visible){
		regenerate(ch, amount, visible, false);
	}

	public static void regenerate(Char ch, int amount, boolean visible, boolean showHPString){
		int healAmt = amount;
		healAmt = Math.min( healAmt, ch.HT - ch.HP );

		if (healAmt > 0 && ch.isAlive()) {
			ch.HP += healAmt;
			if (visible){
				if (showHPString)
					ch.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", healAmt);
				else
					ch.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(healAmt), FloatingText.HEALING);
			}
		}
	}

	@Override
	public boolean act() {
		if (target.isAlive()) {

			//if other trinkets ever get buffs like this should probably make the buff attaching
			// behaviour more like wands/rings/artifacts
			if (ChaoticCenser.averageTurnsUntilGas() != -1){
				Buff.affect(Dungeon.hero, ChaoticCenser.CenserGasTracker.class);
			}

			//cancel regenning entirely in thie case
			if (SaltCube.healthRegenMultiplier() == 0){
				spend(REGENERATION_DELAY);
				return true;
			}

			if (target.HP < regencap() && canHeal()) {
				if (regenOn()) {
					target.HP += 1;
					if (target.HP == regencap()) {
						((Hero) target).resting = false;
					}
				}
			}

			ChaliceOfBlood.chaliceRegen regenBuff = Dungeon.hero.buff( ChaliceOfBlood.chaliceRegen.class);

			float delay = REGENERATION_DELAY;
			if (regenBuff != null && target.buff(MagicImmune.class) == null) {
				if (regenBuff.isCursed()) {
					delay *= 1.5f;
				} else {
					//15% boost at +0, scaling to a 500% boost at +10
					delay -= 1.33f + regenBuff.itemLevel()*0.667f;
					delay /= RingOfEnergy.artifactChargeMultiplier(target);
				}
			}
			if (Dungeon.isChallenged(Conducts.Conduct.KING) && target instanceof Hero) delay /= 2.5f;
			if (target instanceof Mob) delay /= 3;
			delay /= SaltCube.healthRegenMultiplier();
			spend( delay );
			
		} else {
			
			diactivate();
			
		}
		
		return true;
	}
	
	public int regencap(){
		return target.HT;
	}

	public static boolean regenOn(){
		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null && !lock.regenOn()){
			return false;
		}
		return true;
	}
}
