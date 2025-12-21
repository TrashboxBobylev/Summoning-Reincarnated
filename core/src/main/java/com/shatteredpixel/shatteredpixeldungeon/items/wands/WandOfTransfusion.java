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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LifeLink;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BloodParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class WandOfTransfusion extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_TRANSFUSION;

		collisionProperties = Ballistica.PROJECTILE;
	}

	@Override
	public float magicMin(float level) {
		return 4 + level;
	}

	@Override
	public float magicMax(float level) {
		return 8 + 3.75f*level;
	}

	private boolean freeCharge = false;

    @Override
    public float powerModifier(int type) {
        switch (type){
            case 1: return 1.0f;
            case 2: return 0.4f*chargesPerCast();
            case 3: return 0f;
        }
        return super.powerModifier(type);
    }

    @Override
    public float rechargeModifier(int type) {
        switch (type){
            case 1: return 1.2f;
            case 2: return 0.5f;
            case 3: return 4f;
        }
        return super.rechargeModifier(type);
    }

    @Override
    protected int chargesPerCast() {
        switch (type()){
            case 2: return Math.max(1, curCharges);
            case 3: return 2;
        }
        return super.chargesPerCast();
    }

    @Override
	public void onZap(Ballistica beam) {

		for (int c : beam.subPath(0, beam.dist))
			CellEmitter.center(c).burst( BloodParticle.BURST, 1 );

		int cell = beam.collisionPos;

		Char ch = Actor.findChar(cell);

		if (ch instanceof Mob){
			
			wandProc(ch, chargesPerCast());
			
			//this wand does different things depending on the target.

            //on rank III, swap health states
            if (type() == 3 && !ch.properties().contains(Char.Property.BOSS) && !ch.properties().contains(Char.Property.UNDEAD)) {
                int myHealth = curUser.HP;
                int enemyHealth = ch.HP;
                float myHP = curUser.HP * 1f / curUser.HT;
                float enemyHP = ch.HP * 1f / ch.HT;
                if (ch.properties().contains(Char.Property.MINIBOSS)) {
                    myHP /= 3;
                    enemyHP /= 1.5f;
                }
                curUser.HP = (int) (curUser.HT * enemyHP);
                if (curUser.HP - myHealth != 0){
                    if (curUser.HP - myHealth > 0)
                        curUser.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(curUser.HP - myHealth), FloatingText.HEALING);
                    else
                        curUser.sprite.showStatusWithIcon(CharSprite.NEGATIVE, Integer.toString(-(curUser.HP - myHealth)), FloatingText.MAGIC_DMG);
                }
                ch.HP = (int) (ch.HT * myHP);
                if (ch.HP - enemyHealth != 0){
                    if (ch.HP - enemyHealth > 0)
                        ch.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(ch.HP - enemyHealth), FloatingText.HEALING);
                    else
                        ch.sprite.showStatusWithIcon(CharSprite.NEGATIVE, Integer.toString(-(ch.HP - enemyHealth)), FloatingText.MAGIC_DMG);
                }
            } else {

                //heals/shields an ally or a charmed enemy while damaging self
                if (ch.alignment == Char.Alignment.ALLY || ch.buff(Charm.class) != null) {
                    if (type() == 2){
                        Buff.prolong(ch, LifeLink.class, chargesPerCast()*(5 + power())).object = curUser.id();
                        Buff.prolong(curUser, LifeLink.class, chargesPerCast()*(5 + power())).object = ch.id();
                    } else {

                        // 5% of max hp
                        int selfDmg = Math.round(curUser.HT * 0.05f);

                        int healing = Math.round(selfDmg + 3 * power());
                        int shielding = (ch.HP + healing) - ch.HT;
                        if (shielding > 0) {
                            healing -= shielding;
                            Buff.affect(ch, Barrier.class).setShield(shielding);
                        } else {
                            shielding = 0;
                        }

                        ch.HP += healing;

                        ch.sprite.emitter().burst(Speck.factory(Speck.HEALING), (int) (2 + power() / 2));
                        if (healing > 0) {
                            ch.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(healing), FloatingText.HEALING);
                        }
                        if (shielding > 0) {
                            ch.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shielding), FloatingText.SHIELDING);
                        }
                    }

                    if (!freeCharge) {
                        damageHero(Math.round(curUser.HT * 0.05f));
                    } else {
                        freeCharge = false;
                    }

                    //for enemies...
                    //(or for mimics which are hiding, special case)
                } else if ((ch.alignment == Char.Alignment.ENEMY || ch instanceof Mimic) && !(Dungeon.isChallenged(Conducts.Conduct.PACIFIST))) {

                    //grant a self-shield, and...
                    int shield = (int) ((5 + power())*powerModifier(type()));
                    Buff.affect(curUser, Barrier.class).setShield(shield);
                    curUser.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shield), FloatingText.SHIELDING);

                    //charms living enemies
                    if (!ch.properties().contains(Char.Property.UNDEAD)) {
                        Charm charm = Buff.affect(ch, Charm.class, (Charm.DURATION / 2f)*powerModifier(type()));
                        charm.object = curUser.id();
                        charm.ignoreHeroAllies = true;
                        ch.sprite.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 3);

                        //harms the undead
                    } else {
                        ch.damage(damageRoll(), this);
                        ch.sprite.emitter().start(ShadowParticle.UP, 0.05f, (int) (10 + power()));
                        Sample.INSTANCE.play(Assets.Sounds.BURNING);
                    }

                }
            }
			
		}
		
	}

	//this wand costs health too
	private void damageHero(int damage){
		
		curUser.damage(damage, this);

		if (!curUser.isAlive()){
			Badges.validateDeathFromFriendlyMagic();
			Dungeon.fail( this );
			GLog.n( Messages.get(this, "ondeath") );
		}
	}

	@Override
	public void onHit(Char attacker, Char defender, int damage) {
        if (type() == 1 || type() == 2) {
            if (defender.buff(Charm.class) != null && defender.buff(Charm.class).object == attacker.id()) {
                //grants a free use of the staff and shields self
                freeCharge = true;
                int shieldToGive = Math.round((2 * (5 + power())) * procChanceMultiplier(attacker));
                Buff.affect(attacker, Barrier.class).setShield(shieldToGive);
                attacker.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shieldToGive), FloatingText.SHIELDING);
                GLog.p(Messages.get(this, "charged"));
                attacker.sprite.emitter().burst(BloodParticle.BURST, 20);
            }
        }
	}

    @Override
    public String battlemageDesc(int type) {
        if (type() != 3){
            return Messages.get(this, "type_bm" + type, Math.round((2 * (5 + power()))));
        }
        return super.battlemageDesc(type);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (attacker instanceof Hero && ((Hero)attacker).subClass == HeroSubClass.BATTLEMAGE && type() == 3){
            float difference = Math.abs(attacker.HP * 1f / attacker.HT - defender.HP * 1f / defender.HT);
            damage = Math.round(damage*(1f + difference));
        }
        return super.proc(attacker, defender, damage);
    }

    @Override
	public void fx(Ballistica beam, Callback callback) {
		curUser.sprite.parent.add(
				new Beam.HealthRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));
		callback.call();
	}

	@Override
	public void staffFx(WandParticle particle) {
		particle.color( 0xCC0000 );
		particle.am = 0.6f;
		particle.setLifespan(1f);
		particle.speed.polar( Random.Float(PointF.PI2), 2f );
		particle.setSize( 1f, 2f);
		particle.radiateXY(0.5f);
	}

	@Override
	public String statsDesc() {
		int selfDMG = Dungeon.hero != null ? Math.round(Dungeon.hero.HT*0.05f): 1;
        switch (type()){
            case 2: return Messages.get(this, "stats_desc" + type(), selfDMG, (int)(5+power())*chargesPerCast(), (int)(Charm.DURATION*powerModifier(type())), (int)((5+power())*powerModifier(type())), GameMath.printAverage((int) magicMin(), (int) magicMax()));
        }
        return Messages.get(this, "stats_desc" + type(), selfDMG, selfDMG + (int)(3*power()), (int)(5+power()), GameMath.printAverage((int) magicMin(), (int) magicMax()));
	}

    @Override
    public String generalTypeDescription(int type) {
        if (type == 2){
            return Messages.get(this, "type" + type,
                    GameMath.printAverage(
                            Math.round(magicMin(power())*powerModifier(type)),
                            Math.round(magicMax(power())*powerModifier(type))
                    ),
                    getRechargeInfo(type),
                    (int)(Charm.DURATION*powerModifier(type)),
                    (int)((5+power())*powerModifier(type)),
                    Dungeon.hero != null ? Dungeon.hero.HT / 20 : 1,
                    (int)((5+power())*chargesPerCast())
            );
        }
        return Messages.get(this, "type" + type,
                GameMath.printAverage(
                        Math.round(magicMin(power())*powerModifier(type)),
                        Math.round(magicMax(power())*powerModifier(type))
                ),
                getRechargeInfo(type),
                (int)(Charm.DURATION*powerModifier(type)),
                (int)((5+power())*powerModifier(type)),
                Dungeon.hero != null ? Dungeon.hero.HT / 20 : 1,
                (int)((Dungeon.hero != null ? Dungeon.hero.HT / 20f : 1) + 3 * power())
        );
    }

    @Override
	public String upgradeStat1(int level) {
		int selfDMG = Dungeon.hero != null ? Math.round(Dungeon.hero.HT*0.05f): 1;
		return Integer.toString(selfDMG + 3*level);
	}

	@Override
	public String upgradeStat2(int level) {
		return Integer.toString(5 + level);
	}

	@Override
	public String upgradeStat3(int level) {
		return super.upgradeStat1(level); //damage
	}

	private static final String FREECHARGE = "freecharge";

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		freeCharge = bundle.getBoolean( FREECHARGE );
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( FREECHARGE, freeCharge );
	}

}
