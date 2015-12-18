/*
 * MissionActivity.java
 * BombSquad
 *
 * Created by Andy Foulke on 12/2/2015
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved
 */
package com.playtmg.bombsquad;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Activity class for the Mission viewer page in TMG's BombSquad app.  Provides pinch/zoom
 * behavior so fine details can be resolved on small screens.
 */
public class MissionActivity extends Activity {
	public static final String TAG = MissionActivity.class.getSimpleName();
	public static final String MISSION_RESOURCE = "com.playtmg.bombsquad.MISSION_RESOURCE";
	private enum ZoomMode { NONE, DRAG, ZOOM };
	private ZoomMode mode = ZoomMode.NONE;
	private int resId = 0;
	private TouchImageView imgMission;
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist = 1.0f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mission);
		imgMission = (TouchImageView)findViewById(R.id.imgMission);
	}

	@Override
	protected void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
		Intent intent = getIntent();
		resId = intent.getIntExtra(MISSION_RESOURCE, 0);
		if (resId != 0) {
			imgMission.setImageResource(resId);
		}
	}
}
