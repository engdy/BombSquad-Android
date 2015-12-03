/*
 * BombList.java
 * BombSquad
 *
 * Created by Andy Foulke on 4/3/2015.
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved
 */
package com.playtmg.bombsquad;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of bombs for a session of the BombSquad boardgame.
 */
public class BombList {
	private List<Bomb> bombs;
	private int elapsedMillis = 0;
	private boolean showResume = false;  // Used to show Resume button on main screen - will be true if session has started

	/**
	 * Default constructor
	 */
	public BombList() {
		bombs = new ArrayList<>();
		elapsedMillis = 0;
		showResume = false;
	}

	/**
	 * Copy constructor
	 *
	 * @param orig {@link BombList} to copy
	 */
	public BombList(BombList orig) {
		this();
		for (Bomb b : orig.bombs) {
			bombs.add(new Bomb(b));
		}
	}

	/**
	 * Return the bomb with the most time remaining before detonation
	 *
	 * @return {@link Bomb} with most time remaining
	 */
	public Bomb findMaxTimeBomb() {
		long maxTime = 0;
		Bomb max = null;
		for (Bomb b: bombs) {
			if ((b.getState() == Bomb.BombState.LIVE) && (b.getMillisDuration() > maxTime)) {
				max = b;
				maxTime = b.getMillisDuration();
			}
		}
		return max;
	}

	/**
	 * Return the bomb with the least time remaining before detonation
	 *
	 * @return {@link Bomb} with the least time remaining
	 */
	public Bomb findMinTimeBomb() {
		long minTime = Long.MAX_VALUE;
		Bomb min = null;
		for (Bomb b: bombs) {
			if ((b.getState() == Bomb.BombState.LIVE) && (b.getMillisDuration() < minTime)) {
				min = b;
				minTime = b.getMillisDuration();
			}
		}
		return min;
	}

	/**
	 * Find the {@link Bomb} that will end the game.  For fatal bombs, it would be the one
	 * with the least time remaining.  For non-fatal bombs, it will be the one with the most
	 * time remaining.
	 *
	 * @return {@link Bomb} that will end the game
	 */
	public Bomb findUrgentBomb() {
		long minTime = Long.MAX_VALUE;
		long maxTime = 0;
		Bomb minUrgent = null;
		Bomb maxUrgent = null;
		for (Bomb b: bombs) {
			if (b.getState() == Bomb.BombState.LIVE) {
				if (b.isFatal()) {
					if (b.getMillisDuration() < minTime) {
						minTime = b.getMillisDuration();
						minUrgent = b;
					}
				} else {
					if (b.getMillisDuration() > maxTime) {
						maxTime = b.getMillisDuration();
						maxUrgent = b;
					}
				}
			}
		}
		return minUrgent != null ? minUrgent : maxUrgent;
	}

	/**
	 * Return the number of milliseconds for the longest duration bomb.
	 *
	 * @return milliseconds remaining
	 */
	public long findMaxTime() {
		Bomb b = findMaxTimeBomb();
		if (b != null) {
			return b.getMillisDuration();
		}
		return 0;
	}

	/**
	 * Return the number of milliseconds for the current game-ending bomb
	 *
	 * @return milliseconds remaining
	 */
	public long findUrgentTime() {
		Bomb b = findUrgentBomb();
		if (b != null) {
			return b.getMillisDuration();
		}
		return 0;
	}

	/**
	 * Return the time remaining, in {@link String} form, for the longest duration bomb.
	 *
	 * @return String in pattern "MM:SS"
	 */
	public String maxTimeAsString() {
		Bomb b = findMaxTimeBomb();
		if (b != null) {
			return b.stringFromTime(b.getMillisDuration());
		}
		return "00:00";
	}

	/**
	 * Return the time remaining, in {@link String} form, for the current game-ending bomb.
	 *
	 * @return String in pattern "MM:SS"
	 */
	public String urgentTimeAsString() {
		Bomb b = findUrgentBomb();
		if (b != null) {
			return b.stringFromTime(b.getMillisDuration());
		}
		return "00:00";
	}

	/**
	 * See if the current collection of bombs includes one with a particular level/letter.
	 *
	 * @param lev 1 through 3 inclusive
	 * @param let 0 through 2 inclusive
	 * @return True if collection contains the requested bomb, false otherwise
	 */
	public Boolean checkForBomb(int lev, int let) {
		for (Bomb b: bombs) {
			if (lev == b.getLevel() && let == b.getNameIndex()) {
				return true;
			}
		}
		return false;
	}

	public void addBomb(Bomb b) {
		bombs.add(b);
	}

	public void setBomb(int i, Bomb b) {
		bombs.set(i, b);
	}

	public void removeBombAtIndex(int i) {
		bombs.remove(i);
	}

	public List<Bomb> getBombs() {
		return bombs;
	}

	public Bomb getBomb(int position) {
		return bombs.get(position);
	}

	public int getElapsedMillis() {
		return elapsedMillis;
	}

	public void setElapsedMillis(int elapsedMillis) {
		this.elapsedMillis = elapsedMillis;
	}

	public int bombCount() {
		return bombs.size();
	}

	public boolean showResumeButton() { return showResume; }

	public void setShowResumeButton(boolean show) { this.showResume = show; }
}
