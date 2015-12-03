package com.playtmg.bombsquad;

import junit.framework.TestCase;

/**
 * Created by engdy on 4/5/15.
 */
public class BombListTest extends TestCase {
	protected BombList bombs;

	public BombListTest(String name) {
		super(name);
	}

	public BombListTest() {
		this("BombListTest");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		bombs = new BombList();
	}

	public void testEmptyBombList() {
		assertEquals(0, bombs.bombCount());
		assertEquals(0, bombs.getElapsedMillis());
		assertNull(bombs.findMaxTimeBomb());
		assertEquals(0, bombs.findMaxTime());
		assertEquals("00:00.00", bombs.maxTimeAsString());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
