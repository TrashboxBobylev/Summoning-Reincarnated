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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;

public class AboutScene extends PixelScene {

	@Override
	public void create() {
		super.create();

		final float colWidth = 120;
		final float fullWidth = colWidth * (landscape() ? 2 : 1);

		int w = Camera.main.width;
		int h = Camera.main.height;

		Archs archs = new Archs();
		archs.setSize( w, h );
		add( archs );

		//darkens the arches
		add(new ColorBlock(w, h, 0x88000000));

		ScrollPane list = new ScrollPane( new Component() );
		add( list );

		Component content = list.content();
		content.clear();

		CreditsBlock tb = new CreditsBlock(true, ItemSlot.RANK2,
				"Summoning PD: Reincarnated",
				Icons.BOBYLEV.get(),
				"Developed by: _Trashbox Bobylev_\nThe remaster of original Summoning PD\nBased on ShatteredPD's open source",
				"reddit.com/u/TrashboxBobylev",
				"https://reddit.com/u/TrashboxBobylev");
		tb.setRect((w - fullWidth)/2f, 6, 120, 0);
		content.add(tb);

		CreditsBlock zachary = new CreditsBlock(false, 0xE8D906,
				"Wiki, Testing and Proofreading:",
				Icons.ZACHARY.get(),
				"Zackary4536",
				"Click for Fandom page",
				"https://pixeldungeon.fandom.com/wiki/User:Togekissy");
		zachary.setSize(colWidth/2f, 0);
		if (landscape()){
			zachary.setPos(tb.right()-10, tb.top() + (tb.height() - tb.height())/2f);
		} else {
			zachary.setPos(w/2f - colWidth/2f, tb.bottom()+5);
		}
		content.add(zachary);

		CreditsBlock omicronrg = new CreditsBlock(false, ItemSlot.ENHANCED,
				"Support and Testing:",
				Icons.OMICRONRG.get(),
				"Daniel Ømicrónrg Rodriguez",
				"sites.google.com/view/omicronrg9",
				"https://sites.google.com/view/omicronrg9");
		omicronrg.setRect(zachary.right()+13, zachary.top(), colWidth/2f, 0);
		content.add(omicronrg);

		CreditsBlock krauzxe = new CreditsBlock(false, 0xFF8C44,
				"Former Sprite Artist:",
				Icons.KRAUZXE.get(),
				"Krauzxe##1119",
				null,
				null);
		krauzxe.setSize(colWidth/2f, 0);
		if (landscape()){
			krauzxe.setPos(zachary.left()+zachary.width()/2, omicronrg.bottom()+8);
		} else {
			krauzxe.setPos(tb.left()+tb.width()/4, omicronrg.bottom()+7);
		}
		content.add(krauzxe);

		CreditsBlock lolman = new CreditsBlock(false, 0xC6C6C6,
				"Support and Ideas:",
				Icons.LOLMAN.get(),
				"Gammalolman#1119",
				"reddit.com/u/Hyperlolman",
				"https://www.reddit.com/user/Hyperlolman");
		lolman.setSize(colWidth/2f, 0);
		if (landscape()){
			lolman.setPos(tb.right(), krauzxe.bottom()+8);
		} else {
			lolman.setPos(tb.left()-5, krauzxe.bottom()+8);
		}
		content.add(lolman);

		CreditsBlock marshall = new CreditsBlock(false, ItemSlot.WARNING,
				"Conjurer's sprites:",
				Icons.MARSHALL.get(),
				"MarshalldotEXE",
				"Click for Youtube channel",
				"https://www.youtube.com/channel/UCEhheHlAmqkGMqULoDAIMOQ");
		marshall.setRect(lolman.right()+10, lolman.top(), colWidth/2f, 0);
		content.add(marshall);

		CreditsBlock guys = new CreditsBlock(false, Window.TITLE_COLOR,
				"The rest of credits:",
				Icons.INFO.get(),
				"RavenWolf#4290 - Advanced sprites\n" +
						"Zrp200#0484, Kohru#4813, ConsideredHamster#9508 - Code spinnets\n"+
						"smujames#5300, NeoSlav#5320 and rest of community - ideas and support",
				"Pixel Dungeon's Discord link",
				"https://discord.gg/KBfMN8X");
		guys.setSize(colWidth, 4);
		if (landscape()){
			guys.setPos(tb.left(), krauzxe.bottom()+10);
		} else {
			guys.setPos(tb.left(), marshall.bottom()+10);
		}
		content.add(guys);

		Image libIcon = Icons.CONDUCTS_COLOR.get();
		libIcon.hardlight(0x989898);

		CreditsBlock libs = new CreditsBlock(false, Window.WHITE,
				"Libraries used:",
				libIcon,
				"Apache Common Lang, by Apache Software Foundation",
				"commons.apache.org",
				"https://commons.apache.org");
		libs.setSize(colWidth, 4);
		libs.setPos(tb.left(), guys.bottom()+10);
		content.add(libs);

		//*** Shattered Pixel Dungeon Credits ***

		CreditsBlock shpx = new CreditsBlock(true, Window.SHPX_COLOR,
				"Shattered Pixel Dungeon",
				Icons.SHPX.get(),
				"Developed by: _Evan Debenham_\nBased on Pixel Dungeon's open source",
				"ShatteredPixel.com",
				"https://ShatteredPixel.com");
		shpx.setRect((w - fullWidth)/2f, 6, 120, 0);
		content.add(shpx);
		if (landscape()){
			shpx.setRect(tb.left(), libs.bottom() + 24, colWidth, 0);
		} else {
			shpx.setRect(tb.left(), libs.bottom() + 16, colWidth, 0);
		}
		content.add(shpx);

		addLine(shpx.top() - 8, content);

		CreditsBlock alex = new CreditsBlock(false, Window.SHPX_COLOR,
				"Splash Art & Design:",
				Icons.ALEKS.get(),
				"Aleksandar Komitov",
				"akomitov.artstation.com",
				"https://akomitov.artstation.com/");
		alex.setSize(colWidth/2f, 0);
		if (landscape()){
			alex.setPos(shpx.right(), shpx.top() + (shpx.height() - alex.height()*2)/2f);
		} else {
			alex.setPos(w/2f - colWidth/2f, shpx.bottom()+5);
		}
		content.add(alex);

		CreditsBlock charlie = new CreditsBlock(false, Window.SHPX_COLOR,
				"Sound Effects:",
				Icons.CELESTI.get(),
				"Celesti",
				"s9menine.itch.io",
				"https://s9menine.itch.io");
		charlie.setRect(alex.right(), alex.top(), colWidth/2f, 0);
		content.add(charlie);

		CreditsBlock kristjan = new CreditsBlock(false, Window.SHPX_COLOR,
				"Music:",
				Icons.KRISTJAN.get(),
				"Kristjan Haaristo",
				"youtube.com/@kristjan...",
				"https://www.youtube.com/@kristjanthomashaaristo");
		kristjan.setRect(alex.right() - colWidth/4f, alex.bottom() + 5, colWidth/2f, 0);
		content.add(kristjan);

		//*** Pixel Dungeon Credits ***

		final int WATA_COLOR = 0x55AAFF;
		CreditsBlock wata = new CreditsBlock(true, WATA_COLOR,
				"Pixel Dungeon",
				Icons.WATA.get(),
				"Developed by: _Watabou_\nInspired by Brian Walker's Brogue",
				"watabou.itch.io",
				"https://watabou.itch.io/");
		if (landscape()){
			wata.setRect(shpx.left(), kristjan.bottom() + 8, colWidth, 0);
		} else {
			wata.setRect(shpx.left(), kristjan.bottom() + 8, colWidth, 0);
		}
		content.add(wata);

		addLine(wata.top() - 4, content);

		CreditsBlock cube = new CreditsBlock(false, WATA_COLOR,
				"Music:",
				Icons.CUBE_CODE.get(),
				"Cube Code",
				null,
				null);
		cube.setSize(colWidth/2f, 0);
		if (landscape()){
			cube.setPos(wata.right() + colWidth/4f, wata.top() + (wata.height() - cube.height())/2f);
		} else {
			cube.setPos(alex.left() + colWidth/4f, wata.bottom()+5);
		}
		content.add(cube);

		//*** libGDX Credits ***

		final int GDX_COLOR = 0xE44D3C;
		CreditsBlock gdx = new CreditsBlock(true,
				GDX_COLOR,
				"libGDX",
				Icons.LIBGDX.get(),
				"ShatteredPD is powered by _libGDX_!",
				"libgdx.com",
				"https://libgdx.com/");
		if (landscape()){
			gdx.setRect(wata.left(), wata.bottom() + 8, colWidth, 0);
		} else {
			gdx.setRect(wata.left(), cube.bottom() + 8, colWidth, 0);
		}
		content.add(gdx);

		addLine(gdx.top() - 4, content);

		CreditsBlock arcnor = new CreditsBlock(false, GDX_COLOR,
				"Pixel Dungeon GDX:",
				Icons.ARCNOR.get(),
				"Edu García",
				"gamedev.place/@arcnor",
				"https://mastodon.gamedev.place/@arcnor");
		arcnor.setSize(colWidth/2f, 0);
		if (landscape()){
			arcnor.setPos(gdx.right(), gdx.top() + (gdx.height() - arcnor.height())/2f);
		} else {
			arcnor.setPos(alex.left(), gdx.bottom()+5);
		}
		content.add(arcnor);

		CreditsBlock purigro = new CreditsBlock(false, GDX_COLOR,
				"Shattered GDX Help:",
				Icons.PURIGRO.get(),
				"Kevin MacMartin",
				"github.com/prurigro",
				"https://github.com/prurigro/");
		purigro.setRect(arcnor.right()+2, arcnor.top(), colWidth/2f, 0);
		content.add(purigro);

		//*** Transifex Credits ***

		CreditsBlock transifex = new CreditsBlock(true,
				Window.TITLE_COLOR,
				null,
				null,
				"ShatteredPD is community-translated via _Transifex_! Thank you so much to all of Shattered's volunteer translators!",
				"transifex.com/shattered-pixel/...",
				"https://explore.transifex.com/shattered-pixel/shattered-pixel-dungeon/");
		transifex.setRect((Camera.main.width - colWidth)/2f, purigro.bottom() + 12, colWidth, 0);
		content.add(transifex);

		addLine(transifex.top() - 4, content);

		addLine(transifex.bottom() + 4, content);

		//*** Freesound Credits ***

		CreditsBlock freesound = new CreditsBlock(true,
				Window.TITLE_COLOR,
				null,
				null,
				"Shattered Pixel Dungeon uses the following sound samples from _freesound.org_:\n\n" +

				"Creative Commons Attribution License:\n" +
				"_SFX ATTACK SWORD 001.wav_ by _JoelAudio_\n" +
				"_Pack: Slingshots and Longbows_ by _saturdaysoundguy_\n" +
				"_Cracking/Crunching, A.wav_ by _InspectorJ_\n" +
				"_Extracting a sword.mp3_ by _Taira Komori_\n" +
				"_Pack: Uni Sound Library_ by _timmy h123_\n\n" +

				"Creative Commons Zero License:\n" +
				"_Pack: Movie Foley: Swords_ by _Black Snow_\n" +
				"_machine gun shot 2.flac_ by _qubodup_\n" +
				"_m240h machine gun burst 4.flac_ by _qubodup_\n" +
				"_Pack: Onomatopoeia_ by _Adam N_\n" +
				"_Pack: Watermelon_ by _lolamadeus_\n" +
				"_metal chain_ by _Mediapaja2009_\n" +
				"_Pack: Sword Clashes Pack_ by _JohnBuhr_\n" +
				"_Pack: Metal Clangs and Pings_ by _wilhellboy_\n" +
				"_Pack: Stabbing Stomachs & Crushing Skulls_ by _TheFilmLook_\n" +
				"_Sheep bleating_ by _zachrau_\n" +
				"_Lemon,Juicy,Squeeze,Fruit.wav_ by _Filipe Chagas_\n" +
				"_Lemon,Squeeze,Squishy,Fruit.wav_ by _Filipe Chagas_\n" +
				"_The Gong sound_ by _grvmusic_\n" +
                "_Lightning (thunderbolt)_ by _DominikBraun_",
				"www.freesound.org",
				"https://www.freesound.org");
		freesound.setRect(transifex.left()-10, transifex.bottom() + 8, colWidth+20, 0);
		content.add(freesound);

		content.setSize( fullWidth, freesound.bottom()+10 );

		list.setRect( 0, 0, w, h );
		list.scrollTo(0, 0);

		ExitButton btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );

		//fadeIn();
	}
	
	@Override
	protected void onBackPressed() {
		ShatteredPixelDungeon.switchScene(TitleScene.class);
	}

	private void addLine( float y, Group content ){
		ColorBlock line = new ColorBlock(Camera.main.width, 1, 0xFF333333);
		line.y = y;
		content.add(line);
	}

	private static class CreditsBlock extends Component {

		boolean large;
		RenderedTextBlock title;
		Image avatar;
		Flare flare;
		RenderedTextBlock body;

		RenderedTextBlock link;
		ColorBlock linkUnderline;
		PointerArea linkButton;

		//many elements can be null, but body is assumed to have content.
		private CreditsBlock(boolean large, int highlight, String title, Image avatar, String body, String linkText, String linkUrl){
			super();

			this.large = large;

			if (title != null) {
				this.title = PixelScene.renderTextBlock(title, large ? 8 : 6);
				if (highlight != -1) this.title.hardlight(highlight);
				add(this.title);
			}

			if (avatar != null){
				this.avatar = avatar;
				add(this.avatar);
			}

			if (large && highlight != -1 && this.avatar != null){
				this.flare = new Flare( 7, 24 ).color( highlight, true ).show(this.avatar, 0);
				this.flare.angularSpeed = 20;
			}

			this.body = PixelScene.renderTextBlock(body, 6);
			if (highlight != -1) this.body.setHightlighting(true, highlight);
			if (large) this.body.align(RenderedTextBlock.CENTER_ALIGN);
			add(this.body);

			if (linkText != null && linkUrl != null){

				int color = 0xFFFFFFFF;
				if (highlight != -1) color = 0xFF000000 | highlight;
				this.linkUnderline = new ColorBlock(1, 1, color);
				add(this.linkUnderline);

				this.link = PixelScene.renderTextBlock(linkText, 6);
				if (highlight != -1) this.link.hardlight(highlight);
				add(this.link);

				linkButton = new PointerArea(0, 0, 0, 0){
					@Override
					protected void onClick( PointerEvent event ) {
						ShatteredPixelDungeon.platform.openURI( linkUrl );
					}
				};
				add(linkButton);
			}

		}

		@Override
		protected void layout() {
			super.layout();

			float topY = top();

			if (title != null){
				title.maxWidth((int)width());
				title.setPos( x + (width() - title.width())/2f, topY);
				topY += title.height() + (large ? 2 : 1);
			}

			if (large){

				if (avatar != null){
					avatar.x = x + (width()-avatar.width())/2f;
					avatar.y = topY;
					PixelScene.align(avatar);
					if (flare != null){
						flare.point(avatar.center());
					}
					topY = avatar.y + avatar.height() + 2;
				}

				body.maxWidth((int)width());
				body.setPos( x + (width() - body.width())/2f, topY);
				topY += body.height() + 2;

			} else {

				if (avatar != null){
					avatar.x = x;
					body.maxWidth((int)(width() - avatar.width - 1));

					float fullAvHeight = Math.max(avatar.height(), 16);
					if (fullAvHeight > body.height()){
						avatar.y = topY + (fullAvHeight - avatar.height())/2f;
						PixelScene.align(avatar);
						body.setPos( avatar.x + avatar.width() + 1, topY + (fullAvHeight - body.height())/2f);
						topY += fullAvHeight + 1;
					} else {
						avatar.y = topY + (body.height() - fullAvHeight)/2f;
						PixelScene.align(avatar);
						body.setPos( avatar.x + avatar.width() + 1, topY);
						topY += body.height() + 2;
					}

				} else {
					topY += 1;
					body.maxWidth((int)width());
					body.setPos( x, topY);
					topY += body.height()+2;
				}

			}

			if (link != null){
				if (large) topY += 1;
				link.maxWidth((int)width());
				link.setPos( x + (width() - link.width())/2f, topY);
				topY += link.height() + 2;

				linkButton.x = link.left()-1;
				linkButton.y = link.top()-1;
				linkButton.width = link.width()+2;
				linkButton.height = link.height()+2;

				linkUnderline.size(link.width(), PixelScene.align(0.49f));
				linkUnderline.x = link.left();
				linkUnderline.y = link.bottom()+1;

			}

			topY -= 2;

			height = Math.max(height, topY - top());
		}
	}
}
