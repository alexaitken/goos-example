package com.alexaitken.auctionsniper;

public interface Auction {

	public void bid(int bid);

	public void join();
	
	public void addAuctionEventListener(AuctionEventListener listener);

}
