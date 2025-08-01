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

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class ArtifactRecharge extends Buff {

	public static final float DURATION = 30f;

	{
		type = buffType.POSITIVE;
	}

	private float left;
	public boolean ignoreHornOfPlenty;
	
	@Override
	public boolean act() {

		if (target instanceof Hero) {
			float chargeAmount = Math.min(1, left);
			if (chargeAmount > 0){
				for (Buff b : target.buffs()) {
					if (b instanceof Artifact.ArtifactBuff) {
						if (b instanceof HornOfPlenty.hornRecharge && ignoreHornOfPlenty){
							continue;
						}
						if (!((Artifact.ArtifactBuff) b).isCursed()) {
							((Artifact.ArtifactBuff) b).charge((Hero) target, chargeAmount);
						}
					}
				}
			}
		}

		left--;
		if (left < 0){ // we expire after 0 to be more consistent with wand recharging visually
			detach();
		} else {
			spend(TICK);
		}
		
		return true;
	}
	
	public ArtifactRecharge set( float amount ){
		if (left < amount) left = amount;
		return this;
	}
	
	public ArtifactRecharge extend(float amount ){
		left += amount;
		return this;
	}

	public float left(){
		return left;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.RECHARGING;
	}
	
	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0, 1f, 0);
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - left) / DURATION);
	}

	@Override
	public String iconTextDisplay() {
		return Integer.toString((int)left+1);
	}
	
	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns(left+1));
	}
	
	private static final String LEFT = "left";
	private static final String IGNORE_HORN = "ignore_horn";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( LEFT, left );
		bundle.put( IGNORE_HORN, ignoreHornOfPlenty );
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		left = bundle.getFloat(LEFT);
		ignoreHornOfPlenty = bundle.getBoolean(IGNORE_HORN);
	}

	public static void chargeArtifacts( Hero hero, float turns ){
		for (Buff b : hero.buffs()){
			if (b instanceof Artifact.ArtifactBuff && !((Artifact.ArtifactBuff) b).isCursed()){
				if (!((Artifact.ArtifactBuff) b).isCursed()) ((Artifact.ArtifactBuff) b).charge(hero, turns);
			}
		}
	}
}
