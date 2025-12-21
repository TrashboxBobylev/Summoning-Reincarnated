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

package com.shatteredpixel.shatteredpixeldungeon.items.staffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Crow;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;

public class CrowStaff extends Staff {
    {
        image = ItemSpriteSheet.CROW_STAFF;
        minionType = Crow.class;
        tier = 3;
        chargeTurns = 350;
        table = new BalanceTable(
                15, 1, 8,
                5, 0, 0,
                25, 1, 15);
    }

    @Override
    public int getChargeTurns() {
        switch (type()) {
            case 2:
                return 200;
            case 3:
                return 750;
        }
        return super.getChargeTurns();
    }

    @Override
    public Minion.BehaviorType defaultBehaviorType() {
        if (type() == 2){
            return Minion.BehaviorType.PASSIVE;
        }
        return super.defaultBehaviorType();
    }

    @Override
    public Minion.BehaviorType[] availableBehaviorTypes() {
        if (type() == 2){
            return new Minion.BehaviorType[]{Minion.BehaviorType.PASSIVE};
        }
        return super.availableBehaviorTypes();
    }

    @Override
    public void customizeMinion(Minion minion) {
        if (type() == 2){
            minion.behaviorType = Minion.BehaviorType.PASSIVE;
            minion.viewDistance = 5;
        }
    }

    @Override
    public void execute(Hero hero, String action) {
        if (action.equals(AC_BEHAVIOR) && type() == 2){
            if (minion != null){
                GameScene.selectCell(minionDirector);
            }
        } else {
            super.execute(hero, action);
        }
    }

    @Override
    protected String statsDesc() {
        return super.statsDesc() + "\n" + Messages.get(this, "stats_desc" + type() );
    }

    public CellSelector.Listener minionDirector = new CellSelector.Listener(){

        @Override
        public void onSelect(Integer cell) {
            if (cell == null) return;

            Sample.INSTANCE.play( Assets.Sounds.RAY );
            Dungeon.hero.sprite.parent.add(
                    new Beam.LightRay(Dungeon.hero.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(cell)));

            if (minion instanceof Crow)
                ((Crow)minion).directTocell(cell);

        }

        @Override
        public String prompt() {
            return  Messages.get(CrowStaff.class, "direct_prompt");
        }
    };
}
