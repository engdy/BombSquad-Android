/*
 * QuickPlayActivity.java
 * BombSquad
 *
 * Created by Andy Foulke on 12/2/2015
 * Last edited by Andy Foulke on 12/17/2015
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved
 */
package com.playtmg.bombsquad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity class for the quick mission creation page in TMG's BombSquad app
 */
public class QuickPlayActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
	public static final String TAG = QuickPlayActivity.class.getSimpleName();

	public static final int ADD_BOMB_MODE = 1;
	public static final int EDIT_BOMB_MODE = 2;

	public static final String BOMB_PARCEL = "com.playtmg.bombsquad.bomb";
	public static final String BOMB_MODE = "com.playtmg.bombsquad.mode";

	private Button btnStart;
	private Button btnAdd;
	private TextView textTotalTime;
	private ListView listBombs;
	private BombList bombList;
	private BombListAdapter adapter;
	private int editBombNumber = 0;

	/**
	 * onCreate method
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_quickplay);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		btnStart = (Button)findViewById(R.id.btnStart);
		btnAdd = (Button)findViewById(R.id.btnAdd);
		textTotalTime = (TextView)findViewById(R.id.textTotalTime);
		bombList = BombSquadData.getInstance().getBombList();
		listBombs = (ListView)findViewById(R.id.listBombs);
		adapter = new BombListAdapter(this, bombList);
		listBombs.setAdapter(adapter);
		listBombs.setOnItemClickListener(this);
		listBombs.setOnItemLongClickListener(this);
		checkButtons();
	}

	/**
	 * onActivityResult retrieves the bomb that was created or edited by AddBombActivity
	 *
	 * @param requestCode distinguish between Add and Edit modes
	 * @param resultCode Should be RESULT_OK
	 * @param data Parceled {@link Bomb} instance to add/replace to the list
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Bomb b = (Bomb)data.getParcelableExtra(BOMB_PARCEL);
			if (requestCode == ADD_BOMB_MODE) {
				bombList.addBomb(b);
			} else if (requestCode == EDIT_BOMB_MODE) {
				bombList.setBomb(editBombNumber, b);
			}
			checkButtons();
			((BaseAdapter)listBombs.getAdapter()).notifyDataSetChanged();
		}
	}

	/**
	 * When a user clicks on a {@link Bomb} in the {@link ListView}, go to {@link AddBombActivity} to edit the details
	 *
	 * @param parent
	 * @param view
	 * @param position Position of selected {@link Bomb} in the {@link ListView}
	 * @param id
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d(TAG, "Clicked row " + position);
		editBombNumber = position;
		Bomb b = bombList.getBomb(position);
		Intent intent = new Intent(this, AddBombActivity.class);
		intent.putExtra(BOMB_PARCEL, b);
		intent.putExtra(BOMB_MODE, EDIT_BOMB_MODE);
		startActivityForResult(intent, EDIT_BOMB_MODE);
	}

	/**
	 * When a user long-clicks on a {@link Bomb} in the {@link ListView}, they are most likely
	 * trying to delete it.  Get confirmation
	 *
	 * @param parent
	 * @param view
	 * @param position Position of selected {@link Bomb} in the {@link ListView}
	 * @param id
	 * @return true - event handled
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
		Log.d(TAG, "Long-clicked row " + position);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Delete");
		alert.setMessage("Do you want delete this bomb?");
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				bombList.removeBombAtIndex(position);
				adapter.notifyDataSetChanged();
				checkButtons();
			}
		});
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.show();
		return true;
	}

	public void clickedStart(View btn) {
		Log.d(TAG, "Clicked start");
		BombSquadData.getInstance().getTimer().reset();
		Intent intent = new Intent(this, AllBombsActivity.class);
		startActivity(intent);
		finish();
	}

	public void clickedAdd(View btn) {
		Log.d(TAG, "Clicked add");
		Intent intent = new Intent(this, AddBombActivity.class);
		intent.putExtra(BOMB_MODE, ADD_BOMB_MODE);
		startActivityForResult(intent, ADD_BOMB_MODE);
	}

	private void checkButtons() {
		textTotalTime.setText(bombList.maxTimeAsString());
		int count = bombList.bombCount();
		btnStart.setEnabled(count > 0);
		btnAdd.setEnabled(count < 5);  // Can't have more than 4 bombs per game designer
	}
}
