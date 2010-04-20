package com.alexaitken.auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class MainWindow extends JFrame {
	public static final String SNIPER_STATUS_NAME = "sniperStatus";
	public static final String MAIN_WINDOW_NAME = "mainWindow";
	public static final String APPLICATION_TITLE = "Auction Sniper";
	
	private static final String SNIPERS_TABLE = "snipersTable";
	
	
	private final SnipersTableModel snipers;
	
	public MainWindow(SnipersTableModel snipers) {
		super(APPLICATION_TITLE);
		this.snipers = snipers;

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

}
