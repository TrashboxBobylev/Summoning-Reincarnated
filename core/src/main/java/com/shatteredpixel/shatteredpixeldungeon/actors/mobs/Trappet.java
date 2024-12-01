package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CorrosionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CursingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisarmingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DistortionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlashingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GuardianTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RockfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.StormTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WarpingTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TrappetSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class Trappet extends AbyssalMob implements Callback {

    private static final float TIME_TO_ZAP	= 1f;

    {
        spriteClass = TrappetSprite.class;

        HP = HT = 125;
        defenseSkill = 36;
        viewDistance = Light.DISTANCE;

        EXP = 13;

        loot = new Gold();
        lootChance = 0.45f;

        properties.add(Property.DEMONIC);
        properties.add(Property.UNDEAD);
    }

    @Override
    public int attackSkill( Char target ) {
        return 34 + abyssLevel();
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0 + abyssLevel()*3, 7 + abyssLevel()*11);
    }

    @Override
    public boolean canAttack(Char enemy) {
        /*if (buff(ChampionEnemy.Paladin.class) != null){
            return false;
        }*/
        return (super.canAttack(enemy) || new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos);
    }

    protected boolean doAttack( Char enemy ) {
        if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
            sprite.zap( enemy.pos );
            return false;
        } else {
            zap();
            return true;
        }
    }

    protected Class[] traps = new Class[]{
            FrostTrap.class, StormTrap.class, CorrosionTrap.class,  DisintegrationTrap.class,
            RockfallTrap.class, FlashingTrap.class, GuardianTrap.class,
            DisarmingTrap.class,
            WarpingTrap.class, CursingTrap.class, GrimTrap.class, PitfallTrap.class, DistortionTrap.class };

    private void zap() {
        spend( TIME_TO_ZAP );

        if (hit( this, enemy, true )) {
            /*if (enemy.buff(WarriorParry.BlockTrock.class) != null){
                enemy.sprite.emitter().burst( Speck.factory( Speck.FORGE ), 15 );
                SpellSprite.show(enemy, SpellSprite.BLOCK, 2f, 2f, 2f);
                enemy.buff(WarriorParry.BlockTrock.class).triggered = true;
            } else {*/
                ArrayList<Integer> points = Level.getSpawningPoints(enemy.pos);
                if (!points.isEmpty()) {
                    Trap t = ((Trap) Reflection.newInstance(Random.element(traps)));
                    Dungeon.level.setTrap(t, Random.element(points));
                    Dungeon.level.map[t.pos] = t.visible ? Terrain.TRAP : Terrain.SECRET_TRAP;
                    t.reveal();
                } else {
                    Trap t = ((Trap) Reflection.newInstance(Random.element(traps)));
                    Dungeon.level.setTrap(t, enemy.pos);
                    t.activate();
                }

                if (enemy == Dungeon.hero && !enemy.isAlive()) {
                    Dungeon.fail(getClass());
                    GLog.n(Messages.get(this, "bolt_kill"));
                }
            /*}*/
        } else {
            enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
        }
    }

    public void onZapComplete() {
        zap();
        next();
    }

    @Override
    public void call() {
        next();
    }
}
