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
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.CorrosionParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.ColorMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class WandOfCorrosion extends Wand {

	{
		image = ItemSpriteSheet.WAND_CORROSION;

		collisionProperties = Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
	}

    @Override
    public float rechargeModifier(int rank) {
        switch (rank){
            case 1: return 2.0f;
            case 2: return 0.8f;
            case 3: return 3.5f;
        }
        return super.rechargeModifier(rank);
    }

    public int amountOfGas(int rank){
        switch (rank){
            case 1: return (int) (50 + 10 * power());
            case 2: return (int) (60 + 12 * power());
            case 3: return (int) (1000 + 200*power());
        }
        return 1;
    }

    public int gasPower(int rank){
        switch (rank){
            case 1: return Math.round(2 + power());
            case 2: return Math.round(3 + power());
            case 3: return Math.round(1 + power()/2);
        }
        return 1;
    }

    @Override
	public void onZap(Ballistica bolt) {
        if (rank() == 2){
            Char ch = Actor.findChar(bolt.collisionPos);
            if (ch != null){
                Buff.affect(ch, CorrosiveImbue.class).set(gasPower(rank()), amountOfGas(rank()));
            }
        } else {
            CorrosiveGas gas = Blob.seed(bolt.collisionPos, amountOfGas(rank()), CorrosiveGas.class);
            gas.setStrength(gasPower(rank()), getClass());
            if (Dungeon.isChallenged(Conducts.Conduct.PACIFIST))
                gas.setStrength(gas.volume/3);
            GameScene.add(gas);
        }
        CellEmitter.get(bolt.collisionPos).burst(Speck.factory(Speck.CORROSION), 10 );
		Sample.INSTANCE.play(Assets.Sounds.GAS);

		for (int i : PathFinder.NEIGHBOURS9) {
			Char ch = Actor.findChar(bolt.collisionPos + i);
			if (ch != null) {
				wandProc(ch, chargesPerCast());

				if (i == 0 && ch instanceof DwarfKing){
					Statistics.qualifiedForBossChallengeBadge = false;
				}
			}
		}
		
		if (Actor.findChar(bolt.collisionPos) == null){
			Dungeon.level.pressCell(bolt.collisionPos);
		}
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar(
				curUser.sprite.parent,
				MagicMissile.CORROSION,
				curUser.sprite,
				bolt.collisionPos,
				callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}

	@Override
	public void onHit(Char attacker, Char defender, int damage) {
		float level = Math.max( 0, power() );

		// lvl 0 - 33%
		// lvl 1 - 50%
		// lvl 2 - 60%
		float procChance = (level+1f)/(level+3f) * procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {

			float powerMulti = Math.max(1f, procChance);
			
			Buff.affect( defender, Ooze.class ).set( Ooze.DURATION * powerMulti );
			CellEmitter.center(defender.pos).burst( CorrosionParticle.SPLASH, 5 );
			
		}
	}

	@Override
	public void staffFx(WandParticle particle) {
		particle.color( ColorMath.random( 0xAAAAAA, 0xFF8800) );
		particle.am = 0.6f;
		particle.setLifespan( 1f );
		particle.acc.set(0, 20);
		particle.setSize( 0.5f, 3f );
		particle.shuffleXY( 1f );
	}

	@Override
	public String statsDesc() {
        return Messages.get(this, "stats_desc" + rank(), gasPower(rank()));
    }

	@Override
	public String upgradeStat1(int level) {
		return Integer.toString(level+2);
	}

	@Override
	public String upgradeStat2(int level) {
		return Messages.decimalFormat("#.##x", 1+.2f*level);
	}

    @Override
    public String generalRankDescription(int rank) {
        return Messages.get(this, "rank" + rank,
                getRechargeInfo(rank),
                gasPower(rank), amountOfGas(rank)
        );
    }

    public static class CorrosiveImbue extends Buff {
        {
            type = buffType.NEGATIVE;
            announced = true;
        }

        public int power = 0;
        public int burst = 0;

        private static final String POWER = "power";
        private static final String BURST = "nurst";

        @Override
        public void storeInBundle( Bundle bundle ) {
            super.storeInBundle( bundle );
            bundle.put(POWER, power );
            bundle.put(BURST, burst );
        }

        @Override
        public void restoreFromBundle( Bundle bundle ) {
            super.restoreFromBundle( bundle );
            power = bundle.getInt(POWER);
            burst = bundle.getInt(BURST);
        }

        public void set(int power, int burst){
            this.power = power;
            this.burst = burst;
        }

        @Override
        public int icon() {
            return BuffIndicator.POISON;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1f, 0.5f, 0f);
        }

        @Override
        public void fx(boolean on) {
            if (on) target.sprite.aura( 0xce7a4c, 6 );
            else target.sprite.clearAura();
        }

        @Override
        public void detach() {
            super.detach();
            if (!target.isAlive()){
                CorrosiveGas gas = Blob.seed(target.pos, burst, CorrosiveGas.class);
                CellEmitter.get(target.pos).burst(Speck.factory(Speck.CORROSION), 10 );
                gas.setStrength(power, WandOfCorrosion.class);
                if (Dungeon.isChallenged(Conducts.Conduct.PACIFIST))
                    gas.setStrength(gas.volume/3);
                GameScene.add(gas);
                if (target.sprite != null && target.sprite.visible)
                    Sample.INSTANCE.play(Assets.Sounds.GAS);
            }
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", power, burst);
        }

        {
            immunities.add(Corrosion.class);
        }
    }
}
