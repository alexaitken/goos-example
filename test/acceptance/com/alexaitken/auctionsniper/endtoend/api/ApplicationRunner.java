package com.alexaitken.auctionsniper.endtoend.api;

import static com.alexaitken.auctionsniper.ui.SnipersTableModel.*;

import com.alexaitken.auctionsniper.SniperState;
import com.alexaitken.auctionsniper.endtoend.api.drivers.AuctionSniperDriver;
import com.alexaitken.auctionsniper.ui.Main;
import com.alexaitken.auctionsniper.ui.MainWindow;

public class ApplicationRunner {
	protected static final String XMPP_HOST = "localhost";

	public static final String SNIPER_XMPP_ID = "sniper@sideshow-bob.local/Smack";

	private final String SNIPER_ID = "sniper";
	private final String SNIPPER_PASSWORD = "sniper";
	
	private AuctionSniperDriver driver;

	
	public void startBiddingIn(final FakeAuctionServer ... auctions) {
		startSniper();
		for (FakeAuctionServer auction : auctions) {
			openingBidFor(auction, Integer.MAX_VALUE);
		}
		
	}
	public void startBiddingWithStopPrice(FakeAuctionServer auction, int stopPrice) {
		startSniper(auction);
		openingBidFor(auction, stopPrice);
		
	}

	private void openingBidFor(FakeAuctionServer auction, int stopPrice) {
		final String itemId = auction.getItemId();
		driver.startBiddingFor(itemId, stopPrice);
		driver.showsSniperStatus(auction.getItemId(), 0, 0, textFor(SniperState.JOINING));
	}

	private void startSniper(final FakeAuctionServer... auctions) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					Main.main(aruguments(auctions));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			
		};
		
		thread.setDaemon(true);
		thread.start();
		driver = new AuctionSniperDriver(5000);
		driver.hasTitle(MainWindow.APPLICATION_TITLE);
		driver.hasColumnTitles();
	}
	
	protected String[] aruguments(FakeAuctionServer[] auctions) {
		String[] arguments = new String[auctions.length + 3];
		arguments[0] = XMPP_HOST;
		arguments[1] = SNIPER_ID;
		arguments[2] = SNIPPER_PASSWORD;
		for (int i = 0; i < auctions.length; i++) {
			arguments[i+3] = auctions[i].getItemId();
		}
		return arguments;
	}

	public void showsSniperHasLostAuction(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.LOST));
	}

	public void stop() {
		if (driver != null) {
			driver.dispose();
		}
	}

	public void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.BIDDING)); 		
	}

	public void showsSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice, textFor(SniperState.WON));
		
	}

	public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
		driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, textFor(SniperState.WINNING));
		
	}

	

	public void hasShownSniperIsLosing(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.LOSING));
	}

}
