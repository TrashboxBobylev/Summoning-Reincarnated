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

package com.shatteredpixel.shatteredpixeldungeon.items.magic;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostBurn;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.damagesource.DamageProperty;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.damagesource.DamageSource;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.BArray;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.EnumSet;

public class StarBlazing extends ConjurerSpell implements DamageSource {

    {
        image = ItemSpriteSheet.STARS;
        collision = Ballistica.STOP_TARGET;
        alignment = Alignment.OFFENSIVE;
        usesTargeting = true;
    }

    @Override
    public void effect(Ballistica trajectory) {
        if (range(type()) < 1){
            Char ch = Actor.findChar(trajectory.collisionPos);
            if (ch != null && (ch.alignment != Char.Alignment.ALLY || (Dungeon.hero.hasTalent(Talent.CONCENTRATED_SUPPORT) && !(ch instanceof Hero)))){
                ch.damage(damageRoll(), this);
                Buff.affect(ch, Minion.ReactiveTargeting.class, 10f);

                for (int b : PathFinder.NEIGHBOURS8){
                    ((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
                            MagicMissile.MAGIC_MISSILE,
                            ch.sprite,
                            ch.pos+b,
                            null
                    );
                }
                Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f) );
                if (isEmpowered()){
                    Buff.affect(ch, FrostBurn.class).reignite(ch, heroLvl()/4f);
                }
            }
        }
        else {
            PathFinder.buildDistanceMap(trajectory.collisionPos, BArray.not(Dungeon.level.solid, null), range(type()));
            for (int i = 0; i < PathFinder.distance.length; i++) {
                if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                    Char ch = Actor.findChar(i);
                    if (ch != null && (ch.alignment != Char.Alignment.ALLY || (Dungeon.hero.hasTalent(Talent.CONCENTRATED_SUPPORT) && !(ch instanceof Hero) && i == trajectory.collisionPos))) {
                        ch.damage(damageRoll(), this);
                        Buff.affect(ch, Minion.ReactiveTargeting.class, 10f);

                        for (int b : PathFinder.NEIGHBOURS8) {
                            ((MagicMissile) curUser.sprite.parent.recycle(MagicMissile.class)).reset(
                                    MagicMissile.MAGIC_MISSILE,
                                    ch.sprite,
                                    ch.pos + b,
                                    null
                            );
                        }
                        Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f));

                        if (isEmpowered()){
                            Buff.affect(ch, FrostBurn.class).reignite(ch, heroLvl()/3f);
                        }
                        if (ch != Actor.findChar(trajectory.collisionPos))
                            afterZap(Dungeon.hero, new Ballistica(Dungeon.hero.pos, ch.pos, Ballistica.STOP_TARGET));
                    }
                }
            }
        }
    }

    @Override
    public int manaCost(int type) {
        switch (type){
            case 1: return 1;
            case 2: return 4;
            case 3: return 10;
        }
        return 0;
    }

    public int range(int rank) {
        switch (rank){
            case 1: return 0;
            case 2: return 1;
            case 3: return 8;
        }
        return 0;
    }

    private int min(){
        return (int) (2 + heroLvl() / 4f);
    }

    private int max(){
        return (int) (12 + heroLvl()/1.25f);
    }

    private int damageRoll() {
        return Random.NormalIntRange(min(), max());
    }

    @Override
    public String spellDesc() {
        return Messages.get(this, "desc", GameMath.printAverage(min(), max()));
    }

    @Override
    public String spellTypeMessage(int type) {
        return Messages.get(this, "type", range(type)*2+1);
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        Sample.INSTANCE.play(Assets.Sounds.READ);
        Dungeon.hero.sprite.zap(bolt.collisionPos, new Callback() {
            @Override
            public void call() {
                Dungeon.hero.sprite.idle();
                if (type() == 3){
                    PathFinder.buildDistanceMap(bolt.collisionPos, BArray.not(Dungeon.level.solid, null), range(type()));
                    for (int i = 0; i < PathFinder.distance.length; i++) {
                        if (PathFinder.distance[i] < Integer.MAX_VALUE && i != bolt.collisionPos) {
                            Char ch = Actor.findChar(i);
                            if (ch != null && ch.alignment != Char.Alignment.ALLY) {
                                MissileSprite starSprite = (MissileSprite) Dungeon.hero.sprite.parent.recycle(MissileSprite.class);
                                Item sprite = new ProjectileStar();
                                PointF starDest = DungeonTilemap.tileCenterToWorld(i);
                                PointF starSource = DungeonTilemap.raisedTileCenterToWorld(Dungeon.hero.pos);
                                starSource.y -= 150;

                                starSprite.reset( starSource, starDest, sprite, () -> {});
                            }
                        }
                    }
                }
                MissileSprite starSprite = (MissileSprite) Dungeon.hero.sprite.parent.recycle(MissileSprite.class);
                Item sprite = new ProjectileStar();
                PointF starDest = DungeonTilemap.tileCenterToWorld(bolt.collisionPos);
                PointF starSource = DungeonTilemap.raisedTileCenterToWorld(Dungeon.hero.pos);
                starSource.y -= 150;

                starSprite.reset( starSource, starDest, sprite, callback);
            }
        });
    }

    @Override
    public String empowermentDesc() {
        return Messages.get(this, "desc_empower", heroLvl() / (type() == 1 ? 4 : 3));
    }

    @Override
    public String empowermentTypeDesc(int type) {
        return Messages.get(this, "type_empower", heroLvl() / (type == 1 ? 4 : 3));
    }

    @Override
    public EnumSet<DamageProperty> initDmgProperties() {
        return EnumSet.of(DamageProperty.MAGICAL, DamageProperty.SPIRIT);
    }

    public static class ProjectileStar extends Item {
        {
            image = ItemSpriteSheet.STARS;
        }

        @Override
        public Emitter emitter() {
            Emitter e = new Emitter();
            e.pos(6, 6);
            e.fillTarget = false;
            e.pour(MagicMissile.ConcentratedParticle.FACTORY, 0.03f);
            return e;
        }
    };
}
