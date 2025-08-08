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

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class ScrollOfAntiMagic extends ExoticScroll {
	
	{
		icon = ItemSpriteSheet.Icons.SCROLL_ANTIMAGIC;
	}
	
	@Override
	public void doRead() {

		detach(curUser.belongings.backpack);
        GameScene.flash( 0x4000FF00 );
		Buff.affect( curUser, MagicImmune.class, MagicImmune.DURATION );
		new Flare( 5, 48 ).color( 0x00FF00, true ).show( curUser.sprite, 3f );
        ArrayList<Mob> targets = new ArrayList<>();

        //calculate targets first, in case damaging/blinding a target affects hero vision
        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
            if (Dungeon.level.heroFOV[mob.pos]) {
                targets.add(mob);
            }
        }

        for (Mob mob : targets){
            Buff.prolong(mob, EnemyBuff.class, MagicImmune.DURATION*2f);
        }

		identify();
		
		readAnimation();
	}

    public static class EnemyBuff extends FlavourBuff {
        public static final float DURATION = 20f;

        {
            type = buffType.NEGATIVE;
            announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.COMBO;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0, 1, 0);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - visualcooldown()) / DURATION);
        }
    }
}
