/*
 * Bomb.java
 * BombSquad
 *
 * Created by Andy Foulke on 4/3/2015.
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved
 */

package com.playtmg.bombsquad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model class to keep track of a bomb in the game.  Each bomb has a level (1 through 3,
 * corresponding to cards in the game), a letter ("A", "B", or "C"), a duration in milliseconds
 * before it detonates, and a state (live, detonated, disabled).  Implements {@link Parcelable}
 * so that a bomb can be passed from one {@link android.app.Activity} to another.
 */
public class Bomb implements Parcelable {
	public enum BombState { LIVE, DETONATED, DISABLED };
	private int level;
	private String letter;
	private int nameIndex;  // integer way to represent letter (0 = "A", 1 = "B", 2 = "C")
	private long millisDuration;
	private long disarmedMillisRemain;
	private BombState state;
	private boolean isFatal;  // If true, detonation ends the game
	private final int[][] bombRes = {
			{ R.drawable.bomb1a, R.drawable.bomb1b, R.drawable.bomb1c },
			{ R.drawable.bomb2a, R.drawable.bomb2b, R.drawable.bomb2c },
			{ R.drawable.bomb3a, R.drawable.bomb3b, R.drawable.bomb3c }
	};

	/**
	 * Constructor
	 *
	 * @param level 1 through 3 inclusive
	 * @param letter "A" through "C" inclusive
	 * @param millis Milliseconds before bomb detonates
	 * @param fatal If true, detonation ends the game
	 */
	public Bomb(int level, String letter, long millis, boolean fatal) {
		this(level, letter, millis, fatal, BombState.LIVE);
	}

	/**
	 * Constructor
	 *
	 * @param level 1 through 3 inclusive
	 * @param letter "A" through "C" inclusive
	 * @param millis Milliseconds before bomb detonates
	 * @param fatal If true, detonation ends the game
	 * @param state One of LIVE, DETONATED, DISABLED
	 */
	public Bomb(int level, String letter, long millis, boolean fatal, BombState state) {
		this.level = level;
		this.letter = letter;
		this.nameIndex = letter.charAt(0) - 'A';
		this.millisDuration = millis;
		this.disarmedMillisRemain = 0;
		this.isFatal = fatal;
		this.state = state;
	}

	/**
	 * Copy constructor
	 *
	 * @param b {@link Bomb} to copy
	 */
	public Bomb(Bomb b) {
		this(b.level, b.letter, b.millisDuration, b.isFatal, b.state);
		this. disarmedMillisRemain = b.disarmedMillisRemain;
	}

	/**
	 * Test for equality.  Bomb equality only compares level/letter to ensure no duplicates.
	 *
	 * @param o {@link Object} to test against
	 *
	 * @return true if equal, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (getClass() != o.getClass()) {
			return false;
		}
		Bomb b = (Bomb)o;
		return this.level == b.level && this.letter.equals(b.letter);
	}

	@Override
	public String toString() {
		return String.format("%d%s %s", this.level, this.letter, stringFromTime(this.millisDuration));
	}

	public String stringFromTime(long millis) {
		int secs = (int)((millis / 1000) % 60);
		int mins = (int)(millis / 60000);
		return String.format("%02d:%02d", mins, secs);
	}

	public String timeLeftFromElapsed(long elapsedMillis) {
		long time = this.millisDuration - elapsedMillis;
		if (time < 0) {
			time = 0;
		}
		return stringFromTime(time);
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getLetter() {
		return letter;
	}

	public void setLetter(String letter) {
		this.letter = letter;
		this.nameIndex = letter.charAt(0) - 'A';
	}

	public int getNameIndex() {
		return nameIndex;
	}

	public void setNameIndex(int index) {
		nameIndex = index;
		letter = String.valueOf('A' + nameIndex);
	}

	public long getMillisDuration() {
		return millisDuration;
	}

	public void setMillisDuration(long millisDuration) {
		this.millisDuration = millisDuration;
	}

	public long getDisarmedMillisRemain() { return disarmedMillisRemain; }

	public void setDisarmedMillisRemain(long disarmedMillisRemain) { this.disarmedMillisRemain = disarmedMillisRemain; }

	public BombState getState() {
		return state;
	}

	public void setState(BombState state) {
		this.state = state;
	}

	public boolean isFatal() {
		return isFatal;
	}

	public void setFatal(boolean isFatal) {
		this.isFatal = isFatal;
	}

	public int getImgResId() {
		return bombRes[level - 1][nameIndex];
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(level);
		dest.writeString(letter);
		dest.writeLong(millisDuration);
		dest.writeInt(isFatal ? 1 : 0);
		dest.writeString((state == null) ? "" : state.name());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<Bomb> CREATOR = new Creator<Bomb>() {
		@Override
		public Bomb createFromParcel(Parcel source) {
			return new Bomb(
					source.readInt(),
					source.readString(),
					source.readLong(),
					source.readInt() == 1,
					BombState.valueOf(source.readString()));
		}

		@Override
		public Bomb[] newArray(int size) {
			return new Bomb[size];
		}
	};
}
