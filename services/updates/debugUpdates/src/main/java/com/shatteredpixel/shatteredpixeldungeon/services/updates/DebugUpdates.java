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

package com.shatteredpixel.shatteredpixeldungeon.services.updates;


import com.watabou.noosa.Game;

public class DebugUpdates extends UpdateService {

	private static AvailableUpdateData debugUpdateInfo;

	@Override
	public boolean supportsUpdatePrompts() {
		return false; //turn on to debug update prompts
	}

	@Override
	public boolean supportsBetaChannel() {
		return true;
	}

	@Override
	public void checkForUpdate(boolean useMetered, boolean includeBetas, UpdateResultCallback callback) {

		if (!useMetered && !Game.platform.connectedToUnmeteredNetwork()){
			callback.onConnectionFailed();
			return;
		}

		debugUpdateInfo = new AvailableUpdateData();
		debugUpdateInfo.versionCode = Game.versionCode+1;
		debugUpdateInfo.URL = "http://www.google.com";

		callback.onUpdateAvailable(debugUpdateInfo);

	}

	@Override
	public void initializeUpdate(AvailableUpdateData update) {
		Game.platform.openURI( update.URL );
	}

	@Override
	public boolean supportsReviews() {
		return false; //turn on to debug review prompts
	}

	@Override
	public void initializeReview(ReviewResultCallback callback) {
		//does nothing
		callback.onComplete();
	}

	@Override
	public void openReviewURI() {
		Game.platform.openURI("https://www.google.com/");
	}
}
