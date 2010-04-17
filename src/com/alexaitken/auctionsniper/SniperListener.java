package com.alexaitken.auctionsniper;

public interface SniperListener {
	public void sniperLost();


	public void sniperWon();

	public void sniperStateChanged(SniperSnapshot sniperSnapshot);
}
