/*
 * AllBombsActivity.java
 * BombSquad
 *
 * Created by Andy Foulke on 12/02/2015
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved.
 */
package com.playtmg.bombsquad;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Activity class for the running timer view which shows all bombs simultaneously.
 */
public class AllBombsActivity extends RunningActivity {
	private TextView txtMainTime;
	private ListView listBombs;
	private RunningListAdapter adapter;
	private View row;

	/**
	 * onCreate method
	 * <p>
	 * Note that the superclass {@link RunningActivity} obtains the state instances (bomb list,
	 * timer, {@link BURN} audio clip engine).
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = AllBombsActivity.class.getSimpleName();
		Log.d(TAG, "onCreate");
		setContentView(R.layout.activity_allbombs);
		btnStart = (Button)findViewById(R.id.btnStart);
		btnStart.setOnClickListener(this);
		txtMainTime = (TextView)findViewById(R.id.txtMainTime);
		txtMainTime.setOnClickListener(this);
		listBombs = (ListView)findViewById(R.id.listBombs);
		adapter = new RunningListAdapter(this, bombs);
		listBombs.setAdapter(adapter);
	}

	/**
	 * onResume method
	 * <p>
	 * Update button states in case coming back from SingleBombActivity
	 */
	@Override
	protected void onResume() {
		super.onResume();
		for (Bomb b: bombs.getBombs()) {
			updateBombButton(b);
		}
	}

	/**
	 * onDestroy method
	 * <p>
	 * Ensure all outstanding running timer and audio clips are terminated
	 */
	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		bsTimer.stop();
		burn.stopAll();
		super.onDestroy();
	}

	/**
	 * Provides implementation for {@link RunningActivity}'s updateMainTime facade
	 *
	 * @param sTime String in format "MM:SS"
	 */
	@Override
	public void updateMainTime(String sTime) {
		txtMainTime.setText(sTime);
	}

	/**
	 * User has selected a single bomb to view, so launch {@link SingleBombActivity} passing
	 * the index of this bomb.
	 * <p>
	 * This is the implementation for {@link RunningActivity}'s focusBomb facade.
	 *
	 * @param b {@link Bomb} to focus on in {@link SingleBombActivity}
	 */
	@Override
	protected void focusBomb(Bomb b) {
		int position = bombs.getBombs().indexOf(b);
		Intent intent = new Intent(this, SingleBombActivity.class);
		intent.putExtra(SingleBombActivity.BOMB_POSITION, position);
		startActivity(intent);
	}

	/**
	 * {@link BSTimer} is providing the GUI with updated details for a particular {@link Bomb}.
	 * <p>
	 * Details include time remaining, bomb state, bomb button state.  This is the implementation
	 * for {@link RunningActivity}'s updateBomb facade.
	 *
	 * @param b Particular {@link Bomb} to update on the display
	 * @param idx Index of the bomb in the {@link BombList}
	 * @param duration Milliseconds remaining before the bomb detonates.  If < 0, bomb just detonated.
	 */
	@Override
	public void updateBomb(Bomb b, int idx, long duration) {
		// Each listview row is tagged with a Bomb object - find the correct row
		row = listBombs.findViewWithTag(b);
		if (row != null) {
			Button btnTime = (Button)row.findViewById(R.id.btnTime);
			if (duration < 0) { // Detonated
				ImageButton btnBomb = (ImageButton)row.findViewById(R.id.btnBomb);
				btnBomb.setImageResource(R.drawable.redex);
				btnTime.setText("00:00");
				btnBomb.setEnabled(false);
				btnTime.setEnabled(false);
			} else { // Still ticking
				btnTime.setText(b.timeLeftFromElapsed(duration));
				if (shouldVisuallyAlert) { // If configured in settings, button should throb red for the last 30 secs
					long diff = b.getMillisDuration() - duration;
					if (diff < 30000) {
						long mag = 30000L - diff;
						long sdiff = diff % 1000L;
						int nonRedVal = Math.min(255, (int)((diff + ((mag * sdiff) / 1000L)) / 117L));
						Log.d(TAG, "nrv = " + nonRedVal + ", sdiff = " + sdiff);
						btnTime.setBackgroundColor(Color.argb(0xFF, 0xFF, nonRedVal, nonRedVal));
					}
				}
			}
		}
	}

	/**
	 * User defused the {@link Bomb} and now the button must be disabled and marked as defused
	 * <p>
	 * This is the implementation for {@link RunningActivity}'s updateBombButton facade.
	 *
	 * @param b Particular {@link Bomb} that has been defused
	 */
	@Override
	public void updateBombButton(Bomb b) {
		// Each listview row is tagged with a Bomb object - find the correct row
		row = listBombs.findViewWithTag(b);
		if (row != null) {
			ImageButton btnBomb = (ImageButton)row.findViewById(R.id.btnBomb);
			Button btnTime = (Button)row.findViewById(R.id.btnTime);
			if (b.getState() == Bomb.BombState.DISABLED) {
				btnBomb.setImageResource(R.drawable.greencheck);
				btnBomb.setEnabled(false);
				btnTime.setEnabled(false);
			} else if (b.getState() == Bomb.BombState.DETONATED) {
				btnBomb.setImageResource(R.drawable.redex);
				btnBomb.setEnabled(false);
				btnTime.setText("00:00");
				btnTime.setEnabled(false);
			}
		}
	}
}
