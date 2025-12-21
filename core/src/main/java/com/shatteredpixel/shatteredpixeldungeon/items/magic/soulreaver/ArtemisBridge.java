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

package com.shatteredpixel.shatteredpixeldungeon.items.magic.soulreaver;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Empowered;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WhiteParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.ConjurerSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class ArtemisBridge extends ConjurerSpell {

    {
        image = ItemSpriteSheet.SR_RANGED;
        alignment = Alignment.BENEFICIAL;
        collision = Ballistica.STOP_TARGET;
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        callback.call();
    }

    @Override
    public boolean validateCell(int pos){
        Char ch = Actor.findChar(pos);
        if (!(ch != null && ch.alignment != Char.Alignment.ALLY)){
            GLog.i( Messages.get(this, "no_minion"));
            return false;
        } else {
            int chPos = ch.pos;
            ArrayList<Integer> possibleCells = new ArrayList<>();
            for (int dir: PathFinder.NEIGHBOURS8) {
                if (!(Actor.findChar(chPos + dir) != null ||
                        !Dungeon.level.passable[chPos + dir])) {
                    possibleCells.add(chPos + dir);
                }
            }
            if (possibleCells.isEmpty()){
                GLog.i( Messages.get(this, "solid"));
                return false;
            } else {
                for (int dir: PathFinder.NEIGHBOURS8) {
                    if (Dungeon.level.distance(chPos + dir, Dungeon.hero.pos) > distance(type())){
                        possibleCells.remove((Integer)(chPos + dir));
                    }
                }
                if (possibleCells.isEmpty()) {
                    GLog.i(Messages.get(this, "too_far"));
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void effect(Ballistica trajectory) {
        Char ch = Actor.findChar(trajectory.collisionPos);
        if (ch instanceof Minion){
            int pos = ch.pos;
            for (int dir: PathFinder.NEIGHBOURS8){
                if (Actor.findChar(pos + dir) == null &&
                        Dungeon.level.passable[pos+dir]    &&
                        Dungeon.level.distance(pos + dir, Dungeon.hero.pos) <= distance(type())){
                    curUser.busy();
                    curUser.sprite.emitter().burst(WhiteParticle.UP, 8);
                    curUser.sprite.operate(curUser.pos, new Callback() {
                        @Override
                        public void call() {
                            curUser.sprite.parent.add(
                                    new Beam.LightRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(pos+dir)));
                            ScrollOfTeleportation.appear(Dungeon.hero, pos+dir);
                            Dungeon.hero.sprite.idle();
                            Dungeon.hero.pos = pos+dir;
                            Dungeon.observe();
                            GameScene.updateFog();
                            if (isEmpowered()){
                                Buff.affect(ch, Empowered.class, 3f);
                            }
                        }
                    });
                }
            }
        }
    }

    private int distance(int rank){
        switch (rank){
            case 1: return 6;
            case 2: return 13;
            case 3: return 1000;
        }
        return 0;
    }

    @Override
    public int manaCost(int type) {
        switch (type){
            case 1: return 3;
            case 2: return 5;
            case 3: return 10;
        }
        return 0;
    }

    public String spellDesc() {
        return Messages.get(this, "desc", distance(type()));
    }

    @Override
    public String spellTypeMessage(int type) {
        return Messages.get(this, "type", distance(type));
    }
}
