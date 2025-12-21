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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invulnerability;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.generic.InescapableDamage;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

import java.text.DecimalFormat;

public class ShockerBreaker extends ConjurerSpell {

    {
        image = ItemSpriteSheet.SHOCKER;
        alignment = Alignment.BENEFICIAL;
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
        if (ch != null && ch.alignment == Char.Alignment.ALLY && !(ch instanceof Hero)){
            Sample.INSTANCE.play(Assets.Sounds.ZAP);
            Sample.INSTANCE.play(Assets.Sounds.HEALTH_WARN);
            ch.damage((int) (ch.HT * dmg(type())), new NoHeal());
            Camera.main.shake(4f, 0.4f);
            GameScene.flash(0xFFFFFF);
            Buff.affect(ch, Empowered.class, buff(type()));
            Buff.affect(ch, Haste.class, buff(type()));
            Buff.affect(ch, Adrenaline.class, buff(type()));
            Buff.affect(ch, Bless.class, buff(type()));
            if (type() == 2){
                Buff.affect(ch, Invulnerability.class, buff(type()));
            }
            Buff.affect(ch, NoHeal.class, noheal(type()));

            ch.sprite.burst(0xFFFFFFFF, buffedLvl() / 2 + 2);
        }
    }

    private float dmg(int rank){
        if (isEmpowered()){
            return 0f;
        }
        switch (rank){
            case 1: return 0.5f;
            case 2: return 0.95f;
            case 3: return 0.25f;
        }
        return 0f;
    }

    @Override
    public int manaCost(int type) {
        switch (type){
            case 1: return 9;
            case 2: return 12;
            case 3: return 24;
        }
        return 0;
    }

    @Override
    public int manaCost() {
        int manaCost = super.manaCost();
        if (isEmpowered()){
            manaCost *= 2;
        }
        return manaCost;
    }

    private int noheal(int rank){
        switch (rank){
            case 1: return 50;
            case 2: return 1500;
            case 3: return 40;
        }
        return 0;
    }

    private int buff(int rank){
        switch (rank){
            case 1: return 20;
            case 2: return 10;
            case 3: return 40;
        }
        return 0;
    }


    @Override
    public String spellDesc() {
        return Messages.get(this, "desc" + type(),
                new DecimalFormat("#.##").format(dmg(type())*100), buff(type()), noheal(type()));
    }

    @Override
    public String spellTypeMessage(int type) {
        return Messages.get(this, "type" + type,
                new DecimalFormat("#.##").format(dmg(type)*100), buff(type), noheal(type));
    }

    public static class NoHeal extends FlavourBuff implements InescapableDamage {

    }
}
