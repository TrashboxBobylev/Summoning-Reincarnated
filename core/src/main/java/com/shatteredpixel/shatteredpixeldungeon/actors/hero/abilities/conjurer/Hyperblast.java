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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.conjurer;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulParalysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.StarBlazing;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Hyperblast extends ArmorAbility {
    {
        baseChargeUse = 50f;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (Dungeon.level.heroFOV[mob.pos]
				&& mob.alignment != Char.Alignment.ALLY
                && Dungeon.level.trueDistance(mob.pos, hero.pos) < 8) {
				Buff.prolong( mob, SoulParalysis.class, 7 + 1.5f*hero.pointsInTalent(Talent.LEVENSDUUR) );
                if (hero.mana > hero.pointsInTalent(Talent.KRACHT)){
                    hero.changeMana(hero.pointsInTalent(Talent.KRACHT));
                    mob.damage(Random.NormalIntRange(15*hero.pointsInTalent(Talent.KRACHT), 30*hero.pointsInTalent(Talent.KRACHT)), new StarBlazing());
                }
                if (hero.hasTalent(Talent.VERZWAKKEN)){
                    for (int i = 0; i < 1 + hero.pointsInTalent(Talent.VERZWAKKEN)/2; i++)
                        Buff.affect(mob,
                            Random.oneOf(Weakness.class, Vulnerable.class, Cripple.class, Blindness.class,
                                    Terror.class, Vertigo.class, Hex.class, Daze.class, Slow.class, Amok.class), 8f + hero.pointsInTalent(Talent.VERZWAKKEN)*2);
                }
			}
		}

        GameScene.flash( 0x50FFFFFF );

        armor.charge -= chargeUse(hero);
        armor.updateQuickslot();
        Invisibility.dispel();
        hero.spendAndNext(Actor.TICK);
        hero.sprite.operate(hero.pos);

		hero.sprite.centerEmitter().start( ElmoParticle.FACTORY, 0.15f, 4 );
		Sample.INSTANCE.play( Assets.Sounds.BLAST, 1.0f, 0.25f );
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.LEVENSDUUR, Talent.KRACHT, Talent.VERZWAKKEN, Talent.HEROIC_ENERGY};
    }

    @Override
    public int icon() {
        return HeroIcon.HYPERBLAST;
    }
}
