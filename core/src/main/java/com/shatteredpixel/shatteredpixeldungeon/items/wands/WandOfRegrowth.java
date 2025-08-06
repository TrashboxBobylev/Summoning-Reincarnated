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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LotusSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RotLasherSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.ColorMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class WandOfRegrowth extends Wand {

	{
		image = ItemSpriteSheet.WAND_REGROWTH;

		//only used for targeting, actual projectile logic is Ballistica.STOP_SOLID
		collisionProperties = Ballistica.WONT_STOP;
	}
	
	private int totChrgUsed = 0;
	private int chargesOverLimit = 0;

	ConeAOE cone;
	int target;

	@Override
	public boolean tryToZap(Hero owner, int target) {
		if (super.tryToZap(owner, target)){
			this.target = target;
			return true;
		} else {
			return false;
		}
	}

    @Override
    public float rechargeModifier(int rank) {
        switch (rank){
            case 1:
                return 1.333f;
            case 2:
                return 2.25f;
            case 3:
                return 2.5f;
        }
        return super.rechargeModifier(rank);
    }

    public float grassPlaced(int rank, float power){
        switch (rank){
            case 1:
                return 3.67f+power/3f;
            case 2:
                return 2.25f+power/5f;
            case 3:
                return 0f;
        }
        return 1f;
    }

    public float rootPlaced(int rank){
        switch (rank){
            case 1:
                return 4f;
            case 2:
                return 8f;
            case 3:
                return 0f;
        }
        return 0;
    }

    @Override
	public void onZap(Ballistica bolt) {

		ArrayList<Integer> cells = new ArrayList<>(cone.cells);

		float furrowedChance = 0;
		if (totChrgUsed >= chargeLimit(Dungeon.hero.lvl)){
			furrowedChance = (chargesOverLimit+1)/5f;
		}

		int chrgUsed = chargesPerCast();
		int grassToPlace = Math.round((grassPlaced(rank(), power()))*chrgUsed);

		//ignore cells which can't have anything grow in them.
		for (Iterator<Integer> i = cells.iterator(); i.hasNext();) {
			int cell = i.next();
			int terr = Dungeon.level.map[cell];
			if (!(terr == Terrain.EMPTY || terr == Terrain.EMBERS || terr == Terrain.EMPTY_DECO ||
					terr == Terrain.GRASS || terr == Terrain.HIGH_GRASS || terr == Terrain.FURROWED_GRASS)) {
				i.remove();
			} else if (Char.hasProp(Actor.findChar(cell), Char.Property.IMMOVABLE)) {
				i.remove();
			} else if (Dungeon.level.plants.get(cell) != null){
				i.remove();
			} else {
				if (terr != Terrain.HIGH_GRASS && terr != Terrain.FURROWED_GRASS) {
					Level.set(cell, Terrain.GRASS);
					GameScene.updateMap( cell );
				}
				Char ch = Actor.findChar(cell);
				if (ch != null){
					if (ch instanceof DwarfKing){
						Statistics.qualifiedForBossChallengeBadge = false;
					}
					wandProc(ch, chargesPerCast());
					Buff.prolong( ch, Roots.class, rootPlaced(rank()) * chrgUsed );
				}
			}
		}

		Random.shuffle(cells);

		if (chargesPerCast() == 2){
			Lotus l = getFlower();
			l.setLevel(power());
			if (cells.contains(target) && Actor.findChar(target) == null){
				cells.remove((Integer)target);
				l.pos = target;
				GameScene.add(l);
			} else {
				for (int i = bolt.path.size()-1; i >= 0; i--){
					int c = bolt.path.get(i);
					if (cells.contains(c) && Actor.findChar(c) == null){
						cells.remove((Integer)c);
						l.pos = c;
						GameScene.add(l);
						break;
					}
				}
			}
		}

		//places grass along center of cone
		for (int cell : bolt.path){
			if (grassToPlace > 0 && cells.contains(cell)){
				if (Random.Float() > furrowedChance) {
					Level.set(cell, Terrain.HIGH_GRASS);
				} else {
					Level.set(cell, Terrain.FURROWED_GRASS);
				}
				GameScene.updateMap( cell );
				grassToPlace--;
				//moves cell to the back
				cells.remove((Integer)cell);
				cells.add(cell);
			}
		}

		if (!cells.isEmpty() && Random.Float() > furrowedChance &&
				(Random.Int(6) < chrgUsed)){ // 16%/33%/50% chance to spawn a seed pod or dewcatcher
			int cell = cells.remove(0);
			Dungeon.level.plant( Random.Int(2) == 0 ? new Seedpod.Seed() : new Dewcatcher.Seed(), cell);
		}

		if (!cells.isEmpty() && Random.Float() > furrowedChance &&
				(Random.Int(3) < chrgUsed)){ // 33%/66%/100% chance to spawn a plant
			int cell = cells.remove(0);
			Dungeon.level.plant((Plant.Seed) Generator.randomUsingDefaults(Generator.Category.SEED), cell);
		}

		for (int cell : cells){
			if (grassToPlace <= 0 || bolt.path.contains(cell)) break;

			if (Dungeon.level.map[cell] == Terrain.HIGH_GRASS) continue;

			if (Random.Float() > furrowedChance) {
				Level.set(cell, Terrain.HIGH_GRASS);
			} else {
				Level.set(cell, Terrain.FURROWED_GRASS);
			}
			GameScene.updateMap( cell );
			grassToPlace--;
		}

		if (totChrgUsed < chargeLimit(Dungeon.hero.lvl)) {
			chargesOverLimit = 0;
			totChrgUsed += chrgUsed;
			if (totChrgUsed > chargeLimit(Dungeon.hero.lvl)){
				chargesOverLimit = totChrgUsed - chargeLimit(Dungeon.hero.lvl);
				totChrgUsed = chargeLimit(Dungeon.hero.lvl);
			}
		} else {
			chargesOverLimit += chrgUsed;
		}

	}

	private int chargeLimit( int heroLvl ){
		return chargeLimit(  heroLvl, power() );
	}

	private int chargeLimit( int heroLvl, float wndLvl ){
		if (wndLvl >= 7){
			return Integer.MAX_VALUE;
		} else {
			//20 charges at base, plus:
			//2/3.1/4.2/5.5/6.8/8.4/10.4/inf. charges per hero level, at wand level:
			//0/1  /2  /3  /4  /5  /6   /10
			return Math.round(20 + heroLvl * (2+wndLvl) * (1f + (wndLvl/(50 - 5*wndLvl))));
		}
	}

    public Lotus getFlower(){
        switch (rank()) {
            case 1: default:
                return new Lotus();
            case 2:
                return new Thornflower();
            case 3:
                return new Vanguard();
        }
    }

	@Override
	public void onHit(Char attacker, Char defender, int damage) {
		//like pre-nerf vampiric enchantment, except with herbal healing buff, only in grass
		boolean grass = false;
		int terr = Dungeon.level.map[attacker.pos];
		if (terr == Terrain.GRASS || terr == Terrain.HIGH_GRASS || terr == Terrain.FURROWED_GRASS){
			grass = true;
		}
		terr = Dungeon.level.map[defender.pos];
		if (terr == Terrain.GRASS || terr == Terrain.HIGH_GRASS || terr == Terrain.FURROWED_GRASS){
			grass = true;
		}

		if (grass) {
			float level = Math.max(0, power());

			// lvl 0 - 16%
			// lvl 1 - 21%
			// lvl 2 - 25%
			int healing = Math.round(damage * (level + 2f) / (level + 6f) / 2f);
			healing = Math.round(healing * procChanceMultiplier(attacker));
			Buff.affect(attacker, Sungrass.Health.class).boost(healing);
		}

	}

	public void fx(Ballistica bolt, Callback callback) {

		// 5/7 distance
		int maxDist = 3 + 2*chargesPerCast();

		cone = new ConeAOE( bolt,
				maxDist,
				30 + 15*chargesPerCast(),
				Ballistica.STOP_SOLID | Ballistica.STOP_TARGET);

		//cast to cells at the tip, rather than all cells, better performance.
		Ballistica longestRay = null;
		for (Ballistica ray : cone.outerRays){
			if (longestRay == null || ray.dist > longestRay.dist){
				longestRay = ray;
			}
			((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
					MagicMissile.FOLIAGE_CONE,
					curUser.sprite,
					ray.path.get(ray.dist),
					null
			);
		}

		//final zap at half distance of the longest ray, for timing of the actual wand effect
		MagicMissile.boltFromChar( curUser.sprite.parent,
				MagicMissile.FOLIAGE_CONE,
				curUser.sprite,
				longestRay.path.get(longestRay.dist/2),
				callback );
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
	}

	@Override
	protected int chargesPerCast() {
		if (cursed ||
				(charger != null && charger.target != null && charger.target.buff(WildMagic.WildMagicTracker.class) != null)){
			return 1;
		}
		//consumes 2 charges if above 50%, 1 otherwise
		return curCharges >= maxCharges / 2 ? 2 : 1;
	}

	@Override
	public String statsDesc() {
		String desc = Messages.get(this, "stats_desc", chargesPerCast());
		if (isIdentified()){
			int chargeLeft = chargeLimit(Dungeon.hero.lvl) - totChrgUsed;
			if (chargeLeft < 10000) desc += " " + Messages.get(this, "degradation", Math.max(chargeLeft, 0));
		}
		return desc;
	}

	@Override
	public String upgradeStat1(int level) {
		return Messages.decimalFormat("#.##", 3 + (2+level)/3f);
	}

	@Override
	public String upgradeStat2(int level) {
		if (level >= 10){
			return "∞";
		} else {
			return Integer.toString(chargeLimit(Dungeon.hero.lvl, level));
		}
	}

    @Override
    public String getRankMessage(int rank) {
        return Messages.get(this, "rank",
                getRechargeInfo(rank),
                grassPlaced(rank, power()),
                (int)rootPlaced(rank),
                plantDescription(rank)
        );
    }

    public String plantDescription(int rank){
        switch (rank){
            case 1:
                return Messages.get(this, "lotus", (int)(25 + 3*power()), (int)(1 + power()*2), (int)(Math.min( 1f, 0.40f + 0.05f*power() )*100));
            case 2:
                return Messages.get(this, "thorn", (int)(20 + 2.5f*power()), (int)(3+power()), (int)(1 + power()));
            case 3:
                return Messages.get(this, "vanguard", (int)(50 + 7.5f*power()));
        }
        return "";
    }



    @Override
	public void staffFx(WandParticle particle) {
		particle.color( ColorMath.random(0x004400, 0x88CC44) );
		particle.am = 1f;
		particle.setLifespan(1f);
		particle.setSize( 1f, 1.5f);
		particle.shuffleXY(0.5f);
		float dst = Random.Float(11f);
		particle.x -= dst;
		particle.y += dst;
	}
	
	private static final String TOTAL = "totChrgUsed";
	private static final String OVER = "chargesOverLimit";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( TOTAL, totChrgUsed );
		bundle.put( OVER, chargesOverLimit);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		totChrgUsed = bundle.getInt(TOTAL);
		chargesOverLimit = bundle.getInt(OVER);
	}
	
	public static class Dewcatcher extends Plant{

		{
			image = 13;
		}

		@Override
		public void activate( Char ch ) {

			int nDrops = Random.NormalIntRange(3, 6);

			ArrayList<Integer> candidates = new ArrayList<>();
			for (int i : PathFinder.NEIGHBOURS8){
				if (Dungeon.level.passable[pos+i]
						&& pos+i != Dungeon.level.entrance()
						&& pos+i != Dungeon.level.exit()){
					candidates.add(pos+i);
				}
			}

			for (int i = 0; i < nDrops && !candidates.isEmpty(); i++){
				Integer c = Random.element(candidates);
				if (Dungeon.level.heaps.get(c) == null) {
					Dungeon.level.drop(new Dewdrop(), c).sprite.drop(pos);
				} else {
					Dungeon.level.drop(new Dewdrop(), c).sprite.drop(c);
				}
				candidates.remove(c);
			}

		}

		//seed is never dropped, only care about plant class
		public static class Seed extends Plant.Seed {
			{
				plantClass = Dewcatcher.class;
			}
		}
	}

	public static class Seedpod extends Plant{

		{
			image = 14;
		}

		@Override
		public void activate( Char ch ) {

			int nSeeds = Random.NormalIntRange(2, 4);

			ArrayList<Integer> candidates = new ArrayList<>();
			for (int i : PathFinder.NEIGHBOURS8){
				if (Dungeon.level.passable[pos+i]
						&& pos+i != Dungeon.level.entrance()
						&& pos+i != Dungeon.level.exit()){
					candidates.add(pos+i);
				}
			}

			for (int i = 0; i < nSeeds && !candidates.isEmpty(); i++){
				Integer c = Random.element(candidates);
				Dungeon.level.drop(Generator.randomUsingDefaults(Generator.Category.SEED), c).sprite.drop(pos);
				candidates.remove(c);
			}

		}

		//seed is never dropped, only care about plant class
		public static class Seed extends Plant.Seed {
			{
				plantClass = Seedpod.class;
			}
		}

	}

	public static class Lotus extends NPC {

		{
			alignment = Alignment.NEUTRAL;
			properties.add(Property.IMMOVABLE);
			properties.add(Property.STATIC);

			spriteClass = LotusSprite.class;

			viewDistance = 1;
		}

		protected float wandLvl = 0;

		private void setLevel( float lvl ){
			wandLvl = lvl;
			HP = HT = Math.round(lifeMod(lvl));
		}

        public float lifeMod(float lvl){
            return 25 + 3*lvl;
        }

		public boolean inRange(int pos){
			return Dungeon.level.trueDistance(this.pos, pos) <= rangeMod();
		}

        public float rangeMod(){
            return 1 + wandLvl*2;
        }

		public float seedPreservation(){
			return Math.min( 1f, 0.40f + 0.05f*wandLvl );
		}

		@Override
		public boolean canInteract(Char c) {
			return false;
		}

		@Override
		protected boolean act() {
			super.act();

			if (--HP <= 0){
				destroy();
				sprite.die();
			}

			return true;
		}

		@Override
		public void damage( int dmg, Object src ) {
			//do nothing
		}

		@Override
		public boolean add( Buff buff ) {
			return false;
		}

		@Override
		public void destroy() {
			super.destroy();
			Dungeon.observe();
			GameScene.updateFog(pos, viewDistance+1);
		}

		@Override
		public boolean isInvulnerable(Class effect) {
			return true;
		}

		{
			immunities.add( Doom.class );
		}

		@Override
		public String description() {
			String desc = Messages.get(this, "desc");
			if (Actor.chars().contains(this)) {
				int preservation = Math.round(seedPreservation()*100);
				desc += "\n\n" + Messages.get(this, "wand_info", (int)wandLvl, preservation, preservation);
			}
			return desc;
		}

		private static final String WAND_LVL = "wand_lvl";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(WAND_LVL, wandLvl);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			wandLvl = bundle.getFloat(WAND_LVL);
		}
	}

    public static class Thornflower extends Lotus {
        {
            spriteClass = LotusSprite.Thornflower.class;
        }

        @Override
        public float lifeMod(float lvl){
            return 20 + 2.5f*lvl;
        }

        @Override
        public float seedPreservation() {
            return 0f;
        }

        @Override
        public float rangeMod() {
            return 3 + wandLvl;
        }

        @Override
        protected boolean act() {
            viewDistance = (int) (3 + wandLvl);

            boolean act = super.act();

            int lashAmount = 0;
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
                if (mob instanceof ThornLasher)
                    lashAmount++;

            if (lashAmount < wandLvl + 1) {
                HashMap<Char, Float> enemies = new HashMap<>();
                for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
                    if (mob.alignment == Alignment.ENEMY && inRange(mob.pos)
                            && mob.invisible <= 0 && !mob.isInvulnerable(getClass()))
                        //do not target passive mobs
                        //intelligent allies also don't target mobs which are wandering or asleep
                        //unless they are aggressive minions
                        if (mob.state != mob.PASSIVE) {
                            int isOccupied = 0;
                            for (int i: PathFinder.NEIGHBOURS8){
                                if (Actor.findChar(mob.pos + i) instanceof ThornLasher){
                                    isOccupied++;
                                    break;
                                }
                            }
                            if (isOccupied < wandLvl / 2)
                                enemies.put(mob, mob.targetPriority());
                        }
                Char target = chooseClosest(enemies);
                if (target != null){
                    ArrayList<Integer> respawnPoints = new ArrayList<>();

                    for (int i = 0; i < PathFinder.NEIGHBOURS9.length; i++) {
                        int p = target.pos + PathFinder.NEIGHBOURS9[i];
                        if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
                            respawnPoints.add( p );
                        }
                    }

                    if (respawnPoints.size() > 0) {
                        int index = Random.index( respawnPoints );

                        ThornLasher mob = new ThornLasher();
                        mob.pos = respawnPoints.get(index);
                        GameScene.add( mob );

                        if (Dungeon.level.heroFOV[mob.pos]) {
                            CellEmitter.get(mob.pos).start(LeafParticle.LEVEL_SPECIFIC, 0.1f, 8);
                            mob.sprite.alpha( 0 );
                            mob.sprite.parent.add( new AlphaTweener( mob.sprite, 1, 0.4f ) );
                            Sample.INSTANCE.play(Assets.Sounds.PLANT);
                        }
                    }
                }
            }

            return act;
        }

        @Override
        public void destroy() {
            super.destroy();
            for (Char mob: Actor.chars()){
                if (mob instanceof ThornLasher){
                    mob.destroy();
                }
            }
        }

        @Override
        public String description() {
            return Messages.get(this, "desc", (int)(3 + wandLvl), 1 + wandLvl);
        }
    }

    public static class ThornLasher extends Mob {
        {
            alignment = Alignment.ALLY;
            spriteClass = RotLasherSprite.class;
            defenseSkill = 0;

            HP = HT = Dungeon.depth * 3;
            viewDistance = 1;

            actPriority = MOB_PRIO + 10;

            properties.add(Property.IMMOVABLE);

            state = WANDERING = new Waiting();
        }

        @Override
        public void damage(int dmg, Object src) {
            if (src instanceof Burning) {
                destroy();
                sprite.die();
            } else {
                super.damage(dmg, src);
            }
        }

        @Override
        public boolean reset() {
            return true;
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
        public float attackDelay() {
            return 0.5f;
        }

        @Override
        public int damageRoll() {
            return Random.NormalIntRange( 1 + Dungeon.scalingDepth()/2, 2 + 2*Dungeon.scalingDepth());
        }

        @Override
        public int attackProc(Char enemy, int damage) {
            Buff.affect( enemy, Bleeding.class ).set( Math.round(damage*0.45f) );
            return super.attackProc(enemy, damage);
        }

        @Override
        public int attackSkill(Char target) {
            return (int) (Dungeon.scalingDepth()*2.5f);
        }

        {
            immunities.add( ToxicGas.class );
        }

        private class Waiting extends Mob.Wandering{}
    }

    public static class Vanguard extends Lotus {
        {
            spriteClass = LotusSprite.Vanguard.class;
        }

        @Override
        public float lifeMod(float lvl){
            return 50 + 7.5f*lvl;
        }

        @Override
        public float seedPreservation() {
            return 0.5f;
        }

        @Override
        public float rangeMod() {
            return 3;
        }

        @Override
        protected boolean act() {
            viewDistance = 3;

            boolean act = super.act();

            for (int i = 0; i < Dungeon.level.length(); i++){
                if (!Dungeon.level.solid[i] && inRange(i)) {
                    Emitter e = CellEmitter.get(i);
                    e.burst(LeafParticle.GENERAL,  10);
                    int terr = Dungeon.level.map[i];
                    if ((terr == Terrain.EMPTY || terr == Terrain.EMBERS || terr == Terrain.EMPTY_DECO ||
                            terr == Terrain.GRASS || terr == Terrain.HIGH_GRASS || terr == Terrain.FURROWED_GRASS)
                            && !Char.hasProp(Actor.findChar(i), Property.IMMOVABLE)) {
                        Level.set(i, Terrain.FURROWED_GRASS);
                        GameScene.updateMap( i );
                    }
                    Char target;
                    if ((target = Actor.findChar(i)) != null){
                        if (target.alignment == Alignment.ENEMY){
                            Buff.prolong(target, Vulnerable.class, 2f);
                        } else {
                            Buff.affect(target, Earthroot.Armor.class).level(target.HP/3);
                        }
                    }
                }
            }

            return act;
        }
    }

}
