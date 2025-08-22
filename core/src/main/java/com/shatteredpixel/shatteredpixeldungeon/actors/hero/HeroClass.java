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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.AscendedForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.Trinity;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.conjurer.Ascension;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.conjurer.Hyperblast;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.conjurer.TriadOfPower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Challenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.ElementalStrike;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Feint;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.NaturesPower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpectralBlades;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.ElementalBlast;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WarpBeacon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.DeathMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.ShadowClone;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.SmokeBomb;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Endure;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.HeroicLeap;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Shockwave;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KingsCrown;
import com.shatteredpixel.shatteredpixeldungeon.items.Ropes;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ConjurerArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScoutArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.SyntheticArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ConjurerBook;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.BeamOfAffection;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.DreemurrsNecromancy;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.EnergizedRenewal;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.PushingWaveform;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.RunicShell;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.ShockerBreaker;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.StarBlazing;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.SubNullFieldLighter;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.staffs.FroggitStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Slingshot;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Cudgel;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger2;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Rapier;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.ToyKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnive2;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpike;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;

public enum HeroClass {

	WARRIOR( HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR ),
	MAGE( HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK ),
	ROGUE( HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER ),
	HUNTRESS( HeroSubClass.SNIPER, HeroSubClass.WARDEN ),
	DUELIST( HeroSubClass.CHAMPION, HeroSubClass.MONK ),
	CLERIC( HeroSubClass.PRIEST, HeroSubClass.PALADIN ),
	ADVENTURER(HeroSubClass.NONE),
	CONJURER(HeroSubClass.SOUL_WIELDER, HeroSubClass.WILL_SORCERER);

	private HeroSubClass[] subClasses;

	HeroClass( HeroSubClass...subClasses ) {
		this.subClasses = subClasses;
	}

	public void initHero( Hero hero ) {

		hero.heroClass = this;
		Talent.initClassTalents(hero);
        if (Dungeon.mode == Dungeon.GameMode.RANDOM_HERO){
            Talent.shuffleTalents(hero);
        }

		Item i = new ClothArmor().identify();
		if (!Challenges.isItemBlocked(i)) hero.belongings.armor = (ClothArmor)i;

		i = new Food();
		if (!Challenges.isItemBlocked(i)) i.collect();

		new Ropes().quantity(5).collect();

		new VelvetPouch().collect();
		Dungeon.LimitedDrops.VELVET_POUCH.drop();

		Waterskin waterskin = new Waterskin();
		waterskin.collect();

		new ScrollOfIdentify().identify();
		if (Dungeon.isChallenged(Conducts.Conduct.FACE)){
			new ScrollOfUpgrade().identify();
			new PotionOfStrength().identify();
		}

		if (Dungeon.isChallenged(Conducts.Conduct.EVERYTHING)){
			WandOfMagicMissile staff = new WandOfMagicMissile();
			(hero.belongings.weapon = staff).identify();
			hero.belongings.weapon.activate(hero);
			(hero.belongings.armor = new ScoutArmor()).identify();
			SpiritBow bow = new SpiritBow();
			bow.identify().collect();
			Dungeon.quickslot.setSlot(0, bow);
			Dungeon.quickslot.setSlot(1, hero.belongings.armor);
			Dungeon.quickslot.setSlot(2, staff);
			if (hero.belongings.armor != null){
				hero.belongings.armor.affixSeal(new BrokenSeal());
			}
			CloakOfShadows cloak = new CloakOfShadows();
			(hero.belongings.artifact = cloak).identify();
			hero.belongings.artifact.activate( hero );
			Dungeon.quickslot.setSlot(3, cloak);
			ConjurerBook book = new ConjurerBook();
			book.collect();
			FroggitStaff staff1 = new FroggitStaff();
			staff1.identify().collect();
			StarBlazing star = new StarBlazing();
			star.collect();
			EnergizedRenewal energizedRenewal = new EnergizedRenewal();
			energizedRenewal.collect();
			new BeamOfAffection().collect();
			Dungeon.quickslot.setSlot(4, staff1);
			new PotionBandolier().collect();
			Dungeon.LimitedDrops.POTION_BANDOLIER.drop();
			new ScrollHolder().collect();
			Dungeon.LimitedDrops.SCROLL_HOLDER.drop();
			new MagicalHolster().collect();
			Dungeon.LimitedDrops.MAGICAL_HOLSTER.drop();
			Slingshot stones = new Slingshot();
			stones.charge = 1;
			stones.identify().collect();
		}
		switch (this) {
			case WARRIOR:
				initWarrior(hero);
				break;

			case MAGE:
				initMage(hero);
				break;

			case ROGUE:
				initRogue(hero);
				break;

			case HUNTRESS:
				initHuntress(hero);
				break;

			case DUELIST:
				initDuelist(hero);
				break;

			case ADVENTURER:
				initAdventurer(hero);
				break;

			case CONJURER:
				initConjurer(hero);
				break;

			case CLERIC:
				initCleric( hero );
				break;
		}

		if (SPDSettings.quickslotWaterskin()) {
			for (int s = 0; s < QuickSlot.SIZE; s++) {
				if (Dungeon.quickslot.getItem(s) == null) {
					Dungeon.quickslot.setSlot(s, waterskin);
					break;
				}
			}
		}

		if (Dungeon.isChallenged(Conducts.Conduct.WRAITH)) hero.HP = hero.HT = 1;

		if (Dungeon.mode == Dungeon.GameMode.ABYSS_START){
			// makes it compatible with endless potential
			for (Class bagType : new Class[]{MagicalHolster.class, ScrollHolder.class, PotionBandolier.class}){
				if (hero.belongings.getItem(bagType) == null){
					((Item)Reflection.newInstance(bagType)).collect();
				}
			}
			Dungeon.LimitedDrops.MAGICAL_HOLSTER.drop();
			Dungeon.LimitedDrops.SCROLL_HOLDER.drop();
			Dungeon.LimitedDrops.POTION_BANDOLIER.drop();

			new ScrollOfUpgrade().identify().quantity(15).collect();

			Dungeon.gold = 5000;

			hero.STR += 10;
			hero.ATU += 5;
			hero.lvl = 30;
			hero.updateStats();
			hero.HP = hero.HT;
			if (this == HeroClass.CONJURER) {
				new RunicShell().identify().collect();
				new PushingWaveform().identify().collect();
				new ShockerBreaker().identify().collect();
				new DreemurrsNecromancy().identify().collect();
				new SubNullFieldLighter().identify().collect();
			}

			new TengusMask().collect();
			new KingsCrown().collect();
		}

        if (Dungeon.mode == Dungeon.GameMode.RANDOM_HERO){

            boolean wandIsMainWeapon = false;

            if (hero.belongings.weapon instanceof Wand){
                wandIsMainWeapon = true;
            }

            MeleeWeapon weapon = (MeleeWeapon) Generator.randomUsingDefaults(Random.oneOf(Generator.Category.WEP_T1, Generator.Category.WEP_T2, Generator.Category.WEP_T3, Generator.Category.WEP_T4, Generator.Category.WEP_T5));
            if (weapon != null){
                weapon.duelistStart = true;
                weapon.tier = 1;
                weapon.cursed = false;
                weapon.level(0);
                weapon.enchant(null);
                (hero.belongings.weapon = weapon).identify();
            }

            if (wandIsMainWeapon){
                Wand wand;
                do {
                    wand = (Wand) Reflection.newInstance(Random.element(Generator.Category.WAND.classes));
                } while (wand instanceof WandOfMagicMissile);
                wand.identify().collect();
            }

            for (Item item: hero.belongings){
                if (item instanceof MissileWeapon){
                    item.detachAll(hero.belongings.backpack);
                    MissileWeapon randomThrowie;
                    do {
                        randomThrowie = (MissileWeapon) Reflection.newInstance(Random.element(Generator.Category.MISSILE.classes));
                    } while (randomThrowie.getClass() == item.getClass());
                    randomThrowie.identify().collect();
                }
                if (item instanceof Artifact){
                    item.detachAll(hero.belongings.backpack);
                    Random.pushGenerator();
                    Artifact cloak = Generator.randomArtifact();
                    (hero.belongings.artifact = cloak).identify();
                    hero.belongings.artifact.activate(hero);
                    Random.popGenerator();
                    Dungeon.quickslot.setSlot(0, cloak);
                }
            }
        }

        if (Dungeon.isChallenged(Conducts.Conduct.LEVEL_DOWN)){
            hero.lvl = 24;
            hero.exp += hero.maxExp()/2;
            hero.updateStats();
        }

	}

	public Badges.Badge masteryBadge() {
		switch (this) {
			case WARRIOR:
				return Badges.Badge.MASTERY_WARRIOR;
			case MAGE:
				return Badges.Badge.MASTERY_MAGE;
			case ROGUE:
				return Badges.Badge.MASTERY_ROGUE;
			case HUNTRESS:
				return Badges.Badge.MASTERY_HUNTRESS;
			case DUELIST:
				return Badges.Badge.MASTERY_DUELIST;
			case CLERIC:
				return Badges.Badge.MASTERY_CLERIC;
		}
		return null;
	}

	private static void initWarrior( Hero hero ) {
		if (!Dungeon.isChallenged(Conducts.Conduct.EVERYTHING)) {
			(hero.belongings.weapon = new WornShortsword()).identify();
			Slingshot stones = new Slingshot();
			stones.charge = 1;
			stones.identify().collect();

			Dungeon.quickslot.setSlot(0, stones);

            if (hero.belongings.armor != null){
                hero.belongings.armor.affixSeal(new BrokenSeal());
                Catalog.setSeen(BrokenSeal.class); //as it's not added to the inventory
            }
        }

		new PotionOfHealing().identify();
		new ScrollOfRage().identify();
	}

	private static void initMage( Hero hero ) {
		if (!Dungeon.isChallenged(Conducts.Conduct.EVERYTHING)) {
			WandOfMagicMissile wand = new WandOfMagicMissile();

			(hero.belongings.weapon = wand).identify();
			hero.belongings.weapon.activate(hero);

			Dungeon.quickslot.setSlot(0, wand);
		}

		new ScrollOfUpgrade().identify();
		new PotionOfLiquidFlame().identify();
	}

	private static void initRogue( Hero hero ) {
		if (!Dungeon.isChallenged(Conducts.Conduct.EVERYTHING)) {
			(hero.belongings.weapon = new Dagger()).identify();

			CloakOfShadows cloak = new CloakOfShadows();
			(hero.belongings.artifact = cloak).identify();
			hero.belongings.artifact.activate(hero);

			ThrowingKnife knives = new ThrowingKnife();
			knives.identify().collect();

			Dungeon.quickslot.setSlot(0, cloak);
			Dungeon.quickslot.setSlot(1, knives);
		}

		new ScrollOfMagicMapping().identify();
		new PotionOfInvisibility().identify();
	}

	private static void initHuntress( Hero hero ) {
		if (!Dungeon.isChallenged(Conducts.Conduct.EVERYTHING)) {

			(hero.belongings.weapon = new Gloves()).identify();
			(hero.belongings.armor = new ScoutArmor()).identify();
			SpiritBow bow = new SpiritBow();
			bow.identify().collect();

			Dungeon.quickslot.setSlot(0, bow);
			Dungeon.quickslot.setSlot(1, hero.belongings.armor);
		}

		new PotionOfMindVision().identify();
		new ScrollOfLullaby().identify();
	}

	private static void initDuelist( Hero hero ) {
		if (!Dungeon.isChallenged(Conducts.Conduct.EVERYTHING)) {

			(hero.belongings.weapon = new Rapier()).identify();
			hero.belongings.weapon.activate(hero);

			ThrowingSpike spikes = new ThrowingSpike();
			spikes.quantity(2).identify().collect(); //set quantity is 3, but Duelist starts with 2

			Dungeon.quickslot.setSlot(0, hero.belongings.weapon);
			Dungeon.quickslot.setSlot(1, spikes);
		}

		new PotionOfStrength().identify();
		new ScrollOfMirrorImage().identify();
	}

	private static void initAdventurer( Hero hero ) {
		hero.HP = hero.HT = 30;
		hero.STR = 12;

		if (!Dungeon.isChallenged(Conducts.Conduct.EVERYTHING)) {

			(hero.belongings.armor = new SyntheticArmor()).identify();

			(hero.belongings.weapon = new Dagger2()).identify();

			ThrowingKnive2 knives = new ThrowingKnive2();
			knives.quantity(2).identify().collect();
			Dungeon.quickslot.setSlot(0, knives);
			new PotionBandolier().collect();
			Dungeon.LimitedDrops.POTION_BANDOLIER.drop();
			new ScrollHolder().collect();
			Dungeon.LimitedDrops.SCROLL_HOLDER.drop();
			new MagicalHolster().collect();
			Dungeon.LimitedDrops.MAGICAL_HOLSTER.drop();
		}
	}

	private static void initConjurer( Hero hero ) {
		(hero.belongings.weapon = new ToyKnife()).identify();

		if (!Dungeon.isChallenged(Conducts.Conduct.EVERYTHING)) {
			ConjurerBook book = new ConjurerBook();
			book.collect();

			FroggitStaff staff1 = new FroggitStaff();
			staff1.identify().collect();

			new MagicalHolster().collect();
			Dungeon.LimitedDrops.MAGICAL_HOLSTER.drop();

			Dungeon.quickslot.setSlot(0, staff1);
		}

		(hero.belongings.armor = new ConjurerArmor()).identify();

		hero.HP = hero.HT = 13;
		if (!Dungeon.isChallenged(Conducts.Conduct.EVERYTHING)) {
			StarBlazing star = new StarBlazing();
			star.collect();
			EnergizedRenewal energizedRenewal = new EnergizedRenewal();
			energizedRenewal.collect();
			new BeamOfAffection().collect();
		}

		new PotionOfStrength().identify();
	}

	private static void initCleric( Hero hero ) {

		(hero.belongings.weapon = new Cudgel()).identify();
		hero.belongings.weapon.activate(hero);

		HolyTome tome = new HolyTome();
		(hero.belongings.artifact = tome).identify();
		hero.belongings.artifact.activate( hero );

		Dungeon.quickslot.setSlot(0, tome);

		new PotionOfPurity().identify();
		new ScrollOfRemoveCurse().identify();
	}

	public String title() {
		return Messages.get(HeroClass.class, name());
	}

	public String desc(){
		return Messages.get(HeroClass.class, name()+"_desc");
	}

	public String shortDesc(){
		return Messages.get(HeroClass.class, name()+"_desc_short");
	}

    public ArrayList<HeroSubClass> subClasses() {
        return new ArrayList<>(Arrays.asList(subClasses));
    }

	public ArmorAbility[] armorAbilities(){
		switch (this) {
			case WARRIOR: default:
				return new ArmorAbility[]{new HeroicLeap(), new Shockwave(), new Endure()};
			case MAGE:
				return new ArmorAbility[]{new ElementalBlast(), new WildMagic(), new WarpBeacon()};
			case ROGUE:
				return new ArmorAbility[]{new SmokeBomb(), new DeathMark(), new ShadowClone()};
			case HUNTRESS:
				return new ArmorAbility[]{new SpectralBlades(), new NaturesPower(), new SpiritHawk()};
			case DUELIST:
				return new ArmorAbility[]{new Challenge(), new ElementalStrike(), new Feint()};
			case ADVENTURER:
				return new ArmorAbility[]{};
			case CLERIC:
				return new ArmorAbility[]{new AscendedForm(), new Trinity(), new PowerOfMany()};
			case CONJURER:
				return new ArmorAbility[]{new Ascension(), new TriadOfPower(), new Hyperblast()};
		}
	}

	public String spritesheet() {
		switch (this) {
			case WARRIOR: default:
				return Assets.Sprites.WARRIOR;
			case MAGE:
				return Assets.Sprites.MAGE;
			case ROGUE:
				return Assets.Sprites.ROGUE;
			case HUNTRESS:
				return Assets.Sprites.HUNTRESS;
			case DUELIST:
				return Assets.Sprites.DUELIST;
			case ADVENTURER:
				return Assets.Sprites.ADVENTURER;
			case CONJURER:
				return Assets.Sprites.CONJURER;
			case CLERIC:
				return Assets.Sprites.CLERIC;
		}
	}

	public String splashArt(){
		switch (this) {
			case WARRIOR: default:
				return Assets.Splashes.WARRIOR;
			case MAGE:
				return Assets.Splashes.MAGE;
			case ROGUE:
				return Assets.Splashes.ROGUE;
			case HUNTRESS:
				return Assets.Splashes.HUNTRESS;
			case DUELIST:
				return Assets.Splashes.DUELIST;
			case ADVENTURER:
				return Assets.Splashes.ADVENTURER;
			case CONJURER:
				return Assets.Splashes.CONJURER;
			case CLERIC:
				return Assets.Splashes.CLERIC;
		}
	}
	
	public boolean isUnlocked() {
		return true;
	}
//		//always unlock on debug builds
//		if (DeviceCompat.isDebug()) return true;
//
//		switch (this){
//			case WARRIOR: default:
//				return true;
//			case MAGE:
//				return Badges.isUnlocked(Badges.Badge.UNLOCK_MAGE);
//			case ROGUE:
//				return Badges.isUnlocked(Badges.Badge.UNLOCK_ROGUE);
//			case HUNTRESS:
//				return Badges.isUnlocked(Badges.Badge.UNLOCK_HUNTRESS);
//			case DUELIST:
//				return Badges.isUnlocked(Badges.Badge.UNLOCK_DUELIST);
//		}
//	}

	public String unlockMsg() {
		return shortDesc() + "\n\n" + Messages.get(HeroClass.class, name()+"_unlock");
	}

}
