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

package com.shatteredpixel.shatteredpixeldungeon.journal;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.io.IOException;

public class Journal {
	
	public static final String JOURNAL_FILE = "journal.dat";
	
	private static boolean loaded = false;
	
	public static void loadGlobal(){
		if (loaded){
			return;
		}
		
		Bundle bundle;
		try {
			bundle = FileUtils.bundleFromFile( JOURNAL_FILE );
			
		} catch (IOException e){
			bundle = new Bundle();
		}
		
		Catalog.restore( bundle );
		Bestiary.restore( bundle );
		Document.restore( bundle );
		
		loaded = true;
	}
	
	//package-private
	static boolean saveNeeded = false;

	public static void saveGlobal(){
		saveGlobal(false);
	}

	public static void saveGlobal(boolean force){
		if (!force && !saveNeeded){
			return;
		}
		
		Bundle bundle = new Bundle();
		
		Catalog.store(bundle);
		Bestiary.store(bundle);
		Document.store(bundle);
		
		try {
			FileUtils.bundleToFile( JOURNAL_FILE, bundle );
			saveNeeded = false;
		} catch (IOException e) {
			ShatteredPixelDungeon.reportException(e);
		}
		
	}

}
