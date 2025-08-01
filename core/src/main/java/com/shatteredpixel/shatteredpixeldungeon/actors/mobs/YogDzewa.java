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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.YogWall;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LarvaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.YogSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class YogDzewa extends Mob {

	{
		spriteClass = YogSprite.class;

		HP = HT = 1000;

		EXP = 50;

		//so that allies can attack it. States are never actually used.
		state = HUNTING;

		viewDistance = 12;

		properties.add(Property.BOSS);
		properties.add(Property.IMMOVABLE);
		properties.add(Property.DEMONIC);
		properties.add(Property.STATIC);
	}

	private int phase = 0;

	private float abilityCooldown;
	private static final int MIN_ABILITY_CD = 10;
	private static final int MAX_ABILITY_CD = 15;

	private float summonCooldown;
	private static final int MIN_SUMMON_CD = 10;
	private static final int MAX_SUMMON_CD = 15;
	private boolean needCrossBeam;

	private static Class getPairedFist(Class fist){
		if (fist == YogFist.BurningFist.class) return YogFist.SoiledFist.class;
		if (fist == YogFist.SoiledFist.class) return YogFist.BurningFist.class;
		if (fist == YogFist.RottingFist.class) return YogFist.RustedFist.class;
		if (fist == YogFist.RustedFist.class) return YogFist.RottingFist.class;
		if (fist == YogFist.BrightFist.class) return YogFist.DarkFist.class;
		if (fist == YogFist.DarkFist.class) return YogFist.BrightFist.class;
		return null;
	}

	private ArrayList<Class> fistSummons = new ArrayList<>();
	private ArrayList<Class> challengeSummons = new ArrayList<>();
	{
		//offset seed slightly to avoid output patterns
		Random.pushGenerator(Dungeon.seedCurDepth()+1);
			fistSummons.add(Random.Int(2) == 0 ? YogFist.BurningFist.class : YogFist.SoiledFist.class);
			fistSummons.add(Random.Int(2) == 0 ? YogFist.RottingFist.class : YogFist.RustedFist.class);
			fistSummons.add(Random.Int(2) == 0 ? YogFist.BrightFist.class : YogFist.DarkFist.class);
			Random.shuffle(fistSummons);
			//randomly place challenge summons so that two fists of a pair can never spawn together
			if (Random.Int(2) == 0){
				challengeSummons.add(getPairedFist(fistSummons.get(1)));
				challengeSummons.add(getPairedFist(fistSummons.get(2)));
				challengeSummons.add(getPairedFist(fistSummons.get(0)));
			} else {
				challengeSummons.add(getPairedFist(fistSummons.get(2)));
				challengeSummons.add(getPairedFist(fistSummons.get(0)));
				challengeSummons.add(getPairedFist(fistSummons.get(1)));
			}
		Random.popGenerator();
	}

	private ArrayList<Class> regularSummons = new ArrayList<>();
	{
		if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
			for (int i = 0; i < 6; i++){
				if (i >= 4){
					regularSummons.add(YogRipper.class);
				} else if (i >= Statistics.spawnersAlive){
					regularSummons.add(Larva.class);
				} else {
					regularSummons.add( i % 2 == 0 ? YogEye.class : YogScorpio.class);
				}
			}
		} else {
			for (int i = 0; i < 4; i++){
				if (i >= Statistics.spawnersAlive){
					regularSummons.add(Larva.class);
				} else {
					regularSummons.add(YogRipper.class);
				}
			}
		}
		Random.shuffle(regularSummons);
	}

	private ArrayList<Integer> targetedCells = new ArrayList<>();

	@Override
	public int attackSkill(Char target) {
		return INFINITE_ACCURACY;
	}

	@Override
	protected boolean act() {
		//char logic
		if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
			fieldOfView = new boolean[Dungeon.level.length()];
		}
		Dungeon.level.updateFieldOfView( this, fieldOfView );

		throwItems();

		sprite.hideAlert();
		sprite.hideLost();

		//mob logic
		enemy = chooseEnemy();

		enemySeen = enemy != null && enemy.isAlive() && fieldOfView[enemy.pos] && enemy.invisible <= 0;
		//end of char/mob logic

		if (phase == 0){
			if (Dungeon.hero.viewDistance >= Dungeon.level.distance(pos, Dungeon.hero.pos)) {
				Dungeon.observe();
			}
			if (Dungeon.level.heroFOV[pos]) {
				notice();
			}
		}

		if (phase == 0){
			spend(TICK);
			return true;
		} else {

			boolean terrainAffected = false;
			HashSet<Char> affected = new HashSet<>();
			//delay fire on a rooted hero
			if (!Dungeon.hero.rooted) {
				if (needCrossBeam){
					Ballistica[] fenceBeams = new Ballistica[8];
					for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++){
						fenceBeams[i] = new Ballistica(pos, pos + PathFinder.NEIGHBOURS8[i], Ballistica.STOP_SOLID);
						for (int k : fenceBeams[i].path){
							if (k == pos)
								continue;
							GameScene.add(Blob.seed(k, 8, YogWall.class));
							Char ch = Actor.findChar(k);
							if (ch != null && ch.alignment == Alignment.ALLY){
								int dmg = Random.NormalIntRange(50, 170);
								ch.damage(dmg, new Eye.DeathGaze());
							}
						}
						needCrossBeam = false;
					}
				} else {
					for (int i : targetedCells) {
						Ballistica b = new Ballistica(pos, i, Ballistica.WONT_STOP);
						//shoot beams
						sprite.parent.add(new Beam.DeathRay(sprite.center(), DungeonTilemap.raisedTileCenterToWorld(b.collisionPos)));
						for (int p : b.path) {
							Char ch = Actor.findChar(p);
							if (ch != null && (ch.alignment != alignment || ch instanceof Bee)) {
								affected.add(ch);
							}
							if (Dungeon.level.flamable[p]) {
								Dungeon.level.destroy(p);
								GameScene.updateMap(p);
								terrainAffected = true;
							}
						}
					}
					if (terrainAffected) {
						Dungeon.observe();
					}
					Invisibility.dispel(this);
					for (Char ch : affected) {

						if (ch == Dungeon.hero) {
							Statistics.bossScores[4] -= 500;
						}

						if (hit(this, ch, true)) {
							if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
								ch.damage(Random.NormalIntRange(30, 50), new Eye.DeathGaze());
							} else {
								ch.damage(Random.NormalIntRange(20, 30), new Eye.DeathGaze());
							}
							if (Dungeon.level.heroFOV[pos]) {
								ch.sprite.flash();
								CellEmitter.center(pos).burst(PurpleParticle.BURST, Random.IntRange(1, 2));
							}
							if (!ch.isAlive() && ch == Dungeon.hero) {
								Badges.validateDeathFromEnemyMagic();
								Dungeon.fail(this);
								GLog.n(Messages.get(Char.class, "kill", name()));
							}
						} else {
							ch.sprite.showStatus(CharSprite.NEUTRAL, ch.defenseVerb());
						}
					}
					targetedCells.clear();
				}
			}

			if (abilityCooldown <= 0){

				HashSet<Integer> affectedCells = new HashSet<>();

				if (needCrossBeam){
					targetedCells.clear();
					Ballistica[] fenceBeams = new Ballistica[8];
					for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++){
						fenceBeams[i] = new Ballistica(pos, pos + PathFinder.NEIGHBOURS8[i], Ballistica.STOP_SOLID);
						targetedCells.add(pos+PathFinder.NEIGHBOURS8[i]);
						affectedCells.addAll(fenceBeams[i].path);
					}
				} else {

					int beams = 1 + (HT - HP) / 400;

					for (int i = 0; i < beams; i++) {

						int targetPos = Dungeon.hero.pos;
						if (i != 0) {
							do {
								targetPos = Dungeon.hero.pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
							} while (Dungeon.level.trueDistance(pos, Dungeon.hero.pos)
									> Dungeon.level.trueDistance(pos, targetPos));
						}
						targetedCells.add(targetPos);
						Ballistica b = new Ballistica(pos, targetPos, Ballistica.WONT_STOP);
						affectedCells.addAll(b.path);

					}
				}

				//remove one beam if multiple shots would cause every cell next to the hero to be targeted
				boolean allAdjTargeted = true;
				for (int i : PathFinder.NEIGHBOURS9){
					if (!affectedCells.contains(Dungeon.hero.pos + i) && Dungeon.level.passable[Dungeon.hero.pos + i]){
						allAdjTargeted = false;
						break;
					}
				}
				if (allAdjTargeted){
					targetedCells.remove(targetedCells.size()-1);
				}
				for (int i : targetedCells){
					Ballistica b = new Ballistica(pos, i, Ballistica.WONT_STOP);
					for (int p : b.path){
						sprite.parent.add(new TargetedCell(p, needCrossBeam ? 0xFFFFFF : 0xFF0000));
						affectedCells.add(p);
					}
				}

				//don't want to overly punish players with slow move or attack speed
				spend(GameMath.gate(TICK, (int)Math.ceil(Dungeon.hero.cooldown()), 3*TICK));
				Dungeon.hero.interrupt();

				abilityCooldown += Random.NormalFloat(MIN_ABILITY_CD, MAX_ABILITY_CD);
				abilityCooldown -= (phase - 1);
				needCrossBeam = Random.Int(2) == 0;
				if (needCrossBeam){
					GameScene.flash(0xFFFFFF);
					for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++){
						Ballistica b = new Ballistica(pos, pos + PathFinder.NEIGHBOURS8[i], Ballistica.STOP_SOLID);
						for (int p : b.path){
							TargetedCell targetedCell = new TargetedCell(p, 0xFF0000);
							targetedCell.alpha = 2f;
							sprite.parent.add(targetedCell);
							affectedCells.add(p);
						}
					}
					spend(TICK);
				}

			} else {
				spend(TICK);
			}

			while (summonCooldown <= 0){

				Class<?extends Mob> cls = regularSummons.remove(0);
				Mob summon = Reflection.newInstance(cls);
				regularSummons.add(cls);

				int spawnPos = -1;
				for (int i : PathFinder.NEIGHBOURS8){
					if (Actor.findChar(pos+i) == null){
						if (spawnPos == -1 || Dungeon.level.trueDistance(Dungeon.hero.pos, spawnPos) > Dungeon.level.trueDistance(Dungeon.hero.pos, pos+i)){
							spawnPos = pos + i;
						}
					}
				}

				//if no other valid spawn spots exist, try to kill an adjacent sheep to spawn anyway
				if (spawnPos == -1){
					for (int i : PathFinder.NEIGHBOURS8){
						if (Actor.findChar(pos+i) instanceof Sheep){
							if (spawnPos == -1 || Dungeon.level.trueDistance(Dungeon.hero.pos, spawnPos) > Dungeon.level.trueDistance(Dungeon.hero.pos, pos+i)){
								spawnPos = pos + i;
							}
						}
					}
					if (spawnPos != -1){
						Actor.findChar(spawnPos).die(null);
					}
				}

				if (spawnPos != -1) {
					summon.pos = spawnPos;
					GameScene.add( summon );
					Actor.add( new Pushing( summon, pos, summon.pos ) );
					summon.beckon(Dungeon.hero.pos);
					Dungeon.level.occupyCell(summon);

					summonCooldown += Random.NormalFloat(MIN_SUMMON_CD, MAX_SUMMON_CD);
					summonCooldown -= (phase - 1);
					if (findFist() != null){
						summonCooldown += MIN_SUMMON_CD - (phase - 1);
					}
				} else {
					break;
				}
			}

		}

		if (summonCooldown > 0) summonCooldown--;
		if (abilityCooldown > 0) abilityCooldown--;

		//extra fast abilities and summons at the final 100 HP
		if (phase == 5 && abilityCooldown > 2){
			abilityCooldown = 2;
		}
		if (phase == 5 && summonCooldown > 3){
			summonCooldown = 3;
		}

		return true;
	}

	public void processFistDeath(){
		//normally Yog has no logic when a fist dies specifically
		//but the very last fist to die does trigger the final phase
		if (phase == 4 && findFist() == null){
			yell(Messages.get(this, "hope"));
			summonCooldown = -15; //summon a burst of minions!
			phase = 5;
			BossHealthBar.bleed(true);
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					Music.INSTANCE.fadeOut(0.5f, new Callback() {
						@Override
						public void call() {
							Music.INSTANCE.play(Assets.Music.HALLS_BOSS_FINALE, true);
						}
					});
				}
			});
		}
	}

	@Override
	public boolean isAlive() {
		return super.isAlive() || phase != 5;
	}

	@Override
	public boolean isInvulnerable(Class effect) {
		return phase == 0 || findFist() != null || super.isInvulnerable(effect);
	}

	@Override
	public void damage( int dmg, Object src ) {

		int preHP = HP;
		super.damage( dmg, src );

		if (phase == 0 || findFist() != null) return;

		if (phase < 4) {
			HP = Math.max(HP, HT - 300 * phase);
		} else if (phase == 4) {
			HP = Math.max(HP, 100);
		}
		int dmgTaken = preHP - HP;

		if (dmgTaken > 0) {
			abilityCooldown -= dmgTaken / 10f;
			summonCooldown -= dmgTaken / 10f;
		}

		if (phase < 4 && HP <= HT - 300*phase){

			phase++;

			updateVisibility(Dungeon.level);
			GLog.n(Messages.get(this, "darkness"));
			sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "invulnerable"));

			addFist((YogFist)Reflection.newInstance(fistSummons.remove(0)));

			if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
				addFist((YogFist)Reflection.newInstance(challengeSummons.remove(0)));
			}

			CellEmitter.get(Dungeon.level.exit()-1).burst(ShadowParticle.UP, 25);
			CellEmitter.get(Dungeon.level.exit()).burst(ShadowParticle.UP, 100);
			CellEmitter.get(Dungeon.level.exit()+1).burst(ShadowParticle.UP, 25);

			if (abilityCooldown < 5) abilityCooldown = 5;
			if (summonCooldown < 5) summonCooldown = 5;

		}

		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null && !isImmune(src.getClass()) && !isInvulnerable(src.getClass())){
			if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES))   lock.addTime(dmgTaken/3f);
			else                                                    lock.addTime(dmgTaken/2f);
		}

	}

	public void addFist(YogFist fist){
		fist.pos = Dungeon.level.exit();

		CellEmitter.get(Dungeon.level.exit()-1).burst(ShadowParticle.UP, 25);
		CellEmitter.get(Dungeon.level.exit()).burst(ShadowParticle.UP, 100);
		CellEmitter.get(Dungeon.level.exit()+1).burst(ShadowParticle.UP, 25);

		if (abilityCooldown < 5) abilityCooldown = 5;
		if (summonCooldown < 5) summonCooldown = 5;

		int targetPos = Dungeon.level.exit() + Dungeon.level.width();

		if (!Dungeon.isChallenged(Challenges.STRONGER_BOSSES)
				&& (Actor.findChar(targetPos) == null || Actor.findChar(targetPos) instanceof Sheep)){
			fist.pos = targetPos;
		} else if (Actor.findChar(targetPos-1) == null || Actor.findChar(targetPos-1) instanceof Sheep){
			fist.pos = targetPos-1;
		} else if (Actor.findChar(targetPos+1) == null || Actor.findChar(targetPos+1) instanceof Sheep){
			fist.pos = targetPos+1;
		} else if (Actor.findChar(targetPos) == null || Actor.findChar(targetPos) instanceof Sheep){
			fist.pos = targetPos;
		}

		if (Actor.findChar(fist.pos) instanceof Sheep){
			Actor.findChar(fist.pos).die(null);
		}

		GameScene.add(fist, 4);
		Actor.add( new Pushing( fist, Dungeon.level.exit(), fist.pos ) );
		Dungeon.level.occupyCell(fist);
	}

	public void updateVisibility( Level level ){
		int viewDistance = 4;
		if (phase > 1 && isAlive()){
			viewDistance = Math.max(4 - (phase-1), 1);
		}
		if (Dungeon.isChallenged(Challenges.DARKNESS)) {
			viewDistance = Math.min(viewDistance, 2);
		}
		level.viewDistance = viewDistance;
		if (Dungeon.hero != null) {
			if (Dungeon.hero.buff(Light.class) == null) {
				Dungeon.hero.viewDistance = level.viewDistance;
			}
			Dungeon.observe();
		}
	}

	private YogFist findFist(){
		for ( Char c : Actor.chars() ){
			if (c instanceof YogFist){
				return (YogFist) c;
			}
		}
		return null;
	}

	@Override
	public void beckon( int cell ) {
	}

	@Override
	public void clearEnemy() {
		//do nothing
	}

	@Override
	public void aggro(Char ch) {
		if (ch != null && ch.alignment != alignment || !(ch instanceof Larva || ch instanceof YogRipper || ch instanceof YogEye || ch instanceof YogScorpio)) {
			for (Mob mob : (Iterable<Mob>) Dungeon.level.mobs.clone()) {
				if (mob != ch && Dungeon.level.distance(pos, mob.pos) <= 4 && mob.alignment == alignment &&
						(mob instanceof Larva || mob instanceof YogRipper || mob instanceof YogEye || mob instanceof YogScorpio)) {
					mob.aggro(ch);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void die( Object cause ) {

		Bestiary.skipCountingEncounters = true;
		for (Mob mob : (Iterable<Mob>)Dungeon.level.mobs.clone()) {
			if (mob instanceof Larva || mob instanceof YogRipper || mob instanceof YogEye || mob instanceof YogScorpio) {
				mob.die( cause );
			}
		}
		Bestiary.skipCountingEncounters = false;

		updateVisibility(Dungeon.level);

		GameScene.bossSlain();

		if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES) && Statistics.spawnersAlive == 4){
			Badges.validateBossChallengeCompleted();
		} else {
			Statistics.qualifiedForBossChallengeBadge = false;
		}
		Statistics.bossScores[4] += 5000 + 1250*Statistics.spawnersAlive;

		Badges.validateTakingTheMick(cause);

		Dungeon.level.unseal();
		super.die( cause );

		yell( Messages.get(this, "defeated") );
	}

	@Override
	public void notice() {
		if (!BossHealthBar.isAssigned()) {
			BossHealthBar.assignBoss(this);
			yell(Messages.get(this, "notice"));
			for (Char ch : Actor.chars()){
				if (ch instanceof DriedRose.GhostHero){
					((DriedRose.GhostHero) ch).sayBoss();
				}
			}
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					Music.INSTANCE.play(Assets.Music.HALLS_BOSS, true);
				}
			});
			if (phase == 0) {
				phase = 1;
				summonCooldown = Random.NormalFloat(MIN_SUMMON_CD, MAX_SUMMON_CD);
				abilityCooldown = Random.NormalFloat(MIN_ABILITY_CD, MAX_ABILITY_CD);
			}
		}
	}

	@Override
	public String description() {
		String desc = super.description();

		if (Statistics.spawnersAlive > 0){
			desc += "\n\n" + Messages.get(this, "desc_spawners");
		}

		return desc;
	}

	private static final String PHASE = "phase";

	private static final String ABILITY_CD = "ability_cd";
	private static final String SUMMON_CD = "summon_cd";

	private static final String FIST_SUMMONS = "fist_summons";
	private static final String REGULAR_SUMMONS = "regular_summons";
	private static final String CHALLENGE_SUMMONS = "challenges_summons";

	private static final String TARGETED_CELLS = "targeted_cells";
	private static final String NEED_CROSS = "needCross";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(PHASE, phase);

		bundle.put(ABILITY_CD, abilityCooldown);
		bundle.put(SUMMON_CD, summonCooldown);

		bundle.put(FIST_SUMMONS, fistSummons.toArray(new Class[0]));
		bundle.put(CHALLENGE_SUMMONS, challengeSummons.toArray(new Class[0]));
		bundle.put(REGULAR_SUMMONS, regularSummons.toArray(new Class[0]));

		int[] bundleArr = new int[targetedCells.size()];
		for (int i = 0; i < targetedCells.size(); i++){
			bundleArr[i] = targetedCells.get(i);
		}
		bundle.put(TARGETED_CELLS, bundleArr);
		bundle.put(NEED_CROSS, needCrossBeam);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		phase = bundle.getInt(PHASE);
		if (phase != 0) {
			BossHealthBar.assignBoss(this);
			if (phase == 5) BossHealthBar.bleed(true);
		}

		abilityCooldown = bundle.getFloat(ABILITY_CD);
		summonCooldown = bundle.getFloat(SUMMON_CD);
		needCrossBeam = bundle.getBoolean(NEED_CROSS);

		fistSummons.clear();
		Collections.addAll(fistSummons, bundle.getClassArray(FIST_SUMMONS));
		challengeSummons.clear();
		Collections.addAll(challengeSummons, bundle.getClassArray(CHALLENGE_SUMMONS));
		regularSummons.clear();
		Collections.addAll(regularSummons, bundle.getClassArray(REGULAR_SUMMONS));

		for (int i : bundle.getIntArray(TARGETED_CELLS)){
			targetedCells.add(i);
		}
	}

	public static class Larva extends Mob {

		{
			spriteClass = LarvaSprite.class;

			HP = HT = 20;
			defenseSkill = 12;
			viewDistance = Light.DISTANCE;

			EXP = 5;
			maxLvl = -2;

			properties.add(Property.DEMONIC);
			properties.add(Property.BOSS_MINION);
		}

		@Override
		public int attackSkill( Char target ) {
			return 30;
		}

		@Override
		public int damageRoll() {
			return Random.NormalIntRange( 15, 25 );
		}

		@Override
		public int drRoll() {
			return super.drRoll() + Random.NormalIntRange(0, 4);
		}

	}

	//used so death to yog's ripper demons have their own rankings description
	public static class YogRipper extends RipperDemon {
		{
			maxLvl = -2;
			properties.add(Property.BOSS_MINION);
		}
	}
	public static class YogEye extends Eye {
		{
			maxLvl = -2;
			properties.add(Property.BOSS_MINION);
		}
	}
	public static class YogScorpio extends Scorpio {
		{
			maxLvl = -2;
			properties.add(Property.BOSS_MINION);
		}
	}
}
