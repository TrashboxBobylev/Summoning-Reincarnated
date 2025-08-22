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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ScrollOfPassage extends ExoticScroll {
	
	{
		icon = ItemSpriteSheet.Icons.SCROLL_PASSAGE;
	}
	
	@Override
	public void doRead() {

		detach(curUser.belongings.backpack);
        GameScene.flash( 0x60ba76ff );
        ArrayList<Mob> targets = new ArrayList<>();

        //calculate targets first, in case damaging/blinding a target affects hero vision
        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
            if (Dungeon.level.heroFOV[mob.pos] && !mob.isImmune(Dread.class) && mob.alignment != Char.Alignment.ALLY) {
                targets.add(mob);
            }
        }

        int amountToSpawn = 0;

        for (Mob mob : targets){
            amountToSpawn++;
            mob.sprite.killAndErase();
            Dungeon.level.mobs.remove(mob);
            Actor.remove(mob);
            Sample.INSTANCE.play(Assets.Sounds.TELEPORT, 1.0f, Random.Float(0.5f, 2f));
        }

        TargetHealthIndicator.instance.visible = false;

        for (int i = 0; i < amountToSpawn; i++){
            Dungeon.level.spawnMob(12);
        }

        identify();

        readAnimation();
	}
}
