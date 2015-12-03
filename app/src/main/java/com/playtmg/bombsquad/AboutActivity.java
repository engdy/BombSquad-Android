/*
 * AboutActivity.java
 * BombSquad
 *
 * Created by Andy Foulke on 12/02/2015
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved.
 */

package com.playtmg.bombsquad;

import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Activity class for the About page in TMG's BombSquad app.  Provides attribution
 * and links to BGG entry for BombSquad, and the rules .pdf.
 * Displays version of the release.
 */

public class AboutActivity extends ActionBarActivity {
	public static final String TAG = AboutActivity.class.getSimpleName();
	private final String BGG_URL = "https://boardgamegeek.com/boardgame/142267/bomb-squad";
	private final String RULES_URL = "http://playtmg.com/BombSquad_LowResRules.pdf";

	private TextView txtVersion;

	/**
	 * onCreate method
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_about);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		txtVersion = (TextView)findViewById(R.id.textVersion);
		String version = "0.0.2";
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = pInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			Log.e(TAG, "PackageManager.NameNotFoundException");
		}
		txtVersion.setText(version);
	}

	/**
	 * onClick handler for BGG button - starts up browser to boardgamegeek.com
	 * @param btn
	 */
	public void clickedBGG(View btn) {
		Log.d(TAG, "Clicked BGG");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(BGG_URL));
		startActivity(intent);
	}

	/**
	 * onClick handler for Rules button - starts up browser to TMG's website
	 * @param btn
	 */
	public void clickedRules(View btn) {
		Log.d(TAG, "Clicked Rules");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(RULES_URL));
		startActivity(intent);
	}
}
