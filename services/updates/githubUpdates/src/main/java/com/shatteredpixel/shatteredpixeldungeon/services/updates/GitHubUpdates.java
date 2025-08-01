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


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;

import javax.net.ssl.SSLProtocolException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHubUpdates extends UpdateService {

	private static Pattern descPattern = Pattern.compile("(.*?)(\r\n|\n|\r)(\r\n|\n|\r)---", Pattern.DOTALL + Pattern.MULTILINE);
	private static Pattern versionCodePattern = Pattern.compile("internal version number: ([0-9]*)", Pattern.CASE_INSENSITIVE);

	private static Pattern minAndroidPattern = Pattern.compile("Android .*\\(API ([0-9]*)\\)\\+ Devices", Pattern.CASE_INSENSITIVE);
	private static Pattern minIOSPattern = Pattern.compile("iOS ([0-9]*)\\+ Devices", Pattern.CASE_INSENSITIVE);

	@Override
	public boolean supportsUpdatePrompts() {
		return true;
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

		Net.HttpRequest httpGet = new Net.HttpRequest(Net.HttpMethods.GET);
		httpGet.setUrl("https://api.github.com/repos/TrashboxBobylev/Summoning-Reincarnated/releases");
		httpGet.setHeader("Accept", "application/vnd.github.v3+json");

		Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {
			@Override
			public void handleHttpResponse(Net.HttpResponse httpResponse) {
				try {
					Bundle latestRelease = null;
					int latestVersionCode = Game.versionCode;

					for (Bundle b : Bundle.read( httpResponse.getResultAsStream() ).getBundleArray()){
						Matcher m = versionCodePattern.matcher(b.getString("body"));

						if (m.find()){
							int releaseVersion = Integer.parseInt(m.group(1));


							//skip release that aren't the latest update (or an update at all)
							if (releaseVersion <= latestVersionCode) {
								continue;

							// or that are betas when we haven't opted in
							} else if (!includeBetas && b.getBoolean("prerelease")){
								continue;

							// or that aren't compatible
							} else if (DeviceCompat.isAndroid()){
								Matcher minAndroid = minAndroidPattern.matcher(b.getString("body"));
								if (minAndroid.find() && DeviceCompat.getPlatformVersion() < Integer.parseInt(minAndroid.group(1))){
									continue;
								}
							} else if (DeviceCompat.isiOS()){
								Matcher minIOS = minIOSPattern.matcher(b.getString("body"));
								if (minIOS.find() && DeviceCompat.getPlatformVersion() < Integer.parseInt(minIOS.group(1))){
									continue;
								}
							}

							latestRelease = b;
							latestVersionCode = releaseVersion;
						}

					}

					if (latestRelease == null){
						callback.onNoUpdateFound();
					} else {

						AvailableUpdateData update = new AvailableUpdateData();

						update.versionName = latestRelease.getString("name");
						update.versionCode = latestVersionCode;
						Matcher m = descPattern.matcher(latestRelease.getString("body"));
						m.find();
						update.desc = m.group(1);
						update.URL = latestRelease.getString("html_url");

						callback.onUpdateAvailable(update);
					}
				} catch (Exception e) {
					Game.reportException( e );
					callback.onConnectionFailed();
				}
			}

			@Override
			public void failed(Throwable t) {
				//Failure in SSL handshake, possibly because GitHub requires TLS 1.2+.
				// Often happens for old OS versions with outdated security protocols.
				// Future update attempts won't work anyway, so just pretend nothing was found.
				if (t instanceof SSLProtocolException){
					callback.onNoUpdateFound();
				} else {
					Game.reportException(t);
					callback.onConnectionFailed();
				}
			}

			@Override
			public void cancelled() {
				callback.onConnectionFailed();
			}
		});

	}

	@Override
	public void initializeUpdate(AvailableUpdateData update) {
		Game.platform.openURI( update.URL );
	}

	@Override
	public boolean supportsReviews() {
		return false;
	}

	@Override
	public void initializeReview(ReviewResultCallback callback) {
		//does nothing, no review functionality here
		callback.onComplete();
	}

	@Override
	public void openReviewURI() {
		//does nothing
	}
}
