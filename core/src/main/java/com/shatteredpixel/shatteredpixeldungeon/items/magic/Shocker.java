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

package com.shatteredpixel.shatteredpixeldungeon.items.magic;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Empowered;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

import java.text.DecimalFormat;

public class Shocker extends ConjurerSpell {

    {
        image = ItemSpriteSheet.SHOCKER;
    }

    @Override
    public void effect(Ballistica trajectory) {
        Char ch = Actor.findChar(trajectory.collisionPos);
        shock(ch);
        if (level() == 2){
            for (int i: PathFinder.NEIGHBOURS8){
                ch = Actor.findChar(trajectory.collisionPos + i);
                shock(ch);
            }
        }
    }

    private void shock(Char ch) {
        if (ch != null && ch.alignment == Char.Alignment.ALLY){
            Sample.INSTANCE.play(Assets.Sounds.ZAP);
            Sample.INSTANCE.play(Assets.Sounds.HEALTH_WARN);
            ch.damage((int) (ch.HT * dmg(rank())), new Grim());
            Camera.main.shake(4f, 0.4f);
            GameScene.flash(0xFFFFFF);
            Buff.affect(ch, Empowered.class, buff(rank()));
            Buff.affect(ch, Haste.class, buff(rank()));
            Buff.affect(ch, Adrenaline.class, buff(rank()));
            Buff.affect(ch, Bless.class, buff(rank()));
            Buff.affect(ch, NoHeal.class, noheal(rank()));

            ch.sprite.burst(0xFFFFFFFF, buffedLvl() / 2 + 2);
        }
    }

    private float dmg(int rank){
        switch (rank){
            case 1: return 0.5f;
            case 2: return 0.25f;
            case 3: return 0.5f;
        }
        return 0f;
    }

    @Override
    public int manaCost(int rank) {
        switch (rank){
            case 1: return 15;
            case 2: return 25;
            case 3: return 35;
        }
        return 0;
    }

    private int noheal(int rank){
        switch (rank){
            case 1: return 50;
            case 2: return 30;
            case 3: return 40;
        }
        return 0;
    }

    private int buff(int rank){
        switch (rank){
            case 1: return 20;
            case 2: return 15;
            case 3: return 10;
        }
        return 0;
    }


    @Override
    public String desc() {
        return Messages.get(this, "desc" + (rank() == 3 ? "3" : ""),
                new DecimalFormat("#.##").format(dmg(rank()*100)), buff(rank()), noheal(rank()), manaCost());
    }

    @Override
    public String spellRankMessage(int rank) {
        return Messages.get(this, "rank" + (rank == 3 ? "3" : ""),
                new DecimalFormat("#.##").format(dmg(rank*100)), buff(rank), noheal(rank));
    }

    public static class NoHeal extends FlavourBuff {

    }
}
