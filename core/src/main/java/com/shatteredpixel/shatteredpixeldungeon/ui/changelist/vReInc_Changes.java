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
import com.shatteredpixel.shatteredpixeldungeon.items.Ropes;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScoutArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Firebomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CleanWater;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.EnchantParchment;
import com.shatteredpixel.shatteredpixeldungeon.items.staffs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Cleaver;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Quarterstaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RunicBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.shop.StoneHammer;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class vReInc_Changes {
    public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){
        add_v0_3_0_Changes(changeInfos);
        add_v0_2_0_Changes(changeInfos);
        add_v0_1_0_Changes(changeInfos);
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
