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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Preparation;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.GoatClone;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.knight.Concentration;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.knight.DirectingPulse;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.knight.EnergizedBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.knight.MotionBloom;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.knight.ShardsOfDespair;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.soulreaver.AntarcticTouch;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.soulreaver.ArtemisBridge;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.soulreaver.HolyAura;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.soulreaver.TommiesArmorSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.soulreaver.TransmogrificationWand;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChooseSubclass;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;

import java.util.ArrayList;

public class TengusMask extends Item {
	
	private static final String AC_WEAR	= "WEAR";
	
	{
		stackable = false;
		image = ItemSpriteSheet.MASK;

		defaultAction = AC_WEAR;

		unique = true;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_WEAR );
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_WEAR )) {
			
			curUser = hero;

			GameScene.show( new WndChooseSubclass( this, hero ) );
			
		}
	}
	
	@Override
	public boolean doPickUp(Hero hero, int pos) {
		Badges.validateMastery();
		return super.doPickUp( hero, pos );
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	public void choose( HeroSubClass way ) {
		
		detach( curUser.belongings.backpack );
		Catalog.countUse( getClass() );
		
		curUser.spend( Actor.TICK );
		curUser.busy();
		
		curUser.subClass = way;
		Talent.initSubclassTalents(curUser);

		if (way == HeroSubClass.ASSASSIN && curUser.invisible > 0){
			Buff.affect(curUser, Preparation.class);
		}
		
		curUser.sprite.operate( curUser.pos );
		Sample.INSTANCE.play( Assets.Sounds.MASTERY );

		if (curUser.subClass == HeroSubClass.WILL_SORCERER){
			GoatClone.spawnClone();

			new EnergizedBlast().collectWithAnnouncing();
			new MotionBloom().collectWithAnnouncing();
			new Concentration().collectWithAnnouncing();
			new DirectingPulse().collectWithAnnouncing();
			new ShardsOfDespair().collectWithAnnouncing();
		}
		if (curUser.subClass == HeroSubClass.SOUL_WIELDER){
			new AntarcticTouch().collectWithAnnouncing();
			new TommiesArmorSpell().collectWithAnnouncing();
			new TransmogrificationWand().collectWithAnnouncing();
			new ArtemisBridge().collectWithAnnouncing();
			new HolyAura().collectWithAnnouncing();
		}
		
		Emitter e = curUser.sprite.centerEmitter();
		e.pos(e.x-2, e.y-6, 4, 4);
		e.start(Speck.factory(Speck.MASK), 0.05f, 20);
		GLog.p( Messages.get(this, "used"));
		
	}
}
