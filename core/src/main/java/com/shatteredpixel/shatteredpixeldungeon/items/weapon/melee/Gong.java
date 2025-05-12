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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyDamageTag;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Gong extends Cane {
    {
        image = ItemSpriteSheet.GONG;
        hitSound = Assets.Sounds.HIT_GONG;
        hitSoundPitch = 1f;

        tier = 4;
    }

    @Override
    public int max(int lvl) {
        return  4*(tier) +    //16 base, down from 25
                Math.round(lvl*(tier+1)/2f); //+2.5 per level, down from +5
    }

    public float multTagValue(){
        return multTagValue(buffedLvl());
    }

    public float multTagValue(int level){
        return 1.2f + 0.1f*level;
    }

    protected void applyTag(Char target){
        Buff.affect(target, AllyDamageTag.class, 4f).setMult(multTagValue());
    }

    public String statsInfo(){
        if (isIdentified()){
            return Messages.get(this, "stats_desc", Messages.decimalFormat("#.##x", multTagValue()));
        } else {
            return Messages.get(this, "typical_stats_desc", Messages.decimalFormat("#.##x", multTagValue()));
        }
    }

    @Override
    public String abilityInfo() {
        if (levelKnown){
            return Messages.get(this, "ability_desc", Messages.decimalFormat("#.##x", multTagValue()));
        } else {
            return Messages.get(this, "typical_ability_desc", Messages.decimalFormat("#.##x", multTagValue(0)));
        }
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        Cane.minionDefenseBoost(hero, target, 0, multTagValue(), this);
    }
}
