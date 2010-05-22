package com.alexaitken.auctionsniper.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.alexaitken.auctionsniper.AuctionSniper;
import com.alexaitken.auctionsniper.Defect;
import com.alexaitken.auctionsniper.SniperListener;
import com.alexaitken.auctionsniper.SniperPortfolioListener;
import com.alexaitken.auctionsniper.SniperSnapshot;
import com.alexaitken.auctionsniper.SniperState;

public class SnipersTableModel extends AbstractTableModel implements SniperListener, SniperPortfolioListener {
	private static final String[] STATUS_TEXT = { "JOIN", "BIDDING", "WINNING", "LOSING", "LOST", "WON" };
	

	
	private final List<SniperSnapshot> sniperSnapshots = new ArrayList<SniperSnapshot>();
	
	
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
		return sniperSnapshots.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return Column.at(columnIndex).valueIn(sniperSnapshots.get(rowIndex));
	}
	
	public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
		int row = rowMatching(newSniperSnapshot);
		
		sniperSnapshots.set(row, newSniperSnapshot);
		fireTableRowsUpdated(row, row);
	}

	private int rowMatching(SniperSnapshot snapshot) {
		for (int i = 0; i < sniperSnapshots.size(); i++) {
			if (snapshot.isForSameItemAs(sniperSnapshots.get(i))) {
				return i;
			}
		}
		throw new Defect("Cannot find match for " + snapshot);
	}

	private void addSniper(SniperSnapshot newSniper) {
		sniperSnapshots.add(newSniper);
		int row = rowMatching(newSniper);
		fireTableRowsInserted(row, row);
	}
	
	public void sniperAdded(AuctionSniper sniper) {
		addSniper(sniper.getSnapshot());
		sniper.addSniperListener(new SwingThreadSniperListener(this));
	}

		

	public static String textFor(SniperState state) {
		return STATUS_TEXT[state.ordinal()];
	}


}
