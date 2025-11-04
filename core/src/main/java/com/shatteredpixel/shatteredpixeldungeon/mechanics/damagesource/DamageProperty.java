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

import java.util.EnumSet;

/**
 * All property tags a {@link DamageSource} can have.
 */
public enum DamageProperty {
    /**
     * Damage typically dealt by creatures.
     */
    PHYSICAL,
    /**
     * Damage that completely ignores armor.
     */
    IGNORES_ARMOR,
    /**
     * Damage that ignores invulnerability.
     */
    IGNORES_INVULNERABILITY,
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
     * Damage typically dealt by magic; opposite of {@link DamageProperty#PHYSICAL}.
     *
     */
    // TODO: Probably should stop overusing this...
    MAGICAL(IGNORES_ARMOR),
    /**
     * Damage that only hurts organic enemies.
     */
    ORGANIC,
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
     * Damage that is dealt by starvation or food poisoning.
     */
    HUNGER,
    /**
     * Physical damage that cannot be resisted by armor.
     */
    CRUMBLING(PHYSICAL, IGNORES_ARMOR),
    /**
     * Damage dealt by instant kills.
     */
    INSTANT_KILL(MAGICAL)
    ;

    public final EnumSet<DamageProperty> children;

    DamageProperty(){
        children = EnumSet.noneOf(DamageProperty.class);
    }

    DamageProperty(DamageProperty... children){
        this.children = EnumSet.of(children[0], children);
    }
}
