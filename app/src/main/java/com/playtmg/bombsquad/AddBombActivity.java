/*
 * AddBombActivity.java
 * BombSquad
 *
 * Created by Andy Foulke on 12/02/2015
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved.
 */

package com.playtmg.bombsquad;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Activity class for the Add Bomb page in TMG's BombSquad app.
 * Collects information needed to create a new Bomb object, validates the input,
 * and returns the new (parceled) bomb in the Activity result.
 */

public class AddBombActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {
	public static final String TAG = AddBombActivity.class.getSimpleName();
	private Spinner spinnerBomb;
	private NumberPicker pickerMinute;
	private NumberPicker pickerSecond;
	private ToggleButton btnFatal;
	private TextView txtTitle;
	private int bombMode;
	private Bomb editBomb;

	/**
	 * onCreate method
	 * Gets mode (add or edit) from parent activity
	 * Configures the various views
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_addbomb);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		Intent intent = getIntent();
		bombMode = intent.getIntExtra(QuickPlayActivity.BOMB_MODE, QuickPlayActivity.ADD_BOMB_MODE);
		txtTitle = (TextView)findViewById(R.id.textAddBombTitle);
		spinnerBomb = (Spinner)findViewById(R.id.spinnerBomb);
		pickerMinute = (NumberPicker)findViewById(R.id.pickerMinute);
		pickerMinute.setMinValue(0);
		pickerMinute.setMaxValue(60);
		pickerMinute.setWrapSelectorWheel(true);
		pickerSecond = (NumberPicker)findViewById(R.id.pickerSecond);
		pickerSecond.setMinValue(0);
		pickerSecond.setMaxValue(59);
		pickerSecond.setWrapSelectorWheel(true);
		btnFatal = (ToggleButton)findViewById(R.id.btnFatal);
		BombSpinnerAdapter adapter = new BombSpinnerAdapter(this);
		spinnerBomb.setAdapter(adapter);
		if (bombMode == QuickPlayActivity.ADD_BOMB_MODE) {
			pickerMinute.setValue(10);
			pickerSecond.setValue(0);
			btnFatal.setChecked(true);
		} else {
			editBomb = intent.getParcelableExtra(QuickPlayActivity.BOMB_PARCEL);
			long millis = editBomb.getMillisDuration();
			pickerMinute.setValue((int)(millis / 60000));
			pickerSecond.setValue((int)((millis / 1000) % 60));
			btnFatal.setChecked(editBomb.isFatal());
			spinnerBomb.setSelection(3 * (editBomb.getLevel() - 1) + editBomb.getNameIndex());
			txtTitle.setText(R.string.addbomb_edittitle);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		Log.d(TAG, "Selected item " + position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		Log.d(TAG, "onNothingSelected");
	}

	/**
	 * onClick handler for OK button
	 * Performs validations on bomb attributes (must be non-zero duration, can't be
	 * a duplicate of an existing bomb)
	 * Returns new (or edited) bomb in activity results/data
	 *
	 * @param btn
	 */
	public void clickedOK(View btn) {
		Log.d(TAG, "clicked OK");
		int millis = pickerMinute.getValue() * 60000 + pickerSecond.getValue() * 1000;
		if (millis == 0) {
			Toast.makeText(this, R.string.addbomb_needtime, Toast.LENGTH_SHORT).show();
			return;
		}
		int pos = spinnerBomb.getSelectedItemPosition();
		int lev = pos / 3 + 1;
		int let = pos % 3;
		if (BombSquadData.getInstance().getBombList().checkForBomb(lev, let)) {
			if (bombMode == QuickPlayActivity.ADD_BOMB_MODE || editBomb.getLevel() != lev || editBomb.getNameIndex() != let) {
				Toast.makeText(this, R.string.addbomb_duplicate, Toast.LENGTH_SHORT).show();
				return;
			}
		}
		Bomb b = new Bomb(
				lev,
				String.valueOf((char)('A' + let)),
				pickerMinute.getValue() * 60000 + pickerSecond.getValue() * 1000,
				btnFatal.isChecked());
		Intent data = new Intent();
		data.putExtra(QuickPlayActivity.BOMB_PARCEL, b);
		setResult(RESULT_OK, data);
		finish();
	}
}
