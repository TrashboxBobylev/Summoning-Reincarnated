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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.ShieldHalo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Lucky;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class ThrowingKnive2 extends MissileWeapon {
	
	{
		image = ItemSpriteSheet.LIGHT_KNIFE;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1f;
	}

    public float min(float lvl, int type) {
        switch (type){
            case 1: return 2 + lvl*0.75f;
            case 2: return 3 + lvl*1;
            case 3: return 24 + lvl*5f;
        }
        return 0;
    }

    public float max(float lvl, int type) {
        switch (type){
            case 1: return 6 + lvl*3f;
            case 2: return 8 + lvl*3.5f;
            case 3: return 48 + lvl*11f;
        }
        return 0;
    }

    public float baseUses(float lvl, int type){
        switch (type){
            case 1: return 9 + lvl*1f;
            case 2: return 5 + lvl*0.75f;
            case 3: return 1;
        }
        return 1;
    }

    @Override
    public float durabilityPerUse(float level) {
        if (type() == 3){
            return MAX_DURABILITY;
        }
        return super.durabilityPerUse(level);
    }

    @Override
    protected String generalTypeMessage(int type) {
        if (type == 3){
            return Messages.get(this, "type3",
                    GameMath.printAverage(Math.round(min(powerLevel(), type)), Math.round(max(powerLevel(), type))),
                    Math.round(baseUses(powerLevel(), type))
            );
        }
        return super.generalTypeMessage(type);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        int dmg = super.proc(attacker, defender, damage);
        if (type() == 2)
            Buff.affect(defender, Lucky.LuckProc.class).ringLevel = -7;
        if (type() == 3){
            ArrayList<Char> targets = new ArrayList<>();
            for (int i : PathFinder.NEIGHBOURS8){
                if (Actor.findChar(defender.pos+ i) != null) targets.add(Actor.findChar(defender.pos + i));
            }
            ShieldHalo shield;
            GameScene.effect(shield = new ShieldHalo(defender.sprite));
            shield.putOut();
            for (Char target : targets) {
                target.damage(dmg, this);
            }
        }
        return dmg;
    }

    @Override
    public float powerLevel() {
        return Math.max(0, super.powerLevel()-2/1.5f);
    }
}
