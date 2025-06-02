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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.conjurer.Ascension;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.WhiteWound;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Rankable;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class ToyKnife extends MeleeWeapon implements Rankable {

    public boolean ranged;
	
	{
		image = ItemSpriteSheet.TOY_KNIFE;

		tier = 1;

		bones = false;

		defaultAction = AC_THROW;
		usesTargeting = true;

        //technically not, but allows to properly placeholder it for thrown action
        stackable = true;

	}

    @Override
    public int min(int lvl) {
        return Math.round(damageMod(rank())* (1 + 2*lvl));
    }

    @Override
    public int max(int lvl) {
        return Math.round(damageMod(rank())* (7*(tier) + 4*lvl));
    }

    @Override
    public int level() {
        int lvl = super.level() + (Dungeon.hero != null ? Dungeon.hero.ATU() : 1) - 1;
        if (Dungeon.hero != null && Dungeon.hero.buff(Ascension.AscendBuff.class) != null && Dungeon.hero.hasTalent(Talent.EGOISM)) {
            lvl += 2;
        }
        return lvl;
    }

    @Override
    public int buffedLvl() {
        return level();
    }

    @Override
    public int STRReq(int lvl) {
        return super.STRReq(0);
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    public float damageMod(int rank){
        switch (rank){
            case 1: return 1.0f;
            case 2: return 0.75f;
            case 3: return 2f;
        }
        return 1f;
    }

    public float delayMod(int rank){
        switch (rank){
            case 1: return 1.0f;
            case 2: return 1.0f;
            case 3: return 2.0f;
        }
        return 1f;
    }

    @Override
    protected float baseDelay(Char owner) {
        return super.baseDelay(owner)*delayMod(rank());
    }

    @Override
    public int damageRoll(Char owner) {
        int damageRoll = super.damageRoll(owner);
        if (owner instanceof Hero){
            Hero hero = (Hero)owner;
            Char enemy = hero.enemy();
            if (enemy instanceof Mob && hero.buff(Talent.EnergyBreakTracker.class) != null &&
                hero.buff(Talent.EnergyBreakTracker.class).object == enemy.id()){
                hero.buff(Talent.EnergyBreakTracker.class).detach();
                damageRoll += Random.NormalIntRange(1, 3) + hero.pointsInTalent(Talent.ENERGY_BREAK);
            }
        }
        return damageRoll;
    }

    public float soulGainMod(int rank){
        switch (rank){
            case 1: return 1.0f;
            case 2: return 0.75f;
            case 3: return 1.5f;
        }
        return 1f;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage ) {
	    int modifier = ranged ? 8 : 4;
        modifier *= soulGainMod(rank);
        if (rank() == 2)
            modifier *= 1.40f;
        Buff.prolong( defender, SoulGain.class, /*speedModifier(attacker) **/ modifier );
        WhiteWound.hit(defender);
        return super.proc( attacker, defender, damage );
    }

    @Override
    public int throwPos(Hero user, int dst) {

        if (hasEnchant(Projecting.class, user)
                && (Dungeon.level.passable[dst] || Dungeon.level.avoid[dst] || Actor.findChar(dst) != null)
                && Dungeon.level.distance(user.pos, dst) <= Math.round(4 * Enchantment.genericProcChanceMultiplier(user))){
            return dst;
        } else {
            return new Ballistica( user.pos, dst, Ballistica.FRIENDLY_PROJECTILE ).collisionPos;
        }
    }

    @Override
    public void onThrow(int cell) {
        Char enemy = Actor.findChar(cell);
        if (enemy == null || enemy == curUser) {
            super.onThrow(cell);
        } else {
            if (!curUser.shoot(enemy, this)) {
                super.onThrow(cell);
            } else {
                Dungeon.level.drop( this, cell ).sprite.drop();
            }
            processSoulsBurst(this, cell);
        }
    }

    @Override
    public float speedMultiplier(Char owner) {
        float v = super.speedMultiplier(owner);
        if (Dungeon.hero.subClass == HeroSubClass.WILL_SORCERER) v /= 0.7f;
        return v;
    }

    @Override
    public float castDelay(Char user, int dst) {
        return super.castDelay(user, dst)*augment.delayFactor(delayMod(rank()));
    }

    public static void processSoulsBurst(Item source, int pos){
        if (Dungeon.hero.hasTalent(Talent.SOULS_BURST)){
            Class<? extends Item> sample = Dungeon.hero.heroClass == HeroClass.CONJURER ? ToyKnife.class : Wand.class;
            if (sample.isInstance(source)){
                ToyKnife damageSource = source instanceof ToyKnife ? (ToyKnife) source : new ToyKnife();
                int damage = Math.round(damageSource.damageRoll(Dungeon.hero)*(1 + Dungeon.hero.pointsInTalent(Talent.SOULS_BURST)/2f));
                Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f) );
                for (int i : PathFinder.NEIGHBOURS8){
                    ((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
                            MagicMissile.MAGIC_MISSILE,
                            pos,
                            pos+i,
                            null
                    );
                    Char target;
                    if ((target = Actor.findChar(pos+i)) != null && target.alignment == Char.Alignment.ENEMY){
                        target.damage(damage, damageSource);
                        if (source instanceof ToyKnife){
                            Buff.prolong(target, SoulGain.class, 2 + 4*Dungeon.hero.pointsInTalent(Talent.SOULS_BURST));
                        }
                    }
                }
            }
        }
    }

    @Override
    public int reachFactor(Char owner) {
        int reachFactor = super.reachFactor(owner);
        if (Dungeon.hero.buff(Ascension.AscendBuff.class) != null && Dungeon.hero.hasTalent(Talent.EGOISM)){
            reachFactor += 2;
        }
        return reachFactor;
    }

    int rank = 1;

    @Override
    public int rank() {
        return rank;
    }

    @Override
    public void rank(int rank) {
        this.rank = rank;
    }

    @Override
    public String getRankMessage(int rank){
        String rankMessage = generalRankMessage(rank);
        if (!Messages.get(this, "rank" + rank).equals(Messages.NO_TEXT_FOUND))
            rankMessage += "\n\n" + Messages.get(this, "rank" + rank);
        return rankMessage;
    }

    protected String generalRankMessage(int rank) {
        return Messages.get(this, "rank_info",
                Math.round(damageMod(rank)* (1 + 2*buffedLvl())), Math.round(damageMod(rank)* (7*(tier) + 4*buffedLvl())),
                Math.round(4*soulGainMod(rank)), Math.round(8*soulGainMod(rank)*(rank == 2 ? 1.40f : 1f)),
                Messages.decimalFormat("#.##", 1.0f/delayMod(rank))
        );
    }

    private static final String RANK = "rank";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(RANK, rank);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        rank = bundle.getInt(RANK);
    }

    public static class SoulGain extends FlavourBuff{
        @Override
        public void fx(boolean on) {
            if (on) target.sprite.add(CharSprite.State.SPIRIT);
            else target.sprite.remove(CharSprite.State.SPIRIT);
        }
    }
}
