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

package com.shatteredpixel.shatteredpixeldungeon.ios;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.iosrobovm.custom.HWMachine;
import com.badlogic.gdx.backends.iosrobovm.objectal.OALSimpleAudio;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.watabou.input.ControllerHandler;
import com.watabou.noosa.Game;
import com.watabou.utils.PlatformSupport;
import org.robovm.apple.audiotoolbox.AudioServices;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.systemconfiguration.SCNetworkReachability;
import org.robovm.apple.systemconfiguration.SCNetworkReachabilityFlags;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationOpenURLOptions;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IOSPlatformSupport extends PlatformSupport {

	@Override
	public boolean openURI( String uri ){
		//backported from libGDX 1.13.1, required for opening URLs on modern iOS
		UIApplication uiApp = UIApplication.getSharedApplication();
		NSURL url = new NSURL(uri);
		if (uiApp.canOpenURL(url)) {
			uiApp.openURL(url, new UIApplicationOpenURLOptions(), null);
			return true;
		}
		return false;
	}

	@Override
	public void updateDisplaySize() {
		//non-zero safe insets on left/top/right means device has a notch, show status bar
		if (Gdx.graphics.getSafeInsetTop() != 0
				|| Gdx.graphics.getSafeInsetLeft() != 0
				|| Gdx.graphics.getSafeInsetRight() != 0){
			UIApplication.getSharedApplication().setStatusBarHidden(false);
		} else {
			UIApplication.getSharedApplication().setStatusBarHidden(true);
		}

		if (!SPDSettings.fullscreen()) {
			int insetChange = Gdx.graphics.getSafeInsetBottom() - Game.bottomInset;
			Game.bottomInset = Gdx.graphics.getSafeInsetBottom();
			Game.height -= insetChange;
			Game.dispHeight = Game.height;
		} else {
			Game.height += Game.bottomInset;
			Game.dispHeight = Game.height;
			Game.bottomInset = 0;
		}
		Gdx.gl.glViewport(0, Game.bottomInset, Game.width, Game.height);
	}

	@Override
	public void updateSystemUI() {
		int prevInset = Game.bottomInset;
		updateDisplaySize();
		if (prevInset != Game.bottomInset) {
			ShatteredPixelDungeon.seamlessResetScene();
		}
	}

	@Override
	public boolean connectedToUnmeteredNetwork() {
		SCNetworkReachability test = new SCNetworkReachability("www.apple.com");
		return !test.getFlags().contains(SCNetworkReachabilityFlags.IsWWAN);
	}

	@Override
	public boolean supportsVibration() {
		//Devices with haptics...
		if (Gdx.input.isPeripheralAvailable(Input.Peripheral.HapticFeedback)){
			return true;
		};

		//...or with a supported controller connected
		if (ControllerHandler.vibrationSupported()){
			return true;
		}

		//...or with 3d touch
		String machineString = HWMachine.getMachineString();
		if (machineString.equals("iPhone8,4")){ //1st gen SE has no 3D touch specifically
			return false;
		} else { // 6s/7/8/X/XR have 3D touch
			return machineString.contains("iphone8")        //6s
					|| machineString.contains("iphone9")    //7
					|| machineString.contains("iphone10")   //8, and X
					|| machineString.contains("iphone11");  //XS (also XR but that has haptic)
		}
	}

	public void vibrate(int millis ){
		if (ControllerHandler.isControllerConnected()){
			ControllerHandler.vibrate(millis);
		} else if (Gdx.input.isPeripheralAvailable(Input.Peripheral.HapticFeedback)){
			Gdx.input.vibrate( millis );
		} else {
			//devices without haptics but with 3d touch use a short vibrate
			AudioServices.playSystemSound(1520);
			// no vibration otherwise
		}
	}

	@Override
	public void setHonorSilentSwitch( boolean value ) {
		OALSimpleAudio.sharedInstance().setHonorSilentSwitch(value);
	}

	/* FONT SUPPORT */

	//custom pixel font, for use with Latin and Cyrillic languages
	private static FreeTypeFontGenerator basicFontGenerator;
	//droid sans fallback, for asian fonts
	private static FreeTypeFontGenerator asianFontGenerator;

	@Override
	public void setupFontGenerators(int pageSize, boolean systemfont) {
		//don't bother doing anything if nothing has changed
		if (fonts != null && this.pageSize == pageSize && this.systemfont == systemfont){
			return;
		}
		this.pageSize = pageSize;
		this.systemfont = systemfont;

		resetGenerators(false);
		fonts = new HashMap<>();

		if (systemfont) {
			basicFontGenerator = asianFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/droid_sans.ttf"));
		} else {
			basicFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pixel_font.ttf"));
			asianFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/droid_sans.ttf"));
		}

		fonts.put(basicFontGenerator, new HashMap<>());
		fonts.put(asianFontGenerator, new HashMap<>());

		packer = new PixmapPacker(pageSize, pageSize, Pixmap.Format.RGBA8888, 1, false);
	}

	private static final Matcher asianMatcher = Pattern.compile("\\p{InHangul_Syllables}|" +
			"\\p{InCJK_Unified_Ideographs}|\\p{InCJK_Symbols_and_Punctuation}|\\p{InHalfwidth_and_Fullwidth_Forms}|" +
			"\\p{InHiragana}|\\p{InKatakana}").matcher("");

	@Override
	protected FreeTypeFontGenerator getGeneratorForString( String input ){
		if (asianMatcher.reset(input).find()){
			return asianFontGenerator;
		} else {
			return basicFontGenerator;
		}
	}

	//splits on newline (for layout), chinese/japanese (for font choice), and '_'/'**' (for highlighting)
	private Pattern regularsplitter = Pattern.compile(
			"(?<=\n)|(?=\n)|(?<=_)|(?=_)|(?<=\\*\\*)|(?=\\*\\*)|" +
					"(?<=\\p{InHiragana})|(?=\\p{InHiragana})|" +
					"(?<=\\p{InKatakana})|(?=\\p{InKatakana})|" +
					"(?<=\\p{InCJK_Unified_Ideographs})|(?=\\p{InCJK_Unified_Ideographs})|" +
					"(?<=\\p{InCJK_Symbols_and_Punctuation})|(?=\\p{InCJK_Symbols_and_Punctuation})");

	//additionally splits on spaces, so that each word can be laid out individually
	private Pattern regularsplitterMultiline = Pattern.compile(
			"(?<= )|(?= )|(?<=\n)|(?=\n)|(?<=_)|(?=_)|(?<=\\*\\*)|(?=\\*\\*)|" +
					"(?<=\\p{InHiragana})|(?=\\p{InHiragana})|" +
					"(?<=\\p{InKatakana})|(?=\\p{InKatakana})|" +
					"(?<=\\p{InCJK_Unified_Ideographs})|(?=\\p{InCJK_Unified_Ideographs})|" +
					"(?<=\\p{InCJK_Symbols_and_Punctuation})|(?=\\p{InCJK_Symbols_and_Punctuation})");

	@Override
	public String[] splitforTextBlock(String text, boolean multiline) {
		if (multiline) {
			return regularsplitterMultiline.split(text);
		} else {
			return regularsplitter.split(text);
		}
	}
}
