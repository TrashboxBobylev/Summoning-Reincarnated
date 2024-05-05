package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class TieringEmpower extends Buff {

    {
        type = Buff.buffType.POSITIVE;
    }

    @Override
    public int icon() {
        return BuffIndicator.UPGRADE;
    }

    @Override
    public float iconFadePercent() {
        float max = 1 + Dungeon.hero.pointsInTalent(Talent.UPGRADING_MEAL)*2;
        return Math.max(0, (max-left) / max);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(left);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", left);
    }

    public int left;

    public void set(int hits){
        if (hits > left) {
            left = hits;
        }
    }

    private static final String LEFT = "left";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put( LEFT, left );
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        left = bundle.getInt( LEFT );
    }

}
