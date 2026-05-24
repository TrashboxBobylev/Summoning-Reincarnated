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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.InventoryStone;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlink;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfClairvoyance;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDeepSleep;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDetectMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfFear;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfFlock;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfShock;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Mageroyal;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashMap;

public class SandalsOfNature extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_SANDALS;

		levelCap = 3;

		charge = 0;
		chargeCap = 100;

		defaultAction = AC_ROOT;
	}

	public static final String AC_FEED = "FEED";
	public static final String AC_ROOT = "ROOT";

	public ArrayList<Class> consumables = new ArrayList<>();
	public Class curConsumableEffect = null;

	private static final HashMap<Class<? extends Item>, Integer> consumableColors = new HashMap<>();
	static {
		consumableColors.put(Rotberry.Seed.class,     0xCC0022);
		consumableColors.put(Firebloom.Seed.class,    0xFF7F00);
		consumableColors.put(Swiftthistle.Seed.class, 0xCCBB00);
		consumableColors.put(Sungrass.Seed.class,     0x2EE62E);
		consumableColors.put(Icecap.Seed.class,       0x66B3FF);
		consumableColors.put(Stormvine.Seed.class,    0x195D80);
		consumableColors.put(Sorrowmoss.Seed.class,   0xA15CE5);
		consumableColors.put(Mageroyal.Seed.class,    0xFF4CD2);
		consumableColors.put(Earthroot.Seed.class,    0x67583D);
		consumableColors.put(Starflower.Seed.class,   0x404040);
		consumableColors.put(Fadeleaf.Seed.class,     0x919999);
		consumableColors.put(Blindweed.Seed.class,    0XD9D9D9);

		consumableColors.put(StoneOfEnchantment.class,  0x303030);
		consumableColors.put(StoneOfIntuition.class,  	0x00A0FF);
		consumableColors.put(StoneOfDetectMagic.class,	0xFFFFFF);
		consumableColors.put(StoneOfFlock.class,    	0x99EEFF);
		consumableColors.put(StoneOfShock.class,		0xFFFF00);
		consumableColors.put(StoneOfBlink.class, 		0xB47FFE);
		consumableColors.put(StoneOfDeepSleep.class,    0x00FEFF);
		consumableColors.put(StoneOfClairvoyance.class, 0xA3FFE8);
		consumableColors.put(StoneOfAggression.class,   0xFF1A1A);
		consumableColors.put(StoneOfBlast.class, 		0x808080);
		consumableColors.put(StoneOfFear.class,			0x800D0D);
		consumableColors.put(StoneOfAugmentation.class, 0x303030);
	}

	private static final HashMap<Class<? extends Item>, Integer> consumableChargeReqs = new HashMap<>();
	static {
		consumableChargeReqs.put(Rotberry.Seed.class,     8);
		consumableChargeReqs.put(Firebloom.Seed.class,    20);
		consumableChargeReqs.put(Swiftthistle.Seed.class, 20);
		consumableChargeReqs.put(Sungrass.Seed.class,     80);
		consumableChargeReqs.put(Icecap.Seed.class,       20);
		consumableChargeReqs.put(Stormvine.Seed.class,    20);
		consumableChargeReqs.put(Sorrowmoss.Seed.class,   20);
		consumableChargeReqs.put(Mageroyal.Seed.class,    12);
		consumableChargeReqs.put(Earthroot.Seed.class,    40);
		consumableChargeReqs.put(Starflower.Seed.class,   40);
		consumableChargeReqs.put(Fadeleaf.Seed.class,     12);
		consumableChargeReqs.put(Blindweed.Seed.class,    12);

		consumableChargeReqs.put(StoneOfEnchantment.class,  100);
		consumableChargeReqs.put(StoneOfIntuition.class,  	12);
		consumableChargeReqs.put(StoneOfDetectMagic.class,	12);
		consumableChargeReqs.put(StoneOfFlock.class,    	20);
		consumableChargeReqs.put(StoneOfShock.class,		20);
		consumableChargeReqs.put(StoneOfBlink.class, 		12);
		consumableChargeReqs.put(StoneOfDeepSleep.class,    12);
		consumableChargeReqs.put(StoneOfClairvoyance.class, 40);
		consumableChargeReqs.put(StoneOfAggression.class,   30);
		consumableChargeReqs.put(StoneOfBlast.class, 		30);
		consumableChargeReqs.put(StoneOfFear.class,			12);
		consumableChargeReqs.put(StoneOfAugmentation.class, 75);
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (hero.buff(MagicImmune.class) != null){
			return actions;
		}
		if (isEquipped( hero ) && !cursed) {
			actions.add(AC_FEED);
		}
		if (isEquipped( hero )
				&& !cursed
				&& curConsumableEffect != null
				&& charge >= consumableChargeReqs.get(curConsumableEffect)) {
			actions.add(AC_ROOT);
		}
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {
		super.execute(hero, action);

		if (hero.buff(MagicImmune.class) != null) return;

		if (action.equals(AC_FEED)){

			GameScene.selectItem(itemSelector);

		} else if (action.equals(AC_ROOT) && !cursed){

			if (!isEquipped( hero ))                                GLog.i( Messages.get(Artifact.class, "need_to_equip") );
			else {
				if (type() == 3){
					if (charge == 0)    GLog.i( Messages.get(this, "no_charge") );
					else {
						Buff.prolong(hero, Roots.class, Roots.DURATION);
						Buff.affect(hero, Earthroot.Armor.class).level(charge);
						CellEmitter.bottom(hero.pos).start(EarthParticle.FACTORY, 0.05f, 8);
						Camera.main.shake(1, 0.4f);
						charge = 0;
						Talent.onArtifactUsed(Dungeon.hero);
						updateQuickslot();
					}
				} else {
					if (curConsumableEffect == null)                         GLog.i( Messages.get(this, "no_effect") );
					else if (charge < consumableChargeReqs.get(curConsumableEffect))    GLog.i( Messages.get(this, "low_charge") );
					else {
						GameScene.selectCell(cellSelector);
					}
				}
			}
		}
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new Naturalism();
	}
	
	@Override
	public void charge(Hero target, float amount) {
		if (cursed || target.buff(MagicImmune.class) != null) return;
		if (charge < chargeCap) {
			partialCharge += 2*amount*rechargeModifier();
			while (partialCharge >= 1f){
				charge++;
				partialCharge--;
			}
			if (charge >= chargeCap) {
				charge = chargeCap;
				partialCharge = 0;
			}
			updateQuickslot();
		}
	}

	@Override
	public ItemSprite.Glowing glowing() {
		if (curConsumableEffect != null){
			return new ItemSprite.Glowing(consumableColors.get(curConsumableEffect));
		}
		return null;
	}

	@Override
	public String name() {
		if (level() == 0)   return super.name();
		else                return Messages.get(this, "name_" + level());
	}

	@Override
	public String desc() {
		String desc = Messages.get(this, "desc_" + (level()+1));

		if ( isEquipped ( Dungeon.hero ) ) {
			desc += "\n\n";

			if (!cursed) {
				desc += getTypeBasedString( "desc_hint", type());
			} else {
				desc += Messages.get(this, "desc_cursed");
			}

		}

		if (curConsumableEffect != null){
				desc += "\n\n" + getTypeBasedString("desc_ability", type(),
					Messages.titleCase(Messages.get(curConsumableEffect, "name")),
					consumableChargeReqs.get(curConsumableEffect));
		}

		if (!consumables.isEmpty()){
			desc += "\n\n" + getTypeBasedString("desc_seeds", type(), consumables.size());
		}

		return desc;
	}

	@Override
	public String getTypeMessage(int type) {
		return Messages.get(this, "type",
				Math.round(100*rechargeModifier(type))) + "\n\n" + super.getTypeMessage(type);
	}

	@Override
	public Item upgrade() {
		if (level() < 0)        image = ItemSpriteSheet.ARTIFACT_SANDALS;
		else if (level() == 0)  image = ItemSpriteSheet.ARTIFACT_SHOES;
		else if (level() == 1)  image = ItemSpriteSheet.ARTIFACT_BOOTS;
		else if (level() >= 2)  image = ItemSpriteSheet.ARTIFACT_GREAVES;
		return super.upgrade();
	}

	public boolean canUseSeed(Item item){
		boolean isValidType;
		switch (type()){
			case 2:
				isValidType = item instanceof Runestone;
				break;
			default:
				isValidType = item instanceof Plant.Seed;
		}

		return isValidType && !consumables.contains(item.getClass())
				&& (level() < 3 || curConsumableEffect != item.getClass());
	}

	@Override
	public void resetForTrinity(int visibleLevel) {
		super.reset();
		curConsumableEffect = null;
	}

	private static final String SEEDS = "seeds";
	private static final String CUR_SEED_EFFECT = "cur_seed_effect";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put(SEEDS, consumables.toArray(new Class[0]));
		bundle.put(CUR_SEED_EFFECT, curConsumableEffect);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		consumables.clear();
		if (bundle.contains(SEEDS) && bundle.getClassArray(SEEDS) != null) {
			for (Class<?> seed : bundle.getClassArray(SEEDS)) {
				if (seed != null) consumables.add(seed);
			}
		}
		curConsumableEffect = bundle.getClass(CUR_SEED_EFFECT);

		if (level() == 1)  image = ItemSpriteSheet.ARTIFACT_SHOES;
		else if (level() == 2)  image = ItemSpriteSheet.ARTIFACT_BOOTS;
		else if (level() >= 3)  image = ItemSpriteSheet.ARTIFACT_GREAVES;
	}

	public float rechargeModifier(){
		return rechargeModifier(type());
	}

	public float rechargeModifier(int type){
		switch (type){
			case 1:
				return 1.0f;
			case 2:
				return 0.5f;
			case 3:
				return 1.33f;
		}
		return 1.0f;
	}

	public class Naturalism extends ArtifactBuff{
		public void charge() {
			if (cursed || target.buff(MagicImmune.class) != null) return;
			if (charge < chargeCap){
				//0.5 charge per grass at +0, up to 1 at +10
				float chargeGain = (3f + level())/6f;
				chargeGain *= RingOfEnergy.artifactChargeMultiplier(target);
				chargeGain *= rechargeModifier();
				partialCharge += Math.max(0, chargeGain);
				while (partialCharge >= 1){
					charge++;
					partialCharge--;
				}
				charge = Math.min(charge, chargeCap);
				updateQuickslot();
			}
		}
	}

	protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(SandalsOfNature.class, "prompt");
		}

		@Override
		public Class<?extends Bag> preferredBag(){
			return VelvetPouch.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return canUseSeed(item);
		}

		@Override
		public void onSelect( Item item ) {
			if (item != null && (type() == 2 ? item instanceof Runestone : item instanceof Plant.Seed)) {
				if (level() < 3) consumables.add(0, item.getClass());
				curConsumableEffect = item.getClass();

				Hero hero = Dungeon.hero;
				hero.sprite.operate( hero.pos );
				Sample.INSTANCE.play( Assets.Sounds.PLANT );
				hero.busy();
				hero.spend( Actor.TICK );
				if (consumables.size() >= 3+(level()*3)){
					consumables.clear();
					upgrade();
					Catalog.countUses(SandalsOfNature.class, level() == 3 ? 4 : 3);
					if (level() >= 1 && level() <= 3) {
						GLog.p( Messages.get(SandalsOfNature.class, "levelup") );
					}

				} else {
					GLog.i( getTypeBasedString( "absorb_seed", type()) );
				}
				item.detach(hero.belongings.backpack);
			}
		}
	};

	public CellSelector.Listener cellSelector = new CellSelector.Listener(){

		@Override
		public void onSelect(Integer cell) {
			if (cell != null){

				if (!Dungeon.level.heroFOV[cell] || Dungeon.level.distance(curUser.pos, cell) > (type() == 2 ? 4 : 3)){
					GLog.w(Messages.get(SandalsOfNature.class, "out_of_range"));
				} else {

					Ballistica aim = new Ballistica(curUser.pos, cell, Ballistica.STOP_TARGET);
					for (int c : aim.subPath(0, aim.dist)){
						CellEmitter.get( c ).burst( type() == 2 ? EarthParticle.FALLING : LeafParticle.GENERAL, 6 );
					}

					Splash.at(DungeonTilemap.tileCenterToWorld( cell ), -PointF.PI/2, PointF.PI/2, consumableColors.get(curConsumableEffect), 6);
					Invisibility.dispel(curUser);

					if (type() != 2) {
						Plant plant = ((Plant.Seed) Reflection.newInstance(curConsumableEffect)).couch(cell, null);
						plant.activate(Actor.findChar(cell));
						Sample.INSTANCE.play(Assets.Sounds.PLANT);
					} else {
						Runestone item = (Runestone) Reflection.newInstance(curConsumableEffect);
						item.anonymize();
						if (item instanceof InventoryStone){
							curItem = item;
							((InventoryStone) item).directActivate();
						} else {
							item.activate(cell);
							if (Actor.findChar(cell) == null) Dungeon.level.pressCell( cell );
						}
					}
					Sample.INSTANCE.playDelayed(Assets.Sounds.TRAMPLE, 0.25f, 1, Random.Float( 0.96f, 1.05f ) );

					if (Actor.findChar(cell) != null){
						artifactProc(Actor.findChar(cell), visiblyUpgraded(), consumableChargeReqs.get(curConsumableEffect));
					}

					charge -= consumableChargeReqs.get(curConsumableEffect);
					Talent.onArtifactUsed(Dungeon.hero);
					updateQuickslot();
					curUser.spendAndNext(1f);
				}
			}
		}

		@Override
		public String prompt() {
			return Messages.get(SandalsOfNature.class, "prompt_target");
		}
	};

}
