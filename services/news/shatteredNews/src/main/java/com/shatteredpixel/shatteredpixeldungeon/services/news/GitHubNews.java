/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
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


package com.shatteredpixel.shatteredpixeldungeon.services.news;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.watabou.noosa.Game;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

//bang's: in-game news comes from open GitHub issues labelled "공지" in the game's repo,
//written by the repo owner. Write an issue + add the label to post a notice; close it to hide it.
public class GitHubNews extends NewsService {

	public static final String OWNER = "bang0407";
	public static final String REPO = "shattered-pixel-dungeon";
	//"공지" url-encoded
	private static final String LABEL = "%EA%B3%B5%EC%A7%80";

	private static final int MAX_SUMMARY = 400;

	@Override
	public void checkForArticles(boolean useMetered, boolean preferHTTPS, NewsResultCallback callback) {

		if (!useMetered && !Game.platform.connectedToUnmeteredNetwork()){
			callback.onConnectionFailed();
			return;
		}

		Net.HttpRequest httpGet = new Net.HttpRequest(Net.HttpMethods.GET);
		httpGet.setUrl("https://api.github.com/repos/" + OWNER + "/" + REPO + "/issues"
				+ "?state=open&labels=" + LABEL + "&creator=" + OWNER + "&per_page=10");
		httpGet.setHeader("Accept", "application/vnd.github+json");
		httpGet.setHeader("User-Agent", "bangs-pixel-dungeon");

		Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {
			@Override
			public void handleHttpResponse(Net.HttpResponse httpResponse) {
				try {
					if (httpResponse.getStatus().getStatusCode() != 200){
						callback.onConnectionFailed();
						return;
					}

					ArrayList<NewsArticle> articles = new ArrayList<>();
					JsonValue issues = new JsonReader().parse(httpResponse.getResultAsString());

					SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
					dateParser.setTimeZone(TimeZone.getTimeZone("UTC"));

					for (JsonValue issue = issues.child; issue != null; issue = issue.next){
						//the issues API also returns pull requests, skip those
						if (issue.has("pull_request")) continue;

						NewsArticle article = new NewsArticle();
						article.title = issue.getString("title", "");
						article.date = dateParser.parse(issue.getString("created_at"));
						article.summary = cleanBody(issue.getString("body", ""));
						article.URL = issue.getString("html_url", "https://github.com/" + OWNER + "/" + REPO);
						article.icon = null;
						articles.add(article);
					}
					callback.onArticlesFound(articles);
				} catch (Exception e){
					Game.reportException(e);
					callback.onConnectionFailed();
				}
			}

			@Override
			public void failed(Throwable t) {
				callback.onConnectionFailed();
			}

			@Override
			public void cancelled() {
				callback.onConnectionFailed();
			}
		});
	}

	//strips the most common markdown so issue text reads cleanly in-game
	private static String cleanBody(String body){
		if (body == null) return "";
		String s = body.replace("\r", "");
		s = s.replaceAll("!\\[[^\\]]*\\]\\([^)]*\\)", "");        //images
		s = s.replaceAll("\\[([^\\]]*)\\]\\([^)]*\\)", "$1");     //links -> text
		s = s.replaceAll("(?m)^#+\\s*", "");                     //headings
		s = s.replaceAll("[*_`~]", "");                          //emphasis/code
		s = s.replaceAll("\n{3,}", "\n\n").trim();
		if (s.length() > MAX_SUMMARY) s = s.substring(0, MAX_SUMMARY).trim() + "...";
		return s;
	}

}
