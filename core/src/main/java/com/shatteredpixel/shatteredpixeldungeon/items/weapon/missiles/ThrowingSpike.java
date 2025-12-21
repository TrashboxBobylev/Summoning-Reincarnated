/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * Summoning Pixel Dungeon Reincarnated
 * Copyright (C) 2023-2025 Trashbox Bobylev
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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ThrowingSpike extends MissileWeapon {

	{
		image = ItemSpriteSheet.THROWING_SPIKE;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.2f;

		bones = false;
	}

    public float min(float lvl, int type) {
        switch (type){
            case 1: return 2 + lvl*0.75f;
            case 2: return 2 + lvl*0.75f;
            case 3: return 2 + lvl*0.75f;
        }
        return 0;
    }

    public float max(float lvl, int type) {
        switch (type){
            case 1: return 5 + lvl*3f;
            case 2: return 5 + lvl*3f;
            case 3: return 5 + lvl*3f;
        }
        return 0;
    }

    public float baseUses(float lvl, int type){
        switch (type){
            case 1: return 12 + lvl*2.5f;
            case 2: return 6 + lvl*1.5f;
            case 3: return 8 + lvl*2f;
        }
        return 1;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (type() == 2 && defender.buff(Rank2StrikeTracker.class) == null){
            Buff.affect(defender, Rank2StrikeTracker.class);
            Buff.affect( attacker, MeleeWeapon.Charger.class ).gainCharge(0.75f);
            ScrollOfRecharging.charge( attacker );
        }
        if (type() == 3 && attacker instanceof Hero){
            Hero hero = (Hero) attacker;
            MeleeWeapon wep = (MeleeWeapon) hero.belongings.weapon;
            //do nothing
            if (wep == null || !wep.isEquipped(hero) ||
                    hero.heroClass != HeroClass.DUELIST && !Dungeon.isChallenged(Conducts.Conduct.EVERYTHING) ||
                    wep.STRReq() > hero.STR() ||
                    (Buff.affect(hero, MeleeWeapon.Charger.class).charges + Buff.affect(hero, MeleeWeapon.Charger.class).partialCharge) < wep.abilityChargeUse(hero, null)) {
                //do nothing
            } else {
                if (wep.targetingPrompt() == null) {
                    wep.duelistAbility(hero, hero.pos);
                } else {
                    wep.duelistAbility(hero, defender.pos);
                }
                updateQuickslot();
            }
        }
        return super.proc(attacker, defender, damage);
    }

    public static class Rank2StrikeTracker extends Buff {}
}
