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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;

import java.util.ArrayList;

public class Trident extends MissileWeapon {
	
	{
		image = ItemSpriteSheet.TRIDENT;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 0.9f;
	}

    public float min(float lvl, int rank) {
        switch (rank){
            case 1: return 8 + lvl*2;
            case 2: return 8 + lvl*2;
            case 3: return 4 + lvl*1.5f;
        }
        return 0;
    }

    public float max(float lvl, int rank) {
        switch (rank){
            case 1: return 15 + lvl*4.5f;
            case 2: return 15 + lvl*4.5f;
            case 3: return 10 + lvl*3;
        }
        return 0;
    }

    public float baseUses(float lvl, int rank){
        switch (rank){
            case 1: return 8 + lvl*1.75f;
            case 2: return 4 + lvl*0.875f;
            case 3: return 6 + lvl*1.25f;
        }
        return 1;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (rank() == 1){
            if (defender.isWet())
                damage *= 1.5f;
        }
        return super.proc(attacker, defender, damage);
    }

    @Override
    public float castDelay(Char user, int cell) {
        return super.castDelay(user, cell)*2;
    }

    @Override
    protected int collisionProperties() {
        if (rank() == 2){
            return Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
        }
        return super.collisionProperties();
    }

    @Override
    public void cast(Hero user, int dst) {
        super.cast(user, dst);
        if (rank() == 2){
            Ballistica path = new Ballistica(user.pos, dst, collisionProperties());
            ArrayList<Char> targets = new ArrayList<>();

            for (int cell: path.subPath(1, path.dist)){
                Char target;
                if ((target = Actor.findChar(cell)) != null && cell != dst){
                    targets.add(target);
                }
            }

            for (Char target: targets){
                ((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
                        reset(user.sprite,
                                target.sprite,
                                new invisproj(),
                                () -> {
                                    user.shoot(target, Trident.this);
                                });
            }
            if (!targets.isEmpty() && Actor.findChar(path.collisionPos) == null){
                decrementDurability();
            }
        }
        if (rank() == 3){
            if (user.rooted){
                PixelScene.shake( 1, 1f );
                return;
            }

            if (user.isWet()){
                Ballistica route = new Ballistica(user.pos, dst, collisionProperties());
                int cell = route.collisionPos;

                //can't occupy the same cell as another char, so move back one.
                int backTrace = route.dist-1;
                while (Actor.findChar( cell ) != null && cell != user.pos) {
                    cell = route.path.get(backTrace);
                    backTrace--;
                }
                Sample.INSTANCE.play(Assets.Sounds.MISS, 2f);

                final int dest = cell;
                float distance = Math.max( 1f, Dungeon.level.trueDistance( user.pos, cell ));
                final Emitter heroEmit = user.sprite.emitter();
                user.busy();
                user.sprite.jump(user.pos, cell, distance *0.25f, distance*0.075f, new Callback() {
                    @Override
                    public void call() {
                        if (Actor.findChar(route.collisionPos) == null)
                            decrementDurability();
                        user.move(dest);
                        Dungeon.level.occupyCell(user);
                        Dungeon.observe();
                        GameScene.updateFog();
                        heroEmit.on = false;
                        heroEmit.killAndErase();
                        Dungeon.hero.spendAndNext(-1f);

                        Invisibility.dispel();

                        for (int cell: route.subPath(0, route.dist-1)){
                            GameScene.add(Blob.seed(cell, 2, Electricity.class));
                        }
                    }
                });
                heroEmit.pour(SparkParticle.FACTORY, 0.025f);
            }
        }
    }

    public static class invisproj extends Item {
        {
            image = ItemSpriteSheet.LIGHT_KNIFE+1;
        }
    }
}
