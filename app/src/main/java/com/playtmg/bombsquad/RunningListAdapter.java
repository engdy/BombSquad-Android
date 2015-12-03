/*
 * RunningListAdapter.java
 * BombSquad
 *
 * Created by Andy Foulke on 9/5/2015
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved
 */
package com.playtmg.bombsquad;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import java.util.List;

/**
 * {@link ArrayAdapter} used by {@link AllBombsActivity} to populate {@link ListView}
 * of the session's collection of {@link Bomb}s
 */
public class RunningListAdapter extends ArrayAdapter<Bomb> {
	private Context context;
	private List<Bomb> bombs;

	public RunningListAdapter(Context context, BombList bombList) {
		super(context, R.layout.running_layout, bombList.getBombs());
		this.context = context;
		this.bombs = bombList.getBombs();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		View row = inflater.inflate(R.layout.running_layout, parent, false);
		Bomb b = bombs.get(position);
		ImageButton btnImage = (ImageButton)row.findViewById(R.id.btnBomb);
		btnImage.setOnClickListener((RunningActivity)context);
		btnImage.setTag(b);
		Button btnTime = (Button)row.findViewById(R.id.btnTime);
		btnTime.setOnClickListener((RunningActivity)context);
		btnTime.setTag(b);
		row.setTag(b);
		btnTime.setText(b.stringFromTime(b.getMillisDuration()));
		if (b.getLevel() >= 1 && b.getLevel() <= 3 && b.getNameIndex() >= 0 && b.getNameIndex() <= 2) {
			btnImage.setBackgroundResource(b.getImgResId());
		}
		if (b.getState() == Bomb.BombState.DISABLED) {
			btnImage.setEnabled(false);
			btnTime.setEnabled(false);
			btnImage.setImageResource(R.drawable.greencheck);
			btnTime.setText(b.stringFromTime(b.getDisarmedMillisRemain()));
		} else if (b.getState() == Bomb.BombState.DETONATED) {
			btnImage.setEnabled(false);
			btnTime.setEnabled(false);
			btnImage.setImageResource(R.drawable.redex);
			btnTime.setText("00:00");
		} else { // LIVE

		}
		return row;
	}
}
