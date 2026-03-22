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

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.badlogic.gdx.graphics.Color;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Chaosstone;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.PrisonPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.AbyssalSpawnerRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CorrosionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CursingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisarmingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DistortionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlashingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GuardianTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RockfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.StormTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WarpingTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Group;
import com.watabou.noosa.Halo;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.GameMath;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class AbyssLevel extends RegularLevel {
    {
        color1 = 0x232424;
        color2 = 0x3e4040;
        viewDistance = 2;
    }

    public static final int BRANCH = 10;

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
        ArrayList<Room> rooms = super.initRooms();

        rooms.add(new AbyssalSpawnerRoom());

        return rooms;
    }

    @Override
    public void create() {
        addItemToSpawn(Generator.random(Generator.Category.FOOD));
        addItemToSpawn( new com.shatteredpixel.shatteredpixeldungeon.items.Torch() );
        addItemToSpawn( new com.shatteredpixel.shatteredpixeldungeon.items.Torch() );
        for (int i = 0; i < GameMath.gate(1, Dungeon.scalingDepth() / 5 - 5
                - (Dungeon.scalingDepth() % 5 == 0 ? 1 : 0), Integer.MAX_VALUE); i++){
            addItemToSpawn(new Chaosstone());
            if (Random.Int(2) == 0) addItemToSpawn(new Chaosstone());
        }
        super.create();
    }

    @Override
    protected int standardRooms(boolean forceMax) {
        if (forceMax) return 32;
        return Math.max(30, 23 + Dungeon.depth/3)+Random.chances(new float[]{3, 2, 1, 1, 1});
    }

    @Override
    protected int specialRooms(boolean forceMax) {
        if (forceMax) return 3;
        //2 to 4, average 2.5
        return 2 + Random.chances(new float[]{1, 1});
    }

    public Actor addRespawner() {
        return null;
    }

    @Override
    protected Painter painter() {
        return new PrisonPainter()
                .setWater(feeling == Feeling.WATER ? 0.90f : 0.30f, 4)
                .setGrass(feeling == Feeling.GRASS ? 0.80f : 0.20f, 3)
                .setTraps(nTraps(), trapClasses(), trapChances());
    }

    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_ABYSS;
    }

    @Override
    public String waterTex() {
        return Assets.Environment.WATER_ABYSS;
    }

    public static final Class[] trapClasses = {
            FrostTrap.class, StormTrap.class, CorrosionTrap.class, DisintegrationTrap.class,
            RockfallTrap.class, FlashingTrap.class, GuardianTrap.class,
            DisarmingTrap.class, WarpingTrap.class, CursingTrap.class, GrimTrap.class, PitfallTrap.class, DistortionTrap.class};

    @Override
    protected Class<?>[] trapClasses() {
        return trapClasses;
    }

    @Override
    protected float[] trapChances() {
        return new float[]{
                2, 2, 2, 2,
                2, 2, 2,
                1, 1, 1, 1, 1, 1 };
    }

    @Override
    public boolean activateTransition(Hero hero, LevelTransition transition) {
        if (transition.type == LevelTransition.Type.SURFACE){
            return false;
        } else {
            return super.activateTransition(hero, transition);
        }
    }

    @Override
    public String tileName( int tile ) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(AbyssLevel.class, "water_name");
            default:
                return super.tileName( tile );
        }
    }

    @Override
    public String tileDesc(int tile) {
        return Messages.get(AbyssLevel.class, "not_recognizable");
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        addPrisonVisuals(this, visuals);
        return visuals;
    }

    public static void addPrisonVisuals(Level level, Group group){
        for (int i=0; i < level.length(); i++) {
            if (level.map[i] == Terrain.WALL_DECO) {
                group.add( new Torch( i ) );
            }
            if (level.map[i] == Terrain.REGION_DECO || level.map[i] == Terrain.REGION_DECO_ALT) {
                group.addToFront( new GreenFlame( i ) );
            }
        }
    }

    public static class Torch extends Emitter {

        private int pos;

        public Torch( int pos ) {
            super();

            this.pos = pos;

            PointF p = DungeonTilemap.tileCenterToWorld( pos );
            pos( p.x - 1, p.y + 2, 2, 0 );

            pour( ShadowParticle.UP, 0.15f );

            add( new Halo( 12, 0x6b6b6b, 0.4f ).point( p.x, p.y + 1 ) );
        }

        @Override
        public void update() {
            if (visible = (pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[pos])) {
                super.update();
            }
        }
    }

    public static class GreenFlame extends Emitter {

        private int pos;

        public static final Emitter.Factory factory = new Factory() {
            @Override
            public void emit( Emitter emitter, int index, float x, float y ) {
                GreenFlameParticle p = (GreenFlameParticle) emitter.recycle( GreenFlameParticle.class );
                p.reset( x, y );
            }
            @Override
            public boolean lightMode() {
                return true;
            }
        };

        public GreenFlame( int pos ) {
            super();

            this.pos = pos;

            PointF p = DungeonTilemap.raisedTileCenterToWorld( pos );
            pos( p.x - 2, p.y - 5, 4, 4 );

            pour( factory, 0.1f );
        }

        @Override
        public void update() {
            if (visible = (pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[pos])) {
                super.update();
            }
        }

    }

    public static class GreenFlameParticle extends ElmoParticle {

        public GreenFlameParticle(){
            super();
            acc.set( 0, -40 );
            Color tempColor = new Color().fromHsv(Random.Float(360), Random.Float(0.5f, 1f), 0.9f);
            color(tempColor.r, tempColor.g, tempColor.b);
        }

    }
}