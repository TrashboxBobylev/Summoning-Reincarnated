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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class WandOfFireblast extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_FIREBOLT;

		//only used for targeting, actual projectile logic is Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID
		collisionProperties = Ballistica.WONT_STOP;
	}

	//1/2/3 base damage with 1/2/3 scaling based on charges used
    public float magicMin(float lvl){
        return magicMin(lvl, imaginableChargePerCast());
    }
    public float magicMin(float lvl, int rank){
		return (2+lvl)*rank;
	}

	//2/8/18 base damage with 2/4/6 scaling based on charges used
    public float magicMax(float lvl) {
        return magicMax(lvl, rank());
    }
    public float magicMax(float lvl, int rank) {
		switch (rank){
			case 1: default:
				return 3 + 2*lvl;
			case 2:
				return 2*(4 + 2.5f*lvl);
			case 3:
				return 3*(6+2.5f*lvl);
		}
	}

    @Override
    public float rechargeModifier(int level) {
        switch (level){
            case 1: return 1.33f;
            case 2: return 3f;
            case 3: return 6f;
        }
        return 0f;
    }

	ConeAOE cone;

	@Override
	public void onZap(Ballistica bolt) {

		ArrayList<Char> affectedChars = new ArrayList<>();
		ArrayList<Integer> adjacentCells = new ArrayList<>();
		for( int cell : cone.cells ){

			//ignore caster cell
			if (cell == bolt.sourcePos){
				continue;
			}

			//knock doors open
			if (Dungeon.level.map[cell] == Terrain.DOOR){
				Level.set(cell, Terrain.OPEN_DOOR);
				GameScene.updateMap(cell);
			}

			//only ignite cells directly near caster if they are flammable or solid
			if (Dungeon.level.adjacent(bolt.sourcePos, cell)
					&& !(Dungeon.level.flamable[cell] || Dungeon.level.solid[cell])){
				adjacentCells.add(cell);
				//do burn any heaps located here though
				if (Dungeon.level.heaps.get(cell) != null){
					Dungeon.level.heaps.get(cell).burn();
				}
			} else {
				GameScene.add( Blob.seed( cell, 1+imaginableChargePerCast(), Fire.class ) );
			}

			Char ch = Actor.findChar( cell );
			if (ch != null && !(Dungeon.isChallenged(Conducts.Conduct.PACIFIST))) {
				affectedChars.add(ch);
			}
		}

		//if wand was shot right at a wall
		if (cone.cells.isEmpty()){
			adjacentCells.add(bolt.sourcePos);
		}

		//ignite cells that share a side with an adjacent cell, are flammable, and are closer to the collision pos
		//This prevents short-range casts not igniting barricades or bookshelves
		for (int cell : adjacentCells){
			for (int i : PathFinder.NEIGHBOURS8){
				if (Dungeon.level.trueDistance(cell+i, bolt.collisionPos) < Dungeon.level.trueDistance(cell, bolt.collisionPos)
						&& Dungeon.level.flamable[cell+i]
						&& Fire.volumeAt(cell+i, Fire.class) == 0){
					GameScene.add( Blob.seed( cell+i, 1+chargesPerCast(), Fire.class ) );
				}
			}
		}

		for ( Char ch : affectedChars ){
			wandProc(ch, imaginableChargePerCast());
			ch.damage(damageRoll(), this);
			if (ch.isAlive()) {
				Buff.affect(ch, Burning.class).reignite(ch);
				switch (imaginableChargePerCast()) {
					case 1:
						break; //no effects
					case 2:
						Buff.affect(ch, Cripple.class, 4f);
						break;
					case 3:
						Buff.affect(ch, Paralysis.class, 4f);
						break;
				}
			}
		}
	}

	@Override
	public void onHit(Char attacker, Char defender, int damage) {

		//proc chance is initially 0..
		float procChance = 0;
		for (int i : PathFinder.NEIGHBOURS9) {

			//+25% proc chance per burning char within 3x3 of target
			// this includes the attacker and defender
			if (Actor.findChar(defender.pos + i) != null
					&& Actor.findChar(defender.pos + i).buff(Burning.class) != null) {
				procChance += 0.25f;

			//otherwise +5% proc chance per burning tile within 3x3 of target
			} else if (Fire.volumeAt(defender.pos+i, Fire.class) > 0){
				procChance += 0.05f;
			}

		}

		procChance = Math.min(1f, procChance);
		procChance *= Wand.procChanceMultiplier(attacker);

		if (Random.Float() < procChance){

			float powerMulti = Math.max(1f, procChance);

			Blob fire = Dungeon.level.blobs.get(Fire.class);

			//explode, dealing damage to enemies in 3x3, and clearing all fire
			CellEmitter.center(defender.pos).burst(BlastParticle.FACTORY, 30);
			if (fire != null) {
				for (int i : PathFinder.NEIGHBOURS9) {
					CellEmitter.get(defender.pos + i).burst(SmokeParticle.FACTORY, 4);
					if (Fire.volumeAt(defender.pos+i, Fire.class) > 0){
						Dungeon.level.destroy(defender.pos + i);
						GameScene.updateMap(defender.pos + i);
						fire.clear(defender.pos + i);
					}

					Char ch = Actor.findChar(defender.pos + i);
					if (ch != null) {
						if (ch.buff(Burning.class) != null) {
							ch.buff(Burning.class).detach();
						}
						if (ch.alignment == Char.Alignment.ENEMY) {
							//damage of a 2-charge zap
							ch.damage(Math.round(powerMulti*Random.NormalIntRange(2 + 2*buffedLvl(), 8 + 4*buffedLvl())), this);
						}
					}
				}
			}

			Sample.INSTANCE.play( Assets.Sounds.BLAST );

		}
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		//need to perform flame spread logic here so we can determine what cells to put flames in.

		// 5/7/9 distance
		int maxDist = 3 + 2*imaginableChargePerCast();

		cone = new ConeAOE( bolt,
				maxDist,
				30 + 20*imaginableChargePerCast(),
				Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);

		//cast to cells at the tip, rather than all cells, better performance.
		Ballistica longestRay = null;
		for (Ballistica ray : cone.outerRays){
			if (longestRay == null || ray.dist > longestRay.dist){
				longestRay = ray;
			}
			((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
					MagicMissile.FIRE_CONE,
					curUser.sprite,
					ray.path.get(ray.dist),
					null
			);
		}

		//final zap at half distance of the longest ray, for timing of the actual wand effect
		MagicMissile.boltFromChar( curUser.sprite.parent,
				MagicMissile.FIRE_CONE,
				curUser.sprite,
				longestRay.path.get(longestRay.dist/2),
				callback );
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
		Sample.INSTANCE.play( Assets.Sounds.BURNING );
	}

//	@Override
//	protected int chargesPerCast() {
//		if (cursed ||
//				(charger != null && charger.target != null && charger.target.buff(WildMagic.WildMagicTracker.class) != null)){
//			return 1;
//		}
//		//consumes 30% of current charges, rounded up, with a min of 1 and a max of 3.
//		return (int) GameMath.gate(1, (int)Math.ceil(curCharges*0.3f), 3);
//	}

    protected int imaginableChargePerCast() {
        return rank();
    }

    @Override
    public String getRankMessage(int rank){
        return Messages.get(this, "rank" + rank,
                Math.round(magicMin(power(), rank)),
                Math.round(magicMax(power(), rank)),
                getRechargeInfo(rank)
        );
    }

	@Override
	public String statsDesc() {
		if (levelKnown)
			return Messages.get(this, "stats_desc", GameMath.printAverage((int) magicMin(), (int) magicMax()));
		else
			return Messages.get(this, "stats_desc", GameMath.printAverage((int) magicMin(0), (int) magicMax(0)));
	}

	@Override
	public String upgradeStat1(int level) {
		return GameMath.printAverage((1+level), (2+2*level));
	}

	@Override
	public String upgradeStat2(int level) {
		return GameMath.printAverage((2+2*level),2*(4+2*level));
	}

	@Override
	public String upgradeStat3(int level) {
		return GameMath.printAverage((3+3*level),3*(6+2*level));
	}

	@Override
	public void staffFx(WandParticle particle) {
		particle.color( 0xEE7722 );
		particle.am = 0.5f;
		particle.setLifespan(0.6f);
		particle.acc.set(0, -40);
		particle.setSize( 0f, 3f);
		particle.shuffleXY( 1.5f );
	}

}
