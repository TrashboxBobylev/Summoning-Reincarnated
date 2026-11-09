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

package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroPerk;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class PerkTome extends Item {
    {
        image = ItemSpriteSheet.PERK_MASTERY;

        stackable = false;

        unique = true;

        defaultAction = AC_USE;
    }

    public HeroPerk perk;

    public PerkTome(){
        this(null);
    }

    public PerkTome(HeroPerk perk){
        this.perk = perk;
    }

    private static final String AC_USE = "USE";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_USE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_USE)){
            if (perk != null){
                Splash.at(hero.pos, perk.color, 10);

                detach( hero.belongings.backpack );
                Catalog.countUse( getClass() );

                hero.perks.add(perk);
                perk.onAcquiring(hero);

                hero.spend( Actor.TICK );
                hero.busy();
                hero.sprite.operate( hero.pos );
                Sample.INSTANCE.play( Assets.Sounds.MASTERY );

                GLog.p( Messages.get(this, "used"));
            }
        }
    }

    @Override
    public ItemSprite.Glowing glowing() {
        if (perk == null)
            return super.glowing();
        return new ItemSprite.Glowing(perk.color);
    }

    @Override
    public String desc() {
        String base = Messages.get(this, "desc");

        if (perk != null){
            base += "\n" + Messages.format("_%s_", perk.desc());
        }

        return base;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return Math.round(60*(perk != null ? perk.price() : 1.0f));
    }

    private static final String PERK = "perk";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PERK, perk);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        perk = bundle.getEnum(PERK, HeroPerk.class);
    }
}
