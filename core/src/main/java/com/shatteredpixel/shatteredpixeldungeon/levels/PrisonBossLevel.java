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

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Regrowth;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StormCloud;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Tengu;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.HeavyBoomerang;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TenguDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

import java.util.ArrayList;

public class PrisonBossLevel extends Level {
	
	{
		color1 = 0x6a723d;
		color2 = 0x88924c;
		
		//the player should be able to see all of Tengu's arena
		viewDistance = 12;
	}
	
	public enum State {
		START,
		FIGHT_START,
		FIGHT_PAUSE,
		FIGHT_ARENA,
		WON
	}
	
	private State state;
	private Tengu tengu;

	@Override
	public void playLevelMusic() {
		if (state == State.START){
			Music.INSTANCE.end();
		} else if (state == State.WON) {
			Music.INSTANCE.playTracks(PrisonLevel.PRISON_TRACK_LIST, PrisonLevel.PRISON_TRACK_CHANCES, false);
		} else {
			Music.INSTANCE.play(Assets.Music.PRISON_BOSS, true);
		}
	}

	public State state(){
		return state;
	}
	
	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_PRISON;
	}
	
	@Override
	public String waterTex() {
		return Assets.Environment.WATER_PRISON;
	}
	
	private static final String STATE	        = "state";
	private static final String TENGU	        = "tengu";
	private static final String STORED_ITEMS    = "storeditems";
	private static final String TRIGGERED       = "triggered";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( STATE, state );
		bundle.put( TENGU, tengu );
		bundle.put( STORED_ITEMS, storedItems);
		bundle.put(TRIGGERED, triggered );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		state = bundle.getEnum( STATE, State.class );
		
		//in some states tengu won't be in the world, in others he will be.
		if (state == State.START || state == State.FIGHT_PAUSE) {
			tengu = (Tengu)bundle.get( TENGU );
		} else {
			for (Mob mob : mobs){
				if (mob instanceof Tengu) {
					tengu = (Tengu) mob;
					break;
				}
			}
		}
		
		for (Bundlable item : bundle.getCollection(STORED_ITEMS)){
			storedItems.add( (Item)item );
		}
		
		triggered = bundle.getBooleanArray(TRIGGERED);
		
	}
	
	@Override
	protected boolean build() {
		setSize(32, 32);
		
		state = State.START;
		setMapStart();
		
		return true;
	}
	
	private static final int ENTRANCE_POS = 10 + 4*32;
	private static final Rect entranceRoom = new Rect(8, 2, 13, 8);
	private static final Rect startHallway = new Rect(9, 7, 12, 24);
	private static final Rect[] startCells = new Rect[]{ new Rect(5, 9, 10, 16), new Rect(11, 9, 16, 16),
	                                         new Rect(5, 15, 10, 22), new Rect(11, 15, 16, 22)};
	private static final Rect tenguCell = new Rect(6, 23, 15, 32);
	private static final Point tenguCellCenter = new Point(10, 27);
	private static final Point tenguCellDoor = new Point(10, 23);
	private static final Point[] startTorches = new Point[]{ new Point(10, 2),
	                                       new Point(7, 9), new Point(13, 9),
	                                       new Point(7, 15), new Point(13, 15),
	                                       new Point(8, 23), new Point(12, 23)};
	
	private void setMapStart(){
		transitions.add(new LevelTransition(this, ENTRANCE_POS, LevelTransition.Type.REGULAR_ENTRANCE));
		
		Painter.fill(this, 0, 0, 32, 32, Terrain.WALL);
		
		//Start
		Painter.fill(this, entranceRoom, Terrain.WALL);
		Painter.fill(this, entranceRoom, 1, Terrain.EMPTY);
		Painter.set(this, ENTRANCE_POS, Terrain.ENTRANCE);
		
		Painter.fill(this, startHallway, Terrain.WALL);
		Painter.fill(this, startHallway, 1, Terrain.EMPTY);
		
		Painter.set(this, startHallway.left+1, startHallway.top, Terrain.DOOR);
		
		for (Rect r : startCells){
			Painter.fill(this, r, Terrain.WALL);
			Painter.fill(this, r, 1, Terrain.EMPTY);
		}
		
		Painter.set(this, startHallway.left, startHallway.top+5, Terrain.DOOR);
		Painter.set(this, startHallway.right-1, startHallway.top+5, Terrain.DOOR);
		Painter.set(this, startHallway.left, startHallway.top+11, Terrain.DOOR);
		Painter.set(this, startHallway.right-1, startHallway.top+11, Terrain.DOOR);
		
		Painter.fill(this, tenguCell, Terrain.WALL);
		Painter.fill(this, tenguCell, 1, Terrain.EMPTY);
		
		Painter.set(this, tenguCell.left+4, tenguCell.top, Terrain.LOCKED_DOOR);
		
		for (Point p : startTorches){
			Painter.set(this, p, Terrain.WALL_DECO);
		}

		addCagesToCells();

		//we set up the exit for consistently with other levels, even though it's in the walls
		LevelTransition exit = new LevelTransition(this, pointToCell(levelExit), LevelTransition.Type.REGULAR_EXIT);
		exit.right+=2;
		exit.bottom+=3;
		transitions.add(exit);
	}

	//area where items/chars are preserved when moving to the arena
	private static final Rect pauseSafeArea = new Rect(9, 2, 12, 12);

	private void setMapPause(){
		setMapStart();
		transitions.clear();

		Painter.set(this, tenguCell.left+4, tenguCell.top, Terrain.DOOR);

		Painter.fill(this, startCells[1].left, startCells[1].top+3, 1, 7, Terrain.EMPTY);
		Painter.fill(this, startCells[1].left+2, startCells[1].top+2, 3, 10, Terrain.EMPTY);

		Painter.fill(this, entranceRoom, Terrain.WALL);
		Painter.set(this, startHallway.left+1, startHallway.top, Terrain.EMPTY);
		Painter.set(this, startHallway.left+1, startHallway.top+1, Terrain.DOOR);

		addCagesToCells();

	}
	
	private static final Rect arena = new Rect(3, 1, 18, 16);
	
	private void setMapArena(){
		transitions.clear();

		Painter.fill(this, 0, 0, 32, 32, Terrain.WALL);
		
		Painter.fill(this, arena, Terrain.WALL);
		Painter.fillEllipse(this, arena, 1, Terrain.EMPTY);
	
	}
	
	private static int W = Terrain.WALL;
	private static int D = Terrain.WALL_DECO;
	private static int e = Terrain.EMPTY;
	private static int E = Terrain.EXIT;
	private static int C = Terrain.CHASM;
	
	private static final Point endStart = new Point( startHallway.left+2, startHallway.top+2);
	private static final Point levelExit = new Point( endStart.x+11, endStart.y+6);
	private static final int[] endMap = new int[]{
			W, W, D, W, W, W, W, W, W, W, W, W, W, W,
			W, e, e, e, W, W, W, W, W, W, W, W, W, W,
			W, e, e, e, e, e, e, e, e, W, W, W, W, W,
			e, e, e, e, e, e, e, e, e, e, e, e, W, W,
			e, e, e, e, e, e, e, e, e, e, e, e, e, W,
			e, e, e, C, C, C, C, C, C, C, C, e, e, W,
			e, W, C, C, C, C, C, C, C, C, C, E, E, W,
			e, e, e, C, C, C, C, C, C, C, C, E, E, W,
			e, e, e, e, e, C, C, C, C, C, C, E, E, W,
			e, e, e, e, e, e, e, W, W, W, C, C, C, W,
			W, e, e, e, e, e, W, W, W, W, C, C, C, W,
			W, e, e, e, e, W, W, W, W, W, W, C, C, W,
			W, W, W, W, W, W, W, W, W, W, W, C, C, W,
			W, W, W, W, W, W, W, W, W, W, W, C, C, W,
			W, D, W, W, W, W, W, W, W, W, W, C, C, W,
			e, e, e, W, W, W, W, W, W, W, W, C, C, W,
			e, e, e, W, W, W, W, W, W, W, W, C, C, W,
			e, e, e, W, W, W, W, W, W, W, W, C, C, W,
			e, e, e, W, W, W, W, W, W, W, W, C, C, W,
			e, e, e, W, W, W, W, W, W, W, W, C, C, W,
			e, e, e, W, W, W, W, W, W, W, W, C, C, W,
			e, e, e, W, W, W, W, W, W, W, W, C, C, W,
			W, W, W, W, W, W, W, W, W, W, W, C, C, W
	};
	
	private void setMapEnd(){
		
		Painter.fill(this, 0, 0, 32, 32, Terrain.WALL);
		
		setMapStart();
		
		for (Heap h : heaps.valueList()){
			if (h.peek() instanceof IronKey){
				h.destroy();
			}
		}
		
		CustomTilemap vis = new ExitVisual();
		vis.pos(11, 10);
		customTiles.add(vis);
		GameScene.add(vis, false);
		
		vis = new ExitVisualWalls();
		vis.pos(11, 10);
		customWalls.add(vis);
		GameScene.add(vis, true);
		
		Painter.set(this, tenguCell.left+4, tenguCell.top, Terrain.DOOR);
		
		int cell = pointToCell(endStart);
		int i = 0;
		while (cell < length()){
			System.arraycopy(endMap, i, map, cell, 14);
			i += 14;
			cell += width();
		}

		//pre-2.5.1 saves, if exit wasn't already added
		if (exit() == entrance()) {
			LevelTransition exit = new LevelTransition(this, pointToCell(levelExit), LevelTransition.Type.REGULAR_EXIT);
			exit.right += 2;
			exit.bottom += 3;
			transitions.add(exit);
		}

		addCagesToCells();
	}
	
	//keep track of removed items as the level is changed. Dump them back into the level at the end.
	private ArrayList<Item> storedItems = new ArrayList<>();
	
	private void clearEntities(Rect safeArea){
		for (Heap heap : heaps.valueList()){
			if (safeArea == null || !safeArea.inside(cellToPoint(heap.pos))){
				for (Item item : heap.items){
					if (!(item instanceof Bomb) || ((Bomb)item).fuse == null){
						storedItems.add(item);
					}
				}
				heap.destroy();
			}
		}
		
		for (HeavyBoomerang.CircleBack b : Dungeon.hero.buffs(HeavyBoomerang.CircleBack.class)){
			if (b.activeDepth() == Dungeon.depth
					&& (safeArea == null || !safeArea.inside(cellToPoint(b.returnPos())))){
				storedItems.add(b.cancel());
			}
		}
		
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])){
			if (mob != tengu && (safeArea == null || !safeArea.inside(cellToPoint(mob.pos))) && !(mob instanceof Minion)){
				mob.destroy();
				if (mob.sprite != null)
					mob.sprite.killAndErase();
			}
		}
		for (Plant plant : plants.valueList()){
			if (safeArea == null || !safeArea.inside(cellToPoint(plant.pos))){
				plants.remove(plant.pos);
			}
		}
	}
	
	private void cleanMapState(){
		buildFlagMaps();
		cleanWalls();
		
		BArray.setFalse(visited);
		BArray.setFalse(mapped);
		
		for (Blob blob: blobs.values()){
			blob.fullyClear();
		}
		addVisuals(); //this also resets existing visuals
		traps.clear();

		for (CustomTilemap t : customTiles){
			if (t instanceof FadingTraps){
				((FadingTraps) t).remove();
			}
		}
		
		GameScene.resetMap();
		Dungeon.observe();
	}

	//randomly places up to 5 cages on tiles that are aside walls (but not torches or doors!)
	public void addCagesToCells(){
		Random.pushGenerator(Dungeon.seedCurDepth());
			for (int i = 0; i < 5; i++){
				int cell = randomPrisonCellPos();
				boolean valid = false;
				for (int j : PathFinder.NEIGHBOURS4){
					if (map[cell+j] == Terrain.WALL){
						valid = true;
					}
				}
				if (valid){
					Painter.set(this, cell, Terrain.REGION_DECO);
				}
			}

		Random.popGenerator();
	}

	@Override
	public Group addVisuals() {
		super.addVisuals();
		PrisonLevel.addPrisonVisuals(this, visuals);
		return visuals;
	}
	
	public void progress(){
		switch (state){
			case START:
				
				int tenguPos = pointToCell(tenguCellCenter);
				
				//if something is occupying Tengu's space, try to put him in an adjacent cell
				if (Actor.findChar(tenguPos) != null){
					ArrayList<Integer> candidates = new ArrayList<>();
					for (int i : PathFinder.NEIGHBOURS8){
						if (Actor.findChar(tenguPos + i) == null){
							candidates.add(tenguPos + i);
						}
					}
					
					if (!candidates.isEmpty()){
						tenguPos = Random.element(candidates);
					//if there are no adjacent cells, wait and do nothing
					} else {
						return;
					}
				}
				
				seal();
				Statistics.qualifiedForBossChallengeBadge = true;
				set(pointToCell(tenguCellDoor), Terrain.LOCKED_DOOR);
				GameScene.updateMap(pointToCell(tenguCellDoor));

				//moves intelligent allies with the hero, preferring closer pos to cell door
				int doorPos = pointToCell(tenguCellDoor);
				Mob.holdAllies(this, doorPos);
				Mob.restoreAllies(this, Dungeon.hero.pos, doorPos);
				
				tengu.state = tengu.HUNTING;
				tengu.pos = tenguPos;
				GameScene.add( tengu );
				tengu.notice();

				CellEmitter.get( tengu.pos ).burst( Speck.factory( Speck.WOOL ), 6 );
				Sample.INSTANCE.play( Assets.Sounds.PUFF );
				
				state = State.FIGHT_START;

				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						Music.INSTANCE.play(Assets.Music.PRISON_BOSS, true);
					}
				});
				break;
				
			case FIGHT_START:
				
				clearEntities( tenguCell ); //clear anything not in tengu's cell
				
				setMapPause();
				cleanMapState();

				Doom d = tengu.buff(Doom.class);
				Actor.remove(tengu);
				mobs.remove(tengu);
				TargetHealthIndicator.instance.target(null);
				tengu.sprite.kill();
				if (d != null) tengu.add(d);
				
				GameScene.flash(0x80FFFFFF);
				Sample.INSTANCE.play(Assets.Sounds.BLAST);
				
				state = State.FIGHT_PAUSE;
				break;

			case FIGHT_PAUSE:
				
				Dungeon.hero.interrupt();
				
				clearEntities( pauseSafeArea );
				Mob.holdAllies(this, Dungeon.hero.pos);
				Mob.restoreAllies(this, Dungeon.hero.pos);

				setMapArena();
				cleanMapState();
				
				tengu.state = tengu.HUNTING;
				tengu.pos = (arena.left + arena.width()/2) + width()*(arena.top+2);
				GameScene.add(tengu);
				tengu.timeToNow();
				tengu.notice();

				CellEmitter.get( tengu.pos ).burst( Speck.factory( Speck.WOOL ), 6 );
				
				GameScene.flash(0x80FFFFFF);
				Sample.INSTANCE.play(Assets.Sounds.BLAST);
				
				state = State.FIGHT_ARENA;
				break;
				
			case FIGHT_ARENA:
				
				unseal();
				
				Dungeon.hero.interrupt();
				Dungeon.hero.pos = tenguCell.left+4 + (tenguCell.top+2)*width();
				Dungeon.hero.sprite.interruptMotion();
				Dungeon.hero.sprite.place(Dungeon.hero.pos);
				Camera.main.snapTo(Dungeon.hero.sprite.center());
				
				tengu.pos = pointToCell(tenguCellCenter);
				tengu.sprite.place(tengu.pos);
				
				//remove all mobs, but preserve allies
				ArrayList<Mob> allies = new ArrayList<>();
				for(Mob m : mobs.toArray(new Mob[0])){
					if (m.alignment == Char.Alignment.ALLY && !m.properties().contains(Char.Property.IMMOVABLE)){
						allies.add(m);
						mobs.remove(m);
					}
				}
				
				setMapEnd();
				
				for (Mob m : allies){
					do{
						m.pos = randomTenguCellPos();
					} while (findMob(m.pos) != null || m.pos == Dungeon.hero.pos);
					if (m.sprite != null) m.sprite.place(m.pos);
					mobs.add(m);
				}
				
				tengu.die(Dungeon.hero);
				
				clearEntities(tenguCell);
				cleanMapState();
				
				for (Item item : storedItems) {
					if (!(item instanceof Tengu.BombAbility.BombItem)
						&& !(item instanceof Tengu.ShockerAbility.ShockerItem)) {
						drop(item, randomTenguCellPos());
					}
				}
				
				GameScene.flash(0x80FFFFFF);
				Sample.INSTANCE.play(Assets.Sounds.BLAST);
				
				state = State.WON;
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						Music.INSTANCE.fadeOut(5f, new Callback() {
							@Override
							public void call() {
								Music.INSTANCE.end();
							}
						});
					}
				});
				break;
		}
	}
	
	private boolean[] triggered = new boolean[]{false, false, false, false};
	
	@Override
	public void occupyCell(Char ch) {
		if (ch == Dungeon.hero){
			switch (state){
				case START:
					if (cellToPoint(ch.pos).y > tenguCell.top){
						progress();
					}
					break;
				case FIGHT_PAUSE:
					
					if (cellToPoint(ch.pos).y <= startHallway.top+1){
						progress();
					}
					break;
			}
		}

		super.occupyCell(ch);
	}
	
	@Override
	protected void createMobs() {
		tengu = new Tengu(); //We want to keep track of tengu independently of other mobs, he's not always in the level.
	}
	
	public Actor addRespawner() {
		return null;
	}
	
	@Override
	protected void createItems() {
		Random.pushGenerator(Random.Long());
			ArrayList<Item> bonesItems = Bones.get();
			if (bonesItems != null) {
				int pos;
				do {
					pos = randomRespawnCell(null);
				} while (pos == entrance());
				for (Item i : bonesItems) {
					drop(i, pos).setHauntedIfCursed().type = Heap.Type.REMAINS;
				}
			}
		Random.popGenerator();

		int pos;
		do {
			pos = randomPrisonCellPos();
		} while (solid[pos]);
		drop(new IronKey(Dungeon.chapterSize()*2), pos);
	}

	@Override
	public ArrayList<Item> getItemsToPreserveFromSealedResurrect() {
		ArrayList<Item> items = super.getItemsToPreserveFromSealedResurrect();

		items.addAll(storedItems);

		for (Item i : items.toArray(new Item[0])){
			if (i instanceof Tengu.BombAbility.BombItem || i instanceof Tengu.ShockerAbility.ShockerItem){
				items.remove(i);
			}
		}

		return items;
	}

	private int randomPrisonCellPos(){
		Rect room = startCells[Random.Int(startCells.length)];
		
		return Random.IntRange(room.left+1, room.right-2)
				+ width()*Random.IntRange(room.top+1, room.bottom-2);
	}
	
	public int randomTenguCellPos(){
		return Random.IntRange(tenguCell.left+1, tenguCell.right-2)
				+ width()*Random.IntRange(tenguCell.top+1, tenguCell.bottom-2);
	}
	
	public void cleanTenguCell(){
		
		traps.clear();
		Painter.fill(this, tenguCell, 1, Terrain.EMPTY);
		buildFlagMaps();

		for (CustomTilemap vis : customTiles){
			if (vis instanceof FadingTraps){
				((FadingTraps) vis).remove();
			}
		}
		
	}
	
	public void placeTrapsInTenguCell(float fill){
		
		Point tenguPoint = cellToPoint(tengu.pos);
		Point heroPoint = cellToPoint(Dungeon.hero.pos);
		
		PathFinder.setMapSize(7, 7);
		
		int tenguPos = tenguPoint.x-(tenguCell.left+1) + (tenguPoint.y-(tenguCell.top+1))*7;
		int heroPos = heroPoint.x-(tenguCell.left+1) + (heroPoint.y-(tenguCell.top+1))*7;
		
		boolean[] trapsPatch;

		//fill ramps up much faster during challenge, effectively 78%-90%
		if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
			fill = 0.675f + fill/4f;
		}

		int tries = 0;
		do {
			tries++;

			if (tries >= 100){
				tries = 0;
				fill -= 0.01f;
			}

			trapsPatch = Patch.generate(7, 7, fill, 0, false);

			PathFinder.buildDistanceMap(tenguPos, BArray.not(trapsPatch, null));
			//note that the effective range of fill is 40%-90%
			//so distance to tengu starts at 3-6 tiles and scales up to 7-8 as fill increases
		} while (((PathFinder.distance[heroPos] < Math.ceil(7*fill))
				|| (PathFinder.distance[heroPos] > Math.ceil(4 + 4*fill))));
		System.out.println(tries);

		PathFinder.setMapSize(width(), height());
		
		for (int i = 0; i < trapsPatch.length; i++){
			if (trapsPatch[i]) {
				int x = i % 7;
				int y = i / 7;
				int cell = x+tenguCell.left+1 + (y+tenguCell.top+1)*width();
				if (Blob.volumeAt(cell, StormCloud.class) == 0
						&& Blob.volumeAt(cell, Regrowth.class) <= 9
						&& Dungeon.level.plants.get(cell) == null
						&& Actor.findChar(cell) == null) {
					Level.set(cell, Terrain.SECRET_TRAP);
					setTrap(new TenguDartTrap().hide(), cell);
					CellEmitter.get(cell).burst(Speck.factory(Speck.LIGHT), 2);
				}
			}
		}
		
		GameScene.updateMap();
		
		FadingTraps t = new FadingTraps();
		t.fadeDelay = 2f;
		t.setCoveringArea(tenguCell);
		GameScene.add(t, false);
		customTiles.add(t);
	}
	
	@Override
	public int randomRespawnCell( Char ch ) {
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int i : PathFinder.NEIGHBOURS8){
			int cell = ENTRANCE_POS + i;
			if (passable[cell]
					&& Actor.findChar(cell) == null
					&& (!Char.hasProp(ch, Char.Property.LARGE) || openSpace[cell])){
				candidates.add(cell);
			}
		}

		if (candidates.isEmpty()){
			return -1;
		} else {
			return Random.element(candidates);
		}
	}
	
	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(PrisonLevel.class, "water_name");
			case Terrain.REGION_DECO:
			case Terrain.REGION_DECO_ALT:
				return Messages.get(PrisonLevel.class, "region_deco_name");
			default:
				return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.EMPTY_DECO:
				return Messages.get(PrisonLevel.class, "empty_deco_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(PrisonLevel.class, "bookshelf_desc");
			case Terrain.REGION_DECO:
			case Terrain.REGION_DECO_ALT:
				return Messages.get(PrisonLevel.class, "region_deco_desc");
			default:
				return super.tileDesc( tile );
		}
	}
	
	//TODO consider making this external to the prison boss level
	public static class FadingTraps extends CustomTilemap {
		
		{
			texture = Assets.Environment.TERRAIN_FEATURES;
		}
		
		Rect area;
		
		private float fadeDuration = 1f;
		private float initialAlpha = .4f;
		private float fadeDelay = 1f;
		
		public void setCoveringArea(Rect area){
			tileX = area.left;
			tileY = area.top;
			tileH = area.bottom - area.top;
			tileW = area.right - area.left;
			
			this.area = area;
		}
		
		@Override
		public Tilemap create() {
			Tilemap v = super.create();
			int[] data = new int[tileW*tileH];
			int cell;
			Trap t;
			int i = 0;
			for (int y = tileY; y < tileY + tileH; y++){
				cell = tileX + y*Dungeon.level.width();
				for (int x = tileX; x < tileX + tileW; x++){
					t = Dungeon.level.traps.get(cell);
					if (t != null){
						data[i] = t.color + t.shape*16;
					} else {
						data[i] = -1;
					}
					cell++;
					i++;
				}
			}
			
			v.map( data, tileW );
			setFade();
			return v;
		}
		
		@Override
		public String name(int tileX, int tileY) {
			int cell = (this.tileX+tileX) + Dungeon.level.width()*(this.tileY+tileY);
			if (Dungeon.level.traps.get(cell) != null){
				return Messages.titleCase(Dungeon.level.traps.get(cell).name());
			}
			return super.name(tileX, tileY);
		}
		
		@Override
		public String desc(int tileX, int tileY) {
			int cell = (this.tileX+tileX) + Dungeon.level.width()*(this.tileY+tileY);
			if (Dungeon.level.traps.get(cell) != null){
				return Dungeon.level.traps.get(cell).desc();
			}
			return super.desc(tileX, tileY);
		}
		
		private void setFade( ){
			if (vis == null){
				return;
			}
			
			vis.alpha( initialAlpha );
			Actor.addDelayed(new Actor() {
				
				{
					actPriority = HERO_PRIO+1;
				}
				
				@Override
				protected boolean act() {
					Actor.remove(this);
					
					if (vis != null && vis.parent != null) {
						Dungeon.level.customTiles.remove(FadingTraps.this);
						vis.parent.add(new AlphaTweener(vis, 0f, fadeDuration) {
							@Override
							protected void onComplete() {
								super.onComplete();
								vis.killAndErase();
								killAndErase();
							}
						});
					}
					
					return true;
				}
			}, fadeDelay);
		}

		private void remove(){
			if (vis != null){
				vis.killAndErase();
			}
			Dungeon.level.customTiles.remove(this);
		}
		
	}
	
	public static class ExitVisual extends CustomTilemap {
		
		{
			texture = Assets.Environment.PRISON_EXIT;
			
			tileW = 14;
			tileH = 11;
		}
		
		final int TEX_WIDTH = 256;
		
		private static byte[] render = new byte[]{
				0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0,
				1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0,
				1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
				1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
				1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
				1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0,
				1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0,
				1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0,
				0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0,
				0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0
		};
		
		@Override
		public Tilemap create() {
			Tilemap v = super.create();
			int[] data = mapSimpleImage(0, 0, TEX_WIDTH);
			for (int i = 0; i < data.length; i++){
				if (render[i] == 0) data[i] = -1;
			}
			v.map(data, tileW);
			return v;
		}
		
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			tileX = 11;
			tileY = 10;
			tileW = 14;
			tileH = 11;
		}
	}
	
	public static class ExitVisualWalls extends CustomTilemap {
		
		{
			texture = Assets.Environment.PRISON_EXIT;
			
			tileW = 14;
			tileH = 22;
		}
		
		final int TEX_WIDTH = 256;
		
		private static byte[] render = new byte[]{
				0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1,
				0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1,
				1, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1,
				0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1,
				0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 1,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1
		};
		
		@Override
		public Tilemap create() {
			Tilemap v = super.create();
			int[] data = mapSimpleImage(0, 10, TEX_WIDTH);
			for (int i = 0; i < data.length; i++){
				if (render[i] == 0) data[i] = -1;
			}
			v.map(data, tileW);
			return v;
		}
		
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			tileX = 11;
			tileY = 10;
			tileW = 14;
			tileH = 22;
		}
		
	}
	
}
