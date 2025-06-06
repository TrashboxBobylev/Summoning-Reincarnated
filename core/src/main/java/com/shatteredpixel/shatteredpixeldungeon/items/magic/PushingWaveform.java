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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulParalysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class PushingWaveform extends ConjurerSpell {

    {
        image = ItemSpriteSheet.CAMOUFLAGE;
        alignment = Alignment.OFFENSIVE;
    }

    ConeAOE cone;

    @Override
    public void effect(Ballistica bolt) {

        ArrayList<Char> affectedChars = new ArrayList<>();
        for( int cell : cone.cells ){

            //ignore caster cell
            if (cell == bolt.sourcePos){
                continue;
            }

            Char ch = Actor.findChar( cell );
            if (ch != null) {
                affectedChars.add(ch);
            }
        }

        for (int cell : cone.cells){
            Char ch = Actor.findChar( cell );
            if (ch != null) {
                affectedChars.add(ch);
            }
        }
        for (Char ch : affectedChars){
            if (ch.alignment == Char.Alignment.ENEMY) {
                //trace a ballistica to our target (which will also extend past them
                Ballistica trajectory = new Ballistica(Dungeon.hero.pos, ch.pos, Ballistica.STOP_TARGET);
                //trim it to just be the part that goes past them
                trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.FRIENDLY_PROJECTILE);
                WandOfBlastWave.throwChar(ch, trajectory, 3 + rank(), true, true, this);
                Buff.affect(ch, Minion.ReactiveTargeting.class, 10f);
                if (isEmpowered()){
                    Buff.affect(ch, SoulParalysis.class, 3f);
                }
            } else if (ch.alignment == Char.Alignment.ALLY){
                ArrayList<Integer> respawnPoints = new ArrayList<Integer>();

                for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                    int p = Dungeon.hero.pos + PathFinder.NEIGHBOURS8[i];
                    if (Actor.findChar(p) == null && Dungeon.level.passable[p] && Dungeon.hero.pos != p) {
                        respawnPoints.add(p);
                    }
                }
                if (!respawnPoints.isEmpty()){
                    ch.pos = Random.element(respawnPoints);
                    ScrollOfTeleportation.teleportToLocation(ch, ch.pos);
                    if (isEmpowered()){
                        Buff.affect(ch, Swiftthistle.TimeBubble.class).reset(5);
                    }
                }
            }
        }

        Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
        Camera.main.shake( 3, 0.7f );
    }

    @Override
    protected void fx( Ballistica bolt, Callback callback ) {
        //need to perform flame spread logic here so we can determine what cells to put flames in.

        // unlimited distance
        int d = 5 + rank()*2;
        int dist = Math.min(bolt.dist, d);

        cone = new ConeAOE( bolt,
                d,
                (float) (Math.pow(2, rank()-1)*90),
                Ballistica.STOP_SOLID);

        //cast to cells at the tip, rather than all cells, better performance.
        for (Ballistica ray : cone.rays){
            ((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
                    MagicMissile.BEACON,
                    curUser.sprite,
                    ray.path.get(ray.dist),
                    null
            );
        }

        //final zap at half distance, for timing of the actual wand effect
        MagicMissile.boltFromChar( curUser.sprite.parent,
                MagicMissile.MAGIC_MISSILE,
                curUser.sprite,
                bolt.path.get(dist/2),
                callback );
        Sample.INSTANCE.play( Assets.Sounds.ZAP );
        Sample.INSTANCE.play( Assets.Sounds.ROCKS );
    }

    @Override
    public int manaCost(int rank) {
        switch (rank){
            case 1: return 5;
            case 2: return 9;
            case 3: return 16;
        }
        return 0;
    }

    @Override
    public String spellDesc() {
        return Messages.get(this, "desc", 5 + rank()*3);
    }

    @Override
    public String spellRankMessage(int rank) {
        return Messages.get(this, "rank", 3 + rank, 5 + rank*2, (int)(Math.pow(2, rank-1)*90));
    }

}
