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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.AlchemyScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class AlchemistsToolkit extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_TOOLKIT;

		levelCap = 10;
		
		charge = 0;
		partialCharge = 0;
	}

	public static final String AC_BREW = "BREW";
	public static final String AC_ENERGIZE = "ENERGIZE";
	public static final String AC_ADD = "ADD";
	public static final String AC_CREATE = "CREATE";

	private float warmUpDelay;

	private final ArrayList<Class> potions = new ArrayList<>();

	public AlchemistsToolkit() {
		super();

		setupPotions();
	}

	private void setupPotions(){
		potions.clear();

		Class<?>[] potionClasses = Generator.Category.POTION.classes;
		float[] probs = Generator.Category.POTION.defaultProbsTotal.clone(); //array of primitives, clone gives deep copy.
		int i = Random.chances(probs);

		while (i != -1){
			potions.add(potionClasses[i]);
			probs[i] = 0;

			i = Random.chances(probs);
		}
		potions.remove(PotionOfExperience.class);
	}

	private void preparePotions() {
		while (!potions.isEmpty() && potions.size() > (levelCap - 1 - level())) {
			potions.remove(0);
		}
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped( hero ) && !cursed && hero.buff(MagicImmune.class) == null) {
			if (type() == 1) {
				actions.add(AC_BREW);
				if (level() < levelCap) {
					actions.add(AC_ENERGIZE);
				}
			}
			if (type() == 2){
				if (charge > 0){
					actions.add(AC_CREATE);
				}
				if (level() < levelCap) {
					actions.add(AC_ADD);
				}
			}
			if (type() == 3){
				if (level() < levelCap) {
					actions.add(AC_ENERGIZE);
				}
			}
		}
		return actions;
	}

	@Override
	public String defaultAction() {
		switch (type()){
			case 1:
				return AC_BREW;
			case 2:
				return AC_CREATE;
			case 3:
				return null;
		}
		return super.defaultAction();
	}

	@Override
	public void execute(Hero hero, String action ) {

		super.execute(hero, action);

		if (hero.buff(MagicImmune.class) != null) return;

        switch (action) {
            case AC_BREW:
                if (!isEquipped(hero)) GLog.i(Messages.get(this, "need_to_equip"));
                else if (cursed) GLog.w(Messages.get(this, "cursed"));
                else if (warmUpDelay > 0 || type() == 3) GLog.w(Messages.get(this, "not_ready"));
                else {
                    AlchemyScene.assignToolkit(this);
                    Game.switchScene(AlchemyScene.class);
                }

                break;
            case AC_ENERGIZE:
				int energy = type() == 3 ? 7 : 6;
                if (!isEquipped(hero)) GLog.i(Messages.get(this, "need_to_equip"));
                else if (cursed) GLog.w(Messages.get(this, "cursed"));
                else if (Dungeon.energy < energy) GLog.w(Messages.get(this, "need_energy", energy));
                else {

                    final int maxLevels = Math.min(levelCap - level(), Dungeon.energy / energy);

                    String[] options;
                    if (maxLevels > 1) {
                        options = new String[]{Messages.get(this, "energize_1"), Messages.get(this, "energize_all", energy * maxLevels, maxLevels)};
                    } else {
                        options = new String[]{Messages.get(this, "energize_1")};
                    }

                    GameScene.show(new WndOptions(new ItemSprite(image),
                            Messages.titleCase(name()),
                            Messages.get(this, "energize_desc"),
                            options) {
                        @Override
                        protected void onSelect(int index) {
                            super.onSelect(index);

                            if (index == 0) {
                                Dungeon.energy -= energy;
                                Sample.INSTANCE.play(Assets.Sounds.DRINK);
                                Sample.INSTANCE.playDelayed(Assets.Sounds.PUFF, 0.5f);
                                Dungeon.hero.sprite.operate(Dungeon.hero.pos);
                                upgrade();
                                Catalog.countUse(AlchemistsToolkit.class);
                            } else if (index == 1) {
                                Dungeon.energy -= 6 * maxLevels;
                                Sample.INSTANCE.play(Assets.Sounds.DRINK);
                                Sample.INSTANCE.playDelayed(Assets.Sounds.PUFF, 0.5f);
                                Dungeon.hero.sprite.operate(Dungeon.hero.pos);
                                upgrade(maxLevels);
                                Catalog.countUses(AlchemistsToolkit.class, maxLevels);
                            }

                        }

                        @Override
                        protected boolean hasIcon(int index) {
                            return true;
                        }

                        @Override
                        protected Image getIcon(int index) {
                            return new ItemSprite(ItemSpriteSheet.ENERGY);
                        }
                    });
                }
                break;
            case AC_ADD:
                GameScene.selectItem(itemSelector);
                break;
            case AC_CREATE:
                if (!isEquipped(hero)) GLog.i(Messages.get(this, "need_to_equip"));
                else if (charge <= 0) GLog.i(Messages.get(this, "no_charge"));
                else if (cursed) GLog.w(Messages.get(this, "cursed"));
                else {
                    doPotionEffect();
                }

                break;
        }

		updateQuickslot();
	}

	public static class ExploitHandler extends Buff {
		{ actPriority = VFX_PRIO; }

		public Potion potion = null;

		@Override
		public boolean act() {
			curUser = Dungeon.hero;
			if (potion != null) {
				curItem = potion;
				potion.anonymize();
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						handlePotion(curUser, potion, curUser.pos);
						Item.updateQuickslot();
					}
				});
			}
			detach();
			return true;
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( "potion", potion );
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			potion = (Potion) bundle.get("potion");
		}
	}

	public void doPotionEffect(){
		charge--;

		Potion potion;
		do {
			potion = (Potion) Generator.randomUsingDefaults(Generator.Category.POTION);
		} while (potion == null
				//reduce the frequency of these potions by half
				||((potion instanceof PotionOfHealing ||
				potion instanceof PotionOfMindVision ||
				potion instanceof PotionOfLiquidFlame) && Random.Int(2) == 0)
				//cannot roll transmutation
				|| (potion instanceof PotionOfExperience));

		Class<? extends Potion> potionCls = potion.getClass();

		potion.anonymize();
		curItem = potion;
		curUser = Dungeon.hero;

		//if there are charges left and the scroll has been given to the book
		if (charge > 0 && !potions.contains(potionCls)) {
			final Potion fPotion = potion;

			final ExploitHandler handler = Buff.affect(curUser, ExploitHandler.class);
			handler.potion = potion;
			Class<?> goodPotion = ExoticPotion.regToExo.get(potionCls);

			GameScene.show(new WndOptions(new ItemSprite(this),
					Messages.get(this, "prompt_potion"),
					getTypeBasedString("create_empowered", type()),
					potion.trueName(),
					Messages.get(goodPotion, "name")){
				@Override
				protected void onSelect(int index) {
					handler.detach();
					if (index == 1){
						Potion pot = (Potion) Reflection.newInstance(goodPotion);
						curItem = pot;
						charge--;
						pot.anonymize();
						GameScene.selectCell(cellSelector);
						Talent.onArtifactUsed(Dungeon.hero);
					} else {
						GameScene.selectCell(cellSelector);
						Talent.onArtifactUsed(Dungeon.hero);
					}
					updateQuickslot();
				}

				@Override
				public void onBackPressed() {
					//do nothing
				}

				@Override
				protected boolean hasIcon(int index) {
					return true;
				}

				@Override
				protected Image getIcon(int index) {
					switch (index){
						case 0:
							return new ItemSprite(fPotion);
						case 1:
							return new ItemSprite((Item) Reflection.newInstance(goodPotion));
					}
					return super.getIcon(index);
				}

				@Override
				protected boolean hasInfo(int index) {
					return true;
				}

				@Override
				protected void onInfo(int index) {
					Potion potionInfo = null;
					switch (index){
						case 0:
							potionInfo = fPotion;
							break;
						case 1:
							potionInfo = (Potion) Reflection.newInstance(goodPotion);
							break;
					}
					GameScene.show(new WndTitledMessage(
							new ItemSprite(potionInfo),
							Messages.titleCase(potionInfo.name()),
							potionInfo.desc()));
				}
			});
		} else {
			GameScene.selectCell(cellSelector);
			Talent.onArtifactUsed(Dungeon.hero);
		}

		updateQuickslot();
	}

	public static void handlePotion(Hero hero, Potion potion, int cell){
		hero.spend( 1f );
		hero.busy();
		if (hero.pos == cell){
			potion.apply(hero);
			Sample.INSTANCE.play( Assets.Sounds.DRINK );

			hero.sprite.operate( hero.pos );
		} else {
			potion.cast(hero, cell);
		}
	}

	@Override
	public String status() {
		if (isEquipped(Dungeon.hero) && warmUpDelay > 0 && !cursed){
			return Messages.format( "%d%%", Math.max(0, 100 - (int)warmUpDelay) );
		} else {
			return super.status();
		}
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new kitEnergy();
	}
	
	@Override
	public void charge(Hero target, float amount) {
		if (type() == 2){
			if (charge < chargeCap && !cursed && target.buff(MagicImmune.class) == null) {
				partialCharge += 0.08f * amount;
				while (partialCharge >= 1) {
					partialCharge--;
					charge++;
				}
				if (charge >= chargeCap) {
					charge = chargeCap;
					partialCharge = 0;
				}
				updateQuickslot();
			}
		} else {
			if (target.buff(MagicImmune.class) != null) return;
			if (type() == 3)
				amount *= 3;
			partialCharge += 0.25f*amount;
			while (partialCharge >= 1){
				partialCharge--;
				charge++;
				updateQuickslot();
			}
		}
	}

	public int availableEnergy(){
		return charge;
	}

	public int consumeEnergy(int amount){
		int result = amount - charge;
		charge = Math.max(0, charge - amount);
		Talent.onArtifactUsed(Dungeon.hero);
		return Math.max(0, result);
	}

	@Override
	public String desc() {
		String result = getTypeBasedString("desc", type());

		if (isEquipped(Dungeon.hero)) {
			if (cursed)                 result += "\n\n" + Messages.get(this, "desc_cursed");
			else if (warmUpDelay > 0)   result += "\n\n" + Messages.get(this, "desc_warming");
			else                        result += "\n\n" + getTypeBasedString("desc_hint", type());

			if (type() == 2 && level() < levelCap) {
				if (potions.size() > 0) {
					result += "\n\n" + Messages.get(this, "desc_index");
					result += "\n" + "_" + Messages.get(potions.get(0), "name") + "_";
					if (potions.size() > 1)
						result += "\n" + "_" + Messages.get(potions.get(1), "name") + "_";
				}
			}
		}
		
		return result;
	}

	@Override
	public void type(int type) {
		if (type != 2 && type() == 2)
			chargeCap = 0;
		super.type(type);
		preparePotions();
		if (type == 2){
			chargeCap = (int)((level()+1)*0.6f)+2;
			charge = Math.min(charge, chargeCap);
		}
	}

	@Override
	public Item upgrade() {
		if (type() == 2)
			chargeCap = (int)((level()+1)*0.6f)+2;
		else
			chargeCap = 0;

		//for artifact transmutation.
		preparePotions();
		return super.upgrade();
	}
	
	@Override
	public boolean doEquip(Hero hero) {
		if (super.doEquip(hero)){
			if (type() != 2)
				warmUpDelay = 101f;
			return true;
		} else {
			return false;
		}
	}
	
	private static final String WARM_UP = "warm_up";
	private static final String POTIONS = "potions";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(WARM_UP, warmUpDelay);
		bundle.put(POTIONS, potions.toArray(new Class[potions.size()]) );
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		warmUpDelay = bundle.getFloat(WARM_UP);
		potions.clear();
		if (bundle.contains(POTIONS) && bundle.getClassArray(POTIONS) != null) {
			for (Class<?> potion : bundle.getClassArray(POTIONS)) {
				if (potion != null) potions.add(potion);
			}
		}
	}
	
	public class kitEnergy extends ArtifactBuff {

		public int charge(){
			return charge;
		}

		public void depleteCharge(int charge){
			AlchemistsToolkit.this.charge -= charge;
		}

		@Override
		public boolean act() {

			if (warmUpDelay > 0){
				if (level() == 10){
					warmUpDelay = 0;
				} else if (warmUpDelay == 101){
					warmUpDelay = 100f;
				} else if (!cursed && target.buff(MagicImmune.class) == null) {
					float turnsToWarmUp = (int) Math.pow(10 - level(), 2);
					warmUpDelay -= 100 / turnsToWarmUp;
				}
				updateQuickslot();
			}
			if (type() == 2){
				if (charge < chargeCap
						&& !cursed
						&& target.buff(MagicImmune.class) == null
						&& Regeneration.regenOn()) {
					//140 turns to charge at full, 92 turns to charge at 0/8
					float chargeGain = 1 / (140f - (chargeCap - charge)*6f);
					chargeGain *= RingOfEnergy.artifactChargeMultiplier(target);
					partialCharge += chargeGain;

					while (partialCharge >= 1) {
						partialCharge --;
						charge ++;
						updateQuickslot();

						if (charge >= chargeCap){
							charge = chargeCap;
							partialCharge = 0;
						}
					}
				}
			}

			spend(TICK);
			return true;
		}

		public void gainCharge(float levelPortion) {
			if (cursed || target.buff(MagicImmune.class) != null) return;

			//generates 2 energy every hero level, +1 energy per toolkit level
			//to a max of 12 energy per hero level
			//This means that energy absorbed into the kit is recovered in 5 hero levels
			float chargeGain = (2 + level()) * levelPortion;
			chargeGain *= RingOfEnergy.artifactChargeMultiplier(target);
			if (type() == 3)
				chargeGain *= 3;
			partialCharge += chargeGain;

			//charge is in increments of 1 energy.
			while (partialCharge >= 1) {
				charge++;
				partialCharge -= 1;

				updateQuickslot();
			}
		}

	}

	protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(AlchemistsToolkit.class, "prompt_potion");
		}

		@Override
		public Class<?extends Bag> preferredBag(){
			return PotionBandolier.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof Potion && item.isIdentified() && potions.contains(item.getClass());
		}

		@Override
		public void onSelect(Item item) {
			if (item != null && item instanceof Potion && item.isIdentified()){
				Hero hero = Dungeon.hero;
				for (int i = 0; ( i <= 1 && i < potions.size() ); i++){
					if (potions.get(i).equals(item.getClass())){
						hero.sprite.operate( hero.pos );
						hero.busy();
						hero.spend( 2f );
						Sample.INSTANCE.play(Assets.Sounds.DRINK);
						hero.sprite.emitter().burst(Speck.factory(Speck.BUBBLE), 12 );

						potions.remove(i);
						item.detach(hero.belongings.backpack);

						upgrade();
						Catalog.countUse(AlchemistsToolkit.class);
						GLog.i( Messages.get(AlchemistsToolkit.class, "infuse_potion") );
						return;
					}
				}
				GLog.w( Messages.get(AlchemistsToolkit.class, "unable_potion") );
			} else if (item instanceof Potion && !item.isIdentified()) {
				GLog.w( Messages.get(AlchemistsToolkit.class, "unknown_potion") );
			}
		}
	};

	public CellSelector.Listener cellSelector = new CellSelector.Listener() {

		@Override
		public void onSelect(Integer cell) {
			if (cell != null)
				handlePotion(curUser, (Potion)curItem, cell);
		}

		@Override
		public String prompt() {
			return Messages.get(AlchemistsToolkit.class, "prompt_target", curItem.trueName());
		}


	};

}
