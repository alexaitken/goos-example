package com.alexaitken.auctionsniper;

import java.util.HashSet;
import java.util.Set;

import com.alexaitken.auctionsniper.ui.SniperCollector;

public class SniperPortfolio implements SniperCollector {
	private final Announcer<SniperPortfolioListener> announcer = Announcer.to(SniperPortfolioListener.class);
	private final Set<AuctionSniper> snipers = new HashSet<AuctionSniper>();

	public void addSniper(AuctionSniper sniper) {
		snipers.add(sniper);
		announcer.announce().sniperAdded(sniper);
	}

	public void addPortfolioListener(SniperPortfolioListener portfolioListener) {
		announcer.addListener(portfolioListener);
		
	}

}
