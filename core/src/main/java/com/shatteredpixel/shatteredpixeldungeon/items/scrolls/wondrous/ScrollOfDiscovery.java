/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Summoning Pixel Dungeon Reincarnated
 * Copyright (C) 2023-2026 Trashbox Bobylev
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

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls.wondrous;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Identification;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class ScrollOfDiscovery extends WondrousScroll {
    {
        image = ItemSpriteSheet.Icons.SCROLL_DISCOVERY;
    }

    @Override
    public void doRead() {
        detach(curUser.belongings.backpack);
        curUser.sprite.parent.add( new Identification( curUser.sprite.center().offset( 0, -16 ) ) );

        int total = 0;
        Item affected = null;
        for (Item item : Dungeon.hero.belongings){
            if (Random.Int(3) == 0 && !item.isIdentified()) {
                item.identify();
                affected = item;
                total++;
            }
        }
        switch (total) {
            case 0:
                GLog.i( Messages.get(this, "none") );
                break;
            case 1:
                GLog.i( Messages.get(this, "one", affected.name()) );
                break;
            default:
                GLog.i( Messages.get(this, "many", total) );
        }

        Sample.INSTANCE.play( Assets.Sounds.READ );
        identify();
        readAnimation();
    }
}
