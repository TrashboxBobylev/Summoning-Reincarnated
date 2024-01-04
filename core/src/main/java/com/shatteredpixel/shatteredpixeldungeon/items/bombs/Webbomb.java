package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WebParticle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;

public class Webbomb extends Bomb {

    {
        image = ItemSpriteSheet.WEB_BOMB;
        harmless = true;
        fuseDelay = 0;
    }

    @Override
    public void explode(int cell) {
        super.explode(cell);

        PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), 3 );
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                if (Dungeon.level.pit[i])
                    GameScene.add(Blob.seed(i, Math.round(5/**Bomb.nuclearBoost()*/), Web.class));
                else
                    GameScene.add(Blob.seed(i, Math.round(30/**Bomb.nuclearBoost()*/), Web.class));
                CellEmitter.get(i).burst(WebParticle.FACTORY, 3);
            }
        }
        Sample.INSTANCE.play(Assets.Sounds.PUFF);
    }

    @Override
    public int value() {
        //prices of ingredients
        return quantity * (35 + 40);
    }
}
