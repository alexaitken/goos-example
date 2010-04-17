package com.alexaitken.auctionsniper.endtoend.api;

import static com.alexaitken.auctionsniper.ui.MainWindow.*;

import com.alexaitken.auctionsniper.endtoend.api.drivers.AuctionSniperDriver;
import com.alexaitken.auctionsniper.ui.Main;
import com.alexaitken.auctionsniper.ui.MainWindow;

public class ApplicationRunner {
	protected static final String XMPP_HOST = "localhost";

	public static final String SNIPER_XMPP_ID = "sniper@sideshow-bob.local/Smack";

	private final String SNIPER_ID = "sniper";
	private final String SNIPPER_PASSWORD = "sniper";
	
	private AuctionSniperDriver driver;

	private String itemId;
	
	public void startBiddingIn(final FakeAuctionServer auction) {
		itemId = auction.getItemId();
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
		driver = new AuctionSniperDriver(5000);
		driver.showsSniperStatus("", 0, 0, STATUS_JOINING);
	}

	public void showsSniperHasLostAuction() {
		driver.showsSniperStatus(itemId, 0, 0, STATUS_LOST);
	}

	public void stop() {
		if (driver != null) {
			driver.dispose();
		}
	}

	public void hasShownSniperIsBidding(int lastPrice, int lastBid) {
		driver.showsSniperStatus(itemId, lastPrice, lastBid, STATUS_BIDDING); 		
	}

	public void showsSniperHasWonAuction(int lastPrice) {
		driver.showsSniperStatus(itemId, lastPrice, lastPrice, MainWindow.STATUS_WON);
		
	}

	public void hasShownSniperIsWinning(int winningBid) {
		driver.showsSniperStatus(itemId, winningBid, winningBid, MainWindow.STATUS_WINNING);
		
	}

}
