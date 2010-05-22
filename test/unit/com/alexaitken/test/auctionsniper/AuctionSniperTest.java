package com.alexaitken.test.auctionsniper;

import static org.hamcrest.Matchers.*;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.alexaitken.auctionsniper.Auction;
import com.alexaitken.auctionsniper.AuctionSniper;
import com.alexaitken.auctionsniper.Item;
import com.alexaitken.auctionsniper.SniperListener;
import com.alexaitken.auctionsniper.SniperSnapshot;
import com.alexaitken.auctionsniper.SniperState;
import com.alexaitken.auctionsniper.AuctionEventListener.PriceSource;


@RunWith(JMock.class)
public class AuctionSniperTest {
	protected static final Item ITEM = new Item("item-12345", 2345);
	private final Mockery context = new Mockery();
	private final SniperListener sniperListener = context.mock(SniperListener.class);
	private final Auction auction = context.mock(Auction.class);
	
	
	private final AuctionSniper sniper = new AuctionSniper(auction, ITEM);
	
	private final States sniperState = context.states("sniper");
	
	@Before
	public void addListener() {
		sniper.addSniperListener(sniperListener);
	}
	
	@Test
	public void reports_lost_if_auction_closes_immediately() throws Exception {
		context.checking(new Expectations() {{
			atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.LOST)));
		}});
		
		sniper.auctionClosed();
	}
	
	@Test
	public void reports_lost_if_auction_closes_when_bidding() throws Exception {
		context.checking(new Expectations() {{
			ignoring(auction);
			allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING))); then(sniperState.is("bidding"));
			
			atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.LOST))); when(sniperState.is("bidding"));
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
			allowing(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM.identifier, 123, 0, SniperState.WINNING)); then(sniperState.is("winning"));
			
			atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.WON)));	when(sniperState.is("winning"));
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
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM.identifier, price, bid, SniperState.BIDDING));
		}});
		
		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
	}

	
	@Test
	public void reports_winning_when_current_price_comes_from_sniper() throws Exception {
		context.checking(new Expectations() {{
			ignoring(auction);
			allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING))); then(sniperState.is("bidding"));
											
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM.identifier, 135, 135, SniperState.WINNING)); when(sniperState.is("bidding"));
		}});
		
		sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
		sniper.currentPrice(135, 45, PriceSource.FromSniper);
	}
	
	
	
	@Test
	public void reports_bidding_after_a_new_bid_causes_the_sniper_to_no_longer_be_winning() throws Exception {
		context.checking(new Expectations() {{
			ignoring(auction);
			atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING))); when(sniperState.isNot("winning"));
			allowing(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM.identifier, 145, 168, SniperState.WINNING)); then(sniperState.is("winning"));
			
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM.identifier, 167, 256, SniperState.BIDDING)); when(sniperState.is("winning"));
			
		}});
		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.currentPrice(145, 67, PriceSource.FromSniper);
		sniper.currentPrice(167, 89, PriceSource.FromOtherBidder);
		
		
	}
	
	
	@Test
	public void should_not_bid_and_report_losing_when_subseqent_price_is_above_stop_price() throws Exception {
		allowingSniperBidding();
		context.checking(new Expectations() {{
			int bid = 123 + 45;
			allowing(auction).bid(bid);
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM.identifier, 2345, bid, SniperState.LOSING));
																							when(sniperState.is("bidding"));
		}}); 
		
		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.currentPrice(2345, 45, PriceSource.FromOtherBidder);
	}
	
	
	@Test
	public void should_not_bid_and_report_losing_if_the_first_price_is_above_stop_price() throws Exception {
		sniperState.startsAs("joining");
		context.checking(new Expectations() {{
			
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM.identifier, 2345, 0, SniperState.LOSING));
																							when(sniperState.is("joining"));
		}}); 
		
		sniper.currentPrice(2345, 45, PriceSource.FromOtherBidder);
	}
	
	
	@Test
	public void should_report_lost_if_auction_closes_when_losing() throws Exception {
		allowingSniperLosing();
		context.checking(new Expectations() {{
			atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.LOST)));
																							when(sniperState.is("losing"));
		}}); 
		sniper.currentPrice(2345, 45, PriceSource.FromOtherBidder);
		sniper.auctionClosed();
	}
	
	@Test
	public void should_continue_to_be_losing_once_stop_price_is_reached() throws Exception {
		final Sequence states = context.sequence("sniper states");
	    final int price1 = 3233;
	    final int price2 = 3258;

	    context.checking(new Expectations() {{
	      atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM.identifier, price1, 0, SniperState.LOSING)); inSequence(states);
	      atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM.identifier, price2, 0, SniperState.LOSING)); inSequence(states);
	    }});
	   
	    sniper.currentPrice(price1, 25, PriceSource.FromOtherBidder);
	    sniper.currentPrice(price2, 25, PriceSource.FromOtherBidder); 
	}
	
	@Test
	public void should_not_bid_and_report_losing_if_price_after_wining_is_above_stop_price() throws Exception {
		allowingSniperBidding();
		allowingSniperWinning();

		context.checking(new Expectations() {{
			int bid = 50 + 50;
			allowing(auction).bid(bid);
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM.identifier, 3345, 100, SniperState.LOSING)); when(sniperState.is("winning"));
		}});
		
		sniper.currentPrice(50, 50, PriceSource.FromOtherBidder);
		sniper.currentPrice(100, 3245, PriceSource.FromSniper);

		sniper.currentPrice(3345, 1000, PriceSource.FromOtherBidder);
		
	}

	
	
	private void allowingSniperWinning() {
		allowSniperStateChange(SniperState.WINNING, "winning");
	}

	private void allowingSniperBidding() {
		allowSniperStateChange(SniperState.BIDDING, "bidding");
	}
	
	private void allowingSniperLosing() {
		allowSniperStateChange(SniperState.LOSING, "losing");
	}

	private void allowSniperStateChange(final SniperState state, final String newSniperState) {
		context.checking(new Expectations() {{
			allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(state))); 
			then(sniperState.is(newSniperState));
		}});
	}
}
