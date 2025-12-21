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

package com.shatteredpixel.shatteredpixeldungeon.items.armor;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.TypedItem;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.GameMath;

import java.util.ArrayList;

public class ConjurerArmor extends Armor implements TypedItem, ConjurerSet {
	
	{
		image = ItemSpriteSheet.ARMOR_CONJURER;
        unique = true;
        bones = false;
	}

    public ConjurerArmor() {
        super( 1 );
    }

    @Override
    public int DRMax(int lvl) {
        return DRMax(powerlevel(), type());
    }

    @Override
    public int DRMin(int lvl) {
        return DRMin(powerlevel(), type());
    }

    public int DRMax(int lvl, int type) {
        return (int) (super.DRMax(lvl)*defenseLevel(type-1));
    }

    public int DRMin(int lvl, int type) {
        return (int) (super.DRMin(lvl)*defenseLevel(type-1));
    }

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.remove(AC_DROP);
        actions.remove(AC_THROW);
        return actions;
    }

    @Override
    public int type() {
        return level()+1;
    }

    @Override
    public void type(int type) {
        level(type -1);
    }

    //    @Override
//	public void doSpecial() {
//
//		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
//			if (Dungeon.level.heroFOV[mob.pos]
//				&& mob.alignment != Char.Alignment.ALLY) {
//				Buff.prolong( mob, SoulParalysis.class, 7 );
//			}
//		}
//
//		charge -= 75;
//
//		curUser.spend( Actor.TICK );
//		curUser.sprite.operate( curUser.pos );
//		curUser.busy();
//
//		curUser.sprite.centerEmitter().start( ElmoParticle.FACTORY, 0.15f, 4 );
//		Sample.INSTANCE.play( Assets.Sounds.LULLABY );
//	}

    @Override
    public int STRReq(int lvl) {
        return 9;
    }

    @Override
    public boolean keptThroughLostInventory() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    public String getTypeMessage(int type) {
        return Messages.get(this, "type" + type, GameMath.printAverage(DRMin(powerlevel(), type), DRMax(powerlevel(), type)));
    }
}