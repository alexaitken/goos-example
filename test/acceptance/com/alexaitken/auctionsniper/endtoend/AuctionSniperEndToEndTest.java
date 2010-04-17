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
		application.showsSniperHasLostAuction();
		
	}
	
	
	@Test
	public void sniper_makes_a_higher_bid_but_loses() throws Exception {
		auction.startSellingItem();
		
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(1000, 1098);
		
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.announceClosed();
		application.showsSniperHasLostAuction();
	}
	
	
	@Test
	public void sniper_wins_an_auction_by_making_a_higher_bid() throws Exception {
		auction.startSellingItem();
		
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(1000, 1098);
		
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
		application.hasShownSniperIsWinning(1098);
		
		auction.announceClosed();
		application.showsSniperHasWonAuction(1098);
	}
	
	
	@Test
	public void sniper_wins_an_auction_by_making_serveral_higher_bids() throws Exception {
		auction.startSellingItem();
		
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(1000, 1098);
		
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
		application.hasShownSniperIsWinning(1195);
		
		auction.reportPrice(1195, 97, "other bidder");
		application.hasShownSniperIsBidding(1195, 1292);
		
		
		auction.reportPrice(1292, 97, ApplicationRunner.SNIPER_XMPP_ID);
		application.hasShownSniperIsWinning(1292);
		
		
		auction.announceClosed();
		application.showsSniperHasWonAuction(1292);
	}
	
	
	
	@Test
	public void sniper_loses_an_auction_by_being_out_bid_at_the_end_of_the_auction() throws Exception {
		auction.startSellingItem();
		
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(1000, 1098);
		
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
		application.hasShownSniperIsWinning(1098);
		
		auction.reportPrice(1195, 97, "other bidder");
		application.hasShownSniperIsBidding(1195, 1292);
		
		auction.announceClosed();
		application.showsSniperHasLostAuction();
	}
	
	
}
