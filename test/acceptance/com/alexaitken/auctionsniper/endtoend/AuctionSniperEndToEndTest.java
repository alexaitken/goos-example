package com.alexaitken.auctionsniper.endtoend;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alexaitken.auctionsniper.endtoend.api.ApplicationRunner;
import com.alexaitken.auctionsniper.endtoend.api.FakeAuctionServer;

public class AuctionSniperEndToEndTest {
	private FakeAuctionServer auction;
	private ApplicationRunner application;
	
	
	@Before
	public void createAuction() {
		 auction = new FakeAuctionServer("item-54321");
	}
	
	@Before
	public void createApplication() {
		application = new ApplicationRunner();
	}
	
	@After 
	public void stopAuction() {
		auction.stop();
	}
	
	@After 
	public void stopApplication() {
		application.stop();
	}

	@Test 
	public void sniperJoinsAuctionUntilItCloses() throws Exception {
	
		auction.startSellingItem();
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auction.announceClosed();
		application.showSniperHasLostAuction();
		
	}
	
	
	@Test
	public void sniper_makes_a_higher_bid_but_loses() throws Exception {
		auction.startSellingItem();
		
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding();
		
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.announceClosed();
		application.showSniperHasLostAuction();
		
	}
	
}
