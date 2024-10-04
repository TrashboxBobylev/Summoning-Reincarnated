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

package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.EffectTarget;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Halo;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class HolyBomb extends Bomb.ConjuredBomb {

	{
		image = ItemSpriteSheet.HOLY_BOMB;
		fuseDelay = 0;
	}

	@Override
	public boolean explodesDestructively() {
		return false;
	}

	@Override
	public void explode(int cell) {
		super.explode(cell);

		if (Dungeon.level.heroFOV[cell]) {
			new Flare(10, 64).show(Dungeon.hero.sprite.parent, DungeonTilemap.tileCenterToWorld(cell), 2f);
		}

		Camera.main.shake( 4, 0.175f );

		boolean[] FOV = new boolean[Dungeon.level.length()];
		Point c = Dungeon.level.cellToPoint(cell);
		ShadowCaster.castShadow(c.x, c.y, Dungeon.level.width(), FOV, Dungeon.level.losBlocking, 4);

		ArrayList<Char> affected = new ArrayList<>();

		Effect shield;
		EffectTarget effectTarget = new EffectTarget(cell);

		GameScene.effect(shield = new Effect(effectTarget.sprite));
		shield.putOut();

		for (int i = 0; i < FOV.length; i++) {
			if (FOV[i]) {
				Char ch = Actor.findChar(i);
				if (ch != null){
					affected.add(ch);
					if (Dungeon.level.heroFOV[i] && !Dungeon.level.solid[i]) {
						new Flare(8, 40).show(ch.sprite,1.5f);
					}
				}
			}
		}

		for (Char ch : affected){
			//80% bomb damage, which falls off at a rate of 15% per tile of distance after first tile
			float multiplier = Math.min(1f, Math.round((1.2f - .15f*Dungeon.level.distance(cell, ch.pos))));
			int damage = Math.round(damageRoll() * 0.75f * multiplier);
			if (ch.properties().contains(Char.Property.UNDEAD) || ch.properties().contains(Char.Property.DEMONIC)){
				ch.sprite.emitter().start( ShadowParticle.UP, 0.05f, 10 );
				//bomb deals an additional 33% damage to unholy enemies
				damage *= 1.33f;
			}
			if (ch.alignment == Char.Alignment.ALLY && !(ch instanceof Hero)){
				Buff.affect(ch, Bless.class, 25f * multiplier);
			} else {
				ch.damage(damage, new HolyDamage());
				Buff.affect(ch, Blindness.class, 8f * multiplier);
				if (ch.properties().contains(Char.Property.UNDEAD) || ch.properties().contains(Char.Property.DEMONIC)){
					Buff.affect(ch, Weakness.class, 15f * multiplier);
				}
			}
		}

		Sample.INSTANCE.play( Assets.Sounds.BONG, 1, Random.Float(0.87f, 1.15f));
	}

	public static class HolyDamage{}

	@Override
	public String desc() {
		String desc_fuse = Messages.get(this, "desc",
				Math.round(minDamage()*0.8), Math.round(maxDamage()*0.8));
		if (fuse != null){
			desc_fuse += "\n\n" + Messages.get(this, "desc_burning");
		} else {
			desc_fuse += "\n\n" + Messages.get(this, "desc_fuse");
		}

		return desc_fuse;
	}

	@Override
	public int value() {
		//prices of ingredients
		return quantity * (20 + 30);
	}

	public class Effect extends Halo {

		private CharSprite target;

		private float phase;

		public Effect(CharSprite sprite ) {

			//rectangular sprite to circular radius. Pythagorean theorem
			super( (float)Math.sqrt(Math.pow(sprite.width()/2f, 2) + Math.pow(sprite.height()/2f, 2)), 0xFFE97D, 1.25f );

			am = -0.33f;
			aa = +0.33f;

			target = sprite;

			phase = 1;
		}

		@Override
		public void update() {
			super.update();

			if (phase < 1) {
				if ((phase -= Game.elapsed) <= 0) {
					killAndErase();
				} else {
					scale.set( (5 - phase * 4) * radius / RADIUS );
					am = phase * (-1);
					aa = phase * (+1);
				}
			}

			if (visible = target.visible) {
				PointF p = target.center();
				point( p.x, p.y );
			}
		}

		@Override
		public void draw() {
			Blending.setLightMode();
			super.draw();
			Blending.setNormalMode();
		}

		public void putOut() {
			phase = 0.999f;
		}

	}
}
