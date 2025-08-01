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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TenguSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public class Tengu extends Mob {
	
	{
		spriteClass = TenguSprite.class;
		
		HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 250 : 200;
		EXP = 20;
		defenseSkill = 15;
		
		HUNTING = new Hunting();
		
		properties.add(Property.BOSS);
		properties.add(Property.RANGED);
		
		viewDistance = 12;
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 6, 12 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		if (Dungeon.level.adjacent(pos, target.pos)){
			return 10;
		} else {
			return 20;
		}
	}
	
	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 5);
	}

	boolean loading = false;

	//Tengu is immune to debuffs and damage when removed from the level
	@Override
	public boolean add(Buff buff) {
		if (Actor.chars().contains(this) || buff instanceof Doom || loading){
			return super.add(buff);
		}
		return false;
	}

	@Override
	public void damage(int dmg, Object src) {
		if (!Dungeon.level.mobs.contains(this)){
			return;
		}

		PrisonBossLevel.State state = ((PrisonBossLevel)Dungeon.level).state();
		
		int hpBracket = HT / 8;

		int curbracket = HP / hpBracket;

		int beforeHitHP = HP;
		super.damage(dmg, src);

		//cannot be hit through multiple brackets at a time
		if (HP <= (curbracket-1)*hpBracket){
			HP = (curbracket-1)*hpBracket + 1;
		}

		int newBracket =  HP / hpBracket;
		dmg = beforeHitHP - HP;

		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null && !isImmune(src.getClass()) && !isInvulnerable(src.getClass())){
			if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES))   lock.addTime(2*dmg/3f);
			else                                                    lock.addTime(dmg);
		}

		//phase 2 of the fight is over
		if (HP == 0 && state == PrisonBossLevel.State.FIGHT_ARENA) {
			//let full attack action complete first
			Actor.add(new Actor() {

				{
					actPriority = VFX_PRIO;
				}

				@Override
				protected boolean act() {
					Actor.remove(this);
					((PrisonBossLevel)Dungeon.level).progress();
					return true;
				}
			});
			return;
		}

		//phase 1 of the fight is over
		if (state == PrisonBossLevel.State.FIGHT_START && HP <= HT/2){
			HP = (HT/2);
			yell(Messages.get(this, "interesting"));
			((PrisonBossLevel)Dungeon.level).progress();
			BossHealthBar.bleed(true);

		//if tengu has lost a certain amount of hp, jump
		} else if (newBracket != curbracket) {
			//let full attack action complete first
			Actor.add(new Actor() {

				{
					actPriority = VFX_PRIO;
				}

				@Override
				protected boolean act() {
					Actor.remove(this);
					jump();
					return true;
				}
			});
			return;
		}
	}
	
	@Override
	public boolean isAlive() {
		return super.isAlive() || Dungeon.level.mobs.contains(this); //Tengu has special death rules, see prisonbosslevel.progress()
	}

	@Override
	public void die( Object cause ) {
		
		if (Dungeon.hero.subClass == HeroSubClass.NONE) {
			if (Dungeon.hero.heroClass == HeroClass.ADVENTURER){
				Talent.initSubclassTalents(Dungeon.hero);
				GLog.p( Messages.get(TengusMask.class, "adventurer_used"));
			} else {
				Dungeon.level.drop(new TengusMask(), pos).sprite.drop();
			}
		}
		
		GameScene.bossSlain();
		super.die( cause );
		
		Badges.validateBossSlain();
		if (Statistics.qualifiedForBossChallengeBadge){
			Badges.validateBossChallengeCompleted();
		}
		Statistics.bossScores[1] += 2000;
		
		LloydsBeacon beacon = Dungeon.hero.belongings.getItem(LloydsBeacon.class);
		if (beacon != null) {
			beacon.upgrade();
		}
		
		yell( Messages.get(this, "defeated") );
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		return new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos;
	}
	
	private void jump() {
		
		//in case tengu hasn't had a chance to act yet
		if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
			fieldOfView = new boolean[Dungeon.level.length()];
			Dungeon.level.updateFieldOfView( this, fieldOfView );
		}
		
		if (enemy == null) enemy = chooseEnemy();
		if (enemy == null) enemy = Dungeon.hero; //jump away from hero if nothing else is being targeted
		
		int newPos;
		if (Dungeon.level instanceof PrisonBossLevel){
			PrisonBossLevel level = (PrisonBossLevel) Dungeon.level;
			
			//if we're in phase 1, want to warp around within the room
			if (level.state() == PrisonBossLevel.State.FIGHT_START) {
				
				level.cleanTenguCell();

				int tries = 100;
				do {
					newPos = ((PrisonBossLevel)Dungeon.level).randomTenguCellPos();
					tries--;
				} while ( tries > 0 && (level.trueDistance(newPos, enemy.pos) <= 3.5f
						|| level.trueDistance(newPos, Dungeon.hero.pos) <= 3.5f
						|| Actor.findChar(newPos) != null));

				if (tries <= 0) newPos = pos;

				if (level.heroFOV[pos]) CellEmitter.get( pos ).burst( Speck.factory( Speck.WOOL ), 6 );
				
				sprite.move( pos, newPos );
				move( newPos );
				
				if (level.heroFOV[newPos]) CellEmitter.get( newPos ).burst( Speck.factory( Speck.WOOL ), 6 );
				Sample.INSTANCE.play( Assets.Sounds.PUFF );

				float fill = 0.9f - 0.5f*((HP-(HT/2f))/(HT/2f));
				level.placeTrapsInTenguCell(fill);
				
			//otherwise, jump in a larger possible area, as the room is bigger
			} else {

				int tries = 100;
				do {
					newPos = Random.Int(level.length());
					tries--;
				} while (  tries > 0 &&
						(level.solid[newPos] ||
								level.distance(newPos, enemy.pos) < 5 ||
								level.distance(newPos, enemy.pos) > 7 ||
								level.distance(newPos, Dungeon.hero.pos) < 5 ||
								level.distance(newPos, Dungeon.hero.pos) > 7 ||
								level.distance(newPos, pos) < 5 ||
								Actor.findChar(newPos) != null ||
								Dungeon.level.heaps.get(newPos) != null));

				if (tries <= 0) newPos = pos;

				if (level.heroFOV[pos]) CellEmitter.get( pos ).burst( Speck.factory( Speck.WOOL ), 6 );
				
				sprite.move( pos, newPos );
				move( newPos );
				
				if (arenaJumps < 4) arenaJumps++;
				
				if (level.heroFOV[newPos]) CellEmitter.get( newPos ).burst( Speck.factory( Speck.WOOL ), 6 );
				Sample.INSTANCE.play( Assets.Sounds.PUFF );
				
			}
			
		//if we're on another type of level
		} else {
			Level level = Dungeon.level;
			
			newPos = level.randomRespawnCell( this );
			
			if (level.heroFOV[pos]) CellEmitter.get( pos ).burst( Speck.factory( Speck.WOOL ), 6 );
			
			sprite.move( pos, newPos );
			move( newPos );
			
			if (level.heroFOV[newPos]) CellEmitter.get( newPos ).burst( Speck.factory( Speck.WOOL ), 6 );
			Sample.INSTANCE.play( Assets.Sounds.PUFF );
			
		}
		
	}
	
	@Override
	public void notice() {
		super.notice();
		if (!BossHealthBar.isAssigned()) {
			BossHealthBar.assignBoss(this);
			if (HP <= HT/2) BossHealthBar.bleed(true);
			if (HP == HT) {
				yell(Messages.get(this, "notice_gotcha", Dungeon.hero.name()));
				for (Char ch : Actor.chars()){
					if (ch instanceof DriedRose.GhostHero){
						((DriedRose.GhostHero) ch).sayBoss();
					}
				}
			} else {
				yell(Messages.get(this, "notice_have", Dungeon.hero.name()));
			}
		}
	}
	
	{
		immunities.add( Roots.class );
		immunities.add( Blindness.class );
		immunities.add( Dread.class );
		immunities.add( Terror.class );
	}
	
	private static final String LAST_ABILITY     = "last_ability";
	private static final String ABILITIES_USED   = "abilities_used";
	private static final String ARENA_JUMPS      = "arena_jumps";
	private static final String ABILITY_COOLDOWN = "ability_cooldown";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( LAST_ABILITY, lastAbility );
		bundle.put( ABILITIES_USED, abilitiesUsed );
		bundle.put( ARENA_JUMPS, arenaJumps );
		bundle.put( ABILITY_COOLDOWN, abilityCooldown );
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		loading = true;
		super.restoreFromBundle(bundle);
		loading = false;
		lastAbility = bundle.getInt( LAST_ABILITY );
		abilitiesUsed = bundle.getInt( ABILITIES_USED );
		arenaJumps = bundle.getInt( ARENA_JUMPS );
		abilityCooldown = bundle.getInt( ABILITY_COOLDOWN );
		
		BossHealthBar.assignBoss(this);
		if (HP <= HT/2) BossHealthBar.bleed(true);
	}

	//tengu is always hunting, and can use simpler rules because he never moves
	private class Hunting extends Mob.Hunting{
		
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			
			enemySeen = enemyInFOV;
			if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {
				
				if (canUseAbility()){
					return useAbility();
				}

				recentlyAttackedBy.clear();
				target = enemy.pos;
				return doAttack( enemy );
				
			} else {

				//Try to switch targets to another enemy that is closer
				//unless we have already done that and still can't attack them, then move on.
				if (!recursing) {
					Char oldEnemy = enemy;
					enemy = null;
					enemy = chooseEnemy();
					if (enemy != null && enemy != oldEnemy) {
						recursing = true;
						boolean result = act(enemyInFOV, justAlerted);
						recursing = false;
						return result;
					}
				}
				
				//attempt to use an ability, even if enemy can't be decided
				if (canUseAbility()){
					return useAbility();
				}
				
				spend( TICK );
				return true;
				
			}
		}
	}
	
	//*****************************************************************************************
	//***** Tengu abilities. These are expressed in game logic as buffs, blobs, and items *****
	//*****************************************************************************************
	
	//so that mobs can also use this
	private static Char throwingChar;
	
	private int lastAbility = -1;
	private int abilitiesUsed = 0;
	private int arenaJumps = 0;
	
	//starts at 2, so one turn and then first ability
	private int abilityCooldown = 2;
	
	private static final int BOMB_ABILITY    = 0;
	private static final int FIRE_ABILITY    = 1;
	private static final int SHOCKER_ABILITY = 2;
	
	//expects to be called once per turn;
	public boolean canUseAbility(){
		
		if (HP > HT/2) return false;
		
		if (abilitiesUsed >= targetAbilityUses()){
			return false;
		} else {
			
			abilityCooldown--;
			
			if (targetAbilityUses() - abilitiesUsed >= 4 && !Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
				//Very behind in ability uses, use one right away!
				//but not on bosses challenge, we already cast quickly then
				abilityCooldown = 0;
				
			} else if (targetAbilityUses() - abilitiesUsed >= 3){
				//moderately behind in uses, use one every other action.
				if (abilityCooldown == -1 || abilityCooldown > 1) abilityCooldown = 1;
				
			} else {
				//standard delay before ability use, 1-4 turns
				if (abilityCooldown == -1) abilityCooldown = Random.IntRange(1, 4);
			}
			
			if (abilityCooldown == 0){
				return true;
			} else {
				return false;
			}
		}
	}
	
	private int targetAbilityUses(){
		//1 base ability use, plus 2 uses per jump
		int targetAbilityUses = 1 + 2*arenaJumps;
		
		//and ane extra 2 use for jumps 3 and 4
		targetAbilityUses += Math.max(0, arenaJumps-2);
		
		return targetAbilityUses;
	}
	
	public boolean useAbility(){
		boolean abilityUsed = false;
		int abilityToUse = -1;
		
		while (!abilityUsed){
			
			if (abilitiesUsed == 0){
				abilityToUse = BOMB_ABILITY;
			} else if (abilitiesUsed == 1){
				abilityToUse = SHOCKER_ABILITY;
			} else if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
				abilityToUse = Random.Int(2)*2; //0 or 2, can't roll fire ability with challenge
			} else {
				abilityToUse = Random.Int(3);
			}

			//all abilities always target the hero, even if something else is taking Tengu's normal attacks
			
			//If we roll the same ability as last time, 9/10 chance to reroll
			if (abilityToUse != lastAbility || Random.Int(10) == 0){
				switch (abilityToUse){
					case BOMB_ABILITY : default:
						abilityUsed = throwBomb(Tengu.this, Dungeon.hero);
						//if Tengu cannot use his bomb ability first, use fire instead.
						if (abilitiesUsed == 0 && !abilityUsed){
							abilityToUse = FIRE_ABILITY;
							abilityUsed = throwFire(Tengu.this, Dungeon.hero);
						}
						break;
					case FIRE_ABILITY:
						abilityUsed = throwFire(Tengu.this, Dungeon.hero);
						break;
					case SHOCKER_ABILITY:
						abilityUsed = throwShocker(Tengu.this, Dungeon.hero);
						//if Tengu cannot use his shocker ability second, use fire instead.
						if (abilitiesUsed == 1 && !abilityUsed){
							abilityToUse = FIRE_ABILITY;
							abilityUsed = throwFire(Tengu.this, Dungeon.hero);
						}
						break;
				}
				//always use the fire ability with the bosses challenge
				if (abilityUsed && abilityToUse != FIRE_ABILITY && Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
					throwFire(Tengu.this, Dungeon.hero);
				}
			}
			
		}
		
		//spend 1 less turn if seriously behind on ability uses
		if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
			if (targetAbilityUses() - abilitiesUsed >= 4) {
				//spend no time
			} else {
				spend(TICK);
			}
		} else {
			if (targetAbilityUses() - abilitiesUsed >= 4) {
				spend(TICK);
			} else {
				spend(2 * TICK);
			}
		}
		
		lastAbility = abilityToUse;
		abilitiesUsed++;
		return lastAbility == FIRE_ABILITY;
	}
	
	//******************
	//***Bomb Ability***
	//******************
	
	//returns true if bomb was thrown
	public static boolean throwBomb(final Char thrower, final Char target){
		
		int targetCell = -1;
		
		//Targets closest cell which is adjacent to target and has no existing bombs
		for (int i : PathFinder.NEIGHBOURS8){
			int cell = target.pos + i;
			boolean bombHere = false;
			for (BombAbility b : thrower.buffs(BombAbility.class)){
				if (b.bombPos == cell){
					bombHere = true;
				}
			}
			if (!bombHere && !Dungeon.level.solid[cell] &&
					(targetCell == -1 || Dungeon.level.trueDistance(cell, thrower.pos) < Dungeon.level.trueDistance(targetCell, thrower.pos))){
				targetCell = cell;
			}
		}
		
		if (targetCell == -1){
			return false;
		}
		
		final int finalTargetCell = targetCell;
		throwingChar = thrower;
		final BombAbility.BombItem item = new BombAbility.BombItem();
		thrower.sprite.zap(finalTargetCell);
		((MissileSprite) thrower.sprite.parent.recycle(MissileSprite.class)).
				reset(thrower.sprite,
						finalTargetCell,
						item,
						new Callback() {
							@Override
							public void call() {
								item.onThrow(finalTargetCell);
								thrower.next();
							}
						});
		return true;
	}
	
	public static class BombAbility extends Buff {
		
		public int bombPos = -1;
		private int timer = 3;

		private ArrayList<Emitter> smokeEmitters = new ArrayList<>();
		
		@Override
		public boolean act() {

			if (smokeEmitters.isEmpty()){
				fx(true);
			}
			
			PointF p = DungeonTilemap.raisedTileCenterToWorld(bombPos);
			if (timer == 3) {
				FloatingText.show(p.x, p.y, bombPos, "3...", CharSprite.WARNING);
			} else if (timer == 2){
				FloatingText.show(p.x, p.y, bombPos, "2...", CharSprite.WARNING);
			} else if (timer == 1){
				FloatingText.show(p.x, p.y, bombPos, "1...", CharSprite.WARNING);
			} else {
				PathFinder.buildDistanceMap( bombPos, BArray.not( Dungeon.level.solid, null ), 2 );
				for (int cell = 0; cell < PathFinder.distance.length; cell++) {

					if (PathFinder.distance[cell] < Integer.MAX_VALUE) {
						Char ch = Actor.findChar(cell);
						if (ch != null && !(ch instanceof Tengu)) {
							int dmg = Random.NormalIntRange(5 + Dungeon.scalingDepth(), 10 + Dungeon.scalingDepth() * 2);
							dmg -= ch.drRoll();

							if (dmg > 0) {
								ch.damage(dmg, Bomb.class);
							}

							if (ch == Dungeon.hero){
								Statistics.qualifiedForBossChallengeBadge = false;
								Statistics.bossScores[1] -= 100;

								if (!ch.isAlive()) {
									Dungeon.fail(Tengu.class);
								}
							}
						}
					}

				}

				Heap h = Dungeon.level.heaps.get(bombPos);
				if (h != null) {
					for (Item i : h.items.toArray(new Item[0])) {
						if (i instanceof BombItem) {
							h.remove(i);
						}
					}
				}
				Sample.INSTANCE.play(Assets.Sounds.BLAST);
				detach();
				return true;
			}
			
			timer--;
			spend(TICK);
			return true;
		}

		@Override
		public void fx(boolean on) {
			if (on && bombPos != -1){
				PathFinder.buildDistanceMap( bombPos, BArray.not( Dungeon.level.solid, null ), 2 );
				for (int i = 0; i < PathFinder.distance.length; i++) {
					if (PathFinder.distance[i] < Integer.MAX_VALUE) {
						Emitter e = CellEmitter.get(i);
						e.pour( SmokeParticle.FACTORY, 0.25f );
						smokeEmitters.add(e);
					}
				}
			} else if (!on) {
				for (Emitter e : smokeEmitters){
					e.burst(BlastParticle.FACTORY, 2);
				}
			}
		}

		private static final String BOMB_POS = "bomb_pos";
		private static final String TIMER = "timer";
		
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( BOMB_POS, bombPos );
			bundle.put( TIMER, timer );
		}
		
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			bombPos = bundle.getInt( BOMB_POS );
			timer = bundle.getInt( TIMER );
		}
		
		public static class BombItem extends Item {
			
			{
				dropsDownHeap = true;
				unique = true;
				
				image = ItemSpriteSheet.TENGU_BOMB;
			}
			
			@Override
			public boolean doPickUp(Hero hero, int pos) {
				GLog.w( Messages.get(this, "cant_pickup") );
				return false;
			}
			
			@Override
			protected void onThrow(int cell) {
				super.onThrow(cell);
				if (throwingChar != null){
					Buff.append(throwingChar, BombAbility.class).bombPos = cell;
					throwingChar = null;
				} else {
					Buff.append(curUser, BombAbility.class).bombPos = cell;
				}
			}
			
			@Override
			public Emitter emitter() {
				Emitter emitter = new Emitter();
				emitter.pos(7.5f, 3.5f);
				emitter.fillTarget = false;
				emitter.pour(SmokeParticle.SPEW, 0.05f);
				return emitter;
			}
		}
	}
	
	//******************
	//***Fire Ability***
	//******************
	
	public static boolean throwFire(final Char thrower, final Char target){
		
		Ballistica aim = new Ballistica(thrower.pos, target.pos, Ballistica.WONT_STOP);
		
		for (int i = 0; i < PathFinder.CIRCLE8.length; i++){
			if (aim.sourcePos+PathFinder.CIRCLE8[i] == aim.path.get(1)){
				thrower.sprite.zap(target.pos);
				Buff.append(thrower, Tengu.FireAbility.class).direction = i;
				
				thrower.sprite.emitter().start(Speck.factory(Speck.STEAM), .03f, 10);
				return true;
			}
		}
		
		return false;
	}
	
	public static class FireAbility extends Buff {
		
		public int direction;
		private int[] curCells;
		
		HashSet<Integer> toCells = new HashSet<>();
		
		@Override
		public boolean act() {

			toCells.clear();

			if (curCells == null){
				curCells = new int[1];
				curCells[0] = target.pos;
				spreadFromCell( curCells[0] );

			} else {
				for (Integer c : curCells) {
					if (FireBlob.volumeAt(c, FireBlob.class) > 0) spreadFromCell(c);
				}
			}
			
			for (Integer c : curCells){
				toCells.remove(c);
			}
			
			if (toCells.isEmpty()){
				detach();
			} else {
				curCells = new int[toCells.size()];
				int i = 0;
				for (Integer c : toCells){
					GameScene.add(Blob.seed(c, 2, FireBlob.class));
					curCells[i] = c;
					i++;
				}
			}
			
			spend(TICK);
			return true;
		}
		
		private void spreadFromCell( int cell ){
			if (!Dungeon.level.solid[cell + PathFinder.CIRCLE8[left(direction)]]){
				toCells.add(cell + PathFinder.CIRCLE8[left(direction)]);
			}
			if (!Dungeon.level.solid[cell + PathFinder.CIRCLE8[direction]]){
				toCells.add(cell + PathFinder.CIRCLE8[direction]);
			}
			if (!Dungeon.level.solid[cell + PathFinder.CIRCLE8[right(direction)]]){
				toCells.add(cell + PathFinder.CIRCLE8[right(direction)]);
			}
		}
		
		private int left(int direction){
			return direction == 0 ? 7 : direction-1;
		}
		
		private int right(int direction){
			return direction == 7 ? 0 : direction+1;
		}
		
		private static final String DIRECTION = "direction";
		private static final String CUR_CELLS = "cur_cells";
		
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( DIRECTION, direction );
			if (curCells != null) bundle.put( CUR_CELLS, curCells );
		}
		
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			direction = bundle.getInt( DIRECTION );
			if (bundle.contains( CUR_CELLS )) curCells = bundle.getIntArray( CUR_CELLS );
		}
		
		public static class FireBlob extends Blob {
			
			{
				actPriority = BUFF_PRIO - 1;
				alwaysVisible = true;
			}
			
			@Override
			protected void evolve() {
				
				boolean observe = false;
				boolean burned = false;
				
				int cell;
				for (int i = area.left; i < area.right; i++){
					for (int j = area.top; j < area.bottom; j++){
						cell = i + j* Dungeon.level.width();
						off[cell] = (int)GameMath.gate(0, cur[cell] - 1, 1);
						
						if (off[cell] > 0) {
							volume += off[cell];
						}
						
						if (cur[cell] > 0 && off[cell] == 0){

							//similar to fire.burn(), but Tengu is immune, and hero loses score
							Char ch = Actor.findChar( cell );
							if (ch != null && !ch.isImmune(Fire.class) && !(ch instanceof Tengu)) {
								Buff.affect( ch, Burning.class ).reignite( ch );
							}
							if (ch == Dungeon.hero){
								Statistics.qualifiedForBossChallengeBadge = false;
								Statistics.bossScores[1] -= 100;
							}

							Heap heap = Dungeon.level.heaps.get( cell );
							if (heap != null) {
								heap.burn();
							}

							Plant plant = Dungeon.level.plants.get( cell );
							if (plant != null){
								plant.wither();
							}
							
							if (Dungeon.level.flamable[cell]){
								Dungeon.level.destroy( cell );
								
								observe = true;
								GameScene.updateMap( cell );
							}
							
							burned = true;
							CellEmitter.get(cell).start(FlameParticle.FACTORY, 0.03f, 10);
						}
					}
				}
				
				if (observe) {
					Dungeon.observe();
				}
				
				if (burned){
					Sample.INSTANCE.play(Assets.Sounds.BURNING);
				}
			}
			
			@Override
			public void use(BlobEmitter emitter) {
				super.use(emitter);
				
				emitter.pour( Speck.factory( Speck.STEAM ), 0.2f );
			}
			
			@Override
			public String tileDesc() {
				return Messages.get(this, "desc");
			}
		}
	}
	
	//*********************
	//***Shocker Ability***
	//*********************
	
	//returns true if shocker was thrown
	public static boolean throwShocker(final Char thrower, final Char target){
		
		int targetCell = -1;
		
		//Targets closest cell which is adjacent to target, and not adjacent to thrower or another shocker
		for (int i : PathFinder.NEIGHBOURS8){
			int cell = target.pos + i;
			if (Dungeon.level.distance(cell, thrower.pos) >= 2 && !Dungeon.level.solid[cell]){
				boolean validTarget = true;
				for (ShockerAbility s : thrower.buffs(ShockerAbility.class)){
					if (Dungeon.level.distance(cell, s.shockerPos) < 2){
						validTarget = false;
						break;
					}
				}
				if (validTarget && Dungeon.level.trueDistance(cell, thrower.pos) < Dungeon.level.trueDistance(targetCell, thrower.pos)){
					targetCell = cell;
				}
			}
		}
		
		if (targetCell == -1){
			return false;
		}
		
		final int finalTargetCell = targetCell;
		throwingChar = thrower;
		final ShockerAbility.ShockerItem item = new ShockerAbility.ShockerItem();
		thrower.sprite.zap(finalTargetCell);
		((MissileSprite) thrower.sprite.parent.recycle(MissileSprite.class)).
				reset(thrower.sprite,
						finalTargetCell,
						item,
						new Callback() {
							@Override
							public void call() {
								item.onThrow(finalTargetCell);
								thrower.next();
							}
						});
		return true;
	}
	
	public static class ShockerAbility extends Buff {
	
		public int shockerPos;
		private Boolean shockingOrdinals = null;
		
		@Override
		public boolean act() {
			
			if (shockingOrdinals == null){
				shockingOrdinals = Random.Int(2) == 1;
				
				spreadblob();
			} else if (shockingOrdinals){
				
				target.sprite.parent.add(new Lightning(shockerPos - 1 - Dungeon.level.width(), shockerPos + 1 + Dungeon.level.width(), null));
				target.sprite.parent.add(new Lightning(shockerPos - 1 + Dungeon.level.width(), shockerPos + 1 - Dungeon.level.width(), null));
				
				if (Dungeon.level.distance(Dungeon.hero.pos, shockerPos) <= 1){
					Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
				}
				
				shockingOrdinals = false;
				spreadblob();
			} else {
				
				target.sprite.parent.add(new Lightning(shockerPos - Dungeon.level.width(), shockerPos + Dungeon.level.width(), null));
				target.sprite.parent.add(new Lightning(shockerPos - 1, shockerPos + 1, null));
				
				if (Dungeon.level.distance(Dungeon.hero.pos, shockerPos) <= 1){
					Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
				}
				
				shockingOrdinals = true;
				spreadblob();
			}
			
			spend(TICK);
			return true;
		}
		
		private void spreadblob(){
			GameScene.add(Blob.seed(shockerPos, 1, ShockerBlob.class));
			for (int i = shockingOrdinals ? 0 : 1; i < PathFinder.CIRCLE8.length; i += 2){
				if (!Dungeon.level.solid[shockerPos+PathFinder.CIRCLE8[i]]) {
					GameScene.add(Blob.seed(shockerPos + PathFinder.CIRCLE8[i], 2, ShockerBlob.class));
				}
			}
		}
		
		private static final String SHOCKER_POS = "shocker_pos";
		private static final String SHOCKING_ORDINALS = "shocking_ordinals";
		
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( SHOCKER_POS, shockerPos );
			if (shockingOrdinals != null) bundle.put( SHOCKING_ORDINALS, shockingOrdinals );
		}
		
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			shockerPos = bundle.getInt( SHOCKER_POS );
			if (bundle.contains(SHOCKING_ORDINALS)) shockingOrdinals = bundle.getBoolean( SHOCKING_ORDINALS );
		}
		
		public static class ShockerBlob extends Blob {
			
			{
				actPriority = BUFF_PRIO - 1;
				alwaysVisible = true;
			}
			
			@Override
			protected void evolve() {

				boolean shocked = false;
				
				int cell;
				for (int i = area.left; i < area.right; i++){
					for (int j = area.top; j < area.bottom; j++){
						cell = i + j* Dungeon.level.width();
						off[cell] = cur[cell] > 0 ? cur[cell] - 1 : 0;
						
						if (off[cell] > 0) {
							volume += off[cell];
						}
						
						if (cur[cell] > 0 && off[cell] == 0){

							shocked = true;
							
							Char ch = Actor.findChar(cell);
							if (ch != null && !(ch instanceof Tengu)){
								ch.damage(2 + Dungeon.scalingDepth(), new Electricity());
								
								if (ch == Dungeon.hero){
									Statistics.qualifiedForBossChallengeBadge = false;
									Statistics.bossScores[1] -= 100;
									if (!ch.isAlive()) {
										Dungeon.fail(Tengu.class);
										GLog.n(Messages.get(Electricity.class, "ondeath"));
									}
								}
							}
							
						}
					}
				}

				if (shocked) Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
				
			}
			
			@Override
			public void use(BlobEmitter emitter) {
				super.use(emitter);
				
				emitter.pour( SparkParticle.STATIC, 0.10f );
			}
			
			@Override
			public String tileDesc() {
				return Messages.get(this, "desc");
			}
		}
		
		public static class ShockerItem extends Item {
			
			{
				dropsDownHeap = true;
				unique = true;
				
				image = ItemSpriteSheet.TENGU_SHOCKER;
			}
			
			@Override
			public boolean doPickUp(Hero hero, int pos) {
				GLog.w( Messages.get(this, "cant_pickup") );
				return false;
			}
			
			@Override
			protected void onThrow(int cell) {
				super.onThrow(cell);
				if (throwingChar != null){
					Buff.append(throwingChar, ShockerAbility.class).shockerPos = cell;
					throwingChar = null;
				} else {
					Buff.append(curUser, ShockerAbility.class).shockerPos = cell;
				}
			}
			
			@Override
			public Emitter emitter() {
				Emitter emitter = new Emitter();
				emitter.pos(5, 5);
				emitter.fillTarget = false;
				emitter.pour(SparkParticle.FACTORY, 0.1f);
				return emitter;
			}
		}
		
	}
}
