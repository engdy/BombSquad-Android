/*
 * CampaignActivity.java
 * BombSquad
 *
 * Created by Andy Foulke on 12/2/2015
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved
 */
package com.playtmg.bombsquad;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity class for the campaign selection page in TMG's BombSquad app.
 */
public class CampaignActivity
		extends ActionBarActivity
		implements AdapterView.OnItemSelectedListener, View.OnClickListener, ViewPager.OnPageChangeListener {
	public static final String TAG = CampaignActivity.class.getSimpleName();
	private Spinner spinnerCampaign;
	private ViewPager pager;
	private PagerAdapter pagerAdapter;
	private CampaignActivity activity = this;
//	private ImageView imgCampaign;
	private List<Campaign> campaignList;
	private Campaign selectedCampaign;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_campaign);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		buildCampaigns();
		spinnerCampaign = (Spinner)findViewById(R.id.spinnerCampaign);
		spinnerCampaign.setOnItemSelectedListener(this);
		CampaignSpinnerAdapter adapter = new CampaignSpinnerAdapter(this, campaignList);
		spinnerCampaign.setAdapter(adapter);
		pager = (ViewPager)findViewById(R.id.pagerCampaign);
		pagerAdapter = new SlidePagerAdapter();
		pager.setAdapter(pagerAdapter);
		pager.setOnPageChangeListener(this);
//		imgCampaign = (ImageView)findViewById(R.id.imageCampaign);
//		imgCampaign.setOnClickListener(this);
		selectionResults(0);
	}

	public void clickedStart(View btn) {
		Log.d(TAG, "Clicked start");
		int position = spinnerCampaign.getSelectedItemPosition();
		Campaign selectedCampaign = campaignList.get(position);
		BombSquadData.getInstance().setBombList(new BombList(selectedCampaign.getBombs()));
		BombSquadData.getInstance().getTimer().reset();
		Intent intent = new Intent(this, AllBombsActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.imgHelp) {
			clickedImage(v);
		}
	}

	public void clickedImage(View img) {
		Log.d(TAG, "Clicked image");
		Intent intent = new Intent(this, MissionActivity.class);
		intent.putExtra(MissionActivity.MISSION_RESOURCE, selectedCampaign.getPictureRes());
		startActivity(intent);
	}

	private void selectionResults(int position) {
		selectedCampaign = campaignList.get(position);
		Log.d(TAG, "Selected " + selectedCampaign.getName());
//		imgCampaign.setImageResource(selectedCampaign.getPictureRes());
		pager.setCurrentItem(position);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		selectionResults(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		selectedCampaign = campaignList.get(position);
		Log.d(TAG, "Selected " + selectedCampaign.getName());
		spinnerCampaign.setSelection(position, true);
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	private void buildCampaigns() {
		campaignList = new ArrayList<>();
		BombList bl = new BombList();
		bl.addBomb(new Bomb(1, "A", 8 * 60 * 1000, true));
		bl.addBomb(new Bomb(2, "B", 16 * 60 * 1000, true));
		campaignList.add(new Campaign("Training Mission Alpha", R.drawable.scena, bl));
		bl = new BombList();
		bl.addBomb(new Bomb(1, "A", 8 * 60 * 1000, true));
		bl.addBomb(new Bomb(2, "B", 16 * 60 * 1000, true));
		campaignList.add(new Campaign("Training Mission Bravo", R.drawable.scenb, bl));
		bl = new BombList();
		bl.addBomb(new Bomb(1, "A", 10 * 60 * 1000, true));
		bl.addBomb(new Bomb(2, "B", 20 * 60 * 1000, true));
		campaignList.add(new Campaign("Mission #1", R.drawable.scen1, bl));
		bl = new BombList();
		bl.addBomb(new Bomb(1, "A", 10 * 60 * 1000, true));
		bl.addBomb(new Bomb(2, "B", 20 * 60 * 1000, true));
		campaignList.add(new Campaign("Mission #2", R.drawable.scen2, bl));
		bl = new BombList();
		bl.addBomb(new Bomb(1, "A", 10 * 60 * 1000, true));
		bl.addBomb(new Bomb(2, "B", 20 * 60 * 1000, true));
		campaignList.add(new Campaign("Mission #3", R.drawable.scen3, bl));
		bl = new BombList();
		bl.addBomb(new Bomb(1, "A", 10 * 60 * 1000, true));
		bl.addBomb(new Bomb(2, "B", 20 * 60 * 1000, true));
		campaignList.add(new Campaign("Mission #4", R.drawable.scen4, bl));
		bl = new BombList();
		bl.addBomb(new Bomb(2, "A", 20 * 60 * 1000, true));
		bl.addBomb(new Bomb(3, "B", 30 * 60 * 1000, true));
		bl.addBomb(new Bomb(3, "C", 30 * 60 * 1000, true));
		campaignList.add(new Campaign("Mission #5", R.drawable.scen5, bl));
		bl = new BombList();
		bl.addBomb(new Bomb(1, "A", 10 * 60 * 1000, false));
		bl.addBomb(new Bomb(2, "B", 20 * 60 * 1000, false));
		bl.addBomb(new Bomb(3, "C", 30 * 60 * 1000, true));
		campaignList.add(new Campaign("Mission #6", R.drawable.scen6, bl));
		bl = new BombList();
		bl.addBomb(new Bomb(1, "A", 10 * 60 * 1000, true));
		bl.addBomb(new Bomb(2, "B", 20 * 60 * 1000, true));
		bl.addBomb(new Bomb(3, "C", 30 * 60 * 1000, true));
		campaignList.add(new Campaign("Mission #7", R.drawable.scen7, bl));
		bl = new BombList();
		bl.addBomb(new Bomb(1, "A", 10 * 60 * 1000, true));
		bl.addBomb(new Bomb(2, "B", 20 * 60 * 1000, true));
		bl.addBomb(new Bomb(3, "C", 30 * 60 * 1000, true));
		campaignList.add(new Campaign("Mission #8", R.drawable.scen8, bl));
		bl = new BombList();
		bl.addBomb(new Bomb(1, "A", 10 * 60 * 1000, true));
		bl.addBomb(new Bomb(2, "B", 20 * 60 * 1000, true));
		bl.addBomb(new Bomb(3, "C", 30 * 60 * 1000, true));
		campaignList.add(new Campaign("Mission #9", R.drawable.scen9, bl));
		bl = new BombList();
		bl.addBomb(new Bomb(1, "A", 10 * 60 * 1000, true));
		bl.addBomb(new Bomb(2, "B", 20 * 60 * 1000, true));
		bl.addBomb(new Bomb(3, "C", 30 * 60 * 1000, true));
		campaignList.add(new Campaign("Mission #10", R.drawable.scen10, bl));
		bl = new BombList();
		bl.addBomb(new Bomb(1, "A", 10 * 60 * 1000, true));
		bl.addBomb(new Bomb(2, "B", 20 * 60 * 1000, true));
		bl.addBomb(new Bomb(3, "C", 30 * 60 * 1000, true));
		campaignList.add(new Campaign("Mission #11", R.drawable.scen11, bl));
		bl = new BombList();
		bl.addBomb(new Bomb(1, "A", 10 * 60 * 1000, true));
		bl.addBomb(new Bomb(2, "B", 20 * 60 * 1000, true));
		bl.addBomb(new Bomb(3, "C", 30 * 60 * 1000, true));
		campaignList.add(new Campaign("Mission #12", R.drawable.scen12, bl));
	}

	private class SlidePagerAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return campaignList.size();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View)object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			LayoutInflater inflater = (LayoutInflater)container.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.fragment_screen_slide_help, null);
			ImageView iv = (ImageView)view.findViewById(R.id.imgHelp);
			iv.setImageResource(campaignList.get(position).getPictureRes());
			iv.setOnClickListener(activity);
			container.addView(view, 0);
			return view;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((View)object);
		}
	}
}
