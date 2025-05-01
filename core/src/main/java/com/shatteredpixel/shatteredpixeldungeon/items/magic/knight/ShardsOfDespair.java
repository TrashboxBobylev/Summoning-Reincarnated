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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulParalysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.GoatClone;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.AdHocSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.ToyKnife;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.text.DecimalFormat;
import java.util.HashMap;

public class ShardsOfDespair extends AdHocSpell {

    {
        image = ItemSpriteSheet.SPREAD;
        alignment = Alignment.OFFENSIVE;
    }

    private HashMap<Callback, Mob> targets = new HashMap<>();

    @Override
    public boolean effect(Hero hero) {
        for (Mob mob : Dungeon.level.mobs) {
            if (Dungeon.level.distance(curUser.pos, mob.pos) <= (isEmpowered() ? 10000 : 8)
                    && Dungeon.level.heroFOV[mob.pos]
                    && mob.alignment != Char.Alignment.ALLY) {

                Callback callback = new Callback() {
                    @Override
                    public void call() {
                        Mob ch = targets.get(this);
                        if (ch != null) {
                            Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC);
                            Buff.affect(ch, ToyKnife.SoulGain.class, buff(rank()));
                            Buff.affect(ch, Minion.ReactiveTargeting.class, 10f);
                            if (level() >= 1) {
                                ch.damage(damage(rank()), hero);
                                GoatClone clone = GoatClone.findClone();
                                if (clone != null) {
                                    ch.aggro(clone);
                                }
                                if (rank() >= 3){
                                    Buff.prolong(ch, SoulParalysis.class, 6f);
                                }
                            }
                            if (isEmpowered()){
                                // casts positive cursed effects
                                Ballistica bolt = new Ballistica(ch.pos, ch.pos, Ballistica.WONT_STOP);
                                CursedWand.randomValidEffect(null, hero, bolt, true).effect(null, hero, bolt, true);
                            }
                        }
                        targets.remove( this );
                        if (targets.isEmpty()) {
                            Invisibility.dispel();
                            hero.spendAndNext( hero.attackDelay() );
                        }
                    }
                };

                MagicMissile.boltFromChar( curUser.sprite.parent,
                        MagicMissile.CRYSTAL_SHARDS,
                        curUser.sprite,
                        mob.pos,
                        callback);

                targets.put( callback, mob );
            }
        }

        if (targets.size() == 0) {
            GLog.warning( Messages.get(this, "no_enemies") );
            return false;
        }

        curUser.sprite.zap( curUser.pos );
        curUser.busy();
        return true;
    }

    private float buff(int rank){
        switch (rank){
            case 1: return 12.0f;
            case 2: return 20.0f;
            case 3: return 5.0f;
        }
        return 0f;
    }

    private int minDamage(int rank){
        switch (rank){
            case 2: return 6 + heroLvl()*2/5;
            case 3: return 12 + heroLvl()*4/5;
        }
        return 0;
    }

    private int maxDamage(int rank){
        switch (rank){
            case 2: return 16 + heroLvl()*3/4;
            case 3: return 32 + heroLvl()*3/2;
        }
        return 0;
    }

    private int damage(int rank){
        switch (rank){
            case 2: case 3: return Random.NormalIntRange(minDamage(rank), maxDamage(rank));
        }
        return 0;
    }

    @Override
    public int manaCost(int rank) {
        int manaCost = 0;
        switch (rank){
            case 1: manaCost = 8; break;
            case 2: manaCost = 15; break;
            case 3: manaCost = 35; break;
        }
        if (isEmpowered())
            manaCost *= 2;
        return manaCost;
    }

    @Override
    public String spellDesc() {
        return Messages.get(this, "desc", new DecimalFormat("#.#").format(buff(rank())));
    }

    @Override
    public String spellRankMessage(int rank) {
        return Messages.get(this, "rank"+ (rank == 3 ? "3" : ""), new DecimalFormat("#.#").format(buff(rank)), minDamage(rank), maxDamage(rank));
    }

}
