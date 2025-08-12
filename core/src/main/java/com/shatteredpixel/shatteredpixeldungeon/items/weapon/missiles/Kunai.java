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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;

import java.util.ArrayList;
import java.util.HashSet;

public class Kunai extends MissileWeapon {
	
	{
		image = ItemSpriteSheet.KUNAI;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.1f;
	}

    public float min(float lvl, int rank) {
        switch (rank){
            case 1: return 4 + lvl;
            case 2: return 4 + lvl;
            case 3: return 8 + lvl*3;
        }
        return 0;
    }

    public float max(float lvl, int rank) {
        switch (rank){
            case 1: return 8 + lvl*2f;
            case 2: return 9 + lvl*2.5f;
            case 3: return 16 + lvl*5f;
        }
        return 0;
    }

    public float baseUses(float lvl, int rank){
        switch (rank){
            case 1: return 6 + lvl*1.5f;
            case 2: return 3 + lvl*1f;
            case 3: return 5 + lvl*1.33f;
        }
        return 1;
    }

    //do not detach
    @Override
    protected void rangedHit(Char enemy, int cell) {
        if (rank() != 2)
            super.rangedHit(enemy, cell);
    }

    @Override
    protected void rangedMiss(int cell) {
        if (rank() != 2)
            super.rangedMiss(cell);
    }

    @Override
    public void cast(Hero user, int dst) {
        if (rank() == 2) {
            if (Dungeon.hero.visibleEnemies() == 0) {
                GLog.w(Messages.get(this, "no_target"));
                return;
            }

            decrementDurability();

            if (durability <= durabilityPerUse()) {
                if (quantity() > 1) {
                    quantity(quantity() - 1);
                } else {
                    detachAll(Dungeon.hero.belongings.backpack);
                }
            }

            final ArrayList<Char> targets = new ArrayList<>();

            int amount = 0;

            while (amount < 3) {
                for (Mob mob : Dungeon.hero.getVisibleEnemies()) {
                    targets.add(mob);
                    if (++amount >= 3) {
                        break;
                    }
                }
            }

            final HashSet<Callback> callbacks = new HashSet<>();

            for (Char ch : targets) {
                Item proto = new Kunai();

                Callback callback = new Callback() {
                    @Override
                    public void call() {
                        user.shoot(ch, Kunai.this);
                        callbacks.remove(this);
                        if (callbacks.isEmpty()) {
                            Invisibility.dispel();
                            user.spendAndNext(user.attackDelay());
                        }
                    }
                };

                MissileSprite m = (MissileSprite) user.sprite.parent.recycle(MissileSprite.class);
                m.reset(user.sprite, ch.pos, proto, callback);

                callbacks.add(callback);
            }

            user.sprite.zap(user.pos);
            user.busy();
        } else {
            super.cast(user, dst);
        }
    }

	@Override
	public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            if (rank() == 1) {
                Hero hero = (Hero) owner;
                Char enemy = hero.attackTarget();
                if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
                    //deals 60% toward max to max on surprise, instead of min to max.
                    int diff = max() - min();
                    int damage = augment.damageFactor(Hero.heroDamageIntRange(
                            min() + Math.round(diff * 0.6f),
                            max()));
                    int exStr = hero.STR() - STRReq();
                    if (exStr > 0) {
                        damage += Hero.heroDamageIntRange(0, exStr);
                    }
                    return damage;
                }
            } else if (rank() == 3){
                Hero hero = (Hero) owner;
                Char enemy = hero.attackTarget();
                if (enemy == null || Dungeon.level.heroFOV[enemy.pos]){
                    return 0;
                }
            }
        }
		return super.damageRoll(owner);
	}
	
}
