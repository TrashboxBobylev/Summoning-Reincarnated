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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.GooWarn;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Miasma;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.DoomCloud;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ArcaneBomb extends Bomb.ConjuredBomb {

	{
		image = ItemSpriteSheet.ARCANE_BOMB;
		fuseDelay = 4;
	}

	private static final int RADIUS = 5;

	public static ArrayList<Integer> getAreaOfEffect(int cell){
		ArrayList<Integer> aoe = new ArrayList<>();
		int bx = cell % Dungeon.level.width();
		int by = cell / Dungeon.level.width();
		for (int i = 0; i < Dungeon.level.map.length; i++){
			int dx = bx - (i % Dungeon.level.width());
			int dy = by - (i / Dungeon.level.width());
			if (dx*dx + dy*dy <= RADIUS*RADIUS) aoe.add(i);
		}
		return aoe;
	}

	@Override
	protected void onThrow(int cell) {
		super.onThrow(cell);
		if (fuse != null){
			ArrayList<Integer> aoe = getAreaOfEffect(cell);
			for (int i : aoe) {
				GameScene.add(Blob.seed(i, fuseDelay+1, GooWarn.class));
			}
		}
	}

	@Override
	protected int explosionRange() {
		return 5;
	}

	@Override
	public boolean explodesDestructively() {
		return false;
	}

	@Override
	public void explode(int cell) {
		super.explode(cell);

		ArrayList<Char> affected = new ArrayList<>();

		ArrayList<Integer> aoe = getAreaOfEffect(cell);
		if (!Dungeon.bossLevel()) {
			ArrayList<Integer> mainCraterCells = new ArrayList<>();
			mainCraterCells.add(cell);
			for (int k : PathFinder.NEIGHBOURS4){
				mainCraterCells.add(cell+k);
			}
			for (int c : mainCraterCells){
				if (Dungeon.level.getTransition(c) == null) Level.set(c, Terrain.CHASM);
				GameScene.updateMap(c);
				if (Actor.findChar(c) != null){
					Dungeon.level.occupyCell(Actor.findChar(c));
				}
			}
		}
		for (int i : aoe) {
			if (Dungeon.level.heroFOV[i]) {
				CellEmitter.get(i).burst(ShadowParticle.UP, 10);
			}

			if (Dungeon.level.losBlocking[i] && !Dungeon.bossLevel()){
				float chance = 1.2f - Dungeon.level.distance(cell, i)*0.2f;
				if (Random.Float() < chance && Dungeon.level.insideMap(i)){
					if (Dungeon.level.exit() != i && Dungeon.level.entrance() != i)
						Level.set( i, Terrain.EMBERS );
					Blob web = Dungeon.level.blobs.get(Web.class);
					if (web != null){
						web.clear(i);
					}
				}
			}
			GameScene.updateMap( i );
			Char ch = Actor.findChar(i);
			if (ch != null){
				affected.add(ch);
			}
			Heap heap = Dungeon.level.heaps.get(i);
			if (heap != null)
				heap.explode();

			GameScene.add(Blob.seed(i, Math.round(Random.NormalIntRange(100, 150)/**Bomb.nuclearBoost()*/), Miasma.class));
		}

		for (Char ch : affected){
			// 350% bomb damage, but pierces armor.
			int damage = (int) (damageRoll()*3.5f);
			ch.damage(damage, this);
			if (ch == Dungeon.hero && !ch.isAlive()){
				Badges.validateDeathFromFriendlyMagic();
				Dungeon.fail(ArcaneBomb.class);
			}
		}
		DoomCloud.hit(cell);
		for (int k = 0; k < 4; k++) GameScene.flash(0xFFFFFFFF);

		Dungeon.level.buildFlagMaps();
		Dungeon.level.cleanWalls();
		Dungeon.observe();
		Sample.INSTANCE.play( Assets.Sounds.BLAST, 18 );
	}

	@Override
	public String desc() {
		String desc_fuse = Messages.get(this, "desc",
				GameMath.printAverage(Math.round(minDamage()*3.5f), Math.round(maxDamage()*3.5f)));

		if (fuse != null){
			desc_fuse += "\n\n" + Messages.get(this, "desc_burning");
		} else {
			desc_fuse += "\n\n" + Messages.get(this, "desc_fuse");
		}

		return desc_fuse;
	}

	@Override
	public int value() {
		//prices of ingredients
		return quantity * (15 + 30);
	}
}
