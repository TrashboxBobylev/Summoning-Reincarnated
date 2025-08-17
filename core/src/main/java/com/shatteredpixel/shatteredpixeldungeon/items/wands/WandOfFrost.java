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

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostBurn;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FrostBomb;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class WandOfFrost extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_FROST;
	}

	public float magicMin(float lvl){
		return 2+lvl;
	}

	public float magicMax(float lvl){
		return 6+5*lvl;
	}

    @Override
    public float rechargeModifier(int rank) {
        switch (rank){
            case 1: return 1.0f;
            case 2: return 3.25f;
            case 3: return 2.25f;
        }
        return 0f;
    }

    @Override
    public float powerModifier(int rank) {
        switch (rank){
            case 1: return 1.0f;
            case 3: return 0.33f;
        }
        return super.powerModifier(rank);
    }

    ConeAOE cone;

	@Override
	public void onZap(Ballistica bolt) {
        if (rank() == 2){
            ArrayList<Char> affectedChars = new ArrayList<>();
            for( int cell : cone.cells ){

                //ignore caster cell
                if (cell == bolt.sourcePos){
                    continue;
                }

                GameScene.add( Blob.seed(cell, 2, Freezing.class));

                //knock doors open
                if (Dungeon.level.map[cell] == Terrain.DOOR){
                    Level.set(cell, Terrain.OPEN_DOOR);
                    GameScene.updateMap(cell);
                }

                Char ch = Actor.findChar( cell );
                if (ch != null) {
                    affectedChars.add(ch);
                }
            }

            for ( Char ch : affectedChars ){
                ch.sprite.burst(0xFF99CCFF, 7);
                Buff.affect(ch, Frost.class, 10);
            }
        } else {

            Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
            if (heap != null) {
                heap.freeze();
            }

            Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);
            if (fire != null && fire.volume > 0) {
                fire.clear(bolt.collisionPos);
            }

            MagicalFireRoom.EternalFire eternalFire = (MagicalFireRoom.EternalFire) Dungeon.level.blobs.get(MagicalFireRoom.EternalFire.class);
            if (eternalFire != null && eternalFire.volume > 0) {
                eternalFire.clear(bolt.collisionPos);
                //bolt ends 1 tile short of fire, so check next tile too
                if (bolt.path.size() > bolt.dist + 1) {
                    eternalFire.clear(bolt.path.get(bolt.dist + 1));
                }

            }

            Char ch = Actor.findChar(bolt.collisionPos);
            if (ch != null) {

                int damage = damageRoll();

                if (ch.buff(Frost.class) != null) {
                    return; //do nothing, can't affect a frozen target
                }
                if (ch.buff(Chill.class) != null) {
                    //6.67% less damage per turn of chill remaining, to a max of 10 turns (50% dmg)
                    float chillturns = Math.min(10, ch.buff(Chill.class).cooldown());
                    damage = (int) Math.round(damage * Math.pow(0.9333f, chillturns));
                } else {
                    ch.sprite.burst(0xFF99CCFF, (int) (power() / 2 + 2));
                }
                if (!(Dungeon.isChallenged(Conducts.Conduct.PACIFIST))) {
                    wandProc(ch, chargesPerCast());
                    ch.damage(damage, this);
                    Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1, 1.1f * Random.Float(0.87f, 1.15f));

                    if (ch.isAlive()) {
                        float mod = 1f;
                        if (Dungeon.level.water[ch.pos])
                            mod *= 2f;
                        if (rank() == 3){
                            mod *= 2f;
                            if (!ch.isImmune(Frost.class)) {
                                Buff.affect(ch, Frost.class, (2 + power())*mod);
                                Buff.affect(ch, FrostBomb.ResistTracker.class, (2 + power())*mod);
                            }
                        }

                        Buff.affect(ch, FrostBurn.class).reignite(ch, (2 + power())*mod);
                    }
                }
            } else {
                Dungeon.level.pressCell(bolt.collisionPos);
            }
        }
	}

    @Override
    public String statsDesc() {
        if (rank() != 2) {
            if (levelKnown)
                return Messages.get(this, "stats_desc", GameMath.printAverage((int) magicMin(), (int) magicMax()));
            else
                return Messages.get(this, "stats_desc", GameMath.printAverage((int) magicMin(0), (int) magicMax(0)));
        }
        else {
            return Messages.get(this, "stats_desc2");
        }
    }

	@Override
	public String upgradeStat2(int level) {
		return Integer.toString(2 + level);
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
        if (rank() == 2){
            //need to perform flame spread logic here so we can determine what cells to put flames in.
            int maxDist = 8;
            int dist = Math.min(bolt.dist, maxDist);

            cone = new ConeAOE( bolt,
                    maxDist,
                    180,
                    collisionProperties(0) | Ballistica.STOP_TARGET);

            //cast to cells at the tip, rather than all cells, better performance.
            for (Ballistica ray : cone.rays){
                ((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
                        MagicMissile.ABYSS,
                        curUser.sprite,
                        ray.path.get(ray.dist),
                        null
                );
            }

            //final zap at half distance, for timing of the actual wand effect
            MagicMissile.boltFromChar( curUser.sprite.parent,
                    MagicMissile.ABYSS,
                    curUser.sprite,
                    bolt.path.get(dist/2),
                    callback );
            Sample.INSTANCE.play( Assets.Sounds.ZAP );
        }
        else{
            MagicMissile.boltFromChar(curUser.sprite.parent,
                    MagicMissile.FROST,
                    curUser.sprite,
                    bolt.collisionPos,
                    callback);
            Sample.INSTANCE.play(Assets.Sounds.ZAP);
        }
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}

    @Override
    public String generalRankDescription(int rank) {
        if (rank == 2) {
            return Messages.get(this, "rank2",
                    getRechargeInfo(rank)
            );
        }
        float mod = 1f;
        if (rank == 3){
            mod = 2f;
        }
        return Messages.get(this, "rank" + rank,
                Math.round(magicMin(power())*powerModifier(rank)),
                Math.round(magicMax(power())*powerModifier(rank)),
                getRechargeInfo(rank),
                Math.round((2 + power())*mod)
        );
    }

    @Override
	public void onHit(Char attacker, Char defender, int damage) {
		Chill chill = defender.buff(Chill.class);

		if (chill != null) {

			//1/9 at 2 turns of chill, scaling to 9/9 at 10 turns
			float procChance = ((int)Math.floor(chill.cooldown()) - 1)/9f;
			procChance *= procChanceMultiplier(attacker);

			if (Random.Float() < procChance) {

				float powerMulti = Math.max(1f, procChance);

				//need to delay this through an actor so that the freezing isn't broken by taking damage from the staff hit.
				new FlavourBuff() {
					{
						actPriority = VFX_PRIO;
					}

					public boolean act() {
						Buff.affect(target, Frost.class, Math.round(Frost.DURATION * powerMulti));
						return super.act();
					}
				}.attachTo(defender);
			}
		}
	}

	@Override
	public void staffFx(WandParticle particle) {
		particle.color(0x88CCFF);
		particle.am = 0.6f;
		particle.setLifespan(2f);
		float angle = Random.Float(PointF.PI2);
		particle.speed.polar( angle, 2f);
		particle.acc.set( 0f, 1f);
		particle.setSize( 0f, 1.5f);
		particle.radiateXY(Random.Float(1f));
	}

}
