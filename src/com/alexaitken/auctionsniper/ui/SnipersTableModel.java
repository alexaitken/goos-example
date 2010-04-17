package com.alexaitken.auctionsniper.ui;

import javax.swing.table.AbstractTableModel;

import com.alexaitken.auctionsniper.SniperSnapshot;

public class SnipersTableModel extends AbstractTableModel {
	private static String[] STATUS_TEXT = { MainWindow.STATUS_JOINING,
											MainWindow.STATUS_BIDDING,
											MainWindow.STATUS_WINNING,
											MainWindow.STATUS_LOST,
											MainWindow.STATUS_WON };

	private static final SniperSnapshot START_UP = new SniperSnapshot("", 0, 0);

	private String statusText = MainWindow.STATUS_JOINING;
	private SniperSnapshot sniperSnapshot = START_UP;

	@Override
	public int getColumnCount() {
		return Column.values().length;
	}

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (Column.at(columnIndex)) {
		case ITEM_IDENTIFIER:
			return sniperSnapshot.itemId;
		case LAST_BID:
			return sniperSnapshot.lastBid;
		case LAST_PRICE:
			return sniperSnapshot.lastPrice;
		case SNIPER_STATE:
			return statusText;
		default:
			throw new IllegalArgumentException("No column at index [" + columnIndex + "]");
		}
	}
	
	public void sniperStatusChanged(SniperSnapshot newSniperSnapshot) {
		this.sniperSnapshot = newSniperSnapshot;
		this.statusText = STATUS_TEXT[newSniperSnapshot.state.ordinal()];
		fireTableRowsUpdated(0, 0);
		
	}

}
