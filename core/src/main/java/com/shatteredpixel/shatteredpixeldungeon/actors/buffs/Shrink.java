package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.generic.Shrunken;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class Shrink extends Buff implements Shrunken {

    public int distance = 2;

    {
        type = buffType.POSITIVE;
        announced = true;
    }

    @Override
    public int icon() {
        return BuffIndicator.MOMENTUM;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public void tintIcon(Image icon) {
        icon.tint(0.5f, 0, 1, 0.75f);
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.SHRUNK);
        else target.sprite.remove(CharSprite.State.SHRUNK);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }
}
