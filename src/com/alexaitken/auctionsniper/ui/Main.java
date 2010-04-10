package com.alexaitken.auctionsniper.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.alexaitken.auctionsniper.Auction;
import com.alexaitken.auctionsniper.AuctionMessageTranslator;
import com.alexaitken.auctionsniper.AuctionSniper;
import com.alexaitken.auctionsniper.SniperListener;

public class Main {
	
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
		final Chat chat = connection.getChatManager().createChat(autionId(connection, itemNumber), null);
		this.notToBeGcd = chat;
		
		Auction auction =  new XMPPAuction(chat);
		
		chat.addMessageListener(new AuctionMessageTranslator(new AuctionSniper(auction, new SinperStateDisplayer()), connection.getUser()));
		auction.join();
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

	


	
	public class SinperStateDisplayer implements SniperListener {

		public void sniperLost() {
			showStatus(MainWindow.STATUS_LOST);
			
		}

		
		@Override
		public void sniperBidding() {
			showStatus(MainWindow.STATUS_BIDDING);
		}
		
		private void showStatus(final String status) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					ui.showStatus(status);
				}
			});
		}


		@Override
		public void sniperWinning() {
			showStatus(MainWindow.STATUS_WINNING);
		}


		@Override
		public void sniperWon() {
			showStatus(MainWindow.STATUS_WON);
			
		}


	}
}


