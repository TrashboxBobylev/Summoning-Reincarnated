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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class WandOfDisintegration extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_DISINTEGRATION;

		collisionProperties = Ballistica.WONT_STOP;
	}


	public float magicMin(float lvl){
		return 3+lvl;
	}

	public float magicMax(float lvl){
		return 8+5.5f*lvl;
	}

    @Override
    public float powerModifier(int rank) {
        switch (rank){
            case 1: return 1.0f;
            case 2: return 0.75f;
            case 3: return 3.25f;
        }
        return super.powerModifier(rank);
    }

    @Override
    public float rechargeModifier(int rank) {
        switch (rank){
            case 1: return 1.0f;
            case 2: return 1.5f;
            case 3: return 1.75f;
        }
        return super.rechargeModifier(rank);
    }

    @Override
    protected int chargesPerCast() {
        switch (rank()){
            case 3: return 2;
        }
        return super.chargesPerCast();
    }

    @Override
	public int targetingPos(Hero user, int dst) {
		if (!cursed || !cursedKnown) {
			return dst;
		} else {
			return super.targetingPos(user, dst);
		}
	}

	@Override
	public void onZap(Ballistica beam) {
		
		boolean terrainAffected = false;
		
		float level = power();
		
		int maxDistance = Math.min(distance(), beam.dist);
        if (rank() == 2){
            maxDistance = beam.path.size();
        }
		
		ArrayList<Char> chars = new ArrayList<>();

		Blob web = Dungeon.level.blobs.get(Web.class);

		int terrainPassed = 2, terrainBonus = 0;
		for (int c : beam.subPath(1, maxDistance)) {
			
			Char ch;
			if ((ch = Actor.findChar( c )) != null) {

				//we don't want to count passed terrain after the last enemy hit. That would be a lot of bonus levels.
				//terrainPassed starts at 2, equivalent of rounding up when /3 for integer arithmetic.
				terrainBonus += terrainPassed/3;
				terrainPassed = terrainPassed%3;

				if (ch instanceof Mob && ((Mob) ch).state == ((Mob) ch).PASSIVE
						&& !(Dungeon.level.mapped[c] || Dungeon.level.visited[c])){
					//avoid harming undiscovered passive chars
				} else if (ch instanceof Hero ) {

                } else {
					chars.add(ch);
				}
			}

			if (Dungeon.level.solid[c]) {
				terrainPassed++;
			}

			if (Dungeon.level.flamable[c]) {

				Dungeon.level.destroy( c );
				GameScene.updateMap( c );
				terrainAffected = true;
				
			}
			
			CellEmitter.center( c ).burst( PurpleParticle.BURST, Random.IntRange( 1, 2 ) );
		}
		
		if (terrainAffected) {
			Dungeon.observe();
		}
		
		float lvl = level + (chars.size()-1) + terrainBonus;
        if (rank() == 3){
            lvl -= terrainBonus + (chars.size()-1);
        }
		for (Char ch : chars) {
			wandProc(ch, chargesPerCast());
			ch.damage( damageRoll(lvl), this );
			ch.sprite.centerEmitter().burst( PurpleParticle.BURST, Random.IntRange( 1, 2 ) );
			ch.sprite.flash();
		}
	}

	@Override
	public void onHit(Char attacker, Char defender, int damage) {
		//no direct effect, see magesStaff.reachfactor
	}

	private int distance() {
        return (int)power()*2 + 6;
	}

	@Override
	public String upgradeStat2(int level) {
		return Integer.toString(6 + level*2);
	}

    @Override
    public String generalRankDescription(int rank) {
        return Messages.get(this, "rank" + rank,
                Math.round(magicMin(power())*powerModifier(rank)),
                Math.round(magicMax(power())*powerModifier(rank)),
                getRechargeInfo(rank),
                distance()
        );
    }

    @Override
    public int collisionProperties(int target) {
        if (rank() == 2){
            return Ballistica.STOP_SOLID | Ballistica.REFLECT;
        }
        return super.collisionProperties(target);
    }

    @Override
	public void fx(Ballistica beam, Callback callback) {
        if (rank() == 2) {
            if (beam.reflectPositions.isEmpty()) {
                curUser.sprite.parent.add(
                        new Beam.DeathRay(curUser.sprite.center(),
                                DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));
            } else {
                for (int i = 0; i < beam.reflectPositions.size(); i++) {
                    curUser.sprite.parent.add(
                            new Beam.DeathRay(i == 0 ? curUser.sprite.center() : DungeonTilemap.raisedTileCenterToWorld(beam.reflectPositions.get(i - 1)),
                                    DungeonTilemap.raisedTileCenterToWorld(beam.reflectPositions.get(i))));
                }
            }
        } else {
            int cell = beam.path.get(Math.min(beam.dist, distance()));
            curUser.sprite.parent.add(new Beam.DeathRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld( cell )));
        }
		callback.call();
	}

	@Override
	public void staffFx(WandParticle particle) {
		particle.color(0x220022);
		particle.am = 0.6f;
		particle.setLifespan(1f);
		particle.acc.set(10, -10);
		particle.setSize( 0.5f, 3f);
		particle.shuffleXY(1f);
	}

}
