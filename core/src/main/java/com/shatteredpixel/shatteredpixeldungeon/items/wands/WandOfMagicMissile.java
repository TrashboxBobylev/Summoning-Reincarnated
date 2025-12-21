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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class WandOfMagicMissile extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_MAGIC_MISSILE;
	}

	public float magicMin(float lvl){
		return 2+lvl;
	}

	public float magicMax(float lvl){
		return 8+3.75f*lvl;
	}

    @Override
    public float powerModifier(int type) {
        switch (type){
            case 1: return 1f;
            case 2: return 3f;
            case 3: return 0f;
        }
        return super.powerModifier(type);
    }

    @Override
    public float rechargeModifier(int type) {
        switch (type){
            case 1: return 1f;
            case 2: return 1.75f;
            case 3: return 1.33f;
        }
        return super.rechargeModifier(type);
    }

    @Override
    public int min(int lvl) {
        int min = super.min(lvl);
        if (Dungeon.hero != null && Dungeon.hero.subClass == HeroSubClass.BATTLEMAGE && type() == 2){
            min *= 4;
        }
        return min;
    }

    @Override
    public int max(int lvl) {
        int max = super.max(lvl);
        if (Dungeon.hero != null && Dungeon.hero.subClass == HeroSubClass.BATTLEMAGE && type() == 2){
            max *= 2;
        }
        return max;
    }

    @Override
    public float accuracyFactor(Char owner, Char target) {
        float acc = super.accuracyFactor(owner, target);
        if (Dungeon.hero != null && Dungeon.hero.subClass == HeroSubClass.BATTLEMAGE && type() == 2){
            acc *= 0.8f;
        }
        return acc;
    }

    @Override
	public void onZap(Ballistica bolt) {
				
		Char ch = Actor.findChar( bolt.collisionPos );
		if (ch != null) {
            boolean hit = true;

			if (!(Dungeon.isChallenged(Conducts.Conduct.PACIFIST))) {
                if (type() == 2){
                    hit = Char.hit( curUser, ch, 1.5f, true );
                }
                if (hit) {
                    wandProc(ch, chargesPerCast());
                    if (damageRoll() > 0)
                        ch.damage(damageRoll(), this);
                } else {
                    ch.sprite.showStatus( CharSprite.NEUTRAL,  ch.defenseVerb() );
                    Buff.detach(curUser, Talent.FightingWizardryTracker.class);
                }
			}
            if (hit) {
                Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f));
                if (type() != 2) {
                    //apply the magic charge buff if we have another wand in inventory of a lower level, or already have the buff
                    for (Wand.Charger wandCharger : curUser.buffs(Wand.Charger.class)) {
                        if (wandCharger != charger) {
                            Buff.prolong(curUser, MagicCharge.class, MagicCharge.DURATION).setup(this);
                            break;
                        }
                    }
                }
            }

			ch.sprite.burst(0xFFFFFFFF, (int) (power() / 2 + 2));

		} else {
			Dungeon.level.pressCell(bolt.collisionPos);
		}
	}

	@Override
	public void onHit(Char attacker, Char defender, int damage) {
        if (type() == 1) {
            SpellSprite.show(attacker, SpellSprite.CHARGE);
            for (Wand.Charger c : attacker.buffs(Wand.Charger.class)) {
                if (c.wand() != this) {
                    c.gainCharge(0.5f * procChanceMultiplier(attacker));
                }
            }
        }
        if (type() == 3){
            Splash.at(defender.sprite.center(), 0xf84037, 10);
            Buff.count(defender, ArcaneDamageStack.class, Dungeon.hero.ATU()*2);
        }

	}

    public static class ArcaneDamageStack extends CounterBuff {
        @Override
        public int icon() {
            return BuffIndicator.WAND;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0xf84037);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", (int)count());
        }
    }

	public static class MagicCharge extends FlavourBuff {

		{
			type = buffType.POSITIVE;
			announced = true;
		}

		public static float DURATION = 4f;
		private Wand wandJustApplied; //we don't bundle this as it's only used right as the buff is applied

		public void setup(Wand wand){
            this.wandJustApplied = wand;
		}

		@Override
		public void detach() {
			super.detach();
			updateQuickslot();
		}

		//this is used briefly so that a wand of magic missile can't clear the buff it just applied
		public Wand wandJustApplied(){
            return this.wandJustApplied;
		}

        public float powerModifier(){
            if (wandJustApplied.type() == 3){
                return 3f;
            }
            return 1.5f;
        }

		@Override
		public int icon() {
			return BuffIndicator.UPGRADE;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0.2f, 0.6f, 1f);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}

        @Override
        public String desc() {
            return Messages.get(this, "desc", powerModifier(), dispTurns());
        }
    }

}
