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
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.MnemonicPrayer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.effects.Surprise;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class MasterThievesArmband extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_ARMBAND;

		levelCap = 10;

		charge = 0;
		partialCharge = 0;
		chargeCap = 5+level()/2;

		defaultAction = AC_STEAL;
	}

	public static final String AC_STEAL = "STEAL";

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero)
				&& charge > 0
				&& hero.buff(MagicImmune.class) == null
				&& !cursed) {
			actions.add(AC_STEAL);
		}
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (hero.buff(MagicImmune.class) != null) return;

		if (action.equals(AC_STEAL)){

			curUser = hero;

			if (!isEquipped( hero )) {
				GLog.i( Messages.get(Artifact.class, "need_to_equip") );
				usesTargeting = false;

			} else if (charge < 1) {
				GLog.i( Messages.get(this, "no_charge") );
				usesTargeting = false;

			} else if (cursed) {
				GLog.w( Messages.get(this, "cursed") );
				usesTargeting = false;

			} else {
				usesTargeting = true;
				if (type() != 3)
					GameScene.selectCell(targeter);
				else
					GameScene.selectCell(targeter_mnemonic);
			}

		}
	}

	public CellSelector.Listener targeter = new CellSelector.Listener(){

		@Override
		public void onSelect(Integer target) {

			if (target == null) {
				return;
			} else if (!Dungeon.level.adjacent(curUser.pos, target) || Actor.findChar(target) == null){
				GLog.w( Messages.get(MasterThievesArmband.class, "no_target") );
			} else {
				Char ch = Actor.findChar(target);
				if (ch instanceof Shopkeeper){
					GLog.w( Messages.get(MasterThievesArmband.class, "steal_shopkeeper") );
				} else if (ch.alignment != Char.Alignment.ENEMY
						&& !(ch instanceof Mimic && ch.alignment == Char.Alignment.NEUTRAL)){
					GLog.w( Messages.get(MasterThievesArmband.class, "no_target") );
				} else if (ch instanceof Mob) {
					curUser.busy();
					curUser.sprite.attack(target, new Callback() {
						@Override
						public void call() {
							Sample.INSTANCE.play(Assets.Sounds.HIT);

							boolean surprised = ((Mob) ch).surprisedBy(curUser, false);
							float lootMultiplier = 1f + 0.1f*level();
							int debuffDuration = 3 + level()/2;

							Invisibility.dispel(curUser);

							if (surprised){
								lootMultiplier += 0.5f;
								Surprise.hit(ch);
								Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
								debuffDuration += 2;
								exp += 2;
							}
							if (type() == 2)
								lootMultiplier *= 2;

							float lootChance = ((Mob) ch).lootChance() * lootMultiplier;

							if (Dungeon.mode == Dungeon.GameMode.GAUNTLET){
								lootChance = 0;
							} else if (Dungeon.hero.lvl > ((Mob) ch).maxLvl + 2) {
								lootChance = 0;
							} else if (ch.buff(StolenTracker.class) != null){
								lootChance = 0;
							}

							if (lootChance == 0){
								GLog.w(Messages.get(MasterThievesArmband.class, "no_steal"));
							} else if (Random.Float() <= lootChance){
								Item loot = ((Mob) ch).createLoot();
								loot = Challenges.process(loot);
								if (Challenges.isItemBlocked(loot)){
									GLog.i(Messages.get(MasterThievesArmband.class, "failed_steal"));
									Buff.affect(ch, StolenTracker.class).setItemStolen(false);
								} else {
									if (loot.doPickUp(curUser)) {
										//item collection happens instantly
										curUser.spend(-loot.pickupDelay());
									} else {
										Dungeon.level.drop(loot, curUser.pos).sprite.drop();
									}
									if (type() == 2){
										if (Random.Float() < 0.25f + 0.05f*level()){
											Item bonus;
											do {
												bonus = RingOfWealth.genConsumableDrop(level());
											} while (Challenges.isItemBlocked(bonus));
											Dungeon.level.drop(Challenges.process(bonus), target).sprite.drop();
											RingOfWealth.showFlareForBonusDrop(ch.sprite);
										}
									}
									GLog.i(Messages.get(MasterThievesArmband.class, "stole_item", loot.name()));
									Buff.affect(ch, StolenTracker.class).setItemStolen(true);
								}
							} else {
								GLog.i(Messages.get(MasterThievesArmband.class, "failed_steal"));
								Buff.affect(ch, StolenTracker.class).setItemStolen(false);
							}

							if (type() == 1){
								Buff.prolong(ch, Blindness.class, debuffDuration);
								Buff.prolong(ch, Cripple.class, debuffDuration);
							} else {
								Buff.prolong(ch, Vertigo.class, debuffDuration);
							}

							artifactProc(ch, visiblyUpgraded(), 1);

							charge--;
							exp += 3;
                            exp = (int) (exp / rechargeModifier());
							Talent.onArtifactUsed(Dungeon.hero);
							while (exp >= (10 + Math.round(3.33f * level())) && level() < levelCap) {
								exp -= 10 + Math.round(3.33f * level());
								Catalog.countUse(MasterThievesArmband.class);
								GLog.p(Messages.get(MasterThievesArmband.class, "level_up"));
								upgrade();
							}
							Item.updateQuickslot();
							curUser.next();
						}
					});

				}
			}

		}

		@Override
		public String prompt() {
			return Messages.get(MasterThievesArmband.class, "prompt");
		}
	};

	public CellSelector.Listener targeter_mnemonic = new CellSelector.Listener() {

		@Override
		public void onSelect(Integer target) {
			if (target == null) {
				return;
			} else if (!(Dungeon.level.adjacent(curUser.pos, target) || target == curUser.pos) || Actor.findChar(target) == null) {
				GLog.w(Messages.get(MasterThievesArmband.class, "no_target_mnemonic"));
			} else {
				curUser.busy();
				curUser.sprite.attack(target, new Callback() {
					@Override
					public void call() {
						Char ch = Actor.findChar(target);
						MnemonicPrayer.affectChar(ch, 3 + level()/2f);
						curUser.next();
					}
				});
			}
		}

		@Override
		public String prompt() {
			return Messages.get(MasterThievesArmband.class, "prompt_mnemonic");
		}
	};

	//counter of 0 for attempt but no success, 1 for success
	public static class StolenTracker extends CounterBuff {
		{ revivePersists = true; }
		public void setItemStolen(boolean stolen){ if (stolen) countUp(1); }
		public boolean itemWasStolen(){ return count() > 0; }
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new Thievery();
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
				return 2f;
		}
		return 1.0f;
	}
	
	@Override
	public void charge(Hero target, float amount) {
		if (cursed || target.buff(MagicImmune.class) != null) return;
		if (charge < chargeCap) {
			partialCharge += 0.1f * amount * rechargeModifier();
			while (partialCharge >= 1f) {
				charge++;
				partialCharge--;
			}
			if (charge >= chargeCap) {
				GLog.p(Messages.get(MasterThievesArmband.class, "full"));
				partialCharge = 0;
				charge = chargeCap;
			}
			updateQuickslot();
		}
	}

	public int chargeCap(){
		return chargeCap(type());
	}

	public int chargeCap(int type){
		switch (type){
			case 1:
				return 5 + (level()+1)/2;
			case 2:
				return 3 + (level()+1)/3;
			case 3:
				return 6 + level();
		}
		return 0;
	}

	@Override
	public Item upgrade() {
		chargeCap = chargeCap();
		return super.upgrade();
	}

	@Override
	public void type(int type) {
		super.type(type);
		chargeCap = chargeCap(type);
		charge = Math.min(charge, chargeCap());
	}

	@Override
	public String desc() {
		String desc = super.desc();

		if ( isEquipped (Dungeon.hero) ){
			if (cursed){
				desc += "\n\n" + getTypeBasedString("desc_cursed", type());
			} else {
				desc += "\n\n" + getTypeBasedString("desc_worn", type());
			}
		}

		return desc;
	}

	@Override
	public String getTypeMessage(int type) {
		return Messages.get(this, "type",
				Math.round(100*rechargeModifier(type)),
				chargeCap(type)) + "\n\n" + super.getTypeMessage(type);
	}

	public class Thievery extends ArtifactBuff {

		@Override
		public boolean act() {
			if (cursed && Dungeon.gold > 0 && Random.Int(5) == 0){
				if (type() != 3){
					Dungeon.gold--;
				} else {
					Dungeon.hero.belongings.charge(-0.1f);
					for (Buff b : Dungeon.hero.buffs()) {
						if (b instanceof Artifact.ArtifactBuff) {
							if (!((Artifact.ArtifactBuff) b).isCursed()) {
								((Artifact.ArtifactBuff) b).charge(Dungeon.hero, -0.1f);
							}
						}
					}
				}
				updateQuickslot();
			}

			spend(TICK);
			return true;
		}

		public void gainCharge(float levelPortion) {
			if (cursed || target.buff(MagicImmune.class) != null) return;

			if (charge < chargeCap){
				float chargeGain = 3f * levelPortion;
				chargeGain *= rechargeModifier();
				chargeGain *= RingOfEnergy.artifactChargeMultiplier(target);

				partialCharge += chargeGain;
				while (partialCharge > 1f){
					partialCharge--;
					charge++;
					updateQuickslot();

					if (charge == chargeCap){
						GLog.p( Messages.get(MasterThievesArmband.class, "full") );
						partialCharge = 0;
					}
				}

			} else {
				partialCharge = 0f;
			}
		}
		
		public boolean steal(Item item){
			int chargesUsed = chargesToUse(item);
			float stealChance = stealChance(item);
			if (Random.Float() > stealChance){
				return false;
			} else {
				charge -= chargesUsed;
				exp += 4 * chargesUsed;
				GLog.i(Messages.get(MasterThievesArmband.class, "stole_item", item.name()));

				Talent.onArtifactUsed(Dungeon.hero);
				while (exp >= (10 + Math.round(3.33f * level())) && level() < levelCap) {
					exp -= 10 + Math.round(3.33f * level());
					Catalog.countUse(MasterThievesArmband.class);
					GLog.p(Messages.get(MasterThievesArmband.class, "level_up"));
					upgrade();
				}
				updateQuickslot();
				return true;
			}
		}

		public float stealChance(Item item){
			int chargesUsed = chargesToUse(item);
			float val = chargesUsed * (10 + level()/2f);
			val /= rechargeModifier();
			return Math.min(1f, val/item.value());
		}

		public void attunementUpgrade(){
			upgrade();
			upgrade();
			Catalog.countUse(MasterThievesArmband.class);
			GLog.p(Messages.get(MasterThievesArmband.class, "level_up_major"));
		}

		public int chargesToUse(Item item){
			int value = item.value();
			float valUsing = 0;
			int chargesUsed = 0;
			while (valUsing < value && chargesUsed < charge){
				valUsing += 10 + level()/2f;
				chargesUsed++;
			}
			return chargesUsed;
		}
	}


}
