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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.yapdvariants;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM200;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM201;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DM200Sprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class StrongerDM200 extends DM200 {
    {
        spriteClass = Sprite.class;

        HP = HT = 180;
        defenseSkill = 19;
        viewDistance = Light.DISTANCE;

        lootChance = 0.33f;

        EXP = 15;
        maxLvl = 30;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 35, 50 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 36;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 12);
    }

    public Item createLoot() {
        Dungeon.LimitedDrops.DM200_EQUIP.count++;
        //uses probability tables for halls
        if (loot == Generator.Category.WEAPON){
            return Generator.randomWeapon(5, true);
        } else {
            return Generator.randomArmor(5);
        }
    }

    protected void zap(){
        spend( TICK );
        ventCooldown = 15;

        Ballistica trajectory = new Ballistica(pos, enemy.pos, Ballistica.STOP_TARGET);

        for (int i : trajectory.subPath(0, trajectory.dist)){
            GameScene.add(Blob.seed(i, 10, CorrosiveGas.class).setStrength(10));
        }
        GameScene.add(Blob.seed(trajectory.collisionPos, 50, CorrosiveGas.class).setStrength(10));

    }

    public static class Sprite extends DM200Sprite {
        public void zap( int cell ) {

            super.zap( cell );

            MagicMissile.boltFromChar( parent,
                    MagicMissile.CORROSION,
                    this,
                    cell,
                    new Callback() {
                        @Override
                        public void call() {
                            Sample.INSTANCE.play( Assets.Sounds.GAS );
                            ((StrongerDM200)ch).onZapComplete();
                        }
                    } );
            Sample.INSTANCE.play( Assets.Sounds.MISS, 1f, 1.5f );
            GLog.w(Messages.get(DM201.class, "vent"));
        }
    }
}
