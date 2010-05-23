package com.alexaitken.auctionsniper.endtoend;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alexaitken.auctionsniper.endtoend.api.ApplicationRunner;
import com.alexaitken.auctionsniper.endtoend.api.FakeAuctionServer;

public class AuctionSniperEndToEndTest {
	private FakeAuctionServer auction;
	private FakeAuctionServer auction2;
	
	private ApplicationRunner application;
	
	
	@Before
	public void createAuction() {
		 auction = new FakeAuctionServer("item-54321");
		 auction2 = new FakeAuctionServer("item-65432");
	}
	
	@Before
	public void createApplication() {
		application = new ApplicationRunner();
	}
	
	@After 
	public void stopAuction() {
		auction.stop();
		auction2.stop();
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
		application.showsSniperHasLostAuction(auction, 0, 0);
		
	}
	
	
	@Test
	public void sniper_makes_a_higher_bid_but_loses() throws Exception {
		auction.startSellingItem();
		
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(auction, 1000, 1098);
		
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.announceClosed();
		application.showsSniperHasLostAuction(auction, 1000, 1098);
	}
	
	
	@Test
	public void sniper_wins_an_auction_by_making_a_higher_bid() throws Exception {
		auction.startSellingItem();
		
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(auction, 1000, 1098);
		
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
		application.hasShownSniperIsWinning(auction, 1098);
		
		auction.announceClosed();
		application.showsSniperHasWonAuction(auction, 1098);
	}
	
	
	@Test
	public void sniper_wins_an_auction_by_making_serveral_higher_bids() throws Exception {
		auction.startSellingItem();
		
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(auction, 1000, 1098);
		
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
		application.hasShownSniperIsWinning(auction, 1098);
		
		auction.reportPrice(1195, 97, "other bidder");
		application.hasShownSniperIsBidding(auction, 1195, 1292);
		
		
		auction.reportPrice(1292, 97, ApplicationRunner.SNIPER_XMPP_ID);
		application.hasShownSniperIsWinning(auction, 1292);
		
		
		auction.announceClosed();
		application.showsSniperHasWonAuction(auction, 1292);
	}
	
	
	
	@Test
	public void sniper_loses_an_auction_by_being_out_bid_at_the_end_of_the_auction() throws Exception {
		auction.startSellingItem();
		
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(auction, 1000, 1098);
		
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
		application.hasShownSniperIsWinning(auction, 1098);
		
		auction.reportPrice(1195, 97, "other bidder");
		application.hasShownSniperIsBidding(auction, 1195, 1292);
		
		auction.announceClosed();
		application.showsSniperHasLostAuction(auction, 1195, 1292);
	}
	@Test
	public void sniper_bids_on_multiple_items() throws Exception {
		auction.startSellingItem();
		auction2.startSellingItem();
		
		application.startBiddingIn(auction, auction2);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auction2.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(auction, 1000, 1098);
		
		auction2.reportPrice(500, 51, "other bidder");
		application.hasShownSniperIsBidding(auction2, 500, 551);
		
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		auction2.hasReceivedBid(551, ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
		auction2.reportPrice(551, 22, ApplicationRunner.SNIPER_XMPP_ID);
		
		application.hasShownSniperIsWinning(auction, 1098);
		application.hasShownSniperIsWinning(auction2, 551);
		
		auction.announceClosed();
		application.showsSniperHasWonAuction(auction, 1098);
		
		auction2.announceClosed();
		application.showsSniperHasWonAuction(auction2, 551);
	}
	
	
	
	@Test
	public void sniper_loses_an_auction_when_the_price_is_too_high() throws Exception {
		auction.startSellingItem();
		application.startBiddingWithStopPrice(auction, 1100);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(auction, 1000, 1098);
		
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(1197, 10, "third party");
		application.hasShownSniperIsLosing(auction, 1197, 1098);
		
		
		auction.reportPrice(1207, 10, "fourth party");
		application.hasShownSniperIsLosing(auction, 1207, 1098);
		
		auction.announceClosed();
		application.showsSniperHasLostAuction(auction, 1207, 1098);
		
	}
	
	
	@Test
	public void sniper_reports_invalid_auction_message_and_stops_responding_to_events() throws Exception {
		String brokenMessage =  "a broken message";
		auction.startSellingItem();
		auction2.startSellingItem();
		
		application.startBiddingIn(auction, auction2);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(500, 20, "other bidder");
		auction.hasReceivedBid(520, ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.sendInvalidMessageContaining(brokenMessage);
		application.showsSniperHasFailed(auction);
		
		auction.reportPrice(520, 21, "other bidder");
		waitForAnotherAuctionEvent();
		
		application.reportsInvalidMessage(auction, brokenMessage);
		application.showsSniperHasFailed(auction);
		
	}

	private void waitForAnotherAuctionEvent() throws Exception {
		auction2.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auction2.reportPrice(100, 30, "other bidder");
		application.hasShownSniperIsBidding(auction2, 100, 130);
	}
	
}
