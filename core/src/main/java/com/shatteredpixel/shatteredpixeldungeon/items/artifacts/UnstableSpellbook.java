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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.MobSpawner;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.wondrous.ScrollOfMorpheus;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.wondrous.ScrollOfPetrification;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.wondrous.ScrollOfRallying;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.wondrous.WondrousScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.SummonElemental;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
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
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;

public class UnstableSpellbook extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_SPELLBOOK;

		levelCap = 10;

		charge = (int)(level()*0.6f)+2;
		partialCharge = 0;
		chargeCap = (int)(level()*0.6f)+2;

		defaultAction = AC_READ;
	}

	public static final String AC_READ = "READ";
	public static final String AC_ADD = "ADD";

	private final ArrayList<Class> scrolls = new ArrayList<>();
	private final HashSet<Class> mobs = new HashSet<>();

	public UnstableSpellbook() {
		super();

		setupScrolls();
	}

	private void setupScrolls(){
		scrolls.clear();

		Class<?>[] scrollClasses = Generator.Category.SCROLL.classes;
		float[] probs = Generator.Category.SCROLL.defaultProbsTotal.clone(); //array of primitives, clone gives deep copy.
		int i = Random.chances(probs);

		while (i != -1){
			scrolls.add(scrollClasses[i]);
			probs[i] = 0;

			i = Random.chances(probs);
		}
		scrolls.remove(ScrollOfTransmutation.class);

		mobs.clear();
	}

	private void prepareScrolls(){
		while (!scrolls.isEmpty() && scrolls.size() > (levelCap-1-level())) {
			scrolls.remove(0);
		}
		while (mobs.size() < (level()+1)*4){
			mobs.add(Random.element(Random.element(
					EnumSet.of(Bestiary.REGIONAL, Bestiary.BOSSES, Bestiary.RARE, Bestiary.QUEST)
			).entities()));
		}
	}

	@Override
	public void type(int type) {
		super.type(type);
		prepareScrolls();
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped( hero ) && charge > 0 && !cursed && hero.buff(MagicImmune.class) == null) {
			actions.add(AC_READ);
		}
		if (isEquipped( hero ) && level() < levelCap && type() != 3 && !cursed && hero.buff(MagicImmune.class) == null) {
			actions.add(AC_ADD);
		}
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (hero.buff(MagicImmune.class) != null) return;

		if (action.equals( AC_READ )) {

			if (hero.buff( Blindness.class ) != null) GLog.w( Messages.get(this, "blinded") );
			else if (!isEquipped( hero ))             GLog.i( Messages.get(Artifact.class, "need_to_equip") );
			else if (charge <= 0)                     GLog.i( Messages.get(this, "no_charge") );
			else if (cursed)                          GLog.i( Messages.get(this, "cursed") );
			else {
				if (type() != 3)
					doReadEffect(hero);
				else
					doSummonEffect(hero);
			}

		} else if (action.equals( AC_ADD )) {
			GameScene.selectItem(itemSelector);
		}
	}

	public void doReadEffect(Hero hero){
		charge--;

		Scroll scroll;
		do {
			scroll = (Scroll) Generator.randomUsingDefaults(Generator.Category.SCROLL);
		} while (scroll == null
				//reduce the frequency of these scrolls by half
				||((scroll instanceof ScrollOfIdentify ||
				scroll instanceof ScrollOfRemoveCurse ||
				scroll instanceof ScrollOfMagicMapping) && Random.Int(2) == 0)
				//cannot roll transmutation
				|| (scroll instanceof ScrollOfTransmutation));

		Class<? extends Scroll> scrollCls = scroll.getClass();

		if (type() == 2){
			scroll = Reflection.newInstance(ExoticScroll.regToExo.get(scrollCls));
		}

		scroll.anonymize();
		scroll.talentChance = 0;  //spellbook does not trigger on-scroll talents
		curItem = scroll;
		curUser = hero;

		//if there are charges left and the scroll has been given to the book
		if (charge > 0 && !scrolls.contains(scrollCls)) {
			final Scroll fScroll = scroll;

			final ExploitHandler handler = Buff.affect(hero, ExploitHandler.class);
			handler.scroll = scroll;
			Class<?> goodScroll = ExoticScroll.regToExo.get(scrollCls);
			if (type() == 2)
				goodScroll = WondrousScroll.regToWon.get(scrollCls);

			Class<?> finalGoodScroll = goodScroll;
			GameScene.show(new WndOptions(new ItemSprite(this),
					Messages.get(this, "prompt"),
					getTypeBasedString("read_empowered", type()),
					scroll.trueName(),
					Messages.get(finalGoodScroll, "name")){
				@Override
				protected void onSelect(int index) {
					handler.detach();
					if (index == 1){
						Scroll scroll = (Scroll) Reflection.newInstance(finalGoodScroll);
						curItem = scroll;
						charge--;
						scroll.anonymize();
						scroll.talentChance = 0;
						if (scroll instanceof WondrousScroll)
							Catalog.countUse(finalGoodScroll);
						checkForArtifactProc(curUser, scroll);
						scroll.doRead();
						Talent.onArtifactUsed(Dungeon.hero);
					} else {
						checkForArtifactProc(curUser, fScroll);
						fScroll.doRead();
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
							return new ItemSprite(fScroll);
						case 1:
							return new ItemSprite((Item) Reflection.newInstance(finalGoodScroll));
					}
					return super.getIcon(index);
				}

				@Override
				protected boolean hasInfo(int index) {
					return true;
				}

				@Override
				protected void onInfo(int index) {
					Scroll scrollInfo = null;
					switch (index){
						case 0:
							scrollInfo = fScroll;
							break;
						case 1:
							scrollInfo = (Scroll) Reflection.newInstance(finalGoodScroll);
							break;
					}
					GameScene.show(new WndTitledMessage(
							new ItemSprite(scrollInfo),
							Messages.titleCase(scrollInfo.name()),
							scrollInfo.desc()));
				}
			});
		} else {
			checkForArtifactProc(curUser, scroll);
			scroll.doRead();
			Talent.onArtifactUsed(Dungeon.hero);
		}

		updateQuickslot();
	}

	public void doSummonEffect(Hero hero){
		charge--;
		Mob mob = Reflection.newInstance(MobSpawner.getMobRotation(Dungeon.depth).get(0));

		curUser = hero;

		if (charge > 0 && mobs.contains(mob.getClass()) && MobSpawner.RARE_ALTS.get(mob.getClass()) != null){
			final ExploitHandler handler = Buff.affect(hero, ExploitHandler.class);
			final Mob fMob = mob;
			handler.mob = mob;
			Class<?> rareMobClass = MobSpawner.RARE_ALTS.get(mob.getClass());

			GameScene.show(new WndOptions(new ItemSprite(this),
					Messages.get(this, "mob_prompt"),
					Messages.get(this, "read_rare"),
					mob.name(),
					Messages.get(rareMobClass, "name")){
				@Override
				protected void onSelect(int index) {
					handler.detach();
					Mob actualMob = fMob;
					if (index == 1){
						actualMob = (Mob) Reflection.newInstance(rareMobClass);
						charge--;
					}
					actualMob.maxLvl = -20;
					Buff.affect(actualMob, SummonElemental.InvisAlly.class);
					if (actualMob.state != actualMob.PASSIVE) {
						actualMob.state = actualMob.WANDERING;
					}
					ArrayList<Integer> respawnPoints = new ArrayList<>();

					for (int i = 0; i < PathFinder.NEIGHBOURS9.length; i++) {
						int p = curUser.pos + PathFinder.NEIGHBOURS9[i];
						if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
							respawnPoints.add( p );
						}
					}
					if (respawnPoints.size() > 0){
						GameScene.add(actualMob);
						ScrollOfTeleportation.appear(actualMob, Random.element( respawnPoints ));
					}

					Talent.onArtifactUsed(Dungeon.hero);
					updateQuickslot();
				}

				@Override
				public void onBackPressed() {
					//do nothing
				}
			});
		} else {
			mob.maxLvl = -20;
			Buff.affect(mob, SummonElemental.InvisAlly.class);
			if (mob.state != mob.PASSIVE) {
				mob.state = mob.WANDERING;
			}
			ArrayList<Integer> respawnPoints = new ArrayList<>();

			for (int i = 0; i < PathFinder.NEIGHBOURS9.length; i++) {
				int p = curUser.pos + PathFinder.NEIGHBOURS9[i];
				if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
					respawnPoints.add( p );
				}
			}
			if (respawnPoints.size() > 0){
				GameScene.add(mob);
				ScrollOfTeleportation.appear(mob, Random.element( respawnPoints ));
			}

			Talent.onArtifactUsed(Dungeon.hero);
		}

		updateQuickslot();
	}

	private void checkForArtifactProc(Hero user, Scroll scroll){
		//if the base scroll (exotics all match) is an AOE effect, then also trigger illuminate
		if (scroll instanceof ScrollOfLullaby || scroll instanceof ScrollOfMorpheus
				|| scroll instanceof ScrollOfRemoveCurse || scroll instanceof ScrollOfTerror
				|| scroll instanceof ScrollOfPetrification) {
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (Dungeon.level.heroFOV[mob.pos]) {
					artifactProc(mob, visiblyUpgraded(), 1);
				}
			}
		//except rage, which affects everything even if it isn't visible
		} else if (scroll instanceof ScrollOfRage || scroll instanceof ScrollOfRallying){
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				artifactProc(mob, visiblyUpgraded(), 1);
			}
		}
	}

	//forces the reading of a regular scroll if the player tried to exploit by quitting the game when the menu was up
	public static class ExploitHandler extends Buff {
		{ actPriority = VFX_PRIO; }

		public Scroll scroll = null;
		public Mob mob = null;

		@Override
		public boolean act() {
			curUser = Dungeon.hero;
			if (scroll != null) {
				curItem = scroll;
				scroll.anonymize();
				scroll.talentChance = 0;
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						scroll.doRead();
						Item.updateQuickslot();
					}
				});
			} else if (mob != null) {
				ArrayList<Integer> respawnPoints = new ArrayList<>();

				for (int i = 0; i < PathFinder.NEIGHBOURS9.length; i++) {
					int p = curUser.pos + PathFinder.NEIGHBOURS9[i];
					if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
						respawnPoints.add( p );
					}
				}
				if (respawnPoints.size() > 0){
					int index = Random.index( respawnPoints );
					GameScene.add(mob);
					mob.maxLvl = -20;
					Buff.affect(mob, SummonElemental.InvisAlly.class);
					Buff.affect(mob, Invisibility.class);
					ScrollOfTeleportation.appear(mob, respawnPoints.get(index));
				}
			}
			detach();
			return true;
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( "scroll", scroll );
			bundle.put( "mob", mob );
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			scroll = (Scroll)bundle.get("scroll");
			mob = (Mob)bundle.get("mob");
		}
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new bookRecharge();
	}
	
	@Override
	public void charge(Hero target, float amount) {
		if (charge < chargeCap && !cursed && target.buff(MagicImmune.class) == null){
			partialCharge += 0.1f*amount*rechargeModifier();
			while (partialCharge >= 1){
				partialCharge--;
				charge++;
			}
			if (charge >= chargeCap){
				partialCharge = 0;
			}
			updateQuickslot();
		}
	}

	public float rechargeModifier(){
		return rechargeModifier(type());
	}

	public float rechargeModifier(int type){
		switch (type){
			case 1:
				return 1f;
			case 2:
				return 0.75f;
			case 3:
				return 0.67f;
		}
		return 1f;
	}

	@Override
	public Item upgrade() {
		chargeCap = (int)((level()+1)*0.6f)+2;

		//for artifact transmutation.
		prepareScrolls();

		return super.upgrade();
	}

	@Override
	public void resetForTrinity(int visibleLevel) {
		super.resetForTrinity(visibleLevel);
		setupScrolls();
		prepareScrolls();
	}

	@Override
	public String desc() {
		String desc = getTypeBasedString("desc", type());

		if (isEquipped(Dungeon.hero)) {
			if (cursed) {
				desc += "\n\n" + Messages.get(this, "desc_cursed");
			}

            if (level() < levelCap) {
				if (type() != 3 && scrolls.size() > 0) {
					desc += "\n\n" + Messages.get(this, "desc_index");
					desc += "\n" + "_" + Messages.get(scrolls.get(0), "name") + "_";
					if (scrolls.size() > 1)
						desc += "\n" + "_" + Messages.get(scrolls.get(1), "name") + "_";
				} else if (type() == 3){
					desc += "\n\n";
					int counter = 0;
					StringBuilder desc_mobs = new StringBuilder(Messages.get(this, "desc_bestiary"));
					for (Class mob_type: mobs){
						desc_mobs.append(Messages.get(mob_type, "name"));
						if (++counter < mobs.size())
							desc_mobs.append(", ");
					}
					desc += desc_mobs.toString();
				}
			}
		}
		
		if (level() > 0) {
			desc += "\n\n" + getTypeBasedString("desc_empowered", type());
		}

		return desc;
	}

	@Override
	public String getTypeMessage(int type) {
		return Messages.get(this, "type_base", Math.round(100*rechargeModifier(type)), super.getTypeMessage(type));
	}

	private static final String SCROLLS =   "scrolls";
	private static final String MOBS    =   "mobs";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( SCROLLS, scrolls.toArray(new Class[scrolls.size()]) );
		bundle.put( MOBS,    mobs.toArray(new Class[mobs.size()]) );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		scrolls.clear();
		if (bundle.contains(SCROLLS) && bundle.getClassArray(SCROLLS) != null) {
			for (Class<?> scroll : bundle.getClassArray(SCROLLS)) {
				if (scroll != null) scrolls.add(scroll);
			}
		}
		mobs.clear();
		if (bundle.contains(MOBS) && bundle.getClassArray(MOBS) != null) {
			for (Class<?> mob : bundle.getClassArray(MOBS)) {
				if (mob != null) mobs.add(mob);
			}
		}
	}

	public class bookRecharge extends ArtifactBuff{
		@Override
		public boolean act() {
			if (charge < chargeCap
					&& !cursed
					&& target.buff(MagicImmune.class) == null
					&& Regeneration.regenOn()) {
				//120 turns to charge at full, 80 turns to charge at 0/8
				float chargeGain = 1 / (120f - (chargeCap - charge)*5f);
				chargeGain *= RingOfEnergy.artifactChargeMultiplier(target);
				chargeGain *= rechargeModifier();
				partialCharge += chargeGain;

				while (partialCharge >= 1) {
					partialCharge --;
					charge ++;

					if (charge == chargeCap){
						partialCharge = 0;
					}
				}
			}

			updateQuickslot();

			spend( TICK );

			return true;
		}

		public void processKills(Class<?> mobType){
			if (type() == 3 && !cursed && level() < levelCap){
				boolean isValid = false;
				for (Bestiary category: EnumSet.of(Bestiary.REGIONAL, Bestiary.BOSSES, Bestiary.RARE, Bestiary.QUEST)){
					if (category.entities().contains(mobType)){
						isValid = true;
						break;
					}
				}
				if (isValid) {
					mobs.add(mobType);
					if (mobs.size() == 4 * (level() + 1)) {
						Sample.INSTANCE.play(Assets.Sounds.BURNING);
						target.sprite.emitter().burst(ElmoParticle.FACTORY, 12);

						upgrade();
						Catalog.countUse(UnstableSpellbook.class);
						GLog.h(Messages.get(UnstableSpellbook.class, "infuse_bestiary"));
					}
				}
			}
		}
	}

	protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(UnstableSpellbook.class, "prompt");
		}

		@Override
		public Class<?extends Bag> preferredBag(){
			return ScrollHolder.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof Scroll && item.isIdentified() && scrolls.contains(item.getClass());
		}

		@Override
		public void onSelect(Item item) {
			if (item != null && item instanceof Scroll && item.isIdentified()){
				Hero hero = Dungeon.hero;
				for (int i = 0; ( i <= 1 && i < scrolls.size() ); i++){
					if (scrolls.get(i).equals(item.getClass())){
						hero.sprite.operate( hero.pos );
						hero.busy();
						hero.spend( 2f );
						Sample.INSTANCE.play(Assets.Sounds.BURNING);
						hero.sprite.emitter().burst( ElmoParticle.FACTORY, 12 );

						scrolls.remove(i);
						item.detach(hero.belongings.backpack);

						upgrade();
						Catalog.countUse(UnstableSpellbook.class);
						GLog.i( Messages.get(UnstableSpellbook.class, "infuse_scroll") );
						return;
					}
				}
				GLog.w( Messages.get(UnstableSpellbook.class, "unable_scroll") );
			} else if (item instanceof Scroll && !item.isIdentified()) {
				GLog.w( Messages.get(UnstableSpellbook.class, "unknown_scroll") );
			}
		}
	};
}
