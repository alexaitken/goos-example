package com.alexaitken.auctionsniper.endtoend.api;

import static com.alexaitken.auctionsniper.ui.MainWindow.*;

import com.alexaitken.auctionsniper.endtoend.api.drivers.AuctionSniperDriver;
import com.alexaitken.auctionsniper.ui.Main;

public class ApplicationRunner {
	protected static final String XMPP_HOST = "localhost";
	
	private final String SNIPER_ID = "sniper";
	private final String SNIPPER_PASSWORD = "sniper";
	
	private AuctionSniperDriver driver;
	
	public void startBiddingIn(final FakeAuctionServer auction) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					Main.main(XMPP_HOST, SNIPER_ID, SNIPPER_PASSWORD, auction.getItemId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		thread.setDaemon(true);
		thread.start();
		driver = new AuctionSniperDriver(1000);
		driver.showsSniperStatus(STATUS_JOINING);
	}

	public void showSniperHasLostAuction() {
		driver.showsSniperStatus(STATUS_LOST);
	}

	public void stop() {
		if (driver != null) {
			driver.dispose();
		}
	}

}
