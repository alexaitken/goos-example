package com.alexaitken.auctionsniper.integration.xmpp;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alexaitken.auctionsniper.Auction;
import com.alexaitken.auctionsniper.AuctionEventListener;
import com.alexaitken.auctionsniper.endtoend.api.ApplicationRunner;
import com.alexaitken.auctionsniper.endtoend.api.FakeAuctionServer;
import com.alexaitken.auctionsniper.xmpp.XMPPAuctionHouse;


public class XMPPAuctionHouseTest {

	
	FakeAuctionServer server;
	
	@Before
	public void setUpServer() throws Exception {
		server = new FakeAuctionServer("item-54321");
		server.startSellingItem();
	}
	
	
	
	@Test
	public void receives_events_from_auction_server_after_joining() throws Exception {
		CountDownLatch auctionWasClosed = new CountDownLatch(1);
		
		XMPPAuctionHouse auctionHouse = XMPPAuctionHouse.connect("localhost", "sniper", "sniper");
		Auction auction = auctionHouse.auctionFor(server.getItemId());
		auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));
		
		auction.join();
		
		server.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		server.announceClosed();
		
		Assert.assertTrue("should have been closed", auctionWasClosed.await(2, TimeUnit.SECONDS));
	}

	private AuctionEventListener auctionClosedListener(final CountDownLatch auctionWasClosed) {
		return new AuctionEventListener() {
			public void currentPrice(int price, int increment, PriceSource priceSource) {}
			
			public void auctionClosed() {
				auctionWasClosed.countDown();
			}
		};
	}
}
