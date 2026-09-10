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

package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.Image;

public enum SummIcons implements ChangeButton.ChangeIcon {

    V100_CONJURER,
    V100_GREY_RAT_STAFF,
    V100_ATTUNEMENT,
    V100_RING_AGATE,
    V100_LOVE_HOLDER,
    V100_CONJURER_ARMOR,
    V100_ENRAGE,
    V100_PERFUME_BREW,
    V100_CLEAVER,
    V100_SLINGSHOT,
    V100_SNAKE,
    V100_SLIME,
    V100_FINAL_FROGGIT,
    V100_GOLD,
    V100_UNSTABLE,
    V100_RESIST,
    V100_RANKINGS,

    V101_CLEAN_WATER,
    V101_WAND_OF_STENCH,

    V110_CRASH,
    V110_ARCANE_NUKE,
    V110_POWER_HOLDER,
    V110_STONE_HAMMER,
    V110_HALLS,
    V110_DOG,
    V110_VAMPIRIC,
    V110_SCIMITAR,

    V111_WAND_OF_CRYSTAL_BULLET,

    V112_WAND_OF_STARS,

    V120_FROGGIT_STAFF,
    V120_ABYSS,
    V120_SOUL_OF_YENDOR,
    V120_ROPES,
    V120_WAND_OF_CONJURATION,

    V121_SLIME_STAFF,
    V121_WOOLY_STICK,
    V121_SKELETON_STAFF,
    V121_CHICKEN_STAFF,
    V121_MAGIC_MISSILE_STAFF,
    V121_GNOLL_HUNTER_STAFF,
    V121_FROST_ELEMENTAL_STAFF,
    V121_WIZARD_STAFF,
    V121_DARK_ROSE,
    V121_ROBO_STAFF,
    V121_GOO_STAFF,
    V121_GASTER_BLASTER,
    V121_IMP_QUEEN_STAFF,
    V121_HACATU_STAFF,
    V121_STAR_BLAZING,
    V121_HUNTRESS,
    V121_GAME_MODE,
    V121_ENCHANT_TRANSFER,

    V122_ELEMENTAL_BLAST,
    V122_GAME_MODE,
    V122_MASTERY,

    V123_PERFUME_BLAST,
    V123_MIRROR_OF_FATES,

    V124_ARTIFACT_HOLDER,
    V124_MAIL_ARMOR,
    V124_ELIXIR_ICY,

    VR010_SCOUT_ARMOR,

    VR020_CONDUCTS,
    VR020_ADVENTURER,
    VR020_FIREBOMB,

    VR030_FROSTBURN,
    VR030_CLEAVER,
    VR030_TYPE_MANAGER,
    VR030_FIRE_ELIXIR,
    VR030_ELIXIR_OF_ATTUNEMENT,
    VR030_ATTUNEMENT_CONSTRUCT,
    VR030_STAFF_PLACEHOLDER,
    VR030_GRAY_RAT_STAFF,
    VR030_DWARF_WIZARD_STAFF,

    VR031_FACE,

    VR040_CHAOTIC_BOMB,
    VR040_ABYSS_SPAWNER,
    VR040_UPGRADE_CLUMP,
    VR040_SOUL_WIELDER,
    VR040_WILL_SORCERER,
    VR040_CONJURER_BOOK,
    VR040_SPIRITUAL_BARRIER,

    VR050_CROW_STAFF,
    VR050_CONJURER_HEROARM,

    VR053_GAUNTLET,

    VR060_MYSTERY_MERCHANT,
    VR060_JAVELIN,
    VR060_RANDOM_HERO,
    VR060_MYSTIC_PROWESS,

    VR062_COINFLIP,

    VR070_CHAOS,
    VR070_PLACEHOLDER,
    VR070_EXOTIC_RAIDO,
    VR070_EXPLOSIVE_PRIDE,
    VR070_RANDOM_CONDUCT,

    VRSOON_CLOTH_ARMOR,
    VRSOON_DIFFICULTY,
    VRSOON_RAT_KING,
    VRSOON_PROFESSIONS,



    ;

    int w, h;

    SummIcons(){
        w = h = 16;
    }

    SummIcons(int w, int h){
        this.w = w;
        this.h = h;
    }

    public int w(){
        return w;
    }

    public int h(){
        return h;
    }

    public Image get(){
        return ChangeIcons.get(this, Assets.Interfaces.SUMM_ICONS);
    }
}
