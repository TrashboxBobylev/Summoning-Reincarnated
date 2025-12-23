/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Ropes;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScoutArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ConjurerBook;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.ChaoticBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Firebomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfMysticProwess;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.UpgradeClump;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.EnchantParchment;
import com.shatteredpixel.shatteredpixeldungeon.items.staffs.BlasterStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.staffs.FroggitStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.staffs.GnollHunterStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.staffs.GooStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.staffs.GrayRatStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.staffs.MagicMissileStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.staffs.RoboStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.staffs.SheepStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.staffs.WizardStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Slingshot;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Cleaver;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RunicBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.shop.StoneHammer;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AbyssalSpawnerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AttunementConstructSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DogSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MysteryMerchantSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.YogSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class vReInc_Changes {
    public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){
        add_v0_6_0_Changes(changeInfos);
        add_v0_5_0_Changes(changeInfos);
        add_v0_4_0_Changes(changeInfos);
        add_v0_3_0_Changes(changeInfos);
        add_v0_2_0_Changes(changeInfos);
        add_v0_1_0_Changes(changeInfos);
    }

    public static void add_v0_6_0_Changes( ArrayList<ChangeInfo> changeInfos ){
        ChangeInfo changes = new ChangeInfo("vReInc-0.6.2", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.get(Icons.BOBYLEV), "Developer Commentary",
                "_-_ Released December 24th, 2025\n" +
                        "_-_ 47 days after Reincarnated 0.6.1\n" +
                        "_-_ 123 days after Reincarnated 0.6.0"
        ));

        changes.addButton(new ChangeButton(Conducts.Conduct.COINFLIP.getIcon(), "New Conducts",
                "Added two new conducts:\n\n" +
                        "_-_ _Double or Nothing_ makes all hit checks depend on coin flip, either it hit or it doesn't. This includes wands and surprise attacks.\n" +
                        "_-_ _Chambers of Gang War_ makes all mobs spawn as horde leaders, creating followers for every foe."
        ));

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.TYPE_MANAGER), "Ranking -> item type",
                "Changed all mentions of Summoning-exclusive rank system with item type system, therefore rank manager is now type manager and item's ranks are now item's types.\n\n" +
                        "\"Ranking\" system was initial attempt to distance the gameplay system from word \"tier\", as it was used in Project ECLISE, due to it being already used in Pixel Dungeon as measure for item's power.\n" +
                        "However, it didn't really do its job, replacing the word with its synonym, therefore still meaning that some \"ranks\" are better than others. But I only realized the extend of this very recently and decided to change the word again.\n" +
                        "The new term, type, conveys the essence of system far better and still allows for shorthand of T1/T2/T3 to be used (just meaning type 1 instead of tier 1)."
        ));

        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Bosses are now immune to HP reduction effects, like one from Precise Strike talent.\n" +
                        "_-_ Summoning staffs can now be transmutated.\n" +
                        "_-_ Removed legacy item type info screen and replaced it with catalog's tabbed representation.\n" +
                        "_-_ Made minions benefit from Silent Steps talent."
        ));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "_Fixed the following bugs:_\n" +
                        "_-_ Corruption missing its deferred damage effect\n" +
                        "_-_ Crash on using Potion of Mastery on armor\n" +
                        "_-_ Item typing not being transferred on transmutations\n" +
                        "_-_ Enemy to ally conversions not actually giving Gauntlet Mode reward\n" +
                        "_-_ Prison cell room crashing on Project Paradox\n" +
                        "_-_ Arcane Resin scaling from hero's attunement instead of wand's"
        ));

        changes = new ChangeInfo("vReInc-0.6.1", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.get(Icons.BOBYLEV), "Developer Commentary",
                "_-_ Released November 7th, 2025\n" +
                        "_-_ 76 days after Reincarnated 0.6.0"
                ));

        changes.addButton( new ChangeButton(Icons.get(Icons.SHPX), "Shattered Ports",
                "Implemented Shattered v3.2.5 changes.\n\n" +
                        "_-_ Finally fixed the issue with fullscreen/navigation bar.\n" +
                        "_-_ Moved mode/conduct icon to be under player avatar to account for new UI."));

        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Added placeholder-ish Duelist ability to Adventurer's stabber.\n" +
                        "_-_ Refactored conduct choosing screen to be more stable.\n" +
                        "_-_ Reimplemented ability to choose multiple conducts through Balanced Conducts settings option.\n" +
                        "_-_ Changed Scroll of Debug to be a conduct, that is only accessible upon disabling Balanced Conducts, instead of being a settings option.\n" +
                        "_-_ Unified the rankings behavior of seeded runs and runs with \"cheaty\" modifiers, like Endless Potential conduct or Exploration mode.\n" +
                        "_-_ Bones no longer transfer items from conducted runs.\n" +
                        "_-_ Implemented experimental damage system. It consists of sources of damage being given properties, that are used to determine on-damage behavior. Currently it should not affect gameplay in any major ways, but will allow for more complex interactions in the future.\n" +
                        "_-_ Added the Abyss's mobs to Gauntlet Mode's post-game."
                        ));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "_Fixed the following bugs:_\n" +
                        "_-_ Dried Rose not being usable in Abyssal Crusade mode\n" +
                        "_-_ Holy Aura II's shielding going into infinity\n" +
                        "_-_ Gauntlet Mode's rewards not being given, if last mobs were corrupted or enthralled\n" +
                        "_-_ Conjurer's spells being possible to upgrade\n" +
                        "_-_ Grass tile textures not being properly affected by dungeon size\n" +
                        "_-_ Various cases of conducts being doubled or not being reset\n" +
                        "_-_ Rare non-fatal crash with fog of war's implementation and conducts\n" +
                        "_-_ Abyssal Crusade mode's Abyss shops being the same as the shop on setup floor\n" +
                        "_-_ Missing text for entering the Abyss on loading screen\n" +
                        "_-_ Missing tileset pieces in Abyssal Crusade mode's setup floor"
                ));

        changes = new ChangeInfo("vReInc-0.6.0", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.get(Icons.BOBYLEV), "Developer Commentary",
                "_-_ Released August 23th, 2025\n" +
                        "_-_ 39 days after Reincarnated 0.5.3\n\n" +
                        "Trying to bring those waiting times lower and lower...\n" +
                        "This release implements ranks for thrown weapons and wands and making them automatically stale with their respective stats."));

        changes.addButton( new ChangeButton(Icons.get(Icons.SHPX), "Shattered Ports",
                "Implemented Shattered v3.2.1 changes.\n\n" +
                        "_-_ Some of thrown weapon nerfs have been reverted or negated by Summoning's own rework.\n" +
                        "_-_ Added Summoning's effects to new floating text icon's acc/eva showcase.\n" +
                        "_-_ Added journal discovery hints for Summoning's content."));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "new"), false, null);
        changes.hardlight(CharSprite.POSITIVE);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.WAND_MAGIC_MISSILE), "Wand Rework",
                "_-_ Are no longer upgradeable. Instead they scale with player's attunement stat, +1 old system's level per 1 attunement. Offensive wand's damage has been slightly buffed to compensate for this.\n\n" +
                        "_-_ Now can be equipped as melee weapon with Mage class to get the same effects as Mage's staff. Mage's staff has been fully removed from the game, with old saves getting staff's wand after being loaded.\n\n" +
                        "_-_ Now use same ranking system as summoning staffs, essentially creating 3 variants of same item per each wand, including Battlemage's abilities. Some wand's ranks were ported straight from Legacy version, while other's have brand new effects, aiming at providing different experience from \"stronger wand, longer recharge\".\n\n" +
                        "_-_ Removed variable recharge scaling and max charges increase. Now all wands are fixed at 5 max charges and 40 base turns to recharge 1 charge. This can vary from wand's recharge modifier, which changes with ranks.\n\n" +
                        "_-_ Battlemage now gets additional melee damage from wands, in addition to previous effects like recharge and on-hit stuff.\n\n" +
                        "_-_ Replaced _Wand Preservation_ talent with _Fighting Wizardry_ talent, which increases effective power of next wand's zap with each melee hit.\n\n" +
                        "_-_ Arcane Resin is now always made in quantity of 2 and boosts attunement power of wand instead of its level."));

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.JAVELIN), "Thrown Weapon Rework",
                "_-_ Are no longer upgradeable. Instead they scale with player's strength stat, +1.5 old system's level per 2 strength.\n\n" +
                        "_-_ Are no longer divided by tiers and are all usable from 10 starting strength. The generation of thrown weapons has been changed to feature all of them in same pool regardless of progression.\n\n" +
                        "_-_ Changed the durability gain from levels to be a flat number.\n\n" +
                        "_-_ Now use same ranking system as summoning staffs, essentially creating 3 variants of same item per each thrown weapon.\n\n" +
                        "_-_ Some of thrown weapons have been visually redesigned to fit their new purpose as tierless weapons.\n\n" +
                        "_-_ Ring of Sharpshooting has been nerfed by 33% both for durability and throwing level boost.\n\n" +
                        "_-_ Replaced _Shared Upgrades_ talent with _Olympic Dedication_, which rewards Sniper for attacking with thrown weapons rapidly with special attack boost.\n\n" +
                        "_-_ Liquid Metal is now always made and consumed in quantity of 10."));

        changes.addButton(new ChangeButton(Icons.get(Icons.RANDOM_HERO), "New Game Modes and Conducts",
                "Added two new gamemodes:\n\n" +
                        "_-_ _Platinum Champion_ now replaces old challenge system, allowing to experience Shattered's 9 challenge runs in single package.\n\n" +
                        "_-_ _Procedurally Generated Protagonist_ shuffles around most of hero's aspects, like starting equipment, talents and what subclasses and armor abilities they will get.",
                        "Added three new conducts:\n\n" +
                                "_-_ _Shadow Curse_ reduces the vision range and causes hero's memory of the stage to deteriorate over time, making it covered in fog of war again. In addition to that, mobs can spawn from any distance and will spawn more frequently.\n\n" +
                                "_-_ _Oligarchic Paradise_ forces you to buy all items in dungeon, including ones dropped by enemies. Fortunately, hero can earn some gold by damaging enemies.\n\n" +
                                "_-_ _Decline from Fame_ starts with a strong level 24 hero, but makes them lose experience instead of gaining it. If hero goes below level 1, they will die.\n\n" +
                                "And one has been reworked:\n\n" +
                                "_-_ _Starving for Items_ now turns some of equipment into gold and consumables into energy, while providing a chance for gold to be reduced to 1."));

        changes.addButton(new ChangeButton(new ItemSprite(new ElixirOfMysticProwess()), "New/Reworked Items",
                "_-_ Added the _Elixir of Mystic Prowess_, an item that increases attunement power of selected power by 2. It is made from Elixir of Attunement.\n\n" +
                        "_-_ Tridents were reworked to be themed around water and being heavy, taking 2 turns to throw.\n\n" +
                        "_-_ Reworked Scroll of Passage into _Scroll of Discord_, which teleports all enemies away into void, but spawns new ones to replace them in distance.\n\n" +
                        "_-_ Scroll of Antimagic has been buffed to inflict anti-magic effect on enemies, which makes them unable to use their magical abilities."));

        changes.addButton(new ChangeButton(new MysteryMerchantSprite(), "New Dungeon Additions",
                "_-_ Reimplemented floor 20's shopkeeper sprite from Legacy version as brand new NPC, _mysterious merchant_! He can be met in every region behind secret door and offers the ability to either subtract a point from a talent to get it back, or transmute a talent into another talent. This costs 500 gold at the start plus 250 gold per each use.\n\n" +
                        "_-_ Reimplemented gnoll tribe's room, spectral shaman's room and bombs maze room. All of them were slightly polished or changed for the newer Shattered's design."));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(CharSprite.WARNING);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new YogSprite(), "Yog-Dzewa's fight",
                "_-_ Added more rippers and attunement constructs into Yog-Dzewa's summon list.\n" +
                        "_-_ Significantly reduced eradication walls spam during the final phase.\n" +
                        "_-_ The Yog-Dzewa's arena now shrinks in size with each fist summoned."));

        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Scaled down _Abyssal Crusade_'s initial level by reducing amount of treasure bags and rooms, but increasing amount of items they give.\n" +
                        "_-_ Summoning staffs now have random rank instead of 50% to get either rank II or III.\n" +
                        "_-_ Imported Abyss tileset improvements from RKA.\n" +
                        "_-_ Reworked missing text message to display exact missing codename for the text.\n" +
                        "_-_ Entrance and exit rooms are now also randomized in _Project Paradox_.\n" +
                        "_-_ Reduced the efficiency of ally damage tag on low damage allies.\n" +
                        "_-_ Removed the unidentified ranked items always being shown with rank I color.\n" +
                        "_-_ Added ranks descriptions for Spirit Bow.\n" +
                        "_-_ Added sell prices for rank manager, Tengu's mask and Dwarf King's crown.\n" +
                        "_-_ Elemental Blast now works with any wand in inventory, not just equipped one.\n" +
                        "_-_ Ranked items are now affected by degrade debuff."));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed the following bugs:\n" +
                        "_-_ Shop weapons being one tier off from their placement\n" +
                        "_-_ Good Night conduct making allies go to sleep instead of returning to hero\n" +
                        "_-_ Corrupting or enthralling enemies not triggering Gauntlet Mode's floor completion money drops\n" +
                        "_-_ Certain enemies spawning super-weakened in Abyss\n" +
                        "_-_ Traps room crashing when going too far into Abyss\n" +
                        "_-_ Incorrect formatting on pike's Duelist description\n" +
                        "_-_ Conjurer not being able to obtain armor ability in normal modes\n" +
                        "_-_ Saves being impossible to load on Android 12 or below devices, if loading the save with Scroll of Debug active\n" +
                        "_-_ Fast Adventure's ascension deleting the imp shopkeeper\n" +
                        "_-_ Interlevel scene cutscenes not using variable dungeon sizes\n" +
                        "_-_ Gauntlet Mode's shop giving Tengu's mask to Adventurer\n" +
                        "_-_ Sad Ghost ally displaying missing text, while in Abyss\n" +
                        "_-_ Sad Ghost ally's lines not adhering to dungeon size\n" +
                        "_-_ Occasional ghost items in shops, while playing as Adventurer\n" +
                        "_-_ Rare crashes from bee's enemy choosing AI"));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "buffs"), false, null);
        changes.hardlight(CharSprite.POSITIVE);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.get(Icons.TALENT), "Talent Changes",
                "_-_ Added metamorphosis effect for _Attuned Meal_ talent.\n" +
                        "_-_ _Manaburn_ talent no longer triggers above 50% mana.\n" +
                        "_-_ Star Blazing spell can no longer damages the hero even with _Concentrated Support_ talent active."));
    }

    public static void add_v0_5_0_Changes( ArrayList<ChangeInfo> changeInfos ){
        ChangeInfo changes = new ChangeInfo("vReInc-0.5.2", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.get(Icons.BOBYLEV), "Developer Commentary",
                "_-_ Released June 4th, 2025\n" +
                        "_-_ 10 days after Reincarnated 0.5.0\n\n" +
                        "This entire update just showcases how rotten is Legacy Summoning's code, design and objectives were. All of them were demolished just from few days of constant suggestions and reports of one person. This is what feedback does. This is what I deserved after all those years. And I do not happen to be proud of how inadequate the game felt to some of you who haven't spoken to me about all of this."));

        changes = new ChangeInfo("v0.5.3", false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed the following bugs:\n" +
                        "_-_ Being impossible to descend past depth 26\n" +
                        "_-_ Attunement constructs not existing in Abyss\n" +
                        "_-_ Demon halls not having any visibility in Large Enlargement"));

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.BOOK), "Conjurer spells",
                "_Star Blazing:_\n" +
                        "_-_ Increased mana cost from _1/3/8_ to _1/4/10_.\n" +
                        "_-_ No longer changes damage with ranks.\n" +
                        "_-_ Now triggers after-spell effects for every star.\n\n" +
                        "_Beam of Affection:_\n" +
                        "_-_ Decreased mana cost from _0/6/9_ to _0/5/7_.\n" +
                        "_-_ Can no longer be self-targeted.\n" +
                        "_-_ Can affect all kinds of allies, not just minions.\n" +
                        "_-_ Rank II minion effect reworked: now refunds half of minion's HP as staff charge.\n\n" +
                        "_Runic Shell:_\n" +
                        "_-_ No longer benefits hero, if shot in specific way.\n" +
                        "_-_ Changed the wording of ranks to not mention healing.\n\n" +
                        "_Pushing Waveform:_\n" +
                        "_-_ Push force now increases with ranks.\n" +
                        "_-_ Reduced the spell length from _8/11/14_ to _7/9/11_ tiles.\n" +
                        "_-_ Increased the spell's cone width from _90/120/150_ to _90/180/360_ degrees.",
                "_Shocker Breaker:_\n" +
                        "_-_ No longer benefits hero, if shot in specific way.\n" +
                        "_-_ Shock damage now pierces invulnerability to prevent exploits.\n\n" +
                        "_Dreemurr's Necromancy:_\n" +
                        "_-_ Decreased mana cost from _12/24/0_ to _8/16/0_.\n" +
                        "_-_ No longer benefits hero, if shot in specific way.\n" +
                        "_-_ Decreased all necromancy effect's mana drain by 50%.\n\n" +
                        "_Sub-Null Field Igniter:_\n" +
                        "_-_ No longer affects minions.",
                "_Antarctic Touch:_\n" +
                        "_-_ Decreased mana cost from _10/20/20_ to _5/10/20_.\n" +
                        "_-_ Now affects environment in same way as Wand of Frost.\n" +
                        "_-_ Increased rank III's frostburn length from _25_ to _1000 turns_.\n\n" +
                        "_Tommie's Armor Spell:_\n" +
                        "_-_ Can affect all kinds of allies, not just minions.\n\n" +
                        "_Transmogrification Wand:_\n" +
                        "_-_ Decreased mana cost from _8/12/15_ to _5/8/11_.\n" +
                        "_-_ No longer benefits hero, if shot in specific way.\n\n" +
                        "_Artemis Bridge:_\n" +
                        "_-_ Can affect all kinds of allies, not just minions.\n" +
                        "_-_ Significantly improved the use validation logic.\n\n" +
                        "_Holy Aura:_\n" +
                        "_-_ Can no longer be extended by continuous usage.\n" +
                        "_-_ Now properly heals allies.\n" +
                        "_-_ Increased the rate of shielding by 1 turn on all ranks.\n\n" +
                        "_Shards of Despair:_\n" +
                        "_-_ Now triggers after-spell effects for every enemy hit."
                        ));

        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.CONJURER, 0), "Other Conjurer changes",
                    "_-_ Underground Paradise now travels to next floor no matter the distance.\n\n" +
                            "_-_ Severely nerfed Soul Wielder's mana boost: now only increases by 1 instead of being multiplied by 1.5x.\n\n" +
                            "_-_ Soul Sparking's damage boost now maxes out at 10% HP.\n\n" +
                            "_-_ Toy Knife now benefits from Projecting, when thrown.\n\n" +
                            "_-_ _Energy Break_'s damage boost can now spread across multiple targets and benefit _Soul's Burst_'s effect.\n\n" +
                            "_-_ One-hit kills with Toy Knife now collect mana.\n\n" +
                            "_-_ Nerfed _Soul's Burst_'s damage multiplier from _150%/200%_ (was supposed to be 100%/150%) to _75%/125%_."
        ));

        changes.addButton( new ChangeButton(Icons.get(Icons.GAUNTLET), "Gauntlet Mode",
                "_-_ Increased gold payout from _3x-4x_ to _7x-10x_.\n\n" +
                        "_-_ The shop no longer spawns identification consumables.\n\n" +
                        "_-_ Fixed Tengu's Mask not appearing in the shop.\n\n" +
                        "_-_ Added music list into gauntlet levels.\n\n" +
                        "_-_ Added rings and rank managers into the shop.\n\n" +
                        "_-_ Reduced the chance of extra, class-focused equipment from _50%_ to _25%_."));

        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Removed custom minion following code to hopefully make them more obedient.\n" +
                        "_-_ Scrolls of Retribution and Psionic Blast now deal 60% less damage to allies.\n" +
                        "_-_ Made targeting visual less intense on game's performance.\n" +
                        "_-_ Made most of secret and special rooms adhere to chapter sizes.\n" +
                        "_-_ Rooms with trap puzzles now use a smorgasbord of traps in Abyss or Project Paradox mode.\n" +
                        "_-_ Adventurer now directly gets tier 3 talents from Tengu instead of having to use his mask.\n" +
                        "_-_ Added a new effect to Storm Clouds area effect: pushing entities around.\n" +
                        "_-_ Added Abyss enemies into the journal's bestiary.\n" +
                        "_-_ Made frostburn effect not reset duration with reignitions.\n" +
                        "_-_ Extended reach weapons can now reach beyond allies.\n" +
                        "_-_ Made Bless effect use rerolls for accuracy and evasion checks, making it slightly more effective.",
                        "_-_ Rebalanced Dwarf Wizard II:\n" +
                        "   _*_ reduced attack speed from _2 attacks/turn_ to _1.33 attacks per turn_\n" +
                        "   _*_ reduced recharge time from _850_ to _800_ turns\n" +
                        "_-_ Fast Adventure no longer reduces amount of special rooms.\n" +
                        "_-_ Project Paradox no longer reduces amount of special rooms and can double them.\n" +
                        "_-_ Changed enemy target querying to be more thread safe.\n" +
                        "_-_ Made Fire Elementals vulnerable to frostburn.\n" +
                        "_-_ Hero being attacked now aggravates protective minions even if attack was dodged."));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed the following bugs:\n" +
                        "_-_ Attunement and laboratory rooms sometimes not appearing in the run on non-standard chapter sizes\n" +
                        "_-_ Conjurer and Adventurer using warrior seal as icon in hero info window\n" +
                        "_-_ Power of Triad's fighter crashing the game in journal\n" +
                        "_-_ Shops appearing on first level of demon halls\n" +
                        "_-_ Goo minion being effectively invincible\n" +
                        "_-_ Combined Refill talent not actually working\n" +
                        "_-_ Frost elemental minions never using ranged attacks\n" +
                        "_-_ Gnoll brutes not giving any mana"));

        changes = new ChangeInfo("vReInc-0.5.0", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.get(Icons.BOBYLEV), "Developer Commentary",
                "_-_ Released May 25th, 2025\n" +
                        "_-_ 170 days after Reincarnated 0.4.1\n\n" +
                        "At least it is not longer wait, than the previous update, right?\n" +
                        "This release mainly reintroduces game modes and fully completes Conjurer, giving armor abilities and remains item."));

        changes.addButton( new ChangeButton(Icons.get(Icons.SHPX), "Shattered Ports",
                "Implemented Shattered v3.0.2 changes.\n\n" +
                        "_-_ Cleric is dead. Don't ask why, Conjurer is happy enough of a class, right?"));

        changes = new ChangeInfo("v0.5.1", false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed the following bugs:\n" +
                        "_-_ Sheep minions softlocking the game by their attack animation"));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "new"), false, null);
        changes.hardlight(CharSprite.POSITIVE);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.CONJURER, 3), HeroClass.CONJURER.title(),
                "Implemented armor abilities for Conjurer!\n\n" +
                        "_-_ _Ascension_ allows Conjurer to transcend, turning his mana power into large amount of shielding, with possibility of empowering spells and minions with it.\n\n" +
                        "_-_ _Power of Triad_ gives Conjurer ability to call for the trio of powerful allies, each having different abilities, way to summon and niche.\n\n" +
                        "_-_ _Hyperblast_ is armor ability from legacy version, that stuns enemies with irresistible Soul Paralysis.",
                "_-_ Added a remains item: _Spell Page_! This remains summons simple ally Attunement Construct on usage, that scales its power with depth.\n\n" +
                        "_-_ Added a brand new robe appearance, when armor ability is available."));

        changes.addButton(new ChangeButton(Icons.get(Icons.STAIRS), "Game Modes",
                "Reimplemented game modes, the sets of rules that you pick every run!\n\n" +
                        "This build has 7 game modes (the future of the rest remains uncertain):\n" +
                        "_-_ _Fast Adventure_ and _Large Enlargement_ have been tweaked to be more consistent and less invasive on the codebase, but the gameplay should be the same.\n\n" +
                        "_-_ _Exploration_ has been ported without changes.\n\n" +
                        "_-_ _Project Paradox_ have been tweaked to be less \"swingy\" in its difficulty and affects more stuff, like special rooms.\n\n" +
                        "_-_ _Gauntlet Mode_ have been modernized, with new infrequent special and alchemy rooms, new economy and more integrated locked floor mechanic.\n\n" +
                        "_-_ Added a brand new _Abyssal Crusade_ mode, where you start in almost-Abyss! Your character is set to max power and is given special loot bags in shop and across the level to full prepare for the invasion into depths of Abyss."));

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.CROW_STAFF), "New summon weapons",
                "Added 2 new summoning staffs:\n\n" +
                        "_-_ _Ravenous Branch_ is a tier 3 weapon, that summons a dark crow. The crows are adept at support, making other allies deal more damage, scouting or corrupting enemies depending on a rank.\n\n" +
                        "_-_ _Frost Elemental Staff_ is a returning tier 4 weapon, that summons a frost elemental. They received a new look and new tiering behavior.",
                "Added 2 new support weapons, that have extra reach and inflict special ally damage tag onto enemies.\n\n" +
                        "_Tribal Cane_ adds flat bonus damage, while _Ritual Gong_ adds a multiplier. Their effects can stack together for even more power!\n\n" +
                        "Their Duelist abilities allows to give reverse tag to allies, making them take less damage instead of dealing more damage."));

        changes.addButton(new ChangeButton(Icons.get(Icons.CONDUCTS_COLOR), "New conduct effects",
                "Several conducts had their effects reinvented:\n\n" +
                        "_-_ _Curse of the Muggle_ now replaces wand zaps with confetti blasts and minions with chickens instead of disabling them altogether.\n\n" +
                        "_-_ _Anti-Protein Campaign_ have returned with a new effect: it now makes all strength pots essentially temporary instead of removing them.\n\n" +
                        "_-_ _Become Ethereal_ now has a fail-safe in form of ectoplasmic charge, that accumulates on taking damage. Too much charge will kill you for real and dissipating takes some time."));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(CharSprite.WARNING);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.DISPLAY.get(), "Visual & Interface Changes",
                "_-_ Mana indicator now shows Conjurer's spell book. Spell book has been removed from quickslots due to redundancy.\n\n" +
                        "_-_ Conjurer's spells can now be made favorite to be used from action indicator button.\n\n" +
                        "_-_ Resprited title banner to fit with Shattered's new title art.\n\n" +
                        "_-_ Added targeting visual: mobs will trace a white line and show a target visual towards their target, with target cell coloring depending on their alignment.\n\n" +
                        "_-_ Long-clicking on behavior icon in behavior select window will show its description.\n\n" +
                        "_-_ Resprited \"damage from action\" icon for Conjurer's and Adventurer's talents to fit Shattered.\n\n" +
                        "_-_ Minions and Underground Paradise now switch to idling animation if not moving for past turn, imitating hero's behavior.\n\n" +
                        "_-_ Made all equipment with range of numbers show the average number too."));

        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Reimplemented Yog-Dzewa's eradication wall attack, with new visual and more frequent chance of occurring.\n" +
                        "_-_ Made targeting priority system work properly, so minions and other allies are actually prioritized by enemy attacks over player.\n" +
                        "_-_ Tweaked healing gas to heal for flat instead of depending on target's health, reduced the amount to compensate.\n" +
                        "_-_ Minions now retreat to player 2x faster, like other intelligent allies.\n" +
                        "_-_ Certain healing effects no longer work, if player is starving (inspired by Polished PD).\n" +
                        "_-_ Added retribution weapon ability to Cleaver.\n" +
                        "_-_ Levitation buff now gives better odds at dodging.\n" +
                        "_-_ Throwing items onto mobs now counts as trigger for reactive minions.\n" +
                        "_-_ Protective minions are now less prioritized than players to allow them to take the hit.\n" +
                        "_-_ Dungeon of Doom talent no longer upgrades generating thrown weapons.\n" +
                        "_-_ Reduced Arcane Nuke's fuse time from 8 to 4 turns."));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed the following bugs:\n" +
                        "_-_ Recursion overflow in Goo minion III's behavior\n" +
                        "_-_ Wizard's corruption not working\n" +
                        "_-_ Ascension being impossible to initiate\n" +
                        "_-_ Conjurer Robe II not properly nullifying Soul Sparking\n" +
                        "_-_ Toy Knife not setting itself as placeholder properly\n" +
                        "_-_ Toy Knife III's having wrong attack rate description\n" +
                        "_-_ Toy Knife dealing no damage from range\n" +
                        "_-_ Minions actually getting slower with hero becoming faster\n" +
                        "_-_ Self-casted Conjurer spells not properly showing tier info window\n" +
                        "_-_ Sub-Null Field Igniter having broken tier info\n" +
                        "_-_ Minions not benefitting from movement speed boosts\n" +
                        "_-_ Motion Bloom having no description for rank II\n" +
                        "_-_ Crash from Erupting Darkness attempting to use random wand effect",
                "_-_ Enemies not falling into Arcane Nuke's crater\n" +
                        "_-_ Energy Scroll talent giving energy value from entire stack of scrolls\n" +
                        "_-_ Runic Cluster's merge behavior failing to deliver Scroll of Upgrade, if inventory is full\n" +
                        "_-_ Race conditions from wizard III's attunement boost effect\n" +
                        "_-_ Traps and mobs count being disproportionally low in Abyss\n" +
                        "_-_ Gold, shop and equipment generation not considering Abyss's scaling depth\n" +
                        "_-_ Satiation and food poisoning effects displaying 0 in their descriptions\n" +
                        "_-_ Abyssal Dragon's spawns being effectively immortal\n" +
                        "_-_ !!!NO TEXT FOUND!!! message showing up, if checking unidentified too heavy staffs\n" +
                        "_-_ Gray Rat II healing to full with every attack\n" +
                        "_-_ Summon staffs being not transmutable"));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "buffs"), false, null);
        changes.hardlight(CharSprite.POSITIVE);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.BOOK), "Conjurer",
                "_-_ Changed (mostly reduced) mana cost of some spells:\n" +
                        "   _*_ Antarctic Touch: 15/25/25 -> 10/20/20\n" +
                        "   _*_ Tommie's Armor Spell: 20/24/4 -> 15/20/4\n" +
                        "   _*_ Transmogrification Wand: 10/15/18 -> 8/12/15\n" +
                        "   _*_ Artemis Bridge: 3/6/12 -> 3/5/10\n" +
                        "   _*_ Holy Aura: 35/50/20 to 25/40/20\n" +
                        "   _*_ Energized Blast: 25/15/8 -> 30/18/10\n" +
                        "   _*_ Shards of Despair: 10/20/45 -> 8/15/35\n\n" +
                        "_-_ Increased _Energized Blast_ III's damage from _20%_ to _25%_.\n\n" +
                        "_-_ Increased _Shards of Despair_'s damage by _25%_.\n\n" +
                        "_-_ Reduced _Concentration_'s paralysis from _3/8/20_ to _3/6/15_.",
                "_-_ Buffed _Eternal Friendship_ talent from _1/2/3_ armor to _1/3/5_ armor boost.\n\n" +
                        "_-_ Buffed _Froggit III_'s mana steal counter from _4 hits_ to _3 hits_.\n\n" +
                        "_-_ reworked _Benevolent Meal_ talent into _Attuned Meal_:\n" +
                        "   _*_ now grants extra mana on next 3/5 kills\n\n" +
                        "_-_ _Conjurer's Robe_'s armor value now scales with attunement."));

        changes.addButton(new ChangeButton(new WandOfCorruption(),
                "_-_ Reduced corruption resistance from _1x-5x_ to _1x-4x_ (0%-100%).\n\n" +
                        "_-_ Increased Corrupting enchantment's chance from _(5+level)/(25+level)_ to _(8+level)/(25+level)_.\n\n" +
                        "_-_ Corrupted enemies no longer awaken enemies with Swarm Intelligence challenge and _take all damage as deferred_, allowing them to survive for far longer."));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "nerfs"), false, null);
        changes.hardlight(CharSprite.NEGATIVE);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.BOOK), "Conjurer",
                "_-_ Reduced minimal damage of _Star Blazing_ from _4(+0.33/+0.5)_ to _2(+0.25/0.33)_.\n" +
                        "_-_ Reduced _Energized Renewal I_'s healing from _10_ to _8_.\n" +
                        "_-_ Reduced _Conjurer's robe II_'s damage reduction boost from _40%_ to _25%_.\n" +
                        "_-_ Rebalanced Soul Sparking effect:\n" +
                        "   _*_ the increase is now more gradual, with no boost at 50% HP and full boost at 0% HP instead of half of boost at 50% HP and full boost at 0% HP\n" +
                        "   _*_ changed the max boost from _150%/0%/450%_ to _75%/0%/300%_"));

        changes.addButton(new ChangeButton(new DogSprite(), "Dog retiering",
                "_-_ Now replaces crabs instead of snakes.\n" +
                        "_-_ Reduced HP from 10 to 9.\n" +
                        "_-_ Increased EXP values to that of a crab.\n" +
                        "_-_ Increased evasion from 8 to 12.\n" +
                        "_-_ Decreased attack from 2-5 to 1-4, but now attacks twice per turn.\n" +
                        "_-_ Reduced accuracy from 12 to 10."));
    }

    public static void add_v0_4_0_Changes( ArrayList<ChangeInfo> changeInfos ){
        ChangeInfo changes = new ChangeInfo("vReInc-0.4.0", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.get(Icons.BOBYLEV), "Developer Commentary",
                "_-_ Released December 4th, 2024\n" +
                        "_-_ 217 days after Reincarnated 0.3.2\n\n" +
                        "Each update just keeps one-upping another in terms of length and wait..." +
                        "This monumental update reimplements Conjurer and Abyss, adds Conjurer talents and reintroduces a lot of original Summoning's first release, like slingshot and new mobs!\n\n" +
                        "This release was actually supposed to be done on October 31st, the 5th anniversary to original Summoning's release, but PC upgrading troubles, overall motivation blocks and getting re-addicted to modded Minecraft made it hard to do it on deadline.\n" +
                        "Still, this release should actually make Reincarnated feel like Summoning we know and love... for its first few years of existence, that is. More global stuff, like game modes, wand and armor ranks, rogue's talents, abilities, still remain unported."));

        changes.addButton( new ChangeButton(Icons.get(Icons.SHPX), "Shattered Ports",
                "Implemented Shattered v2.5.4 changes."));

        changes = new ChangeInfo("v0.4.1", false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Reworked minion behavior selection to use better UI and to be instantaneous.\n" +
                        "_-_ Added Conjurer text for shopkeeper and blacksmith.\n" +
                        "_-_ Tweaked clarity of minion behavior icon.\n" +
                        "_-_ Added distinct particles for each type of behavior switch.\n" +
                        "_-_ Underground Paradise now moves at the same speed as his master."));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed the following bugs:\n" +
                        "_-_ Stairs not spawning in entrance rooms\n" +
                        "_-_ Conjurer being able to equip other armor\n" +
                        "_-_ Toy Knife rank III attacking 2x faster instead of 2x slower\n" +
                        "_-_ Froggit rank III moving 4x slower instead of 2x faster\n" +
                        "_-_ Victory showcase mentioning Shattered PD"));

        changes = new ChangeInfo("Conjurer", false, null);
        changes.hardlight(0x4c51ad);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.CONJURER, 0), HeroClass.CONJURER.title(),
                "The Goat is back, and more invested than ever.\n\n" +
                        "_-_ Now has a full set of tier 1, tier 2 and tier 3 talents (no armor abilities or t4...)!\n" +
                        "_-_ His appearance changes when choosing a subclass.\n" +
                        "_-_ The mana system has been \"deflated\", now depending on current attunement instead of mob's EXP value. This reduces mana outputs by 2x-3x depending on stage of the game.\n" +
                        "_-_ All spell costs have been adjusted for mana deflation.\n" +
                        "_-_ Added Shattered 2.4's text icons for gaining and spending mana.\n" +
                        "_-_ Added a tag-based mana indicator instead of plain white bar.\n" +
                        "_-_ Removed food healing perk.\n" +
                        "_-_ Mana can now be gained by any ally, as long as their target has soul gain effect."));

        changes.addButton(new ChangeButton(new ItemSprite(new ConjurerBook()), "Spell Changes",
                "_-_ Several spells have been renamed to more accurately reflect their purposes and powers.\n\n" +
                        "_-_ _Energized Renewal_'s ranking is completely reworked, and the spell no longer can be used to heal Conjurer himself.\n\n" +
                        "_-_ _Star Blazing_ no longer hits allies, ignores obstacles and has its damage increased on rank II and rank III. Rank III now covers the entire room worth of enemies.\n\n" +
                        "_-_ _Beam of Affection_ rank III can be used to enrage minion, making them deal more damage, but also take more damage.\n\n" +
                        "_-_ _Runic Shell_ is significantly cheaper and gives more shielding and longer Temporary Block.\n\n" +
                        "_-_ _Shocker Breaker_ rank III is more powerful and rank II now almost kills minion, but makes them invulnerable for short time and practically unhealable.\n\n" +
                        "_-_ _Pushing Waveform_ now has slightly higher push force.\n\n" +
                        "_-_ _Dreemur's Necromancy_ is completely reworked: now makes characters be able to live on 0 HP, but consume mana as if it was their health; works on both enemies and allies."));

        changes.addButton(new ChangeButton(new ItemSprite(new FroggitStaff()), "Equipment Changes",
                "_-_ Toy Knife is now a ranked weapon and scales with attunement. Its ranged soul gain is increased from 7 to 8 turns.\n\n" +
                        "_-_ Froggit has received completely new ranks: rank II rapidly attacks with low damage, while rank III sucks up mana out of enemies it targets."));

        changes.addButton(new ChangeButton(new TalentIcon(Talent.SPIRITUAL_BARRIER), "Base Talents",
                "_Tier 1:_\n\n" +
                        "_-_ _Benevolent Meal_ makes Conjurer heal his allies on eating food.\n\n" +
                        "_-_ _Empowering Intuition_ rewards Conjurer with mana on identifying items.\n\n" +
                        "_-_ _Energy Break_ gives Toy Knife extra damage, when used after a spell.\n\n" +
                        "_-_ _Spiritual Barrier_ makes Conjurer generate shielding for his allies, when his soul is sparking.",

                        "_Tier 2:_\n\n" +
                        "_-_ _Inspiring Meal_ empowers Conjurer's allies, when he is eating.\n\n" +
                        "_-_ _Liquid Casting_ creates short-living mana stealing effect on using potions.\n\n" +
                        "_-_ _Energized Support_ rewards active spell usage by speeding up recharge of inactive summon staffs.\n\n" +
                        "_-_ _Manaburn_ gives Conjurer great defensive ability of hurting attackers at cost of mana.\n\n" +
                        "_-_ _Soul's Burst_ increases Toy Knife's ranged capabilities by giving it 3x3 spirit explosions.",

                        "_Tier 3:_\n\n" +
                        "_-_ _Newborn Motivation_ boosts newly created summons with Empowered effect.\n\n" +
                        "_-_ _Combined Refill_ rewards diverse spellcasting by refunding some of mana cast of two spells, that are used consequently."));

        changes.addButton(new ChangeButton(new HeroIcon(HeroSubClass.SOUL_WIELDER), HeroSubClass.SOUL_WIELDER.title(),
                "_-_ Increased physical weakness from 25% to 33%.\n\n" +
                        "_-_ Reduced _Antarctic Touch_'s frostburn effect from 7/20/40 to 7/15/25 turns.",

                        "_Talents:_\n\n" +
                        "_-_ _Leader Appreciation_ greatly buffs mirror images with shielding and allows to spawn them with manually searching at cost of mana.\n\n" +
                        "_-_ _Concentrated Support_ allows to boost minion's attunement power with casting spells on them.\n\n" +
                        "_-_ _Eternal Friendship_ rewards diverse ally army by giving them extra armor, the strength of which depends on unique ally types."));

        changes.addButton(new ChangeButton(new HeroIcon(HeroSubClass.WILL_SORCERER), HeroSubClass.WILL_SORCERER.title(),
                "_-_ Was previously named Knight.\n\n" +
                        "_-_ Changed _Directing Pulse_'s soul gain buff from 9/3/1.1 to 12/6/2 turns.\n\n" +
                        "_-_ _Shards of Despair_'s rank II and rank III damage have been significantly increased, stun duration is increased from 1 to 6 turns.\n\n" +
                        "_-_ _Motion Bloom_ is now significantly cheaper, but has longer cooldown; rank II applies Haste instead of Time Freeze.\n\n" +
                        "_-_ Moved stand healing into its own talent.",

                        "_Talents:_\n\n" +
                                "_-_ _Violent Overcoming_ makes the stand empowered after hitting the enemy enough times, increasing their damage per turn.\n\n" +
                                "_-_ _Rejuvenating Force_ heals and restores Will Sorcerer's mana every 5 successful hits.\n\n" +
                                "_-_ _Spiritual Restock_ allows to target the stand at allies, making them buffed with Adrenaline and Cleanse effects."));

        changes = new ChangeInfo("Abyss", false, null);
        changes.hardlight(0xa8a8a8);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new Image(Assets.Environment.TILES_ABYSS, 0, 48, 16, 16 ), "Highlights",
                "_-_ Has been ported to new branch standard, replacing the older format with going beyond depth 26.\n\n" +
                "_-_ Added \"placeholder\" music, consisting of 5 ominous edits of existing tracks.\n\n" +
                "_-_ Added a chaotic, distorted splash background.\n\n" +
                "_-_ Runs, that got into Abyss, now have white tint over the depth icon in rankings and save select..\n\n" +
                "_-_ Certain score categories are now more rewarding in Abyss.\n\n" +
                "_-_ Abyss' difficulty increase doesn't happen yet due to no Increased Difficulty mode.\n\n" +
                "_-_ Room randomization now includes entrance and exit rooms.\n\n" +
                "_-_ Exit tile is now highlighted."));

        changes.addButton(new ChangeButton(new ItemSprite(new UpgradeClump()), "Progression",
                "_-_ Scrolls of Upgrade and Potions of Strength no longer _spawn naturally_ in Abyss.\n\n" +
                "_-_ Instead, special items are dropped by abyssal spawners, _Runic and Reddish Clusters_ and that can be converted into progression items.\n\n" +
                "_-_ 2 Runic Clusters turn into Scroll of Upgrade, while 3 Reddish Clusters can be brewed into Potion of Strength.\n\n" +
                "_-_ This effectively decreases amount of strength to 1.67 and amount of upgrades to 2.5 on each set of 5 floors."));

        changes.addButton(new ChangeButton(new AbyssalSpawnerSprite(), "Enemies",
                "_Abyssal Spawners:_\n\n" +
                "_-_ Updated sprites.\n" +
                "_-_ Reduced HP from 420 to 150, but restored soft damage cap, now equal to 25% of spawner's HP.\n" +
                "_-_ Now heals 0.67% of its max HP every turn.\n" +
                "_-_ Increased spawn cooldown from _23 at depth 1 to 4 at depth 19_ to _25 at depth 1 to 3 at abyss depth 44_.",

                "_Abyssal Dragons:_\n\n" +
                "_-_ Increased HP from 300 to 400 and EXP from 20 to 50.\n" +
                "_-_ Decreased evasion from 45 to 30.\n" +
                "_-_ Increased attack delay to 2 turns and decreased movement speed from 2 to 1.5 tiles per turn.\n" +
                "_-_ Increased physical damage from 46-90 to 60-115.\n" +
                "_-_ Increased armor roll from 20-35 to 25-40.\n" +
                "_-_ Increased ranged attack cooldown from 3-5 to 9-16 turns, but it now inflicts 12 turns of frostburn instead of 8 and uses 1 turn instead of 2.\n\n" +
                "_Dragon Spawns:_\n\n" +
                "_-_ Reduced HP from 85 to 70.\n" +
                "_-_ Increased evasion from 60 to 70 and accuracy from 55 to 65.\n" +
                "_-_ Decreased physical damage from 25-48 to 16-30.\n" +
                "_-_ Decreased attack delay to 0.5 turns.\n" +
                "_-_ Decreased armor roll from 19-28 to 12-20.",

                "_Abyssal Nightmares:_\n\n" +
                "_-_ Reduced HP from 620 to 320 and passive healing from 7 to 2-3 and increased EXP from 20 to 50.\n" +
                "_-_ Now reduces magical damage/effects by 75% instead of being completely immune to any magic.\n" +
                "_-_ Now has scaling damage reduction, with 67% at 100% HP to -200% at 0% HP.\n" +
                "_-_ Clones spawn with 25% chance instead of 20%, but no longer split further and have 80% HP of their host.\n\n" +
                "_Lost Spirits:_\n\n" +
                "_-_ Reduced health from 145 to 70 and increased EXP from 20 to 50.\n" +
                "_-_ Increased threshold of summon teleport from 10% HP to 20% HP.\n" +
                "_-_ Now gets shielding equal to 75% max HP of target from successful championing.\n\n" +
                "_Miscellaneous:_\n\n" +
                "_-_ Possessed Rodents now do rapid but weak attacks instead of zaps in melee range.\n" +
                "_-_ Phantoms are more visible, but their miasma attack has lower chance to occur, spreads 1.5x more miasma and has its use time and cooldown doubled."));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "new"), false, null);
        changes.hardlight(CharSprite.POSITIVE);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new DogSprite(), "Enemies",
                "_-_ To make the enemy selection less overwhelming, all original Summoning enemies from this point forward will spawn as _mob variants_, randomly being chosen to replace vanilla enemies at beginning of each run.\n\n" +
                        "_-_ Sewer dog is variant of snake, with no changes.\n\n" +
                        "_-_ Rattlesnake is variant of spinner, with attack speed doubled, but damage decreased."));

        changes.addButton(new ChangeButton(new ItemSprite(new ChaoticBomb()), "Bomb Revamp",
                "_-_ _Holy Grenade_ has been reworked to hit 9x9 area instead of 3x3 cross for more damage, bless allies and produce satisfying visuals and sound.\n\n" +
                        "_-_ _Frost Bomb_ now freezes its targets solid, making them take less damage but be completely paralyzed in ice.\n\n" +
                        "_-_ Webbomb has been reworked into _Spider Bomb_ with webs poisoning its targets.\n\n" +
                        "_-_ Woolly Bomb and Shrinking Bomb now exist together.\n\n" +
                        "_-_ Added _Chaotic Bomb_ that fires cursed wand blasts in all directions instead of exploding.\n\n" +
                        "_-_ _Noisemaker_ applies an actual knockback instead of reverse knockback-ish.\n\n" +
                        "_-_ _Arcane Bomb_ does less damage, but no longer destroys unnecessary stuff and has no damage falloff.\n\n" +
                        "_-_ _Supply Station_ provides its healing and saturation faster, also increased total satiety from 400 to 450.\n\n" +
                        "_-_ _Electrical Explosive_ does more damage and stuns enemies, but charges slower and break chance scales depending on charges used instead of total throws."));

        changes.addButton(new ChangeButton(new Slingshot(),
                "_-_ Implemented slingshot with no changes."));

        changes.addButton(new ChangeButton(new ItemSprite(new Food()), "Full Food rework",
                "Finished porting original Summoning's hunger system:\n\n" +
                        "_-_ Increased max satiety from 450 to 1000.\n" +
                        "_-_ Adjusted food values to be roughly the same as original Summoning.\n" +
                        "_-_ Reimplemented saturation and food poisoning buffs.\n" +
                        "_-_ Reimplemented satiety stat in hero's info menu.\n" +
                        "_-_ Hunger consumption values have been returned to original values."));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(CharSprite.WARNING);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.STAFF_PLACEHOLDER), "Staffs in General",
                "_-_ Now actually have enchantments and curses (they weren't saved before).\n\n" +
                        "_-_ Enchanted staff minions now glow just like staff items.\n\n" +
                        "_-_ Now can be rewarded by Sad Ghost.\n\n" +
                        "_-_ Now can be actually stored in magical holster.\n\n" +
                        "_-_ Journal tracks how many times staffs have been used.\n\n" +
                        "_-_ Minions get additional defense, that scales with attunement.\n\n" +
                        "_-_ Slightly increased the health of all late-game minions.\n\n" +
                        "_-_ Reduced attunement HP scaling from 33% to 20% and damage scaling from 33% to 25%.\n\n" +
                        "_-_ Invisibility and purity potions now affect minions in visible range, when drank by player.\n\n" +
                        "_-_ DM-150 is now inorganic enemy."));

        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Phase 2 Tengu now attempts to bring minions next to you.\n" +
                        "_-_ Added Duelist weapon abilities for shop weapons.\n" +
                        "_-_ Hero automatically swaps with allies when moving.\n" +
                        "_-_ Rank Manager now shows ranking stats instead of having buttons for them in revamped menu.\n" +
                        "_-_ Using \"resume motion\" button no longer disables damage interruption (to do old behavior, hold the button).\n" +
                        "_-_ Added all of Reincarnated's content into new journal.\n" +
                        "_-_ Changed healing icon to be more respectful to outside world.\n" +
                        "_-_ Spectral Necromancer's wraiths now spawn with Empowered effect.\n" +
                        "_-_ Elixir of Attunement room now spawns either in floor 2 or 3, and uses same logic as laboratory.\n" +
                        "_-_ Changed Enchant Transfer spell to use arcane resin, but cost 2 less energy."));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed the following bugs:\n" +
                        "_-_ Attunement Constructs never actually spawning\n" +
                        "_-_ Staffs being impossible to uncurse\n" +
                        "_-_ Crystal chests not having a text string for staffs\n" +
                        "_-_ Horde members literally sharing behavior code with their leader"));
    }

    public static void add_v0_3_0_Changes( ArrayList<ChangeInfo> changeInfos ){
        ChangeInfo changes = new ChangeInfo("vReInc-0.3.0", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.get(Icons.BOBYLEV), "Developer Commentary",
                "_-_ Released May 1st, 2024\n" +
                        "_-_ 116 days after Reincarnated 0.2.0\n\n" +
                        "It's been a long wait, isn't it? The 3 months long development hell was surely something for mental state and other stuff... " +
                        "But at least the main course of action, minions, are finally here!"));

        changes.addButton( new ChangeButton(Icons.get(Icons.SHPX), "Shattered Ports",
                "Implemented Shattered v2.3.2 changes."));

        changes = new ChangeInfo("v0.3.2", false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.ADVENTURER, 6), HeroClass.ADVENTURER.title(),
                "_-_ _Precise Strike_ +2 no longer heals enemies and sets max HP of enemies beyond 1 HP.\n\n" +
                        "_-_ _Empowering Meal_ now actually works as effect.\n\n" +
                        "_-_ Reduced _Slice of Power_'s boost by half, decreasing DPS boost from 4x to 2x."));

        changes.addButton(new ChangeButton(new ItemSprite(new GooStaff()), "Hammer of Acid II Buffs",
                "_-_ Now is able to affect hero.\n\n" +
                        "_-_ Increased _Liquid Flame_ effect's damage by _100%_\n\n" +
                        "_-_ Increased _Frostburn Brew_ effect's damage by _78%_ and chill duration by _50%_\n\n" +
                        "_-_ Increased _Toxic Gas_ effect's initial poison by _42%_\n\n" +
                        "_-_ Increased _Corrosive Gas_ effect's initial duration by _33%_ and initial damage by _78%_\n\n" +
                        "_-_ _Mind Vision_ effect now heals for _20%_ instead of _33%_ and now applies the same effects of paralysis and forgetfulness as Phase Shift\n\n" +
                        "_-_ _Caustic Brew_ effect now damages enemies for 50% of their max health, with min damage of 20 and max damage of 50"));

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.STAFF_PLACEHOLDER), "Staffs in General",
                "_-_ Now have actual gold price.\n\n" +
                        "_-_ Now can have random ranks and be enchanted and cursed.\n\n" +
                        "_-_ Now can spawn in crystal chests, mimics and as Ring of Wealth rewards.\n\n" +
                        "_-_ Can no longer be upgraded."));

        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Scrolls of Upgrade and Potions of Strength are now identified by default in face challenge\n" +
                        "_-_ Healing Dart now has the same accuracy in point-blank and at range\n" +
                        "_-_ Tweaked hordes:\n" +
                        "   _*_ Increased the chance to spawn from _14%_ to _20%_\n" +
                        "   _*_ Now spawn on level generation instead of first act\n" +
                        "   _*_ Refactored some of relationship between horde heads and horde members to make it more stable\n" +
                        "   _*_ Wandering horde minions now follow their head directly instead of following head's destination"));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed the following bugs:\n" +
                        "_-_ Effect type relation not working, which lead to resistances and immunities also not working\n" +
                        "_-_ Crashes from minions attempting to retarget or attack\n" +
                        "_-_ Affected by Luck talent upgrading thrown weapons\n" +
                        "_-_ Bizarre horde-related things"));

        changes = new ChangeInfo("v0.3.1", false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton( Conducts.Conduct.FACE.getIcon(), "Curse of Faced",
                "Implemented popular face challenge as _Curse of Faced_ conduct.\n\n" +
                        "_-_ Thanks @tiresdonexits and other face players for telling me most of details.\n" +
                        "_-_ The bugs can still be there."));

        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Staffs can now generate in shops\n" +
                        "_-_ Missile weapons and most of wands can now go through allies\n" +
                        "_-_ Added conduct icons into save file buttons\n" +
                        "_-_ Swarms can no longer spawn hordes\n" +
                        "_-_ Heavy boomerang no longer hurts allies on returning\n" +
                        "_-_ Refactored target priority to allow enemies to attack minions, if they block the path to better target\n" +
                        "_-_ Removed the update notes scene (for now)"));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed the following bugs:\n" +
                        "_-_ Frostburn and frost fire not being visible\n" +
                        "_-_ Adventurer having no shopkeeper line and therefore showing there !!!NO TEXT FOUND!!!\n" +
                        "_-_ Affected by Luck talent upgrading thrown weapons"));

        changes = new ChangeInfo("Minions Overhaul", false,
                "Only 8 staffs have been implemented into the game, more will come with future updates.");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.STAFF_PLACEHOLDER), "In general",
                "_-_ Removed attunement \"minion slots\"; now each minion corresponds to their own staff, that tracks their health.\n" +
                        "_-_ Now have _4 behavior types_, that tell them what enemies to hunt:\n" +
                        "   * _Reactive_ minions will target enemies that are hit by hero\n" +
                        "   * _Protective_ minions will target enemies that attack hero\n" +
                        "   * _Aggressive_ minions will target any enemies they see, even sleeping ones\n" +
                        "   * _Passive_ minions will not attack anything, but slowly regenerate their health\n\n" +
                        "_-_ Behavior types can be switched after summoning the minion with quick-using the staff.\n" +
                        "_-_ Attunement now acts as magical equivalent of strength, gating stronger staffs and making earlier staffs more powerful with progression.\n" +
                        "_-_ Sentries no longer use their health to attack, they now have _Sentry's Resource_ effect which tracks how many actions the sentry has left.\n" +
                        "_-_ Sentries can be moved around with quick-use, but this consumes sentry's Resource points.\n" +
                        "_-_ Staffs no longer recharge when minions are present."));

        changes.addButton(new ChangeButton(new GrayRatStaff(),
                "_-_ Resprited both staff and minion to be more distinct.\n" +
                        "_-_ HP changed from _35/23/12_ to _35/30/10_.\n" +
                        "_-_ Damage changed from _5-12/10-16/15-25_ to _3-14/3-11/1-8_.\n" +
                        "_-_ No longer gets more armor with ranks; armor changed to _1-3_.\n" +
                        "_-_ _Rank II_ now heals itself when attacking and reduces player's hunger usage, when passive.\n" +
                        "_-_ _Rank III_ is equivalent of normal rat, but staff's recharge is reduced to 100 turns and rat gets 7.5 turns of adrenaline on summoning."));

        changes.addButton(new ChangeButton(new SheepStaff(),
                "_-_ No longer considered \"tanky\" minion, but has higher target priority over other minions.\n" +
                        "_-_ HP changed from _50/50/50_ to _55/45/93_.\n" +
                        "_-_ Recharge changed from _500 turns_ to _575 turns_.\n" +
                        "_-_ No longer gets more armor with ranks; armor changed to _2-6/1-4/0_.\n" +
                        "_-_ _Rank II_ now reflects damage back at the attacker.\n" +
                        "_-_ _Rank III_ recharges for 862 turns and permanently has Targeted debuff on it, making all enemies engage such sheep when possible."));

        changes.addButton(new ChangeButton(new MagicMissileStaff(),
                "_-_ HP changed from _85/125/160_ to _40/55/20_.\n" +
                        "_-_ Damage changed from _12-24/12-24/12-24_ to _4-15/10-30/3-12_.\n" +
                        "_-_ Maximum resource changed from _85/42/40_ to _25/25/3_.\n" +
                        "_-_ No longer gains AoE with ranks.\n" +
                        "_-_ Now has additional halo effects on successful hits.\n" +
                        "_-_ _Rank II_ has higher damage, that is affected by armor and less accurate, like YAPD's magic missile.\n" +
                        "_-_ _Rank III_ is short-living sentry, that recharges for 75 turns and gives wand recharge on hitting enemies."));

        changes.addButton(new ChangeButton(new GnollHunterStaff(),
                "_-_ No longer always pierces armor.\n" +
                        "_-_ HP changed from _25/40/66_ to _25/30/25_.\n" +
                        "_-_ Damage changed from _6-12/7-12/9-12_ to _5-15/1-5/20-45_.\n" +
                        "_-_ Now runs away from enemies more consistently.\n" +
                        "_-_ No longer throws more darts per turn with ranks.\n" +
                        "_-_ _Rank II_ now throws 3 darts per turn, that deal low damage, but trigger gnoll's enchantment more frequently.\n" +
                        "_-_ _Rank III_ uses sniper shots, that pierce armor and knock enemies away, but trigger cooldown for each attack."));

        changes.addButton(new ChangeButton(new WizardStaff(),
                "_-_ Resprited both staff and minion to be more distinct.\n" +
                        "_-_ HP changed from _35/28/19_ to _35/25/35_.\n" +
                        "_-_ Damage changed from _4-15/2-8/0-5_ to _7-17/2-6/0_.\n" +
                        "_-_ Recharge changed from _700/500/300 turns_ to _600/850/750 turns_.\n" +
                        "_-_ His magic resistance is now capped at 25% and no longer changes with ranks.\n" +
                        "_-_ _Rank II_ now attacks twice as frequently and attempts to corrupt the target enemy; the potency scales with progression.\n" +
                        "_-_ _Rank III_ doesn't attack at all, but provides various boosts to player, that depend on wizard's behavior type."));

        changes.addButton(new ChangeButton(new RoboStaff(),
                "_-_ No longer considered \"tanky\" minion, but has higher target priority over other minions.\n" +
                        "_-_ HP changed from _120/175/225_ to _120/105/215_.\n" +
                        "_-_ Damage changed from _25-50/30-55/35-60_ to _20-40/15-25/5-15_.\n" +
                        "_-_ Recharge changed from _675/875/1175 turns_ to _600/800/1500 turns_.\n" +
                        "_-_ Attack delay changed from _3.5/2.5/1.5 turns_ to _2/1.5/1 turns_.\n" +
                        "_-_ Now has 40% evasion penalty.\n" +
                        "_-_ _Rank II_ has lower stats, but can chain to itself any kind of enemy, not just ranged ones.\n" +
                        "_-_ _Rank III_ has lower armor, damage and passive regen rate, but heals from both attacking and being attacked."));

        changes.addButton(new ChangeButton(new GooStaff(),
                "_-_ HP changed from _45/55/65_ to _45/55/15_.\n" +
                        "_-_ Damage changed from _20-35/15-26/8-17_ to _20-35/12-25/4-15_.\n" +
                        "_-_ Charged attacks no longer charge for longer and become more deadly with ranks. They also no longer damage allies.\n" +
                        "_-_ Armor is capped at _5-20_ and no longer increases with ranks.\n" +
                        "_-_ _Rank II_ charged attack no longer deals damage, but covers larger range and applies effect based on a potion infused into Goo minion. Potions can be infused by throwing them into the minion.\n" +
                        "_-_ _Rank III_ recharges for 200 turns and takes 10% damage without viscosity; its charged attack does damage based on depth's bomb damage and makes minion instantly die after using it."));

        changes.addButton(new ChangeButton(new BlasterStaff(),
                "_-_ HP changed from _120/160/200_ to _85/75/100_.\n" +
                        "_-_ Damage changed from _15-60/12-40/10-34_ to _20-50/6-14/30-75_.\n" +
                        "_-_ Maximum resource changed from _60/27/25_ to _18/50/12_.\n" +
                        "_-_ Attack delay changed from _1.75/1.2/0.66 turns_ to _2/0.33/2.5 turns_.\n" +
                        "_-_ No longer damages allies with its beams.\n" +
                        "_-_ _Rank II_ rapidly fires weak beams, that deal more damage the more enemies they penetrate.\n" +
                        "_-_ _Rank III_ recharges for 700 turns and deals huge KARMA damage, that ignores enemy's magic armor and ability to snap out of paralysing effects, and marks it with KARMA effect, which permanently makes enemy take 15% more damage from everything." +
                        "As price, Rank III blaster can only strike each enemy once."));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(CharSprite.WARNING);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.ELIXIR_ATTUNED), "Elixir of Attunement",
                "Reimplemented Elixir of Attunement with some important changes:\n\n" +
                        "_-_ Resprited.\n" +
                        "_-_ No longer sold in shops.\n" +
                        "_-_ No longer possible to create with alchemy.\n" +
                        "_-_ Now has its own room with custom blue carpet tiles."));

        changes.addButton( new ChangeButton(new RatSprite(), "Hordes",
                "Reimplemented hordes.\n\n" +
                        "_-_ Should be less glitchy or more glitchy, idk."));

        changes.addButton( new ChangeButton(new AttunementConstructSprite(), "Attunement Construct",
                "Reimplemented Final Froggits, with flavor change into Attunement Constructs.\n\n" +
                        "_-_ Reduced some stats.\n" +
                        "_-_ Spawn in less quantities in Demon Halls.\n" +
                        "_-_ Does not spawn on Yog-Dzewa yet."));

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.ELIXIR_DRAGON), "Elemental Elixirs/Brews",
                "Reimplemented merge of Blizzard Brew/Elixir of Icy Touch and Inferno Brew/Elixir of Dragon's Blood."));

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.TYPE_MANAGER), "New way to obtain Ranks",
                "Changed ranking from being changed with upgrades to to be managed by new item, Rank Manager.\n\n" +
                        "_-_ Rank managers spawn every 2 depths, but not on boss depths, meaning there is 10 rank managers in entire game.\n" +
                        "_-_ Rank managers allow to switch applicable item's rank to any of three, from I to III, and preview rank's effects on item."));

        changes.addButton( new ChangeButton(new SpiritBow(),
                "Reimplemented Spirit Bow's ability to have ranks.\n\n" +
                        "_-_ Increased bow II's cone from 50 to 65 degrees.\n" +
                        "_-_ Super-shot cooldown scales with bow's rank attack modifier."));

        changes.addButton( new ChangeButton(new Food(),
                "Adjusted satiety consumption across the board:\n\n" +
                        "_-_ Decreased moving satiety value from _1.33_ to _1.25_.\n" +
                        "_-_ Increased unlocking satiety value from _3.75_ to _5_.\n" +
                        "_-_ Decreased item picking satiety value from _2.25_ to _1.75_.\n" +
                        "_-_ Decreased throwing satiety value from _3_ to _2.5_.\n" +
                        "_-_ Decreased equipping satiety value from _17_ to _15_.\n" +
                        "_-_ Decreased level moving satiety value from _30_ to _25_.\n" +
                        "_-_ Decreased attacking satiety value from _3.75_ to _3_."));

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.POTION_CRIMSON), "Healing gases",
                "Reimplemented gases produced by throwing Potion of Healing and Elixir of Honeyed Healing, with some changes:\n\n" +
                        "_-_ Now hurt hero with Pharmacophobia challenge active.\n" +
                        "_-_ Both gases now heal percentage amount of HP to characters inside of it (2% for PoH and 6.25% for EoHH).\n" +
                        "_-_ Honey gas is less dense visually and lasts 20% more time.\n" +
                        "_-_ Healing gas lasts 10% less time."));

        changes.addButton( new ChangeButton(new Cleaver(),
                "Reimplemented cleaver, with rework to its functionality:\n\n" +
                        "_-_ Now T3 weapon instead of T2.\n" +
                        "_-_ Now deals 50% more damage than sword, but takes 50% more time to swing.\n" +
                        "_-_ Decreased accuracy penalty from 40% to 20%.\n" +
                        "_-_ Now deals 2x damage to enemies with full HP."));

        changes.addButton( new ChangeButton(new EnchantParchment(),
                "Reimplemented Enchant Transfer spell.\n\n" +
                        "_-_ Now has recipe with stone of enchantment."));

        changes.addButton( new ChangeButton(new BuffIcon(BuffIndicator.FROSTBURN, true), "Frostburn effect",
                "Reimplemented Frostburn and cold fire:\n\n" +
                        "_-_ The way to inflict it for now are Wand of Frost and Frostburn Brew.\n" +
                        "_-_ Wand of Frost does less damage but inflicts Frostburn.\n" +
                        "_-_ Frostburn Brew is made from potions of Liquid Flame and Frost and acts like Liquid Flame, but spreads frost fire instead of normal fire."));

        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Reimplemented Mineral Water and Scimitar's rework, with no changes.\n" +
                        "_-_ Staffs now have their own journal tab and identification badge.\n" +
                        "_-_ Fixed inconsistent outlines for all Summoning-sourced sprites.\n" +
                        "_-_ Added remains item for Adventurer, which adds one bonus talent point into tier chosen.\n" +
                        "_-_ Added badges for acquiring certain amount of attunement.\n" +
                        "_-_ Reimplemented about page, with updated icons and links for contributors.\n" +
                        "_-_ Resprited Runic Blade's projectile."));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed the following bugs from original Summoning:\n" +
                        "_-_ Enemies running from Flashbang's effect even if they are immovable entities.\n" +
                        "_-_ Dying with Blessed Ankh and Become Ethereal conduct doesn't trigger the revive."));
    }

    public static void add_v0_2_0_Changes( ArrayList<ChangeInfo> changeInfos ){
        ChangeInfo changes = new ChangeInfo("vReInc-0.2.0", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.get(Icons.BOBYLEV), "Developer Commentary",
                "_-_ Released January 6th, 2024\n" +
                        "_-_ 13 days after Reincarnated 0.1.0"));

        changes.addButton( new ChangeButton( new ItemSprite(new Firebomb()), "Enhanced bombs",
                "Reimplemented all enhanced bomb overhauls and changes.\n\n" +
                        "_-_ Shrinking from shrinking bomb now works on minibosses, but is only applied for 8 turns."));

        changes.addButton( new ChangeButton(new ItemSprite(new Ropes()), "Ropes",
                "Reimplemented ropes.\n\n" +
                        "_-_ Cost slightly less."));

        changes.addButton( new ChangeButton(HeroSprite.avatar(HeroClass.ADVENTURER, 1), "Adventurer",
                "Reimplemented Adventurer, with some placeholder-ish T1, T2 and T3 talents.\n\n" +
                        "_-_ Shopkeeper and Blacksmith quotes are not implemented yet, due to forgetfulness."));

        changes.addButton( new ChangeButton(Icons.get(Icons.CONDUCTS_COLOR), "Conducts",
                "Reimplemented most of conducts (16 out of 24) and all their backend code.\n\n" +
                        "_-_ Some of conducts will likely never return, due to being too unfun to play and code.\n" +
                        "_-_ Some of conducts will be back, when Shattered's challenges will be deprecated."));

        changes.addButton( new ChangeButton(new ItemSprite(new RunicBlade()), "Runic Blade",
                "Reimplemented runic blade rework.\n\n" +
                        "_-_ The cooldown is now actually shown as buff.\n" +
                        "_-_ Fixed all typos in its script."));

        changes.addButton( new ChangeButton(new ItemSprite(new Food()), "Hunger rework",
                "Reimplemented hunger rework.\n\n" +
                        "_-_ The numbers are still up to debate."));

        changes.addButton( new ChangeButton(Icons.get(Icons.GOLD), "Email button",
                "Implemented email button from Experienced Redone.\n\n" +
                        "_-_ More emails, but hopefully feedback will be more useful here."));

        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ All classes are now always unlocked.\n\n" +
                        "_-_ Reimplemented the code that allows interfaces be used for buff checks.\n\n" +
                        "_-_ Removed support nagging thing after Goo."));

        changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Armored Cloak being encounterable in the wild\n" +
                        "_-_ Debug scroll being not possible to enable"));
    }

    public static void add_v0_1_0_Changes( ArrayList<ChangeInfo> changeInfos ){
        ChangeInfo changes = new ChangeInfo("vReInc-0.1.0", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.get(Icons.BOBYLEV), "Developer Commentary",

                "Uhm, hello there.\n" +
                        "\n" +
                        "This is a \"first\" update to what I call Summoning: Reincarnated project, which is supposed to reimplement the majority of Summoning's content and mechanics in newest Shattered engine.\n\n" +
                        "The changes will be gradual, focusing on smaller and less invasive code-wise features, building up to big guns, like summoner class, ranking and Abyss.\n\n" +
                        "Shattered mechanics will be eventually discarded in favor of Summoning's ones (but still persist in code for easier merging).\n\n" +
                        "If this project will be successful, it will become the main version of Summoning, with previous one, that is based on v0.9.4, becoming legacy."));

        changes.addButton( new ChangeButton( new ItemSprite(new ScoutArmor()), "Armored Cloak",
                "Reimplemented Armored Cloak as starting Huntress's armor, with following changes:\n" +
                        "_-_ Increased super-shot's damage boost from 32% and x1.13x per tile to 35% and x1.20x per tile\n" +
                        "_-_ Increased super-shot's cooldown from 20 turns to 25 turns"));

        changes.addButton( new ChangeButton(new ItemSprite(new StoneHammer()), "Shop weapons",
                "Reimplemented exclusive shop weapons, with fixed typos in their descriptions."));

        changes.addButton( new ChangeButton(new ItemSprite(new Bomb()), "Bomb rework",
                "Reimplemented bomb's damage boost and reduced delay, with changed sprites for some bombs, but with without their reworks."));

        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ The language is always locked to English and cannot be changed.\n\n" +
                        "_-_ Spirit Bow's arrow sprite depend on the augment (with new sprite for speed bow).\n\n" +
                        "_-_ The app's icon is different from Summoning (looking at fully replicating the original).\n\n" +
                        "_-_ Reimplemented most of Summoning's UI changes, with improvements to fit new style.\n\n" +
                        "_-_ Debug scroll can be enabled in Connectivity part of menu."));
    }
}
