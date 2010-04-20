package com.alexaitken.auctionsniper.ui;

import javax.swing.table.AbstractTableModel;

import com.alexaitken.auctionsniper.SniperListener;
import com.alexaitken.auctionsniper.SniperSnapshot;
import com.alexaitken.auctionsniper.SniperState;

public class SnipersTableModel extends AbstractTableModel implements SniperListener {
	private static final SniperSnapshot START_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
	
	private static final String[] STATUS_TEXT = { "JOIN", "BIDDING", "WINNING", "LOST", "WON" };
	
	public static String textFor(SniperState state) {
		return STATUS_TEXT[state.ordinal()];
	}

	
	private SniperSnapshot sniperSnapshot = START_UP;

	
	
	
	@Override
	public String getColumnName(int column) {
		return Column.at(column).name;
	}

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
		return Column.at(columnIndex).valueIn(sniperSnapshot);
	}
	
	public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
		this.sniperSnapshot = newSniperSnapshot;
		fireTableRowsUpdated(0, 0);
	}

}
