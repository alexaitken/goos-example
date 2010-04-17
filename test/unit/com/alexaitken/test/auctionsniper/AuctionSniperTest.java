package com.alexaitken.test.auctionsniper;

import static org.hamcrest.Matchers.*;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.alexaitken.auctionsniper.Auction;
import com.alexaitken.auctionsniper.AuctionSniper;
import com.alexaitken.auctionsniper.SniperListener;
import com.alexaitken.auctionsniper.SniperSnapshot;
import com.alexaitken.auctionsniper.SniperState;
import com.alexaitken.auctionsniper.AuctionEventListener.PriceSource;


@RunWith(JMock.class)
public class AuctionSniperTest {
	protected static final String ITEM_ID = "item-12345";
	private final Mockery context = new Mockery();
	private final SniperListener sniperListener = context.mock(SniperListener.class);
	private final Auction auction = context.mock(Auction.class);
	
	
	private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener, ITEM_ID);
	
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
			allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING))); then(sniperState.is("bidding"));
			
			atLeast(1).of(sniperListener).sniperLost(); when(sniperState.is("bidding"));
		}});
		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.auctionClosed();
	}
	
	private Matcher<SniperSnapshot> aSniperThatIs(final SniperState state) {
		return new FeatureMatcher<SniperSnapshot, SniperState>(equalTo(state), "sniper that is ", "was") {
				@Override
				protected SniperState featureValueOf(SniperSnapshot actual) {
					return actual.state;
				}
			
			
		};
	}

	@Test
	public void reports_won_if_auction_closes_when_winning() throws Exception {
		context.checking(new Expectations() {{
			ignoring(auction);
			allowing(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 123, 123, SniperState.WINNING)); then(sniperState.is("winning"));
			
			atLeast(1).of(sniperListener).sniperWon();	when(sniperState.is("winning"));
		}});
		sniper.currentPrice(123, 45, PriceSource.FromSniper);
		sniper.auctionClosed();
	}
	
	
	
	@Test
	public void bids_higer_and_reports_bidding_when_new_price_arrives() throws Exception {
		final int price = 1001;
		final int increment =  25;
		final int bid = price + increment;
		context.checking(new Expectations() {{
			one(auction).bid(bid);
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING));
		}});
		
		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
	}

	
	@Test
	public void reports_winning_when_current_price_comes_from_sniper() throws Exception {
		context.checking(new Expectations() {{
			ignoring(auction);
			allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING))); then(sniperState.is("bidding"));
											
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 135, 135, SniperState.WINNING)); when(sniperState.is("bidding"));
		}});
		
		sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
		sniper.currentPrice(135, 45, PriceSource.FromSniper);
	}
	
	
	
	@Test
	public void reports_bidding_after_a_new_bid_causes_the_sniper_to_no_longer_be_winning() throws Exception {
		context.checking(new Expectations() {{
			ignoring(auction);
			atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING))); when(sniperState.isNot("winning"));
			allowing(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 145, 145, SniperState.WINNING)); then(sniperState.is("winning"));
			
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 167, 256, SniperState.BIDDING)); when(sniperState.is("winning"));
			
		}});
		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.currentPrice(145, 67, PriceSource.FromSniper);
		sniper.currentPrice(167, 89, PriceSource.FromOtherBidder);
		
	}
	
}
