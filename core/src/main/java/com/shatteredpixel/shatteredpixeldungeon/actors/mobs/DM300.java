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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.WallOfLight;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DM300Sprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

import java.util.ArrayList;

public class DM300 extends Mob {

	{
		spriteClass = DM300Sprite.class;

		HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 400 : 300;
		EXP = 30;
		defenseSkill = 15;

		properties.add(Property.BOSS);
		properties.add(Property.INORGANIC);
		properties.add(Property.LARGE);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 15, 25 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 20;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 10);
	}

	public int pylonsActivated = 0;
	public boolean supercharged = false;
	public boolean chargeAnnounced = false;

	private final int MIN_COOLDOWN = 5;
	private final int MAX_COOLDOWN = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 7 : 9;

	private int turnsSinceLastAbility = -1;
	private int abilityCooldown = Random.NormalIntRange(MIN_COOLDOWN, MAX_COOLDOWN);

	private int lastAbility = 0;
	private static final int NONE = 0;
	private static final int GAS = 1;
	private static final int ROCKS = 2;

	private static final String PYLONS_ACTIVATED = "pylons_activated";
	private static final String SUPERCHARGED = "supercharged";
	private static final String CHARGE_ANNOUNCED = "charge_announced";

	private static final String TURNS_SINCE_LAST_ABILITY = "turns_since_last_ability";
	private static final String ABILITY_COOLDOWN = "ability_cooldown";

	private static final String LAST_ABILITY = "last_ability";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(PYLONS_ACTIVATED, pylonsActivated);
		bundle.put(SUPERCHARGED, supercharged);
		bundle.put(CHARGE_ANNOUNCED, chargeAnnounced);
		bundle.put(TURNS_SINCE_LAST_ABILITY, turnsSinceLastAbility);
		bundle.put(ABILITY_COOLDOWN, abilityCooldown);
		bundle.put(LAST_ABILITY, lastAbility);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		pylonsActivated = bundle.getInt(PYLONS_ACTIVATED);
		supercharged = bundle.getBoolean(SUPERCHARGED);
		chargeAnnounced = bundle.getBoolean(CHARGE_ANNOUNCED);
		turnsSinceLastAbility = bundle.getInt(TURNS_SINCE_LAST_ABILITY);
		abilityCooldown = bundle.getInt(ABILITY_COOLDOWN);
		lastAbility = bundle.getInt(LAST_ABILITY);

		if (turnsSinceLastAbility != -1){
			BossHealthBar.assignBoss(this);
			if (!supercharged && pylonsActivated == totalPylonsToActivate()) BossHealthBar.bleed(true);
		}
	}

	@Override
	protected boolean act() {

		if (paralysed > 0){
			return super.act();
		}

		//ability logic only triggers if DM is not supercharged
		if (!supercharged){
			if (turnsSinceLastAbility >= 0) turnsSinceLastAbility++;

			//in case DM-300 hasn't been able to act yet
			if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
				fieldOfView = new boolean[Dungeon.level.length()];
				Dungeon.level.updateFieldOfView( this, fieldOfView );
			}

			//determine if DM can reach its enemy
			boolean canReach;
			if (enemy == null || !enemy.isAlive()){
				if (Dungeon.level.adjacent(pos, Dungeon.hero.pos)){
					canReach = true;
				} else {
					canReach = (Dungeon.findStep(this, Dungeon.hero.pos, Dungeon.level.openSpace, fieldOfView, Dungeon.PATHBLOCK_NORMAL) != -1);
				}
			} else {
				if (Dungeon.level.adjacent(pos, enemy.pos)){
					canReach = true;
				} else {
					canReach = (Dungeon.findStep(this, enemy.pos, Dungeon.level.openSpace, fieldOfView, Dungeon.PATHBLOCK_NORMAL) != -1);
				}
			}

			if (state != HUNTING){
				if (Dungeon.hero.invisible <= 0 && canReach){
					beckon(Dungeon.hero.pos);
				}
			} else {

				if ((enemy == null || !enemy.isAlive()) && Dungeon.hero.invisible <= 0) {
					enemy = Dungeon.hero;
				}

				//more aggressive ability usage when DM can't reach its target
				if (enemy != null && enemy.isAlive() && !canReach){

					//try to fire gas at an enemy we can't reach
					if (turnsSinceLastAbility >= MIN_COOLDOWN){
						//use a coneAOE to try and account for trickshotting angles
						ConeAOE aim = new ConeAOE(new Ballistica(pos, enemy.pos, Ballistica.WONT_STOP), Float.POSITIVE_INFINITY, 30, Ballistica.STOP_SOLID);
						if (aim.cells.contains(enemy.pos) && !Char.hasProp(enemy, Property.INORGANIC)) {
							lastAbility = GAS;
							turnsSinceLastAbility = 0;

							if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
								sprite.zap(enemy.pos);
								return false;
							} else {
								ventGas(enemy);
								Sample.INSTANCE.play(Assets.Sounds.GAS);
								return true;
							}
						//if we can't gas, or if target is inorganic then drop rocks
						//unless enemy is already stunned, we don't want to stunlock them
						} else if (enemy.paralysed <= 0) {
							lastAbility = ROCKS;
							turnsSinceLastAbility = 0;
							if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
								((DM300Sprite)sprite).slam(enemy.pos);
								return false;
							} else {
								dropRocks(enemy);
								Sample.INSTANCE.play(Assets.Sounds.ROCKS);
								return true;
							}
						}

					}

				} else if (enemy != null && enemy.isAlive() && fieldOfView[enemy.pos]) {
					if (turnsSinceLastAbility > abilityCooldown) {

						if (lastAbility == NONE) {
							//50/50 either ability
							lastAbility = Random.Int(2) == 0 ? GAS : ROCKS;
						} else if (lastAbility == GAS) {
							//more likely to use rocks
							lastAbility = Random.Int(4) == 0 ? GAS : ROCKS;
						} else {
							//more likely to use gas
							lastAbility = Random.Int(4) != 0 ? GAS : ROCKS;
						}

						if (Char.hasProp(enemy, Property.INORGANIC)){
							lastAbility = ROCKS;
						}

						//doesn't spend a turn if enemy is at a distance
						if (Dungeon.level.adjacent(pos, enemy.pos)){
							spend(TICK);
						}

						turnsSinceLastAbility = 0;
						abilityCooldown = Random.NormalIntRange(MIN_COOLDOWN, MAX_COOLDOWN);

						if (lastAbility == GAS) {
							if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
								sprite.zap(enemy.pos);
								return false;
							} else {
								ventGas(enemy);
								Sample.INSTANCE.play(Assets.Sounds.GAS);
								return true;
							}
						} else {
							if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
								((DM300Sprite)sprite).slam(enemy.pos);
								return false;
							} else {
								dropRocks(enemy);
								Sample.INSTANCE.play(Assets.Sounds.ROCKS);
								return true;
							}
						}
					}
				}
			}
		} else {

			if (!chargeAnnounced){
				yell(Messages.get(this, "supercharged"));
				chargeAnnounced = true;
			}

			if (Dungeon.hero.invisible <= 0){
				beckon(Dungeon.hero.pos);
				state = HUNTING;
				enemy = Dungeon.hero;
			}

		}

		return super.act();
	}

	@Override
	public boolean attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti) {
		if (enemy == Dungeon.hero && supercharged){
			Statistics.qualifiedForBossChallengeBadge = false;
		}
		return super.attack(enemy, dmgMulti, dmgBonus, accMulti);
	}

	@Override
	protected Char chooseEnemy() {
		Char enemy = super.chooseEnemy();
		if (supercharged && enemy == null){
			enemy = Dungeon.hero;
		}
		return enemy;
	}

	@Override
	public void move(int step, boolean travelling) {
		super.move(step, travelling);

		if (travelling) PixelScene.shake( supercharged ? 3 : 1, 0.25f );

		if (!flying && Dungeon.level.map[pos] == Terrain.INACTIVE_TRAP && state == HUNTING) {

			//don't gain energy from cells that are energized
			if (CavesBossLevel.PylonEnergy.volumeAt(pos, CavesBossLevel.PylonEnergy.class) > 0){
				return;
			}

			if (Dungeon.level.heroFOV[pos]) {
				if (buff(Barrier.class) == null) {
					GLog.w(Messages.get(this, "shield"));
				}
				Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
				sprite.emitter().start(SparkParticle.STATIC, 0.05f, 20);
				sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(30 + (HT - HP)/10), FloatingText.SHIELDING);
			}

			Buff.affect(this, Barrier.class).setShield( 30 + (HT - HP)/10);

		}
	}

	@Override
	public float speed() {
		return super.speed() * (supercharged ? 2 : 1);
	}

	@Override
	public void notice() {
		super.notice();
		if (!BossHealthBar.isAssigned()) {
			BossHealthBar.assignBoss(this);
			turnsSinceLastAbility = 0;
			yell(Messages.get(this, "notice"));
			for (Char ch : Actor.chars()){
				if (ch instanceof DriedRose.GhostHero){
					((DriedRose.GhostHero) ch).sayBoss();
				}
			}
		}
	}

	public void onZapComplete(){
		ventGas(enemy);
		next();
	}

	public void ventGas( Char target ){
		Dungeon.hero.interrupt();

		int gasVented = 0;

		Ballistica trajectory = new Ballistica(pos, target.pos, Ballistica.STOP_TARGET);

		int gasMulti = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 2 : 1;

		for (int i : trajectory.subPath(0, trajectory.dist)){
			GameScene.add(Blob.seed(i, 20*gasMulti, ToxicGas.class));
			gasVented += 20*gasMulti;
		}

		GameScene.add(Blob.seed(trajectory.collisionPos, 100*gasMulti, ToxicGas.class));

		if (gasVented < 250*gasMulti){
			int toVentAround = (int)Math.ceil(((250*gasMulti) - gasVented)/8f);
			for (int i : PathFinder.NEIGHBOURS8){
				GameScene.add(Blob.seed(pos+i, toVentAround, ToxicGas.class));
			}

		}

	}

	public void onSlamComplete(){
		dropRocks(enemy);
		next();
	}

	public void dropRocks( Char target ) {

		Dungeon.hero.interrupt();
		final int rockCenter;

		//knock back 2 tiles if adjacent
		if (Dungeon.level.adjacent(pos, target.pos)){
			int oppositeAdjacent = target.pos + (target.pos - pos);
			Ballistica trajectory = new Ballistica(target.pos, oppositeAdjacent, Ballistica.MAGIC_BOLT);
			WandOfBlastWave.throwChar(target, trajectory, 2, false, false, this);
			if (target == Dungeon.hero){
				Dungeon.hero.interrupt();
			}
			rockCenter = trajectory.path.get(Math.min(trajectory.dist, 2));

		//knock back 1 tile if there's 1 tile of space
		} else if (fieldOfView[target.pos] && Dungeon.level.distance(pos, target.pos) == 2) {
			int oppositeAdjacent = target.pos + (target.pos - pos);
			Ballistica trajectory = new Ballistica(target.pos, oppositeAdjacent, Ballistica.MAGIC_BOLT);
			WandOfBlastWave.throwChar(target, trajectory, 1, false, false, this);
			if (target == Dungeon.hero){
				Dungeon.hero.interrupt();
			}
			rockCenter = trajectory.path.get(Math.min(trajectory.dist, 1));

		//otherwise no knockback
		} else {
			rockCenter = target.pos;
		}

		int safeCell;
		do {
			safeCell = rockCenter + PathFinder.NEIGHBOURS8[Random.Int(8)];
		} while (safeCell == pos
				|| (Dungeon.level.solid[safeCell] && Random.Int(2) == 0)
				|| (Blob.volumeAt(safeCell, CavesBossLevel.PylonEnergy.class) > 0 && Random.Int(2) == 0));

		ArrayList<Integer> rockCells = new ArrayList<>();

		int start = rockCenter - Dungeon.level.width() * 3 - 3;
		int pos;
		for (int y = 0; y < 7; y++) {
			pos = start + Dungeon.level.width() * y;
			for (int x = 0; x < 7; x++) {
				if (!Dungeon.level.insideMap(pos)) {
					pos++;
					continue;
				}
				//add rock cell to pos, if it is not solid, and isn't the safecell
				if (!Dungeon.level.solid[pos] && pos != safeCell && Random.Int(Dungeon.level.distance(rockCenter, pos)) == 0) {
					rockCells.add(pos);
				}
				pos++;
			}
		}
		for (int i : rockCells){
			sprite.parent.add(new TargetedCell(i, 0xFF0000));
		}
		//don't want to overly punish players with slow move or attack speed
		Buff.append(this, FallingRockBuff.class, GameMath.gate(TICK, (int)Math.ceil(target.cooldown()), 3*TICK)).setRockPositions(rockCells);

	}

	private boolean invulnWarned = false;

	@Override
	public void damage(int dmg, Object src) {
		if (!BossHealthBar.isAssigned()){
			notice();
		}

		int preHP = HP;
		super.damage(dmg, src);
		if (isInvulnerable(src.getClass())){
			return;
		}

		int dmgTaken = preHP - HP;
		if (dmgTaken > 0) {
			LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
			if (lock != null && !isImmune(src.getClass()) && !isInvulnerable(src.getClass())){
				if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES))   lock.addTime(dmgTaken/2f);
				else                                                    lock.addTime(dmgTaken);
			}
		}

		int threshold;
		if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
			threshold = HT / 4 * (3 - pylonsActivated);
		} else {
			threshold = HT / 3 * (2 - pylonsActivated);
		}

		if (HP <= threshold && threshold > 0){
			HP = threshold;
			supercharge();
		}

	}

	public int totalPylonsToActivate(){
		return Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 3 : 2;
	}

	@Override
	public boolean isInvulnerable(Class effect) {
		if (supercharged && !invulnWarned){
			invulnWarned = true;
			GLog.w(Messages.get(this, "charging_hint"));
		}
		return supercharged || super.isInvulnerable(effect);
	}

	public void supercharge(){
		supercharged = true;
		((CavesBossLevel)Dungeon.level).activatePylon();
		pylonsActivated++;

		spend(Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 2f : 3f);
		yell(Messages.get(this, "charging"));
		sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "invulnerable"));
		((DM300Sprite)sprite).updateChargeState(true);
		((DM300Sprite)sprite).charge();
		chargeAnnounced = false;

	}

	public boolean isSupercharged(){
		return supercharged;
	}

	public void loseSupercharge(){
		supercharged = false;
		((DM300Sprite)sprite).updateChargeState(false);

		//adjust turns since last ability to prevent DM immediately using an ability when charge ends
		turnsSinceLastAbility = Math.max(turnsSinceLastAbility, MIN_COOLDOWN-3);

		if (pylonsActivated < totalPylonsToActivate()){
			yell(Messages.get(this, "charge_lost"));
		} else {
			yell(Messages.get(this, "pylons_destroyed"));
			BossHealthBar.bleed(true);
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					Music.INSTANCE.fadeOut(0.5f, new Callback() {
						@Override
						public void call() {
							Music.INSTANCE.play(Assets.Music.CAVES_BOSS_FINALE, true);
						}
					});
				}
			});
		}
	}

	@Override
	public boolean isAlive() {
		return super.isAlive() || pylonsActivated < totalPylonsToActivate();
	}

	@Override
	public void die( Object cause ) {

		super.die( cause );

		GameScene.bossSlain();
		Dungeon.level.unseal();

		//60% chance of 2 shards, 30% chance of 3, 10% chance for 4. Average of 2.5
		int shards = Random.chances(new float[]{0, 0, 6, 3, 1});
		for (int i = 0; i < shards; i++){
			int ofs;
			do {
				ofs = PathFinder.NEIGHBOURS8[Random.Int(8)];
			} while (!Dungeon.level.passable[pos + ofs]);
			Dungeon.level.drop( new MetalShard(), pos + ofs ).sprite.drop( pos );
		}

		Badges.validateBossSlain();
		if (Statistics.qualifiedForBossChallengeBadge){
			Badges.validateBossChallengeCompleted();
		}
		Statistics.bossScores[2] += 3000;

		LloydsBeacon beacon = Dungeon.hero.belongings.getItem(LloydsBeacon.class);
		if (beacon != null) {
			beacon.upgrade();
		}

		yell( Messages.get(this, "defeated") );
	}

	@Override
	protected boolean getCloser(int target) {
		if (super.getCloser(target)){
			return true;
		} else {

			if (!supercharged || state != HUNTING || rooted || target == pos || Dungeon.level.adjacent(pos, target)) {
				return false;
			}

			int bestpos = pos;
			for (int i : PathFinder.NEIGHBOURS8){
				if (Actor.findChar(pos+i) == null &&
						Dungeon.level.trueDistance(bestpos, target) > Dungeon.level.trueDistance(pos+i, target)){
					bestpos = pos+i;
				}
			}
			if (bestpos != pos){
				Sample.INSTANCE.play( Assets.Sounds.ROCKS );

				Rect gate = CavesBossLevel.gate;
				for (int i : PathFinder.NEIGHBOURS9){
					if (Dungeon.level.map[pos+i] == Terrain.WALL || Dungeon.level.map[pos+i] == Terrain.WALL_DECO){
						Point p = Dungeon.level.cellToPoint(pos+i);
						if (p.y < gate.bottom && p.x >= gate.left-2 && p.x < gate.right+2){
							continue; //don't break the gate or walls around the gate
						}
						if (!CavesBossLevel.diggableArea.inside(p)){
							continue; //Don't break any walls out of the boss arena
						}
						Level.set(pos+i, Terrain.EMPTY_DECO);
						GameScene.updateMap(pos+i);
					}
					if (Dungeon.level.blobs.get(WallOfLight.LightWall.class) != null){
						Dungeon.level.blobs.get(WallOfLight.LightWall.class).clear(pos+i);
					}
				}
				Dungeon.level.cleanWalls();
				Dungeon.observe();
				spend(Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 2f : 3f);

				bestpos = pos;
				for (int i : PathFinder.NEIGHBOURS8){
					if (Actor.findChar(pos+i) == null && Dungeon.level.openSpace[pos+i] &&
							Dungeon.level.trueDistance(bestpos, target) > Dungeon.level.trueDistance(pos+i, target)){
						bestpos = pos+i;
					}
				}

				if (bestpos != pos) {
					move(bestpos);
				}
				PixelScene.shake( 5, 1f );

				return true;
			}

			return false;
		}
	}

	@Override
	public String description() {
		String desc = super.description();
		if (supercharged) {
			desc += "\n\n" + Messages.get(this, "desc_supercharged");
		}
		return desc;
	}

	{
		immunities.add(Sleep.class);

		resistances.add(Terror.class);
		resistances.add(Charm.class);
		resistances.add(Vertigo.class);
		resistances.add(Cripple.class);
		resistances.add(Chill.class);
		resistances.add(Frost.class);
		resistances.add(Roots.class);
		resistances.add(Slow.class);
	}

	public static class FallingRockBuff extends DelayedRockFall {

		@Override
		public void affectChar(Char ch) {
			if (!(ch instanceof DM300)){
				Buff.prolong(ch, Paralysis.class, Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 5 : 3);
				if (ch == Dungeon.hero) {
					Statistics.bossScores[2] -= 100;
				}
			}
		}

	}
}
