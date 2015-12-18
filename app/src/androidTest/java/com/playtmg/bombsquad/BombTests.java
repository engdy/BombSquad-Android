/*
 * BombTest.java
 * BombSquad
 *
 * Created by Andy Foulke on 4/4/2015
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved
 */
package com.playtmg.bombsquad;

import android.os.Parcel;
import android.test.suitebuilder.annotation.SmallTest;
import junit.framework.TestCase;

public class BombTests extends TestCase {
	protected Bomb bomb;

	public BombTests(String name) {
		super(name);
	}

	public BombTests() {
		this("BombTests");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		bomb = new Bomb(1, "A", 65432, true);
	}

	@SmallTest
	public void testBombCreation() {
		assertEquals(1, bomb.getLevel());
		assertEquals("A", bomb.getLetter());
		assertEquals(0, bomb.getNameIndex());
		assertEquals(65432, bomb.getMillisDuration());
		assertTrue(bomb.isFatal());
		assertEquals(Bomb.BombState.LIVE, bomb.getState());
	}

	@SmallTest
	public void testBombDisplay() {
		assertEquals("1A 01:05", bomb.toString());
		assertEquals("00:34", bomb.timeLeftFromElapsed(31000));
		assertEquals("00:00", bomb.timeLeftFromElapsed(70000));
	}

	@SmallTest
	public void testBombChange() {
		bomb.setFatal(false);
		assertFalse(bomb.isFatal());
		bomb.setLetter("B");
		assertEquals("B", bomb.getLetter());
		assertEquals(1, bomb.getNameIndex());
		bomb.setLevel(1);
		assertEquals(1, bomb.getLevel());
		bomb.setMillisDuration(76543);
		assertEquals(76543, bomb.getMillisDuration());
		bomb.setState(Bomb.BombState.DETONATED);
		assertEquals(Bomb.BombState.DETONATED, bomb.getState());
	}

	@SmallTest
	public void testBombParcel() {
		Parcel parcel = Parcel.obtain();
		bomb.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);
		Bomb createdFromParcel = Bomb.CREATOR.createFromParcel(parcel);
		assertEquals(bomb, createdFromParcel);
	}

	@SmallTest
	public void testBombCopy() {
		Bomb b2 = new Bomb(bomb);
		assertEquals(b2, bomb);
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
