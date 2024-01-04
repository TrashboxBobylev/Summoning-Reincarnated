package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GooSprite;
import com.watabou.utils.Random;

import java.util.HashMap;

public class Miasma extends Blob {

    private static final HashMap<Class<? extends FlavourBuff>, Float> MINOR_DEBUFFS = new HashMap<>();
    static{
        MINOR_DEBUFFS.put(Weakness.class,       1f);
        MINOR_DEBUFFS.put(Cripple.class,        1f);
        MINOR_DEBUFFS.put(Blindness.class,      1f);
        MINOR_DEBUFFS.put(Terror.class,         1f);

        MINOR_DEBUFFS.put(Chill.class,          1f);
        MINOR_DEBUFFS.put(Roots.class,          1f);
        MINOR_DEBUFFS.put(Vertigo.class,        1f);
        MINOR_DEBUFFS.put(Paralysis.class, 1f);
        MINOR_DEBUFFS.put(Slow.class, 1f);
    }

    @Override
    protected void evolve() {

        int cell;

        for (int i = area.left; i < area.right; i++){
            for (int j = area.top; j < area.bottom; j++){
                cell = i + j* Dungeon.level.width();
                off[cell] = cur[cell] > 0 ? cur[cell] - 1 : 0;

                if (off[cell] > 0) {

                    volume += off[cell];

                    Char ch = Actor.findChar( cell );

                    if (ch != null && !ch.isImmune(this.getClass())) {
                        Class<?extends FlavourBuff> debuffCls = Random.chances(MINOR_DEBUFFS);
                        Buff.affect(ch, debuffCls, 5);
                    }
                }
            }
        }
    }

    @Override
    public void use( BlobEmitter emitter ) {
        super.use( emitter );

        emitter.pour( GooSprite.GooParticle.FACTORY, 0.06f );
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }
}
