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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.EbonyMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SupplyRation;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Chaosstone;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.MimicTooth;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.Builder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.LoopBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.ArenaPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss.SewerBossExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.LaboratoryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.EntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ConfusionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CorrosionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CursingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisarmingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DistortionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlashingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GuardianTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RockfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.StormTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WarpingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WornDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Group;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ArenaLevel extends RegularLevel {

    {
        color1 = 0x4b6636;
        color2 = 0xf2f2f2;
    }

    protected Painter painter() {
        return new ArenaPainter()
                .setWater( 0.35f, 4 )
                .setGrass( 0.25f, 3 )
                .setTraps(nTraps(), trapClasses(), trapChances());
    }

    private int arenaDoor;
    private boolean enteredArena = false;
    private boolean keyDropped = false;

    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_CITY;
    }

    @Override
    public String waterTex() {
        return Assets.Environment.WATER_CITY;
    }

    @Override
    protected Class<?>[] trapClasses() {
        return new Class[]{
                FrostTrap.class, SummoningTrap.class, StormTrap.class, CorrosionTrap.class, ConfusionTrap.class,
                RockfallTrap.class, FlashingTrap.class, WornDartTrap.class, GuardianTrap.class, ExplosiveTrap.class,
                DisarmingTrap.class, WarpingTrap.class, CursingTrap.class, GrimTrap.class, PitfallTrap.class, DistortionTrap.class };
    }

    @Override
    protected float[] trapChances() {
        return new float[]{
                1, 1, 1, 1, 1,
                1, 1, 1, 1,
                1, 1, 1 };
    }

    private static final String DOOR	= "door";
    private static final String ENTERED	= "entered";
    private static final String DROPPED	= "droppped";
    private static final String STAIRS	= "stairs";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( DOOR, arenaDoor );
        bundle.put( ENTERED, enteredArena );
        bundle.put( DROPPED, keyDropped );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        arenaDoor = bundle.getInt( DOOR );
        enteredArena = bundle.getBoolean( ENTERED );
        keyDropped = bundle.getBoolean( DROPPED );
        roomExit = roomEntrance;
    }

    @Override
    protected int nTraps() {
        return 0;
    }

    @Override
    protected Builder builder() {
        return new LoopBuilder()
                .setLoopShape( 2 ,
                        Random.Float(0f, 0.6f),
                        Random.Float(0f, 0.6f));
    }

    @Override
    protected int specialRooms(boolean forceMax) {
        return Dungeon.scalingDepth()/Dungeon.chapterSize();
    }

    public Actor addRespawner() {
        return null;
    }

    protected ArrayList<Room> initRooms() {
        ArrayList<Room> initRooms = new ArrayList<>();
        initRooms.add ( roomEntrance = new EntranceRoom());
        initRooms.add( new ShopRoom());
        initRooms.add( roomExit = new SewerBossExitRoom());

        for (int i = 0; i < 2 + Dungeon.depth * 2 / Dungeon.chapterSize(); i++) {
            StandardRoom s = StandardRoom.createRoom();
            initRooms.add(s);
        }
        if (Dungeon.depth % 4 == 0){
            initRooms.add(new LaboratoryRoom());
        }

        return initRooms;
    }

    @Override
    public void pressCell(int cell) {
        super.pressCell(cell);

        for (Heap heap : Dungeon.level.heaps.valueList().toArray(new Heap[0])){
            if (heap.pos == cell){
                for (Item item : heap.items){
                    if (item instanceof SkeletonKey){
                        keyDropped = true;
                    }
                }
            }
        }
    }

    @Override
    public void create() {

        for (int i = 0; i < (Dungeon.depth - Dungeon.chapterSize()*5 + 1) / Dungeon.chapterSize(); i++){
            addItemToSpawn(new Chaosstone());
            if (Random.Int(2) == 0) addItemToSpawn(new Chaosstone());
        }
        if (Dungeon.depth % 2 == 0) addItemToSpawn(new Food());

        super.create();
    }

    @Override
    protected void createItems() {
        for (Item item : itemsToSpawn) {
            int cell = randomDropCell();
            drop( item, cell ).type = Heap.Type.HEAP;
            if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
                map[cell] = Terrain.GRASS;
                losBlocking[cell] = false;
            }
        }

        //use a separate generator for this to prevent held items and meta progress from affecting levelgen
        Random.pushGenerator( Random.Long() );
        ArrayList<Item> bonesItems = Bones.get();
        if (bonesItems != null) {
            int cell = randomDropCell();
            if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
                map[cell] = Terrain.GRASS;
                losBlocking[cell] = false;
            }
            for (Item i : bonesItems) {
                drop(Challenges.process(i), cell).setHauntedIfCursed().type = Heap.Type.REMAINS;
            }
        }
        Random.popGenerator();

        Random.pushGenerator( Random.Long() );
        DriedRose rose = Dungeon.hero.belongings.getItem( DriedRose.class );
        if (rose != null && rose.isIdentified() && !rose.cursed && Ghost.Quest.completed()){
            //aim to drop 1 petal every 2 floors
            int petalsNeeded = (int) Math.ceil((float)((Dungeon.depth / 2) - rose.droppedPetals) / 3);

            for (int i=1; i <= petalsNeeded; i++) {
                //the player may miss a single petal and still max their rose.
                if (rose.droppedPetals < 11) {
                    Item item = new DriedRose.Petal();
                    int cell = randomDropCell();
                    drop( item, cell ).type = Heap.Type.HEAP;
                    if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
                        map[cell] = Terrain.GRASS;
                        losBlocking[cell] = false;
                    }
                    rose.droppedPetals++;
                }
            }
        }

        Random.popGenerator();

        //cached rations try to drop in a special room on floors 2/4/7, to a max of 2/3
        //we incremented dropped by 2 for compatibility with pre-v2.4 saves (when the talent dropped 4/6 items)
        Random.pushGenerator( Random.Long() );
        if (Dungeon.hero.hasTalent(Talent.CACHED_RATIONS)){
            Talent.CachedRationsDropped dropped = Buff.affect(Dungeon.hero, Talent.CachedRationsDropped.class);
            int targetFloor = (int)(2 + dropped.count());
            if (dropped.count() > 4) targetFloor++;
            if (Dungeon.depth >= targetFloor && dropped.count() < 2 + 2*Dungeon.hero.pointsInTalent(Talent.CACHED_RATIONS)){
                int cell;
                int tries = 100;
                boolean valid;
                do {
                    cell = randomDropCell(SpecialRoom.class);
                    valid = cell != -1 && !(room(cell) instanceof SecretRoom)
                            && !(room(cell) instanceof ShopRoom)
                            && map[cell] != Terrain.EMPTY_SP
                            && map[cell] != Terrain.WATER
                            && map[cell] != Terrain.PEDESTAL;
                } while (tries-- > 0 && !valid);
                if (valid) {
                    if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
                        map[cell] = Terrain.GRASS;
                        losBlocking[cell] = false;
                    }
                    drop(Challenges.process(new SupplyRation()), cell).type = Heap.Type.CHEST;
                    dropped.countUp(2);
                }
            }
        }
        Random.popGenerator();

        //ebony mimics >:)
        Random.pushGenerator(Random.Long());
        if (Random.Float() < MimicTooth.ebonyMimicChance()){
            ArrayList<Integer> candidateCells = new ArrayList<>();
            if (Random.Int(2) == 0){
                for (Heap h : heaps.valueList()){
                    if (h.type == Heap.Type.HEAP
                            && !(room(h.pos) instanceof SpecialRoom)
                            && findMob(h.pos) == null){
                        candidateCells.add(h.pos);
                    }
                }
            } else {
                if (Random.Int(5) == 0 && findMob(exit()) == null){
                    candidateCells.add(exit());
                } else {
                    for (int i = 0; i < length(); i++) {
                        if (map[i] == Terrain.DOOR && findMob(i) == null) {
                            candidateCells.add(i);
                        }
                    }
                }
            }

            int pos = Random.element(candidateCells);
            mobs.add(Mimic.spawnAt(pos, EbonyMimic.class, false));
        }
        Random.popGenerator();
    }

    @Override
    public int randomRespawnCell( Char ch ) {
        int cell;
        do {
            cell = pointToCell( roomEntrance.random() );
        } while (!passable[cell]
                || (Char.hasProp(ch, Char.Property.LARGE) && !openSpace[cell])
                || Actor.findChar(cell) != null);
        return cell;
    }

    @Override
    public void seal() {
        super.seal();
        set( entrance(), Terrain.WALL );
        GameScene.updateMap( entrance() );
        GameScene.ripple( entrance() );
    }

    @Override
    public void occupyCell(Char ch) {
        super.occupyCell(ch);

        if (ch == Dungeon.hero && !roomEntrance.inside(cellToPoint(ch.pos)) && !locked && !keyDropped){
            seal();
        }
    }

    @Override
    public boolean activateTransition(Hero hero, LevelTransition transition) {
        if (transition.type == LevelTransition.Type.REGULAR_ENTRANCE || transition.type == LevelTransition.Type.SURFACE &&
                (Dungeon.level.locked || Dungeon.depth == 1)) {
                GLog.w(Messages.get(this, "entrance_blocked"));
            return false;
        } else {
            return super.activateTransition(hero, transition);
        }
    }

    @Override
    public void unseal() {
            super.unseal();

            set( entrance(), Terrain.ENTRANCE );
            GameScene.updateMap( entrance() );
    }

    @Override
    public String tileName( int tile ) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(CityLevel.class, "water_name");
            case Terrain.HIGH_GRASS:
                return Messages.get(CityLevel.class, "high_grass_name");
            default:
                return super.tileName( tile );
        }
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.ENTRANCE:
                return Messages.get(CityLevel.class, "entrance_desc");
            case Terrain.EXIT:
                return Messages.get(CityLevel.class, "exit_desc");
            case Terrain.WALL_DECO:
            case Terrain.EMPTY_DECO:
                return Messages.get(CityLevel.class, "deco_desc");
            case Terrain.EMPTY_SP:
                return Messages.get(CityLevel.class, "sp_desc");
            case Terrain.STATUE:
            case Terrain.STATUE_SP:
                return Messages.get(CityLevel.class, "statue_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(CityLevel.class, "bookshelf_desc");
            default:
                return super.tileDesc( tile );
        }
    }

    @Override
    public Group addVisuals( ) {
        super.addVisuals();
        CityLevel.addCityVisuals(this, visuals);
        if (map[exit()-1] != Terrain.WALL_DECO) visuals.add(new PrisonLevel.Torch(exit()-1));
        if (map[exit()+1] != Terrain.WALL_DECO) visuals.add(new PrisonLevel.Torch(exit()+1));
        return visuals;
    }
}
