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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KingsCrown;
import com.shatteredpixel.shatteredpixeldungeon.items.Ropes;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.LeatherArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MailArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScaleArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfAttunement;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CleanWater;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.treasurebags.AccessoriesBag;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.treasurebags.EquipmentBag;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.treasurebags.HolsterBag;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.treasurebags.PotionsBag;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.treasurebags.ScrollsBag;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.treasurebags.SeedsBag;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.treasurebags.StonesBag;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Alchemize;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.TypeManager;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDetectMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.shop.Jjango;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.shop.Pike;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.shop.Stabber;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.shop.StoneHammer;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.levels.AbyssLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.ArenaLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;

public class ShopRoom extends SpecialRoom {

	protected ArrayList<Item> itemsToSpawn;
	
	@Override
	public int minWidth() {
		return Math.max(7, (int)(Math.sqrt(spacesNeeded())+3));
	}
	
	@Override
	public int minHeight() {
		return Math.max(7, (int)(Math.sqrt(spacesNeeded())+3));
	}

	public int spacesNeeded(){
		if (itemsToSpawn == null){
			if (Dungeon.mode == Dungeon.GameMode.GAUNTLET){
				itemsToSpawn = generateItemsGauntlet();
			}
			else {
				itemsToSpawn = generateItems();
			}
		}

		//sandbags spawn based on current level of an hourglass the player may be holding
		// so, to avoid rare cases of min sizes differing based on that, we ignore all sandbags
		// and then add 4 items in all cases, which is max number of sandbags that can be in the shop
		int spacesNeeded = itemsToSpawn.size();
		for (Item i : itemsToSpawn){
			if (i instanceof TimekeepersHourglass.sandBag){
				spacesNeeded--;
			}
		}
		spacesNeeded += 4;

		//we also add 1 more space, for the shopkeeper
		spacesNeeded++;
		return spacesNeeded;
	}
	
	public void paint( Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY_SP );

		placeShopkeeper( level );

		placeItems( level );
		
		for (Door door : connected.values()) {
			door.set( Door.Type.REGULAR );
		}

	}

	protected void placeShopkeeper( Level level ) {

		int pos = level.pointToCell(center());

		Mob shopkeeper = new Shopkeeper();
		shopkeeper.pos = pos;
		level.mobs.add( shopkeeper );

	}

	protected void placeItems( Level level ){

		if (itemsToSpawn == null){
			if (level instanceof ArenaLevel){
				itemsToSpawn = generateItemsGauntlet();
			}
			else {
				itemsToSpawn = generateItems();
			}
		}

		Point entryInset = new Point(entrance());
		if (entryInset.y == top){
			entryInset.y++;
		} else if (entryInset.y == bottom) {
			entryInset.y--;
		} else if (entryInset.x == left){
			entryInset.x++;
		} else {
			entryInset.x--;
		}

		Point curItemPlace = entryInset.clone();

		int inset = 1;

		for (Item item : itemsToSpawn.toArray(new Item[0])) {

			//place items in a clockwise pattern
			if (curItemPlace.x == left+inset && curItemPlace.y != top+inset){
				curItemPlace.y--;
			} else if (curItemPlace.y == top+inset && curItemPlace.x != right-inset){
				curItemPlace.x++;
			} else if (curItemPlace.x == right-inset && curItemPlace.y != bottom-inset){
				curItemPlace.y++;
			} else {
				curItemPlace.x--;
			}

			//once we get to the inset from the entrance again, move another cell inward and loop
			if (curItemPlace.equals(entryInset)){

				if (entryInset.y == top+inset){
					entryInset.y++;
				} else if (entryInset.y == bottom-inset){
					entryInset.y--;
				}
				if (entryInset.x == left+inset){
					entryInset.x++;
				} else if (entryInset.x == right-inset){
					entryInset.x--;
				}
				inset++;

				if (inset > (Math.min(width(), height())-3)/2){
					break; //out of space!
				}

				curItemPlace = entryInset.clone();

				//make sure to step forward again
				if (curItemPlace.x == left+inset && curItemPlace.y != top+inset){
					curItemPlace.y--;
				} else if (curItemPlace.y == top+inset && curItemPlace.x != right-inset){
					curItemPlace.x++;
				} else if (curItemPlace.x == right-inset && curItemPlace.y != bottom-inset){
					curItemPlace.y++;
				} else {
					curItemPlace.x--;
				}
			}

			int cell = level.pointToCell(curItemPlace);
			//prevents high grass from being trampled, potentially dropping dew/seeds onto shop items
			if (level.map[cell] == Terrain.HIGH_GRASS){
				Level.set(cell, Terrain.GRASS, level);
				GameScene.updateMap(cell);
			}
			level.drop( item, cell ).type = Heap.Type.FOR_SALE;
			itemsToSpawn.remove(item);
		}

		//we didn't have enough space to place everything neatly, so now just fill in anything left
		if (!itemsToSpawn.isEmpty()){
			for (Point p : getPoints()){
				int cell = level.pointToCell(p);
				if ((level.map[cell] == Terrain.EMPTY_SP || level.map[cell] == Terrain.EMPTY)
						&& level.heaps.get(cell) == null && level.findMob(cell) == null){
					level.drop( itemsToSpawn.remove(0), level.pointToCell(p) ).type = Heap.Type.FOR_SALE;
				}
				if (itemsToSpawn.isEmpty()){
					break;
				}
			}
		}

		if (!itemsToSpawn.isEmpty()){
			ShatteredPixelDungeon.reportException(new RuntimeException("failed to place all items in a shop!"));
		}

	}

	protected static int shopLevel() {
		if (Dungeon.mode == Dungeon.GameMode.BIGGER)
			return Dungeon.scalingDepth() / 5;
		else
			return Dungeon.chapterNumber()-1;
	}

	protected static ArrayList<Item> generateItems() {

		ArrayList<Item> itemsToSpawn = new ArrayList<>();

		if (Dungeon.mode == Dungeon.GameMode.ABYSS_START && Dungeon.branch != AbyssLevel.BRANCH){
			//just spawn a couple of bags
			for (int i = 0; i < 2; i++) {
				itemsToSpawn.add(new SeedsBag());
				itemsToSpawn.add(new StonesBag());
				itemsToSpawn.add(new PotionsBag());
				itemsToSpawn.add(new ScrollsBag());
				itemsToSpawn.add(new EquipmentBag());
				itemsToSpawn.add(new HolsterBag());
				itemsToSpawn.add(new AccessoriesBag());
			}
			itemsToSpawn.add(new Amulet());

		} else {
			MeleeWeapon w = null;
            MissileWeapon m = (MissileWeapon) Generator.random(Generator.Category.MISSILE);
			if (shopLevel() == 1) {
				w = (MeleeWeapon) Generator.random(Generator.wepTiers[1]);
				itemsToSpawn.add(new LeatherArmor().identify(false));
			} else if (shopLevel() == 2) {
				w = (MeleeWeapon) Generator.random(Generator.wepTiers[2]);
				itemsToSpawn.add(new MailArmor().identify(false));
			} else if (shopLevel() == 3) {
				w = (MeleeWeapon) Generator.random(Generator.wepTiers[3]);
				itemsToSpawn.add(new ScaleArmor().identify(false));
			} else if (shopLevel() == 4 || Dungeon.depth == 20) {
				w = (MeleeWeapon) Generator.random(Generator.wepTiers[4]);
				itemsToSpawn.add(new PlateArmor().identify(false));
				itemsToSpawn.add(new Torch());
				itemsToSpawn.add(new Torch());
				itemsToSpawn.add(new Torch());
			}
			if (Dungeon.scalingDepth() > 26) {
				w = (MeleeWeapon) Generator.random(Generator.wepTiers[4]);
				itemsToSpawn.add(ClassArmor.upgrade(Dungeon.hero, new PlateArmor()));
				itemsToSpawn.add(Generator.randomUsingDefaults(Generator.Category.POTION));
				itemsToSpawn.add(Generator.randomUsingDefaults(Generator.Category.SCROLL));
			}
			if (w != null) {
				w.enchant(null);
				w.cursed = false;
				w.level(0);
				w.identify(false);
				itemsToSpawn.add(w);
			}
            if (m != null){
                m.enchant(null);
                m.cursed = false;
                m.level(0);
                m.identify(false);
                itemsToSpawn.add(m);
            }

			if (Random.Float() < 0.6) itemsToSpawn.add(ChooseShopWeapon());

			Bag bag = ChooseBag(Dungeon.hero.belongings);
			if (bag != null) {
				itemsToSpawn.add(bag);
			}

			itemsToSpawn.add(new PotionOfHealing());
			itemsToSpawn.add(Generator.randomUsingDefaults(Generator.Category.POTION));
			itemsToSpawn.add(Generator.randomUsingDefaults(Generator.Category.POTION));

			itemsToSpawn.add(new ScrollOfIdentify());
			itemsToSpawn.add(new ScrollOfRemoveCurse());
			itemsToSpawn.add(new ScrollOfMagicMapping());

			for (int i = 0; i < 2; i++)
				itemsToSpawn.add(Random.Int(2) == 0 ?
						Generator.randomUsingDefaults(Generator.Category.POTION) :
						Generator.randomUsingDefaults(Generator.Category.SCROLL));


			switch (Random.Int(4)) {
				case 0:
					itemsToSpawn.add(new Bomb());
					break;
				case 1:
				case 2:
					itemsToSpawn.add(new Bomb.DoubleBomb());
					break;
				case 3:
					itemsToSpawn.add(new Honeypot());
					break;
			}

			Item rare;
			switch (Random.Int(10)) {
				case 0:
					rare = Generator.random(Generator.Category.WAND);
					rare.level(0);
					break;
				case 1:
					rare = Generator.random(Generator.Category.RING);
					rare.level(0);
					break;
				case 2:
					rare = Generator.random(Generator.Category.ARTIFACT);
					break;
				case 3:
					rare = Generator.randomStaff();
					break;
				default:
					rare = new Stylus();
			}
			rare.cursed = false;
			rare.cursedKnown = true;
			itemsToSpawn.add(rare);
		}

		itemsToSpawn.add(TippedDart.randomTipped(2));

		itemsToSpawn.add(new Alchemize().quantity(Random.IntRange(2, 3)));

		itemsToSpawn.add(new Ropes().quantity(Random.Int(3, 10)));

		itemsToSpawn.add(new SmallRation());
		itemsToSpawn.add(new SmallRation());

		itemsToSpawn.add(new Ankh());
		itemsToSpawn.add(new StoneOfAugmentation());
		itemsToSpawn.add(new CleanWater());

		TimekeepersHourglass hourglass = Dungeon.hero.belongings.getItem(TimekeepersHourglass.class);
		if (hourglass != null && hourglass.isIdentified() && !hourglass.cursed) {
			int bags = 0;
			//creates the given float percent of the remaining bags to be dropped.
			//this way players who get the hourglass late can still max it, usually.
			if (shopLevel() == 1)
					bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.20f);
			else if (shopLevel() == 2)
					bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.25f);
			else if (shopLevel() == 3)
					bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.50f);
			else if (shopLevel() >= 4)
					bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.80f);

			for (int i = 1; i <= bags; i++) {
				itemsToSpawn.add(new TimekeepersHourglass.sandBag());
				hourglass.sandBags++;
			}
		}

		//use a new generator here to prevent items in shop stock affecting levelgen RNG (e.g. sandbags)
		//we can use a random long for the seed as it will be the same long every time
		Random.pushGenerator(Random.Long());
			Random.shuffle(itemsToSpawn);
		Random.popGenerator();

		return itemsToSpawn;
	}

	protected static ArrayList<Item> generateItemsGauntlet(){
		ArrayList<Item> itemsToSpawn = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			itemsToSpawn.add(Generator.random(Generator.Category.POTION).identify());

			// do not add useless items
			Item scroll;
			do {
				scroll = Generator.random(Generator.Category.SCROLL).identify();
			} while (scroll instanceof ScrollOfIdentify);
			itemsToSpawn.add(scroll);

			Item stone;
			do {
				stone = Generator.random(Generator.Category.STONE).identify();
			} while (stone instanceof StoneOfDetectMagic || stone instanceof StoneOfIntuition);
			itemsToSpawn.add(stone);
		}

		if (Dungeon.depth % 4 == 0) itemsToSpawn.add( TippedDart.randomTipped(2) );

		itemsToSpawn.add (new Ropes().quantity(Random.Int(2, 6)));

		if (Dungeon.depth % 2 == 0) itemsToSpawn.add( new ScrollOfUpgrade().identify());
		if (Dungeon.depth % 3 == 0) itemsToSpawn.add( new PotionOfStrength().identify());
		if (Dungeon.depth % 3 == 0) itemsToSpawn.add( new TypeManager());
		if (Dungeon.depth % 3 == 0) itemsToSpawn.add(new CleanWater());
		if (Dungeon.depth % 5 == 0) itemsToSpawn.add( new ElixirOfAttunement());
		if (Dungeon.depth % 2 == 0) itemsToSpawn.add( Generator.random(Generator.Category.MISSILE).identify());
		if (Dungeon.depth == Dungeon.chapterSize()*5+1) itemsToSpawn.add(new Amulet());
		if (Dungeon.hero.lvl >= 12 && Dungeon.hero.subClass == HeroSubClass.NONE && Dungeon.hero.heroClass.subClasses().size() > 1) itemsToSpawn.add( new TengusMask());
		if (Dungeon.hero.lvl >= 21 && Dungeon.hero.belongings.armor != null &&
				Dungeon.hero.armorAbility == null && Dungeon.hero.heroClass.armorAbilities().length > 0) itemsToSpawn.add( new KingsCrown());

		Item rare;
		switch (Random.Int(6)){
			case 0:
				rare = Generator.randomUsingDefaults( Generator.Category.WAND );
				break;
			case 1:
				rare = Generator.randomUsingDefaults( Generator.Category.ARTIFACT );
				break;
			case 2:
				rare = Generator.randomWeapon();
				break;
			case 3:
				rare = Generator.randomArmor();
				break;
			case 4:
				rare = Generator.randomStaff();
				break;
			case 5:
				rare = Generator.randomUsingDefaults(Generator.Category.RING);
				break;
			default:
				rare = new Dewdrop();
		}
		rare.identify();
		itemsToSpawn.add( rare );
		itemsToSpawn.add( new Bomb().random() );
		if (Random.Int(4) == 0){
			Item additionalRare;
			switch (Dungeon.hero.heroClass){
				case WARRIOR:
					additionalRare = Generator.randomArmor(); break;
				case MAGE:
					additionalRare = Generator.random(Generator.Category.WAND); break;
				case ROGUE:
					additionalRare = Generator.random(Generator.Category.RING); break;
				case DUELIST:
					additionalRare = Generator.randomWeapon(); break;
				case HUNTRESS:
					additionalRare = Generator.random(Generator.Category.MISSILE); break;
				case CONJURER:
					additionalRare = Generator.randomStaff(); break;
				case ADVENTURER:
					additionalRare = Generator.random(); break;
				default:
					additionalRare = new Dewdrop();
			}
			additionalRare.identify();
			itemsToSpawn.add( additionalRare );
		}

		if (Dungeon.depth % 6 == 0){

            Bag chosenBag = ChooseBag(Dungeon.hero.belongings);
            if (chosenBag != null)
                itemsToSpawn.add(chosenBag);
        }

		TimekeepersHourglass hourglass = Dungeon.hero.belongings.getItem(TimekeepersHourglass.class);
		if (hourglass != null && hourglass.isIdentified() && !hourglass.cursed){
			int bags = 0;
			//creates the given float percent of the remaining bags to be dropped.
			//this way players who get the hourglass late can still max it, usually.
			if (shopLevel() == 1)
				bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.20f);
			else if (shopLevel() == 2)
				bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.25f);
			else if (shopLevel() == 3)
				bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.50f);
			else if (shopLevel() >= 4)
				bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.80f);

			for(int k = 1; k <= bags; k++){
				itemsToSpawn.add( new TimekeepersHourglass.sandBag());
				hourglass.sandBags ++;
			}
		}

		Random.pushGenerator(Random.Long());
		Random.shuffle(itemsToSpawn);
		Random.popGenerator();

		ScrollOfRemoveCurse.uncurse(Dungeon.hero, itemsToSpawn.toArray(new Item[0]));

		return itemsToSpawn;
	}

	protected static Bag ChooseBag(Belongings pack){

		//generate a hashmap of all valid bags.
		HashMap<Bag, Integer> bags = new HashMap<>();
		if (!Dungeon.LimitedDrops.VELVET_POUCH.dropped()) bags.put(new VelvetPouch(), 1);
		if (!Dungeon.LimitedDrops.SCROLL_HOLDER.dropped()) bags.put(new ScrollHolder(), 0);
		if (!Dungeon.LimitedDrops.POTION_BANDOLIER.dropped()) bags.put(new PotionBandolier(), 0);
		if (!Dungeon.LimitedDrops.MAGICAL_HOLSTER.dropped()) bags.put(new MagicalHolster(), 0);

		if (bags.isEmpty()) return null;

		//count up items in the main bag
		for (Item item : pack.backpack.items) {
			for (Bag bag : bags.keySet()){
				if (bag.canHold(item)){
					bags.put(bag, bags.get(bag)+1);
				}
			}
		}

		//find which bag will result in most inventory savings, drop that.
		Bag bestBag = null;
		for (Bag bag : bags.keySet()){
			if (bestBag == null){
				bestBag = bag;
			} else if (bags.get(bag) > bags.get(bestBag)){
				bestBag = bag;
			}
		}

		if (bestBag instanceof VelvetPouch){
			Dungeon.LimitedDrops.VELVET_POUCH.drop();
		} else if (bestBag instanceof ScrollHolder){
			Dungeon.LimitedDrops.SCROLL_HOLDER.drop();
		} else if (bestBag instanceof PotionBandolier){
			Dungeon.LimitedDrops.POTION_BANDOLIER.drop();
		} else if (bestBag instanceof MagicalHolster){
			Dungeon.LimitedDrops.MAGICAL_HOLSTER.drop();
		}

		return bestBag;

	}

	protected static Weapon ChooseShopWeapon(){
		MeleeWeapon wepToReplace = Generator.randomWeapon();
		MeleeWeapon shopWeapon;
		switch (wepToReplace.tier){
			case 2:
				shopWeapon = new StoneHammer(); break;
			case 3:
				shopWeapon = new Pike(); break;
			case 4:
				shopWeapon = new Stabber(); break;
			case 5:
				shopWeapon = new Jjango(); break;
			default:
				shopWeapon = new WornShortsword(); break;
		}
		if (!wepToReplace.hasCurseEnchant()) shopWeapon.enchantment = wepToReplace.enchantment;
		shopWeapon.augment = wepToReplace.augment;
		shopWeapon.cursed = false;
		shopWeapon.identify();
		shopWeapon.level(wepToReplace.level());
		return shopWeapon;
	}

}
