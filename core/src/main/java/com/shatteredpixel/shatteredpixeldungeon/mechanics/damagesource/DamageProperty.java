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

package com.shatteredpixel.shatteredpixeldungeon.mechanics.damagesource;

import java.util.Arrays;
import java.util.HashSet;

/**
 * All property tags a {@link DamageSource} can have.
 */
public enum DamageProperty {
    /**
     * Damage that accounts for armor.
     */
    ACKNOWLEDGES_ARMOR,
    /**
     * Damage that completely ignores armor.
     */
    IGNORES_ARMOR,
    /**
     * Damage that ignores invulnerability.
     */
    IGNORES_INVULNERABILITY,
    /**
     * Damage that doesn't trigger character's AI re-targeting.
     */
    IGNORES_AI_CHANGE,
    /**
     * Damage that ignores character's shielding.
     */
    IGNORES_SHIELDING,
    /**
     * Damage that is amplified on undead/demonic creatures.
     */
    HOLY,
    /**
     * Damage that is dealt by electric weapons or environment.
     */
    ELECTRIC,
    /**
     * Damage that is dealt by Conjurer's spells and effects.
     */
    SPIRIT,
    /**
     * Rare damage dealt by water sources, hurts firey enemies.
     */
    WATER,
    /**
     * Damage that is dealt by fire and fire magic.
     */
    FIRE,
    /**
     * Damage that is dealt by freezing.
     */
    FROST,
    /**
     * Damage that is dealt by poisonous sources.
     */
    POISON,
    /**
     * Damage that is dealt by acidic sources.
     */
    ACID,
    /**
     * Damage that is dealt by dark magic.
     */
    DARK,
    /**
     * Damage that decays over time.
     */
    DEFERRED,
    /**
     * Damage based on percentage of health.
     */
    PERCENTAGE_BASED,
    /**
     * Damage that cannot spread through life link.
     */
    LIFE_LINK_IGNORE,
    /**
     * Damage typically dealt by creatures.
     */
    PHYSICAL(ACKNOWLEDGES_ARMOR),
    /**
     * Damage typically dealt by magic; opposite of {@link DamageProperty#PHYSICAL}.
     *
     */
    MAGICAL,
    /**
     * Damage that only hurts organic enemies.
     */
    ORGANIC,
    /**
     * Damage suffered by decaying enemies, like ones inflicted with corruption.
     * Decay damage does not awake enemies.
     */
    DECAY,
    /**
     * Damage that is dealt by explosives.
     */
    EXPLOSIVE,
    /**
     * Damage that is dealt by traps.
     */
    TRAPPED,
    /**
     * Damage that is dealt by falling into the chasm.
     */
    FALL,
    /**
     * Damage that is dealt by knockback.
     */
    KNOCKBACK,
    /**
     * Property used to determine, if enemy's HP can be reduced.
     */
    HP_REDUCTION,
    /**
     * Damage that is dealt by bleeding out.
     */
    BLEEDING(ORGANIC),
    /**
     * Damage that is dealt by toxic gases.
     */
    TOXIC_GAS(ORGANIC),
    /**
     * Damage that is dealt by corrosion.
     */
    CORROSION(ACID),
    /**
     * Damage that is dealt by ascension's 5th phase.
     */
    ASCENSION(MAGICAL),
    /**
     * Damage that is dealt by starvation or food poisoning.
     */
    HUNGER(IGNORES_SHIELDING),
    /**
     * Damage that is dealt by ectoplasmic charge.
     */
    ECTO(MAGICAL, DARK),
    /**
     * Physical damage that cannot be resisted by armor.
     */
    CRUMBLING(PHYSICAL, IGNORES_ARMOR),
    /**
     * Damage dealt by instant kills.
     */
    INSTANT_KILL(MAGICAL)
    ;

    public HashSet<DamageProperty> children;

    DamageProperty(){
        children = new HashSet<>();
    }

    DamageProperty(DamageProperty... children){
        this.children = new HashSet<>(Arrays.asList(children));
    }
}
