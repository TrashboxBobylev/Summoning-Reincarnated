/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ConjurerBook;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.BeamOfAffection;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.DreemurrsNecromancy;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.EnergizedRenewal;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.PushingWaveform;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.RunicShell;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.ShockerBreaker;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.StarBlazing;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.SubNullFieldLighter;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.knight.Concentration;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.knight.DirectingPulse;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.knight.EnergizedBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.knight.MotionBloom;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.knight.ShardsOfDespair;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.soulreaver.AntarcticTouch;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.soulreaver.ArtemisBridge;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.soulreaver.HolyAura;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.soulreaver.TommiesArmorSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.soulreaver.TransmogrificationWand;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.treasurebags.GenericBag;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.damagesource.DamageSource;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.VaultMirrorSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class VaultMirror extends NPC {

	{
		spriteClass = VaultMirrorSprite.class;

		properties.add(Property.IMMOVABLE);
		properties.add(Property.OBJECT);
	}

	@Override
	protected void throwItems() {
		Heap heap = Dungeon.level.heaps.get( pos );
		if (heap != null) {
			Dungeon.level.drop( heap.pickUp(), pos+Dungeon.level.width() ).sprite.drop( pos );
		}
	}

	public Item reward = null;

	public void createReward(HeroClass cls){
		//we create a new generator here as some heroes call RNG here and some don't
		Random.pushGenerator(Random.Long());
			switch (cls) {
				case WARRIOR:
					reward = new BrokenSeal().upgrade().identify(false);
					((BrokenSeal)reward).setGlyph(Armor.Glyph.random());
					break;
				case MAGE:
					reward = new WandOfMagicMissile();
					((WandOfMagicMissile)reward).enchant();
					((WandOfMagicMissile) reward).magicalPotionBonus = true;
					break;
				case ROGUE:
					reward = new CloakOfShadows().upgrade(8).identify(false);
					((CloakOfShadows) reward).directCharge(8);
					break;
				case HUNTRESS:
					reward = new SpiritBow().identify(false);
					((SpiritBow)reward).enchant();
					break;
				case DUELIST:
					reward = new MirrorSword().upgrade(3).identify(false);
					((MeleeWeapon)reward).enchant();
					break;
				case CLERIC:
					reward = new HolyTome().upgrade(8).identify(false);
					((HolyTome) reward).directCharge(8);
					break;
				case CONJURER:
					ConjurerBook book = new ConjurerBook();
					book.items.add(new StarBlazing());
					book.items.add(new EnergizedRenewal());
					book.items.add(new BeamOfAffection());
					book.items.add(new RunicShell());
					book.items.add(new PushingWaveform());
					book.items.add(new ShockerBreaker());
					book.items.add(new DreemurrsNecromancy());
					book.items.add(new SubNullFieldLighter());
					if (Dungeon.hero.subClass == HeroSubClass.WILL_SORCERER){
						book.items.add(new EnergizedBlast());
						book.items.add(new MotionBloom());
						book.items.add(new Concentration());
						book.items.add(new DirectingPulse());
						book.items.add(new ShardsOfDespair());
					} else if (Dungeon.hero.subClass == HeroSubClass.SOUL_WIELDER){
						book.items.add(new AntarcticTouch());
						book.items.add(new TommiesArmorSpell());
						book.items.add(new TransmogrificationWand());
						book.items.add(new ArtemisBridge());
						book.items.add(new HolyAura());
					}
					reward = book;
					break;
				case ADVENTURER:
					reward = new GenericBag();
					break;
			}
		Random.popGenerator();
	}

	@Override
	public boolean interact(Char c) {
		if (c instanceof Hero) {
			ShatteredPixelDungeon.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					if (reward != null) {

						String sceneText = Messages.get(VaultMirror.class, "approach") + "\n\n";
						switch (((Hero) c).heroClass){
							case WARRIOR:
								sceneText += Messages.get(VaultMirror.class, "scene_warrior");
								break;
							case MAGE:
								sceneText += Messages.get(VaultMirror.class, "scene_mage");
								break;
							case ROGUE:
								sceneText += Messages.get(VaultMirror.class, "scene_rogue");
								break;
							case HUNTRESS:
								sceneText += Messages.get(VaultMirror.class, "scene_huntress");
								break;
							case DUELIST:
								sceneText += Messages.get(VaultMirror.class, "scene_duelist");
								break;
							case CLERIC:
								sceneText += Messages.get(VaultMirror.class, "scene_cleric");
								break;
							case ADVENTURER:
								sceneText += Messages.get(VaultMirror.class, "scene_adventurer");
								break;
							case CONJURER:
								sceneText += Messages.get(VaultMirror.class, "scene_conjurer");
								break;
						}
						sceneText += "\n\n" + Messages.get(VaultMirror.class, "scene_final");

						GameScene.show(new WndOptions(sprite(),
								Messages.titleCase(name()),
								sceneText,
								Messages.get(VaultMirror.class, "take")) {
							@Override
							protected void onSelect(int index) {
								super.onSelect(index);
								if (index == 0) {
									GameScene.show(new WndTitledMessage(sprite(), Messages.titleCase(name()), Messages.get(VaultMirror.class, "scene_take")));
									if (reward.doPickUp((Hero) c)) {
										GLog.i( Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", reward.name())) );
									} else {
										Dungeon.level.drop(reward, c.pos).sprite.drop();
									}
									Imp.Quest.mirrorUsed = true;
									reward = null;
								}
							}
						});
					} else {
						GameScene.show(new WndTitledMessage(sprite(), Messages.titleCase(name()), Messages.get(VaultMirror.class, "scene_nothing")));
					}
				}
			});
		}
		return false;
	}

	@Override
	public int defenseSkill( Char enemy ) {
		return INFINITE_EVASION;
	}

	@Override
	public void damage( int dmg, DamageSource src ) {
		//do nothing
	}

	@Override
	public boolean add( Buff buff ) {
		return false;
	}

	@Override
	public boolean reset() {
		return true;
	}

	private static final String REWARD = "reward";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		if (reward != null) {
			bundle.put(REWARD, reward);
		}
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(REWARD)){
			reward = (Item) bundle.get(REWARD);
		}
	}

	public static class MirrorSword extends Greatsword {

		{
			//cannot be taken out of the vault
			unique = true;
		}

	}

}
