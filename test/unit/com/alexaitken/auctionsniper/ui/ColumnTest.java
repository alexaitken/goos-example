package com.alexaitken.auctionsniper.ui;

import static org.junit.Assert.*;

import org.junit.Test;

import com.alexaitken.auctionsniper.SniperSnapshot;
import com.alexaitken.auctionsniper.SniperState;

public class ColumnTest {

	private SniperSnapshot snapshot = new SniperSnapshot("12345", 100, 400, SniperState.BIDDING);
	
	@Test
	public void should_have_item_identifier_map_to_snap_shot_item_id() {
		assertEquals("12345", Column.ITEM_IDENTIFIER.valueIn(snapshot));
	}
	
	@Test
	public void should_have_last_price_map_to_snapshot_last_price() {
		assertEquals(100, Column.LAST_PRICE.valueIn(snapshot));
	}
	
	@Test
	public void should_have_last_bid_map_to_snapshot_last_bid() {
		assertEquals(400, Column.LAST_BID.valueIn(snapshot));
	}
	
	@Test
	public void should_have_sniper_state_map_to_snapshot_sniper_state_text() {
		assertEquals(SnipersTableModel.textFor(SniperState.BIDDING), Column.SNIPER_STATE.valueIn(snapshot));
	}

	@Test
	public void should_columns_indexed_in_the_correct_order() {
		assertEquals(Column.ITEM_IDENTIFIER, Column.at(0));
		assertEquals(Column.LAST_PRICE, Column.at(1));
		assertEquals(Column.LAST_BID, Column.at(2));
		assertEquals(Column.SNIPER_STATE, Column.at(3));
	}

}
