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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;

public class Momentum extends Buff implements ActionIndicator.Action {
	
	{
		type = buffType.POSITIVE;

		//acts before the hero
		actPriority = HERO_PRIO+1;
	}
	
	private int momentumStacks = 0;
	private int freerunTurns = 0;
	private int freerunCooldown = 0;

	private boolean movedLastTurn = true;

	@Override
	public void detach() {
		super.detach();
		ActionIndicator.clearAction(this);
	}

	@Override
	public boolean act() {
		if (freerunCooldown > 0){
			freerunCooldown--;
		}

		if (freerunCooldown == 0 && !freerunning() && target.invisible > 0 && Dungeon.hero.pointsInTalent(Talent.SPEEDY_STEALTH) >= 1){
			momentumStacks = Math.min(momentumStacks + 2, 10);
			movedLastTurn = true;
			ActionIndicator.setAction(this);
			BuffIndicator.refreshHero();
		}

		if (freerunTurns > 0){
			if (target.invisible == 0 || Dungeon.hero.pointsInTalent(Talent.SPEEDY_STEALTH) < 2) {
				freerunTurns--;
			}
		} else if (!movedLastTurn){
			momentumStacks = (int)GameMath.gate(0, momentumStacks-1, Math.round(momentumStacks * 0.667f));
			if (momentumStacks <= 0) {
				ActionIndicator.clearAction(this);
				BuffIndicator.refreshHero();
			} else {
				ActionIndicator.refresh();
			}
		}
		movedLastTurn = false;

		spend(TICK);
		return true;
	}
	
	public void gainStack(){
		movedLastTurn = true;
		if (freerunCooldown <= 0 && !freerunning()){
			postpone(target.cooldown()+(1/target.speed()));
			momentumStacks = Math.min(momentumStacks + 1, 10);
			ActionIndicator.setAction(this);
			BuffIndicator.refreshHero();
		}
	}

	public boolean freerunning(){
		return freerunTurns > 0;
	}
	
	public float speedMultiplier(){
		if (freerunning()){
			return 2;
		} else if (target.invisible > 0 && Dungeon.hero.pointsInTalent(Talent.SPEEDY_STEALTH) == 3){
			return 2;
		} else {
			return 1;
		}
	}
	
	public int evasionBonus( int heroLvl, int excessArmorStr ){
		if (freerunTurns > 0) {
			return heroLvl/2 + excessArmorStr*Dungeon.hero.pointsInTalent(Talent.EVASIVE_ARMOR);
		} else {
			return 0;
		}
	}
	
	@Override
	public int icon() {
		if (momentumStacks > 0 || freerunCooldown > 0)  return BuffIndicator.MOMENTUM;
		else                                            return BuffIndicator.NONE;
	}
	
	@Override
	public void tintIcon(Image icon) {
		if (freerunCooldown == 0 || freerunTurns > 0){
			icon.hardlight(1,1,0);
		} else {
			icon.hardlight(0.5f,0.5f,1);
		}
	}

	@Override
	public float iconFadePercent() {
		if (freerunTurns > 0){
			return (20 - freerunTurns) / 20f;
		} else if (freerunCooldown > 0){
			return (freerunCooldown) / 30f;
		} else {
			return 0;
		}
	}

	@Override
	public String iconTextDisplay() {
		if (freerunTurns > 0){
			return Integer.toString(freerunTurns);
		} else if (freerunCooldown > 0){
			return Integer.toString(freerunCooldown);
		} else {
			return "";
		}
	}

	@Override
	public String name() {
		if (freerunTurns > 0){
			return Messages.get(this, "running");
		} else if (freerunCooldown > 0){
			return Messages.get(this, "resting");
		} else {
			return Messages.get(this, "momentum");
		}
	}
	
	@Override
	public String desc() {
		if (freerunTurns > 0){
			return Messages.get(this, "running_desc", freerunTurns);
		} else if (freerunCooldown > 0){
			return Messages.get(this, "resting_desc", freerunCooldown);
		} else {
			return Messages.get(this, "momentum_desc", momentumStacks);
		}
	}
	
	private static final String STACKS =        "stacks";
	private static final String FREERUN_TURNS = "freerun_turns";
	private static final String FREERUN_CD =    "freerun_CD";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(STACKS, momentumStacks);
		bundle.put(FREERUN_TURNS, freerunTurns);
		bundle.put(FREERUN_CD, freerunCooldown);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		momentumStacks = bundle.getInt(STACKS);
		freerunTurns = bundle.getInt(FREERUN_TURNS);
		freerunCooldown = bundle.getInt(FREERUN_CD);
		if (momentumStacks > 0 && freerunTurns <= 0){
			ActionIndicator.setAction(this);
		}
		movedLastTurn = false;
	}

	@Override
	public String actionName() {
		return Messages.get(this, "action_name");
	}

	@Override
	public int actionIcon() {
		return HeroIcon.MOMENTUM;
	}

	@Override
	public Visual secondaryVisual() {
		BitmapText txt = new BitmapText(PixelScene.pixelFont);
		txt.text(Integer.toString((int)momentumStacks) );
		txt.hardlight(CharSprite.POSITIVE);
		txt.measure();
		return txt;
	}

	@Override
	public int indicatorColor() {
		return 0x444444;
	}

	@Override
	public void doAction() {
		freerunTurns = 2*momentumStacks;
		//cooldown is functionally 10+2*stacks when active effect ends
		freerunCooldown = 10 + 4*momentumStacks;
		Sample.INSTANCE.play(Assets.Sounds.MISS, 1f, 0.8f);
		target.sprite.emitter().burst(Speck.factory(Speck.JET), 5+ momentumStacks);
		SpellSprite.show(target, SpellSprite.HASTE, 1, 1, 0);
		momentumStacks = 0;
		BuffIndicator.refreshHero();
		ActionIndicator.clearAction(this);
	}

}
