/*
 * CampaignTests.java
 * BombSquad
 *
 * Created by Andy Foulke on 12/3/2015
 * Copyright (c) 2015 Tasty Minstrel Games.  All rights reserved
 */
package com.playtmg.bombsquad;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

public class CampaignTests extends TestCase {
	private BombList bl;

	public CampaignTests(String name) { super(name); }

	public CampaignTests() { this("CampaignTests"); }

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		bl = new BombList();
		bl.addBomb(new Bomb(1, "A", 1000L, true));
		bl.addBomb(new Bomb(2, "B", 2000L, true));
		bl.addBomb(new Bomb(3, "C", 3000L, true));
	}

	@SmallTest
	public void testCampaign() {
		Campaign c = new Campaign("Camp1", 23, bl);

		assertEquals("Camp1", c.getName());
		assertEquals(23, c.getPictureRes());
		assertEquals(3, c.getBombs().bombCount());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
