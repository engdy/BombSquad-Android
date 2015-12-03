/*
 * HelpActivity.java
 * BombSquad
 *
 * Created by Andy Foulke on 12/2/2015
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved.
 */
package com.playtmg.bombsquad;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Activity class for the Help page in TMG's BombSquad app.  Provides ViewPager to scroll
 * through various pages of help instructions.
 */
public class HelpActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener {
	public static final String TAG = HelpActivity.class.getSimpleName();
	private ViewPager pager;
	private PagerAdapter adapter;
	private ImageView imgLeft;
	private ImageView imgRight;
	private int currentPos = 0;
	private int[] helpResId = {
			R.drawable.help1,
			R.drawable.help2,
			R.drawable.help3,
			R.drawable.help4,
			R.drawable.help5,
			R.drawable.help6,
			R.drawable.help7,
			R.drawable.help8,
			R.drawable.help9,
			R.drawable.help10,
			R.drawable.help11,
			R.drawable.help12,
			R.drawable.help13,
			R.drawable.help14
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.activity_help);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		pager = (ViewPager)findViewById(R.id.pager);
		adapter = new SlidePagerAdapter();
		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(this);
		imgLeft = (ImageView)findViewById(R.id.imgLeft);
		imgLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentPos > 0) {
					pager.setCurrentItem(--currentPos, true);
				}
			}
		});
		imgRight = (ImageView)findViewById(R.id.imgRight);
		imgRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentPos < helpResId.length - 1) {
					pager.setCurrentItem(++currentPos, true);
				}
			}
		});
		checkButtons();
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		currentPos = position;
		checkButtons();
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	private void checkButtons() {
		imgLeft.setVisibility(currentPos == 0 ? View.INVISIBLE : View.VISIBLE);
		imgRight.setVisibility(currentPos == helpResId.length - 1 ? View.INVISIBLE : View.VISIBLE);
	}

	private class SlidePagerAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return helpResId.length;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			LayoutInflater inflater = (LayoutInflater)container.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.fragment_screen_slide_help, null);
			ImageView iv = (ImageView)view.findViewById(R.id.imgHelp);
			iv.setImageResource(helpResId[position]);
			container.addView(view, 0);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View)object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((View)object);
		}
	}
}
