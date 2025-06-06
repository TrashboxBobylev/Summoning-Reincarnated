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

package com.shatteredpixel.shatteredpixeldungeon.items.magic.knight;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MonkEnergy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WhiteParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.AdHocSpell;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

import java.text.DecimalFormat;

public class Concentration extends AdHocSpell {
    {
        image = ItemSpriteSheet.HEALKI;
        alignment = Alignment.BENEFICIAL;
    }

    @Override
    public boolean effect(Hero hero) {
        hero.sprite.operate(hero.pos, new Callback() {
            @Override
            public void call() {
                hero.spendAndNext(paralyse(rank()));
                hero.sprite.idle();
                hero.sprite.emitter().burst(WhiteParticle.UP, 8);
                Sample.INSTANCE.play(Assets.Sounds.LULLABY);
                Regeneration.regenerate(hero, intHeal(rank()));
                new Flare(10, 64).color(0xFFFFFF, true).show(Dungeon.hero.sprite.parent, DungeonTilemap.tileCenterToWorld(hero.pos), 1.5f);
                if (isEmpowered())
                    Buff.affect(hero, MonkEnergy.MonkAbility.Meditate.MeditateResistance.class, hero.cooldown());
            }
        });
        hero.busy();
        return true;
    }

    private int intHeal(int rank){
//        if (Dungeon.mode == Dungeon.GameMode.HELL){
//            return 0;
//        }
        switch (rank){
            case 1: return 20;
            case 2: return 35;
            case 3: return 60;
        }
        return 0;
    }

    private float paralyse(int rank){
        switch (rank){
            case 1: return 3f;
            case 2: return 6f;
            case 3: return 15f;
        }
        return 0f;
    }

    @Override
    public int manaCost(int rank) {
        return 15;
    }

    public String spellDesc() {
        return Messages.get(this, "desc", intHeal(rank()), new DecimalFormat("#.#").format(paralyse(rank())));
    }

    @Override
    public String spellRankMessage(int rank) {
        return Messages.get(this, "rank", intHeal(rank), new DecimalFormat("#.#").format(paralyse(rank)));
    }
}
