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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WizardSprite;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.HashMap;

public class Wizard extends Minion implements Callback {

	{
		spriteClass = WizardSprite.class;

		properties.add(Property.UNDEAD);
		properties.add(Property.RANGED);

		minDefense = 3;
		maxDefense = 10;
	}
	
	@Override
	public boolean canAttack(Char enemy) {
		return rank != 3 && new Ballistica( pos, enemy.pos, Ballistica.FRIENDLY_MAGIC).collisionPos == enemy.pos;
	}

	private static final HashMap<Class<? extends Buff>, Float> DEBUFFS = new HashMap<>();
    static{
        DEBUFFS.put(Cripple.class,        4f);
        DEBUFFS.put(Weakness.class,       3f);
        DEBUFFS.put(Blindness.class,      4f);
        DEBUFFS.put(Slow.class,           3f);
        DEBUFFS.put(Vertigo.class,        3f);
        DEBUFFS.put(Amok.class,           2f);
    }
	
	protected boolean doAttack( Char enemy ) {

		if (Dungeon.level.adjacent( pos, enemy.pos )) {
			
			return super.doAttack( enemy );
			
		} else {
			
			boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
			if (visible) {
				sprite.zap( enemy.pos );
			} else {
				zap();
			}
			
			return !visible;
		}
	}

	@Override
	public void damage(int dmg, Object src) {
		for (Class c : AntiMagic.RESISTS){
			if (c.isAssignableFrom(src.getClass())){
                dmg = Math.round(dmg * 0.75f);
			}
		}
		super.damage(dmg, src);
	}

	@Override
	protected boolean act() {
		boolean actResult = super.act();

		if (paralysed <= 0 && rank == 3){
			switch (behaviorType){
				case REACTIVE:
					for (Mob ch: Dungeon.level.mobs.toArray( new Mob[0] )){
						if (ch.buff(ReactiveTargeting.class) != null){
							ch.damage(1, this);
							Buff.affect(ch, Hex.class, 1f);
						}
					}
					break;
				case PROTECTIVE:
					for (Mob ch: Dungeon.level.mobs.toArray( new Mob[0] )){
						if (ch.buff(ProtectiveTargeting.class) != null){
							Buff.affect( ch, Terror.class, 1 ).object = Dungeon.hero.id();
						}
					}
					break;
				case AGGRESSIVE:
					//do nothing by itself
					//the actual effect is in Hero.ATU()
					break;
				case PASSIVE:
					for (Buff buff: Dungeon.hero.buffs()){
						if (buff.type == Buff.buffType.NEGATIVE){
							float heal = 1.5f;
							//effectively 2 HP per buff
							if (Random.Float() < heal%1){
								heal++;
							}
							if (heal >= 1f && Dungeon.hero.HP < Dungeon.hero.HT) {
								Dungeon.hero.HP = Math.min(Dungeon.hero.HT, Dungeon.hero.HP + (int)heal);
								Dungeon.hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString((int)heal), FloatingText.HEALING);

								if (Dungeon.hero.HP == Dungeon.hero.HT) {
									Dungeon.hero.resting = false;
								}
							}
						}
					}
			}
		}

		return actResult;
	}

	//used so resistances can differentiate between melee and magical attacks
	public static class DarkBolt{}
	
	private void zap() {
		spend( attackDelay() );
		
		if (hit( this, enemy, true )) {
			if (rank == 2){
				WandOfCorruption wand = new WandOfCorruption();
				wand.level((int) (Dungeon.scalingDepth() / Dungeon.chapterSize() + Math.max(0, Dungeon.hero.ATU() - attunement)));
				wand.onZap(new Ballistica( pos, enemy.pos, Ballistica.FRIENDLY_MAGIC));
			}
			else {
			    Class<? extends FlavourBuff> buff = (Class<? extends FlavourBuff>) Random.chances(DEBUFFS);
				Buff.prolong( enemy, buff, 4 );
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
	}

	@Override
	public float attackDelay() {
		float mod = 0;
		switch (rank){
			case 1: mod = 1; break;
			case 2: mod = 0.5f; break;
			case 3: mod = 1f; break;
		}
		return super.attackDelay() * mod;
	}
}
