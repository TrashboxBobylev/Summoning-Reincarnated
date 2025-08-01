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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.AbyssLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public abstract class StandardRoom extends Room {
	
	public enum SizeCategory {
		
		NORMAL(4, 10, 1),
		LARGE(10, 14, 2),
		GIANT(14, 18, 3);
		
		public final int minDim, maxDim;
		public final int roomValue;
		
		SizeCategory(int min, int max, int val){
			minDim = min;
			maxDim = max;
			roomValue = val;
		}
		
	}
	
	public SizeCategory sizeCat;
	{ setSizeCat(); }
	
	//Note that if a room wishes to allow itself to be forced to a certain size category,
	//but would (effectively) never roll that size category, consider using Float.MIN_VALUE
	public float[] sizeCatProbs(){
		//always normal by default
		return new float[]{1, 0, 0};
	}

	private static float[] chaosSizeCatProbs = new float[]{1, 1, 0};

	public boolean setSizeCat(){
		return setSizeCat(0, SizeCategory.values().length-1);
	}
	
	//assumes room value is always ordinal+1
	public boolean setSizeCat( int maxRoomValue ){
		return setSizeCat(0, maxRoomValue-1);
	}
	
	//returns false if size cannot be set
	public boolean setSizeCat( int minOrdinal, int maxOrdinal ) {
		float[] probs = sizeCatProbs();
		SizeCategory[] categories = SizeCategory.values();
		
		if (probs.length != categories.length) return false;
		
		for (int i = 0; i < minOrdinal; i++)                    probs[i] = 0;
		for (int i = maxOrdinal+1; i < categories.length; i++)  probs[i] = 0;
		
		int ordinal = Random.chances(probs);
		if (Dungeon.mode == Dungeon.GameMode.CHAOS){
			ordinal = Random.chances(chaosSizeCatProbs);
		}

		if (ordinal != -1){
			sizeCat = categories[ordinal];
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int minWidth() { return sizeCat.minDim; }
	public int maxWidth() { return sizeCat.maxDim; }
	
	@Override
	public int minHeight() { return sizeCat.minDim; }
	public int maxHeight() { return sizeCat.maxDim; }

	//larger standard rooms generally count as multiple rooms for various counting/weighting purposes
	//but there can be exceptions
	public int sizeFactor(){
		return sizeCat.roomValue;
	}

	public int mobSpawnWeight(){
		if (isEntrance()){
			return 1; //entrance rooms don't have higher mob spawns even if they're larger
		}
		if (Dungeon.mode == Dungeon.GameMode.CHAOS) return Random.IntRange(1, sizeFactor());
		return sizeFactor();
	}

	public int connectionWeight(){
		if (Dungeon.mode == Dungeon.GameMode.CHAOS) return Random.IntRange(1, sizeFactor()*sizeFactor());
		return sizeFactor() * sizeFactor();
	}

	@Override
	public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
		if (Dungeon.mode == Dungeon.GameMode.CHAOS) return true;
		int cell = l.pointToCell(pointInside(p, 1));
		return (Terrain.flags[l.map[cell]] & Terrain.SOLID) == 0;
	}

	//FIXME this is a very messy way of handing variable standard rooms
	private static ArrayList<Class<?extends StandardRoom>> rooms = new ArrayList<>();
	static {
		rooms.add(SewerPipeRoom.class);
		rooms.add(RingRoom.class);
		rooms.add(WaterBridgeRoom.class);
		rooms.add(RegionDecoPatchRoom.class);
		rooms.add(CircleBasinRoom.class);

		rooms.add(RegionDecoLineRoom.class);
		rooms.add(SegmentedRoom.class);
		rooms.add(PillarsRoom.class);
		rooms.add(ChasmBridgeRoom.class);
		rooms.add(CellBlockRoom.class);

		rooms.add(CaveRoom.class);
		rooms.add(RegionDecoBridgeRoom.class);
		rooms.add(CavesFissureRoom.class);
		rooms.add(CirclePitRoom.class);
		rooms.add(CircleWallRoom.class);

		rooms.add(HallwayRoom.class);
		rooms.add(LibraryHallRoom.class);
		rooms.add(LibraryRingRoom.class);
		rooms.add(StatuesRoom.class);
		rooms.add(SegmentedLibraryRoom.class);

		rooms.add(RuinsRoom.class);
		rooms.add(RegionDecoPatchRoom.class);
		rooms.add(ChasmRoom.class);
		rooms.add(SkullsRoom.class);
		rooms.add(RitualRoom.class);


		rooms.add(PlantsRoom.class);
		rooms.add(AquariumRoom.class);
		rooms.add(PlatformRoom.class);
		rooms.add(BurnedRoom.class);
		rooms.add(FissureRoom.class);
		rooms.add(GrassyGraveRoom.class);
		rooms.add(StripedRoom.class);
		rooms.add(StudyRoom.class);
		rooms.add(SuspiciousChestRoom.class);
		rooms.add(MinefieldRoom.class);
	}
	
	private static float[][] chances = new float[27][];
	private static float[] abyss;
	static {
		chances[1] =  new float[]{16,8,8,4,4,   0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0,  1,0,1,0,1,0,1,1,0,0};
		chances[2] =  new float[]{16,8,8,4,4,   0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0,  1,1,1,1,1,1,1,1,1,1};
		chances[4] =  chances[3] = chances[2];
		chances[5] =  new float[]{16,8,8,4,0,   0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0,  0,0,0,0,0,0,0,0,0,0};

		chances[6] =  new float[]{0,0,0,0,0, 10,10,10,5,5, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0,  1,1,1,1,1,1,1,1,1,1};
		chances[10] = chances[9] = chances[8] = chances[7] = chances[6];

		chances[11] = new float[]{0,0,0,0,0, 0,0,0,0,0, 16,8,8,4,4,   0,0,0,0,0, 0,0,0,0,0,  1,1,1,1,1,1,1,1,1,1};
		chances[15] = chances[14] = chances[13] = chances[12] = chances[11];

		chances[16] = new float[]{0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 10,10,10,5,5, 0,0,0,0,0,  1,1,1,1,1,1,1,1,1,1};
		chances[20] = chances[19] = chances[18] = chances[17] = chances[16];

		chances[21] = new float[]{0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 10,10,10,5,5,  1,1,1,1,1,1,1,1,1,1};
		chances[26] = chances[25] = chances[24] = chances[23] = chances[22] = chances[21];

		abyss       = new float[]{1,  1,1,1,1, 1,1,1,1, 1,1,1,1, 1,1,1,1, 1,1,1,1,     1,1,1,1,1,1,1,1,1,1};
	}
	
	public static StandardRoom createRoom(){
		if (Dungeon.mode == Dungeon.GameMode.GAUNTLET){
			return Reflection.newInstance(EmptyRoom.class);
		}
		if (Dungeon.branch == AbyssLevel.BRANCH || Dungeon.mode == Dungeon.GameMode.CHAOS){
			return Reflection.newInstance(rooms.get(Random.chances(abyss)));
		}
		return Reflection.newInstance(rooms.get(Random.chances(chances[
                (int) Math.ceil(Dungeon.depth / (Dungeon.chapterSize()*5f/25f))])));
	}
	
}
