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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.watabou.input.ControllerHandler;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Signal;

public class Button extends Component {

	public static float longClick = 0.5f;
	
	protected PointerArea hotArea;
	protected Tooltip hoverTip;

	//only one button should be pressed at a time
	protected static Button pressedButton;
	protected float pressTime;
	protected boolean clickReady;

	@Override
	protected void createChildren() {
		hotArea = new PointerArea( 0, 0, 0, 0 ) {
			@Override
			protected void onPointerDown( PointerEvent event ) {
				pressedButton = Button.this;
				pressTime = 0;
				clickReady = true;
				Button.this.onPointerDown();
			}
			@Override
			protected void onPointerUp( PointerEvent event ) {
				if (pressedButton == Button.this){
					pressedButton = null;
				} else {
					//cancel any potential click, only one button can be activated at a time
					clickReady = false;
				}
				Button.this.onPointerUp();
			}
			@Override
			protected void onClick( PointerEvent event ) {
				if (clickReady) {
					killTooltip();
					switch (event.button){
						case PointerEvent.LEFT: default:
							Button.this.onClick();
							break;
						case PointerEvent.RIGHT:
							Button.this.onRightClick();
							break;
						case PointerEvent.MIDDLE:
							Button.this.onMiddleClick();
							break;
					}

				}
			}

			@Override
			protected void onHoverStart(PointerEvent event) {
				String text = hoverText();
				if (text != null){
					int key = 0;
					if (keyAction() != null){
						key = KeyBindings.getFirstKeyForAction(keyAction(), ControllerHandler.controllerActive);
					}

					if (key == 0 && secondaryTooltipAction() != null){
						key = KeyBindings.getFirstKeyForAction(secondaryTooltipAction(), ControllerHandler.controllerActive);
					}

					if (key != 0){
						text += " _(" + KeyBindings.getKeyName(key) + ")_";
					}
					hoverTip = new Tooltip(Button.this, text, 80);
					Button.this.parent.addToFront(hoverTip);
					hoverTip.camera = camera();
					alignTooltip(hoverTip);
				}
			}

			@Override
			protected void onHoverEnd(PointerEvent event) {
				killTooltip();
			}
		};
		add( hotArea );
		
		KeyEvent.addKeyListener( keyListener = new Signal.Listener<KeyEvent>() {
			@Override
			public boolean onSignal ( KeyEvent event ) {
				if ( active && KeyBindings.getActionForKey( event ) == keyAction()){
					if (event.pressed){
						pressedButton = Button.this;
						pressTime = 0;
						clickReady = true;
						Button.this.onPointerDown();
					} else {
						Button.this.onPointerUp();
						if (pressedButton == Button.this) {
							pressedButton = null;
							if (clickReady) onClick();
						}
					}
					return true;
				}
				return false;
			}
		});
	}
	
	private Signal.Listener<KeyEvent> keyListener;
	
	public GameAction keyAction(){
		return null;
	}

	//used in cases where the main key action isn't bound, but a secondary action can be used for the tooltip
	public GameAction secondaryTooltipAction(){
		return null;
	}

	@Override
	public void update() {
		super.update();
		
		hotArea.active = visible;
		
		if (pressedButton == this && (pressTime += Game.elapsed) >= longClick) {
			pressedButton = null;
			if (onLongClick()) {

				hotArea.reset();
				clickReady = false; //did a long click, can't do a regular one
				onPointerUp();

				if (SPDSettings.vibration()) {
					Game.vibrate(50);
				}
			}
		}
	}
	
	protected void onPointerDown() {}
	protected void onPointerUp() {}
	protected void onClick() {} //left click, default key type
	protected void onRightClick() {}
	protected void onMiddleClick() {}
	protected boolean onLongClick() {
		return false;
	}

	protected String hoverText() {
		return null;
	}

	//TODO might be nice for more flexibility here
	private void alignTooltip( Tooltip tip ){
		tip.setPos(x, y-tip.height()-1);
		Camera cam = camera();
		//shift left if there's no room on the right
		if (tip.right() > (cam.width+cam.scroll.x)){
			tip.setPos(tip.left() - (tip.right() - (cam.width+cam.scroll.x)), tip.top());
		}
		//move to the bottom if there's no room on top
		if (tip.top() < 0){
			tip.setPos(tip.left(), bottom()+1);
		}
	}

	public void killTooltip(){
		if (hoverTip != null){
			hoverTip.killAndErase();
			hoverTip = null;
		}
	}
	
	@Override
	protected void layout() {
		hotArea.x = x;
		hotArea.y = y;
		hotArea.width = width;
		hotArea.height = height;
	}
	
	@Override
	public synchronized void destroy () {
		super.destroy();
		KeyEvent.removeKeyListener( keyListener );
		killTooltip();
	}

	public void givePointerPriority(){
		hotArea.givePointerPriority();
	}
	
}
