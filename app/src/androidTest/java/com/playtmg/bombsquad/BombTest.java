package com.playtmg.bombsquad;

import android.os.Parcel;
import junit.framework.TestCase;

/**
 * Created by engdy on 4/4/15.
 */
public class BombTest extends TestCase {
	protected Bomb bomb;

	public BombTest(String name) {
		super(name);
	}

	public BombTest() {
		this("BombTest");
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		bomb = new Bomb(1, "A", 65432, true);
	}

	public void testBombCreation() {
		assertEquals(1, bomb.getLevel());
		assertEquals("A", bomb.getLetter());
		assertEquals(0, bomb.getNameIndex());
		assertEquals(65432, bomb.getMillisDuration());
		assertTrue(bomb.isFatal());
		assertEquals(Bomb.BombState.LIVE, bomb.getState());
	}

	public void testBombDisplay() {
		assertEquals("1A 01:05.43", bomb.toString());
		assertEquals("00:34.43", bomb.timeLeftFromElapsed(31000));
		assertEquals("00:00.00", bomb.timeLeftFromElapsed(70000));
	}

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

	public void testBombParcel() {
		Parcel parcel = Parcel.obtain();
		bomb.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);
		Bomb createdFromParcel = Bomb.CREATOR.createFromParcel(parcel);
		assertEquals(bomb, createdFromParcel);
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
