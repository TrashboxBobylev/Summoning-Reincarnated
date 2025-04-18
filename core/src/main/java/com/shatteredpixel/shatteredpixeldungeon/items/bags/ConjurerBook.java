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

package com.shatteredpixel.shatteredpixeldungeon.items.bags;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Rankable;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.ConjurerSpell;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Visual;
import com.watabou.utils.Bundle;

public class ConjurerBook extends Bag {

	{
		image = ItemSpriteSheet.BOOK;
	}

	@Override
	public boolean canHold( Item item ) {
		if (item instanceof ConjurerSpell){
			return super.canHold(item);
		} else {
			return false;
		}
	}

	public int capacity(){
		return 13;
	}

	private ConjurerSpell quickSpell = null;

	private static final String QUICK_CLS = "quick_cls";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		if (quickSpell != null) {
			bundle.put(QUICK_CLS, quickSpell.getClass());
		}
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(QUICK_CLS)){
			Class quickCls = bundle.getClass(QUICK_CLS);
			for (Item spell: items){
				if (spell.getClass() == quickCls){
					quickSpell = (ConjurerSpell) spell;
				}
			}
		}
	}

	//some copypaste to have artifact-like buff

	protected Buff passiveBuff;

	public void activate(Hero hero){
		if (passiveBuff != null){
			if (passiveBuff.target != null) passiveBuff.detach();
			passiveBuff = null;
		}
		passiveBuff = new SpellFavorite();
		passiveBuff.attachTo(hero);
	}

	@Override
	public boolean collect( Bag container ) {
		if (super.collect(container)){
			if (container.owner instanceof Hero
					&& passiveBuff == null){
				activate((Hero) container.owner);
			}
			return true;
		} else{
			return false;
		}
	}

	@Override
	public void onDetach() {
		if (passiveBuff != null){
			passiveBuff.detach();
			passiveBuff = null;
		}
	}

	public void setQuickSpell(ConjurerSpell spell){
		if (quickSpell == spell){
			quickSpell = null; //re-assigning the same spell clears the quick spell
			if (passiveBuff != null){
				ActionIndicator.clearAction((ActionIndicator.Action) passiveBuff);
			}
		} else {
			quickSpell = spell;
			if (passiveBuff != null){
				ActionIndicator.setAction((ActionIndicator.Action) passiveBuff);
			}
		}
	}

	public class SpellFavorite extends Buff implements ActionIndicator.Action {
		@Override
		public boolean attachTo( Char target ) {
			if (super.attachTo( target )) {
				//if we're loading in and the hero has partially spent a turn, delay for 1 turn
				if (target instanceof Hero && Dungeon.hero == null && cooldown() == 0 && target.cooldown() > 0) {
					spend(TICK);
				}
				if (quickSpell != null) ActionIndicator.setAction(this);
				return true;
			}
			return false;
		}

		@Override
		public void detach() {
			super.detach();
			ActionIndicator.clearAction(this);
		}

		@Override
		public void doAction() {
			if (quickSpell != null){
				quickSpell.execute((Hero) owner, ConjurerSpell.AC_ZAP);
			}
		}

		@Override
		public String actionName() {
			return quickSpell.name();
		}

		@Override
		public Visual primaryVisual() {
			return new ItemSprite(quickSpell.image);
		}

		@Override
		public Visual secondaryVisual() {
			BitmapText txt = new BitmapText(PixelScene.pixelFont);
			txt.text(Rankable.getRankString(quickSpell.rank()));
			txt.hardlight(Rankable.getRankColor(quickSpell.rank()));
			txt.measure();
			return txt;
		}

		@Override
		public int indicatorColor() {
			return 0x4C51AD;
		}
	}
}
