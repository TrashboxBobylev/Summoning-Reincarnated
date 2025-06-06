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

package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.TelekineticGrab;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class UpgradeClump extends Item {
    {
        image = ItemSpriteSheet.UPGRADE_CLUMP;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        Item item = new ScrollOfUpgrade();
        boolean couldntpickupgrade = false;

        if (!Dungeon.LimitedDrops.ABYSSAL_SPAWNER.dropped()) {
            Dungeon.LimitedDrops.ABYSSAL_SPAWNER.drop();
            GLog.p(Messages.capitalize(Messages.get(this, "piece1")));
        } else {
            Dungeon.LimitedDrops.ABYSSAL_SPAWNER.count = 0;
            GLog.p(Messages.capitalize(Messages.get(this, "piece2")));

            if (item.doPickUp(hero, hero.pos)) {
                hero.spend(-Item.TIME_TO_PICK_UP);
                GLog.i(Messages.capitalize(Messages.get(hero, "you_now_have", item.name())));
                return true;
            } else {
                GLog.w(Messages.get(TelekineticGrab.class, "cant_grab"));
                couldntpickupgrade = true;
            }
        }

        GameScene.pickUp(this, pos);
        Sample.INSTANCE.play(Assets.Sounds.ITEM);
        Talent.onItemCollected(hero, this);
        hero.spendAndNext(TIME_TO_PICK_UP);

        if (couldntpickupgrade){
            Dungeon.level.heaps.get(pos).pickUp();
            Dungeon.level.drop(item, hero.pos).sprite.drop();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return new ItemSprite.Glowing();
    }
}
