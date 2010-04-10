package com.alexaitken.test.auctionsniper;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.alexaitken.auctionsniper.Auction;
import com.alexaitken.auctionsniper.AuctionSniper;
import com.alexaitken.auctionsniper.SniperListener;
import com.alexaitken.auctionsniper.AuctionEventListener.PriceSource;


@RunWith(JMock.class)
public class AuctionSniperTest {
	private final Mockery context = new Mockery();
	private final SniperListener sniperListener = context.mock(SniperListener.class);
	private final Auction auction = context.mock(Auction.class);
	
	
	private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener);
	
	private final States sniperState = context.states("sniper");
	
	@Test
	public void reports_lost_if_auction_closes_immediately() throws Exception {
		context.checking(new Expectations() {{
			atLeast(1).of(sniperListener).sniperLost();
		}});
		
		sniper.auctionClosed();
	}
	
	@Test
	public void reports_lost_if_auction_closes_when_bidding() throws Exception {
		context.checking(new Expectations() {{
			ignoring(auction);
			allowing(sniperListener).sniperBidding(); then(sniperState.is("bidding"));
			
			atLeast(1).of(sniperListener).sniperLost(); when(sniperState.is("bidding"));
		}});
		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.auctionClosed();
	}
	
	@Test
	public void reports_won_if_auction_closes_when_winning() throws Exception {
		context.checking(new Expectations() {{
			ignoring(auction);
			allowing(sniperListener).sniperWinning(); then(sniperState.is("winning"));
			
			atLeast(1).of(sniperListener).sniperWon();	when(sniperState.is("winning"));
		}});
		sniper.currentPrice(123, 45, PriceSource.FromSniper);
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
		
		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
	}

	
	@Test
	public void reports_winning_when_current_price_comes_from_sniper() throws Exception {
		context.checking(new Expectations() {{
			atLeast(1).of(sniperListener).sniperWinning();
		}});
		
		sniper.currentPrice(1, 1, PriceSource.FromSniper);
	}
	
	
	
	@Test
	public void reports_bidding_after_a_new_bid_causes_the_sniper_to_no_longer_be_winning() throws Exception {
		context.checking(new Expectations() {{
			ignoring(auction);
			atLeast(1).of(sniperListener).sniperBidding(); when(sniperState.isNot("winning"));
			allowing(sniperListener).sniperWinning(); then(sniperState.is("winning"));
			
			atLeast(1).of(sniperListener).sniperBidding(); when(sniperState.is("winning"));
			
		}});
		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.currentPrice(145, 67, PriceSource.FromSniper);
		sniper.currentPrice(167, 89, PriceSource.FromOtherBidder);
		
	}
	
}
