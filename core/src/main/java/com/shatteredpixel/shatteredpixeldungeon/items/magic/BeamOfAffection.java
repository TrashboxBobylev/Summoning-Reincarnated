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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Fury;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Stamina;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class BeamOfAffection extends ConjurerSpell {

    {
        image = ItemSpriteSheet.ZAP;
    }

    @Override
    public void effect(Ballistica trajectory) {
        Char ch = Actor.findChar(trajectory.collisionPos);
        if (ch != null){
            if (ch instanceof Minion){
                if (rank() != 3)
                    ch.die( curUser );
                if (rank() == 2){
                    int gain = 18;
                    gain = Math.min(Dungeon.hero.maxMana() - Dungeon.hero.mana, gain);
                    Dungeon.hero.mana += gain;
                    if (gain > 0) {
                        Dungeon.hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(gain), FloatingText.MANA);
                    }
                } else if (rank() == 3){
                    CellEmitter.center(ch.pos).start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
                    Buff.affect(ch, Fury.class);
                }
            } else {
                ch.damage(0, curUser);
                Buff.affect(ch, Minion.UniversalTargeting.class, 5f);
                if (level() == 1){
                    Buff.affect(ch, Weakness.class, 10f);
                    Buff.affect(ch, Hex.class, 10f);
                    Buff.affect(ch, Vulnerable.class, 10f);
                } else if (level() == 2){
                    Buff.affect(ch, Minion.UniversalTargeting.class, 15f);
                    for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )){
                        if (mob instanceof Minion){
                            mob.enemy = ch;
                            mob.enemySeen = true;
                            mob.aggro(ch);
                            mob.state = mob.HUNTING;
                            mob.notice();
                            mob.beckon(ch.pos);
                            Buff.affect(mob, Haste.class, 4f);
                            Buff.affect(mob, Stamina.class, 4f);
                        }
                    }
                }
            }
        }
    }

    @Override
    public int manaCost(int rank) {
        switch (rank){
            case 1: return 0;
            case 2: return 6;
            case 3: return 9;
        }
        return 0;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc" + level());
    }

    @Override
    protected void fx( Ballistica beam, Callback callback ) {
        curUser.sprite.parent.add(
                new Beam.LightRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));
        Sample.INSTANCE.play( Assets.Sounds.RAY );
        callback.call();
    }
}
