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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Shrink;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class Tomahawk extends MissileWeapon {

	{
		image = ItemSpriteSheet.TOMAHAWK;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 0.9f;
	}

    public float min(float lvl, int rank) {
        switch (rank){
            case 1: return 3 + lvl*0.75f;
            case 2: return 4 + lvl*2f;
            case 3: return 2 + lvl;
        }
        return 0;
    }

    public float max(float lvl, int rank) {
        switch (rank){
            case 1: return 7 + lvl*2f;
            case 2: return 10 + lvl*3.5f;
            case 3: return 8 + lvl*1.75f;
        }
        return 0;
    }

    public float baseUses(float lvl, int rank){
        switch (rank){
            case 1: return 5 + lvl*1.5f;
            case 2: return 6 + lvl*2f;
            case 3: return 4 + lvl*1f;
        }
        return 1;
    }

    @Override
    protected float adjacentAccFactor(Char owner, Char target) {
        if (rank() == 2){
            if (Dungeon.level.adjacent( owner.pos, target.pos )) {
                if (owner instanceof Hero){
                    return (1.5f + 0.6f*((Hero) owner).pointsInTalent(Talent.POINT_BLANK));
                } else {
                    return 1.5f;
                }
            } else {
                return 0.5f;
            }
        }
        return super.adjacentAccFactor(owner, target);
    }

    @Override
	public int proc( Char attacker, Char defender, int damage ) {
        if (rank() == 1) {
            //40% damage roll as bleed, but ignores armor and str bonus
            Buff.affect(defender, Bleeding.class).set(Math.round(augment.damageFactor(Random.NormalIntRange(min(), max())) * 0.4f));
            return super.proc( attacker, defender, damage );
        }
        if (rank() == 3 && defender.buff(Rank3TomahawkTracker.class) == null){
            int dmg = super.proc(attacker, defender, damage);
            if (defender.HP >= 2 + dmg && !Char.hasProp(defender, Char.Property.BOSS) && !Char.hasProp(defender, Char.Property.MINIBOSS)){
                ArrayList<Integer> candidates = new ArrayList<>();

                int[] neighbours = {defender.pos + 1, defender.pos - 1, defender.pos + Dungeon.level.width(), defender.pos - Dungeon.level.width()};
                for (int n : neighbours) {
                    if (!Dungeon.level.solid[n]
                            && Actor.findChar( n ) == null
                            && (Dungeon.level.passable[n] || Dungeon.level.avoid[n])
                            && (!defender.properties().contains(Char.Property.LARGE) || Dungeon.level.openSpace[n])) {
                        candidates.add( n );
                    }
                }

                if (candidates.size() > 0) {
                    Buff.affect(defender, Rank3TomahawkTracker.class);
                    Buff.affect(defender, Shrink.class);

                    Mob clone = mobsplit(defender);
                    clone.pos = Random.element( candidates );
                    clone.state = clone.HUNTING;
                    GameScene.add( clone, 0f ); //we add before assigning HP due to ascension

                    clone.HP = (defender.HP - damage) / 2;
                    Actor.add( new Pushing( clone, defender.pos, clone.pos ) );
                    Buff.affect(clone, Rank3TomahawkTracker.class);
                    Buff.affect(clone, Shrink.class);

                    Dungeon.level.occupyCell(clone);

                    defender.HP -= clone.HP;
                }
            }
            return dmg;
        }
		return super.proc( attacker, defender, damage );
	}

    @Override
    protected String distanceInfo() {
        if (rank() == 2){
            return Messages.get(Tomahawk.class, "distance2");
        }
        return super.distanceInfo();
    }

    private <MobType extends Mob> MobType mobsplit(Char target) {
        MobType clone = (MobType) Reflection.newInstance(target.getClass());
        clone.EXP = 0;
        if (target.buff( Burning.class ) != null) {
            Buff.affect( clone, Burning.class ).reignite( clone );
        }
        if (target.buff( Poison.class ) != null) {
            Buff.affect( clone, Poison.class ).set(2);
        }
        for (Buff b : target.buffs()){
            if (b.revivePersists) {
                Buff.affect(clone, b.getClass());
            }
        }
        return clone;
    }

    public static class Rank3TomahawkTracker extends Buff{}
}
