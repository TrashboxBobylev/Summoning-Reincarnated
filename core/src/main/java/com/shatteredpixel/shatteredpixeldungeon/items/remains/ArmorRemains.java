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

package com.shatteredpixel.shatteredpixeldungeon.items.remains;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.generic.TalentBooster;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class ArmorRemains extends RemainsItem {

	{
		image = ItemSpriteSheet.ARMOR_REMAINS;
	}

	@Override
	protected void doEffect(Hero hero) {
		int currentTier = 0;
		for (int i = 1; i < Talent.MAX_TALENT_TIERS+1; i++){
            if (hero.lvl < (Talent.tierLevelThresholds[i] - 1)) {
				currentTier = i-1;
				break;
			}
        }
		Buff.affect(hero, ArmorRemainsTracker.class).setBoosted(currentTier);
		GameScene.showlevelUpStars();
		new Flare( 6, 32 ).color(0xFFFF00, true).show( hero.sprite, 2f );
		Sample.INSTANCE.playDelayed(Assets.Sounds.LEVELUP, 0.6f, 0.7f, 1.2f);
	}

	public static class ArmorRemainsTracker extends Buff implements TalentBooster {

		{
			type = buffType.POSITIVE;
			revivePersists = true;
		}

		private int boostedTier;

		private static final String BOOSTED_TIER = "boosted_tier";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(BOOSTED_TIER, boostedTier);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			boostedTier = bundle.getInt(BOOSTED_TIER);
		}

		public void setBoosted( int tier ){
			boostedTier = tier;
		}

		public boolean isBoosted( int tier ){
			return boostedTier == tier;
		}

		@Override
		public int boostPoints(int tier) {
			return 1;
		}

	}

}
