package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;

public class DoomCloud extends Image {

    private static final float TIME_TO_FADE = 18f;

    private float time;

    public DoomCloud() {
        super(Effects.get(Effects.Type.DOOM_CLOUD));
        origin.set(width / 2, height / 2);
        scale.x = 4f;
        scale.y = 4f;
    }

    public void reset(int p) {
        revive();

        x = (p % Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - width) / 2;
        y = (p / Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - height) / 2;

        time = TIME_TO_FADE;
    }

    public void reset(Visual v) {
        revive();

        point(v.center(this));

        time = TIME_TO_FADE;
    }

    @Override
    public void update() {
        super.update();

        if ((time -= Game.elapsed) <= 0) {
            kill();
        } else {
            float p = time / TIME_TO_FADE;
            alpha(p);
            scale.y = 1 + p/2;
        }
    }

    public static void hit(Char ch) {
        hit(ch, 0);
    }

    public static void hit(Char ch, float angle) {
        if (ch.sprite.parent != null) {
            DoomCloud s = (DoomCloud) ch.sprite.parent.recycle(DoomCloud.class);
            ch.sprite.parent.bringToFront(s);
            s.reset(ch.sprite);
            s.angle = angle;
        }
    }

    public static void hit(int pos) {
        hit(pos, 0);
    }

    public static void hit(int pos, float angle) {
        Group parent = Dungeon.hero.sprite.parent;
        DoomCloud s = (DoomCloud) parent.recycle(DoomCloud.class);
        parent.bringToFront(s);
        s.reset(pos);
        s.angle = angle;
    }
}
