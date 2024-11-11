/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.ChargingItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ElectricalExplosive extends Bomb implements ChargingItem {

	{
		image = ItemSpriteSheet.FLASHBANG;
	}

	public Fuse fuseShock;

	public static class Fuse extends Actor{

		{
			actPriority = BLOB_PRIO+1; //after hero, before other actors
		}

		private ElectricalExplosive bomb;

		public Fuse ignite(ElectricalExplosive bomb){
			this.bomb = bomb;
			return this;
		}

		@Override
		protected boolean act() {

			//something caused our bomb to explode early, or be defused. Do nothing.
			if (bomb.fuseShock != this){
				Actor.remove( this );
				return true;
			}

			//look for our bomb, remove it from its heap, and blow it up.
			for (Heap heap : Dungeon.level.heaps.valueList()) {
				if (heap.items.contains(bomb)) {

					bomb.explode(heap.pos);
					Catalog.countUse(bomb.getClass());

					diactivate();
					Actor.remove(this);
					return true;
				}
			}

			//can't find our bomb, something must have removed it, do nothing.
			bomb.fuseShock = null;
			Actor.remove( this );
			return true;
		}
	}

	public float charge = 1;
	public int usedCharge;
	public Charger charger;

	@Override
	public boolean isSimilar(Item item) {
		return super.isSimilar(item) && this.fuseShock == ((ElectricalExplosive) item).fuseShock;
	}

	@Override
	protected void onThrow( int cell ) {
		if (!Dungeon.level.pit[ cell ] && lightingFuse) {
			Actor.addDelayed(fuseShock = new ElectricalExplosive.Fuse().ignite(this), 0);
		}
		if (Actor.findChar( cell ) != null && !(Actor.findChar( cell ) instanceof Hero) ){
			ArrayList<Integer> candidates = new ArrayList<>();
			for (int i : PathFinder.NEIGHBOURS8)
				if (Dungeon.level.passable[cell + i])
					candidates.add(cell + i);
			int newCell = candidates.isEmpty() ? cell : Random.element(candidates);
			Dungeon.level.drop( this, newCell ).sprite.drop( cell );
		} else {
			Heap heap = Dungeon.level.drop(this, cell);
			if (!heap.isEmpty()) {
				heap.sprite.drop(cell);
			}
		}
	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {
		if (fuseShock != null) {
			GLog.warning( Messages.get(this, "snuff_fuse") );
			fuseShock = null;
		}
		return super.doPickUp(hero, pos);
	}

	public float canBreak(){
		float chance = 0;

		if (usedCharge < 1000) chance = 0.01f;
		if (usedCharge >= 1000 && usedCharge < 1500) chance = 0.033f;
		if (usedCharge >= 1500 && usedCharge < 2000) chance = 0.06f;
		if (usedCharge >= 2000 && usedCharge < 2500) chance = 0.08f;
		if (usedCharge >= 2500 && usedCharge > 3000) chance = 0.12f;
		if (usedCharge > 3000) chance = 0.24f;
//		chance /= Bomb.nuclearBoost();
		return chance;
	}

	private void arc( Char ch, ArrayList<Char> affected, ArrayList<Lightning.Arc> arcs ) {

		affected.add( ch );

		int dist;
		if (Dungeon.level.water[ch.pos] && !ch.flying)
			dist = (int) (4 + charge / 0.5f);
		else
			dist = (int) (2 + charge / 0.5f);
//		dist *= Bomb.nuclearBoost();

		PathFinder.buildDistanceMap( ch.pos, BArray.not( Dungeon.level.solid, null ), dist );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE){
				Char n = Actor.findChar( i );
				if (n == Dungeon.hero && PathFinder.distance[i] > 1)
					//the hero is only zapped if they are adjacent
					continue;
				else if (n != null && !affected.contains( n )) {
					arcs.add(new Lightning.Arc(ch.sprite.center(), n.sprite.center()));
					arc(n, affected, arcs);
				}
			}
		}
	}

	public void explode(int cell){
		this.fuseShock = null;
		this.glowing();

		Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );

		ArrayList<Char> affected = new ArrayList<>();

		ArrayList<Lightning.Arc> arcs = new ArrayList<>();

		Heap h = Dungeon.level.heaps.get(cell);

		if (h == null){
			h = new Heap();
			h.seen = Dungeon.level.heroFOV[cell];
			h.pos = cell;
			h.drop(this);
		}

		for (int i : PathFinder.NEIGHBOURS9) {
			Char ch = Actor.findChar(cell + i);
			if (ch != null) {
				arcs.add(new Lightning.Arc(DungeonTilemap.raisedTileCenterToWorld(cell), ch.sprite.center()));
				arc(ch, affected, arcs);
			} else {
				CellEmitter.center(cell).burst(SparkParticle.FACTORY, 3);
			}
		}



		if (Dungeon.level.heroFOV[cell]) {

			CellEmitter.center(cell).burst(SparkParticle.FACTORY, 20);
			Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
			CellEmitter.center(cell).burst(SparkParticle.FACTORY, 20);
			if (h.sprite != null) {
				h.sprite.parent.addToFront(new Lightning(arcs, null));
			}
		}

		for (Char target : affected){
			//lightning deals less damage per-target, the more targets that are hit.
			float multipler = 0.66f + (0.4f/affected.size());
			//if the main target is in water, all affected take full damage
			if (Actor.findChar(cell) != null && Dungeon.level.water[Actor.findChar(cell).pos]) multipler = 1f;
			int dmg = Math.round(damageRoll() * 1.25f * charge * multipler);

			target.damage(dmg, new Electricity());
			if (target.isAlive()) Buff.prolong(target, Paralysis.class, charge / 0.06f);
			if (target == Dungeon.hero) Camera.main.shake( 2, 0.3f );
			target.sprite.centerEmitter().burst( SparkParticle.FACTORY, 3 );
			target.sprite.flash();

			if (target == Dungeon.hero && !target.isAlive()) {
				Badges.validateDeathFromFriendlyMagic();
				Dungeon.fail(Electricity.class);
			}
		}

		usedCharge += charge*100;
		charge = 0;

		if (Random.Float() < canBreak()) {
			for (Heap heap : Dungeon.level.heaps.valueList()) {
				if (heap.items.contains(this)) {

					heap.items.remove(this);
					if (heap.items.isEmpty()) {
						heap.destroy();
					}
					if (Dungeon.level.heroFOV[cell]){
						CellEmitter.center(cell).burst(BlastParticle.FACTORY, 50);
						Sample.INSTANCE.play(Assets.Sounds.DEGRADE);
					}
				}
			}
		}
	}

	public class Charger extends Buff {

		@Override
		public boolean attachTo( Char target ) {
			super.attachTo( target );

			return true;
		}

		@Override
		public boolean act() {
			if (charge < 1)
				recharge();

			spend( TICK );

			return true;
		}

		private void recharge(){

			LockedFloor lock = target.buff(LockedFloor.class);
			if (lock == null || lock.regenOn())
				charge += 0.015f;

			for (Recharging bonus : target.buffs(Recharging.class)){
				if (bonus != null && bonus.remainder() > 0f) {
					charge += 0.04f * bonus.remainder();
				}
			}
//			if (target instanceof Hero && ((Hero) target).pointsInTalent(Talent.SUFFERING_AWAY) > 1 &&
//					target.buff(FinalFroggit.Eradication.class) != null){
//				int power = target.buff(FinalFroggit.Eradication.class).combo;
//				if (((Hero) target).pointsInTalent(Talent.SUFFERING_AWAY) > 2)
//					power *= 1.75f;
//				charge += 0.05f * power;
//			}
			if (charge > 1) charge = 1;
			updateQuickslot();
		}

		public ElectricalExplosive bomb(){
			return ElectricalExplosive.this;
		}

		public void gainCharge(float charge) {
			ElectricalExplosive.this.charge += Math.min(charge, 1);
			updateQuickslot();
		}
	}

	@Override
	public String desc() {
		String desc_fuse = Messages.get(this, "desc",
				Math.round(minDamage()*1.25f * charge), Math.round(maxDamage()*1.25f * charge));
		if (fuse != null){
			desc_fuse += "\n\n" + Messages.get(this, "desc_burning");
		} else {
			desc_fuse += "\n\n" + Messages.get(this, "desc_fuse");
		}
		desc_fuse += "\n\n" + Messages.get(this, "counter", new DecimalFormat("#.##").format(canBreak() * 100f));
		return desc_fuse;
	}

	@Override
	public boolean collect( Bag container ) {
		if (super.collect( container )) {
			if (container.owner != null) {
				charge( container.owner );
				identify();
			}
			return true;
		} else {
			return false;
		}
	}

	public void charge(Char owner ) {
		if (charger == null) charger = new Charger();
		charger.attachTo( owner );
	}

	@Override
	public void charge(Char owner, float chargeScaleFactor) {
		charge(owner);
	}

	public void stopCharging() {
		if (charger != null) {
			charger.detach();
			charger = null;
		}
	}

	public void gainCharge( float amt ){
		charge += amt;
		charge = Math.min(1, charge);
		updateQuickslot();
	}

	@Override
	public String status() {
		if (levelKnown) {
			return Messages.format( "%s%%", Math.round(charge*100));
		} else {
			return null;
		}
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("charge", charge);
		bundle.put("used_charge", usedCharge);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge = bundle.getFloat("charge");
		if (bundle.contains("number_of_uses"))
			usedCharge = bundle.getInt("number_of_uses")*50;
		else
			usedCharge = bundle.getInt("used_charge");
	}
	
	@Override
	public int value() {
		//prices of ingredients
		return quantity * (20 + 30);
	}
}
