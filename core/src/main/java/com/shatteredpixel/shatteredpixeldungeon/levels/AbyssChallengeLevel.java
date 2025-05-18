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
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.treasurebags.AccessoriesBag;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.treasurebags.EquipmentBag;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.treasurebags.GenericBag;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.treasurebags.HolsterBag;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.treasurebags.PotionsBag;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.treasurebags.ScrollsBag;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.treasurebags.SeedsBag;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.treasurebags.StonesBag;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.treasurebags.TreasureBag;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.HallsPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EmptyRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.EntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.ExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashMap;

public class AbyssChallengeLevel extends RegularLevel {

    {
        color1 = 0x727272;
        color2 = 0xe8e8e8;
    }

    @Override
    public void playLevelMusic() {
        Music.INSTANCE.playTracks(
                new String[]{Assets.Music.ABYSS_1, Assets.Music.ABYSS_2, Assets.Music.ABYSS_3,
                        Assets.Music.ABYSS_4, Assets.Music.ABYSS_5},
                new float[]{1, 1, 1, 1, 0.25f},
                true);
    }

    @Override
    protected ArrayList<Room> initRooms() {
        ArrayList<Room> initRooms = new ArrayList<>();

        initRooms.add(roomEntrance = EntranceRoom.createEntrance());
        initRooms.add(roomExit = ExitRoom.createExit());

        int standards = standardRooms(true);
        for (int i = 0; i < standards; i++) {
            StandardRoom s = new EmptyRoom();
            //force to normal size
            s.setSizeCat(0, 0);
            initRooms.add(s);
        }

        initRooms.add(new ShopRoom());
        return initRooms;
    }

    private static HashMap<Class<? extends TreasureBag>, Float> bagLootTable = new HashMap<>();
    static {
        bagLootTable.put(GenericBag.class, 25f);

        bagLootTable.put(SeedsBag.class,   15f);
        bagLootTable.put(StonesBag.class,  15f);
        bagLootTable.put(PotionsBag.class, 10f);
        bagLootTable.put(ScrollsBag.class, 10f);

        bagLootTable.put(EquipmentBag.class,   6f);
        bagLootTable.put(HolsterBag.class,     5f);
        bagLootTable.put(AccessoriesBag.class, 4f);
    }

    @Override
    protected void createItems() {
        int nItems = 25;

        if (Dungeon.hero.heroClass == HeroClass.ADVENTURER) nItems *= 2;
        if (Dungeon.isChallenged(Conducts.Conduct.NO_LOOT)){
            nItems = 1;
            if (Dungeon.hero.heroClass == HeroClass.ADVENTURER) nItems = 2;
        }

        if (Dungeon.mode == Dungeon.GameMode.BIGGER){
            nItems = Math.round(nItems * 1.75f);
        }

        for (int i=0; i < nItems; i++) {

            Item toDrop = Reflection.newInstance(Random.chances(bagLootTable));
            if (toDrop == null) continue;

            int cell = randomDropCell();
            if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
                map[cell] = Terrain.GRASS;
                losBlocking[cell] = false;
            }

            Heap.Type type;
            switch (Random.Int( 20 )) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    type = Heap.Type.CHEST;
                    break;
                default:
                    type = Heap.Type.HEAP;
                    break;
            }
            Heap dropped = drop( Challenges.process(toDrop), cell );
            dropped.type = type;
        }
        itemsToSpawn.add(new TrinketCatalyst());
        itemsToSpawn.add(new StoneOfEnchantment());
        itemsToSpawn.add(new Stylus());

        for (Item item : itemsToSpawn) {
            int cell = randomDropCell();
            if (item instanceof TrinketCatalyst){
                drop( item, cell ).type = Heap.Type.LOCKED_CHEST;
                int keyCell = randomDropCell();
                drop( new GoldenKey(Dungeon.depth), keyCell ).type = Heap.Type.HEAP;
                if (map[keyCell] == Terrain.HIGH_GRASS || map[keyCell] == Terrain.FURROWED_GRASS) {
                    map[keyCell] = Terrain.GRASS;
                    losBlocking[keyCell] = false;
                }
            } else {
                drop( Challenges.process(item), cell ).type = Heap.Type.HEAP;
            }
            if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
                map[cell] = Terrain.GRASS;
                losBlocking[cell] = false;
            }
        }
    }

    @Override
    protected int nTraps() {
        return 0;
    }

    @Override
    protected Painter painter() {
        return new HallsPainter()
                .setWater(feeling == Feeling.WATER ? 0.90f : 0.30f, 4)
                .setGrass(feeling == Feeling.GRASS ? 0.80f : 0.20f, 3)
                .setTraps(nTraps(), trapClasses(), trapChances());
    }

    @Override
    protected int standardRooms(boolean forceMax) {
        return 20;
    }

    @Override
    protected int specialRooms(boolean forceMax) {
        return 0;
    }

    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_ABYSS2;
    }

    @Override
    public String waterTex() {
        return Assets.Environment.WATER_ABYSS;
    }

    @Override
    public String tileName( int tile ) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(AbyssChallengeLevel.class, "water_name");
            case Terrain.GRASS:
                return Messages.get(AbyssChallengeLevel.class, "grass_name");
            case Terrain.HIGH_GRASS:
                return Messages.get(AbyssChallengeLevel.class, "high_grass_name");
            case Terrain.STATUE:
            case Terrain.STATUE_SP:
                return Messages.get(AbyssChallengeLevel.class, "statue_name");
            default:
                return super.tileName( tile );
        }
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(AbyssChallengeLevel.class, "water_desc");
            case Terrain.STATUE:
            case Terrain.STATUE_SP:
                return Messages.get(AbyssChallengeLevel.class, "statue_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(AbyssChallengeLevel.class, "bookshelf_desc");
            default:
                return super.tileDesc( tile );
        }
    }
}
