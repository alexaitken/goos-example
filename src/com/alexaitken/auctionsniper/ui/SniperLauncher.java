/**
 * 
 */
package com.alexaitken.auctionsniper.ui;

import com.alexaitken.auctionsniper.Auction;
import com.alexaitken.auctionsniper.AuctionHouse;
import com.alexaitken.auctionsniper.AuctionSniper;

public class SniperLauncher implements UserRequestListener {
	
	private final AuctionHouse auctionHouse;
	private final SniperCollector collector;
	
	public SniperLauncher(AuctionHouse auctionHouse, SniperCollector collector) {
		this.auctionHouse = auctionHouse;
		this.collector = collector;
	}

	public void joinAuction(String itemId) {
		Auction auction = auctionHouse.auctionFor(itemId);
		AuctionSniper sniper = new AuctionSniper(auction, itemId);
		auction.addAuctionEventListener(sniper);
		collector.addSniper(sniper);
		auction.join();
	}

	
}