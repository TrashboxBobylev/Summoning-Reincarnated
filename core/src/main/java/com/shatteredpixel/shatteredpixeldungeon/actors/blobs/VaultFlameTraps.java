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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;

import java.util.Arrays;

//contains both blob logic and logic for seeding itself
public class VaultFlameTraps extends Blob {

	public int[] initialCooldowns;
	public int[] cooldowns;
	public int[] triggers;

	@Override
	public boolean act() {
		super.act();

		if (initialCooldowns != null) {
			for (int i = 0; i < initialCooldowns.length; i++) {
				if (initialCooldowns[i] > -1) {
					if (cooldowns[i] <= 0) {
						cooldowns[i] = initialCooldowns[i];
					}
					cooldowns[i]--;
					if (cooldowns[i] <= 0) {
						seed(Dungeon.level, i, triggers[i]);
					}
				}
			}
		}

		return true;
	}

	@Override
	protected void evolve() {
		int cell;

		for (int i = area.left; i < area.right; i++) {
			for (int j = area.top; j < area.bottom; j++) {
				cell = i + j* Dungeon.level.width();
				if (cur[cell] > 0) {

					//similar to fire.burn(), but Tengu is immune, and hero loses score
					Char ch = Actor.findChar( cell );
					if (ch == Dungeon.hero){
						Sample.INSTANCE.play(Assets.Sounds.BURNING);
						ch.sprite.showStatus(CharSprite.NEGATIVE, "!!!");
					}
					/*if (ch != null && !ch.isImmune(Fire.class)) {
						Buff.affect( ch, Burning.class ).reignite( ch );
					}

					Heap heap = Dungeon.level.heaps.get( cell );
					if (heap != null) {
						heap.burn();
					}

					Plant plant = Dungeon.level.plants.get( cell );
					if (plant != null){
						plant.wither();
					}

					if (Dungeon.level.flamable[cell]){
						Dungeon.level.destroy( cell );

						GameScene.updateMap( cell );
					}*/

					if (Dungeon.level.heroFOV[cell]){
						CellEmitter.get(cell).start(ElmoParticle.FACTORY, 0.02f, 10);
					}
					off[cell] = cur[cell] - 1;
					volume += off[cell];
				} else {
					off[cell] = 0;
				}
			}
		}
	}

	public void seed(Level level, int cell, int amount ) {
		super.seed(level, cell, amount);
		if (initialCooldowns == null) {
			initialCooldowns = new int[level.length()];
			Arrays.fill(initialCooldowns, -1);
		}
		if (cooldowns == null){
			cooldowns = new int[level.length()];
		}
		if (triggers == null){
			triggers = new int[level.length()];
		}
	}

	private static final String INITIAL_CDS	= "initial_cds";
	private static final String COOLDOWNS	= "cooldowns";
	private static final String TRIGGERS	= "triggers";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		if (initialCooldowns != null) {
			bundle.put(INITIAL_CDS, initialCooldowns);
			bundle.put(COOLDOWNS, cooldowns);
			bundle.put(TRIGGERS, triggers);
		}
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(INITIAL_CDS)){
			initialCooldowns = bundle.getIntArray(INITIAL_CDS);
			cooldowns = bundle.getIntArray(COOLDOWNS);
			triggers = bundle.getIntArray(TRIGGERS);
		}
	}

	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		emitter.bound.set(0.4f, 0.4f, 0.6f, 0.6f);
		emitter.pour( ElmoParticle.FACTORY, 0.3f );
	}

	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}

}
