package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Ropes;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScoutArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Firebomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Quarterstaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RunicBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.shop.StoneHammer;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class vReInc_Changes {
    public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){

        add_v0_2_0_Changes(changeInfos);
        add_v0_1_0_Changes(changeInfos);
    }

    public static void add_v0_2_0_Changes( ArrayList<ChangeInfo> changeInfos ){
        ChangeInfo changes = new ChangeInfo("vReInc-0.2.0", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.get(Icons.BOBYLEV), "Developer Commentary",
                "_-_ Released January 6th, 2023\n" +
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

        changes.addButton( new ChangeButton(Icons.get(Icons.CONDUCTS_ON), "Conducts",
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
