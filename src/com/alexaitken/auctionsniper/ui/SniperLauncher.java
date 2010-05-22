/**
 * 
 */
package com.alexaitken.auctionsniper.ui;

import com.alexaitken.auctionsniper.Auction;
import com.alexaitken.auctionsniper.AuctionHouse;
import com.alexaitken.auctionsniper.AuctionSniper;
import com.alexaitken.auctionsniper.Item;

public class SniperLauncher implements UserRequestListener {
	
	private final AuctionHouse auctionHouse;
	private final SniperCollector collector;
	
	public SniperLauncher(AuctionHouse auctionHouse, SniperCollector collector) {
		this.auctionHouse = auctionHouse;
		this.collector = collector;
	}

	public void joinAuction(Item item) {
		Auction auction = auctionHouse.auctionFor(item);
		AuctionSniper sniper = new AuctionSniper(auction, item);
		auction.addAuctionEventListener(sniper);
		collector.addSniper(sniper);
		auction.join();
	}

	
}