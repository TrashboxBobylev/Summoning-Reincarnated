/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2022 Evan Debenham
 *
 * Summoning Pixel Dungeon
 * Copyright (C) 2019-2022 TrashboxBobylev
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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.utils.Random;

public class HealGas extends Blob {

	@Override
	protected void evolve() {
		super.evolve();

		Char ch;
		int cell;

		for (int i = area.left; i < area.right; i++){
			for (int j = area.top; j < area.bottom; j++){
				cell = i + j*Dungeon.level.width();
				if (cur[cell] > 0 && (ch = Actor.findChar( cell )) != null) {
					if (!ch.isImmune(Healing.class) || (ch instanceof Hero && !Dungeon.isChallenged(Challenges.NO_HEALING))) {
						int heal = 1 + ch.HT / 50;
						if (ch.HP < ch.HT)
							ch.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(heal), FloatingText.HEALING);
                        ch.HP = Math.min(ch.HT, ch.HP + heal);
					} else {

                        int damage = (ch.HT + 5 + Dungeon.chapterNumber()) / 40;
                        if (Random.Int( 40 ) < (ch.HT + 5 + Dungeon.chapterNumber()) % 40) {
                            damage++;
                        }

                        ch.damage(damage, this);
                    }
				}
			}
		}
	}

	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );

		emitter.pour( Speck.factory( Speck.HEALGAS, true ), 0.4f );
	}

	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
}