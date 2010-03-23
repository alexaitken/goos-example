package com.alexaitken.auctionsniper.endtoend;

import org.junit.After;
import org.junit.Test;

import com.alexaitken.auctionsniper.endtoend.api.ApplicationRunner;
import com.alexaitken.auctionsniper.endtoend.api.FakeAuctionServer;

public class AuctionSniperEndToEndTest {
	private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
	private final ApplicationRunner application= new ApplicationRunner();
	

	@Test public void 
	sniperJoinsAuctionUntilItCloses() throws Exception {
	
		auction.startSellingItem();
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFromSniper();
		auction.announceClosed();
		application.showSniperHasLostAuction();
		
	}
	
	@After public void stopAuction() {
		auction.stop();
	}
	
	@After public void stopApplication() {
		application.stop();
	}
}
