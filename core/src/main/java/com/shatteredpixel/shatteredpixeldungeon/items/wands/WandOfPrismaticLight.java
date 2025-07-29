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
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class WandOfPrismaticLight extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_PRISMATIC_LIGHT;

		collisionProperties = Ballistica.FRIENDLY_MAGIC;
	}

	public float magicMin(float lvl){
		return 1+lvl;
	}

	public float magicMax(float lvl){
		return 5+3*lvl;
	}

    @Override
    public int collisionProperties(int target) {
        if (rank() == 2){
            if ((Dungeon.level.passable[target] || Dungeon.level.avoid[target] || Actor.findChar(target) != null)
                    && Dungeon.level.distance(Dungeon.hero.pos, target) <= 6)
                return Ballistica.STOP_TARGET;
        }
        return super.collisionProperties(target);
    }

    @Override
    public float rechargeModifier(int rank) {
        switch (rank){
            case 1: return 1.0f;
            case 2: return 1.6f;
            case 3: return 2.0f;
        }
        return super.rechargeModifier(rank);
    }

    @Override
    public float powerModifier(int rank) {
        switch (rank){
            case 1: return 1.0f;
            case 2: return 0.1f;
            case 3: return 1.4f;
        }
        return super.powerModifier(rank);
    }

    @Override
	public void onZap(Ballistica beam) {
        if (rank() != 3)
		    affectMap(beam);
		
		if (Dungeon.level.viewDistance < 6 && rank() != 3){
            float modification = 1f;
            switch (rank()){
                case 2:
                    modification = 3f;
                    break;
            }
			if (Dungeon.isChallenged(Challenges.DARKNESS)){
				Buff.prolong( curUser, Light.class, (2f + power())*modification);
			} else {
				Buff.prolong( curUser, Light.class, (10f+power()*5)*modification);
			}
		}
        if (rank() != 2){
            Char ch = Actor.findChar(beam.collisionPos);
            if (ch != null && !(Dungeon.isChallenged(Conducts.Conduct.PACIFIST))){
                wandProc(ch, chargesPerCast());
                affectTarget(ch);
            }
        }
	}

	private void affectTarget(Char ch){
		int dmg = damageRoll();

		//three in (5+lvl) chance of failing
		if (Random.Float((5+power())) >= 3 || (rank() == 3)) {
			Buff.prolong(ch, Blindness.class, (2f + (power() * 0.333f)) * ((rank() == 3) ? 2 : 1f));
            if (rank() == 3){
                Buff.prolong(ch, Cripple.class, (4f + (power() * 0.666f)));
            }
			ch.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 6 );
		}

		if (ch.properties().contains(Char.Property.DEMONIC) || ch.properties().contains(Char.Property.UNDEAD)){
            if (rank() == 3){
                ch.sprite.emitter().start( ShadowParticle.UP, 0.01f, 100);
                Sample.INSTANCE.play(Assets.Sounds.BURNING);
                Sample.INSTANCE.play(Assets.Sounds.CURSED);

                if (ch instanceof Mob && ch.resist(Grim.class) >= 1f){
                    ((Mob)ch).EXP = 0;
                    Buff.affect(ch, MasterThievesArmband.StolenTracker.class).setItemStolen(true);
                    ch.die(this);
                }
            } else {
                ch.sprite.emitter().start( ShadowParticle.UP, 0.05f, (int) (10+power()));
                Sample.INSTANCE.play(Assets.Sounds.BURNING);

                ch.damage(Math.round(dmg*1.333f), this);
            }
		} else {
			ch.sprite.centerEmitter().burst( RainbowParticle.BURST, (int) (10+power()));

			ch.damage(dmg, this);
		}

	}

	private void affectMap(Ballistica beam){
		boolean noticed = false;
		for (int c : beam.subPath(0, beam.dist)){
			if (!Dungeon.level.insideMap(c)){
				continue;
			}
			for (int n : PathFinder.NEIGHBOURS9){
				int cell = c+n;

				if (Dungeon.level.discoverable[cell])
					Dungeon.level.mapped[cell] = true;

				int terr = Dungeon.level.map[cell];
				if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {

					Dungeon.level.discover( cell );

					GameScene.discoverTile( cell, terr );
					ScrollOfMagicMapping.discover(cell);

					noticed = true;
				}
			}
            if (rank() == 2) {
                Char ch = Actor.findChar(c);
                if (ch != null && ch.alignment == Char.Alignment.ENEMY && !(Dungeon.isChallenged(Conducts.Conduct.PACIFIST))) {
                    wandProc(ch, chargesPerCast());
                    ch.damage(1, this);
                    ch.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 12);
                    Buff.prolong(ch, Paralysis.class, 3f);
                }
            }

			CellEmitter.center(c).burst( RainbowParticle.BURST, Random.IntRange( 1, 2 ) );
		}
		if (noticed)
			Sample.INSTANCE.play( Assets.Sounds.SECRET );

		GameScene.updateFog();
	}

	@Override
	public String upgradeStat2(int level) {
		return Messages.decimalFormat("#", 100*(1-(3/(float)(5+level)))) + "%";
	}

	@Override
	public String upgradeStat3(int level) {
		if (Dungeon.isChallenged(Challenges.DARKNESS)){
			return Integer.toString(2 + level);
		} else {
			return Integer.toString(10 + 5*level);
		}
	}

	@Override
	public void fx(Ballistica beam, Callback callback) {
		curUser.sprite.parent.add(
				new Beam.LightRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));
		callback.call();
	}

	@Override
	public void onHit(Char attacker, Char defender, int damage) {
		//cripples enemy
		Buff.prolong( defender, Cripple.class, Math.round((1+power())*procChanceMultiplier(attacker)));
	}

	@Override
	public void staffFx(WandParticle particle) {
		particle.color( Random.Int( 0x1000000 ) );
		particle.am = 0.5f;
		particle.setLifespan(1f);
		particle.speed.polar(Random.Float(PointF.PI2), 2f);
		particle.setSize( 1f, 2f);
		particle.radiateXY( 0.5f);
	}

}
