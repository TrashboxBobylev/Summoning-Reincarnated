/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.GooBlob;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.*;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPassage;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Bomb extends Item {
	
	{
		image = ItemSpriteSheet.BOMB;

		defaultAction = AC_LIGHTTHROW;
		usesTargeting = true;

		stackable = true;
	}

	public Fuse fuse;
	public int fuseDelay = 1;
	public Class<? extends Buff> fuseTriggerClass = null;
	public boolean harmless = false;

	//FIXME using a static variable for this is kinda gross, should be a better way
	protected static boolean lightingFuse = false;

	private static final String AC_LIGHTTHROW = "LIGHTTHROW";

	@Override
	public boolean isSimilar(Item item) {
		return super.isSimilar(item) && this.fuse == ((Bomb) item).fuse;
	}
	
	public boolean explodesDestructively(){
		return !harmless;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions( hero );
		actions.add ( AC_LIGHTTHROW );
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {

		if (action.equals(AC_LIGHTTHROW)) {
			lightingFuse = true;
			action = AC_THROW;
		} else
			lightingFuse = false;

		super.execute(hero, action);
	}

	protected Fuse createFuse(){
		return new Fuse();
	}

	@Override
	protected void onThrow( int cell ) {
		if (!Dungeon.level.pit[ cell ] && (lightingFuse || curUser == null)) {
			Actor.addDelayed(fuse = createFuse().ignite(this), fuseDelay);
			if (fuseTriggerClass != null)
				((FuseBuff)(Buff.affect(Dungeon.hero, fuseTriggerClass))).set(cell);
		}
		if (Actor.findChar( cell ) != null && !(Actor.findChar( cell ) instanceof Hero) ){
			ArrayList<Integer> candidates = new ArrayList<>();
			for (int i : PathFinder.NEIGHBOURS8)
				if (Dungeon.level.passable[cell + i])
					candidates.add(cell + i);
			int newCell = candidates.isEmpty() ? cell : Random.element(candidates);
			Dungeon.level.drop( this, newCell ).sprite.drop( cell );
		} else
			super.onThrow( cell );
	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {
		if (fuse != null) {
			GLog.w( Messages.get(this, "snuff_fuse") );
			return false;
		}
		return super.doPickUp(hero, pos);
	}

	public static int damageRoll(){
		//sewers: 8-22
		//prison: 24-47
		//caves: 40-72
		//city: 56-97
		//halls: 72-122
		return Random.NormalIntRange(minDamage(), maxDamage());
	}

	public static int maxDamage() {
		int baseDamage = 22 + (Dungeon.chapterNumber()) * 25;
//		if (Dungeon.hero.pointsInTalent(Talent.NUCLEAR_RAGE) > 1)
//			baseDamage *= 1.05f + 0.1f * (Dungeon.hero.pointsInTalent(Talent.NUCLEAR_RAGE)-1);
		return baseDamage;
	}

	public static int minDamage() {
		int baseDamage = 8 + (Dungeon.chapterNumber())*16;
//		if (Dungeon.hero.pointsInTalent(Talent.NUCLEAR_RAGE) > 0)
//			baseDamage *= 1.05f + 0.1f * (Dungeon.hero.pointsInTalent(Talent.NUCLEAR_RAGE));
		return baseDamage;
	}

//	public static float nuclearBoost(){
//		return Math.max(1f, (85 + 15 * Dungeon.hero.pointsInTalent(Talent.NUCLEAR_RAGE)) / 100f);
//	}

	public void explode(int cell){
		//We're blowing up, so no need for a fuse anymore.
		this.fuse = null;

		Sample.INSTANCE.play( Assets.Sounds.BLAST );

		if (explodesDestructively()) {
			
			ArrayList<Char> affected = new ArrayList<>();
			
			if (Dungeon.level.heroFOV[cell]) {
				CellEmitter.center(cell).burst(BlastParticle.FACTORY, 30);
			}
			
			boolean terrainAffected = false;
			for (int n : PathFinder.NEIGHBOURS9) {
				int c = cell + n;
				if (c >= 0 && c < Dungeon.level.length()) {
					if (Dungeon.level.heroFOV[c]) {
						CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4);
					}
					
					if (Dungeon.level.flamable[c]) {
						Dungeon.level.destroy(c);
						GameScene.updateMap(c);
						terrainAffected = true;
					}
					
					//destroys items / triggers bombs caught in the blast.
					Heap heap = Dungeon.level.heaps.get(c);
					if (heap != null)
						heap.explode();
					
					Char ch = Actor.findChar(c);
					if (ch != null) {
						affected.add(ch);
					}
				}
			}
			
			for (Char ch : affected){

				//if they have already been killed by another bomb
				if(!ch.isAlive()){
					continue;
				}

				int dmg = Bomb.damageRoll();
				if (ch instanceof Hero && Dungeon.isChallenged(Conducts.Conduct.EXPLOSIONS))
					dmg /= 2;

				//those not at the center of the blast take less damage
				if (ch.pos != cell){
					dmg = Math.round(dmg*0.67f);
				}

				dmg -= ch.drRoll();

				if (dmg > 0 && !harmless && !((Dungeon.isChallenged(Conducts.Conduct.PACIFIST)))) {
					ch.damage(dmg, this);
				}
				
				if (ch == Dungeon.hero && !ch.isAlive()) {
					if (this instanceof ConjuredBomb){
						Badges.validateDeathFromFriendlyMagic();
					}
					GLog.n(Messages.get(this, "ondeath"));
					Dungeon.fail(this);
				}
			}
			
			if (terrainAffected) {
				Dungeon.observe();
			}
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

	private Class<? extends Bomb>[] enhancedBombs = new Class[] {
			Firebomb.class,
			FrostBomb.class,
			SupplyBomb.class,
			RegrowthBomb.class,
			WoollyBomb.class,
			HolyBomb.class,
			Webbomb.class,
			Flashbang.class,
			Noisemaker.class,
			ShockBomb.class
	};
	
	@Override
	public Item random() {
		if (Dungeon.isChallenged(Conducts.Conduct.EXPLOSIONS)) {
			switch (Random.Int(15)) {
				case 0:
				case 4:
				case 5:
					return this;
				case 1:
				case 2:
				case 3:
					return new DoubleBomb();
				default:
					return Reflection.newInstance(Random.element(enhancedBombs));
			}
		}
		switch(Random.Int( 15 )){
			case 0: case 1: case 2: case 3: case 4:
				return new DoubleBomb();
			case 14:
				return Reflection.newInstance(Random.element(enhancedBombs));
			default:
				return this;
		}
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return fuse != null ? new ItemSprite.Glowing( 0xFF0000, 0.6f) : null;
	}

	@Override
	public int value() {
		return 20 * quantity;
	}

	@Override
	public String desc() {
		String desc_fuse = Messages.get(this, "desc",
				Math.round(minDamage()*0.8), Math.round(maxDamage()*0.8))+ "\n\n" + Messages.get(this, "desc_fuse");
		if (fuse != null){
			desc_fuse = Messages.get(this, "desc",
					Math.round(minDamage()*0.8), Math.round(maxDamage()*0.8)) + "\n\n" + Messages.get(this, "desc_burning");
		}

		return desc_fuse;
	}

	private static final String FUSE = "fuse";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( FUSE, fuse );
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains( FUSE ))
			Actor.add( fuse = ((Fuse)bundle.get(FUSE)).ignite(this) );
	}

	//used to track the death from friendly magic badge, if an explosion was conjured by magic
	public static class ConjuredBomb extends Bomb{};

	public interface FuseBuff {
		void set(int cell);
	}

	public static class Fuse extends Actor{

		{
			actPriority = BLOB_PRIO+1; //after hero, before other actors
		}

		protected Bomb bomb;

		public Fuse ignite(Bomb bomb){
			this.bomb = bomb;
			return this;
		}

		@Override
		protected boolean act() {

			//something caused our bomb to explode early, or be defused. Do nothing.
			if (bomb.fuse != this){
				Actor.remove( this );
				return true;
			}

			//look for our bomb, remove it from its heap, and blow it up.
			for (Heap heap : Dungeon.level.heaps.valueList()) {
				if (heap.items.contains(bomb)) {

					trigger(heap);
					return true;
				}
			}

			//can't find our bomb, something must have removed it, do nothing.
			bomb.fuse = null;
			Actor.remove( this );
			return true;
		}

		protected void trigger(Heap heap){
			heap.remove(bomb);
			bomb.explode(heap.pos);
			Actor.remove(this);
		}

		public boolean freeze(){
			bomb.fuse = null;
			Actor.remove(this);
			return true;
		}
	}


	public static class DoubleBomb extends Bomb{

		{
			image = ItemSpriteSheet.DBL_BOMB;
			stackable = false;
		}

		@Override
		public boolean doPickUp(Hero hero, int pos) {
			Bomb bomb = new Bomb();
			bomb.quantity(2);
			if (bomb.doPickUp(hero, pos)) {
				//isaaaaac.... (don't bother doing this when not in english)
				if (SPDSettings.language() == Languages.ENGLISH)
					hero.sprite.showStatus(CharSprite.NEUTRAL, "1+1 free!");
				return true;
			}
			return false;
		}
	}
	
	public static class EnhanceBomb extends Recipe {
		
		public static final LinkedHashMap<Class<?extends Item>, Class<?extends Bomb>> validIngredients = new LinkedHashMap<>();
		static {
			validIngredients.put(PotionOfFrost.class,           FrostBomb.class);
			validIngredients.put(ScrollOfPassage.class,         WoollyBomb.class);
			
			validIngredients.put(PotionOfLiquidFlame.class,     Firebomb.class);
			validIngredients.put(ScrollOfRage.class,            Noisemaker.class);
			
			validIngredients.put(PotionOfInvisibility.class,    Flashbang.class);
			validIngredients.put(ScrollOfRecharging.class,      ShockBomb.class);
			
			validIngredients.put(PotionOfHealing.class,         RegrowthBomb.class);
			validIngredients.put(ScrollOfRemoveCurse.class,     HolyBomb.class);
			
			validIngredients.put(GooBlob.class,                 ArcaneBomb.class);
			validIngredients.put(MetalShard.class,              ShrapnelBomb.class);

			validIngredients.put(ScrollOfTerror.class,       Webbomb.class);
			validIngredients.put(Food.class,                 SupplyBomb.class);
		}
		
		private static final HashMap<Class<?extends Bomb>, Integer> bombCosts = new HashMap<>();
		static {
			bombCosts.put(FrostBomb.class,      0);
			bombCosts.put(WoollyBomb.class,     3);
			
			bombCosts.put(Firebomb.class,       2);
			bombCosts.put(Noisemaker.class,     4);
			
			bombCosts.put(Flashbang.class,      2);
			bombCosts.put(ShockBomb.class,      4);

			bombCosts.put(RegrowthBomb.class,   3);
			bombCosts.put(HolyBomb.class,       3);
			bombCosts.put(Webbomb.class,        3);
			bombCosts.put(SupplyBomb.class,     1);
			
			bombCosts.put(ArcaneBomb.class,     6);
			bombCosts.put(ShrapnelBomb.class,   6);
		}
		
		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			boolean bomb = false;
			boolean ingredient = false;
			
			for (Item i : ingredients){
				if (!i.isIdentified()) return false;
				if (i.getClass().equals(Bomb.class)){
					bomb = true;
				} else if (validIngredients.containsKey(i.getClass())){
					ingredient = true;
				}
			}
			
			return bomb && ingredient;
		}
		
		@Override
		public int cost(ArrayList<Item> ingredients) {
			for (Item i : ingredients){
				if (validIngredients.containsKey(i.getClass())){
					return (bombCosts.get(validIngredients.get(i.getClass())));
				}
			}
			return 0;
		}
		
		@Override
		public Item brew(ArrayList<Item> ingredients) {
			Item result = null;
			
			for (Item i : ingredients){
				i.quantity(i.quantity()-1);
				if (validIngredients.containsKey(i.getClass())){
					result = Reflection.newInstance(validIngredients.get(i.getClass()));
					if (result instanceof HolyBomb) result.quantity(2);
				}
			}
			
			return result;
		}
		
		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			for (Item i : ingredients){
				if (validIngredients.containsKey(i.getClass())){
					Bomb bomb = Reflection.newInstance(validIngredients.get(i.getClass()));
					if (bomb instanceof HolyBomb) bomb.quantity(2);
					return bomb;
				}
			}
			return null;
		}
	}
}
