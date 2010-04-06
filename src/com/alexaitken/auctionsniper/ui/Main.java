package com.alexaitken.auctionsniper.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.alexaitken.auctionsniper.AuctionEventListener;
import com.alexaitken.auctionsniper.AuctionMessageTranslator;

public class Main implements AuctionEventListener {
	
	public static final String AUCTION_RESOURCE = "Auction";
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";
	public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Event: BID; Price: %d;";
	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Event: JOIN;";

	
	
	public static void main(String ...args) throws Exception {
		Main main = new Main();
		main.joinAuction(connectTo(args[0], args[1], args[2]),args[3]);
	}

	private static XMPPConnection connectTo(String hostName, String signIn, String password) throws XMPPException {
		XMPPConnection xmppConnection = new XMPPConnection(hostName);
		xmppConnection.connect();
		xmppConnection.login(signIn, password);
		return xmppConnection;
	}
	

	
	private MainWindow ui;
	
	@SuppressWarnings("unused")
	private Chat notToBeGcd;
	
	
	
	public Main() throws Exception {
		startUserInterface();
	}
	


	private void joinAuction(XMPPConnection connection, String itemNumber) throws XMPPException {
		disconnectWhenUICloses(connection);
		final Chat chat = connection.getChatManager().createChat(
				autionId(connection, itemNumber), 
				new AuctionMessageTranslator(this));
		this.notToBeGcd = chat;
		chat.sendMessage(JOIN_COMMAND_FORMAT);
	}



	private void disconnectWhenUICloses(final XMPPConnection connection) {
		ui.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				connection.disconnect();
			}
		});
		
	}

	private static String autionId(XMPPConnection connection, String itemNumber) {
		return String.format(AUCTION_ID_FORMAT, itemNumber, connection.getServiceName());
	}



	
	private void startUserInterface() throws Exception {
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				ui = new MainWindow();
			}
		});
		
	}

	@Override
	public void auctionClosed() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ui.showStatus(MainWindow.STATUS_LOST);
			}
		});
		
	}

	@Override
	public void currentPrice(int price, int increment) {
		// TODO Auto-generated method stub
		
	}

}
