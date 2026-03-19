/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Empowered;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class ScrollOfAttunement extends ExoticScroll {

    {
        icon = ItemSpriteSheet.Icons.SCROLL_ATTUNED;
    }

    @Override
    public void doRead() {
        detach(curUser.belongings.backpack);
        new Flare( 5, 32 ).color( 0xFFFFFF, true ).show( curUser.sprite, 2f );
        Sample.INSTANCE.play( Assets.Sounds.READ );
        GameScene.flash( 0xFFFFFF );

        int count = 0;
        Mob affected = null;
        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
            if (Dungeon.level.heroFOV[mob.pos]){
                if (mob.alignment == Char.Alignment.ALLY) {
                    Buff.affect(mob, Empowered.class, Empowered.DURATION);

                    if (mob.buff(Empowered.class) != null) {
                        count++;
                        affected = mob;
                    }
                } else if (mob.alignment == Char.Alignment.ENEMY){
                    Buff.affect(mob, Weakness.class, Weakness.DURATION / 4);
                }
            }
        }

        switch (count) {
            case 0:
                GLog.i( Messages.get(this, "none") );
                break;
            case 1:
                GLog.i( Messages.get(this, "one", affected.name()) );
                break;
            default:
                GLog.i( Messages.get(this, "many") );
        }

        identify();

        readAnimation();
    }
}
