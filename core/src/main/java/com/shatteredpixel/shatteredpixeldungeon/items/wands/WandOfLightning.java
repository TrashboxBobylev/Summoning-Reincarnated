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
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public class WandOfLightning extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_LIGHTNING;
	}
	
	private ArrayList<Char> affected = new ArrayList<>();

	private ArrayList<Lightning.Arc> arcs = new ArrayList<>();

	public float magicMin(float lvl){
		return 6+lvl;
	}

	public float magicMax(float lvl){
		return 12+6*lvl;
	}

    @Override
    public float timeToZap() {
        if (rank() == 2){
            return 5f;
        }
        return super.timeToZap();
    }

    @Override
    public float powerModifier(int rank) {
        switch (rank){
            case 1: return 1.0f;
            case 2: return 4.5f;
            case 3: return 0.66f;
        }
        return super.powerModifier(rank);
    }

    @Override
    public float rechargeModifier(int rank) {
        switch (rank){
            case 1: return 1.0f;
            case 2: return 3f;
            case 3: return 1.5f;
        }
        return super.rechargeModifier(rank);
    }

    @Override
    public int collisionProperties(int target) {
        if (rank() == 2){
            return Ballistica.STOP_TARGET;
        }
        return super.collisionProperties(target);
    }

    @Override
	public void onZap(Ballistica bolt) {

		for (Char ch : affected.toArray(new Char[0])){
			if (ch != curUser && ch.alignment == curUser.alignment && ch.pos != bolt.collisionPos){
				affected.remove(ch);
			} else if (ch.buff(LightningCharge.class) != null && ch.buff(LightningCharge.class).rank == 1){
				affected.remove(ch);
			}
		}

		//lightning deals less damage per-target, the more targets that are hit.
        float[] ratios = new float[]{0.4f, 0.6f};
        switch (rank()){
            case 2:
                ratios[0] = 0.9f;
                ratios[1] = 0.1f;
                break;
        }
		float multiplier = ratios[0] + (ratios[1]/affected.size());
		//if the main target is in water, all affected take full damage
		if ((Dungeon.level.water[bolt.collisionPos] || rank() == 3) && rank() != 2) multiplier = 1f;

		for (Char ch : affected){
			if (!(Dungeon.isChallenged(Conducts.Conduct.PACIFIST))) {
				if (ch == Dungeon.hero) PixelScene.shake(2, 0.3f);
				ch.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
				ch.sprite.flash();


				wandProc(ch, chargesPerCast());
				if (ch == curUser && ch.isAlive()) {
					ch.damage(Math.round(damageRoll() * multiplier * 0.5f), this);
					if (!curUser.isAlive()) {
						Badges.validateDeathFromFriendlyMagic();
						Dungeon.fail(this);
						GLog.n(Messages.get(this, "ondeath"));
					}
				} else {
					ch.damage(Math.round(damageRoll() * multiplier), this);
				}
                if (rank() == 3){
                    Buff.affect(ch, Paralysis.class, 2f);
                    if (!ch.isAlive()){
                        GameScene.add(Blob.seed(ch.pos, 5, Electricity.class));
                    }
                }
			}
		}
	}

	@Override
	public void onHit(Char attacker, Char defender, int damage) {

		// lvl 0 - 25%
		// lvl 1 - 40%
		// lvl 2 - 50%
		float procChance = (buffedLvl()+1f)/(buffedLvl()+4f) * procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {

			float powerMulti = Math.min(1f, procChance);

			FlavourBuff.prolong(attacker, LightningCharge.class, powerMulti*LightningCharge.DURATION).rank = rank();
			attacker.sprite.centerEmitter().burst( SparkParticle.FACTORY, 10 );
			attacker.sprite.flash();
			Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );

		}
	}

	public static class LightningCharge extends FlavourBuff {

		{
			type = buffType.POSITIVE;
		}

		public static float DURATION = 10f;

		@Override
		public int icon() {
			return BuffIndicator.IMBUE;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(1, 1, 0);
		}

        public int rank = 1;

        @Override
        public HashSet<Class> immunities() {
            HashSet<Class> immunitiesList = super.immunities();
            if (rank == 3){
                immunitiesList.add(WandOfLightning.class);
                immunitiesList.add(Electricity.class);
                immunitiesList.add(Elemental.ShockElemental.class);
            }
            return immunitiesList;
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc" + rank, dispTurns());
        }

        private static final String RANK = "rank";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(RANK, rank);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            rank = bundle.getInt(RANK);
        }
    }

	private void arc( Char ch ) {

		int dist = Dungeon.level.water[ch.pos] ? 2 : 1;
        if (rank() == 3){
            dist *= 2;
        }
        if (rank() == 2){
            dist = 1;
        }

		if (curUser.buff(LightningCharge.class) != null && curUser.buff(LightningCharge.class).rank == 1){
			dist++;
		}

		ArrayList<Char> hitThisArc = new ArrayList<>();
		PathFinder.buildDistanceMap( ch.pos, BArray.not( Dungeon.level.solid, null ), dist );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE){
				Char n = Actor.findChar( i );
				if (n == Dungeon.hero && PathFinder.distance[i] > 1)
					//the hero is only zapped if they are adjacent
					continue;
				else if (n != null && !affected.contains( n )) {
					hitThisArc.add(n);
				}
			}
		}
		
		affected.addAll(hitThisArc);
		for (Char hit : hitThisArc){
			arcs.add(new Lightning.Arc(ch.sprite.center(), hit.sprite.center()));
            if (rank() != 2)
			    arc(hit);
		}
	}
	
	@Override
	public void fx(Ballistica bolt, Callback callback) {

		affected.clear();
		arcs.clear();

		int cell = bolt.collisionPos;
        PointF source = curUser.sprite.center();
        if (rank() == 2){
            source = DungeonTilemap.raisedTileCenterToWorld(cell);
            source.y -= source.y*5;
        }

		Char ch = Actor.findChar( cell );
		if (ch != null) {
			if (ch instanceof DwarfKing){
				Statistics.qualifiedForBossChallengeBadge = false;
			}

			affected.add( ch );
            if (rank() == 2){
                for (int i = 0; i < 5; i++){
                    PointF dest = ch.sprite.center();
                    dest.x += Random.Int(-4, 4);
                    arcs.add( new Lightning.Arc(source, dest));
                }
            } else {
                arcs.add(new Lightning.Arc(source, ch.sprite.center()));
            }
			arc(ch);
		} else {
            if (rank() == 2){
                for (int i = 0; i < 5; i++){
                    PointF dest = DungeonTilemap.raisedTileCenterToWorld(bolt.collisionPos);
                    dest.x += Random.Int(-4, 4);
                    arcs.add( new Lightning.Arc(source, dest));
                }
            } else {
                arcs.add( new Lightning.Arc(source, DungeonTilemap.raisedTileCenterToWorld(bolt.collisionPos)));
            }
			CellEmitter.center( cell ).burst( SparkParticle.FACTORY, 3 );
		}

        if (rank() == 2){
            curUser.sprite.parent.add(new Lightning(cell - 1, cell + 1, null));
            curUser.sprite.parent.add(new Lightning(cell - Dungeon.level.width(), cell + Dungeon.level.width(), null));
            curUser.sprite.parent.add(new Lightning(cell - 1 - Dungeon.level.width(), cell + 1 + Dungeon.level.width(), null));
            curUser.sprite.parent.add(new Lightning(cell - 1 + Dungeon.level.width(), cell + 1 - Dungeon.level.width(), null));
        }

		//don't want to wait for the effect before processing damage.
		curUser.sprite.parent.addToFront( new Lightning( arcs, null ) );
		Sample.INSTANCE.play( rank() == 2 ? Assets.Sounds.LIGHTNING_BOLT : Assets.Sounds.LIGHTNING );
		callback.call();
	}

	@Override
	public void staffFx(WandParticle particle) {
		particle.color(0xFFFFFF);
		particle.am = 0.6f;
		particle.setLifespan(0.6f);
		particle.acc.set(0, +10);
		particle.speed.polar(-Random.Float(3.1415926f), 6f);
		particle.setSize(0f, 1.5f);
		particle.sizeJitter = 1f;
		particle.shuffleXY(1f);
		float dst = Random.Float(1f);
		particle.x -= dst;
		particle.y += dst;
	}
	
}
