package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.VaultSentry;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;

import java.util.ArrayList;

public class VaultManyScansRoom extends VaultTreasureRoom {

	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY_SP );

		ArrayList<Integer> corners = new ArrayList<>();
		int w = level.width();
		Point c = center();
		corners.add(left+1  + w*(top+1));
		corners.add(c.x     + w*(top+1));
		corners.add(right-1 + w*(top+1));
		corners.add(left+1  + w*(c.y));
		//skip center
		corners.add(right-1 + w*(c.y));
		corners.add(left+1  + w*(bottom-1));
		corners.add(c.x     +  w*(bottom-1));
		corners.add(right-1 +  w*(bottom-1));

		Door entrance = entrance();
		entrance.set( Door.Type.REGULAR );

		for (int cell : corners){
			if (level.trueDistance(cell, level.pointToCell(entrance)) >= 2){
				VaultSentry sentry = new VaultSentry();
				sentry.pos = cell;
				sentry.scanLength = 7;
				sentry.scanWidth = 70;
				sentry.scanDirs = new int[][]{
						new int[]{c.x + w*c.y}
				};
				level.mobs.add(sentry);
			}
		}

		Painter.set(level, c, Terrain.PEDESTAL);
		Item treasureItem = Generator.randomUsingDefaults(Generator.Category.WEP_T5);
		if (treasureItem.cursed){
			treasureItem.cursed = false;
			if (((MeleeWeapon) treasureItem).hasCurseEnchant()){
				((MeleeWeapon) treasureItem).enchant(null);
			}
		}
		//not true ID
		treasureItem.levelKnown = treasureItem.cursedKnown = true;
		level.drop(treasureItem, c.x + w*c.y).type = Heap.Type.CHEST;

		//TODO add solution item to item spawn pool (potion of invisibility?)

	}

}
