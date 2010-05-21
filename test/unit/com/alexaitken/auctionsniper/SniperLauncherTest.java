package com.alexaitken.auctionsniper;

import static org.hamcrest.Matchers.*;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.alexaitken.auctionsniper.ui.SniperCollector;
import com.alexaitken.auctionsniper.ui.SniperLauncher;

@RunWith(JMock.class)
public class SniperLauncherTest {
	private final Mockery context = new Mockery();
	private final States auctionState = context.states("auction states").startsAs("not joined");
	private final AuctionHouse auctionHouse = context.mock(AuctionHouse.class);
	private final Auction auction = context.mock(Auction.class);
	private final SniperCollector collector = context.mock(SniperCollector.class);
	
	@Test
	public void should_add_new_sniper_to_collector_and_then_join_auction() throws Exception {
		final String itemId = "item 123";
		context.checking(new Expectations() {{
			allowing(auctionHouse).auctionFor(itemId); will(returnValue(auction));
		      
			oneOf(auction).addAuctionEventListener(with(sniperForItem(itemId))); when(auctionState.is("not joined"));
			oneOf(collector).addSniper(with(sniperForItem(itemId))); when(auctionState.is("not joined"));
	      
			one(auction).join(); then(auctionState.is("joined"));

		}});

				
		SniperLauncher sut = new SniperLauncher(auctionHouse, collector);
		sut.joinAuction(itemId);
	}
		
	protected Matcher<AuctionSniper>sniperForItem(String itemId) {
	    return new FeatureMatcher<AuctionSniper, String>(equalTo(itemId), "sniper with item id", "item") {
	      @Override protected String featureValueOf(AuctionSniper actual) {
	        return actual.getSnapshot().itemId;
	      }
	    };
	  }
}
