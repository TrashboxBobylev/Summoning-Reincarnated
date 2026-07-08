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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EnergyParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.staffs.Staff;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;

import java.util.ArrayList;

public class EmeradicBattery extends Artifact {
    {
        image = ItemSpriteSheet.BATTERY;

        levelCap = 5;
        defaultAction = AC_USE;
        charge = 0;
        chargeCap = 50 + level()*10;
    }

    public static final String AC_USE = "USE";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );
        if (isEquipped(hero) && !cursed)
            actions.add(AC_USE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_USE)){
            if (!isEquipped( hero ))             GLog.i( Messages.get(Artifact.class, "need_to_equip") );
            else if (charge == chargeCap)        GLog.i( Messages.get(this, "full_charge") );
            else if (cursed)                     GLog.i( Messages.get(this, "cursed") );
            else {
                GameScene.selectItem(itemSelector);
            }
        }
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if (isEquipped(Dungeon.hero)) {
            if (cursed) {
                desc += "\n\n" + getTypeBasedString("desc_cursed", type());
            }
            else {
                desc += "\n\n" + getTypeBasedString("desc_equipped", type(), Math.round((1 + (2 + level()))/3f)*100);
            }
        }

        return desc;
    }

    @Override
    public void charge(Hero target, float amount) {
        if (charge < chargeCap) {
            charge += 2f * amount;
            if (charge == chargeCap) {
                GLog.positive(Messages.get(EmeradicBattery.class, "full_charge"));
                partialCharge = 0;
            }
        }
    }

    @Override
    public void level(int value) {
        super.level(value);
        chargeCap = 50 + level()*10;
    }

    @Override
    public Item upgrade() {
        super.upgrade();
        chargeCap = 50 + level()*10;
        return this;
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new fuelBuff();
    }

    @Override
    public String getTypeMessage(int type) {
        return Messages.get(this, "type" + type, Math.round((1 + (2 + level()))/3f)*100);
    }

    public static void procArcaneEnergy(Char hero){
        EmeradicBattery.fuelBuff emeradicArcana;
        if ((emeradicArcana = hero.buff(EmeradicBattery.fuelBuff.class)) != null && emeradicArcana.itemType() == 2
                && emeradicArcana.charges() > 0){
            emeradicArcana.useCharge(1);
        }
    }

    public static boolean procWondrousEnergy(Hero hero){
        EmeradicBattery.fuelBuff emeradicResin;
        if ((emeradicResin = hero.buff(EmeradicBattery.fuelBuff.class)) != null && emeradicResin.itemType() == 3
                && emeradicResin.charges() > 0){
            emeradicResin.useCharge(2);
            return true;
        }
        return false;
    }

    public static boolean isEmeradicEvil(Hero hero){
        EmeradicBattery.fuelBuff emeradicResin;
        return (emeradicResin = hero.buff(fuelBuff.class)) != null && emeradicResin.itemType() == 3
                && emeradicResin.isCursed();
    }

    public class fuelBuff extends ArtifactBuff {

        public int charges(){
            return charge;
        }

        public boolean canUseCharge(Wand wand, int charges){
            return charge >= wand.rechargeModifier() * charges * 10;
        }

        public void useCharge(Wand wand, int charges){
            useCharge(Math.round(wand.rechargeModifier() * charges * 10));
        }

        private void useCharge(int usedCharge) {
            charge -= usedCharge;
            exp += usedCharge;
            if (exp >= 30 + level()*10 && level() < levelCap){
                exp -= 30 + level()*10;
                upgrade();
                Catalog.countUse(EmeradicBattery.class);
                GLog.positive(Messages.get(EmeradicBattery.class, "level_up"));
            }
            if (target.sprite != null) {
                Emitter e = target.sprite.centerEmitter();
                if (e != null) e.burst(EmeradicEnergy.FACTORY, 20);
                target.sprite.showStatusWithIcon(CharSprite.NEGATIVE, String.valueOf(usedCharge), FloatingText.EMERADIC);
            }
            updateQuickslot();
        }
    }

    protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

        @Override
        public String textPrompt() {
            return Messages.get(EmeradicBattery.class, "prompt");
        }

        @Override
        public Class<?extends Bag> preferredBag(){
            return MagicalHolster.class;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return (item instanceof Wand && ((Wand) item).curCharges > 0) ||
                    (item instanceof Staff && ((Staff) item).minion() == null && (((Staff) item).partialCharge > 0 || ((Staff) item).curCharges == 1));
        }

        @Override
        public void onSelect(Item item) {
            if (itemSelectable(item)){
                Wand wand = null;
                Staff staff = null;
                if (item instanceof Wand)
                    wand = (Wand)item;
                if (item instanceof Staff)
                    staff = (Staff) item;
                Hero hero = Dungeon.hero;
                hero.sprite.operate( hero.pos );
                hero.busy();
                hero.spend( 2f );

                final int previousCharge = charge;
                int chargeGain = 0;
                if (wand != null)
                    chargeGain = Math.round(wand.curCharges * 10 * wand.rechargeModifier());
                else if (staff != null)
                    chargeGain = Math.round(staff.getChargeTurns() / (staff.curCharges == 1 ? 5.33f : staff.partialCharge * 5.33f));

                charge = Math.min(chargeCap, charge + chargeGain);
                hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, String.valueOf(charge - previousCharge), FloatingText.EMERADIC);
                if (wand != null)
                    wand.curCharges = 0;
                else if (staff != null)
                    staff.curCharges = 0;

                Sample.INSTANCE.play(Assets.Sounds.BURNING);
                Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
                ScrollOfRecharging.charge(hero);
                updateQuickslot();
            }
        }
    };

    public static class EmeradicEnergy extends EnergyParticle {
        public EmeradicEnergy() {
            super();
            color(0x5ADD70);
        }

        public static final Emitter.Factory FACTORY = new Emitter.Factory() {
            @Override
            public void emit( Emitter emitter, int index, float x, float y ) {
                ((EmeradicEnergy)emitter.recycle( EmeradicEnergy.class )).reset( x, y );
            }
            @Override
            public boolean lightMode() {
                return true;
            }
        };
    }
}
