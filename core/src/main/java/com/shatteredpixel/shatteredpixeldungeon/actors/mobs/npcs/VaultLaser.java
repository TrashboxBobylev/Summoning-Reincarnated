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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WardSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.utils.Bundle;

public class VaultLaser extends NPC {

	{
		spriteClass = WardSprite.class;

		properties.add(Char.Property.IMMOVABLE);
	}

	public int[] laserDirs;
	public int laserDirIdx;

	public int initialLaserCooldown;
	public int cooldown;

	@Override
	protected boolean act() {

		cooldown--;
		if (cooldown <= 0){

			Ballistica beam = new Ballistica(pos, laserDirs[laserDirIdx], Ballistica.STOP_SOLID);
			boolean visible = false;
			for (int cell : beam.subPath(1, beam.dist)){
				if (Dungeon.level.heroFOV[cell]){
					visible = true;
				}
				if (Actor.findChar(cell) == Dungeon.hero){
					Dungeon.hero.sprite.showStatus(CharSprite.NEGATIVE, "!!!");
				}
			}
			if (visible){
				sprite.parent.add(new Beam.DeathRay(sprite.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));
			}

			laserDirIdx++;
			if (laserDirIdx >= laserDirs.length){
				laserDirIdx = 0;
			}
			cooldown = initialLaserCooldown;

		}

		if (cooldown == 1){

			Ballistica nextBeam = new Ballistica(pos, laserDirs[laserDirIdx], Ballistica.STOP_SOLID);
			for (int cell : nextBeam.subPath(1, nextBeam.dist)){
				if (Dungeon.level.heroFOV[cell]) {
					sprite.parent.add(new TargetedCell(cell, 0xFF0000));
				}
			}

		}

		throwItems();

		spend(TICK);
		return true;
	}

	@Override
	public boolean isImmune(Class effect) {
		return true;
	}

	@Override
	public boolean isInvulnerable(Class effect) {
		return true;
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	public boolean interact(Char c) {
		return true;
	}

	private static final String LASER_DIRS = "laser_dirs";
	private static final String LASER_DIR_IDX = "laser_dir_idx";
	private static final String INITIAL_COOLDOWN = "initial_cooldown";
	private static final String COOLDOWN = "cooldown";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LASER_DIRS, laserDirs);
		bundle.put(LASER_DIR_IDX, laserDirIdx);
		bundle.put(INITIAL_COOLDOWN, initialLaserCooldown);
		bundle.put(COOLDOWN, cooldown);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		laserDirs = bundle.getIntArray(LASER_DIRS);
		laserDirIdx = bundle.getInt(LASER_DIR_IDX);
		initialLaserCooldown = bundle.getInt(INITIAL_COOLDOWN);
		cooldown = bundle.getInt(COOLDOWN);
	}

	@Override
	public CharSprite sprite() {
		WardSprite sprite = (WardSprite) super.sprite();
		sprite.linkVisuals(this);
		return sprite;
	}
}
