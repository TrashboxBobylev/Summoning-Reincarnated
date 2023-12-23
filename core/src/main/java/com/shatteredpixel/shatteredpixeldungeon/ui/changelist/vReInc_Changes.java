package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScoutArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Quarterstaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.shop.StoneHammer;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

import java.util.ArrayList;

public class vReInc_Changes {
    public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){

        add_v0_1_0_Changes(changeInfos);
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
