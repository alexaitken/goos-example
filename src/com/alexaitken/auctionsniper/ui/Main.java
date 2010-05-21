package com.alexaitken.auctionsniper.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import com.alexaitken.auctionsniper.SniperPortfolio;
import com.alexaitken.auctionsniper.xmpp.XMPPAuctionHouse;

public class Main {

	
	public static void main(String... args) throws Exception {
		Main main = new Main();
		XMPPAuctionHouse auctionHouse = XMPPAuctionHouse.connect(args[0], args[1], args[2]);
		
		main.disconnectWhenUICloses(auctionHouse);
		
		main.addUserRquestListenerFor(auctionHouse);
		
	}

	private void addUserRquestListenerFor(final XMPPAuctionHouse auctionHouse) {
		ui.addUserRequestListener(new SniperLauncher(auctionHouse, portfolio));
	}


	
	private MainWindow ui;
	private final SniperPortfolio portfolio = new SniperPortfolio();
	

	public Main() throws Exception {
		startUserInterface();
	}

	


	private void disconnectWhenUICloses(final XMPPAuctionHouse auctionHouse) {
		ui.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				auctionHouse.disconnect();
			}
		});

	}

	

	private void startUserInterface() throws Exception {
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				ui = new MainWindow(portfolio);
			}
		});

	}
}
