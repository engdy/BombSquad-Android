/*
 * BombListTest.java
 * BombSquad
 *
 * Created by Andy Foulke on 4/5/2015
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved
 */
package com.playtmg.bombsquad;

import android.test.suitebuilder.annotation.SmallTest;
import junit.framework.TestCase;

public class BombListTests extends TestCase {
	static final long TEN_MINUTES = 1000L * 10 * 60;
	static final long TWENTY_MINUTES = 1000L * 20 * 60;
	static final long THIRTY_MINUTES = 1000L * 30 * 60;

	protected BombList bombs;

	public BombListTests(String name) {
		super(name);
	}

	public BombListTests() {
		this("BombListTests");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		bombs = new BombList();
	}

	@SmallTest
	public void testEmptyBombList() {
		assertEquals(0, bombs.bombCount());
		assertEquals(0, bombs.getElapsedMillis());
		assertNull(bombs.findMaxTimeBomb());
		assertEquals(0, bombs.findMaxTime());
		assertEquals("00:00", bombs.maxTimeAsString());
	}

	@SmallTest
	public void testThreeFatalBombs() {
		Bomb bombA = new Bomb(1, "A", TEN_MINUTES, true);
		Bomb bombB = new Bomb(2, "B", TWENTY_MINUTES, true);
		Bomb bombC = new Bomb(3, "C", THIRTY_MINUTES, true);
		bombs.addBomb(bombA);
		bombs.addBomb(bombB);
		bombs.addBomb(bombC);

		assertEquals(3, bombs.bombCount());
		assertEquals(bombA, bombs.findMinTimeBomb());
		assertEquals(bombC, bombs.findMaxTimeBomb());
		assertEquals(THIRTY_MINUTES, bombs.findMaxTime());
		assertEquals("30:00", bombs.maxTimeAsString());
		assertEquals(bombA, bombs.findUrgentBomb());
		assertEquals(TEN_MINUTES, bombs.findUrgentTime());
		assertEquals("10:00", bombs.urgentTimeAsString());
		assertTrue("Looking for bomb 1A", bombs.checkForBomb(1, 0));
		assertFalse("Looking for non-existent bomb 2C", bombs.checkForBomb(2, 2));

		bombs.setBomb(1, new Bomb(2, "C", TWENTY_MINUTES, true));

		assertTrue("Looking for bomb 2C", bombs.checkForBomb(2, 2));
		assertFalse("Looking for non-existent bomb 2B", bombs.checkForBomb(2, 1));
	}

	@SmallTest
	public void testThreeNonFatalBombs() {
		Bomb bombA = new Bomb(1, "A", TEN_MINUTES, false);
		Bomb bombB = new Bomb(2, "B", TWENTY_MINUTES, false);
		Bomb bombC = new Bomb(3, "C", THIRTY_MINUTES, false);
		bombs.addBomb(bombA);
		bombs.addBomb(bombB);
		bombs.addBomb(bombC);

		assertEquals(3, bombs.bombCount());
		assertEquals(bombA, bombs.findMinTimeBomb());
		assertEquals(bombC, bombs.findMaxTimeBomb());
		assertEquals(THIRTY_MINUTES, bombs.findMaxTime());
		assertEquals("30:00", bombs.maxTimeAsString());
		assertEquals(bombC, bombs.findUrgentBomb());
		assertEquals(THIRTY_MINUTES, bombs.findUrgentTime());
		assertEquals("30:00", bombs.urgentTimeAsString());
	}

	@SmallTest
	public void testBombListDetails() {
		Bomb bombA = new Bomb(1, "A", TEN_MINUTES, true);
		Bomb bombB = new Bomb(2, "B", TWENTY_MINUTES, true);
		Bomb bombC = new Bomb(3, "C", THIRTY_MINUTES, true);
		bombs.addBomb(bombA);
		bombs.addBomb(bombB);
		bombs.addBomb(bombC);

		bombs.removeBombAtIndex(1);
		assertEquals(2, bombs.bombCount());
		assertFalse("Checking for 2B gone", bombs.checkForBomb(2, 1));

		assertFalse("Resume defaults to false", bombs.showResumeButton());
		bombs.setShowResumeButton(true);
		assertTrue("Resume now true", bombs.showResumeButton());
		assertEquals(bombC, bombs.getBomb(1));
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
