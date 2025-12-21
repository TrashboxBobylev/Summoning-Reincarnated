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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.FrostFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostBurn;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.ConjurerSpell;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

import java.text.DecimalFormat;

public class AntarcticTouch extends ConjurerSpell {

    {
        image = ItemSpriteSheet.SR_OFFENSE;
        alignment = Alignment.OFFENSIVE;
        usesTargeting = true;
    }

    @Override
    public void effect(Ballistica trajectory) {
        Char ch = Actor.findChar(trajectory.collisionPos);

        if (ch != null){
            Buff.affect(ch, FrostBurn.class).reignite(ch, frostburn(type()));
            if (isEmpowered()){
                for (int offset : PathFinder.NEIGHBOURS9){
                    if (!Dungeon.level.solid[trajectory.collisionPos+offset]) {
                        GameScene.add(Blob.seed(trajectory.collisionPos + offset, Math.round(frostburn(type())/3), FrostFire.class));
                    }
                }
            }
            Buff.affect(ch, Minion.ReactiveTargeting.class, 10f);
            Buff.affect(ch, Minion.UniversalTargeting.class, 15f);
            for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                if (mob instanceof Minion && type() < 3){
                    mob.aggro(ch);
                    mob.beckon(trajectory.collisionPos);
                }
            }
        }

        Heap heap = Dungeon.level.heaps.get(trajectory.collisionPos);
        if (heap != null) {
            heap.freeze();
        }

        Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);
        if (fire != null && fire.volume > 0) {
            fire.clear( trajectory.collisionPos );
        }

        MagicalFireRoom.EternalFire eternalFire = (MagicalFireRoom.EternalFire)Dungeon.level.blobs.get(MagicalFireRoom.EternalFire.class);
        if (eternalFire != null && eternalFire.volume > 0) {
            eternalFire.clear( trajectory.collisionPos );
            //bolt ends 1 tile short of fire, so check next tile too
            if (trajectory.path.size() > trajectory.dist+1){
                eternalFire.clear( trajectory.path.get(trajectory.dist+1) );
            }

        }
    }

    private float frostburn(int rank){
        switch (rank){
            case 1: return 7f;
            case 2: return 15f;
            case 3: return 1000f;
        }
        return 0f;
    }

    @Override
    public int manaCost(int type) {
        switch (type){
            case 1: return 5;
            case 2: return 10;
            case 3: return 20;
        }
        return 0;
    }

    public String spellDesc() {
        return Messages.get(this, "desc", new DecimalFormat("#.#").format(frostburn(type())));
    }

    @Override
    public String empowermentDesc() {
        return Messages.get(this, "desc_empower", new DecimalFormat("#.#").format(frostburn(type())/3));
    }

    @Override
    public String spellTypeMessage(int type) {
        return Messages.get(this, "type"+ (type == 3 ? "3" : ""), new DecimalFormat("#.#").format(frostburn(type)), manaCost());
    }

    @Override
    public String empowermentTypeDesc(int type) {
        return Messages.get(this, "type_empower", new DecimalFormat("#.#").format(frostburn(type())/3));
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar(curUser.sprite.parent,
                MagicMissile.FROST,
                curUser.sprite,
                bolt.collisionPos,
                callback);
    }
}
