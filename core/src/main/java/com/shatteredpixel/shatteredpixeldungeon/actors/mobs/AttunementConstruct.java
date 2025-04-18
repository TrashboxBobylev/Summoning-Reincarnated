/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Shrink;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TimedShrink;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfPrismaticLight;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AttunementConstructSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class AttunementConstruct extends Mob implements Callback {
	
	private static final float TIME_TO_ZAP	= /*Dungeon.mode == Dungeon.GameMode.DIFFICULT ? 0.5f : */ 1f;
	
	{
		spriteClass = AttunementConstructSprite.class;
		
		HP = HT = 80;
		defenseSkill = 20;
		
		EXP = 15;
		maxLvl = 30;
		
		loot = Generator.random();
		lootChance = 1f;

		properties.add(Char.Property.UNDEAD);
		properties.add(Char.Property.DEMONIC);
		properties.add(Char.Property.RANGED);
	}

	//abyss not implemented yet
	public int abyssLevel(){
//		if (Dungeon.mode == Dungeon.GameMode.DIFFICULT)
//			return Math.max(0, Dungeon.chapterNumber()-4);

		return 0;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 18, 25 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 30 + abyssLevel()*10;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.Int(0, 8 + abyssLevel()*15);
	}
	
	@Override
	public boolean canAttack(Char enemy) {
		return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}
	
	protected boolean doAttack( Char enemy ) {
			
			boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
			if (visible) {
				sprite.zap( enemy.pos );
			} else {
				zap();
			}
			
			return !visible;
	}
	
	//used so resistances can differentiate between melee and magical attacks
//	public static class Bolt extends MagicalAttack{
//		public Bolt(Mob attacker, int damage) {
//			super(attacker, damage);
//		}
//	}
	
	private void zap() {
		spend( TIME_TO_ZAP );
		
		if (hit( this, enemy, true )) {

			Eradication eradication = enemy.buff(Eradication.class);
			float multiplier = 1f;
			if (eradication != null){
			    multiplier = (float) (Math.pow(1.2f, eradication.combo));
            }
			int damage = Random.Int( 4 + abyssLevel()*4, 10 + abyssLevel()*8 );
			if (buff(Shrink.class) != null|| enemy.buff(TimedShrink.class) != null) damage *= 0.6f;
			
			int dmg = Math.round(damage * multiplier);


			Buff.prolong( enemy, Eradication.class, Eradication.DURATION ).combo++;

			enemy.damage( dmg, this/*new Bolt(this, damage)*/ );
			
			if (!enemy.isAlive() && enemy == Dungeon.hero) {
				Badges.validateDeathFromEnemyMagic();
				Dungeon.fail( getClass() );
				GLog.negative( Messages.get(this, "bolt_kill") );
			}
		} else {
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		}
	}
	
	public void onZapComplete() {
		zap();
		next();
	}
	
	@Override
	public void call() {
		next();
	}

	{
		resistances.add( Grim.class );
		immunities.add(WandOfPrismaticLight.class);
		immunities.add(Blindness.class);
		immunities.add(Vertigo.class);
	}

    public static class Eradication extends FlavourBuff {

        public static final float DURATION = 4f;

        {
            type = buffType.NEGATIVE;
//			severity = buffSeverity.DAMAGING;
            announced = true;
        }

        public int combo;

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put("combo", combo);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            combo = bundle.getInt("combo");
        }

        @Override
        public int icon() {
            return BuffIndicator.ERADICATION;
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", dispTurns(), (float)Math.pow(1.2f, combo));
        }
    }
}
