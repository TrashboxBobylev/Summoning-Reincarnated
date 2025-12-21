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

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.GrayRat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.SaltCube;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.levels.AbyssChallengeLevel;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.damagesource.DamageProperty;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.damagesource.DamageSource;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.EnumSet;

public class Hunger extends Buff implements Hero.Doom, DamageSource {

	public static final float HUNGRY	= 900f;
	public static final float STARVING	= 1000f;

	private float level;
	private float partialDamage;

	private static final String LEVEL			= "level";
	private static final String PARTIALDAMAGE 	= "partialDamage";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( LEVEL, level );
		bundle.put( PARTIALDAMAGE, partialDamage );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		level = bundle.getFloat( LEVEL );
		partialDamage = bundle.getFloat(PARTIALDAMAGE);
	}

	@Override
	public boolean act() {

		if (!target.isAlive()) {

			diactivate();

		}
		spend(TICK);

		return true;
	}

	public static void adjustHunger(float energy ) {
		Hunger hunger = Buff.affect(Dungeon.hero, Hunger.class);
		Char target = hunger.target;
		if (Dungeon.level.locked
				|| target.buff(WellFed.class) != null
				|| SPDSettings.intro()
				|| target.buff(ScrollOfChallenge.ChallengeArena.class) != null
				|| Dungeon.level instanceof AbyssChallengeLevel){
			return;
		}
//		if (Dungeon.hero.heroClass == HeroClass.WARRIOR && energy != -50 && energy < 0) energy *= 0.75f;
		if (Dungeon.isChallenged(Conducts.Conduct.KING)) energy *= 3;
		if (Dungeon.hero.hasTalent(Talent.MEAL_OF_POWER)) energy *= 1f - 0.2f * Dungeon.hero.pointsInTalent(Talent.MEAL_OF_POWER);

		float ratMod = 1.0f;
		for (Mob mob: Dungeon.level.mobs){
			if (mob instanceof GrayRat && ((GrayRat) mob).type == 2 && ((GrayRat) mob).behaviorType == Minion.BehaviorType.PASSIVE){
				ratMod += 1.25f;
			}
		}
		energy *= 1f / ratMod;
		energy *= SaltCube.hungerGainMultiplier();
		hunger.level = Math.max(hunger.level - energy, 0);
		switchHungerLevel(energy, hunger, target);
		BuffIndicator.refreshHero();
	}

	private static void switchHungerLevel(float energy, Hunger hunger, Char target) {
		if (hunger.level + 1 > HUNGRY && !hunger.isHungry()){
			GLog.warning(Messages.get(hunger, "onhungry"));
			if (!Document.ADVENTURERS_GUIDE.isPageRead(Document.GUIDE_FOOD)){

				GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_FOOD);
			}
			hunger.level = HUNGRY;
			return;
		}
		if (hunger.level + 1 >= STARVING && !hunger.isStarving()){

			GLog.negative(Messages.get(hunger, "onstarving"));
			Dungeon.hero.resting = false;
			Dungeon.hero.damage(1, hunger);
			Dungeon.hero.interrupt();
			hunger.level = STARVING;
			return;
		}
		if (hunger.isStarving()){
			hunger.level = STARVING;
			hunger.partialDamage += Math.abs(energy) * target.HT/800f;
			if (hunger.partialDamage > 1){
				target.damage( (int)Math.abs(hunger.partialDamage), new Hunger());
				hunger.partialDamage -= (int)hunger.partialDamage;
			}
		}
	}

	public void satisfy( float energy ) {
		affectHunger( energy, false );
	}

	public void affectHunger(float energy ){
		affectHunger( energy, false );
	}

	public void affectHunger(float energy, boolean overrideLimits ) {

		if (energy < 0 && target.buff(WellFed.class) != null){
			target.buff(WellFed.class).left += energy;
			BuffIndicator.refreshHero();
			return;
		}

		float oldLevel = level;

		level -= energy;
		if (level < 0 && !overrideLimits) {
			level = 0;
		} else if (level > STARVING) {
			float excess = level - STARVING;
			level = STARVING;
			partialDamage += excess * (target.HT/1000f);
			if (partialDamage > 1f){
				target.damage( (int)partialDamage, this );
				partialDamage -= (int)partialDamage;
			}
		}

		if (oldLevel < HUNGRY && level >= HUNGRY){
			GLog.w( Messages.get(this, "onhungry") );
		} else if (oldLevel < STARVING && level >= STARVING){
			GLog.n( Messages.get(this, "onstarving") );
			target.damage( 1, this );
		}

		BuffIndicator.refreshHero();
	}

	public boolean isStarving() {
		return level >= STARVING;
	}

	public boolean isHungry(){
		return level >= HUNGRY;
	}

	public int hunger() {
		return (int)Math.ceil(level);
	}

	@Override
	public int icon() {
		if (level < HUNGRY) {
			return BuffIndicator.NONE;
		} else if (level < STARVING) {
			return BuffIndicator.HUNGER;
		} else {
			return BuffIndicator.STARVATION;
		}
	}

	@Override
	public String name() {
		if (level < STARVING) {
			return Messages.get(this, "hungry");
		} else {
			return Messages.get(this, "starving");
		}
	}

	@Override
	public String desc() {
		String result;
		if (level < STARVING) {
			result = Messages.get(this, "desc_intro_hungry");
		} else {
			result = Messages.get(this, "desc_intro_starving");
		}

		result += Messages.get(this, "desc");

		return result;
	}

	@Override
	public void onDeath() {

		Badges.validateDeathFromHunger();

		Dungeon.fail( this );
		GLog.n( Messages.get(this, "ondeath") );
	}

    @Override
    public EnumSet<DamageProperty> initDmgProperties() {
        return EnumSet.of(DamageProperty.HUNGER, DamageProperty.LIFE_LINK_IGNORE);
    }
}
