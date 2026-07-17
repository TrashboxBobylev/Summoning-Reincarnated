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
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttunementBoost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostBurn;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Stasis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DirectableAlly;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ConjurerSet;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.HolyBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfAntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.AbyssLevel;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.damagesource.DamageProperty;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.damagesource.DamageSource;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.AlchemyScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GhostSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.EnumSet;

public class DriedRose extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_ROSE1;

		levelCap = 10;

		charge = 100;
		chargeCap = 100;

		defaultAction = AC_SUMMON;
	}

	private boolean talkedTo = false;
	private boolean firstSummon = false;
	
	private GhostHero ghost = null;
	private int ghostID = 0;
	
	private MeleeWeapon weapon = null;
	private MeleeWeapon weapon2 = null;
	private Wand wand = null;
	private Armor armor = null;

	public int droppedPetals = 0;

	public static final String AC_SUMMON = "SUMMON";
	public static final String AC_DIRECT = "DIRECT";
	public static final String AC_OUTFIT = "OUTFIT";

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (!Ghost.Quest.completed()){
			return actions;
		}
		if (isEquipped( hero )
				&& charge == chargeCap
				&& !cursed
				&& hero.buff(MagicImmune.class) == null
				&& ghostID == 0) {
			actions.add(AC_SUMMON);
		}
		if (ghostID != 0){
			actions.add(AC_DIRECT);
		}
		if (isIdentified() && !cursed){
			actions.add(AC_OUTFIT);
		}
		
		return actions;
	}

	@Override
	public String defaultAction() {
		if (ghost != null){
			return AC_DIRECT;
		} else {
			return AC_SUMMON;
		}
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute(hero, action);

		if (action.equals(AC_SUMMON)) {

			if (hero.buff(MagicImmune.class) != null) return;

			if (!Ghost.Quest.completed())   GameScene.show(new WndUseItem(null, this));
			else if (ghost != null)         GLog.i( Messages.get(this, "spawned") );
			else if (!isEquipped( hero ))   GLog.i( Messages.get(Artifact.class, "need_to_equip") );
			else if (charge != chargeCap)   GLog.i( Messages.get(this, "no_charge") );
			else if (cursed)                GLog.i( Messages.get(this, "cursed") );
			else {
				ArrayList<Integer> spawnPoints = new ArrayList<>();
				for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
					int p = hero.pos + PathFinder.NEIGHBOURS8[i];
					if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
						spawnPoints.add(p);
					}
				}

				if (spawnPoints.size() > 0) {
					ghost = new GhostHero( this );
					ghostID = ghost.id();
					ghost.pos = Random.element(spawnPoints);

					GameScene.add(ghost, 1f);
					Dungeon.level.occupyCell(ghost);
					
					CellEmitter.get(ghost.pos).start( ShaftParticle.FACTORY, 0.3f, 4 );
					CellEmitter.get(ghost.pos).start( Speck.factory(Speck.LIGHT), 0.2f, 3 );

					hero.spend(1f);
					hero.busy();
					hero.sprite.operate(hero.pos);

					if (!firstSummon) {
						ghost.yell( Messages.get(GhostHero.class, "hello", Messages.titleCase(Dungeon.hero.name())) );
						Sample.INSTANCE.play( Assets.Sounds.GHOST );
						firstSummon = true;
						
					} else {
						if (BossHealthBar.isAssigned()) {
							ghost.sayBoss();
						} else {
							ghost.sayAppeared();
						}
					}

					Invisibility.dispel(hero);
					Talent.onArtifactUsed(hero);
					charge = 0;
					partialCharge = 0;
					updateQuickslot();

				} else
					GLog.i( Messages.get(this, "no_space") );
			}

		} else if (action.equals(AC_DIRECT)){
			if (ghost == null && ghostID != 0){
				findGhost();
			}
			if (ghost != null && ghost != Stasis.getStasisAlly()){
				GameScene.selectCell(ghostDirector);
			}

		} else if (action.equals(AC_OUTFIT)){
			GameScene.show( new WndGhostHero(this) );
		}
	}

	private void findGhost(){
		Actor a = Actor.findById(ghostID);
		if (a != null){
			ghost = (GhostHero)a;
		} else {
			if (Stasis.getStasisAlly() instanceof GhostHero){
				ghost = (GhostHero) Stasis.getStasisAlly();
				ghostID = ghost.id();
			} else {
				ghostID = 0;
			}
		}
	}
	
	public int ghostStrength(){
		return 13 + level()/2;
	}

	public int ghostAttunement(){
		return 2 + level()/2;
	}

	public int ghostHealth(){
		return ghostHealth(type());
	}

	public int ghostHealth(int type){
		switch (type){
			case 1:
				return 20 + 8*level();
			case 2:
				return 30 + 12*level();
			case 3:
				return 16 + 6*level();
		}
		return 1;
	}

	@Override
	public void type(int type) {
		if (type() == 2 && type != 2 && weapon2 != null){
			if (!weapon2.doPickUp(Dungeon.hero, Dungeon.hero.pos)) {
				Dungeon.level.drop(weapon2, Dungeon.hero.pos).sprite.drop();
			} else {
				Dungeon.hero.spend(-Item.TIME_TO_PICK_UP);
				GLog.i(Messages.get(Hero.class, "you_now_have", weapon2.name()));
			}
			weapon2 = null;
		}
		if (type() == 3 && type != 3 && wand != null){
			if (!wand.doPickUp(Dungeon.hero, Dungeon.hero.pos)) {
				Dungeon.level.drop(wand, Dungeon.hero.pos).sprite.drop();
			} else {
				Dungeon.hero.spend(-Item.TIME_TO_PICK_UP);
				GLog.i(Messages.get(Hero.class, "you_now_have", wand.name()));
			}
			if (ghost != null){
				Buff.detach(ghost, WandStrikeCooldown.class);
			}
			wand = null;
		}
		if (type == 2 && armor != null){
			if (!armor.doPickUp(Dungeon.hero, Dungeon.hero.pos)) {
				Dungeon.level.drop(armor, Dungeon.hero.pos).sprite.drop();
			} else {
				Dungeon.hero.spend(-Item.TIME_TO_PICK_UP);
				GLog.i(Messages.get(Hero.class, "you_now_have", armor.name()));
			}
			armor = null;
		}
		if (type == 3 && weapon != null){
			if (!weapon.doPickUp(Dungeon.hero, Dungeon.hero.pos)) {
				Dungeon.level.drop(weapon, Dungeon.hero.pos).sprite.drop();
			} else {
				Dungeon.hero.spend(-Item.TIME_TO_PICK_UP);
				GLog.i(Messages.get(Hero.class, "you_now_have", weapon.name()));
			}
			weapon = null;
		}
		super.type(type);
	}

	@Override
	public String desc() {
		if (!Ghost.Quest.completed()
				&& (ShatteredPixelDungeon.scene() instanceof GameScene || ShatteredPixelDungeon.scene() instanceof AlchemyScene)){
			return Messages.get(this, "desc_no_quest");
		}
		
		String desc = super.desc();

		if (isEquipped( Dungeon.hero )){
			if (!cursed){

				if (level() < levelCap)
					desc+= "\n\n" + Messages.get(this, "desc_hint");

			} else {
				desc += "\n\n" + Messages.get(this, "desc_cursed");
			}
		}

		if (weapon != null || weapon2 != null || wand != null || armor != null) {
			desc += "\n";

			if (weapon != null) {
				desc += "\n" + Messages.get(this, "desc_weapon", Messages.titleCase(weapon.title()));
			}

			if (weapon2 != null) {
				desc += "\n" + Messages.get(this, "desc_weapon2", Messages.titleCase(weapon2.title()));
			}

			if (wand != null) {
				desc += "\n" + Messages.get(this, "desc_wand", Messages.titleCase(wand.title()));
			}

			if (armor != null) {
				desc += "\n" + Messages.get(this, "desc_armor", Messages.titleCase(armor.title()));
			}

			desc += "\n" + Messages.get(this, "desc_strength", ghostStrength());

		}
		
		return desc;
	}

	@Override
	public String getTypeMessage(int type) {
		return Messages.get(this, "type",
				Math.round(100*rechargeModifier(type)),
				Math.round(100*regenModifier(type)),
				ghostHealth(type)) + "\n\n" + super.getTypeMessage(type);
	}

	@Override
	public int value() {
        if (weapon != null || weapon2 != null || wand != null || armor != null) {
            return -1;
        }
        return super.value();
	}

	@Override
	public String status() {
		if (ghost == null && ghostID != 0){
			try {
				findGhost();
			} catch ( ClassCastException e ){
				ShatteredPixelDungeon.reportException(e);
				ghostID = 0;
			}
		}
		if (ghost == null){
			return super.status();
		} else {
			return ((ghost.HP*100) / ghost.HT) + "%";
		}
	}
	
	@Override
	protected ArtifactBuff passiveBuff() {
		return new roseRecharge();
	}

	public float rechargeModifier(){
		return rechargeModifier(type());
	}

	public float rechargeModifier(int type){
		switch (type){
			case 1:
				return 1.0f;
			case 2:
				return 0.33f;
			case 3:
				return 0.4f;
		}
		return 1;
	}

	public float regenModifier(){
		return regenModifier(type());
	}

	public float regenModifier(int type){
		switch (type){
			case 1:
				return 1.0f;
			case 2:
				return 2f;
			case 3:
				return 1.25f;
		}
		return 1;
	}
	
	@Override
	public void charge(Hero target, float amount) {
		if (cursed || target.buff(MagicImmune.class) != null) return;

		if (ghost == null){
			if (charge < chargeCap) {
				partialCharge += 4*amount*rechargeModifier();
				while (partialCharge >= 1f){
					charge++;
					partialCharge--;
				}
				if (charge >= chargeCap) {
					charge = chargeCap;
					partialCharge = 0;
					GLog.p(Messages.get(DriedRose.class, "charged"));
				}
				updateQuickslot();
			}
		} else if (ghost.HP < ghost.HT) {
			int heal = Math.round((1 + level()/3f)*amount*rechargeModifier());
			ghost.HP = Math.min( ghost.HT, ghost.HP + heal);
			if (ghost.sprite != null) {
				ghost.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(heal), FloatingText.HEALING);
			}
			updateQuickslot();
		}
	}
	
	@Override
	public Item upgrade() {
		if (level() >= 9)
			image = ItemSpriteSheet.ARTIFACT_ROSE3;
		else if (level() >= 4)
			image = ItemSpriteSheet.ARTIFACT_ROSE2;

		//For upgrade transferring via well of transmutation
		droppedPetals = Math.max( level(), droppedPetals );
		
		if (ghost != null){
			ghost.updateRose();
			ghost.HP = Math.min(ghost.HP+8, ghost.HT);
		}

		return super.upgrade();
	}
	
	public Weapon ghostWeapon(){
		return weapon;
	}

	public Wand ghostWand(){
		return wand;
	}
	
	public Armor ghostArmor(){
		return armor;
	}

	private static final String TALKEDTO =      "talkedto";
	private static final String FIRSTSUMMON =   "firstsummon";
	private static final String GHOSTID =       "ghostID";
	private static final String PETALS =        "petals";
	
	private static final String WEAPON =        "weapon";
	private static final String WEAPON2=        "weapon2";
	private static final String WAND   =        "wand";
	private static final String ARMOR =         "armor";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);

		bundle.put( TALKEDTO, talkedTo );
		bundle.put( FIRSTSUMMON, firstSummon );
		bundle.put( GHOSTID, ghostID );
		bundle.put( PETALS, droppedPetals );
		
		if (weapon != null) bundle.put( WEAPON, weapon );
		if (weapon2 != null) bundle.put( WEAPON2, weapon2 );
		if (wand != null) bundle.put( WAND, wand );
		if (armor != null)  bundle.put( ARMOR, armor );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);

		talkedTo = bundle.getBoolean( TALKEDTO );
		firstSummon = bundle.getBoolean( FIRSTSUMMON );
		ghostID = bundle.getInt( GHOSTID );
		droppedPetals = bundle.getInt( PETALS );
		
		if (bundle.contains(WEAPON)) weapon = (MeleeWeapon)bundle.get( WEAPON );
		if (bundle.contains(WEAPON2)) weapon2 = (MeleeWeapon)bundle.get( WEAPON2 );
		if (bundle.contains(WAND)) wand = (Wand) bundle.get( WAND );
		if (bundle.contains(ARMOR))  armor = (Armor)bundle.get( ARMOR );
	}

	public class roseRecharge extends ArtifactBuff {

		@Override
		public boolean act() {
			
			spend( TICK );
			
			if (ghost == null && ghostID != 0){
				findGhost();
			}

			if (ghost != null && !ghost.isAlive()){
				ghost = null;
			}
			
			//rose does not charge while ghost hero is alive
			if (ghost != null && !cursed && target.buff(MagicImmune.class) == null){
				
				//heals to full over 500 turns
				if (ghost.HP < ghost.HT && Regeneration.regenOn()) {
					partialCharge += (ghost.HT / 500f) * RingOfEnergy.artifactChargeMultiplier(target)*regenModifier();
					updateQuickslot();
					
					while (partialCharge > 1) {
						ghost.HP++;
						partialCharge--;
						if (ghost.HP == ghost.HT){
							partialCharge = 0;
						}
					}
				} else {
					partialCharge = 0;
				}
				
				return true;
			}
			
			if (charge < chargeCap
					&& !cursed
					&& target.buff(MagicImmune.class) == null
					&& Regeneration.regenOn()) {
				//500 turns to a full charge
				partialCharge += (1/5f * RingOfEnergy.artifactChargeMultiplier(target))*rechargeModifier();
				while (partialCharge > 1){
					charge++;
					partialCharge--;
					if (charge == chargeCap){
						partialCharge = 0f;
						GLog.p( Messages.get(DriedRose.class, "charged") );
					}
				}
			} else if (cursed && Random.Int(100) == 0) {

				ArrayList<Integer> spawnPoints = new ArrayList<>();

				for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
					int p = target.pos + PathFinder.NEIGHBOURS8[i];
					if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
						spawnPoints.add(p);
					}
				}

				if (spawnPoints.size() > 0) {
					Wraith.spawnAt(Random.element(spawnPoints), Wraith.class);
					Sample.INSTANCE.play(Assets.Sounds.CURSED);
				}

			}

			updateQuickslot();

			return true;
		}
	}
	
	public CellSelector.Listener ghostDirector = new CellSelector.Listener(){
		
		@Override
		public void onSelect(Integer cell) {
			if (cell == null) return;
			
			Sample.INSTANCE.play( Assets.Sounds.GHOST );

			ghost.directTocell(cell);

		}
		
		@Override
		public String prompt() {
			return  "\"" + Messages.get(GhostHero.class, "direct_prompt") + "\"";
		}
	};

	public static class Petal extends Item {

		{
			stackable = true;
			dropsDownHeap = true;
			
			image = ItemSpriteSheet.PETAL;
		}

		@Override
		public boolean doPickUp(Hero hero, int pos) {
			Catalog.setSeen(getClass());
			Statistics.itemTypesDiscovered.add(getClass());
			DriedRose rose = hero.belongings.getItem( DriedRose.class );

			if (rose == null){
				GLog.w( Messages.get(this, "no_rose") );
				return false;
			} if ( rose.level() >= rose.levelCap ){
				GLog.i( Messages.get(this, "no_room") );
				hero.spendAndNext(pickupDelay());
				return true;
			} else {

				rose.upgrade();
				Catalog.countUse(rose.getClass());
				if (rose.level() == rose.levelCap) {
					GLog.p( Messages.get(this, "maxlevel") );
				} else
					GLog.i( Messages.get(this, "levelup") );

				Sample.INSTANCE.play( Assets.Sounds.DEWDROP );
				GameScene.pickUp(this, pos);
				hero.spendAndNext(pickupDelay());
				return true;

			}
		}

		@Override
		public boolean isUpgradable() {
			return false;
		}

		@Override
		public boolean isIdentified() {
			return true;
		}

	}

	public static class GhostHero extends DirectableAlly {

		{
			spriteClass = GhostSprite.class;

			flying = true;
			
			state = HUNTING;
			
			properties.add(Property.UNDEAD);
			properties.add(Property.INORGANIC);
		}
		
		public DriedRose rose = null;
		private boolean weapon2 = false;
		
		public GhostHero(){
			super();
		}

		public GhostHero(DriedRose rose){
			super();
			this.rose = rose;
			updateRose();
			HP = HT;
		}

		@Override
		public void defendPos(int cell) {
			yell(Messages.get(this, "directed_position_" + Random.IntRange(1, 5)));
			super.defendPos(cell);
		}

		@Override
		public void followHero() {
			yell(Messages.get(this, "directed_follow_" + Random.IntRange(1, 5)));
			super.followHero();
		}

		@Override
		public void targetChar(Char ch) {
			yell(Messages.get(this, "directed_attack_" + Random.IntRange(1, 5)));
			super.targetChar(ch);
		}

		private void updateRose(){
			if (rose == null) {
				rose = Dungeon.hero.belongings.getItem(DriedRose.class);
				if (rose != null) {
					rose.ghost = this;
					rose.ghostID = id();
				}
			}
			
			//same dodge as the hero
			defenseSkill = (Dungeon.hero.lvl+4);
			if (rose == null) return;
			HT = rose.ghostHealth();
		}

		public Weapon weapon(){
			if (rose != null){
				if (rose.type() == 2){
					return weapon2 ? rose.weapon2 : rose.weapon;
				} else
					return rose.weapon;
			}
			else                return null;
		}

		public Armor armor(){
			if (rose != null){
				if (rose.type() == 2)
					return null;
				return rose.armor;
			}
			else                return null;
		}

		@Override
		protected boolean act() {
			updateRose();
			if (rose == null
					|| !rose.isEquipped(Dungeon.hero)
					|| Dungeon.hero.buff(MagicImmune.class) != null){
				damage(1, new NoRoseDamage());
			} else if (rose != null && rose.type() == 3 && buff(LightCooldown.class) == null){
				Buff.affect(this, LightCooldown.class, 10);
				HolyBomb.Effect shield;
				GameScene.effect(shield = new HolyBomb.Effect(sprite));
				shield.putOut();
				PathFinder.buildDistanceMap( pos, BArray.not( Dungeon.level.solid, null ), 2 );
				for (int i = 0; i < PathFinder.distance.length; i++) {
					if (PathFinder.distance[i] < Integer.MAX_VALUE) {
						Char ch = Actor.findChar(i);
						if (ch != null){
							if (ch.alignment == Dungeon.hero.alignment && ch != Dungeon.hero && ch != this) {
								Regeneration.regenerate(ch, rose.level()*3/4);
								Buff.affect(ch, AttunementBoost.class, 10).boost(rose.level()*0.2f);
							}
							if (ch.alignment == Alignment.ENEMY){
								ch.damage(rose.level() * 2, new DamageSource() {
									@Override
									public EnumSet<DamageProperty> initDmgProperties() {
										return EnumSet.of(DamageProperty.MAGICAL, DamageProperty.HOLY);
									}
								});
							}
						}
					}
				}
			}
			
			if (!isAlive()) {
				return true;
			}
			return super.act();
		}

		public static class NoRoseDamage implements DamageSource{
            @Override
            public EnumSet<DamageProperty> initDmgProperties() {
                return EnumSet.of(DamageProperty.CRUMBLING);
            }
        }

		@Override
		public int attackSkill(Char target) {
			
			//same accuracy as the hero.
			int acc = Dungeon.hero.lvl + 9;
			
			if (weapon() != null){
				acc *= weapon().accuracyFactor( this, target );
			}
			if (rose != null && rose.weapon2 != null)
				acc = acc * 2/3;
			
			return acc;
		}
		
		@Override
		public float attackDelay() {
			float delay = super.attackDelay();
			if (weapon() != null){
				delay *= weapon().delayFactor(this);
			}
			if (rose != null){
				if (rose.weapon2 != null)
					delay /= 2;
				if (rose.wand != null)
					delay *= 2;
			}
			return delay;
		}
		
		@Override
		protected boolean canAttack(Char enemy) {
			if (rose != null && rose.wand != null && buff(ScrollOfAntiMagic.EnemyBuff.class) == null && buff(WandStrikeCooldown.class) == null){
				return super.canAttack(enemy) || (rose.wand.tryToZap(this, enemy.pos)
						&& new Ballistica( pos, enemy.pos, rose.wand.collisionProperties(enemy.pos)).collisionPos == enemy.pos);
			}
			return super.canAttack(enemy) || (weapon() != null && weapon().canReach(this, enemy.pos));
		}

		@Override
		protected boolean doAttack(Char enemy) {
			if (rose != null && rose.wand != null && buff(WandStrikeCooldown.class) == null && canAttack(enemy)){
				if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
					sprite.zap( enemy.pos );
					return false;
				} else {
					zap(new Ballistica( pos, enemy.pos, rose.wand.collisionProperties(enemy.pos)), rose.wand);
					return true;
				}
			}
			return super.doAttack(enemy);
		}

		@Override
		public void onAttackComplete() {
			super.onAttackComplete();
			if (rose != null && rose.type() == 2 && rose.weapon2 != null)
				weapon2 = !weapon2;
		}

		@Override
		public int damageRoll() {
			int dmg = 0;
			if (weapon() != null){
				dmg += weapon().damageRoll(this);
			} else {
				dmg += Random.NormalIntRange(0, 5);
			}
			
			return dmg;
		}
		
		@Override
		public int attackProc(Char enemy, int damage) {
			damage = super.attackProc(enemy, damage);

			if (weapon() != null) {
				damage = weapon().proc(this, enemy, damage);
				if (!enemy.isAlive() && enemy == Dungeon.hero) {
					Dungeon.fail(this);
					GLog.n(Messages.capitalize(Messages.get(Char.class, "kill", name())));
				}
			}

			return damage;
		}
		
		@Override
		public int defenseProc(Char enemy, int damage) {
			if (armor() != null) {
				damage = armor().proc( enemy, this, damage );
			}
			return super.defenseProc(enemy, damage);
		}
		
		@Override
		public void damage(int dmg, DamageSource src) {
			super.damage( dmg, src );
			
			//for the rose status indicator
			Item.updateQuickslot();
		}
		
		@Override
		public float speed() {
			float speed = super.speed();

			//moves 2 tiles at a time when returning to the hero
			if (state == WANDERING
					&& defendingPos == -1
					&& Dungeon.level.distance(pos, Dungeon.hero.pos) > 1){
				speed *= 2;
			}
			
			return speed;
		}
		
		@Override
		public int defenseSkill(Char enemy) {
			int defense = super.defenseSkill(enemy);

			if (defense != 0 && armor() != null ){
				defense = Math.round(armor().evasionFactor( this, defense ));
			}
			
			return defense;
		}

		@Override
		public int drRoll() {
			int dr = super.drRoll();
			if (armor() != null){
				dr += Random.NormalIntRange( armor().DRMin(), armor().DRMax());
			}
			if (weapon() != null){
				dr += Random.NormalIntRange( 0, weapon().defenseFactor( this ));
			}
			return dr;
		}

		@Override
		public int glyphLevel(Class<? extends Armor.Glyph> cls) {
			if (armor() != null && armor().hasGlyph(cls, this)){
				return Math.max(super.glyphLevel(cls), armor().buffedLvl());
			} else {
				return super.glyphLevel(cls);
			}
		}

		@Override
		public boolean interact(Char c) {
			updateRose();
			if (c == Dungeon.hero && rose != null && !rose.talkedTo){
				rose.talkedTo = true;
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show(new WndQuest(GhostHero.this, Messages.get(GhostHero.this, "introduce") ));
					}
				});
				return true;
			} else {
				return super.interact(c);
			}
		}

		@Override
		public float targetPriority() {
			if (rose != null && rose.type() == 3)
				return 0.75f;
			return 4.0f;
		}

		@Override
		public void die(Object cause) {
			sayDefeated();
			super.die(cause);
		}

		@Override
		public void destroy() {
			updateRose();
			//TODO stasis?
			if (rose != null) {
				rose.ghost = null;
				rose.charge = 0;
				rose.partialCharge = 0;
				rose.ghostID = -1;
			}
			super.destroy();
		}

		public void onZapComplete(Ballistica shot, Wand wand) {
			zap(shot, wand);
			next();
		}

		public void zap(Ballistica shot, Wand wand){
			spend(1f);

			Invisibility.dispel(this);

			if (Dungeon.isChallenged(Conducts.Conduct.COINFLIP) && Random.Int(2) == 0){
				PointF point = DungeonTilemap.tileCenterToWorld(shot.collisionPos);
				FloatingText.show( point.x, point.y, shot.collisionPos, defenseVerb(), CharSprite.NEUTRAL, FloatingText.MISS_COINFLIP, true );
				Buff.detach(this, Talent.FightingWizardryTracker.class);
			} else {
				wand.onZap(shot, this);
				float rechargeDuration = wand.rechargeModifier() * wand.chargesPerCast() * Wand.Charger.BASE_CHARGE_DELAY * 3 / 4;
				Buff.affect(this, WandStrikeCooldown.class, rechargeDuration);
				sprite.showStatusWithIcon(CharSprite.WARNING, Integer.toString((int) rechargeDuration), FloatingText.MAGIC_DMG);

			}
		}
		
		public void sayAppeared(){
			if (Dungeon.hero.buff(AscensionChallenge.class) != null){
				yell( Messages.get( this, "dialogue_ascension_" + Random.IntRange(1, 6) ));

			} else {

				int depth = (Dungeon.depth - 1) / Dungeon.chapterSize();

				//only some lines are said on the first floor of a depth
				int variant = Dungeon.depth % Dungeon.chapterSize() == 1 ? Random.IntRange(1, 3) : Random.IntRange(1, 6);

				if (Dungeon.branch == AbyssLevel.BRANCH){
					depth = 10;
				}

				switch (depth) {
					case 0:
						yell(Messages.get(this, "dialogue_sewers_" + variant));
						break;
					case 1:
						yell(Messages.get(this, "dialogue_prison_" + variant));
						break;
					case 2:
						yell(Messages.get(this, "dialogue_caves_" + variant));
						break;
					case 3:
						yell(Messages.get(this, "dialogue_city_" + variant));
						break;
					case 4:
						yell(Messages.get(this, "dialogue_halls_" + variant));
						break;
					case 10:
					default:
						yell(Messages.get(this, "dialogue_abyss_" + variant));
						break;
				}
			}
			if (ShatteredPixelDungeon.scene() instanceof GameScene) {
				Sample.INSTANCE.play( Assets.Sounds.GHOST );
			}
		}
		
		public void sayBoss(){
			int depth = (Dungeon.depth - 1) / Dungeon.chapterSize();
			
			switch(depth){
				case 0:
					yell( Messages.get( this, "seen_goo_" + Random.IntRange(1, 3) ));
					break;
				case 1:
					yell( Messages.get( this, "seen_tengu_" + Random.IntRange(1, 3) ));
					break;
				case 2:
					yell( Messages.get( this, "seen_dm300_" + Random.IntRange(1, 3) ));
					break;
				case 3:
					yell( Messages.get( this, "seen_king_" + Random.IntRange(1, 3) ));
					break;
				case 4: default:
					yell( Messages.get( this, "seen_yog_" + Random.IntRange(1, 3) ));
					break;
			}
			Sample.INSTANCE.play( Assets.Sounds.GHOST );
		}
		
		public void sayDefeated(){
			if (BossHealthBar.isAssigned()){
				yell( Messages.get( this, "defeated_by_boss_" + Random.IntRange(1, 3) ));
			} else {
				yell( Messages.get( this, "defeated_by_enemy_" + Random.IntRange(1, 3) ));
			}
			Sample.INSTANCE.play( Assets.Sounds.GHOST );
		}
		
		public void sayHeroKilled(){
			yell( Messages.get( this, "player_killed_" + Random.IntRange(1, 3) ));
			GLog.newLine();
			Sample.INSTANCE.play( Assets.Sounds.GHOST );
		}
		
		public void sayAnhk(){
			yell( Messages.get( this, "blessed_ankh_" + Random.IntRange(1, 3) ));
			Sample.INSTANCE.play( Assets.Sounds.GHOST );
		}
		
		{
			immunities.add( CorrosiveGas.class );
			immunities.add( Burning.class );
			immunities.add( FrostBurn.class );
			immunities.add( ScrollOfRetribution.class );
			immunities.add( ScrollOfPsionicBlast.class );
			immunities.add( AllyBuff.class );
		}

	}

	public static class WandStrikeCooldown extends FlavourBuff {
		@Override
		public int icon() {
			return BuffIndicator.TIME;
		}
		public void tintIcon(Image icon) { icon.hardlight(0x8d2cab); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / Wand.Charger.BASE_CHARGE_DELAY*3/4); }
	}
	public static class LightCooldown extends FlavourBuff{}
	
	private static class WndGhostHero extends Window{
		
		private static final int BTN_SIZE	= 32;
		private static final float GAP		= 2;
		private static final float BTN_GAP	= 12;
		private static final int WIDTH		= 116;
		
		private ItemButton btnWeapon;
		private ItemButton btnWeapon2;
		private ItemButton btnArmor;

		private void handleWeapon(ItemButton button, DriedRose rose, int weapon) {
			if ((weapon == 1 ? rose.weapon : rose.weapon2) != null){
				button.item(new WndBag.Placeholder(ItemSpriteSheet.WEAPON_HOLDER));
				if (!(weapon == 1 ? rose.weapon : rose.weapon2).doPickUp(Dungeon.hero)){
					Dungeon.level.drop( (weapon == 1 ? rose.weapon : rose.weapon2), Dungeon.hero.pos);
				}
				if (weapon == 1)
					rose.weapon = null;
				else
					rose.weapon2 = null;
			} else {
				GameScene.selectItem(new WndBag.ItemSelector() {

					@Override
					public String textPrompt() {
						return Messages.get(WndGhostHero.class, "weapon_prompt");
					}

					@Override
					public Class<?extends Bag> preferredBag(){
						return Belongings.Backpack.class;
					}

					@Override
					public boolean itemSelectable(Item item) {
						return item instanceof MeleeWeapon;
					}

					@Override
					public void onSelect(Item item) {
						if (!(item instanceof MeleeWeapon)) {
							//do nothing, should only happen when window is cancelled
						} else if (item.unique) {
							GLog.w( Messages.get(WndGhostHero.class, "cant_unique"));
							hide();
						} else if (item.cursed || !item.cursedKnown) {
							GLog.w(Messages.get(WndGhostHero.class, "cant_cursed"));
							hide();
						}  else if (!item.levelKnown && ((MeleeWeapon)item).STRReq(0) > rose.ghostStrength()){
							GLog.w( Messages.get(WndGhostHero.class, "cant_strength_unknown"));
							hide();
						} else if (((MeleeWeapon)item).STRReq() > rose.ghostStrength()) {
							GLog.w( Messages.get(WndGhostHero.class, "cant_strength"));
							hide();
						} else {
							if (item.isEquipped(Dungeon.hero)){
								((MeleeWeapon) item).doUnequip(Dungeon.hero, false, false);
							} else {
								item.detach(Dungeon.hero.belongings.backpack);
							}
							if (weapon == 1)
								rose.weapon = (MeleeWeapon) item;
							else
								rose.weapon2 = (MeleeWeapon) item;
							button.item((weapon == 1 ? rose.weapon : rose.weapon2));
						}

					}
				});
			}
		}
		
		WndGhostHero(final DriedRose rose){
			
			IconTitle titlebar = new IconTitle();
			titlebar.icon( new ItemSprite(rose) );
			titlebar.label( Messages.get(this, "title") );
			titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );
			
			RenderedTextBlock message =
					PixelScene.renderTextBlock(rose.getTypeBasedString( "desc_window", rose.type(), rose.ghostStrength()), 6);
			message.maxWidth( WIDTH );
			message.setPos(0, titlebar.bottom() + GAP);
			add( message );

			if (rose.type() != 3) {

				btnWeapon = new ItemButton() {
					@Override
					protected void onClick() {
						handleWeapon(this, rose, 1);
					}

					@Override
					protected boolean onLongClick() {
						if (item() != null && item().name() != null) {
							GameScene.show(new WndInfoItem(item()));
							return true;
						}
						return false;
					}
				};
				btnWeapon.setRect((WIDTH - BTN_GAP) / 2 - BTN_SIZE, message.top() + message.height() + GAP, BTN_SIZE, BTN_SIZE);
				if (rose.weapon != null) {
					btnWeapon.item(rose.weapon);
				} else {
					btnWeapon.item(new WndBag.Placeholder(ItemSpriteSheet.WEAPON_HOLDER));
				}
				add(btnWeapon);
			} else {
				btnWeapon = new ItemButton() {
					@Override
					protected void onClick() {
						if (rose.wand != null) {
							item(new WndBag.Placeholder(ItemSpriteSheet.WAND_HOLDER));
							if (!rose.wand.doPickUp(Dungeon.hero)) {
								Dungeon.level.drop(rose.wand, Dungeon.hero.pos);
							}
							rose.wand = null;
						} else {
							GameScene.selectItem(new WndBag.ItemSelector() {

								@Override
								public String textPrompt() {
									return Messages.get(WndGhostHero.class, "wand_prompt");
								}

								@Override
								public Class<? extends Bag> preferredBag() {
									return MagicalHolster.class;
								}

								@Override
								public boolean itemSelectable(Item item) {
									return item instanceof Wand;
								}

								@Override
								public void onSelect(Item item) {
									if (!(item instanceof Wand)) {
										//do nothing, should only happen when window is cancelled
									} else if (item.unique) {
										GLog.w(Messages.get(WndGhostHero.class, "cant_unique"));
										hide();
									} else if (item.cursed || !item.cursedKnown) {
										GLog.w(Messages.get(WndGhostHero.class, "cant_cursed"));
										hide();
									} else {
										if (item.isEquipped(Dungeon.hero)) {
											((Wand) item).doUnequip(Dungeon.hero, false, false);
										} else {
											item.detach(Dungeon.hero.belongings.backpack);
										}
										rose.wand = (Wand) item;
										item(rose.wand);
									}

								}
							});
						}
					}

					@Override
					protected boolean onLongClick() {
						if (item() != null && item().name() != null) {
							GameScene.show(new WndInfoItem(item()));
							return true;
						}
						return false;
					}
				};
				btnWeapon.setRect((WIDTH - BTN_GAP) / 2 - BTN_SIZE, message.top() + message.height() + GAP, BTN_SIZE, BTN_SIZE);
				if (rose.wand != null) {
					btnWeapon.item(rose.wand);
				} else {
					btnWeapon.item(new WndBag.Placeholder(ItemSpriteSheet.WAND_HOLDER));
				}
				add(btnWeapon);
			}

			if (rose.type() == 2){
				btnWeapon2 = new ItemButton(){
					@Override
					protected void onClick() {
						handleWeapon(this, rose, 2);
					}

					@Override
					protected boolean onLongClick() {
						if (item() != null && item().name() != null){
							GameScene.show(new WndInfoItem(item()));
							return true;
						}
						return false;
					}
				};
				btnWeapon2.setRect( btnWeapon.right() + BTN_GAP, btnWeapon.top(), BTN_SIZE, BTN_SIZE );
				if (rose.weapon2 != null) {
					btnWeapon2.item(rose.weapon2);
				} else {
					btnWeapon2.item(new WndBag.Placeholder(ItemSpriteSheet.WEAPON_HOLDER));
				}
				add( btnWeapon2 );
				resize(WIDTH, (int)( btnWeapon2.bottom() + GAP));
			} else {

				btnArmor = new ItemButton() {
					@Override
					protected void onClick() {
						if (rose.armor != null) {
							item(new WndBag.Placeholder(ItemSpriteSheet.ARMOR_HOLDER));
							if (!rose.armor.doPickUp(Dungeon.hero)) {
								Dungeon.level.drop(rose.armor, Dungeon.hero.pos);
							}
							rose.armor = null;
						} else {
							GameScene.selectItem(new WndBag.ItemSelector() {

								@Override
								public String textPrompt() {
									return Messages.get(WndGhostHero.class, "armor_prompt");
								}

								@Override
								public Class<? extends Bag> preferredBag() {
									return Belongings.Backpack.class;
								}

								@Override
								public boolean itemSelectable(Item item) {
									return item instanceof Armor && !(item instanceof ConjurerSet);
								}

								@Override
								public void onSelect(Item item) {
									if (!(item instanceof Armor)) {
										//do nothing, should only happen when window is cancelled
									} else if (item.unique || ((Armor) item).checkSeal() != null) {
										GLog.w(Messages.get(WndGhostHero.class, "cant_unique"));
										hide();
									} else if (item.cursed || !item.cursedKnown) {
										GLog.w(Messages.get(WndGhostHero.class, "cant_cursed"));
										hide();
									} else if (!item.levelKnown && ((Armor) item).STRReq(0) > rose.ghostStrength()) {
										GLog.w(Messages.get(WndGhostHero.class, "cant_strength_unknown"));
										hide();
									} else if (((Armor) item).STRReq() > rose.ghostStrength()) {
										GLog.w(Messages.get(WndGhostHero.class, "cant_strength"));
										hide();
									} else {
										if (item.isEquipped(Dungeon.hero)) {
											((Armor) item).doUnequip(Dungeon.hero, false, false);
										} else {
											item.detach(Dungeon.hero.belongings.backpack);
										}
										rose.armor = (Armor) item;
										item(rose.armor);
									}

								}
							});
						}
					}

					@Override
					protected boolean onLongClick() {
						if (item() != null && item().name() != null) {
							GameScene.show(new WndInfoItem(item()));
							return true;
						}
						return false;
					}
				};
				btnArmor.setRect(btnWeapon.right() + BTN_GAP, btnWeapon.top(), BTN_SIZE, BTN_SIZE);
				if (rose.armor != null) {
					btnArmor.item(rose.armor);
				} else {
					btnArmor.item(new WndBag.Placeholder(ItemSpriteSheet.ARMOR_HOLDER));
				}
				add(btnArmor);

				resize(WIDTH, (int)( btnArmor.bottom() + GAP));
			}
		}
	
	}
}
