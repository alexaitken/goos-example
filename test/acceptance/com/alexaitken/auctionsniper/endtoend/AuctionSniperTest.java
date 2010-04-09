package com.alexaitken.auctionsniper.endtoend;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.alexaitken.auctionsniper.Auction;
import com.alexaitken.auctionsniper.AuctionSniper;
import com.alexaitken.auctionsniper.SniperListener;


@RunWith(JMock.class)
public class AuctionSniperTest {
	private final Mockery context = new Mockery();
	private final SniperListener sniperListener = context.mock(SniperListener.class);
	private final Auction auction = context.mock(Auction.class);
	
	
	private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener);
	
	@Test
	public void reports_lost_when_auction_closes() throws Exception {
		context.checking(new Expectations() {{
			atLeast(1).of(sniperListener).sniperLost();
		}});
		
		sniper.auctionClosed();
	}
	
	
	@Test
	public void bids_higer_and_reports_bidding_when_new_price_arrives() throws Exception {
		final int price = 1001;
		final int increment =  25;
		context.checking(new Expectations() {{
			one(auction).bid(price + increment);
			atLeast(1).of(sniperListener).sniperBidding();
		}});
		
		sniper.currentPrice(price, increment);
	}
	
}
