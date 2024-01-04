package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

public class FireKeeper extends Blob {

    @Override
    protected void evolve() {

        int cell;

        Freezing freeze = (Freezing) Dungeon.level.blobs.get( Freezing.class );

        for (int i = area.left; i < area.right; i++){
            for (int j = area.top; j < area.bottom; j++){
                cell = i + j*Dungeon.level.width();
                off[cell] = cur[cell] > 0 ? cur[cell] - 1 : 0;

                if (freeze != null && freeze.volume > 0 && freeze.cur[cell] > 0){
                    freeze.clear(cell);
                    off[cell] = cur[cell] = 0;
                    continue;
                }

                if (off[cell] > 0) {

                    volume += off[cell];

                    GameScene.add(Blob.seed(cell, 5, Fire.class));
                }
            }
        }
    }

    @Override
    public void use( BlobEmitter emitter ) {
        super.use( emitter );
    }

    @Override
    public String tileDesc() {
        return "";
    }
}
