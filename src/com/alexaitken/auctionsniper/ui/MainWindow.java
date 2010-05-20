package com.alexaitken.auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.alexaitken.auctionsniper.Announcer;

public class MainWindow extends JFrame {
	public static final String SNIPER_STATUS_NAME = "sniperStatus";
	public static final String MAIN_WINDOW_NAME = "mainWindow";
	public static final String NEW_ITEM_ID_NAME = "newItemId";
	public static final String JOIN_BUTTON_NAME = "joinButton";
	
	
	public static final String APPLICATION_TITLE = "Auction Sniper";
	private static final String SNIPERS_TABLE = "snipersTable";
	
	
	
	private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);
	
	public MainWindow(SnipersTableModel snipers) {
		super(APPLICATION_TITLE);

		setName(MainWindow.MAIN_WINDOW_NAME);
		
		fillContentPane(makeSnipersTable(snipers), makeControls());
		
		pack();
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	
	private JPanel makeControls() {
		JPanel controls = new JPanel(new FlowLayout());
		
		final JTextField itemIdField = new JTextField();
		itemIdField.setColumns(25);
		itemIdField.setName(NEW_ITEM_ID_NAME);
		controls.add(itemIdField);
		
		JButton joinAuctionButton = new JButton("Join Auction");
		joinAuctionButton.setName(JOIN_BUTTON_NAME);
		joinAuctionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userRequests.announce().joinAuction(itemIdField.getText());
			}
		});
		
		controls.add(joinAuctionButton);
		
		return controls;
	}


	private void fillContentPane(JTable snipersTable, JPanel joinControls) {
		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(joinControls, BorderLayout.PAGE_START);
		
		contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
		
		
		
	}


	private JTable makeSnipersTable(final SnipersTableModel snipers) {
		JTable snipersTable = new JTable(snipers);
		snipersTable.setName(SNIPERS_TABLE);
		return snipersTable;
	}


	public void addUserRequestListener(UserRequestListener userRequestListener) {
		userRequests.addListener(userRequestListener);
	}

}
