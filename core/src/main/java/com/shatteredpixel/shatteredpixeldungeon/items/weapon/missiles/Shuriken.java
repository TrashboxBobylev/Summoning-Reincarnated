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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Callback;

public class Shuriken extends MissileWeapon {

	{
		image = ItemSpriteSheet.SHURIKEN;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.2f;
		
		tier = 2;
	}

    boolean bounced = false;

    public float min(float lvl, int rank) {
        switch (rank){
            case 1: return 4 + lvl;
            case 2: return 2 + lvl/2f;
            case 3: return 5 + lvl*2;
        }
        return 0;
    }

    public float max(float lvl, int rank) {
        switch (rank){
            case 1: return 8 + lvl*2.5f;
            case 2: return 7 + lvl*2.5f;
            case 3: return 12 + lvl*4f;
        }
        return 0;
    }

    public float baseUses(float lvl, int rank){
        switch (rank){
            case 1: return 6 + lvl*1.5f;
            case 2: return 4 + lvl*1.25f;
            case 3: return 8 + lvl*1.75f;
        }
        return 1;
    }

    @Override
	public void onThrow(int cell) {
        boolean hitGround = Actor.findChar(cell) == null;
		super.onThrow(cell);
        bounced = false;
        if (rank() == 1) {
            if (curUser.buff(ShurikenInstantTracker.class) == null) {
                //1 less turn as the attack will be instant
                FlavourBuff.affect(curUser, ShurikenInstantTracker.class, ShurikenInstantTracker.DURATION - 1);
            }
        } else if (rank() == 2){
            Char enemy = Actor.findChar( cell );
            if (!bounced && !hitGround) {
                Mob[] mobs = Dungeon.level.mobs.toArray(new Mob[0]);
                int targetPos = Integer.MAX_VALUE - 1;
                Mob desiredMob = null;
                for (Mob m : mobs) {
                    if (new Ballistica(cell, m.pos, Ballistica.FRIENDLY_PROJECTILE).collisionPos == m.pos
                            && Dungeon.level.trueDistance(cell, m.pos) <= Dungeon.level.trueDistance(cell, targetPos) && m != enemy
                            && m.alignment == Char.Alignment.ENEMY && !m.isInvulnerable(getClass())) {
                        targetPos = m.pos;
                        desiredMob = m;
                    }
                }
                if (targetPos == Integer.MAX_VALUE - 1){
                    return;
                } else {
                    bounced = true;
                    Shuriken thrownOne = this;
                    if (enemy != null) {
                        PinCushion pinCushion = enemy.buff(PinCushion.class);
                        if (pinCushion != null) {
                            thrownOne = (Shuriken) pinCushion.grabOne();
                        }
                    } else if (Dungeon.level.heaps.get(cell) != null){
                        Heap h = Dungeon.level.heaps.get(cell);
                        h.remove(thrownOne);
                    }
                    Mob finalDesiredMob = desiredMob;
                    Shuriken finalThrownOne = thrownOne;
                    ((MissileSprite) CellEmitter.center(cell).parent.recycle(MissileSprite.class)).
                            reset(cell,
                                    desiredMob.sprite,
                                    thrownOne,
                                    new Callback() {
                                        @Override
                                        public void call() {
                                            curUser = Dungeon.hero;
                                            if (curUser.shoot(finalDesiredMob, finalThrownOne)){
                                                if (durability > 0 && !spawnedForEffect){
                                                    //attempt to stick the missile weapon to the enemy, just drop it if we can't.
                                                    if (finalDesiredMob.isActive() && finalDesiredMob.alignment != Char.Alignment.ALLY){
                                                        PinCushion p = Buff.affect(finalDesiredMob, PinCushion.class);
                                                        if (p.target == finalDesiredMob){
                                                            p.stick(finalThrownOne);
                                                        }
                                                    } else {
                                                        Dungeon.level.drop(finalThrownOne, finalDesiredMob.pos).sprite.drop();
                                                    }
                                                }
                                                Dungeon.hero.next();
                                            }
                                        }
                                    });
                }
            }
        }
	}

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (rank() == 3) {
            damage = super.proc(attacker, defender, damage);
            Buff.affect(defender, Viscosity.DeferedDamage.class).extend(damage);
            return 0;
        } else {
            return super.proc(attacker, defender, damage);
        }
    }

    @Override
	public float castDelay(Char user, int cell) {
        if (rank() == 3 && user instanceof Hero && ((Hero) user).justMoved)  return 0;
        if (bounced) return 0f;
		return (rank() != 1 || user.buff(ShurikenInstantTracker.class) != null) ? super.castDelay(user, cell) : 0;
	}

	public static class ShurikenInstantTracker extends FlavourBuff {

		public static int DURATION = 20;

		@Override
		public int icon() {
			return BuffIndicator.THROWN_WEP;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0.6f, 0.6f, 0.6f);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}

	}

}
