/*
 * RunningActivity.java
 * BombSquad
 *
 * Created by Andy Foulke on 11/10/2015
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved
 */
package com.playtmg.bombsquad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Abstract Activity class for both {@link AllBombsActivity} and {@link SingleBombActivity}.
 * Provides facade for both.
 */
public abstract class RunningActivity extends FragmentActivity implements View.OnClickListener {
	public String TAG = RunningActivity.class.getSimpleName();
	public enum ButtonState { PLAY_ENABLED, PAUSE_ENABLED, PLAY_DISABLED };
	protected Button btnStart;
	protected BSTimer bsTimer;
	protected BombList bombs;
	protected BURN burn;
	private boolean shouldPlaySoundtrack = false;
	private int soundtrackSelection = 0;
	private float soundtrackVolume = 100.0f;
	private boolean shouldPlayBombs = false;
	private float bombVolume = 100.0f;
	private boolean shouldPlayCountdown = false;
	protected boolean shouldVisuallyAlert = false;
	private Bomb bombForDialog;
	private boolean isRunningDuringAlert = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		burn = BombSquadData.getInstance().getBurn();
		bsTimer = BombSquadData.getInstance().getTimer();
		bombs = BombSquadData.getInstance().getBombList();
		bsTimer.setBombList(bombs);
	}

	/**
	 * onStart method
	 * <p>
	 * Gathers many of the settings such as volumes and soundtrack selection
	 */
	@Override
	protected void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
		bsTimer.setActivity(this);
		Bomb urgentBomb = bombs.findUrgentBomb();
		if (urgentBomb != null) {
			updateMainTime(urgentBomb.timeLeftFromElapsed(bsTimer.getElapsedMillis()));
		} else {
			updateMainTime("00:00");
		}
		SharedPreferences prefs = getSharedPreferences(SettingsActivity.BOMB_SQUAD_PREFS, MODE_PRIVATE);
		shouldPlaySoundtrack = prefs.getBoolean(SettingsActivity.SOUNDTRACK_ENABLED, true);
		soundtrackSelection = prefs.getInt(SettingsActivity.SOUNDTRACK_SELECTION, 0);
		int vol = prefs.getInt(SettingsActivity.SOUNDTRACK_VOLUME, 100);
		soundtrackVolume = (float)vol / 100.0f;
		bsTimer.enableSoundtrack(shouldPlaySoundtrack, soundtrackSelection, soundtrackVolume);
		shouldPlayBombs = prefs.getBoolean(SettingsActivity.BOMB_SOUNDS, true);
		vol = prefs.getInt(SettingsActivity.BOMB_VOLUME, 100);
		bombVolume = (float)vol / 100.0f;
		bsTimer.enableBombSounds(shouldPlayBombs, bombVolume);
		shouldPlayCountdown = prefs.getBoolean(SettingsActivity.AUDIO_ALERT, true);
		bsTimer.enableCountdown(shouldPlayCountdown);
		shouldVisuallyAlert = prefs.getBoolean(SettingsActivity.VISUAL_ALERT, true);
		bombs.setShowResumeButton(true);
		if (bsTimer.isRunning()) {
			updatePlayButton(ButtonState.PAUSE_ENABLED);
		} else {
			updatePlayButton(ButtonState.PLAY_ENABLED);
		}
	}

	/**
	 * onResume method
	 * <p>
	 * Switches facade to point to the currently resuming activity
	 */
	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
		bsTimer.setActivity(this);
	}

	public void updatePlayButton(ButtonState state) {
		switch (state) {
			case PLAY_ENABLED: // Currently paused, ready to start
				btnStart.setEnabled(true);
				btnStart.setText(R.string.play_start);
				break;

			case PAUSE_ENABLED: // Currently running, ready to pause
				btnStart.setEnabled(true);
				btnStart.setText(R.string.play_pause);
				break;

			case PLAY_DISABLED: // Currently finished game.  Can't resume
				btnStart.setEnabled(false);
				btnStart.setText(R.string.play_start);
				break;
		}
	}

	/**
	 * onClick method
	 * <p>
	 * If user clicked on bomb icon or time button, they clicked on a row in the {@link ListView}.
	 * The particular {@link Bomb} has been stored in the view's tag.
	 *
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		Bomb b;
		switch (v.getId()) {
			case R.id.btnBomb:
				b = (Bomb)v.getTag();
				defuseBomb(b);
				return;

			case R.id.btnTime:
				b = (Bomb)v.getTag();
				focusBomb(b);
				return;

			case R.id.btnStart:
				clickedStart();
				return;

			default:
				// Ignore
		}
	}

	/**
	 * User clicked on a bomb icon with the likely intention of deactivating the bomb.
	 * <p>
	 * Get confirmation, then set the {@link Bomb} state to disabled.  Suspend the timer while
	 * the dialog is displayed.
	 *
	 * @param b
	 */
	protected void defuseBomb(Bomb b) {
		Log.d(TAG, "defuseBomb(" + b + ")");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.timer_confirm);
		builder.setMessage(getString(R.string.timer_sure, b.getLevel(), b.getLetter()));
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				bombForDialog.setDisarmedMillisRemain(bombForDialog.getMillisDuration()
						- bsTimer.getElapsedMillis());
				bombForDialog.setMillisDuration(0);
				bombForDialog.setState(Bomb.BombState.DISABLED);
				burn.playDefused();
				updateBombButton(bombForDialog);
				if (isRunningDuringAlert) {
					bsTimer.start();
				}
			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (isRunningDuringAlert) {
					bsTimer.start();
				}
			}
		});
		bombForDialog = b;
		isRunningDuringAlert = bsTimer.isRunning();
		bsTimer.stop();
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void clickedStart() {
		Log.d(TAG, "Clicked " + (bsTimer.isRunning() ? "Pause" : "Start"));
		if (bsTimer.isRunning()) {
			updatePlayButton(ButtonState.PLAY_ENABLED);
			burn.pause();
			bsTimer.stop();
			bsTimer.stopMusic();
		} else {
			updatePlayButton(ButtonState.PAUSE_ENABLED);
			burn.start();
			bsTimer.start();
			bsTimer.startMusic();
		}
	}

	/* Facade methods to be implemented in AllBombsActivity or SingleBombActivity */

	protected abstract void focusBomb(Bomb b);
	public abstract void updateMainTime(String sTime);
	public abstract void updateBomb(Bomb b, int idx, long duration);
	public abstract void updateBombButton(Bomb b);
}
