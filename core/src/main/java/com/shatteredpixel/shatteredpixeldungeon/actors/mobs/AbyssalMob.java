package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.watabou.utils.Bundle;

public abstract class AbyssalMob extends Mob {
    public boolean spawned = false;

    {
        maxLvl = 100000;
    }

    @Override
    protected boolean act() {
        if (!spawned){
            spawned = true;
            if (abyssLevel() > 0) {
                HP = HT = Math.round(HT * (1f + .50f * abyssLevel()));
                defenseSkill += abyssLevel() * 3;
            }
        }

        return super.act();
    }

    private static final String SPAWNED = "spawned";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put(SPAWNED, spawned);
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        spawned = bundle.getBoolean(SPAWNED);
    }

    public int abyssLevel(){
//        return Math.max(0, (Dungeon.depth-1) / 5);
        return 0;
    }
}
