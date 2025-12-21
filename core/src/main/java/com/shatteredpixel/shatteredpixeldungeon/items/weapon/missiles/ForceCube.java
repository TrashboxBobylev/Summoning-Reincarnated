/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TenguDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class ForceCube extends MissileWeapon {
	
	{
		image = ItemSpriteSheet.FORCE_CUBE;

		sticky = false;
	}

    public float min(float lvl, int type) {
        switch (type){
            case 1: return 4 + lvl;
            case 2: return 3 + lvl*0.67f;
            case 3: return 1 + lvl*1.5f;
        }
        return 0;
    }

    public float max(float lvl, int type) {
        switch (type){
            case 1: return 8 + lvl*3f;
            case 2: return 6 + lvl*2.25f;
            case 3: return 20 + lvl*8;
        }
        return 0;
    }

    public float baseUses(float lvl, int type){
        switch (type){
            case 1: return 5 + lvl*1.25f;
            case 2: return 3 + lvl*1f;
            case 3: return 1 + lvl*0.25f;
        }
        return 1;
    }

	@Override
	public void hitSound(float pitch) {
		//no hitsound as it never hits enemies directly
	}

	@Override
	public float castDelay(Char user, int cell) {
		//special rules as throwing this onto empty space or yourself does trigger it
		if (!Dungeon.level.pit[cell] && Actor.findChar(cell) == null){
			return delayFactor( user );
		} else {
			return super.castDelay(user, cell);
		}
	}

	@Override
    public void onThrow(int cell) {
		if ((Dungeon.level.pit[cell] && Actor.findChar(cell) == null)){
			super.onThrow(cell);
			return;
		}

		//keep the parent reference for things like IDing
		MissileWeapon parentTemp = parent;
		rangedHit( null, cell );
		parent = parentTemp;
		Dungeon.level.pressCell(cell);
		
		ArrayList<Char> targets = new ArrayList<>();
		if (Actor.findChar(cell) != null) targets.add(Actor.findChar(cell));

        if (type() != 3){
            for (int i : PathFinder.NEIGHBOURS8){
                if (!(Dungeon.level.traps.get(cell+i) instanceof TenguDartTrap)) Dungeon.level.pressCell(cell+i);
                if (Actor.findChar(cell + i) != null) targets.add(Actor.findChar(cell + i));
            }
        } else {
            PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), 3 );
            for (int i = 0; i < PathFinder.distance.length; i++) {
                if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                    if (!(Dungeon.level.traps.get(i) instanceof TenguDartTrap)) Dungeon.level.pressCell(i);
                    if (Actor.findChar(i) != null && Actor.findChar(i).alignment == Char.Alignment.ENEMY) targets.add(Actor.findChar(i));
                }
            }
        }
		
		for (Char target : targets){
			curUser.shoot(target, this);
			if (target == Dungeon.hero && !target.isAlive()){
				Badges.validateDeathFromFriendlyMagic();
				Dungeon.fail(this);
				GLog.n(Messages.get(this, "ondeath"));
			}
            if (type() == 2){
                //do not push chars that are dieing over a pit, or that move due to the damage
                if ((target.isAlive() || target.flying || !Dungeon.level.pit[target.pos])) {
                    //trace a ballistica to our target (which will also extend past them
                    Ballistica trajectory = new Ballistica(cell, target.pos, Ballistica.STOP_TARGET);
                    //trim it to just be the part that goes past them
                    trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
                    int strength = 2 + (int) powerLevel();
                    WandOfBlastWave.throwChar(target, trajectory, strength, false, true, this);
                }
            }
		}
		
		WandOfBlastWave.BlastWave.blast(cell);
        if (type() == 3){
            for (int i : PathFinder.NEIGHBOURS8){
                WandOfBlastWave.BlastWave.blast(cell+i);
            }
            Sample.INSTANCE.play( Assets.Sounds.BLAST, 1.5f, 0.5f );
        }
		Sample.INSTANCE.play( Assets.Sounds.BLAST );
	}

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (type() == 3){
            damage += defender.drRoll();
        }
        return super.proc(attacker, defender, damage);
    }
}
