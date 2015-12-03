/*
 * BombListAdapter.java
 * BombSquad
 *
 * Created by Andy Foulke on 4/9/2015.
 * Copyright (c) 2015 Tasty Minstrel Games,  All rights reserved
 */
package com.playtmg.bombsquad;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

/**
 * {@link ArrayAdapter} used by {@link QuickPlayActivity} to populate {@link android.widget.ListView}
 * of the session's collection of {@link Bomb}s
 */
public class BombListAdapter extends ArrayAdapter<Bomb> {
	private Context context;
	private List<Bomb> bombs;

	public BombListAdapter(Context context, BombList bombList) {
		super(context, R.layout.bomblist_layout, bombList.getBombs());
		this.context = context;
		this.bombs = bombList.getBombs();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		BombHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(R.layout.bomblist_layout, parent, false);

			holder = new BombHolder();
			holder.image = (ImageView)row.findViewById(R.id.bombImage);
			holder.text = (TextView)row.findViewById(R.id.textTime);

			row.setTag(holder);
		} else {
			holder = (BombHolder)row.getTag();
		}

		Bomb b = bombs.get(position);
		holder.text.setText(b.stringFromTime(b.getMillisDuration()));
		if (b.getLevel() >= 1 && b.getLevel() <= 3 && b.getNameIndex() >= 0 && b.getNameIndex() <= 2) {
			holder.image.setImageResource(b.getImgResId());
		}

		return row;
	}

	static class BombHolder {
		ImageView image;
		TextView text;
	}
}
