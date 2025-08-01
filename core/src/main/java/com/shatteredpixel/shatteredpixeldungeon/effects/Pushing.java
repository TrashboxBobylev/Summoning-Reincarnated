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

package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Visual;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;

public class Pushing extends Actor {

	private CharSprite sprite;
	private int from;
	private int to;
	
	private Effect effect;
	private Char ch;

	private Callback callback;

	{
		actPriority = VFX_PRIO+10;
	}
	
	public Pushing( Char ch, int from, int to ) {
		this.ch = ch;
		sprite = ch.sprite;
		this.from = from;
		this.to = to;
		this.callback = null;

		if (ch == Dungeon.hero){
			Camera.main.panFollow(ch.sprite, 20f);
		}
	}

	public Pushing( Char ch, int from, int to, Callback callback ) {
		this(ch, from, to);
		this.callback = callback;
	}
	
	@Override
	protected boolean act() {
		Actor.remove( Pushing.this );

		if (sprite != null && sprite.parent != null) {
			if (Dungeon.level.heroFOV[from] || Dungeon.level.heroFOV[to]){
				sprite.visible = true;
			}
			if (effect == null) {
				new Effect();
			}
		} else {
			return true;
		}

		//so that all pushing effects at the same time go simultaneously
		for ( Actor actor : Actor.all() ){
			if (actor instanceof Pushing && actor.cooldown() == 0)
				return true;
		}
		return false;

	}

	public static boolean pushingExistsForChar(Char ch) {
		for (Actor a : all()){
			if (a instanceof Pushing && ((Pushing)a).ch == ch){
				return true;
			}
		}
		return false;
	}

	public class Effect extends Visual {

		private static final float DELAY = 0.15f;
		
		private PointF end;
		
		private float delay;
		
		public Effect() {
			super( 0, 0, 0, 0 );
			
			point( sprite.worldToCamera( from ) );
			end = sprite.worldToCamera( to );
			
			speed.set( 2 * (end.x - x) / DELAY, 2 * (end.y - y) / DELAY );
			acc.set( -speed.x / DELAY, -speed.y / DELAY );
			
			delay = 0;

			if (sprite.parent != null)
				sprite.parent.add( this );
		}
		
		@Override
		public void update() {
			super.update();
			
			if ((delay += Game.elapsed) < DELAY) {
				
				sprite.x = x;
				sprite.y = y;
				
			} else {
				
				sprite.point(end);
				
				killAndErase();
				Actor.remove(Pushing.this);
				if (callback != null) callback.call();
				GameScene.sortMobSprites();

				next();
			}
		}
	}

}
