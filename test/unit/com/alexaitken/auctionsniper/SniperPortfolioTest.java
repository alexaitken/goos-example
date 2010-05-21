package com.alexaitken.auctionsniper;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class SniperPortfolioTest {
	private final Mockery context = new Mockery();
	private final SniperPortfolioListener portfolioListener = context.mock(SniperPortfolioListener.class);
	
	
	@Test
	public void should_notify_portfolio_listener_when_a_sniper_is_added() throws Exception {
	    final AuctionSniper sniper = new AuctionSniper(null, "item 123");
		context.checking(new Expectations() {{ 
			oneOf(portfolioListener).sniperAdded(sniper);
		}});
	    SniperPortfolio sut = new SniperPortfolio();
	    sut.addPortfolioListener(portfolioListener);
		sut.addSniper(sniper);
	}
	
}
