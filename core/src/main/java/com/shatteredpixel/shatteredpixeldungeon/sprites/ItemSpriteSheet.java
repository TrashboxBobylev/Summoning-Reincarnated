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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class ItemSpriteSheet {

	private static final int WIDTH = 16;
	public static final int SIZE = 16;

	public static TextureFilm film = new TextureFilm( Assets.Sprites.ITEMS, SIZE, SIZE );

	private static int xy(int x, int y){
		x -= 1; y -= 1;
		return x + WIDTH*y;
	}

	private static void assignItemRect( int item, int width, int height ){
		int x = (item % WIDTH) * SIZE;
		int y = (item / WIDTH) * SIZE;
		film.add( item, x, y, x+width, y+height);
	}

	private static final int PLACEHOLDERS   =                               xy(1, 1);   //18 slots
	//SOMETHING is the default item sprite at position 0. May show up ingame if there are bugs.
	public static final int SOMETHING       = PLACEHOLDERS+0;
	public static final int WEAPON_HOLDER   = PLACEHOLDERS+1;
	public static final int ARMOR_HOLDER    = PLACEHOLDERS+2;
	public static final int MISSILE_HOLDER  = PLACEHOLDERS+3;
	public static final int WAND_HOLDER     = PLACEHOLDERS+4;
	public static final int RING_HOLDER     = PLACEHOLDERS+5;
	public static final int ARTIFACT_HOLDER = PLACEHOLDERS+6;
	public static final int TRINKET_HOLDER  = PLACEHOLDERS+7;
	public static final int FOOD_HOLDER     = PLACEHOLDERS+8;
	public static final int BOMB_HOLDER     = PLACEHOLDERS+9;
	public static final int POTION_HOLDER   = PLACEHOLDERS+10;
	public static final int SEED_HOLDER     = PLACEHOLDERS+11;
	public static final int SCROLL_HOLDER   = PLACEHOLDERS+12;
	public static final int STONE_HOLDER    = PLACEHOLDERS+13;
	public static final int ELIXIR_HOLDER   = PLACEHOLDERS+14;
	public static final int SPELL_HOLDER    = PLACEHOLDERS+15;
	public static final int MOB_HOLDER      = PLACEHOLDERS+16;
	public static final int DOCUMENT_HOLDER = PLACEHOLDERS+17;
	static{
		assignItemRect(SOMETHING,       8,  13);
		assignItemRect(WEAPON_HOLDER,   14, 14);
		assignItemRect(ARMOR_HOLDER,    14, 12);
		assignItemRect(MISSILE_HOLDER,  15, 15);
		assignItemRect(WAND_HOLDER,     14, 14);
		assignItemRect(RING_HOLDER,     8,  10);
		assignItemRect(ARTIFACT_HOLDER, 15, 15);
		assignItemRect(TRINKET_HOLDER,  16, 11);
		assignItemRect(FOOD_HOLDER,     15, 11);
		assignItemRect(BOMB_HOLDER,     10, 13);
		assignItemRect(POTION_HOLDER,   12, 14);
		assignItemRect(SEED_HOLDER,     10, 10);
		assignItemRect(SCROLL_HOLDER,   15, 14);
		assignItemRect(STONE_HOLDER,    14, 12);
		assignItemRect(ELIXIR_HOLDER,   12, 14);
		assignItemRect(SPELL_HOLDER,    8,  16);
		assignItemRect(MOB_HOLDER,      15, 14);
		assignItemRect(DOCUMENT_HOLDER, 10, 11);
	}

	private static final int UNCOLLECTIBLE  =                               xy(3, 2);   //14 slots
	public static final int GOLD            = UNCOLLECTIBLE+0;
	public static final int ENERGY          = UNCOLLECTIBLE+1;

	public static final int DEWDROP         = UNCOLLECTIBLE+3;
	public static final int PETAL           = UNCOLLECTIBLE+4;
	public static final int SANDBAG         = UNCOLLECTIBLE+5;
	public static final int SPIRIT_ARROW    = UNCOLLECTIBLE+6;
	public static final int SPIRIT_BLAST    = UNCOLLECTIBLE+7;
	public static final int SUPER_SHOT      = UNCOLLECTIBLE+11;
	public static final int SPIRIT_DART     = UNCOLLECTIBLE+12;

	public static final int TENGU_BOMB      = UNCOLLECTIBLE+8;
	public static final int TENGU_SHOCKER   = UNCOLLECTIBLE+9;
	public static final int GEO_BOULDER     = UNCOLLECTIBLE+10;
	static{
		assignItemRect(GOLD,        15, 13);
		assignItemRect(ENERGY,      16, 16);

		assignItemRect(DEWDROP,     10, 10);
		assignItemRect(PETAL,       8,  8);
		assignItemRect(SANDBAG,     10, 10);
		assignItemRect(SPIRIT_ARROW,11, 11);
		assignItemRect(SPIRIT_BLAST, 15, 15);
		assignItemRect(SPIRIT_DART, 9, 9);

		assignItemRect(TENGU_BOMB,      10, 10);
		assignItemRect(TENGU_SHOCKER,   10, 10);
		assignItemRect(GEO_BOULDER,     16, 14);
	}

	private static final int CONTAINERS     =                               xy(1, 3);   //16 slots
	public static final int BONES           = CONTAINERS+0;
	public static final int REMAINS         = CONTAINERS+1;
	public static final int TOMB            = CONTAINERS+2;
	public static final int GRAVE           = CONTAINERS+3;
	public static final int CHEST           = CONTAINERS+4;
	public static final int LOCKED_CHEST    = CONTAINERS+5;
	public static final int CRYSTAL_CHEST   = CONTAINERS+6;
	public static final int EBONY_CHEST     = CONTAINERS+7;
	static{
		assignItemRect(BONES,           14, 11);
		assignItemRect(REMAINS,         14, 11);
		assignItemRect(TOMB,            14, 15);
		assignItemRect(GRAVE,           14, 15);
		assignItemRect(CHEST,           16, 14);
		assignItemRect(LOCKED_CHEST,    16, 14);
		assignItemRect(CRYSTAL_CHEST,   16, 14);
		assignItemRect(EBONY_CHEST,     16, 14);
	}

	private static final int MISC_CONSUMABLE =                              xy(1, 4);   //32 slots
	public static final int ANKH            = MISC_CONSUMABLE +0;
	public static final int STYLUS          = MISC_CONSUMABLE +1;
	public static final int SEAL            = MISC_CONSUMABLE +2;
	public static final int TORCH           = MISC_CONSUMABLE +3;
	public static final int BEACON          = MISC_CONSUMABLE +4;
	public static final int HONEYPOT        = MISC_CONSUMABLE +5;
	public static final int SHATTPOT        = MISC_CONSUMABLE +6;
	public static final int IRON_KEY        = MISC_CONSUMABLE +7;
	public static final int GOLDEN_KEY      = MISC_CONSUMABLE +8;
	public static final int CRYSTAL_KEY     = MISC_CONSUMABLE +9;
	public static final int SKELETON_KEY    = MISC_CONSUMABLE +10;
	public static final int MASK            = MISC_CONSUMABLE +11;
	public static final int CROWN           = MISC_CONSUMABLE +12;
	public static final int AMULET          = MISC_CONSUMABLE +13;
	public static final int MASTERY         = MISC_CONSUMABLE +14;
	public static final int KIT             = MISC_CONSUMABLE +15;
	public static final int SEAL_SHARD      = MISC_CONSUMABLE +16;
	public static final int BROKEN_STAFF    = MISC_CONSUMABLE +17;
	public static final int CLOAK_SCRAP     = MISC_CONSUMABLE +18;
	public static final int BOW_FRAGMENT    = MISC_CONSUMABLE +19;
	public static final int BROKEN_HILT     = MISC_CONSUMABLE +20;
	public static final int TORN_PAGE       = MISC_CONSUMABLE +21;
	public static final int ARMOR_REMAINS   = MISC_CONSUMABLE +22;
	public static final int SPELL_PAGE      = MISC_CONSUMABLE +23;
	public static final int TRINKET_CATA    = MISC_CONSUMABLE +24;


	static{
		assignItemRect(ANKH,            10, 16);
		assignItemRect(STYLUS,          12, 13);
		
		assignItemRect(SEAL,            13, 13);
		assignItemRect(TORCH,           12, 15);
		assignItemRect(BEACON,          16, 15);
		
		assignItemRect(HONEYPOT,        14, 12);
		assignItemRect(SHATTPOT,        14, 12);
		assignItemRect(IRON_KEY,        8,  14);
		assignItemRect(GOLDEN_KEY,      8,  14);
		assignItemRect(CRYSTAL_KEY,     8,  14);
		assignItemRect(SKELETON_KEY,    8,  14);
		assignItemRect(MASK,            11,  9);
		assignItemRect(CROWN,           13,  7);
		assignItemRect(AMULET,          16, 16);
		assignItemRect(MASTERY,         13, 16);
		assignItemRect(KIT,             16, 15);

		assignItemRect(SEAL_SHARD,      12, 12);
		assignItemRect(BROKEN_STAFF,    14, 10);
		assignItemRect(CLOAK_SCRAP,      9,  9);
		assignItemRect(BOW_FRAGMENT,    12,  9);
		assignItemRect(BROKEN_HILT,      9,  9);
		assignItemRect(ARMOR_REMAINS,    9,  9);
		assignItemRect(TORN_PAGE,       11, 13);
		assignItemRect(SPELL_PAGE,      11, 13);

		assignItemRect(TRINKET_CATA,    12, 11);
	}
	
	private static final int BOMBS          =                               xy(1, 6);   //16 slots
	public static final int BOMB            = BOMBS+0;
	public static final int DBL_BOMB        = BOMBS+1;
	public static final int FIRE_BOMB       = BOMBS+2;
	public static final int FROST_BOMB      = BOMBS+3;
	public static final int REGROWTH_BOMB   = BOMBS+4;
	public static final int SMOKE_BOMB      = BOMBS+5;
	public static final int FLASHBANG       = BOMBS+6;
	public static final int HOLY_BOMB       = BOMBS+7;
	public static final int WOOLY_BOMB      = BOMBS+14;
	public static final int NOISEMAKER      = BOMBS+9;
	public static final int ARCANE_BOMB     = BOMBS+10;
	public static final int SHRAPNEL_BOMB   = BOMBS+11;
	public static final int WEB_BOMB        = BOMBS+12;
	public static final int SUPPLY_BOMB     = BOMBS+13;
	public static final int SHRINK_BOMB     = BOMBS+8;
	public static final int CHAOS_BOMB      = BOMBS+15;

	static{
		assignItemRect(BOMB,            10, 13);
		assignItemRect(DBL_BOMB,        14, 13);
		assignItemRect(FIRE_BOMB,       13, 12);
		assignItemRect(FROST_BOMB,      13, 12);
		assignItemRect(REGROWTH_BOMB,   13, 12);
		assignItemRect(SMOKE_BOMB,      13, 12);
		assignItemRect(FLASHBANG,       9,  15);
		assignItemRect(HOLY_BOMB,       10, 14);
		assignItemRect(WOOLY_BOMB,      10, 13);
		assignItemRect(NOISEMAKER,      9,  15);
		assignItemRect(ARCANE_BOMB,     10, 13);
		assignItemRect(SHRAPNEL_BOMB,   10, 13);
		assignItemRect(WEB_BOMB,        14, 15);
		assignItemRect(SUPPLY_BOMB,     9,  15);
		assignItemRect(SHRINK_BOMB,     10, 13);
		assignItemRect(CHAOS_BOMB,      13, 12);
	}

	private static final int SUMM_WEAPONS  =                                xy(9, 9);
	public static final int STONE_HAMMER = SUMM_WEAPONS+0;
	public static final int A_STABBER    = SUMM_WEAPONS+1;
	public static final int PIKE         = SUMM_WEAPONS+2;
	public static final int STABBER      = SUMM_WEAPONS+3;
	public static final int JJANGO       = SUMM_WEAPONS+4;
	public static final int CANE         = SUMM_WEAPONS+5;
	public static final int GONG         = SUMM_WEAPONS+6;

	static {
		assignItemRect(STONE_HAMMER, 16, 16);
		assignItemRect(A_STABBER, 12, 13);
		assignItemRect(PIKE, 16, 16);
		assignItemRect(STABBER, 12, 13);
		assignItemRect(JJANGO, 15, 16);
	}

	private static final int WEP_TIER1      =                               xy(1, 7);   //8 slots
	public static final int WORN_SHORTSWORD = WEP_TIER1+0;
	public static final int CUDGEL          = WEP_TIER1+1;
	public static final int GLOVES          = WEP_TIER1+2;
	public static final int RAPIER          = WEP_TIER1+3;
	public static final int DAGGER          = WEP_TIER1+4;
	public static final int MAGES_STAFF     = WEP_TIER1+5;
	public static final int TOY_KNIFE       = WEP_TIER1+6;
	static{
		assignItemRect(WORN_SHORTSWORD, 13, 13);
		assignItemRect(CUDGEL,          15, 15);
		assignItemRect(GLOVES,          12, 16);
		assignItemRect(RAPIER,          13, 14);
		assignItemRect(DAGGER,          12, 13);
		assignItemRect(MAGES_STAFF,     15, 16);
		assignItemRect(TOY_KNIFE,       11, 12);
	}

	private static final int WEP_TIER2      =                               xy(9, 7);   //8 slots
	public static final int SHORTSWORD      = WEP_TIER2+0;
	public static final int HAND_AXE        = WEP_TIER2+1;
	public static final int SPEAR           = WEP_TIER2+2;
	public static final int QUARTERSTAFF    = WEP_TIER2+3;
	public static final int DIRK            = WEP_TIER2+4;
	public static final int SICKLE          = WEP_TIER2+5;
	static{
		assignItemRect(SHORTSWORD,      13, 13);
		assignItemRect(HAND_AXE,        12, 14);
		assignItemRect(SPEAR,           16, 16);
		assignItemRect(QUARTERSTAFF,    16, 16);
		assignItemRect(DIRK,            13, 14);
		assignItemRect(SICKLE,          15, 15);
	}

	private static final int WEP_TIER3      =                               xy(1, 8);   //8 slots
	public static final int SWORD           = WEP_TIER3+0;
	public static final int MACE            = WEP_TIER3+1;
	public static final int SCIMITAR        = WEP_TIER3+2;
	public static final int ROUND_SHIELD    = WEP_TIER3+3;
	public static final int SAI             = WEP_TIER3+4;
	public static final int WHIP            = WEP_TIER3+5;
	public static final int CLEAVER         = WEP_TIER3+6;
	static{
		assignItemRect(SWORD,           14, 14);
		assignItemRect(MACE,            15, 15);
		assignItemRect(SCIMITAR,        13, 16);
		assignItemRect(ROUND_SHIELD,    16, 16);
		assignItemRect(SAI,             16, 16);
		assignItemRect(WHIP,            14, 14);
		assignItemRect(CLEAVER,         15, 16);
	}

	private static final int WEP_TIER4      =                               xy(9, 8);   //8 slots
	public static final int LONGSWORD       = WEP_TIER4+0;
	public static final int BATTLE_AXE      = WEP_TIER4+1;
	public static final int FLAIL           = WEP_TIER4+2;
	public static final int RUNIC_BLADE     = WEP_TIER4+3;
	public static final int ASSASSINS_BLADE = WEP_TIER4+4;
	public static final int CROSSBOW        = WEP_TIER4+5;
	public static final int KATANA          = WEP_TIER4+6;
	static{
		assignItemRect(LONGSWORD,       15, 15);
		assignItemRect(BATTLE_AXE,      16, 16);
		assignItemRect(FLAIL,           14, 14);
		assignItemRect(RUNIC_BLADE,     14, 14);
		assignItemRect(ASSASSINS_BLADE, 14, 15);
		assignItemRect(CROSSBOW,        15, 15);
		assignItemRect(KATANA,          15, 16);
	}

	private static final int WEP_TIER5      =                               xy(1, 9);   //8 slots
	public static final int GREATSWORD      = WEP_TIER5+0;
	public static final int WAR_HAMMER      = WEP_TIER5+1;
	public static final int GLAIVE          = WEP_TIER5+2;
	public static final int GREATAXE        = WEP_TIER5+3;
	public static final int GREATSHIELD     = WEP_TIER5+4;
	public static final int GAUNTLETS       = WEP_TIER5+5;
	public static final int WAR_SCYTHE      = WEP_TIER5+6;
	static{
		assignItemRect(GREATSWORD,  16, 16);
		assignItemRect(WAR_HAMMER,  16, 16);
		assignItemRect(GLAIVE,      16, 16);
		assignItemRect(GREATAXE,    12, 16);
		assignItemRect(GREATSHIELD, 12, 16);
		assignItemRect(GAUNTLETS,   13, 15);
		assignItemRect(WAR_SCYTHE,  14, 15);
	}

	                                                                                    //8 free slots

	private static final int MISSILE_WEP    =                               xy(1, 10);  //16 slots. 3 per tier + bow
	public static final int SPIRIT_BOW      = MISSILE_WEP+0;
	
	public static final int THROWING_SPIKE  = MISSILE_WEP+1;
	public static final int THROWING_KNIFE  = MISSILE_WEP+2;
	public static final int THROWING_STONE  = MISSILE_WEP+3;
	
	public static final int FISHING_SPEAR   = MISSILE_WEP+4;
	public static final int SHURIKEN        = MISSILE_WEP+5;
	public static final int THROWING_CLUB   = MISSILE_WEP+6;
	
	public static final int THROWING_SPEAR  = MISSILE_WEP+7;
	public static final int BOLAS           = MISSILE_WEP+8;
	public static final int KUNAI           = MISSILE_WEP+9;
	
	public static final int JAVELIN         = MISSILE_WEP+10;
	public static final int TOMAHAWK        = MISSILE_WEP+11;
	public static final int BOOMERANG       = MISSILE_WEP+12;
	
	public static final int TRIDENT         = MISSILE_WEP+13;
	public static final int THROWING_HAMMER = MISSILE_WEP+14;
	public static final int FORCE_CUBE      = MISSILE_WEP+15;
	public static final int LIGHT_KNIFE     = MISSILE_WEP+29;

	static{
		assignItemRect(SPIRIT_BOW,      16, 16);
		
		assignItemRect(THROWING_SPIKE,  11, 10);
		assignItemRect(THROWING_KNIFE,  12, 13);
		assignItemRect(THROWING_STONE,  12, 10);
		
		assignItemRect(FISHING_SPEAR,   11, 11);
		assignItemRect(SHURIKEN,        12, 12);
		assignItemRect(THROWING_CLUB,   12, 12);
		
		assignItemRect(THROWING_SPEAR,  13, 13);
		assignItemRect(BOLAS,           15, 14);
		assignItemRect(KUNAI,           15, 15);
		
		assignItemRect(JAVELIN,         16, 16);
		assignItemRect(TOMAHAWK,        13, 13);
		assignItemRect(BOOMERANG,       14, 14);
		
		assignItemRect(TRIDENT,         16, 16);
		assignItemRect(THROWING_HAMMER, 12, 12);
		assignItemRect(FORCE_CUBE,      11, 12);
	}
	
	public static final int DARTS    =                                      xy(1, 11);  //16 slots
	public static final int DART            = DARTS+0;
	public static final int ROT_DART        = DARTS+1;
	public static final int INCENDIARY_DART = DARTS+2;
	public static final int ADRENALINE_DART = DARTS+3;
	public static final int HEALING_DART    = DARTS+4;
	public static final int CHILLING_DART   = DARTS+5;
	public static final int SHOCKING_DART   = DARTS+6;
	public static final int POISON_DART     = DARTS+7;
	public static final int CLEANSING_DART  = DARTS+8;
	public static final int PARALYTIC_DART  = DARTS+9;
	public static final int HOLY_DART       = DARTS+10;
	public static final int DISPLACING_DART = DARTS+11;
	public static final int BLINDING_DART   = DARTS+12;
	static {
		for (int i = DARTS; i < DARTS+16; i++)
			assignItemRect(i, 15, 15);
		assignItemRect(LIGHT_KNIFE, 11, 12);
	}
	
	private static final int ARMOR          =                               xy(1, 12);  //16 slots
	public static final int ARMOR_CLOTH     = ARMOR+0;
	public static final int ARMOR_LEATHER   = ARMOR+1;
	public static final int ARMOR_MAIL      = ARMOR+2;
	public static final int ARMOR_SCALE     = ARMOR+3;
	public static final int ARMOR_PLATE     = ARMOR+4;
	public static final int ARMOR_WARRIOR   = ARMOR+5;
	public static final int ARMOR_MAGE      = ARMOR+6;
	public static final int ARMOR_ROGUE     = ARMOR+7;
	public static final int ARMOR_HUNTRESS  = ARMOR+8;
	public static final int ARMOR_DUELIST   = ARMOR+9;
	public static final int ARMOR_CLERIC    = ARMOR+10;
	public static final int ARMOR_SCOUT     = ARMOR+11;
	public static final int ARMOR_ADVENTURER= ARMOR+12;
	public static final int ARMOR_CONJURER  = ARMOR+13;
	public static final int ARMOR_T6CONJURER= ARMOR+14;
	static{
		assignItemRect(ARMOR_CLOTH,     15, 12);
		assignItemRect(ARMOR_LEATHER,   14, 13);
		assignItemRect(ARMOR_MAIL,      14, 12);
		assignItemRect(ARMOR_SCALE,     14, 11);
		assignItemRect(ARMOR_PLATE,     12, 12);
		assignItemRect(ARMOR_WARRIOR,   12, 12);
		assignItemRect(ARMOR_MAGE,      15, 15);
		assignItemRect(ARMOR_ROGUE,     14, 12);
		assignItemRect(ARMOR_HUNTRESS,  13, 15);
		assignItemRect(ARMOR_DUELIST,   12, 13);
		assignItemRect(ARMOR_SCOUT,  13, 15);
		assignItemRect(ARMOR_ADVENTURER, 14, 13);
		assignItemRect(ARMOR_CONJURER,  15, 15);
		assignItemRect(ARMOR_CLERIC,    13, 14);
		assignItemRect(ARMOR_T6CONJURER,15, 15);
	}

	private static final int STAFF_T1       =                               xy(1, 34);
	public static final int FROGGIT_STAFF     = STAFF_T1+0;

	static {
		assignItemRect(FROGGIT_STAFF, 14, 14);
	}

	private static final int STAFF_T2       =                               xy(7, 34);
	public static final int GREY_RAT_STAFF      = STAFF_T2+0;
	public static final int GNOLL_HUNTER_STAFF  = STAFF_T2+1;
	public static final int WOOLY_STAFF         = STAFF_T2+2;

	static {
		assignItemRect(GREY_RAT_STAFF, 16, 16);
		assignItemRect(GNOLL_HUNTER_STAFF, 16, 16);
		assignItemRect(WOOLY_STAFF, 16, 16);
	}

	private static final int STAFF_T3       =                               xy(1, 35);
	public static final int SKELETON_STAFF      = STAFF_T3+0;
	public static final int SLIME_STAFF         = STAFF_T3+1;
	public static final int CHICKEN_STAFF       = STAFF_T3+2;
	public static final int MAGIC_MISSILE_STAFF = STAFF_T3+3;
	public static final int CROW_STAFF          = STAFF_T3+4;

	static {
		assignItemRect(SKELETON_STAFF,     15, 15);
		assignItemRect(SLIME_STAFF,        16, 16);
		assignItemRect(CHICKEN_STAFF,      16, 16);
		assignItemRect(MAGIC_MISSILE_STAFF,14, 14);
		assignItemRect(CROW_STAFF,         15, 16);
	}

	private static final int STAFF_T4       =                               xy(7, 35);
	public static final int WIZARD_STAFF              = STAFF_T4+0;
	public static final int ROBO_STAFF                = STAFF_T4+1;
	public static final int ROSE_STAFF                = STAFF_T4+2;
	public static final int FROST_ELEMENTAL_STAFF     = STAFF_T4+3;

	static {
		assignItemRect(WIZARD_STAFF,           16, 16);
		assignItemRect(ROBO_STAFF,             16, 16);
		assignItemRect(ROSE_STAFF,             14, 14);
		assignItemRect(FROST_ELEMENTAL_STAFF,  15, 15);
	}

	private static final int STAFF_T5       =                               xy(1, 36);
	public static final int GOO_STAFF    = STAFF_T5+0;
	public static final int GASTER_STAFF = STAFF_T5+1;
	public static final int IMP_STAFF    = STAFF_T5+2;
	public static final int HACATU_STAFF = STAFF_T5+3;
	static {
		assignItemRect(GOO_STAFF,    16, 16);
		assignItemRect(GASTER_STAFF, 16, 16);
		assignItemRect(IMP_STAFF,    16, 16);
		assignItemRect(HACATU_STAFF, 14, 16);
	}

	private static final int WANDS              =                           xy(1, 14);  //16 slots
	public static final int WAND_MAGIC_MISSILE  = WANDS+0;
	public static final int WAND_FIREBOLT       = WANDS+1;
	public static final int WAND_FROST          = WANDS+2;
	public static final int WAND_LIGHTNING      = WANDS+3;
	public static final int WAND_DISINTEGRATION = WANDS+4;
	public static final int WAND_PRISMATIC_LIGHT= WANDS+5;
	public static final int WAND_CORROSION      = WANDS+6;
	public static final int WAND_LIVING_EARTH   = WANDS+7;
	public static final int WAND_BLAST_WAVE     = WANDS+8;
	public static final int WAND_CORRUPTION     = WANDS+9;
	public static final int WAND_WARDING        = WANDS+10;
	public static final int WAND_REGROWTH       = WANDS+11;
	public static final int WAND_TRANSFUSION    = WANDS+12;
	public static final int WAND_CONJURATION    = WANDS+13;
	public static final int WAND_CRYSTAL        = WANDS+14;
	public static final int WAND_STARS          = WANDS+15;
	static {
		for (int i = WANDS; i < WANDS+16; i++)
			assignItemRect(i, 14, 14);
	}

	private static final int RINGS          =                               xy(1, 15);  //16 slots
	public static final int RING_GARNET     = RINGS+0;
	public static final int RING_RUBY       = RINGS+1;
	public static final int RING_TOPAZ      = RINGS+2;
	public static final int RING_EMERALD    = RINGS+3;
	public static final int RING_ONYX       = RINGS+4;
	public static final int RING_OPAL       = RINGS+5;
	public static final int RING_TOURMALINE = RINGS+6;
	public static final int RING_SAPPHIRE   = RINGS+7;
	public static final int RING_AMETHYST   = RINGS+8;
	public static final int RING_QUARTZ     = RINGS+9;
	public static final int RING_AGATE      = RINGS+10;
	public static final int RING_DIAMOND    = RINGS+11;
	static {
		for (int i = RINGS; i < RINGS+16; i++)
			assignItemRect(i, 8, 10);
	}

	private static final int ARTIFACTS          =                            xy(1, 16);  //24 slots
	public static final int ARTIFACT_CLOAK      = ARTIFACTS+0;
	public static final int ARTIFACT_ARMBAND    = ARTIFACTS+1;
	public static final int ARTIFACT_CAPE       = ARTIFACTS+2;
	public static final int ARTIFACT_TALISMAN   = ARTIFACTS+3;
	public static final int ARTIFACT_HOURGLASS  = ARTIFACTS+4;
	public static final int ARTIFACT_TOOLKIT    = ARTIFACTS+5;
	public static final int ARTIFACT_SPELLBOOK  = ARTIFACTS+6;
	public static final int ARTIFACT_BEACON     = ARTIFACTS+7;
	public static final int ARTIFACT_CHAINS     = ARTIFACTS+8;
	public static final int ARTIFACT_HORN1      = ARTIFACTS+9;
	public static final int ARTIFACT_HORN2      = ARTIFACTS+10;
	public static final int ARTIFACT_HORN3      = ARTIFACTS+11;
	public static final int ARTIFACT_HORN4      = ARTIFACTS+12;
	public static final int ARTIFACT_CHALICE1   = ARTIFACTS+13;
	public static final int ARTIFACT_CHALICE2   = ARTIFACTS+14;
	public static final int ARTIFACT_CHALICE3   = ARTIFACTS+15;
	public static final int ARTIFACT_SANDALS    = ARTIFACTS+16;
	public static final int ARTIFACT_SHOES      = ARTIFACTS+17;
	public static final int ARTIFACT_BOOTS      = ARTIFACTS+18;
	public static final int ARTIFACT_GREAVES    = ARTIFACTS+19;
	public static final int ARTIFACT_ROSE1      = ARTIFACTS+20;
	public static final int ARTIFACT_ROSE2      = ARTIFACTS+21;
	public static final int ARTIFACT_ROSE3      = ARTIFACTS+22;
	public static final int ARTIFACT_TOME       = ARTIFACTS+23;
	static{
		assignItemRect(ARTIFACT_CLOAK,      9,  15);
		assignItemRect(ARTIFACT_ARMBAND,    16, 13);
		assignItemRect(ARTIFACT_CAPE,       16, 14);
		assignItemRect(ARTIFACT_TALISMAN,   15, 13);
		assignItemRect(ARTIFACT_HOURGLASS,  13, 16);
		assignItemRect(ARTIFACT_TOOLKIT,    15, 13);
		assignItemRect(ARTIFACT_SPELLBOOK,  13, 16);
		assignItemRect(ARTIFACT_BEACON,     16, 16);
		assignItemRect(ARTIFACT_CHAINS,     16, 16);
		assignItemRect(ARTIFACT_HORN1,      15, 15);
		assignItemRect(ARTIFACT_HORN2,      15, 15);
		assignItemRect(ARTIFACT_HORN3,      15, 15);
		assignItemRect(ARTIFACT_HORN4,      15, 15);
		assignItemRect(ARTIFACT_CHALICE1,   12, 15);
		assignItemRect(ARTIFACT_CHALICE2,   12, 15);
		assignItemRect(ARTIFACT_CHALICE3,   12, 15);
		assignItemRect(ARTIFACT_SANDALS,    16, 6 );
		assignItemRect(ARTIFACT_SHOES,      16, 6 );
		assignItemRect(ARTIFACT_BOOTS,      16, 9 );
		assignItemRect(ARTIFACT_GREAVES,    16, 14);
		assignItemRect(ARTIFACT_ROSE1,      14, 14);
		assignItemRect(ARTIFACT_ROSE2,      14, 14);
		assignItemRect(ARTIFACT_ROSE3,      14, 14);
		assignItemRect(ARTIFACT_TOME,       14, 16);
	}

	private static final int TRINKETS        =                               xy(9, 17);  //24 slots
	public static final int RAT_SKULL       = TRINKETS+0;
	public static final int PARCHMENT_SCRAP = TRINKETS+1;
	public static final int PETRIFIED_SEED  = TRINKETS+2;
	public static final int EXOTIC_CRYSTALS = TRINKETS+3;
	public static final int MOSSY_CLUMP     = TRINKETS+4;
	public static final int SUNDIAL         = TRINKETS+5;
	public static final int CLOVER          = TRINKETS+6;
	public static final int TRAP_MECHANISM  = TRINKETS+7;
	public static final int MIMIC_TOOTH     = TRINKETS+8;
	public static final int WONDROUS_RESIN  = TRINKETS+9;
	public static final int EYE_OF_NEWT     = TRINKETS+10;
	public static final int SALT_CUBE       = TRINKETS+11;
	public static final int BLOOD_VIAL      = TRINKETS+12;
	public static final int OBLIVION_SHARD  = TRINKETS+13;
	public static final int CHAOTIC_CENSER  = TRINKETS+14;
	public static final int FERRET_TUFT     = TRINKETS+15;
	static{
		assignItemRect(RAT_SKULL,       16, 11);
		assignItemRect(PARCHMENT_SCRAP, 10, 14);
		assignItemRect(PETRIFIED_SEED,  10, 10);
		assignItemRect(EXOTIC_CRYSTALS, 14, 13);
		assignItemRect(MOSSY_CLUMP,     12, 11);
		assignItemRect(SUNDIAL,         16, 12);
		assignItemRect(CLOVER,          11, 15);
		assignItemRect(TRAP_MECHANISM,  13, 15);
		assignItemRect(MIMIC_TOOTH,     8,  15);
		assignItemRect(WONDROUS_RESIN,  12, 11);
		assignItemRect(EYE_OF_NEWT,     12, 12);
		assignItemRect(SALT_CUBE,       12, 13);
		assignItemRect(BLOOD_VIAL,      6,  15);
		assignItemRect(OBLIVION_SHARD,  7,  14);
		assignItemRect(CHAOTIC_CENSER,  13, 15);
		assignItemRect(FERRET_TUFT,     16, 15);
	}

	private static final int SUMM_MISCS     =                               xy(1, 26);
	public static final int ROPES           = SUMM_MISCS+1;
	public static final int RUNIC_SHOT      = SPIRIT_DART+1;
	public static final int CLEAN_WATER     = SUMM_MISCS+0;
	public static final int STAFF_PLACEHOLDER= SUMM_MISCS+2;
	public static final int FACELESS_ITEM   = SUMM_MISCS+3;
	public static final int UPGRADE_CLUMP   = SUMM_MISCS+4;
	public static final int RED_CRYSTAL     = SUMM_MISCS+5;
	public static final int CHAOSSTONE      = SUMM_MISCS+6;
	public static final int SLINGSHOT       = SUMM_MISCS+7;
	static {
		assignItemRect(ROPES, 16, 16);
		assignItemRect(RUNIC_SHOT, 13, 13);
		assignItemRect(CLEAN_WATER, 10, 14);
		assignItemRect(STAFF_PLACEHOLDER, 14, 14);
		assignItemRect(FACELESS_ITEM, 12, 11);
		assignItemRect(UPGRADE_CLUMP,   12, 11);
		assignItemRect(RED_CRYSTAL,     10, 15);
		assignItemRect(CHAOSSTONE, 10, 15);
		assignItemRect(SLINGSHOT, 12, 13);
	}

	private static final int SCROLLS        =                               xy(1, 19);  //16 slots
	public static final int SCROLL_KAUNAN   = SCROLLS+0;
	public static final int SCROLL_SOWILO   = SCROLLS+1;
	public static final int SCROLL_LAGUZ    = SCROLLS+2;
	public static final int SCROLL_YNGVI    = SCROLLS+3;
	public static final int SCROLL_GYFU     = SCROLLS+4;
	public static final int SCROLL_RAIDO    = SCROLLS+5;
	public static final int SCROLL_ISAZ     = SCROLLS+6;
	public static final int SCROLL_MANNAZ   = SCROLLS+7;
	public static final int SCROLL_NAUDIZ   = SCROLLS+8;
	public static final int SCROLL_BERKANAN = SCROLLS+9;
	public static final int SCROLL_ODAL     = SCROLLS+10;
	public static final int SCROLL_TIWAZ    = SCROLLS+11;

	public static final int ARCANE_RESIN    = SCROLLS+13;
	static {
		for (int i = SCROLLS; i < SCROLLS+16; i++)
			assignItemRect(i, 15, 14);
		assignItemRect(ARCANE_RESIN   , 12, 11);
	}
	
	private static final int EXOTIC_SCROLLS =                               xy(1, 20);  //16 slots
	public static final int EXOTIC_KAUNAN   = EXOTIC_SCROLLS+0;
	public static final int EXOTIC_SOWILO   = EXOTIC_SCROLLS+1;
	public static final int EXOTIC_LAGUZ    = EXOTIC_SCROLLS+2;
	public static final int EXOTIC_YNGVI    = EXOTIC_SCROLLS+3;
	public static final int EXOTIC_GYFU     = EXOTIC_SCROLLS+4;
	public static final int EXOTIC_RAIDO    = EXOTIC_SCROLLS+5;
	public static final int EXOTIC_ISAZ     = EXOTIC_SCROLLS+6;
	public static final int EXOTIC_MANNAZ   = EXOTIC_SCROLLS+7;
	public static final int EXOTIC_NAUDIZ   = EXOTIC_SCROLLS+8;
	public static final int EXOTIC_BERKANAN = EXOTIC_SCROLLS+9;
	public static final int EXOTIC_ODAL     = EXOTIC_SCROLLS+10;
	public static final int EXOTIC_TIWAZ    = EXOTIC_SCROLLS+11;
	static {
		for (int i = EXOTIC_SCROLLS; i < EXOTIC_SCROLLS+16; i++)
			assignItemRect(i, 15, 14);
	}
	
	private static final int STONES             =                           xy(1, 21);  //16 slots
	public static final int STONE_AGGRESSION    = STONES+0;
	public static final int STONE_AUGMENTATION  = STONES+1;
	public static final int STONE_FEAR          = STONES+2;
	public static final int STONE_BLAST         = STONES+3;
	public static final int STONE_BLINK         = STONES+4;
	public static final int STONE_CLAIRVOYANCE  = STONES+5;
	public static final int STONE_SLEEP         = STONES+6;
	public static final int STONE_DETECT        = STONES+7;
	public static final int STONE_ENCHANT       = STONES+8;
	public static final int STONE_FLOCK         = STONES+9;
	public static final int STONE_INTUITION     = STONES+10;
	public static final int STONE_SHOCK         = STONES+11;
	static {
		for (int i = STONES; i < STONES+16; i++)
			assignItemRect(i, 14, 12);
	}

	private static final int POTIONS        =                               xy(1, 22);  //16 slots
	public static final int POTION_CRIMSON  = POTIONS+0;
	public static final int POTION_AMBER    = POTIONS+1;
	public static final int POTION_GOLDEN   = POTIONS+2;
	public static final int POTION_JADE     = POTIONS+3;
	public static final int POTION_TURQUOISE= POTIONS+4;
	public static final int POTION_AZURE    = POTIONS+5;
	public static final int POTION_INDIGO   = POTIONS+6;
	public static final int POTION_MAGENTA  = POTIONS+7;
	public static final int POTION_BISTRE   = POTIONS+8;
	public static final int POTION_CHARCOAL = POTIONS+9;
	public static final int POTION_SILVER   = POTIONS+10;
	public static final int POTION_IVORY    = POTIONS+11;

	public static final int LIQUID_METAL    = POTIONS+13;
	static {
		for (int i = POTIONS; i < POTIONS+16; i++)
			assignItemRect(i, 12, 14);
		assignItemRect(LIQUID_METAL,    8, 15);
	}
	
	private static final int EXOTIC_POTIONS =                               xy(1, 23);  //16 slots
	public static final int EXOTIC_CRIMSON  = EXOTIC_POTIONS+0;
	public static final int EXOTIC_AMBER    = EXOTIC_POTIONS+1;
	public static final int EXOTIC_GOLDEN   = EXOTIC_POTIONS+2;
	public static final int EXOTIC_JADE     = EXOTIC_POTIONS+3;
	public static final int EXOTIC_TURQUOISE= EXOTIC_POTIONS+4;
	public static final int EXOTIC_AZURE    = EXOTIC_POTIONS+5;
	public static final int EXOTIC_INDIGO   = EXOTIC_POTIONS+6;
	public static final int EXOTIC_MAGENTA  = EXOTIC_POTIONS+7;
	public static final int EXOTIC_BISTRE   = EXOTIC_POTIONS+8;
	public static final int EXOTIC_CHARCOAL = EXOTIC_POTIONS+9;
	public static final int EXOTIC_SILVER   = EXOTIC_POTIONS+10;
	public static final int EXOTIC_IVORY    = EXOTIC_POTIONS+11;
	static {
		for (int i = EXOTIC_POTIONS; i < EXOTIC_POTIONS+16; i++)
			assignItemRect(i, 12, 13);
	}

	private static final int SEEDS              =                           xy(1, 24);  //16 slots
	public static final int SEED_ROTBERRY       = SEEDS+0;
	public static final int SEED_FIREBLOOM      = SEEDS+1;
	public static final int SEED_SWIFTTHISTLE   = SEEDS+2;
	public static final int SEED_SUNGRASS       = SEEDS+3;
	public static final int SEED_ICECAP         = SEEDS+4;
	public static final int SEED_STORMVINE      = SEEDS+5;
	public static final int SEED_SORROWMOSS     = SEEDS+6;
	public static final int SEED_MAGEROYAL      = SEEDS+7;
	public static final int SEED_EARTHROOT      = SEEDS+8;
	public static final int SEED_STARFLOWER     = SEEDS+9;
	public static final int SEED_FADELEAF       = SEEDS+10;
	public static final int SEED_BLINDWEED      = SEEDS+11;
	static{
		for (int i = SEEDS; i < SEEDS+16; i++)
			assignItemRect(i, 10, 10);
	}
	
	private static final int BREWS          =                               xy(1, 25);  //8 slots
	public static final int BREW_INFERNAL   = BREWS+0;
	public static final int BREW_BLIZZARD   = BREWS+1;
	public static final int BREW_SHOCKING   = BREWS+2;
	public static final int BREW_CAUSTIC    = BREWS+3;
	public static final int BREW_FROSTBURN	= BREWS+6;
	public static final int BREW_AQUA       = BREWS+4;
	public static final int BREW_UNSTABLE   = BREWS+5;

	private static final int ELIXIRS        =                               xy(9, 25);  //8 slots
	public static final int ELIXIR_HONEY    = ELIXIRS+0;
	public static final int ELIXIR_AQUA     = ELIXIRS+1;
	public static final int ELIXIR_MIGHT    = ELIXIRS+2;
	public static final int ELIXIR_DRAGON   = ELIXIRS+3;
	public static final int ELIXIR_TOXIC    = ELIXIRS+4;
	public static final int ELIXIR_ICY      = ELIXIRS+5;
	public static final int ELIXIR_ARCANE   = ELIXIRS+6;
	public static final int ELIXIR_ATTUNED  = ELIXIRS-1;
	public static final int ELIXIR_FEATHER  = ELIXIRS+7;
	static{
		for (int i = BREWS; i < BREWS+16; i++)
			assignItemRect(i, 12, 14);

		assignItemRect(BREW_AQUA, 9, 11);
	}
	
	                                                                                    //16 free slots
	
	private static final int SPELLS         =                               xy(1, 27);  //16 slots
	public static final int WILD_ENERGY     = SPELLS+0;
	public static final int PHASE_SHIFT     = SPELLS+1;
	public static final int TELE_GRAB       = SPELLS+2;
	public static final int UNSTABLE_SPELL  = SPELLS+3;

	public static final int CURSE_INFUSE    = SPELLS+5;
	public static final int MAGIC_INFUSE    = SPELLS+6;
	public static final int ALCHEMIZE       = SPELLS+7;
	public static final int RECYCLE         = SPELLS+8;

	public static final int RECLAIM_TRAP    = SPELLS+10;
	public static final int RETURN_BEACON   = SPELLS+11;
	public static final int SUMMON_ELE      = SPELLS+12;
	public static final int RANK_MANAGER    = SPELLS+14;
	public static final int ENCHANT_TRANSFER= SPELLS+15;

	static{
		assignItemRect(WILD_ENERGY,     12, 11);
		assignItemRect(PHASE_SHIFT,     12, 11);
		assignItemRect(TELE_GRAB,       12, 11);
		assignItemRect(UNSTABLE_SPELL,  12, 13);

		assignItemRect(CURSE_INFUSE,    10, 15);
		assignItemRect(MAGIC_INFUSE,    10, 15);
		assignItemRect(ALCHEMIZE,       10, 15);
		assignItemRect(RECYCLE,         10, 15);

		assignItemRect(RECLAIM_TRAP,     8, 16);
		assignItemRect(RETURN_BEACON,    8, 16);
		assignItemRect(SUMMON_ELE,       8, 16);

		assignItemRect(RANK_MANAGER,     8, 16);
		assignItemRect(ENCHANT_TRANSFER,15, 14);
	}
	
	private static final int FOOD       =                                   xy(1, 28);  //16 slots
	public static final int MEAT            = FOOD+0;
	public static final int STEAK           = FOOD+1;
	public static final int STEWED          = FOOD+2;
	public static final int OVERPRICED      = FOOD+3;
	public static final int CARPACCIO       = FOOD+4;
	public static final int RATION          = FOOD+5;
	public static final int PASTY           = FOOD+6;
	public static final int MEAT_PIE        = FOOD+7;
	public static final int BLANDFRUIT      = FOOD+8;
	public static final int BLAND_CHUNKS    = FOOD+9;
	public static final int BERRY           = FOOD+10;
	public static final int PHANTOM_MEAT    = FOOD+11;
	public static final int SUPPLY_RATION   = FOOD+12;
	static{
		assignItemRect(MEAT,            15, 11);
		assignItemRect(STEAK,           15, 11);
		assignItemRect(STEWED,          15, 11);
		assignItemRect(OVERPRICED,      14, 11);
		assignItemRect(CARPACCIO,       15, 11);
		assignItemRect(RATION,          16, 12);
		assignItemRect(PASTY,           16, 11);
		assignItemRect(MEAT_PIE,        16, 12);
		assignItemRect(BLANDFRUIT,      9,  12);
		assignItemRect(BLAND_CHUNKS,    14,  6);
		assignItemRect(BERRY,           9,  11);
		assignItemRect(PHANTOM_MEAT,    15, 11);
		assignItemRect(SUPPLY_RATION,   16, 12);
	}

	private static final int HOLIDAY_FOOD   =                               xy(1, 29);  //16 slots
	public static final int STEAMED_FISH    = HOLIDAY_FOOD+0;
	public static final int FISH_LEFTOVER   = HOLIDAY_FOOD+1;
	public static final int CHOC_AMULET     = HOLIDAY_FOOD+2;
	public static final int EASTER_EGG      = HOLIDAY_FOOD+3;
	public static final int RAINBOW_POTION  = HOLIDAY_FOOD+4;
	public static final int SHATTERED_CAKE  = HOLIDAY_FOOD+5;
	public static final int PUMPKIN_PIE     = HOLIDAY_FOOD+6;
	public static final int VANILLA_CAKE    = HOLIDAY_FOOD+7;
	public static final int CANDY_CANE      = HOLIDAY_FOOD+8;
	public static final int SPARKLING_POTION= HOLIDAY_FOOD+9;
	static{
		assignItemRect(STEAMED_FISH,    16, 12);
		assignItemRect(FISH_LEFTOVER,   16, 12);
		assignItemRect(CHOC_AMULET,     16, 16);
		assignItemRect(EASTER_EGG,      12, 14);
		assignItemRect(RAINBOW_POTION,  12, 14);
		assignItemRect(SHATTERED_CAKE,  14, 13);
		assignItemRect(PUMPKIN_PIE,     16, 12);
		assignItemRect(VANILLA_CAKE,    14, 13);
		assignItemRect(CANDY_CANE,      13, 16);
		assignItemRect(SPARKLING_POTION, 7, 16);
	}

	private static final int QUEST  =                                       xy(1, 30);  //16 slots
	public static final int DUST    = QUEST+1;
	public static final int CANDLE  = QUEST+2;
	public static final int EMBER   = QUEST+3;
	public static final int PICKAXE = QUEST+4;
	public static final int ORE     = QUEST+5;
	public static final int TOKEN   = QUEST+6;
	public static final int BLOB    = QUEST+7;
	public static final int SHARD   = QUEST+8;
	static{
		assignItemRect(DUST,    12, 11);
		assignItemRect(CANDLE,  12, 12);
		assignItemRect(EMBER,   12, 11);
		assignItemRect(PICKAXE, 14, 14);
		assignItemRect(ORE,     15, 15);
		assignItemRect(TOKEN,   12, 12);
		assignItemRect(BLOB,    10,  9);
		assignItemRect(SHARD,    8, 10);
	}

	private static final int BAGS       =                                   xy(1, 31);  //16 slots
	public static final int WATERSKIN   = BAGS+0;
	public static final int BACKPACK    = BAGS+1;
	public static final int POUCH       = BAGS+2;
	public static final int HOLDER      = BAGS+3;
	public static final int BANDOLIER   = BAGS+4;
	public static final int HOLSTER     = BAGS+5;
	public static final int VIAL        = BAGS+6;
	public static final int BOOK        = BAGS+7;
	static{
		assignItemRect(WATERSKIN,   16, 14);
		assignItemRect(BACKPACK,    16, 16);
		assignItemRect(POUCH,       14, 15);
		assignItemRect(HOLDER,      16, 16);
		assignItemRect(BANDOLIER,   15, 16);
		assignItemRect(HOLSTER,     15, 16);
		assignItemRect(VIAL,        12, 12);
		assignItemRect(BOOK,        13, 16);
	}

	private static final int DOCUMENTS  =                                   xy(1, 32);  //16 slots
	public static final int GUIDE_PAGE  = DOCUMENTS+0;
	public static final int ALCH_PAGE   = DOCUMENTS+1;
	public static final int SEWER_PAGE  = DOCUMENTS+2;
	public static final int PRISON_PAGE = DOCUMENTS+3;
	public static final int CAVES_PAGE  = DOCUMENTS+4;
	public static final int CITY_PAGE   = DOCUMENTS+5;
	public static final int HALLS_PAGE  = DOCUMENTS+6;
	static{
		assignItemRect(GUIDE_PAGE,  10, 11);
		assignItemRect(ALCH_PAGE,   10, 11);
		assignItemRect(SEWER_PAGE,  10, 11);
		assignItemRect(PRISON_PAGE, 10, 11);
		assignItemRect(CAVES_PAGE,  10, 11);
		assignItemRect(CITY_PAGE,   10, 11);
		assignItemRect(HALLS_PAGE,  10, 11);
	}

	private static final int C_SPELLS =                                    xy(1, 37);
	public static final int STARS           = C_SPELLS +0;
	public static final int HEAL            = C_SPELLS +1;
	public static final int ZAP             = C_SPELLS +2;
	public static final int SHIELD          = C_SPELLS +3;
	public static final int SHOCKER         = C_SPELLS +4;
	public static final int CAMOUFLAGE      = C_SPELLS +5;
	public static final int CLONE           = C_SPELLS +6;
	public static final int GONER           = C_SPELLS +7;

	private static final int C_KNIGHT = xy(1, 38);
	public static final int PUNCH           = C_KNIGHT +0;
	public static final int HEALKI          = C_KNIGHT +1;
	public static final int BOOM            = C_KNIGHT +2;
	public static final int SPREAD          = C_KNIGHT +3;
	public static final int FLOWER          = C_KNIGHT +4;

	private static final int C_SOUL_REAVER = xy(9, 38);
	public static final int SR_OFFENSE      = C_SOUL_REAVER +0;
	public static final int SR_DEFENSE      = C_SOUL_REAVER +1;
	public static final int SR_MAGICAL      = C_SOUL_REAVER +2;
	public static final int SR_RANGED       = C_SOUL_REAVER +3;
	public static final int SR_SUPPORT      = C_SOUL_REAVER +4;
	static {
		assignItemRect(STARS,      11, 11);
		assignItemRect(HEAL,       10, 10);
		assignItemRect(ZAP,        10, 7);
		assignItemRect(SHIELD,     10, 10);
		assignItemRect(SHOCKER,    13, 15);
		assignItemRect(CAMOUFLAGE, 14, 14);
		assignItemRect(CLONE,      14, 13);
		assignItemRect(GONER,      14, 16);

		assignItemRect(PUNCH,  13, 14);
		assignItemRect(HEALKI, 13, 14);
		assignItemRect(BOOM,   15, 14);
		assignItemRect(SPREAD, 12, 15);
		assignItemRect(FLOWER, 15, 15);

		assignItemRect(SR_OFFENSE, 12, 12);
		assignItemRect(SR_DEFENSE, 11, 11);
		assignItemRect(SR_MAGICAL, 13, 13);
		assignItemRect(SR_RANGED,  12, 12);
		assignItemRect(SR_SUPPORT, 10, 9);
	}

	public static final int BEHAVIOR_ICONS = xy(1, 33);
	public static final int BEHAVIOR_REACTIVE   = BEHAVIOR_ICONS+0;
	public static final int BEHAVIOR_DEFENSIVE  = BEHAVIOR_ICONS+1;
	public static final int BEHAVIOR_AGGRESSIVE = BEHAVIOR_ICONS+2;
	public static final int BEHAVIOR_PASSIVE    = BEHAVIOR_ICONS+3;

	static {
		assignItemRect(BEHAVIOR_REACTIVE,  14, 14);
		assignItemRect(BEHAVIOR_DEFENSIVE, 13, 14);
		assignItemRect(BEHAVIOR_AGGRESSIVE,13, 13);
		assignItemRect(BEHAVIOR_PASSIVE,   12, 12);
	}

	private static final int TREASURE_BAGS = xy(1, 39);
	public static final int BAG_SEEDS       = TREASURE_BAGS+0;
	public static final int BAG_POTIONS     = TREASURE_BAGS+1;
	public static final int BAG_STONES      = TREASURE_BAGS+2;
	public static final int BAG_SCROLLS     = TREASURE_BAGS+3;
	public static final int BAG_EQUIPMENT   = TREASURE_BAGS+4;
	public static final int BAG_HOLSTER     = TREASURE_BAGS+5;
	public static final int BAG_ACCESSORIES = TREASURE_BAGS+6;
	public static final int BAG_GENERIC     = TREASURE_BAGS+7;

	static {
		for (int i = TREASURE_BAGS; i < TREASURE_BAGS + 16; i++)
			assignItemRect(i, 14, 15);
	}

	//for smaller 8x8 icons that often accompany an item sprite
	public static class Icons {

		private static final int WIDTH = 16;
		public static final int SIZE = 8;

		public static TextureFilm film = new TextureFilm( Assets.Sprites.ITEM_ICONS, SIZE, SIZE );

		private static int xy(int x, int y){
			x -= 1; y -= 1;
			return x + WIDTH*y;
		}

		private static void assignIconRect( int item, int width, int height ){
			int x = (item % WIDTH) * SIZE;
			int y = (item / WIDTH) * SIZE;
			film.add( item, x, y, x+width, y+height);
		}

		private static final int RINGS          =                            xy(1, 1);  //16 slots
		public static final int RING_ACCURACY   = RINGS+0;
		public static final int RING_ARCANA     = RINGS+1;
		public static final int RING_ELEMENTS   = RINGS+2;
		public static final int RING_ENERGY     = RINGS+3;
		public static final int RING_EVASION    = RINGS+4;
		public static final int RING_FORCE      = RINGS+5;
		public static final int RING_FUROR      = RINGS+6;
		public static final int RING_HASTE      = RINGS+7;
		public static final int RING_MIGHT      = RINGS+8;
		public static final int RING_SHARPSHOOT = RINGS+9;
		public static final int RING_TENACITY   = RINGS+10;
		public static final int RING_WEALTH     = RINGS+11;
		static {
			assignIconRect( RING_ACCURACY,      7, 7 );
			assignIconRect( RING_ARCANA,        7, 7 );
			assignIconRect( RING_ELEMENTS,      7, 7 );
			assignIconRect( RING_ENERGY,        7, 5 );
			assignIconRect( RING_EVASION,       7, 7 );
			assignIconRect( RING_FORCE,         5, 6 );
			assignIconRect( RING_FUROR,         7, 6 );
			assignIconRect( RING_HASTE,         6, 6 );
			assignIconRect( RING_MIGHT,         7, 7 );
			assignIconRect( RING_SHARPSHOOT,    7, 7 );
			assignIconRect( RING_TENACITY,      6, 6 );
			assignIconRect( RING_WEALTH,        7, 6 );
		}

		private static final int BEHAVIORS =                                 xy(1, 2);

		public static final int BEHAVIOR_REACT  = BEHAVIORS+0;
		public static final int BEHAVIOR_DEFEND = BEHAVIORS+1;
		public static final int BEHAVIOR_ATTACK = BEHAVIORS+2;
		public static final int BEHAVIOR_PASSIVE= BEHAVIORS+3;

		static {
			for (int i = BEHAVIORS; i < BEHAVIORS+16; i++){
				assignIconRect(i, 5, 5);
			}
		}

		private static final int SCROLLS        =                            xy(1, 3);  //16 slots
		public static final int SCROLL_UPGRADE  = SCROLLS+0;
		public static final int SCROLL_IDENTIFY = SCROLLS+1;
		public static final int SCROLL_REMCURSE = SCROLLS+2;
		public static final int SCROLL_MIRRORIMG= SCROLLS+3;
		public static final int SCROLL_RECHARGE = SCROLLS+4;
		public static final int SCROLL_TELEPORT = SCROLLS+5;
		public static final int SCROLL_LULLABY  = SCROLLS+6;
		public static final int SCROLL_MAGICMAP = SCROLLS+7;
		public static final int SCROLL_RAGE     = SCROLLS+8;
		public static final int SCROLL_RETRIB   = SCROLLS+9;
		public static final int SCROLL_TERROR   = SCROLLS+10;
		public static final int SCROLL_TRANSMUTE= SCROLLS+11;
		static {
			assignIconRect( SCROLL_UPGRADE,     7, 7 );
			assignIconRect( SCROLL_IDENTIFY,    4, 7 );
			assignIconRect( SCROLL_REMCURSE,    7, 7 );
			assignIconRect( SCROLL_MIRRORIMG,   7, 5 );
			assignIconRect( SCROLL_RECHARGE,    7, 5 );
			assignIconRect( SCROLL_TELEPORT,    7, 7 );
			assignIconRect( SCROLL_LULLABY,     7, 6 );
			assignIconRect( SCROLL_MAGICMAP,    7, 7 );
			assignIconRect( SCROLL_RAGE,        6, 6 );
			assignIconRect( SCROLL_RETRIB,      5, 6 );
			assignIconRect( SCROLL_TERROR,      5, 7 );
			assignIconRect( SCROLL_TRANSMUTE,   7, 7 );
		}

		private static final int EXOTIC_SCROLLS =                            xy(1, 4);  //16 slots
		public static final int SCROLL_ENCHANT  = EXOTIC_SCROLLS+0;
		public static final int SCROLL_DIVINATE = EXOTIC_SCROLLS+1;
		public static final int SCROLL_ANTIMAGIC= EXOTIC_SCROLLS+2;
		public static final int SCROLL_PRISIMG  = EXOTIC_SCROLLS+3;
		public static final int SCROLL_MYSTENRG = EXOTIC_SCROLLS+4;
		public static final int SCROLL_PASSAGE  = EXOTIC_SCROLLS+5;
		public static final int SCROLL_SIREN    = EXOTIC_SCROLLS+6;
		public static final int SCROLL_FORESIGHT= EXOTIC_SCROLLS+7;
		public static final int SCROLL_CHALLENGE= EXOTIC_SCROLLS+8;
		public static final int SCROLL_PSIBLAST = EXOTIC_SCROLLS+9;
		public static final int SCROLL_DREAD    = EXOTIC_SCROLLS+10;
		public static final int SCROLL_METAMORPH= EXOTIC_SCROLLS+11;
		static {
			assignIconRect( SCROLL_ENCHANT,     7, 7 );
			assignIconRect( SCROLL_DIVINATE,    7, 6 );
			assignIconRect( SCROLL_ANTIMAGIC,   7, 7 );
			assignIconRect( SCROLL_PRISIMG,     5, 7 );
			assignIconRect( SCROLL_MYSTENRG,    7, 5 );
			assignIconRect( SCROLL_PASSAGE,     5, 7 );
			assignIconRect( SCROLL_SIREN,       7, 6 );
			assignIconRect( SCROLL_FORESIGHT,   7, 5 );
			assignIconRect( SCROLL_CHALLENGE,   7, 7 );
			assignIconRect( SCROLL_PSIBLAST,    5, 6 );
			assignIconRect( SCROLL_DREAD,       5, 7 );
			assignIconRect( SCROLL_METAMORPH,   7, 7 );
		}

		                                                                                //16 free slots

		private static final int POTIONS        =                            xy(1, 6);  //16 slots
		public static final int POTION_STRENGTH = POTIONS+0;
		public static final int POTION_HEALING  = POTIONS+1;
		public static final int POTION_MINDVIS  = POTIONS+2;
		public static final int POTION_FROST    = POTIONS+3;
		public static final int POTION_LIQFLAME = POTIONS+4;
		public static final int POTION_TOXICGAS = POTIONS+5;
		public static final int POTION_HASTE    = POTIONS+6;
		public static final int POTION_INVIS    = POTIONS+7;
		public static final int POTION_LEVITATE = POTIONS+8;
		public static final int POTION_PARAGAS  = POTIONS+9;
		public static final int POTION_PURITY   = POTIONS+10;
		public static final int POTION_EXP      = POTIONS+11;
		static {
			assignIconRect( POTION_STRENGTH,    7, 7 );
			assignIconRect( POTION_HEALING,     6, 7 );
			assignIconRect( POTION_MINDVIS,     7, 5 );
			assignIconRect( POTION_FROST,       7, 7 );
			assignIconRect( POTION_LIQFLAME,    5, 7 );
			assignIconRect( POTION_TOXICGAS,    7, 7 );
			assignIconRect( POTION_HASTE,       6, 6 );
			assignIconRect( POTION_INVIS,       5, 7 );
			assignIconRect( POTION_LEVITATE,    6, 7 );
			assignIconRect( POTION_PARAGAS,     7, 7 );
			assignIconRect( POTION_PURITY,      5, 7 );
			assignIconRect( POTION_EXP,         7, 7 );
		}

		private static final int EXOTIC_POTIONS =                            xy(1, 7);  //16 slots
		public static final int POTION_MASTERY  = EXOTIC_POTIONS+0;
		public static final int POTION_SHIELDING= EXOTIC_POTIONS+1;
		public static final int POTION_MAGISIGHT= EXOTIC_POTIONS+2;
		public static final int POTION_SNAPFREEZ= EXOTIC_POTIONS+3;
		public static final int POTION_DRGBREATH= EXOTIC_POTIONS+4;
		public static final int POTION_CORROGAS = EXOTIC_POTIONS+5;
		public static final int POTION_STAMINA  = EXOTIC_POTIONS+6;
		public static final int POTION_SHROUDFOG= EXOTIC_POTIONS+7;
		public static final int POTION_STRMCLOUD= EXOTIC_POTIONS+8;
		public static final int POTION_EARTHARMR= EXOTIC_POTIONS+9;
		public static final int POTION_CLEANSE  = EXOTIC_POTIONS+10;
		public static final int POTION_DIVINE   = EXOTIC_POTIONS+11;
		static {
			assignIconRect( POTION_MASTERY,     7, 7 );
			assignIconRect( POTION_SHIELDING,   6, 6 );
			assignIconRect( POTION_MAGISIGHT,   7, 5 );
			assignIconRect( POTION_SNAPFREEZ,   7, 7 );
			assignIconRect( POTION_DRGBREATH,   7, 7 );
			assignIconRect( POTION_CORROGAS,    7, 7 );
			assignIconRect( POTION_STAMINA,     6, 6 );
			assignIconRect( POTION_SHROUDFOG,   7, 7 );
			assignIconRect( POTION_STRMCLOUD,   7, 7 );
			assignIconRect( POTION_EARTHARMR,   6, 6 );
			assignIconRect( POTION_CLEANSE,     7, 7 );
			assignIconRect( POTION_DIVINE,      7, 7 );
		}

		                                                                                //16 free slots

	}

}
