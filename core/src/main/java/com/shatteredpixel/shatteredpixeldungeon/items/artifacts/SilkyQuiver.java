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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Gravery;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EffectBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Empowered;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invulnerability;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ThrowieBoost;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpectralBlades;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.EffectTarget;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.ShieldHalo;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.TypedItem;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuiver;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashSet;

public class SilkyQuiver extends Artifact {
    {
        image = ItemSpriteSheet.QUIVER;
        levelCap = 5;
        charge = 5;
        chargeCap = 5 + level();
        defaultAction = AC_USE;
    }

    public static final String AC_USE = "USE";
    public static final float CHARGE_GAIN = 0.25f;

    public static ComboMove selectedMove;

    public enum ComboMove {
        SHOOT(1, 0x99ffff){
            @Override
            public float powerLevel() {
                return 1 + curUser.lvl/4f;
            }

            @Override
            public void execute(Char enemy, Arrow arrow) {
                curUser.shoot(enemy, arrow);
            }

            public String desc(){
                return Messages.get(this, name()+"_desc",
                        GameMath.printAverage(Math.round(4 + fullLevel()), Math.round(10 + fullLevel()*4.5f)));
            }
        },
        SHOOT2(1, 0x99ffff){
            @Override
            public float powerLevel() {
                return 2 + curUser.lvl/3f;
            }

            @Override
            public void execute(Char enemy, Arrow arrow) {
                curUser.shoot(enemy, arrow);
            }

            public String desc(){
                return Messages.get(this, name()+"_desc",
                        GameMath.printAverage(Math.round(4 + fullLevel()), Math.round(10 + fullLevel()*4.5f)));
            }
        },
        KNOCKBACK(2, 0xb3b3b3){
            @Override
            public void execute(Char enemy, Arrow arrow) {
                Ballistica trajectory = new Ballistica(curUser.pos, enemy.pos, Ballistica.STOP_TARGET);
                trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
                WandOfBlastWave.throwChar(enemy, trajectory, 4, false, false, this);
                enemy.sprite.bloodBurstA( curUser.sprite.center(), 10 );
                enemy.sprite.flash();
                Sample.INSTANCE.play(Assets.Sounds.BLAST, 1f, Random.Float(0.87f, 1.15f));
            }

            @Override
            public float turnAmount() {
                return 0f;
            }
        },
        COPY(2, 0x959fa6){
            @Override
            public void execute(Char enemy, Arrow arrow) {
                QuiverTracker tracker = curUser.buff(QuiverTracker.class);
                MissileWeapon item = (MissileWeapon) Reflection.newInstance(tracker.item);
                item.type(tracker.itemType);
                item.spawnedForEffect = true;
                item.identify(false);
                Enchanting.show(enemy, item);
                curUser.shoot(enemy, item);
            }

            @Override
            public String desc() {
                if (Dungeon.hero == null || Dungeon.hero.buff(QuiverTracker.class) == null)
                    return super.desc();
                else
                    return Messages.get(this, name()+"_desc_item",
                            Messages.titleCase(Reflection.newInstance(Dungeon.hero.buff(QuiverTracker.class).item).name()),
                            TypedItem.getTypeString(Dungeon.hero.buff(QuiverTracker.class).itemType));
            }
        },
        MARK(3, 0xff2828){
            @Override
            public float turnAmount() {
                return 2f;
            }

            @Override
            public void execute(Char enemy, Arrow arrow) {
                enemy.sprite.bloodBurstA( curUser.sprite.center(), 10 );
                enemy.sprite.flash();
                Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1f, Random.Float(0.87f, 1.15f));
                Buff.affect(enemy, QuiverMark.class, 35f);
            }
        },
        BURST(4, 0x66b3ff){
            @Override
            public void execute(Char enemy, Arrow arrow) {
                curUser.shoot(enemy, arrow);
            }

            @Override
            public float powerLevel() {
                return 2 + curUser.lvl/3f;
            }

            @Override
            public float turnAmount() {
                return 1.5f;
            }

            public String desc(){
                return Messages.get(this, name()+"_desc",
                        GameMath.printAverage(Math.round(4 + fullLevel()), Math.round(10 + fullLevel()*4.5f)));
            }
        },
        BURST2(3, 0x66b3ff){
            @Override
            public void execute(Char enemy, Arrow arrow) {
                curUser.shoot(enemy, arrow);
            }

            @Override
            public float powerLevel() {
                return 2 + curUser.lvl/3f;
            }

            @Override
            public float turnAmount() {
                return 1.5f;
            }

            public String desc(){
                return Messages.get(this, name()+"_desc",
                        GameMath.printAverage(Math.round(4 + fullLevel()), Math.round(10 + fullLevel()*4.5f)));
            }
        },
        EMPOWER(4, 0x8b82d9){
            @Override
            public void execute(Char enemy, Arrow arrow) {
                if (enemy.alignment == Char.Alignment.ALLY){
                    ShieldHalo shield;
                    GameScene.effect(shield = new ShieldHalo(enemy.sprite));
                    shield.putOut();
                    new Flare( 6, 32 ).show( curUser.sprite, 2f );
                    Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
                    Sample.INSTANCE.play(Assets.Sounds.BLAST);
                    Buff.affect(enemy, Invulnerability.class, 20);
                    Buff.affect(enemy, Empowered.class, 75);
                } else {
                    curUser.shoot(enemy, arrow);
                }
            }

            @Override
            public float powerLevel() {
                return 3 + curUser.lvl/2.5f;
            }

            @Override
            public float turnAmount() {
                return 5f;
            }
        },
        GRIM(5, 0x090e22){

            @Override
            public float turnAmount() {
                return 2f;
            }

            @Override
            public float powerLevel() {
                return 1 + curUser.lvl/3f;
            }

            @Override
            public void execute(Char enemy, Arrow arrow) {
                SilkyQuiver quiver = (SilkyQuiver) curItem;
                boolean successfulKill = false;
                if (curUser.shoot(enemy, arrow)) {
                    if (enemy.isAlive()) {
                        int enemyHealth = enemy.HP;

                        float maxChance = 0.75f + .05f * (Dungeon.hero.lvl / 3f);
                        float chanceMulti = (float) Math.pow(((enemy.HT - enemyHealth) / (float) enemy.HT), 2);
                        float chance = maxChance * chanceMulti;

                        if (Random.Float() < chance) {

                            enemy.damage(enemy.HP, new Grim());
                            enemy.sprite.emitter().burst(ShadowParticle.UP, 10);

                            successfulKill = true;
                        }
                    } else {
                        successfulKill = true;
                    }
                }
                if (successfulKill){
                    quiver.exp++;
                    if (quiver.exp >= quiver.level() && quiver.level() < quiver.levelCap) {
                        quiver.exp -= quiver.level();
                        quiver.upgrade();
                        Catalog.countUse(SilkyQuiver.class);
                        GLog.positive(Messages.get(SilkyQuiver.class, "level_up"));
                        quiver.charge = Math.min(quiver.charge + 2, quiver.chargeCap);
                        updateQuickslot();
                    }
                }
            }

            public String desc(){
                return Messages.get(this, name()+"_desc",
                        GameMath.printAverage(Math.round(8 + fullLevel()*2), Math.round(20 + fullLevel()*9f)));
            }
        },
        GRAVE(5, 0x090e22){
            @Override
            public void execute(Char enemy, Arrow arrow) {
                PathFinder.buildDistanceMap( enemy.pos, BArray.not( Dungeon.level.solid, null ), 2 );
                for (int i = 0; i < PathFinder.distance.length; i++) {
                    if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                        if (!Dungeon.level.pit[i]) {
                            GameScene.add(Blob.seed(i, 50, Gravery.class));
                            CellEmitter.get(i).burst(MagicMissile.YogParticle.FACTORY, 3);
                        }
                    }
                }
                Sample.INSTANCE.play(Assets.Sounds.BURNING, 1, 0.5f);
                Sample.INSTANCE.play(Assets.Sounds.CURSED, 1, 0.5f);
            }
        };

        public final int cost, color;

        ComboMove(int cost, int color) {
            this.cost = cost;
            this.color = color;
        }

        public void execute(Char enemy, Arrow arrow){ }

        public float turnAmount(){
            return 1f;
        }

        public float powerLevel(){
            return 0;
        }

        public float fullLevel(){
            float power = powerLevel();
            if (Dungeon.hero != null) {
                if (Dungeon.hero.buff(ThrowieBoost.class) != null) {
                    power += Dungeon.hero.buff(ThrowieBoost.class).boost();
                }
                power += RingOfSharpshooting.levelDamageBonus(Dungeon.hero)*0.67f;
            }
            return power;
        }

        public String desc(){
            return Messages.get(this, name()+"_desc");
        }
    }

    public ComboMove[] powers(int type){
        switch (type){
            case 1: default:
                return new ComboMove[]{ComboMove.SHOOT, ComboMove.KNOCKBACK, ComboMove.MARK, ComboMove.BURST, ComboMove.GRIM};
            case 2:
                return new ComboMove[]{ComboMove.SHOOT2, ComboMove.COPY, ComboMove.BURST2, ComboMove.EMPOWER, ComboMove.GRAVE};
        }
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );
        if (isEquipped(hero) && !cursed && charge > 0)
            actions.add(AC_USE);
        return actions;
    }

    public float rechargeModifier(){
        return rechargeModifier(type());
    }

    public float rechargeModifier(int type){
        switch (type){
            case 1:
                return 1.0f;
            case 2:
                return 0.5f;
            case 3:
                return 2.5f;
        }
        return 1.0f;
    }

    @Override
    public void level(int value) {
        super.level(value);
        chargeCap = 5 + level();
    }

    @Override
    public Item upgrade() {
        super.upgrade();
        chargeCap = 5 + level();
        updateQuickslot();
        return this;
    }

    public boolean isUsable(ComboMove move){
        if (move == ComboMove.COPY){
            return Dungeon.hero != null && Dungeon.hero.buff(QuiverTracker.class) != null;
        }
        return charge >= move.cost;
    }

    public void useMove(ComboMove move){
        selectedMove = move;
        GameScene.selectCell(shooter);
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_USE)){
            if (!isEquipped( hero ))             GLog.i( Messages.get(Artifact.class, "need_to_equip") );
            else if (cursed)                     GLog.i( Messages.get(this, "cursed") );
            else if (charge == 0)        GLog.i( Messages.get(this, "no_charge") );
            else {
                curUser = hero;
                curItem = this;
                GameScene.show(new WndQuiver(this));
            }
        }
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if (isEquipped(Dungeon.hero)) {
            if (cursed) {
                desc += "\n\n" + Messages.get(this, "desc_cursed");
            }
            else {
                desc += "\n\n" + getTypeBasedString("desc_equipped", type());
            }
        }

        return desc;
    }

    @Override
    public void charge(Hero target, float amount) {
        target.buff(quiverBuff.class).gainCharge(CHARGE_GAIN * (1 + 0.25f*level()) * rechargeModifier());
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new quiverBuff();
    }

    @Override
    public String getTypeMessage(int type) {
        return Messages.get(this, "type", Math.round(100*rechargeModifier(type))) + "\n\n" + super.getTypeMessage(type);
    }

    public class quiverBuff extends ArtifactBuff {

        public void gainCharge(){
            gainCharge(CHARGE_GAIN * (1 + 0.25f*itemLevel()) * rechargeModifier());
        }

        public void gainCharge(float charge_gain){
            if (charge < chargeCap && !cursed){
                partialCharge += charge_gain;
                while (partialCharge >= 1) {
                    charge++;
                    partialCharge--;
                    if (charge == chargeCap){
                        partialCharge = 0f;
                        GLog.positive( Messages.get(SilkyQuiver.class, "charged") );
                    }
                    updateQuickslot();
                }
            }
        }

        public void gainGraveExp(){
            exp++;
            gainCharge(0.5f);
            if (exp >= level()*2 && level() < levelCap) {
                exp -= level()*2;
                upgrade();
                Catalog.countUse(SilkyQuiver.class);
                GLog.positive(Messages.get(SilkyQuiver.class, "level_up"));
                charge = Math.min(charge + 2, chargeCap);
                updateQuickslot();
            }
        }

        public void loseArrows(){
            if (charge > 0 && Dungeon.hero.getVisibleEnemies().size() > 0) {
                final ArrayList<Char> targets = new ArrayList<>();
                int amount = 0;

                while (amount < charge) {
                    for (Mob mob : Dungeon.hero.getVisibleEnemies()) {
                        targets.add(mob);
                        if (++amount >= charge) {
                            break;
                        }
                    }
                }

                MissileWeapon proto = new Arrow();

                final HashSet<Callback> callbacks = new HashSet<>();

                for (Char ch : targets) {
                    Callback callback = new Callback() {
                        @Override
                        public void call() {
                            Dungeon.hero.shoot(ch, proto);
                            callbacks.remove(this);
                            if (callbacks.isEmpty()) {
                                Invisibility.dispel();
                                Dungeon.hero.next();
                            }
                        }
                    };

                    MissileSprite m = ((MissileSprite) Dungeon.hero.sprite.parent.recycle(MissileSprite.class));
                    m.reset(Dungeon.hero.sprite, ch.pos, proto, callback);

                    callbacks.add(callback);
                }
            }

            charge = 0;
            Item.updateQuickslot();
        }
    }

    public static class Arrow extends MissileWeapon {
        {
            image = ItemSpriteSheet.QUIVER_ARROW;
        }

        @Override
        public void throwSound() {
            Sample.INSTANCE.play( Assets.Sounds.ATK_SPIRITBOW, 1, Random.Float(0.8f, 1.2f) );
        }

        @Override
        public ItemSprite.Glowing glowing() {
            if (selectedMove != null)
                return new ItemSprite.Glowing(selectedMove.color, 0.25f);
            return super.glowing();
        }

        @Override
        public Emitter emitter() {
            Emitter e = new Emitter();
            e.pos(5, 5);
            e.fillTarget = false;
            e.pour(Speck.factory(Speck.DISCOVER), 0.02f);
            return e;
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            return super.proc(attacker, defender, damage);
        }

        @Override
        public float castDelay(Char user, int cell) {
            if (selectedMove == null)
                return 0;
            return super.castDelay(user, cell);
        }

        @Override
        public void cast(final Hero user, final int dst) {
            final int cell = throwPos(user, dst);
            QuickSlotButton.target(Actor.findChar(cell));
            Hunger.adjustHunger(-3 * castDelay(user, dst));
            Invisibility.dispel();
            if (selectedMove == ComboMove.BURST || selectedMove == ComboMove.BURST2){
                Ballistica b = new Ballistica(curUser.pos, cell, Ballistica.WONT_STOP);
                final HashSet<Char> targets = new HashSet<>();
                Char enemy = SpectralBlades.findChar(b, curUser, 0, targets);
                if (enemy == null){
                    GLog.warning(Messages.get(SpectralBlades.class, "no_target"));
                    ((SilkyQuiver)curItem).charge += selectedMove.cost;
                    selectedMove = null;
                    Item.updateQuickslot();
                    return;
                }
                targets.add(enemy);
                int degrees = ((SilkyQuiver)curItem).type() == 2 ? 240 : 120;
                ConeAOE cone = new ConeAOE(b, degrees);
                for (Ballistica ray : cone.rays){
                    // 1/3/5/7/9 up from 0/2/4/6/8
                    Char toAdd = SpectralBlades.findChar(ray, curUser, 0, targets);
                    if (toAdd != null && curUser.fieldOfView[toAdd.pos]){
                        targets.add(toAdd);
                    }
                }
                final HashSet<Callback> callbacks = new HashSet<>();
                for (Char ch : targets) {
                    Callback callback = new Callback() {
                        @Override
                        public void call() {
                            selectedMove.execute(ch, Arrow.this);
                            callbacks.remove( this );
                            if (callbacks.isEmpty()) {
                                Invisibility.dispel();
                                curUser.spendAndNext( selectedMove.turnAmount() );
                                selectedMove = null;
                            }
                        }
                    };

                    MissileSprite m = ((MissileSprite)curUser.sprite.parent.recycle( MissileSprite.class ));
                    m.reset( curUser.sprite, ch.pos, this, callback );
                    m.alpha(0.8f);
                    throwSound();

                    callbacks.add( callback );
                }

                curUser.sprite.zap( enemy.pos );
                curUser.busy();
            } else {
                user.sprite.zap(cell);
                user.busy();

                throwSound();
                final float delay = selectedMove.turnAmount();
                Char enemy = Actor.findChar(cell);
                QuickSlotButton.target(enemy);
                MissileSprite missileSprite = (MissileSprite) user.sprite.parent.recycle(MissileSprite.class);
                if (enemy != null) {
                    missileSprite.
                            reset(user.sprite,
                                    cell,
                                    this,
                                    () -> {
                                        user.spendAndNext(delay);
                                        selectedMove.execute(enemy, this);
                                        selectedMove = null;
                                    });
                } else {
                    missileSprite.reset(user.sprite,
                            cell,
                            this,
                            () -> {
                                user.spendAndNext(delay);
                                selectedMove.execute(new EffectTarget(cell), this);
                                selectedMove = null;
                            });
                }
            }
        }

        @Override
        public int min() {
            if (selectedMove == ComboMove.GRIM){
                return super.min()*2;
            } else {
                return super.min();
            }
        }

        @Override
        public int max() {
            if (selectedMove == ComboMove.GRIM){
                return super.max()*2;
            } else {
                return super.max();
            }
        }

        @Override
        public float accuracyFactor(Char owner, Char target) {
            if (selectedMove == ComboMove.GRIM){
                return Float.POSITIVE_INFINITY;
            } else {
                return super.accuracyFactor(owner, target);
            }
        }

        @Override
        public float powerLevel() {
            //doesn't scale with strength at all, but still can receive boosts
            float level = selectedMove != null ? selectedMove.fullLevel() : Dungeon.hero.lvl / 12f;
            return level;
        }
    }

    public static class QuiverMark extends EffectBuff {
        {
            type = Buff.buffType.NEGATIVE;
        }

        @Override
        public int icon() {
            return BuffIndicator.MARK;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0x18badc);
        }
    }

    public static class QuiverTracker extends FlavourBuff {
       {
            type = buffType.POSITIVE;
        }

        public static void track(Hero hero, MissileWeapon item) {
            detach(hero, QuiverTracker.class);
            QuiverTracker tracker = prolong(hero, QuiverTracker.class, duration());
            tracker.item = item.getClass();
            tracker.itemType = item.type();
        }

        public Class<?extends Item> item;
        public int itemType;

        @Override
        public int icon() {
            return BuffIndicator.THROWN_WEP;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.tint(0x18badc);
        }

        public static float duration() {
            return 50;
        }

        @Override
        public float iconFadePercent() {
            float duration = duration();
            return Math.max(0, (duration - visualcooldown()) / duration);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", Messages.titleCase(Reflection.newInstance(item).name()), TypedItem.getTypeString(itemType), dispTurns());
        }

        private static final String ITEM = "item", TYPE = "type";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(ITEM, item);
            bundle.put(TYPE, itemType);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            item = bundle.getClass(ITEM);
            itemType = bundle.getInt(TYPE);
        }
    }

    private static final CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target != null && curItem instanceof SilkyQuiver) {
                Arrow arrow = new Arrow();
                Weapon wep = (Weapon) curUser.belongings.weapon;
                if (wep instanceof MeleeWeapon)
                    arrow.enchant(wep.enchantment);
                arrow.cast(curUser, target);
                ((SilkyQuiver)curItem).charge -= selectedMove.cost;
                updateQuickslot();
            }
        }
        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };
}
