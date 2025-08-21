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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Empowered;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Stasis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EarthGuardianSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.ColorMath;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class WandOfLivingEarth extends DamageWand {
	
	{
		image = ItemSpriteSheet.WAND_LIVING_EARTH;
	}
	
	@Override
	public float magicMin(float lvl) {
		return 4;
	}
	
	@Override
	public float magicMax(float lvl) {
		return 6 + 3*lvl;
	}

    @Override
    public float powerModifier(int rank) {
        switch (rank){
            case 1: return 1.0f;
            case 2: return 2.0f;
            case 3: return 9.0f;
        }
        return super.powerModifier(rank);
    }

    @Override
    public float rechargeModifier(int rank) {
        switch (rank){
            case 1: return 1.0f;
            case 2: return 2.5f;
            case 3: return 25000f;
        }
        return super.powerModifier(rank);
    }

    @Override
    protected int chargesPerCast() {
        if (rank() == 3){
            return maxCharges;
        }
        return super.chargesPerCast();
    }

    @Override
	public void onZap(Ballistica bolt) {
		Char ch = Actor.findChar(bolt.collisionPos);
		int damage = damageRoll();
		int armorToAdd = damage;

		EarthGuardian guardian = null;
		for (Mob m : Dungeon.level.mobs){
			if (m instanceof EarthGuardian){
				guardian = (EarthGuardian) m;
				break;
			}
		}

		if (Stasis.getStasisAlly() instanceof EarthGuardian){
			guardian = (EarthGuardian)Stasis.getStasisAlly();
		}

		RockArmor buff = curUser.buff(RockArmor.class);
		//only grant armor if we are shooting at an enemy, a hiding mimic, or the guardian
		if ((guardian == null || ch != guardian) && (ch == null
				|| ch.alignment == Char.Alignment.ALLY
				|| ch.alignment == Char.Alignment.NEUTRAL && !(ch instanceof Mimic))){
			armorToAdd = 0;
		} else {
            if (rank() == 3){
                armorToAdd = (int) (curUser.HT*4.5f);
            }
			if (buff == null && guardian == null) {
				buff = Buff.affect(curUser, RockArmor.class);
			}
			if (buff != null) {
				buff.addArmor( power(), rank(), armorToAdd);
			}
		}

		//shooting at the guardian
		if (guardian != null && guardian == ch){
			guardian.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, (int) (8 + power() / 2));
			guardian.setInfo(curUser, power(), rank(), armorToAdd);
			wandProc(guardian, chargesPerCast());
			Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, 0.9f * Random.Float(0.87f, 1.15f) );

		//shooting the guardian at a location
		} else if ( guardian == null && buff != null && (buff.armor >= buff.armorToGuardian() && rank() != 3)){

			//create a new guardian
			guardian = new EarthGuardian();
			guardian.setInfo(curUser, power(), rank(), buff.armor);

			if (buff.powerOfManyTurns > 0){
				Buff.affect(guardian, PowerOfMany.PowerBuff.class, buff.powerOfManyTurns);
			}

			//if the collision pos is occupied (likely will be), then spawn the guardian in the
			//adjacent cell which is closes to the user of the wand.
			if (ch != null) {

				ch.sprite.centerEmitter().burst(MagicMissile.EarthParticle.BURST, (int) (5 + power() / 2));

				if (!(Dungeon.isChallenged(Conducts.Conduct.PACIFIST))){
					wandProc(ch, chargesPerCast());
					ch.damage(damage, this);
				}

				int closest = -1;
				boolean[] passable = Dungeon.level.passable;

				for (int n : PathFinder.NEIGHBOURS9) {
					int c = bolt.collisionPos + n;
					if (passable[c] && Actor.findChar( c ) == null
						&& (closest == -1 || (Dungeon.level.trueDistance(c, curUser.pos) < (Dungeon.level.trueDistance(closest, curUser.pos))))) {
						closest = c;
					}
				}

				if (closest == -1){
					if (armorToAdd > 0) {
						curUser.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, (int) (8 + power() / 2));
					}
					return; //do not spawn guardian or detach buff
				} else {
					guardian.pos = closest;
					GameScene.add(guardian, 1);
					Dungeon.level.occupyCell(guardian);
				}

				if (ch.alignment == Char.Alignment.ENEMY || ch.buff(Amok.class) != null) {
					guardian.aggro(ch);
				}

			} else {
				guardian.pos = bolt.collisionPos;
				GameScene.add(guardian, 1);
				Dungeon.level.occupyCell(guardian);
			}

			guardian.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, (int) (8 + power()/2));
			buff.detach();
			Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, 0.9f * Random.Float(0.87f, 1.15f) );

		//shooting at a location/enemy with no guardian being shot
		} else {

			if (ch != null) {

				ch.sprite.centerEmitter().burst(MagicMissile.EarthParticle.BURST, (int) (5 + power() / 2));

				wandProc(ch, chargesPerCast());
				ch.damage(damage, this);
				Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, 0.8f * Random.Float(0.87f, 1.15f) );
				
				if (guardian == null) {
					if (armorToAdd > 0) {
						curUser.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, (int) (8 + power() / 2));
					}
				} else {
					if (guardian.sprite != null) { //may be in stasis
						guardian.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, (int) (8 + power() / 2));
					}
					guardian.setInfo(curUser, power(), rank(), armorToAdd);
					if (ch.alignment == Char.Alignment.ENEMY || ch.buff(Amok.class) != null) {
						guardian.aggro(ch);
					}
				}

			} else {
				Dungeon.level.pressCell(bolt.collisionPos);
			}
		}

	}

	@Override
	public String upgradeStat2(int level) {
		return Integer.toString(16 + 8*level);
	}

	@Override
	public String upgradeStat3(int level) {
		if (Dungeon.mode == Dungeon.GameMode.NINE_CHAL){
			return level + "-" + (2+level);
		} else {
			return level + "-" + (3+(3*level));
		}
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar(curUser.sprite.parent,
				MagicMissile.EARTH,
				curUser.sprite,
				bolt.collisionPos,
				callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}
	
	@Override
	public void onHit(Char attacker, Char defender, int damage) {
        if (rank() == 1) {
            EarthGuardian guardian = null;
            for (Mob m : Dungeon.level.mobs) {
                if (m instanceof EarthGuardian) {
                    guardian = (EarthGuardian) m;
                    break;
                }
            }

            int armor = Math.round(damage * 0.33f * procChanceMultiplier(attacker));

            if (guardian != null) {
                guardian.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, (int) (8 + power() / 2));
                guardian.setInfo(Dungeon.hero, power(), rank(), armor);
            } else {
                attacker.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, (int) (8 + power() / 2));
                Buff.affect(attacker, RockArmor.class).addArmor(power(), rank(), armor);
            }
        }
        if (rank() == 2){
            EarthGuardian guardian = null;
            for (Mob m : Dungeon.level.mobs) {
                if (m instanceof EarthGuardian) {
                    guardian = (EarthGuardian) m;
                    break;
                }
            }

            if (guardian != null){
                Buff.prolong(guardian, Empowered.class, power()-1);
            }
        }
        if (rank() == 3 && curCharges > 0){
            ConeAOE cone = new ConeAOE( new Ballistica(attacker.pos, defender.pos, Ballistica.WONT_STOP),
                    5,
                    60,
                    Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);

            //cast to cells at the tip, rather than all cells, better performance.
            Ballistica longestRay = null;
            for (Ballistica ray : cone.outerRays){
                if (longestRay == null || ray.dist > longestRay.dist){
                    longestRay = ray;
                }
                ((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
                        MagicMissile.EARTH_CONE,
                        curUser.sprite,
                        ray.path.get(ray.dist),
                        null
                );
            }

            //final zap at half distance of the longest ray, for timing of the actual wand effect
            MagicMissile.boltFromChar( curUser.sprite.parent,
                    MagicMissile.EARTH_CONE,
                    curUser.sprite,
                    longestRay.path.get(longestRay.dist/2),
                    () -> {
                        ArrayList<Char> affectedChars = new ArrayList<>();
                        for( int cell : cone.cells ){

                            //ignore caster cell
                            if (cell == attacker.pos){
                                continue;
                            }

                            CellEmitter.get( cell ).start(Speck.factory(Speck.ROCK), 0.07f, 10);
                            Dungeon.level.pressCell(cell);

                            Char ch = Actor.findChar( cell );
                            if (ch != null && !(Dungeon.isChallenged(Conducts.Conduct.PACIFIST)) && ch.alignment != Char.Alignment.ALLY) {
                                affectedChars.add(ch);
                            }
                        }

                        for ( Char ch : affectedChars ){
                            ch.damage(damageRoll()/3, this);
                            if (ch.isAlive()) {
                                Buff.affect(ch, Paralysis.class, 3f);
                            }
                        }

                        PixelScene.shake(3, 0.7f);
                    } );
            Sample.INSTANCE.play( Assets.Sounds.ZAP );
            Sample.INSTANCE.play( Assets.Sounds.ROCKS );
            curCharges--;
        }
	}
	
	@Override
	public void staffFx(WandParticle particle) {
		if (Random.Int(10) == 0){
			particle.color(ColorMath.random(0xFFF568, 0x80791A));
		} else {
			particle.color(ColorMath.random(0x805500, 0x332500));
		}
		particle.am = 1f;
		particle.setLifespan(2f);
		particle.setSize( 1f, 2f);
		particle.shuffleXY(0.5f);
		float dst = Random.Float(11f);
		particle.x -= dst;
		particle.y += dst;
	}

    @Override
    public String generalRankDescription(int rank) {
        return Messages.get(this, "rank" + rank,
                GameMath.printAverage(
                        Math.round(magicMin(power())*powerModifier(rank)),
                        Math.round(magicMax(power())*powerModifier(rank))
                ),
                getRechargeInfo(rank),
                neededArmor(rank, power()),
                Math.round(damageRatio(rank)*100),
                Math.round(16 + 8 * power())*rank
        );
    }

    public static int neededArmor(int rank, float power){
        switch (rank){
            case 1: return Math.round(8 + power*4);
            case 2: return Math.round(16 + power*8);
            case 3: return Dungeon.hero != null ? Math.round(Dungeon.hero.HT * 4.5f) : 90;
        }
        return 0;
    }
    public static float damageRatio(int rank){
        switch (rank){
            case 1: return 1/2f;
            case 2: return 0f;
            case 3: return 1f;
        }
        return 0f;
    }

    @Override
    public String battlemageDesc(int rank) {
        if (rank == 2){
           return Messages.get(this, "rank_bm" + rank, (int)(power()-1));
        }
        if (rank == 3){
            return Messages.get(this, "rank_bm" + rank, GameMath.printAverage((int)(magicMin(power())*powerModifier(rank)/3), (int)(magicMax(power())*powerModifier(rank)/3)));
        }
        return super.battlemageDesc(rank);
    }

    public static class RockArmor extends Buff {

		{
			type = buffType.POSITIVE;
		}

		private float wandLevel;
		private int armor;
        private int rank;

		private float powerOfManyTurns = 0;

		@Override
		public boolean act() {
			if (powerOfManyTurns > 0){
				powerOfManyTurns--;
				if (powerOfManyTurns <= 0){
					powerOfManyTurns = 0;
					BuffIndicator.refreshHero();
				}
			}
			spend(TICK);
			return true;
		}

		private void addArmor(float wandLevel, int rank, int toAdd ){
			this.wandLevel = Math.max(this.wandLevel, wandLevel);
            this.rank = rank;
			armor += toAdd;
			armor = (int) Math.min(armor, 2*armorToGuardian());
		}

		private float armorToGuardian(){
            return neededArmor(rank, wandLevel);
		}



		public int absorb( int damage ) {
			int block = (int) (damage - damage*(1f-damageRatio(rank)));
			if (armor <= block) {
				detach();
				return damage - armor;
			} else {
				armor -= block;
				return damage - block;
			}
		}

		public boolean isEmpowered(){
			return powerOfManyTurns > 0;
		}

		@Override
		public int icon() {
			return BuffIndicator.ARMOR;
		}

		@Override
		public void tintIcon(Image icon) {
			if (isEmpowered()){
				icon.hardlight(1.8f, 1.8f, 0.6f);
			} else {
				icon.brightness(0.6f);
			}
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (armorToGuardian() - armor) / armorToGuardian());
		}

		@Override
		public String iconTextDisplay() {
			return Integer.toString(armor);
		}

		@Override
		public String desc() {
			String desc = Messages.get( this, "desc", Math.round(damageRatio(rank)*100), armor, (int)armorToGuardian());
			if (isEmpowered()){
				desc += "\n\n" + Messages.get(this, "desc_many", (int)powerOfManyTurns);
			}
			return desc;
		}

		private static final String WAND_LEVEL = "wand_level";
		private static final String ARMOR = "armor";
        private static final String RANK = "rank";
		private static final String POWER_TURNS = "power_turns";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(WAND_LEVEL, wandLevel);
			bundle.put(ARMOR, armor);
			bundle.put(POWER_TURNS, powerOfManyTurns);
            bundle.put(RANK, rank);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			wandLevel = bundle.getFloat(WAND_LEVEL);
			armor = bundle.getInt(ARMOR);
			powerOfManyTurns = bundle.getFloat(POWER_TURNS);
            rank = bundle.getInt(RANK);
		}
	}

	public static class EarthGuardian extends NPC {

		{
			spriteClass = EarthGuardianSprite.class;

			alignment = Alignment.ALLY;
			state = HUNTING;
			intelligentAlly = true;

			properties.add(Property.INORGANIC);

			WANDERING = new Wandering();

			//before other mobs
			actPriority = MOB_PRIO + 1;

			HP = HT = 0;
		}

		private float wandLevel = -1;
        private int rank = 1;

		public void setInfo(Hero hero, float wandLevel, int rank, int healthToAdd){
			if (wandLevel > this.wandLevel) {
				this.wandLevel = wandLevel;
				HT = Math.round(16 + 8 * wandLevel);
                if (rank == 2){
                    HT *= 2;
                }
			}
            this.rank = rank;
			if (HP != 0 && sprite != null){
				sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(healthToAdd), FloatingText.HEALING);
			}
			HP = Math.min(HT, HP + healthToAdd);
			//half of hero's evasion
			defenseSkill = (hero.lvl + 4)/2;
            if (rank == 2){
                defenseSkill /= 1.5f;
            }
		}

		@Override
		public int attackSkill(Char target) {
			//same as the hero
            int accuracy = 2 * defenseSkill + 5;
            if (rank == 2){
                accuracy *= 1.5f;
            }
            return accuracy;
		}

		@Override
		public int attackProc(Char enemy, int damage) {
			if (enemy instanceof Mob) ((Mob)enemy).aggro(this);
			return super.attackProc(enemy, damage);
		}

		@Override
		public int damageRoll() {
            int damage = Random.NormalIntRange(2, 4 + Dungeon.scalingDepth() / 2);
            if (rank == 2){
                damage *= 3;
            }
            return damage;
		}

        @Override
        public float attackDelay() {
            if (rank == 2){
                return super.attackDelay()*2;
            }
            return super.attackDelay();
        }

        @Override
		public int drRoll() {
			int dr = super.drRoll();
			if (Dungeon.mode == Dungeon.GameMode.NINE_CHAL){
				dr += Random.NormalIntRange((int) wandLevel, (int) (2 + wandLevel));
			} else {
				dr += Random.NormalIntRange((int) wandLevel, (int) (3 + 3 * wandLevel));
			}
            if (rank == 2){
                dr *= 1.5f;
            }
            return dr;
		}

		@Override
		public String description() {
			String desc = Messages.get(this, "desc");

			if (Actor.chars().contains(this)) {
                int minDr, maxDr;
                if (Dungeon.mode == Dungeon.GameMode.NINE_CHAL){
                    minDr = (int)wandLevel; maxDr = (int)(2 + wandLevel);
                } else {
                    minDr = (int) wandLevel; maxDr = (int) (3 + 3 * wandLevel);
                }
                if (rank == 2){
                    minDr *= 1.5f; maxDr *= 1.5f;
                }
                desc += "\n\n" + Messages.get(this, "wand_info", minDr, maxDr);
			}

			return desc;
			
		}
		
		{
			immunities.add( AllyBuff.class );
		}

		private static final String DEFENSE = "defense";
		private static final String WAND_LEVEL = "wand_level";
        private static final String RANK = "rank";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(DEFENSE, defenseSkill);
			bundle.put(WAND_LEVEL, wandLevel);
            bundle.put(RANK, rank);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			defenseSkill = bundle.getInt(DEFENSE);
			wandLevel = bundle.getFloat(WAND_LEVEL);
            rank = bundle.getInt(RANK);
		}

		private class Wandering extends Mob.Wandering{

			@Override
			public boolean act(boolean enemyInFOV, boolean justAlerted) {
				if (!enemyInFOV){
					Buff.affect(Dungeon.hero, RockArmor.class).addArmor(wandLevel, rank, HP);
					if (buff(PowerOfMany.PowerBuff.class) != null){
						Buff.affect(Dungeon.hero, RockArmor.class).powerOfManyTurns = buff(PowerOfMany.PowerBuff.class).cooldown()+1;
					}
					Dungeon.hero.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, (int) (8 + wandLevel/2));
					destroy();
					sprite.die();
					return true;
				} else {
					return super.act(enemyInFOV, justAlerted);
				}
			}

		}

	}
}
