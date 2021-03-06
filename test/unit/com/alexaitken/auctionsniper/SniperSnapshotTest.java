package com.alexaitken.auctionsniper;

import static org.junit.Assert.*;

import org.junit.Test;

public class SniperSnapshotTest {

	@Test
	public void transitionsBetweenStates() {
		final String itemId = "item id";
		SniperSnapshot joining = SniperSnapshot.joining(itemId);

		assertEquals(new SniperSnapshot(itemId, 0, 0, SniperState.JOINING), joining);

		SniperSnapshot bidding = joining.bidding(123, 234);
		assertEquals(new SniperSnapshot(itemId, 123, 234, SniperState.BIDDING), bidding);

		assertEquals(new SniperSnapshot(itemId, 456, 234, SniperState.WINNING), bidding.winning(456));
		
		assertEquals(new SniperSnapshot(itemId, 456, 234, SniperState.LOSING), bidding.losing(456));
		
		assertEquals(new SniperSnapshot(itemId, 456, 234, SniperState.LOST), bidding.losing(456).closed());
		
		assertEquals(new SniperSnapshot(itemId, 0, 0, SniperState.FAILED), bidding.failed());
		assertEquals(new SniperSnapshot(itemId, 0, 0, SniperState.FAILED), bidding.winning(100).failed());
		assertEquals(new SniperSnapshot(itemId, 0, 0, SniperState.FAILED), bidding.losing(100).failed());
		assertEquals(new SniperSnapshot(itemId, 0, 0, SniperState.FAILED), bidding.failed().closed());

		assertEquals(new SniperSnapshot(itemId, 123, 234, SniperState.LOST), bidding.closed());

		assertEquals(new SniperSnapshot(itemId, 678, 234, SniperState.WON), bidding.winning(678).closed());
	}

	@Test
	public void comparesItemIdentities() {
		assertTrue(SniperSnapshot.joining("item 1").isForSameItemAs(SniperSnapshot.joining("item 1")));
		assertFalse(SniperSnapshot.joining("item 1").isForSameItemAs(SniperSnapshot.joining("item 2")));
	}
}
