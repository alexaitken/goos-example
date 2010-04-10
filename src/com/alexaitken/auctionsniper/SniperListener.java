package com.alexaitken.auctionsniper;

public interface SniperListener {
	public void sniperLost();

	public void sniperBidding();

	public void sniperWinning();

	public void sniperWon();
}
