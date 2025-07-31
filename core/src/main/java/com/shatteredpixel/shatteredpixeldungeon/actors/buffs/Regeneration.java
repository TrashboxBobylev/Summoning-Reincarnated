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

import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.SpiritForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ChaoticCenser;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.SaltCube;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.utils.Bundle;

public class Regeneration extends Buff {
	
	{
		//unlike other buffs, this one acts after the hero and takes priority against other effects
		//healing is much more useful if you get some of it off before taking damage
		actPriority = HERO_PRIO - 1;
	}

	private float partialRegen = 0f;

	private static final float REGENERATION_DELAY = 10; //1HP every 10 turns

	public boolean canHeal(){
		if (target instanceof Hero){
			return !((Hero)target).isStarving() && ((Hero) target).subClass != HeroSubClass.WILL_SORCERER;
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

			if (regenOn() && target.HP < regencap() && canHeal()) {
				boolean chaliceCursed = false;
				int chaliceLevel = -1;
				if (target.buff(MagicImmune.class) == null) {
					if (Dungeon.hero.buff(ChaliceOfBlood.chaliceRegen.class) != null) {
						chaliceCursed = Dungeon.hero.buff(ChaliceOfBlood.chaliceRegen.class).isCursed();
						chaliceLevel = Dungeon.hero.buff(ChaliceOfBlood.chaliceRegen.class).itemLevel();
					} else if (Dungeon.hero.buff(SpiritForm.SpiritFormBuff.class) != null
							&& Dungeon.hero.buff(SpiritForm.SpiritFormBuff.class).artifact() instanceof ChaliceOfBlood) {
						chaliceLevel = SpiritForm.artifactLevel();
					}
				}

				float delay = REGENERATION_DELAY;
				if (chaliceLevel != -1 && target.buff(MagicImmune.class) == null) {
					if (chaliceCursed) {
						delay *= 1.5f;
					} else {
						//15% boost at +0, scaling to a 500% boost at +10
						delay -= 1.33f + chaliceLevel*0.667f;
						delay /= RingOfEnergy.artifactChargeMultiplier(target);
					}
				}

                if (Dungeon.isChallenged(Conducts.Conduct.KING) && target instanceof Hero) delay /= 2.5f;
                if (target instanceof Mob) delay /= 3;

				//salt cube is turned off while regen is disabled.
				if (target.buff(LockedFloor.class) == null) {
					delay /= SaltCube.healthRegenMultiplier();
				}

				partialRegen += 1f / delay;

				if (partialRegen >= 1) {
					target.HP += 1;
					partialRegen--;
					if (target.HP == regencap()) {
						((Hero) target).resting = false;
					}
				}

			}

			spend( TICK );
			
		} else {
			
			diactivate();
			
		}
		
		return true;
	}
	
	public int regencap(){
		return target.HT;
	}

	public static boolean regenOn(){
        if (Dungeon.hero == null){
            return true;
        }
		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null && !lock.regenOn()){
			return false;
		}
		return true;
	}

	public static boolean canSustain(){
		return regenOn() && Dungeon.hero.buff(Hunger.class) != null && !Dungeon.hero.buff(Hunger.class).isStarving();
	}

	public static final String PARTIAL_REGEN = "partial_regen";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(PARTIAL_REGEN, partialRegen);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		partialRegen = bundle.getFloat(PARTIAL_REGEN);
	}
}
