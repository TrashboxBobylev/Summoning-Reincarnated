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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Stasis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.WallOfLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WardSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class WandOfWarding extends Wand {

	{
		image = ItemSpriteSheet.WAND_WARDING;
		usesTargeting = false; //player usually targets wards or spaces, not enemies
	}

	@Override
	public int collisionProperties(int target) {
		if (cursed)                                 return super.collisionProperties(target);
		else if (!Dungeon.level.heroFOV[target])    return Ballistica.PROJECTILE;
		else                                        return Ballistica.STOP_TARGET;
	}

	@Override
	public void execute(Hero hero, String action) {
		//cursed warding does use targeting as it's just doing regular cursed zaps
		usesTargeting = cursed && cursedKnown;
		super.execute(hero, action);
	}

    @Override
    public float rechargeModifier(int rank) {
        switch (rank){
            case 1: return 0.75f;
            case 2: return 2f;
            case 3: return 0.8f;
        }
        return super.rechargeModifier(rank);
    }

    private boolean wardAvailable = true;
	
	@Override
	public boolean tryToZap(Hero owner, int target) {
        if (rank() == 2){
            return super.tryToZap(owner, target);
        }
		
		int currentWardEnergy = 0;
		for (Char ch : Actor.chars()){
			if (ch instanceof Ward){
				currentWardEnergy += ((Ward) ch).tier;
			}
		}

		if (Stasis.getStasisAlly() instanceof Ward){
			currentWardEnergy += ((Ward) Stasis.getStasisAlly()).tier;
		}
		
		int maxWardEnergy = 0;
		for (Buff buff : curUser.buffs()){
			if (buff instanceof Wand.Charger){
				if (((Charger) buff).wand() instanceof WandOfWarding){
					maxWardEnergy += 3 + ((Charger) buff).wand().power()*1.5f;
				}
			}
		}
		
		wardAvailable = (currentWardEnergy < maxWardEnergy);
		
		Char ch = Actor.findChar(target);
		if (ch instanceof Ward){
			if (!wardAvailable && ((Ward) ch).tier <= 3){
				GLog.w( Messages.get(this, "no_more_wards"));
				return false;
			}
		} else {
			if ((currentWardEnergy + 1) > maxWardEnergy){
				GLog.w( Messages.get(this, "no_more_wards"));
				return false;
			}
		}
		
		return super.tryToZap(owner, target);
	}
	
	@Override
	public void onZap(Ballistica bolt) {

        if (rank() == 2){
            int closest = curUser.pos;
            int closestIdx = -1;

            for (int i = 0; i < PathFinder.CIRCLE8.length; i++){
                int ofs = PathFinder.CIRCLE8[i];
                if (Dungeon.level.trueDistance(bolt.collisionPos, curUser.pos+ofs) < Dungeon.level.trueDistance(bolt.collisionPos, closest)){
                    closest = curUser.pos+ofs;
                    closestIdx = i;
                }
            }

            int leftDirX = 0;
            int leftDirY = 0;

            int rightDirX = 0;
            int rightDirY = 0;

            int steps = (int) (2 + power()/3);

            switch (closestIdx){
                case 0: //top left
                    leftDirX = -1;
                    leftDirY = 1;
                    rightDirX = 1;
                    rightDirY = -1;
                    break;
                case 1: //top
                    leftDirX = -1;
                    rightDirX = 1;
                    leftDirY = rightDirY = 0;
                    break;
                case 2: //top right (left and right DIR are purposefully inverted)
                    leftDirX = 1;
                    leftDirY = 1;
                    rightDirX = -1;
                    rightDirY = -1;
                    break;
                case 3: //right
                    leftDirY = -1;
                    rightDirY = 1;
                    leftDirX = rightDirX = 0;
                    break;
                case 4: //bottom right (left and right DIR are purposefully inverted)
                    leftDirX = 1;
                    leftDirY = -1;
                    rightDirX = -1;
                    rightDirY = 1;
                    break;
                case 5: //bottom
                    leftDirX = 1;
                    rightDirX = -1;
                    leftDirY = rightDirY = 0;
                    break;
                case 6: //bottom left
                    leftDirX = -1;
                    leftDirY = -1;
                    rightDirX = 1;
                    rightDirY = 1;
                    break;
                case 7: //left
                    leftDirY = -1;
                    rightDirY = 1;
                    leftDirX = rightDirX = 0;
                    break;
            }

            if (Dungeon.level.blobs.get(WallOfLight.LightWall.class) != null){
                Dungeon.level.blobs.get(WallOfLight.LightWall.class).fullyClear();
            }

            boolean placedWall = false;

            int knockBackDir = PathFinder.CIRCLE8[closestIdx];

            //if all 3 tiles infront of Paladin are blocked, assume cast was in error and cancel
            if (Dungeon.level.solid[closest]
                    && Dungeon.level.solid[curUser.pos + PathFinder.CIRCLE8[(closestIdx+1)%8]]
                    && Dungeon.level.solid[curUser.pos + PathFinder.CIRCLE8[(closestIdx+7)%8]]){
                GLog.w(Messages.get(this, "invalid_target"));
                return;
            }

            placeWall(closest, knockBackDir);

            int leftPos = closest;
            int rightPos = closest;

            //iterate to the left and right, placing walls as we go
            for (int i = 0; i < steps; i++) {
                if (leftDirY != 0) {
                    leftPos += leftDirY * Dungeon.level.width();
                    if (!Dungeon.level.insideMap(leftPos)){
                        break;
                    }
                    placeWall(leftPos, knockBackDir);
                }
                if (leftDirX != 0) {
                    leftPos += leftDirX;
                    if (!Dungeon.level.insideMap(leftPos)){
                        break;
                    }
                    placeWall(leftPos, knockBackDir);
                }
            }
            for (int i = 0; i < steps; i++) {
                if (rightDirX != 0) {
                    rightPos += rightDirX;
                    if (!Dungeon.level.insideMap(rightPos)){
                        break;
                    }
                    placeWall(rightPos, knockBackDir);
                }
                if (rightDirY != 0) {
                    rightPos += rightDirY * Dungeon.level.width();
                    if (!Dungeon.level.insideMap(rightPos)){
                        break;
                    }
                    placeWall(rightPos, knockBackDir);
                }
            }
        } else {

            int target = bolt.collisionPos;
            Char ch = Actor.findChar(target);
            if (ch != null && !(ch instanceof Ward)) {
                if (bolt.dist > 1) target = bolt.path.get(bolt.dist - 1);

                ch = Actor.findChar(target);
                if (ch != null && !(ch instanceof Ward)) {
                    GLog.w(Messages.get(this, "bad_location"));
                    Dungeon.level.pressCell(bolt.collisionPos);
                    return;
                }
            }

            if (ch != null) {
                if (ch instanceof Ward) {
                    if (wardAvailable) {
                        ((Ward) ch).upgrade(power());
                    } else {
                        ((Ward) ch).wandHeal(power());
                    }
                    ch.sprite.emitter().burst(MagicMissile.WardParticle.UP, ((Ward) ch).tier);
                    if (rank() == 3)
                        ((Ward) ch).isBomb = true;
                } else {
                    GLog.w(Messages.get(this, "bad_location"));
                    Dungeon.level.pressCell(target);
                }

            } else if (!Dungeon.level.passable[target]) {
                GLog.w(Messages.get(this, "bad_location"));
                Dungeon.level.pressCell(target);

            } else {
                Ward ward = new Ward();
                ward.pos = target;
                ward.wandLevel = power();
                if ((Dungeon.isChallenged(Conducts.Conduct.PACIFIST)))
                    ward.wandLevel /= 3;
                GameScene.add(ward, 1f);
                Dungeon.level.occupyCell(ward);
                ward.sprite.emitter().burst(MagicMissile.WardParticle.UP, ward.tier);
                Dungeon.level.pressCell(target);
                if (rank() == 3)
                    ward.isBomb = true;

            }
        }
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
        if (rank() == 2){
            callback.call();
        } else {
            MagicMissile m = MagicMissile.boltFromChar(curUser.sprite.parent,
                    MagicMissile.WARD,
                    curUser.sprite,
                    bolt.collisionPos,
                    callback);

            if (bolt.dist > 10) {
                m.setSpeed(bolt.dist * 20);
            }
        }
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}

    private void placeWall( int pos, int knockbackDIR){
        if (!Dungeon.level.solid[pos]) {
            GameScene.add(Blob.seed(pos, 15, WallOfLight.LightWall.class));

            Char ch = Actor.findChar(pos);
            if (ch != null && ch.alignment == Char.Alignment.ENEMY){
                WandOfBlastWave.throwChar(ch, new Ballistica(pos, pos+knockbackDIR, Ballistica.PROJECTILE), 1, false, false, WallOfLight.INSTANCE);
                Buff.affect(ch, Paralysis.class, ch.cooldown());
            }
        }
    }

	@Override
	public void onHit(Char attacker, Char defender, int damage) {
		float level = Math.max( 0, power() );

		// lvl 0 - 20%
		// lvl 1 - 33%
		// lvl 2 - 43%
		float procChance = (level+1f)/(level+5f) * procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {

			float powerMulti = Math.max(1f, procChance);

			for (Char ch : Actor.chars()){
				if (ch instanceof Ward){
					((Ward) ch).wandHeal(power(), powerMulti);
					ch.sprite.emitter().burst(MagicMissile.WardParticle.UP, ((Ward) ch).tier);
				}
			}
		}
	}

	@Override
	public void staffFx(WandParticle particle) {
		particle.color( 0x8822FF );
		particle.am = 0.3f;
		particle.setLifespan(3f);
		particle.speed.polar(Random.Float(PointF.PI2), 0.3f);
		particle.setSize( 1f, 2f);
		particle.radiateXY(2.5f);
	}

	@Override
	public String statsDesc() {
		if (rank() == 1)
			return Messages.get(this, "stats_desc1", (int)Math.ceil(power()*1.5f+3));
        if (rank() == 2)
            return Messages.get(this, "stats_desc2", (int)(power()/3+2));
        else
            return Messages.get(this, "stats_desc3", (int)Math.ceil(power()*1.5f+3));
	}

    @Override
    public String generalRankDescription(int rank) {
        if (rank == 1)
            return Messages.get(this, "rank" + rank,
                    Math.round(4 + power()),
                    Math.round(Math.round(8 + 5.33f*power())),
                    getRechargeInfo(rank),
                    (int)Math.ceil(power()*1.5f)+3
            );
        if (rank == 2)
            return Messages.get(this, "rank" + rank,
                    getRechargeInfo(rank),
                    Math.round(2+power()/3),
                    1 + Dungeon.depth/5
            );
        return Messages.get(this, "rank" + rank,
                Math.round(4 + power()),
                Math.round(8 + 5.33f*power()),
                Math.round((2 + power())*4),
                Math.round((8 + 5.33f*power())*4),
                getRechargeInfo(rank),
                (int)Math.ceil(power()*1.5f)+3
        );
    }

    @Override
	public String upgradeStat1(int level) {
		return 2+level + "-" + (8+4*level);
	}

	@Override
	public String upgradeStat2(int level) {
		return Integer.toString(level+2);
	}

	public static class Ward extends NPC {

		public int tier = 1;
		private float wandLevel = 1;

		public int totalZaps = 0;
        public boolean isBomb = false;

		{
			spriteClass = WardSprite.class;

			alignment = Alignment.ALLY;

			properties.add(Property.IMMOVABLE);
			properties.add(Property.INORGANIC);

			viewDistance = 4;
			state = WANDERING;
		}

		@Override
		public String name() {
			return Messages.get(this, "name_" + tier );
		}

		public void upgrade(float wandLevel ){
			if (this.wandLevel < wandLevel){
				this.wandLevel = wandLevel;
			}

			switch (tier){
				case 1: case 2: default:
					break; //do nothing
				case 3:
					HT = 35;
					HP = 15 + (5-totalZaps)*4;
					break;
				case 4:
					HT = 54;
					HP += 19;
					break;
				case 5:
					HT = 84;
					HP += 30;
					break;
				case 6:
					wandHeal(wandLevel);
					break;
			}

			if (Actor.chars().contains(this) && tier >= 3){
				Bestiary.setSeen(WardSentry.class);
			}

			if (tier < 6){
				tier++;
				viewDistance++;
				if (sprite != null){
					((WardSprite)sprite).updateTier(tier);
					sprite.place(pos);
				}
				GameScene.updateFog(pos, viewDistance+1);
			}

		}

        @Override
        public void damage(int dmg, Object src) {
            super.damage(dmg, src);
            if (isBomb){
                die(src);
                Sample.INSTANCE.play(Assets.Sounds.BLAST, 1.5f);
                PathFinder.buildDistanceMap( pos, BArray.not( Dungeon.level.solid, null ), tier );
                for (int i = 0; i < PathFinder.distance.length; i++) {
                    if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                        CellEmitter.center(i).burst(MagicMissile.WardParticle.FACTORY, 15);
                        Char ch = Actor.findChar(i);
                        if (ch != null && ch.alignment == Alignment.ENEMY) {
                            Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC);
                            int damage = Math.round(Hero.heroDamageIntRange((int) (4 + wandLevel), (int) (8 + 5.33f*wandLevel))*Math.max(1, tier*2f/3));
                            damage *= 1f - (Dungeon.level.trueDistance(ch.pos, pos)-1)/tier;
                            ch.damage(damage, this);
                            if (ch.isAlive()){
                                Wand.wandProc(ch, wandLevel, 1);
                            }
                        }
                    }
                }
            }
        }

        //this class is used so that wards and sentries can have two entries in the Bestiary
		public static class WardSentry extends Ward{};

		public void wandHeal( float wandLevel ){
			wandHeal( wandLevel, 1f );
		}

		public void wandHeal( float wandLevel, float healFactor ){
			if (this.wandLevel < wandLevel){
				this.wandLevel = wandLevel;
			}

			int heal;
			switch(tier){
				default:
					return;
				case 2:
					heal = Math.round(1 * healFactor);
					break;
				case 3:
					heal = Math.round(Random.IntRange(1, 2) * healFactor);
					break;
				case 4:
					heal = Math.round(9 * healFactor); //9/5 1.8
					break;
				case 5:
					heal = Math.round(12 * healFactor); //12/6, 2
					break;
				case 6:
					heal = Math.round(16 * healFactor); //16/7, 2.28
					break;
			}

			if (tier <= 3){
				totalZaps = (Math.max(0, totalZaps-heal));
			} else {
				HP = Math.min(HT, HP + heal);
			}
			if (sprite != null) sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(heal), FloatingText.HEALING);

		}

		@Override
		public int defenseSkill(Char enemy) {
			if (tier > 3){
				defenseSkill = 4 + Dungeon.scalingDepth();
			}
			return super.defenseSkill(enemy);
		}

		@Override
		public int drRoll() {
			int dr = super.drRoll();
			if (tier > 3){
				return dr + Math.round(Random.NormalIntRange(0, 3 + Dungeon.scalingDepth()/2) / (7f - tier));
			} else {
				return dr;
			}
		}

		@Override
		protected boolean canAttack( Char enemy ) {
			return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
		}

        @Override
        public float targetPriority() {
            if (isBomb)
                return super.targetPriority()*5f;
            return super.targetPriority();
        }

        @Override
		protected boolean doAttack(Char enemy) {
            if (isBomb) {
                spend( attackDelay() );
                return true;
            }
			boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
			if (visible) {
				sprite.zap( enemy.pos );
			} else {
				zap();
			}

			return !visible;
		}

		private void zap() {
			spend( 1f );

			//always hits
			int dmg = Hero.heroDamageIntRange((int) (4 + wandLevel), (int) (8 + 5.33f*wandLevel));
			Char enemy = this.enemy;
			enemy.damage( dmg, this );
			if (enemy.isAlive()){
				Wand.wandProc(enemy, wandLevel, 1);
			}

			if (!enemy.isAlive() && enemy == Dungeon.hero) {
				Badges.validateDeathFromFriendlyMagic();
				GLog.n(Messages.capitalize(Messages.get( this, "kill", name() )));
				Dungeon.fail( WandOfWarding.class );
			}

			totalZaps++;
			switch(tier){
				case 1: case 2: case 3: default:
					if (totalZaps >= (2*tier-1)){
						die(this);
					}
					break;
				case 4:
					damage(5, this);
					break;
				case 5:
					damage(6, this);
					break;
				case 6:
					damage(7, this);
					break;
			}
		}

		public void onZapComplete() {
			zap();
			next();
		}

		@Override
		protected boolean getCloser(int target) {
			return false;
		}

		@Override
		protected boolean getFurther(int target) {
			return false;
		}

		@Override
		public CharSprite sprite() {
			WardSprite sprite = (WardSprite) super.sprite();
			sprite.linkVisuals(this);
			return sprite;
		}

		@Override
		public void updateSpriteState() {
			super.updateSpriteState();
			((WardSprite)sprite).updateTier(tier);
			sprite.place(pos);
		}
		
		@Override
		public void destroy() {
			super.destroy();
			Dungeon.observe();
			GameScene.updateFog(pos, viewDistance+1);
		}
		
		@Override
		public boolean canInteract(Char c) {
			return true;
		}

		@Override
		public boolean interact( Char c ) {
			if (c != Dungeon.hero){
				return true;
			}
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(new WndOptions( sprite(),
							Messages.get(Ward.this, "dismiss_title"),
							Messages.get(Ward.this, "dismiss_body"),
							Messages.get(Ward.this, "dismiss_confirm"),
							Messages.get(Ward.this, "dismiss_cancel") ){
						@Override
						protected void onSelect(int index) {
							if (index == 0){
								die(null);
							}
						}
					});
				}
			});
			return true;
		}

		@Override
		public String description() {
			if (!Actor.chars().contains(this)){
				//for viewing in the journal
				if (tier < 4){
					return Messages.get(this, "desc_generic_ward");
				} else {
					return Messages.get(this, "desc_generic_sentry");
				}
			} else {
                if (isBomb)
                    return Messages.get(this, "desc_bomb", GameMath.printAverage((int) ((4 + wandLevel)*Math.max(1, tier*2f/3)), (int) ((8 + 5.33f * wandLevel)*Math.max(1, tier*2f/3))), tier, 1 + tier*2);
				return Messages.get(this, "desc_" + tier, GameMath.printAverage((int) (4 + wandLevel), (int) (8 + 5.33f * wandLevel)), tier);
			}
		}
		
		{
			immunities.add( Sleep.class );
			immunities.add( Terror.class );
			immunities.add( Dread.class );
			immunities.add( Vertigo.class );
			immunities.add( AllyBuff.class );
		}

		private static final String TIER = "tier";
		private static final String WAND_LEVEL = "wand_level";
		private static final String TOTAL_ZAPS = "total_zaps";
        private static final String BOMB       = "bomb";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(TIER, tier);
			bundle.put(WAND_LEVEL, wandLevel);
			bundle.put(TOTAL_ZAPS, totalZaps);
            bundle.put(BOMB, isBomb);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			tier = bundle.getInt(TIER);
			viewDistance = 3 + tier;
			wandLevel = bundle.getFloat(WAND_LEVEL);
			totalZaps = bundle.getInt(TOTAL_ZAPS);
            if (bundle.contains(BOMB))
                isBomb = bundle.getBoolean(BOMB);
		}
	}
}
