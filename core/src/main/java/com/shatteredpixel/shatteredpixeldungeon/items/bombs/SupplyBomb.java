package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

public class SupplyBomb extends Bomb {

    {
        image = ItemSpriteSheet.SUPPLY_BOMB;
        fuseDelay = 50;
        harmless = true;
        fuseTriggerClass = Trigger.class;
    }

//    public void setTrigger(int cell){
//
//		Buff.affect(Dungeon.hero, Trigger.class).set(cell);
//
//		CellEmitter.center( cell ).start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
//		Sample.INSTANCE.play( Assets.Sounds.ALERT );
//
//		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
//			mob.beckon( cell );
//		}
//
//
//
//	}

    public static class Trigger extends Buff implements FuseBuff {

        int cell;
        int floor;
        int left;

        @Override
        public void set(int cell){
            floor = Dungeon.depth;
            this.cell = cell;
        }

        @Override
        public boolean act() {

            if (Dungeon.depth != floor){
                spend(TICK);
                return true;
            }

            SupplyBomb bomb = null;
            Heap heap = Dungeon.level.heaps.get(cell);

            if (heap != null){
                for (Item i : heap.items){
                    if (i instanceof SupplyBomb){
                        bomb = (SupplyBomb) i;
                        break;
                    }
                }
            }

            if (bomb == null) {
                detach();

            }  else {
                spend(TICK/* / Bomb.nuclearBoost()*/);

                PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), 2 );
                for (int i = 0; i < PathFinder.distance.length; i++) {
                    if (PathFinder.distance[i] < Integer.MAX_VALUE && Dungeon.hero.pos == i) {
                        if (true/*Dungeon.mode != Dungeon.GameMode.HELL*/) {
                            CellEmitter.get(i).burst(Speck.factory(Speck.HEALING), 1);
                            Dungeon.hero.HP = Math.min(Dungeon.hero.HP + 1, Dungeon.hero.HT);
                        }
                        Hunger.adjustHunger(8f);
                    }
                }

            }

            return true;
        }

        private static final String CELL = "cell";
        private static final String FLOOR = "floor";
        private static final String LEFT = "left";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(CELL, cell);
            bundle.put(FLOOR, floor);
            bundle.put(LEFT, left);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            cell = bundle.getInt(CELL);
            floor = bundle.getInt(FLOOR);
            left = bundle.getInt(LEFT);
        }
    }

    @Override
    public int value() {
        //prices of ingredients
        return quantity * (35 + 50);
    }
}
