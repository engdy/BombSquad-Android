/*
 * BombSquadData.java
 * BombSquad
 *
 * Created by Andy Foulke on 4/9/2015.
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved.
 */
package com.playtmg.bombsquad;

/**
 * Singleton class to maintain the instance of {@link BombList}, {@link BURN}, and {@link BSTimer}
 * used by the application.  Keeping the instances here allows them to survive various
 * {@link android.app.Activity} lifecycles.
 */
public class BombSquadData {
	private static final BombSquadData instance = new BombSquadData();
	private BombList bombList;
	private BURN burn;
	private BSTimer timer;

	public BombList getBombList() {
		return bombList;
	}

	public void setBombList(BombList bombList) {
		this.bombList = bombList;
	}

	public BURN getBurn() {
		return burn;
	}

	public void setBurn(BURN burn) {
		this.burn = burn;
	}

	public BSTimer getTimer() {
		return timer;
	}

	public void setTimer(BSTimer timer) {
		this.timer = timer;
	}

	private BombSquadData() {}

	public static BombSquadData getInstance() {
		return instance;
	}
}
