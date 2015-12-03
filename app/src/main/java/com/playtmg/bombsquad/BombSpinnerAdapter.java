/*
 * BombSpinnerAdapter.java
 * BombSquad
 *
 * Created by Andy Foulke on 4/10/2015.
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved
 */
package com.playtmg.bombsquad;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;

/**
 * {@link SpinnerAdapter} used by {@link AddBombActivity} to populate {@link android.widget.Spinner}
 * that user will select new {@link Bomb} type from
 */
public class BombSpinnerAdapter implements SpinnerAdapter {
	private Context context;
	private final int[] bombRes = {
			R.drawable.bomb1a, R.drawable.bomb1b, R.drawable.bomb1c,
			R.drawable.bomb2a, R.drawable.bomb2b, R.drawable.bomb2c,
			R.drawable.bomb3a, R.drawable.bomb3b, R.drawable.bomb3c
	};

	public BombSpinnerAdapter(Context context) {
		this.context = context;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.bombspinner_layout, parent, false);
		}
		((ImageView)convertView).setImageResource(bombRes[position]);
		return convertView;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {

	}

	@Override
	public int getCount() {
		return bombRes.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView image = (ImageView)View.inflate(context, R.layout.bombspinner_layout, null);
		image.setImageResource(bombRes[position]);
		return image;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}
}
