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

package com.shatteredpixel.shatteredpixeldungeon.levels.features;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfFeatherFall;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.WeakFloorRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Chasm implements Hero.Doom {

	public static boolean jumpConfirmed = false;
	private static int heroPos;
	
	public static void heroJump( final Hero hero ) {
		heroPos = hero.pos;
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				GameScene.show(
						new WndOptions( new Image(Dungeon.level.tilesTex(), 176, 16, 16, 16),
								Messages.get(Chasm.class, "chasm"),
								Messages.get(Chasm.class, "jump"),
								Messages.get(Chasm.class, "yes"),
								Messages.get(Chasm.class, "no") ) {

							private float elapsed = 0f;

							@Override
							public synchronized void update() {
								super.update();
								elapsed += Game.elapsed;
							}

							@Override
							public void hide() {
								if (elapsed > 0.2f){
									super.hide();
								}
							}

							@Override
							protected void onSelect( int index ) {
								if (index == 0 && elapsed > 0.2f) {
									if (Dungeon.hero.pos == heroPos) {
										jumpConfirmed = true;
										hero.resume(false);
									}
								}
							}
						}
				);
			}
		});
	}
	
	public static void heroFall( int pos ) {
		
		jumpConfirmed = false;
				
		Sample.INSTANCE.play( Assets.Sounds.FALLING );

		Level.beforeTransition();

		if (Dungeon.hero.isAlive()) {
			Dungeon.hero.interrupt();
			InterlevelScene.mode = InterlevelScene.Mode.FALL;
			if (Dungeon.level instanceof RegularLevel &&
						((RegularLevel)Dungeon.level).room( pos ) instanceof WeakFloorRoom){
				InterlevelScene.fallIntoPit = true;
				Notes.remove(Notes.Landmark.DISTANT_WELL);
			} else {
				InterlevelScene.fallIntoPit = false;
			}
			Game.switchScene( InterlevelScene.class );
		} else {
			Dungeon.hero.sprite.visible = false;
		}
	}

	@Override
	public void onDeath() {
		Badges.validateDeathFromFalling();

		Dungeon.fail( Chasm.class );
		GLog.n( Messages.get(Chasm.class, "ondeath") );
	}

	public static void heroLand() {
		
		Hero hero = Dungeon.hero;
		
		ElixirOfFeatherFall.FeatherBuff b = hero.buff(ElixirOfFeatherFall.FeatherBuff.class);
		
		if (b != null){
			hero.sprite.emitter().burst( Speck.factory( Speck.JET ), 20);
			b.processFall();
			return;
		}
		
		PixelScene.shake( 4, 1f );

		Dungeon.level.occupyCell(hero );
		Buff.prolong( hero, Cripple.class, Cripple.DURATION );

		//The lower the hero's HP, the more bleed and the less upfront damage.
		//Hero has a 50% chance to bleed out at 66% HP, and begins to risk instant-death at 25%
		Buff.affect( hero, Bleeding.class).set( Math.round(hero.HT / (6f + (6f*(hero.HP/(float)hero.HT)))), Chasm.class);
		hero.damage( Math.max( hero.HP / 2, Random.NormalIntRange( hero.HP / 2, hero.HT / 4 )), new Chasm() );
	}

	public static void mobFall( Mob mob ) {
		if (mob.isAlive()) {
			Buff.prolong(mob, Trap.HazardAssistTracker.class, Trap.HazardAssistTracker.DURATION);
			mob.die( Chasm.class );
		}
		
		if (mob.sprite != null) ((MobSprite)mob.sprite).fall();
	}
	
	public static class Falling extends Buff {
		
		{
			actPriority = VFX_PRIO;
		}
		
		@Override
		public boolean act() {
			heroLand();
			detach();
			return true;
		}
	}

}
