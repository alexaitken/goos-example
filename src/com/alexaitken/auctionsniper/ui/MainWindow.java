package com.alexaitken.auctionsniper.ui;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

public class MainWindow extends JFrame {
	public static final String STATUS_JOINING = "JOIN";
	public static final String STATUS_LOST = "LOST";
	
	
	private final JLabel sniperStatus = createLabel(STATUS_JOINING);
	public static final String SNIPER_STATUS_NAME = "sniperStatus";
	public static final String MAIN_WINDOW_NAME = "mainWindow";
	
	
	public MainWindow() {
		super("Auction Sniper");

		setName(MainWindow.MAIN_WINDOW_NAME);
		add(sniperStatus);
		
		
		pack();
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}


	private JLabel createLabel(String initialText) {
		JLabel result = new JLabel(initialText);
		result.setName(MainWindow.SNIPER_STATUS_NAME);
		result.setBorder(new LineBorder(Color.BLACK));
		return result;
	}


	public void showStatus(String newStatus) {
		sniperStatus.setText(newStatus);

	}


}
