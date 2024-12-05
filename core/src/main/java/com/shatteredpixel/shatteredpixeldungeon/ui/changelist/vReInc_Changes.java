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
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatSprite;
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
        add_v0_4_0_Changes(changeInfos);
        add_v0_3_0_Changes(changeInfos);
        add_v0_2_0_Changes(changeInfos);
        add_v0_1_0_Changes(changeInfos);
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

        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.CONJURER, 6), HeroClass.CONJURER.title(),
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

        changes.addButton( new ChangeButton( new Image(Assets.Interfaces.SUBCLASS_ICONS, (Conducts.Conduct.FACE.icon - 1) * 16, 16, 16, 16), "Curse of Faced",
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

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.RANK_MANAGER), "New way to obtain Ranks",
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
