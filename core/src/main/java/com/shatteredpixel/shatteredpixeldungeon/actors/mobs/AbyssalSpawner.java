package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.RedCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.UpgradeClump;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AbyssalSpawnerSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class AbyssalSpawner extends AbyssalMob {

    {
        spriteClass = AbyssalSpawnerSprite.class;

        HP = HT = 150;
        defenseSkill = 0;

        EXP = 30;

        loot = UpgradeClump.class;
        lootChance = 1f;

        state = PASSIVE;

        properties.add(Property.IMMOVABLE);
        properties.add(Property.MINIBOSS);
        properties.add(Property.DEMONIC);
        properties.add(Property.INORGANIC);
        properties.add(Property.UNDEAD);
    }

    @Override
    public void beckon(int cell) {
        //do nothing
    }

    @Override
    public boolean reset() {
        return true;
    }

    private float spawnCooldown = 0;

    public boolean spawnRecorded = false;

    @Override
    protected boolean act() {

        spawnCooldown--;
        HP = Math.min(HT, HP + 2 + abyssLevel());
        if (spawnCooldown <= 0){
            ArrayList<Integer> candidates = new ArrayList<>();
            for (int n : PathFinder.NEIGHBOURS8) {
                if (Dungeon.level.passable[pos+n] && Actor.findChar( pos+n ) == null) {
                    candidates.add( pos+n );
                }
            }

            if (!candidates.isEmpty()) {
                Mob spawn = Dungeon.level.createMob();

                spawn.pos = Random.element( candidates );
                spawn.state = spawn.HUNTING;

                Dungeon.level.occupyCell(spawn);

                GameScene.add( spawn, 1 );
                if (sprite.visible) {
                    Actor.addDelayed(new Pushing(spawn, pos, spawn.pos), -1);
                }

                spawnCooldown = Math.max(3, 25 - Dungeon.depth / 2);
            }
        }
        return super.act();
    }

    @Override
    public void damage(int dmg, Object src) {
        spawnCooldown -= dmg / 2f;
        if (dmg >= HT / 4){
            dmg = HT/4 - 1 + (int)(Math.sqrt(8*(dmg - (HT/4f - 1)) + 1) - 1)/2;
        }
        super.damage(dmg, src);
    }

    @Override
    protected boolean getCloser(int target) {
        return false;
    }

    @Override
    protected boolean getFurther(int target) {
        return false;
    }

    @Override
    public void aggro(Char ch) {
    }

    @Override
    public Item createLoot() {
        //drop two things manually
        Dungeon.level.drop(new RedCrystal(), pos).sprite.drop();
        Dungeon.level.drop(new UpgradeClump(), pos).sprite.drop();
        return null;
    }

    public static final String SPAWN_COOLDOWN = "spawn_cooldown";
    public static final String SPAWN_RECORDED = "spawn_recorded";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SPAWN_COOLDOWN, spawnCooldown);
        bundle.put(SPAWN_RECORDED, spawnRecorded);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        spawnCooldown = bundle.getFloat(SPAWN_COOLDOWN);
        spawnRecorded = bundle.getBoolean(SPAWN_RECORDED);
    }

    {
        immunities.add( Paralysis.class );
        immunities.add( Amok.class );
        immunities.add( Sleep.class );
        immunities.add( Terror.class );
        immunities.add( Vertigo.class );
    }

}
