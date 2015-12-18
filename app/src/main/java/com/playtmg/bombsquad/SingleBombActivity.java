/*
 * SingleBombActivity.java
 * BombSquad
 *
 * Created by Andy Foulke on 12/02/2015
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved.
 */
package com.playtmg.bombsquad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity class for the running timer view which focuses on a particular bomb
 */
public class SingleBombActivity
		extends RunningActivity
		implements ViewPager.OnPageChangeListener, View.OnClickListener {
	private ViewPager pager;
	private PagerAdapter adapter;
	private LinearLayout bombContainer;
	private Bomb currentBomb;
	private SingleBombActivity activity = this;
	public static final String BOMB_POSITION = "com.playtmg.bombsquad.BOMB_POSITION";
	private DisplayMetrics metrics;
	private float textSize;
	private List<TextView> tvList;

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
		TAG = SingleBombActivity.class.getSimpleName();
		Log.d(TAG, "onCreate");
		setContentView(R.layout.activity_singlebomb);
		btnStart = (Button)findViewById(R.id.btnStart);
		btnStart.setOnClickListener(this);
		tvList = new ArrayList<>();
		pager = (ViewPager)findViewById(R.id.pager);
		adapter = new SlidePagerAdapter();
		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(this);
		bombContainer = (LinearLayout)findViewById(R.id.bombContainer);
		currentBomb = bombs.getBomb(0);
		metrics = getResources().getDisplayMetrics();
	}

	/**
	 * onStart method
	 * <p>
	 * Ensure the focused bomb is the one requested by the user.  Dynamically populate the
	 * horizontal ListView of bombs at the top of the page.
	 */
	@Override
	protected void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
		LinearLayout.LayoutParams bombLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		Intent intent = getIntent();
		int pos = intent.getIntExtra(BOMB_POSITION, 0);
		Log.d(TAG, "Bomb " + pos);
		currentBomb = bombs.getBomb(pos);
		Log.d(TAG, "Bomb " + currentBomb);
		for (Bomb b: bombs.getBombs()) {
			ImageButton btnBomb = new ImageButton(this);
			btnBomb.setLayoutParams(bombLayout);
			btnBomb.setImageResource(b.getState() == Bomb.BombState.DISABLED ? R.drawable.greencheck
					: b.getState() == Bomb.BombState.DETONATED ? R.drawable.redex
					: b.getImgResId());
			btnBomb.setBackgroundColor(b == currentBomb ? Color.RED : Color.BLACK);
			btnBomb.setOnClickListener(this);
			btnBomb.setTag(b);
			btnBomb.setId(R.id.btnBomb);
			bombContainer.addView(btnBomb);
		}
		pager.setCurrentItem(pos, false);
	}

	/**
	 * Ignored in SingleBombActivity
	 *
	 * @param sTime
	 */
	@Override
	public void updateMainTime(String sTime) {}

	/**
	 * {@link BSTimer} is providing the GUI with updated details for a particular {@link Bomb}.
	 * <p>
	 * Details include time remaining, bomb state, bomb button state.  This is the implementation
	 * for {@link RunningActivity}'s updateBomb facade.  Note that the TextView for each bomb
	 * is a child of the ViewPager, and is tagged with the bomb object.  The ImageButton
	 * for each bomb is a child of the horizontal ListView at the top of the display, and is also
	 * tagged with the bomb object.
	 *
	 * @param b Particular {@link Bomb} to update on the display
	 * @param idx Index of the bomb in the {@link BombList}
	 * @param duration Milliseconds remaining before the bomb detonates.  If < 0, bomb just
	 *                 detonated.
	 */
	@Override
	public void updateBomb(Bomb b, int idx, long duration) {
		TextView text = (TextView)pager.findViewWithTag(b);
		if (text == null) {
			Log.d(TAG, "Couldn't find txtTime");
			return;
		}
		if (duration < 0) {
			ImageButton btnBomb = (ImageButton)bombContainer.findViewWithTag(b);
			btnBomb.setImageResource(R.drawable.redex);
			btnBomb.setEnabled(false);
			text.setText("00:00");
		} else {
			text.setText(b.timeLeftFromElapsed(duration));
		}
	}

	@Override
	protected void focusBomb(Bomb b) {
		// Do nothing
	}

	@Override
	public void updateBombButton(Bomb b) {
		ImageButton btnBomb = (ImageButton)bombContainer.findViewWithTag(b);
		if (b.getState() == Bomb.BombState.DISABLED) {
			btnBomb.setImageResource(R.drawable.greencheck);
			btnBomb.setEnabled(false);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

	@Override
	public void onPageSelected(int position) {
		Log.d(TAG, "Scrolled to bomb " + position);
		ImageButton btn = (ImageButton)bombContainer.findViewWithTag(currentBomb);
		btn.setBackgroundColor(Color.BLACK);
		currentBomb = bombs.getBomb(position);
		btn = (ImageButton)bombContainer.findViewWithTag(currentBomb);
		btn.setBackgroundColor(Color.RED);
	}

	@Override
	public void onPageScrollStateChanged(int state) {}

	@Override
	protected void defuseBomb(Bomb b) {
		if (b == currentBomb) {
			super.defuseBomb(b);
		} else {
			ImageButton btn = (ImageButton)bombContainer.findViewWithTag(currentBomb);
			btn.setBackgroundColor(Color.BLACK);
			currentBomb = b;
			btn = (ImageButton)bombContainer.findViewWithTag(currentBomb);
			btn.setBackgroundColor(Color.RED);
			pager.setCurrentItem(bombs.getBombs().indexOf(b), true);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.txtTime) {
			finish();
		} else {
			super.onClick(v);
		}
	}

	private class SlidePagerAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return bombs.bombCount();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Log.d(TAG, "instantiateItem");
			LayoutInflater inflater = (LayoutInflater)container.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.fragment_screen_slide_timer, null);
			TextView tv = (TextView)view.findViewById(R.id.txtTime);
			tv.setTag(bombs.getBomb(position));
			tv.setText(bombs.getBomb(position).timeLeftFromElapsed(bsTimer.getElapsedMillis()));
			tv.setOnClickListener(activity);
//			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 200f);
			container.addView(view, 0);
			tvList.add(tv);
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