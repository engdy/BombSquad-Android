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
public class MissionActivity extends Activity implements View.OnTouchListener {
	public static final String TAG = MissionActivity.class.getSimpleName();
	public static final String MISSION_RESOURCE = "com.playtmg.bombsquad.MISSION_RESOURCE";
	private enum ZoomMode { NONE, DRAG, ZOOM };
	private ZoomMode mode = ZoomMode.NONE;
	private int resId = 0;
	private ImageView imgMission;
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
		imgMission = (ImageView)findViewById(R.id.imgMission);
		imgMission.setOnTouchListener(this);
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		ImageView view = (ImageView)v;
		view.setScaleType(ImageView.ScaleType.MATRIX);
		float scale;

		switch (event.getAction() & MotionEvent.ACTION_MASK) {

			case MotionEvent.ACTION_DOWN:
				matrix.set(view.getImageMatrix());
				savedMatrix.set(matrix);
				start.set(event.getX(), event.getY());
				mode = ZoomMode.DRAG;
				Log.d(TAG, "Mode = DRAG");
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				mode = ZoomMode.NONE;
				Log.d(TAG, "Mode = NONE");
				break;

			case MotionEvent.ACTION_POINTER_DOWN:
				oldDist = spacing(event);
				Log.d(TAG, "oldDist = " + oldDist);
				if (oldDist > 5.0f) {
					savedMatrix.set(matrix);
					midPoint(mid, event);
					mode = ZoomMode.ZOOM;
					Log.d(TAG, "Mode = ZOOM");
				}
				break;

			case MotionEvent.ACTION_MOVE:
				if (mode == ZoomMode.DRAG) {
					matrix.set(savedMatrix);
					matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
				} else if (mode == ZoomMode.ZOOM) {
					float newDist = spacing(event);
					Log.d(TAG, "newDist = " + newDist);
					if (newDist > 5.0f) {
						matrix.set(savedMatrix);
						scale = newDist / oldDist;
						matrix.postScale(scale, scale, mid.x, mid.y);
					}
				}
				break;
		}
		view.setImageMatrix(matrix);
		return true;
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float)Math.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
}
