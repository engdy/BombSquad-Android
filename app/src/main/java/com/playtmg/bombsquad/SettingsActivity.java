/*
 * SettingsActivity.java
 * BombSquad
 *
 * Created by Andy Foulke on 12/02/2015
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved.
 */

package com.playtmg.bombsquad;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingsActivity
		extends ActionBarActivity
		implements AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener {
	public static final String TAG = SettingsActivity.class.getSimpleName();
	private static final String TMG = "com.playtmg.bombsquad";
	public static final String SOUNDTRACK_SELECTION = TMG + ".SOUNDTRACK_SELECTION";
	public static final String SOUNDTRACK_VOLUME = TMG + ".SOUNDTRACK_VOLUME";
	public static final String SOUNDTRACK_ENABLED = TMG + ".SOUNDTRACK_ENABLED";
	public static final String VISUAL_ALERT = TMG + ".VISUAL_ALERT";
	public static final String AUDIO_ALERT = TMG + ".AUDIO_ALERT";
	public static final String BOMB_SOUNDS = TMG + ".BOMB_SOUNDS";
	public static final String BOMB_VOLUME = TMG + ".BOMB_VOLUME";
	public static final String BURN_LEVEL = TMG + ".BURN_LEVEL";
	public static final String BOMB_SQUAD_PREFS = "BSprefs";
	private final String[] soundtrackTitle = {
			"A Mission",
			"Heartbeat",
			"Hidden Agenda",
			"Hitman",
			"Impending Boom",
			"Mechanical",
			"Spy Groove",
			"Ticking Clock"
	};
	public static final int[] soundtrackRes = {
			R.raw.amission,
			R.raw.heartbeat,
			R.raw.hiddenagenda,
			R.raw.hitman,
			R.raw.impendingboom,
			R.raw.mechanical,
			R.raw.spygroove,
			R.raw.tickingclock
	};
	private final int[] burnRes = {
			R.string.stg_burn_zero,
			R.string.stg_burn_low,
			R.string.stg_burn_high,
			R.string.stg_burn_max
	};
	private ToggleButton btnVisualAlert;
	private ToggleButton btnAudioAlert;
	private ToggleButton btnBombSounds;
	private ToggleButton btnSoundtrack;
	private SeekBar sliderSoundtrackVolume;
	private SeekBar sliderBombVolume;
	private SeekBar sliderBURNLevel;
	private Spinner spinnerSoundtrack;
	private TextView textBURNDesc;
	private MediaPlayer soundtrackPlayer;
	private MediaPlayer bombPlayer;
	private boolean isSoundtrackPlaying = false;
	private boolean shouldPlaySoundtrack = false;
	private float soundtrackVolume = (float)1.0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_settings);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		btnVisualAlert = (ToggleButton)findViewById(R.id.btnVisualAlert);
		btnAudioAlert = (ToggleButton)findViewById(R.id.btnAudioAlert);
		btnBombSounds = (ToggleButton)findViewById(R.id.btnBombSounds);
		btnSoundtrack = (ToggleButton)findViewById(R.id.btnSoundtrack);
		sliderSoundtrackVolume = (SeekBar)findViewById(R.id.sliderSoundtrackVolume);
		sliderSoundtrackVolume.setOnSeekBarChangeListener(this);
		sliderBombVolume = (SeekBar)findViewById(R.id.sliderBombVolume);
		sliderBombVolume.setOnSeekBarChangeListener(this);
		sliderBURNLevel = (SeekBar)findViewById(R.id.sliderBURNLevel);
		sliderBURNLevel.setOnSeekBarChangeListener(this);
		spinnerSoundtrack = (Spinner)findViewById(R.id.spinnerSoundtrack);
		ArrayAdapter<String> soundtrackAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, soundtrackTitle);
		spinnerSoundtrack.setAdapter(soundtrackAdapter);
		spinnerSoundtrack.setOnItemSelectedListener(this);
		textBURNDesc = (TextView)findViewById(R.id.textBURNDescription);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	@Override
	protected void onStart() {
		super.onStart();
		SharedPreferences prefs = getSharedPreferences(BOMB_SQUAD_PREFS, MODE_PRIVATE);
		btnBombSounds.setChecked(prefs.getBoolean(BOMB_SOUNDS, true));
		btnVisualAlert.setChecked(prefs.getBoolean(VISUAL_ALERT, true));
		btnAudioAlert.setChecked(prefs.getBoolean(AUDIO_ALERT, true));
		shouldPlaySoundtrack = prefs.getBoolean(SOUNDTRACK_ENABLED, true);
		btnSoundtrack.setChecked(shouldPlaySoundtrack);
		spinnerSoundtrack.setEnabled(shouldPlaySoundtrack);
		int soundtrackSelection = prefs.getInt(SOUNDTRACK_SELECTION, 0);
		spinnerSoundtrack.setSelection(soundtrackSelection);
		int vol = prefs.getInt(SOUNDTRACK_VOLUME, 100);
		soundtrackVolume = (float)vol / 100.0f;
		sliderSoundtrackVolume.setProgress(vol);
		sliderBombVolume.setProgress(prefs.getInt(BOMB_VOLUME, 100));
		int burn = prefs.getInt(BURN_LEVEL, 1);
		sliderBURNLevel.setProgress(burn);
		textBURNDesc.setText(burnRes[burn]);
		playSoundtrack(soundtrackRes[soundtrackSelection]);
	}

	private void stopSoundtrack() {
		if (isSoundtrackPlaying) {
			soundtrackPlayer.stop();
			soundtrackPlayer = null;
			isSoundtrackPlaying = false;
		}
	}

	private void playSoundtrack(int resId) {
		stopSoundtrack();
		if (shouldPlaySoundtrack) {
			soundtrackPlayer = MediaPlayer.create(this, resId);
			soundtrackPlayer.setLooping(true);
			soundtrackPlayer.setVolume(soundtrackVolume, soundtrackVolume);
			soundtrackPlayer.start();
			isSoundtrackPlaying = true;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		stopSoundtrack();
		if (bombPlayer != null) {
			bombPlayer.stop();
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		Log.d(TAG, "Selected " + soundtrackTitle[position] + " soundtrack");
		SharedPreferences prefs = getSharedPreferences(BOMB_SQUAD_PREFS, MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		int selection = spinnerSoundtrack.getSelectedItemPosition();
		editor.putInt(SOUNDTRACK_SELECTION, selection);
		editor.commit();
		playSoundtrack(soundtrackRes[selection]);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		Log.d(TAG, "Soundtrack spinner: Nothing selected");
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		SharedPreferences prefs = getSharedPreferences(BOMB_SQUAD_PREFS, MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		if (seekBar == sliderSoundtrackVolume) {
			int prog = sliderSoundtrackVolume.getProgress();
			Log.d(TAG, "Soundtrack volume changed to " + prog);
			editor.putInt(SOUNDTRACK_VOLUME, prog);
			float vol = (float)((double)prog / 100.0);
			if (isSoundtrackPlaying) {
				soundtrackPlayer.setVolume(vol, vol);
			}
		} else if (seekBar == sliderBombVolume) {
			int prog = sliderBombVolume.getProgress();
			Log.d(TAG, "Bomb volume changed to " + prog);
			editor.putInt(BOMB_VOLUME, prog);
			float vol = (float)((double)prog / 100.0);
			if (bombPlayer != null) {
				bombPlayer.stop();
			}
			bombPlayer = MediaPlayer.create(this, R.raw.smallbomb);
			bombPlayer.setVolume(vol, vol);
			bombPlayer.start();
		} else {
			int prog = sliderBURNLevel.getProgress();
			Log.d(TAG, "BURN level now at " + prog);
			editor.putInt(BURN_LEVEL, prog);
			textBURNDesc.setText(burnRes[prog]);
			BombSquadData.getInstance().getBurn().setBurnLevel(prog);
			BombSquadData.getInstance().getBurn().resetClips();
		}
		editor.commit();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	public void clickedVisualAlert(View btn) {
		Log.d(TAG, "Clicked Visual Alert button");
		SharedPreferences prefs = getSharedPreferences(BOMB_SQUAD_PREFS, MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(VISUAL_ALERT, btnVisualAlert.isChecked());
		editor.commit();
	}

	public void clickedAudioAlert(View btn) {
		Log.d(TAG, "Clicked Audio Alert button");
		SharedPreferences prefs = getSharedPreferences(BOMB_SQUAD_PREFS, MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(AUDIO_ALERT, btnAudioAlert.isChecked());
		editor.commit();
	}

	public void clickedBombSounds(View btn) {
		Log.d(TAG, "Clicked bomb sounds button");
		SharedPreferences prefs = getSharedPreferences(BOMB_SQUAD_PREFS, MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		boolean playBombs = btnBombSounds.isChecked();
		editor.putBoolean(BOMB_SOUNDS, playBombs);
		editor.commit();
		sliderBombVolume.setEnabled(playBombs);
		if (!playBombs && bombPlayer != null) {
			bombPlayer.stop();
		}
	}

	public void clickedSoundtrack(View btn) {
		Log.d(TAG, "Clicked soundtrack");
		boolean isChecked = btnSoundtrack.isChecked();
		spinnerSoundtrack.setEnabled(isChecked);
		sliderSoundtrackVolume.setEnabled(isChecked);
		SharedPreferences prefs = getSharedPreferences(BOMB_SQUAD_PREFS, MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(SOUNDTRACK_ENABLED, isChecked);
		editor.commit();
		if (isChecked) {
			playSoundtrack(soundtrackRes[spinnerSoundtrack.getSelectedItemPosition()]);
		} else {
			stopSoundtrack();
		}
	}
}
