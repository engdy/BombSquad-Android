/*
 * MainActivity.java
 * BombSquad
 *
 * Created by Andy Foulke on 12/2/2015
 * Modified by Andy Foulke on 12/17/2015
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved
 */
package com.playtmg.bombsquad;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Activity class for the main page of TMG's BombSquad timer app.  Used to send the user
 * off to some other page.
 */
public class MainActivity extends Activity {
	public static final String TAG = MainActivity.class.getSimpleName();
	private BombList bombList;
	private BURN burn;
	private BSTimer bsTimer;
	private Button btnResume;
	private boolean shouldShowResume = false;

	/**
	 * onCreate method
	 * <p>
	 * Initializes state classes and timer for the whole application
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		bombList = new BombList();
		BombSquadData.getInstance().setBombList(bombList);
		burn = new BURN(this);
		BombSquadData.getInstance().setBurn(burn);
		btnResume = (Button)findViewById(R.id.btnResume);
		bsTimer = BombSquadData.getInstance().getTimer();
		if (bsTimer == null) {
			bsTimer = new BSTimer(burn);
			BombSquadData.getInstance().setTimer(bsTimer);
		}
	}

	/**
	 * onResume method
	 * <p>
	 * Determines if Resume button should be displayed
	 */
	@Override
	protected void onResume() {
		super.onResume();
		bombList = BombSquadData.getInstance().getBombList();
		long timeLeft = bombList.findMaxTime() - bsTimer.getElapsedMillis();
		shouldShowResume = bombList.showResumeButton() && bombList.bombCount() > 0 && timeLeft > 0;
		btnResume.setEnabled(shouldShowResume);
		btnResume.setVisibility(shouldShowResume ? View.VISIBLE : View.INVISIBLE);
		bsTimer.stopMusic();
	}

	public void clickedCampaign(View btn) {
		Log.d(TAG, "Clicked campaign");
		Intent intent = new Intent(this, CampaignActivity.class);
		startActivity(intent);
	}

	public void clickedQuickStart(View btn) {
		Log.d(TAG, "Clicked quickstart");
		if (!shouldShowResume) {
			BombSquadData.getInstance().setBombList(new BombList());
		}
		Intent intent = new Intent(this, QuickPlayActivity.class);
		startActivity(intent);
	}

	public void clickedSettings(View btn) {
		Log.d(TAG, "Clicked settings");
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	public void clickedHelp(View btn) {
		Log.d(TAG, "Clicked help");
		Intent intent = new Intent(this, HelpActivity.class);
		startActivity(intent);
	}

	public void clickedAbout(View btn) {
		Log.d(TAG, "Clicked about");
		Intent intent = new Intent(this, AboutActivity.class);
		startActivity(intent);
	}

	public void clickedResume(View btn) {
		Log.d(TAG, "Clicked resume");
		Intent intent = new Intent(this, AllBombsActivity.class);
		startActivity(intent);
	}
}
