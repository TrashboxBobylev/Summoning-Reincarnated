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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EffectBuff;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ConjurerArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScoutArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.SyntheticArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ConjurerBook;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.BeamOfAffection;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.DreemurrsNecromancy;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.EnergizedRenewal;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.PushingWaveform;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.RunicShell;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.ShockerBreaker;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.StarBlazing;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.SubNullFieldLighter;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.TypeManager;
import com.shatteredpixel.shatteredpixeldungeon.items.staffs.FroggitStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Slingshot;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentIcon;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public enum HeroPerk {
    WEAPON_ABILITIES(0xcc33ff){
        @Override
        public Image icon() {
            return new TalentIcon(Talent.SWIFT_EQUIP);
        }
    },
    WARRIOR_SEAL(0x9a0000){
        @Override
        public Image icon() {
            return new ItemSprite(ItemSpriteSheet.SEAL);
        }

        @Override
        public void onInitialization(Hero hero) {
            if (hero.belongings.armor != null){
                hero.belongings.armor.affixSeal(new BrokenSeal());
                Catalog.setSeen(BrokenSeal.class); //as it's not added to the inventory
            }
        }
    },
    SLINGSHOT(0x666114){
        @Override
        public Image icon() {
            return new ItemSprite(ItemSpriteSheet.SLINGSHOT);
        }

        @Override
        public float price() {
            return 0.75f;
        }

        @Override
        public void onInitialization(Hero hero) {
            Slingshot stones = new Slingshot();
            stones.charge = 1;
            stones.identify().collect();
        }
    },
    EQUIPPABLE_WANDS(0xffff00){
        @Override
        public Image icon() {
            return new ItemSprite(ItemSpriteSheet.MAGES_STAFF);
        }
    },
    SEARCH_RANGE(0x7fa9d2){
        @Override
        public Image icon() {
            return Icons.get(Icons.MAGNIFY);
        }

        @Override
        public float price() {
            return 2/3f;
        }
    },
    CLOAK_OF_SHADOWS(0x090909){
        @Override
        public Image icon() {
            return new ItemSprite(ItemSpriteSheet.ARTIFACT_CLOAK);
        }

        @Override
        public void onInitialization(Hero hero) {
            CloakOfShadows cloak = new CloakOfShadows();
            (hero.belongings.artifact = cloak).identify();
            hero.belongings.artifact.activate( hero );
        }

        @Override
        public void onAcquiring(Hero hero) {
            new CloakOfShadows().identify().collect();
        }

        @Override
        public float price() {
            return 1.66f;
        }
    },
    HUNTRESS_BOW(0x99ffff){
        @Override
        public Image icon() {
            return new ItemSprite(ItemSpriteSheet.SPIRIT_BOW);
        }

        @Override
        public void onInitialization(Hero hero) {
            SpiritBow bow = new SpiritBow();
            bow.identify().collect();
        }
    },
    SCOUT_ARMOR(0x062f4a, HUNTRESS_BOW){
        @Override
        public Image icon() {
            return new ItemSprite(ItemSpriteSheet.ARMOR_SCOUT);
        }

        @Override
        public float price() {
            return 0.75f;
        }

        @Override
        public void onInitialization(Hero hero) {
            (hero.belongings.armor = new ScoutArmor()).identify();
            if (hero.perks.contains(WARRIOR_SEAL))
                hero.belongings.armor.affixSeal(new BrokenSeal());
        }

        @Override
        public void onAcquiring(Hero hero) {
            new ScoutArmor().identify().collect();
        }
    },
    FURROWED_GRASS(0x005826){
        @Override
        public float price() {
            return 0.75f;
        }

        @Override
        public Image icon() {
            return Icons.GRASS.get();
        }
    },
    CONJURER_MAGIC(0x4c51ad){
        @Override
        public Image icon() {
            return new ItemSprite(ItemSpriteSheet.BEHAVIOR_PASSIVE);
        }

        @Override
        public void onInitialization(Hero hero) {
            ConjurerBook book = new ConjurerBook();
            book.collect();
            StarBlazing star = new StarBlazing();
            star.collect();
            EnergizedRenewal energizedRenewal = new EnergizedRenewal();
            energizedRenewal.collect();
            new BeamOfAffection().collect();
        }

        @Override
        public void onAcquiring(Hero hero) {
            super.onAcquiring(hero);
            if (hero.lvl >= 5) new RunicShell().identify().collectWithAnnouncing();
            if (hero.lvl >= 7) new PushingWaveform().identify().collectWithAnnouncing();
            if (hero.lvl >= 9) new ShockerBreaker().identify().collectWithAnnouncing();
            if (hero.lvl >= 11) new DreemurrsNecromancy().identify().collectWithAnnouncing();
            if (hero.lvl >= 15) new SubNullFieldLighter().identify().collectWithAnnouncing();
        }
    },
    CONJURER_ARMOR(0x4c51ad){
        @Override
        public Image icon() {
            return new ItemSprite(ItemSpriteSheet.ARMOR_CONJURER);
        }

        @Override
        public float price() {
            return 0.75f;
        }

        @Override
        public void onInitialization(Hero hero) {
            (hero.belongings.armor = new ConjurerArmor()).identify();
            if (hero.perks.contains(WARRIOR_SEAL))
                hero.belongings.armor.affixSeal(new BrokenSeal());
        }

        @Override
        public void onAcquiring(Hero hero) {
            new ConjurerArmor().identify().collect();
        }
    },
    STARTER_SUMMON(0x827b00){
        @Override
        public Image icon() {
            return new ItemSprite(ItemSpriteSheet.FROGGIT_STAFF);
        }

        @Override
        public float price() {
            return 0.75f;
        }

        @Override
        public void onInitialization(Hero hero) {
            FroggitStaff staff = new FroggitStaff();
            staff.identify().collect();
        }
    },
    EASY_TIME(0xff0000){
        @Override
        public Image icon() {
            return new TalentIcon(Talent.STRONGMAN);
        }

        @Override
        public float price() {
            return 1.33f;
        }

        @Override
        public void onInitialization(Hero hero) {
            hero.HP = hero.HT = 30;
            hero.STR = 12;
        }

        @Override
        public void onAcquiring(Hero hero) {
            hero.STR += 12;
            hero.updateHT(false);
        }
    },
    EXTRA_LOOT(0x7c7c7c){
        @Override
        public Image icon() {
            return new ItemSprite(ItemSpriteSheet.BAG_GENERIC);
        }

        @Override
        public float price() {
            return 2f;
        }
    },
    ADVENTURER_ARMOR(0x858585){
        @Override
        public Image icon() {
            return new ItemSprite(ItemSpriteSheet.ARMOR_ADVENTURER);
        }

        @Override
        public float price() {
            return 0.5f;
        }

        @Override
        public void onInitialization(Hero hero) {
            (hero.belongings.armor = new SyntheticArmor()).identify();
            if (hero.perks.contains(WARRIOR_SEAL))
                hero.belongings.armor.affixSeal(new BrokenSeal());
        }

        @Override
        public void onAcquiring(Hero hero) {
            new SyntheticArmor().identify().collect();
        }
    },
    ALL_BAGS(0xaf775c){
        @Override
        public Image icon() {
            return new Image(Assets.Interfaces.BADGES, 48, 128, 16, 16);
        }

        @Override
        public float price() {
            return 1.66f;
        }

        @Override
        public void onInitialization(Hero hero) {
            new PotionBandolier().collect();
            Dungeon.LimitedDrops.POTION_BANDOLIER.drop();
            new ScrollHolder().collect();
            Dungeon.LimitedDrops.SCROLL_HOLDER.drop();
            new MagicalHolster().collect();
            Dungeon.LimitedDrops.MAGICAL_HOLSTER.drop();
        }
    },
    EXTRA_ALCHEMY(0xb2f2ff){
        @Override
        public Image icon() {
            return new ItemSprite(ItemSpriteSheet.ENERGY);
        }

        @Override
        public float price() {
            return 0.75f;
        }
    },
    EXTRA_TYPE_MANAGER(0x3f61bf){
        @Override
        public Image icon() {
            return new ItemSprite(ItemSpriteSheet.TYPE_MANAGER);
        }

        @Override
        public float price() {
            return 0.8f;
        }

        @Override
        public void onInitialization(Hero hero) {
            new TypeManager().collect();
        }
    },
    ARTIFACT_BOOST(0x46f574){
        @Override
        public Image icon() {
            return new BuffIcon(new ArtifactRecharge(), true);
        }

        @Override
        public float price() {
            return 0.75f;
        }
    }
    ;

    public int color = -1;
    public final ArrayList<HeroPerk> parents;

    HeroPerk(int color){
        this.color = color;
        this.parents = new ArrayList<>();
    }

    HeroPerk(int color, HeroPerk... parentPerks){
        this.color = color;
        this.parents = new ArrayList<>(List.of(parentPerks));
    }

    public Image icon(){
        Image icon = Icons.get(Icons.TALENT);
        if (color != -1)
            icon.hardlight(color);
        return icon;
    }

    public float price(){
        return 1.0f;
    }

    public String desc(){
        return Messages.get(this, name() + ".desc");
    }

    public void onInitialization(Hero hero){

    }

    public void onAcquiring(Hero hero){
        onInitialization(hero);
    }

    public static class ArtifactBoostBuff extends EffectBuff {
        @Override
        public int icon() {
            return BuffIndicator.RECHARGING;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0x46f574);
        }
    }

    public static boolean menuAvailable(){
        return Dungeon.mode == Dungeon.GameMode.RANDOM_HERO;
    }

    public static EnumSet<HeroPerk> omniheroPerks(){
        return EnumSet.of(
            WARRIOR_SEAL,
            SLINGSHOT,
            EQUIPPABLE_WANDS,
            CLOAK_OF_SHADOWS,
            WEAPON_ABILITIES,
            HUNTRESS_BOW,
            SCOUT_ARMOR,
            CONJURER_MAGIC,
            STARTER_SUMMON,
            ALL_BAGS
        );
    }

    // a primary ability to start with
    public static EnumSet<HeroPerk> essentialAbilities(){
        return EnumSet.of(
                WARRIOR_SEAL,
                EQUIPPABLE_WANDS,
                HUNTRESS_BOW,
                WEAPON_ABILITIES,
                CONJURER_MAGIC
        );
    }

    // secondary things (2 of them)
    // doesn't include most of the adventurer stuff due to being overpowered without downsides
    public static EnumSet<HeroPerk> helpfulAbilities(){
        return EnumSet.of(
                SLINGSHOT,
                SEARCH_RANGE,
                FURROWED_GRASS,
                STARTER_SUMMON,
                EXTRA_ALCHEMY,
                EXTRA_TYPE_MANAGER,
                ARTIFACT_BOOST
        );
    }

    public static HashMap<HeroPerk, Float> armors = new HashMap<>();
    static {
        armors.put(ADVENTURER_ARMOR, 5.0f);
        armors.put(CONJURER_ARMOR, 2.0f);
        armors.put(SCOUT_ARMOR, 3.0f);
    }

    // perk buying handler
    public static ArrayDeque<HeroPerk> queue = new ArrayDeque<>();

    public static void initQueue(){
        ArrayList<HeroPerk> firstQueue = new ArrayList<>(List.of(HeroPerk.values()));

        firstQueue.removeAll(Dungeon.hero.perks);

        Random.pushGenerator(Dungeon.seed+1);
        Random.shuffle(firstQueue);
        Random.popGenerator();

        queue.addAll(firstQueue);
    }

    private static final String QUEUE = "perk_queue";

    public static void storeQueue(Bundle bundle){
        bundle.put(QUEUE, queue.stream().map(HeroPerk::name).toArray(String[]::new));
    }

    public static void restoreQueue(Bundle bundle){
        if (bundle.contains(QUEUE))
            queue = Arrays.stream(bundle.getStringArray(QUEUE)).map(HeroPerk::valueOf).collect(Collectors.toCollection(ArrayDeque::new));
        else
            HeroPerk.initQueue();
    }
}
