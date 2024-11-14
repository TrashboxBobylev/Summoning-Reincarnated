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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.WhiteWound;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class ToyKnife extends MeleeWeapon {

    public boolean ranged;
	
	{
		image = ItemSpriteSheet.TOY_KNIFE;

		tier = 1;

		bones = false;

		defaultAction = AC_THROW;
		usesTargeting = true;

	}

    @Override
    public int max(int lvl) {
        return  7*(tier) + ((1 + (lvl+1)) / 2)*lvl;

    }

//    @Override
//    public int image() {
//	    switch (buffedLvl()){
//            case 0: case 1: case 2: case 3:
//                return ItemSpriteSheet.KNIFE;
//            case 4: case 5: case 6:
//                return ItemSpriteSheet.KNIVE_MK2;
//            case 7: case 8: case 9:
//                return ItemSpriteSheet.KNIVE_MK3;
//            default:
//                return ItemSpriteSheet.KNIVE_MK4;
//        }
//    }

//    @Override
//    public String name() {
//        String name;
//        switch (buffedLvl()){
//            case 0: case 1: case 2: case 3:
//                name = Messages.get(this, "name");
//                break;
//            case 4: case 5: case 6:
//                name = Messages.get(this, "name2");
//                break;
//            case 7: case 8: case 9:
//                name = Messages.get(this, "name3");
//                break;
//            default:
//                name = Messages.get(this, "name4");
//                break;
//        }
//        return enchantment != null && (cursedKnown || !enchantment.curse()) ? enchantment.name( name ) : name;
//    }

//    @Override
//    public String desc() {
//        switch (buffedLvl()){
//            case 0: case 1: case 2: case 3:
//                return Messages.get(this, "desc");
//            case 4: case 5: case 6:
//                return Messages.get(this, "desc2");
//            case 7: case 8: case 9:
//                return Messages.get(this, "desc3");
//            default:
//                return Messages.get(this, "desc4");
//        }
//    }


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

    @Override
    public int proc(Char attacker, Char defender, int damage ) {
	    int modifier = ranged ? 7 : 4;
        Buff.prolong( defender, SoulGain.class, /*speedModifier(attacker) **/ modifier );
        WhiteWound.hit(defender);
        return super.proc( attacker, defender, damage );
    }

    @Override
    public void onThrow(int cell) {
	    Dungeon.quickslot.convertToPlaceholder(this);
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

    public static class SoulGain extends FlavourBuff{
        @Override
        public void fx(boolean on) {
            if (on) target.sprite.add(CharSprite.State.SPIRIT);
            else target.sprite.remove(CharSprite.State.SPIRIT);
        }
    }
}
