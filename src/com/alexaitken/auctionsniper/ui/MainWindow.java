package com.alexaitken.auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.alexaitken.auctionsniper.Announcer;
import com.alexaitken.auctionsniper.Item;
import com.alexaitken.auctionsniper.SniperPortfolio;

public class MainWindow extends JFrame {
	public static final String SNIPER_STATUS_NAME = "sniperStatus";
	public static final String MAIN_WINDOW_NAME = "mainWindow";
	public static final String NEW_ITEM_ID_NAME = "newItemId";
	public static final String JOIN_BUTTON_NAME = "joinButton";
	public static final String NEW_ITEM_STOP_PRICE_NAME = "newItemStopPrice";
	
	public static final String APPLICATION_TITLE = "Auction Sniper";
	private static final String SNIPERS_TABLE = "snipersTable";
	
	
	
	
	private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);
	
	
	public MainWindow(SniperPortfolio portfolio) {
		super(APPLICATION_TITLE);
		setName(MainWindow.MAIN_WINDOW_NAME);
		
		SnipersTableModel model = new SnipersTableModel();
		portfolio.addPortfolioListener(model);
		
		fillContentPane(makeSnipersTable(model), makeControls());
		
		pack();
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	
	private JPanel makeControls() {
		JPanel controls = new JPanel(new FlowLayout());
		
		JLabel itemIdLabel = new JLabel();
		itemIdLabel.setText("Item:");
		controls.add(itemIdLabel);
		
		final JTextField itemIdField = new JTextField();
		itemIdField.setColumns(10);
		itemIdField.setName(NEW_ITEM_ID_NAME);
		controls.add(itemIdField);
		
		JLabel stopPriceLabel = new JLabel();
		stopPriceLabel.setText("Stop Price:");
		controls.add(stopPriceLabel);
		
		final JFormattedTextField itemStopPriceField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		itemStopPriceField.setColumns(7);
		itemStopPriceField.setName(NEW_ITEM_STOP_PRICE_NAME);
		
		controls.add(itemStopPriceField);
		
		JButton joinAuctionButton = new JButton("Join Auction");
		joinAuctionButton.setName(JOIN_BUTTON_NAME);
		joinAuctionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userRequests.announce().joinAuction(new Item(itemId(), stopPrice()));
			}
			private String itemId() {
				return itemIdField.getText();
			}
			private int stopPrice() { 
				return ((Number)itemStopPriceField.getValue()).intValue(); 
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
