/*
 * CampaignSpinnerAdapter.java
 * BombSquad
 *
 * Created by Andy Foulke on 4/9/2015
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved
 */
package com.playtmg.bombsquad;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import java.util.List;

/**
 * {@link SpinnerAdapter} used by {@link CampaignActivity} to populate {@link android.widget.Spinner}
 * that user will select {@link Campaign} from
 */
public class CampaignSpinnerAdapter implements SpinnerAdapter {
	private Context context;
	private List<Campaign> campaignList;

	public CampaignSpinnerAdapter(Context context, List<Campaign> campaignList) {
		this.context = context;
		this.campaignList = campaignList;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
		}
		((TextView)convertView).setText(campaignList.get(position).getName());
		return convertView;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {}

	@Override
	public int getCount() {
		return campaignList.size();
	}

	@Override
	public Object getItem(int position) {
		return campaignList.get(position);
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
		TextView textView = (TextView)View.inflate(context, android.R.layout.simple_spinner_item, null);
		textView.setText(campaignList.get(position).getName());
		return textView;
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
		return campaignList.isEmpty();
	}
}
