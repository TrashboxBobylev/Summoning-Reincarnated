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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SilkyQuiver;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.damagesource.DamageProperty;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.damagesource.DamageSource;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.EnumSet;

public class Gravery extends Blob implements Hero.Doom, DamageSource {

    @Override
    protected void evolve() {

        int cell;

        for (int i = area.left-1; i <= area.right; i++) {
            for (int j = area.top-1; j <= area.bottom; j++) {
                cell = i + j* Dungeon.level.width();
                if (cur[cell] > 0) {

                    Gravery.corrupt(cell);

                    off[cell] = cur[cell] - 1;
                    volume += off[cell];
                } else {
                    off[cell] = 0;
                }
            }
        }
    }

    public static void corrupt(int cell ){
        Char ch = Actor.findChar( cell );
        if (ch != null) {
            if (ch.properties().contains(Char.Property.UNDEAD) && (ch.buff(Corruption.class) == null && ch.buff(Doom.class) == null)){
                WandOfCorruption corruption = new WandOfCorruption();
                corruption.level((int) (Dungeon.chapterNumber()*1.5f));
                corruption.onZap(new Ballistica(cell, ch.pos, Ballistica.STOP_TARGET));
                if (ch.buff(Corruption.class) != null){
                    if (ch instanceof Mob)
                        ((Mob) ch).EXP = 0;
                    ch.die(new Gravery());
                    Wraith w = Wraith.spawnForcefullyAt(ch.pos);
                    if (w != null) {
                        Buff.affect(w, Corruption.class);
                        if (Dungeon.level.heroFOV[ch.pos]) {
                            CellEmitter.get(ch.pos).burst(ShadowParticle.CURSE, 6);
                            Sample.INSTANCE.play(Assets.Sounds.CURSED);
                        }
                    }
                }
            } else if (!ch.properties().contains(Char.Property.BOSS) && !ch.properties().contains(Char.Property.MINIBOSS) && !(ch instanceof Wraith)){
                ch.damage(Dungeon.chapterNumber()*4+4, new Gravery());
            }
            if (ch.buff(Corruption.class) != null || !ch.isAlive()){
                SilkyQuiver.quiverBuff quiverBuff = Dungeon.hero.buff(SilkyQuiver.quiverBuff.class);
                if (quiverBuff != null && quiverBuff.itemType() == 2){
                    quiverBuff.gainGraveExp();
                }
            }
        }

        Heap heap = Dungeon.level.heaps.get( cell );
        if (heap != null){
            heap.burn();
            heap.explode();
            heap.freeze();
        }
    }

    @Override
    public void use( BlobEmitter emitter ) {
        super.use( emitter );
        emitter.start( ShadowParticle.UP, 0.05f, 0 );
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }

    @Override
    public void onDeath() {
        Dungeon.fail( getClass() );
        GLog.negative( Messages.get(this, "ondeath") );
    }

    @Override
    public EnumSet<DamageProperty> initDmgProperties() {
        return EnumSet.of(DamageProperty.MAGICAL, DamageProperty.DARK, DamageProperty.DECAY);
    }
}
