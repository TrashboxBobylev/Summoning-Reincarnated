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
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EffectBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.InventoryStone;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class MirrorOfFates extends Artifact {
    {
        image = ItemSpriteSheet.MIRROR;
        defaultAction = AC_USE;
        levelCap = 10;
    }

    public static final String AC_USE = "USE";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );
        if (isEquipped(hero) && !cursed && hero.buff(MagicImmune.class) == null && canBeUsed(hero))
            actions.add(AC_USE);
        return actions;
    }

    protected boolean canBeUsed(Hero hero){
        switch (type()){
            case 3:
                return hero.buff(UsedItemTracker.class) != null && chargeUse(hero) > 0 && chargeUse(hero) <= chargeCap;
            default:
                return !isMirrorActive(hero) && !isMirrorDown(hero);
        }
    }

    public static boolean isMirrorActive(Char ch){
        switch (mirrorType(Dungeon.hero)){
            case 1:
                return ch == Dungeon.hero && ch.buff(MirrorShield.class) != null;
            case 2:
                return Dungeon.hero.buff(MirrorShield.class) != null;
            default:
                return false;
        }
    }

    public static int mirrorType(Char ch){
        if (ch.buff(MirrorShield.class) == null)
            return 0;
        return ch.buff(MirrorShield.class).itemType;
    }

    public static boolean isMirrorDown(Char ch){
        return ch.buff(MirrorCooldown.class) != null;
    }

    public float rechargeModifier(){
        return rechargeModifier(type());
    }

    public static float rechargeModifier(int type){
        switch (type){
            case 1:
                return 1.0f;
            case 2:
                return 0.66f;
        }
        return 1.0f;
    }

    public float mirrorDurability(){
        return mirrorDurability(type());
    }

    public float mirrorDurability(int type){
        switch (type){
            case 1:
                return 1.0f;
            case 2:
                return 1.33f;
        }
        return 1.0f;
    }

    @Override
    public void charge(Hero target, float amount) {
        if (type() != 3) {
            MirrorCooldown cooldown;
            if ((cooldown = target.buff(MirrorCooldown.class)) != null) {
                cooldown.spend(amount * 3);
            }
        } else {
            if (cursed || target.buff(MagicImmune.class) != null) return;

            if (charge < chargeCap) {
                partialCharge += 0.2f*amount;
                while (partialCharge >= 1f) {
                    charge++;
                    partialCharge--;
                }
                if (charge >= chargeCap){
                    partialCharge = 0;
                    charge = chargeCap;
                }
                updateQuickslot();
            }
        }
    }

    public int chargeCap(){
        return chargeCap(type());
    }

    public int chargeCap(int type){
        if (type == 3) {
            return Math.min(2 + level(), 10);
        }
        return 0;
    }

    protected float baseMirrorCost(Hero hero) {
        if (hero.buff(UsedItemTracker.class) != null){
            Class<? extends Item> item = hero.buff(UsedItemTracker.class).item;
            if (ExoticScroll.class.isAssignableFrom(item)){
                if (item == ScrollOfMetamorphosis.class || item == ScrollOfEnchantment.class){
                    return 5;
                } else {
                    return 3;
                }
            } else if (Scroll.class.isAssignableFrom(item)){
                if (item == ScrollOfTransmutation.class){
                    return 4;
                } else {
                    return 2;
                }
            } else if (Runestone.class.isAssignableFrom(item)){
                if (item == StoneOfAugmentation.class || item == StoneOfEnchantment.class){
                    return 2;
                } else {
                    return 1;
                }
            }
        }
        return 0;
    }

    public float chargeUse(Hero hero) {
        float chargeUse = baseMirrorCost(hero);
        if (chargeUse == 0) return 0;
        // cost increases by +50% every use
        chargeUse = (int) Math.ceil(chargeUse * (1 + hero.buff(UsedItemTracker.class).used / 2f));
        return chargeUse;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (hero.buff(MagicImmune.class) != null) return;

        if (action.equals(AC_USE)){

            curUser = hero;

            if (!isEquipped( hero )) {
                GLog.i( Messages.get(Artifact.class, "need_to_equip") );
                QuickSlotButton.cancel();

            } else if (!canBeUsed(hero)) {
                GLog.i( getTypeBasedString("cant_be_used", type()) );
                QuickSlotButton.cancel();

            } else if (cursed) {
                GLog.warning( Messages.get(this, "cursed") );
                QuickSlotButton.cancel();

            } else {
                Invisibility.dispel();
                Talent.onArtifactUsed(hero);
                Sample.INSTANCE.play(Assets.Sounds.TELEPORT);

                if (type() != 3) {
                    Buff.affect(curUser, MirrorShield.class).setPotency(
                            (int) (curUser.HT * (0.2f + 0.04f * level()) * mirrorDurability())).setType(type());
                    curUser.spendAndNext(1f);
                } else {
                    UsedItemTracker usedItem = hero.buff(UsedItemTracker.class);
                    if (usedItem == null){
                        return;
                    }

                    Item item = Reflection.newInstance(usedItem.item);

                    item.setCurrent(hero);

                    hero.sprite.operate(hero.pos);
                    Enchanting.show(hero, item);

                    if (item instanceof Scroll){
                        ((Scroll) item).anonymize();
                        ((Scroll) item).doRead();
                    } else if (item instanceof Runestone){
                        ((Runestone) item).anonymize();
                        if (item instanceof InventoryStone){
                            ((InventoryStone) item).directActivate();
                        } else {
                            //we're already on the render thread, but we want to delay this
                            //as things like time freeze cancel can stop stone throwing from working
                            ShatteredPixelDungeon.runOnRenderThread(new Callback() {
                                @Override
                                public void call() {
                                    item.doThrow(hero);
                                }
                            });
                        }
                    }

                    float chargesSpent = chargeUse(hero);

                    partialCharge -= chargesSpent;
                    while (partialCharge < 0){
                        charge--;
                        partialCharge++;
                    }

                    //target hero level is 1 + 2*mirror level
                    int lvlDiffFromTarget = Dungeon.hero.lvl - (1+level()*2);
                    //plus an extra one for each level after 6
                    if (level() >= 7){
                        lvlDiffFromTarget -= level()-6;
                    }

                    if (lvlDiffFromTarget >= 0){
                        exp += Math.round(chargesSpent * 10f * Math.pow(1.1f, lvlDiffFromTarget));
                    } else {
                        exp += Math.round(chargesSpent * 10f * Math.pow(0.75f, -lvlDiffFromTarget));
                    }

                    if (exp >= (level() + 1) * 30 && level() < levelCap) {
                        upgrade();
                        Catalog.countUse(MirrorOfFates.class);
                        exp -= level() * 30;
                        GLog.p(getTypeBasedString("levelup", type()));
                    }

                    usedItem.used++;
                    // remove if impossible to be used
                    if (chargeUse(hero) > chargeCap) usedItem.detach();
                }
            }
        }
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new mirrorExp();
    }

    public String desc() {
        String desc = super.desc();

        if (isEquipped( Dungeon.hero )){
            desc += "\n\n";
            if (cursed)
                desc += Messages.get(this, "desc_cursed");
            else {
                desc += getTypeBasedString("desc_equipped", type());
            }
        }
        return desc;
    }

    @Override
    public Item upgrade() {
        Item upgraded = super.upgrade();
        if (type() == 3)
            chargeCap = chargeCap();
        return upgraded;
    }

    @Override
    public void type(int type) {
        super.type(type);
        chargeCap = chargeCap(type);
        charge = Math.min(charge, chargeCap());
        if (type() != 3 && Dungeon.hero.buff(UsedItemTracker.class) != null){
            Dungeon.hero.buff(UsedItemTracker.class).detach();
        }
    }

    @Override
    public String getTypeMessage(int type) {
        if (type == 3)
            return Messages.get(this, "type3", chargeCap(type));
        else
            return Messages.get(this, "type",
                    Math.round(100*mirrorDurability(type)),
                    Math.round(100/rechargeModifier(type))) + "\n\n" + super.getTypeMessage(type);
    }

    public class mirrorExp extends ArtifactBuff {
        @Override
        public boolean act() {
            if (type() == 3){
                if (charge < chargeCap && !cursed && target.buff(MagicImmune.class) == null) {
                    if (Regeneration.regenOn()) {
                        float missing = (chargeCap - charge);
                        if (level() > 7) missing += 8 * (level() - 7) / 3f;
                        float turnsToCharge = (60 - missing);
                        turnsToCharge /= RingOfEnergy.artifactChargeMultiplier(target);
                        float chargeToGain = (1f / turnsToCharge);
                        partialCharge += chargeToGain;
                    }

                    while (partialCharge >= 1) {
                        charge++;
                        partialCharge -= 1;
                        if (charge == chargeCap) {
                            partialCharge = 0;
                        }

                    }
                } else {
                    partialCharge = 0;
                }
            }

            updateQuickslot();

            spend( TICK );

            return true;
        }
        public void gainExp(int exp){
            MirrorOfFates.this.exp += exp;
            if (MirrorOfFates.this.exp > 20 + (level()+1)*20){
                MirrorOfFates.this.exp -= 20 + (level()+1)*20;
                GLog.p(getTypeBasedString("levelup", type()));
                upgrade();
                Catalog.countUse(MirrorOfFates.class);
                updateQuickslot();
            }
        }
    }

    public static class MirrorShield extends Buff {
        {
            type = buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return BuffIndicator.MIRROR;
        }

        public int potency;
        public int maxPotency;
        public int itemType;

        public MirrorShield setPotency(int p){
            maxPotency = p;
            potency = p;
            target.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(p), FloatingText.MIRROR);
            return this;
        }

        public MirrorShield setType(int type){
            this.itemType = type;
            return this;
        }

        private static final String POTENCY = "potency";
        private static final String DURATION = "duration";
        private static final String TYPE    = "type";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(POTENCY, potency);
            bundle.put(DURATION, maxPotency);
            bundle.put(TYPE, itemType);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            potency = bundle.getInt(POTENCY);
            maxPotency = bundle.getInt(DURATION);
            itemType = bundle.getInt(TYPE);
        }

        public int damage(int damage){
            potency -= damage;
            target.sprite.showStatusWithIcon(CharSprite.NEGATIVE, Integer.toString(damage), FloatingText.MIRROR);
            mirrorExp exp = target.buff(mirrorExp.class);
            if (exp != null){
                exp.gainExp(damage);
            }
            if (potency <= 0){
                destroy();
                damage = -potency;
            } else {
                damage = 0;
            }
            return damage;
        }

        public void destroy(){
            Splash.at( target.sprite.center(), 0xF09da8bd, 20 );
            Sample.INSTANCE.play(Assets.Sounds.HIT_PARRY);
            Sample.INSTANCE.play( Assets.Sounds.SHATTER );
            Buff.affect(target, MirrorCooldown.class, 100f/rechargeModifier(itemType));
            detach();
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (maxPotency - potency) / 1f / maxPotency);
        }

        @Override
        public void fx(boolean on) {
            if (on) target.sprite.add(CharSprite.State.SHIELDED);
            else target.sprite.remove(CharSprite.State.SHIELDED);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", potency);
        }
    }

    public static class MirrorCooldown extends EffectBuff {
        {
            announced = false;
        }

        @Override
        public int icon() {
            return BuffIndicator.MIRROR;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.5f, 0f, 0f);
        }

        @Override
        public void spend(float time) {
            super.spend(time);
        }
    }

    public static class UsedItemTracker extends FlavourBuff {

        {
            type = buffType.POSITIVE;
        }

        public static void track(Hero hero, Class<?extends Item> item) {
            detach(hero, UsedItemTracker.class);
            prolong(hero, UsedItemTracker.class, duration()).item = item;
        }

        public Class<?extends Item> item;

        private int used = 0;

        @Override
        public int icon() {
            return BuffIndicator.MIRROR;
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
            return Messages.get(this, "desc", Messages.titleCase(Reflection.newInstance(item).name()), dispTurns());
        }

        private static String ITEM = "item", USED = "used";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(ITEM, item);
            bundle.put(USED, used);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            item = bundle.getClass(ITEM);
            used = bundle.getInt(USED);
        }
    }

    public interface IndirectAttack {
        Char caster();
        int damage();
    }
}
