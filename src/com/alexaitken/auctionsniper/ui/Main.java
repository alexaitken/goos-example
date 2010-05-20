package com.alexaitken.auctionsniper.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.alexaitken.auctionsniper.Auction;
import com.alexaitken.auctionsniper.AuctionMessageTranslator;
import com.alexaitken.auctionsniper.AuctionSniper;
import com.alexaitken.auctionsniper.SniperListener;
import com.alexaitken.auctionsniper.SniperSnapshot;
import com.alexaitken.auctionsniper.xmpp.XMPPAuction;

public class Main {

	public static final String AUCTION_RESOURCE = "Auction";
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";
	public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Event: BID; Price: %d;";
	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Event: JOIN;";

	public static void main(String... args) throws Exception {
		Main main = new Main();
		XMPPConnection connection = connectTo(args[0], args[1], args[2]);
		main.disconnectWhenUICloses(connection);
		
		main.addUserRquestListenerFor(connection);
		
	}

	private void addUserRquestListenerFor(final XMPPConnection connection) {
		ui.addUserRequestListener(new UserRequestListener() {
			@Override
			public void joinAuction(String itemId) {
				snipers.addSniper(SniperSnapshot.joining(itemId));
				
				Chat chat = connection.getChatManager().createChat(autionId(connection, itemId), null);
				notToBeGcd.add(chat);

				Auction auction = new XMPPAuction(chat);

				chat.addMessageListener(new AuctionMessageTranslator(
						new AuctionSniper(auction, new SwingThreadSniperListener(snipers), itemId), connection.getUser()));
				
				
				auction.join();
			}
		});
	}

	private static XMPPConnection connectTo(String hostName, String signIn, String password) throws XMPPException {
		XMPPConnection xmppConnection = new XMPPConnection(hostName);
		xmppConnection.connect();
		xmppConnection.login(signIn, password);
		return xmppConnection;
	}

	private final SnipersTableModel snipers = new SnipersTableModel();
	private MainWindow ui;

	private Set<Chat> notToBeGcd = new HashSet<Chat>();

	public Main() throws Exception {
		startUserInterface();
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
				ui = new MainWindow(snipers);
			}
		});

	}

	public class SwingThreadSniperListener implements SniperListener {
		private final SniperListener delagate;

		private SwingThreadSniperListener(SniperListener delagate) {
			this.delagate = delagate;
		}

		@Override
		public void sniperStateChanged(final SniperSnapshot sniperSnapshot) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					delagate.sniperStateChanged(sniperSnapshot);
				}
			});

		}

	}
}
