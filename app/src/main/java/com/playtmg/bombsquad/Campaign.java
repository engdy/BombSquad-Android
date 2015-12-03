/*
 * Campaign.java
 * BombSquad
 *
 * Created by Andy Foulke on 4/3/2015
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved
 */
package com.playtmg.bombsquad;

/**
 * Data class to store mission details
 */
public class Campaign {
	private String name;
	private int resource; // drawable image of mission card
	private BombList bombs;

	public Campaign(String name, int pict, BombList bl) {
		this.name = name;
		this.resource = pict;
		this.bombs = bl;
	}

	public String getName() {
		return name;
	}

	public int getPictureRes() {
		return resource;
	}

	public BombList getBombs() {
		return bombs;
	}
}
