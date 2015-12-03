/*
 * TimeSlidePageFragment.java
 * BombSquad
 *
 * Created by Andy Foulke on 11/14/2015
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved
 */
package com.playtmg.bombsquad;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TimeSlidePageFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_screen_slide_timer, container, false);
	}
}
