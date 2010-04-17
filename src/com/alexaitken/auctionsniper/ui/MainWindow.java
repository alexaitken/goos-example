package com.alexaitken.auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.alexaitken.auctionsniper.SniperSnapshot;

public class MainWindow extends JFrame {
	public static final String STATUS_JOINING = "JOIN";
	public static final String STATUS_LOST = "LOST";
	public static final String STATUS_BIDDING = "BIDDING";
	public static final String STATUS_WINNING = "WINNING";
	public static final String STATUS_WON = "WON";
	
	
	public static final String SNIPER_STATUS_NAME = "sniperStatus";
	public static final String MAIN_WINDOW_NAME = "mainWindow";
	private static final String SNIPERS_TABLE = "snipersTable";
	
	private final SnipersTableModel snipers = new SnipersTableModel();
	
	
	public MainWindow() {
		super("Auction Sniper");

		setName(MainWindow.MAIN_WINDOW_NAME);
		
		fillContentPane(makeSnipersTable());
		
		pack();
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	
	private void fillContentPane(JTable snipersTable) {
		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
		
	}


	private JTable makeSnipersTable() {
		JTable snipersTable = new JTable(snipers);
		snipersTable.setName(SNIPERS_TABLE);
		return snipersTable;
	}

	


	public void showStatus(String newStatus) {
		snipers.setStatusText(newStatus);

	}


	public void sniperStatusChanged(SniperSnapshot sniperSnapshot) {
		snipers.sniperStatusChanged(sniperSnapshot);
	}


}
