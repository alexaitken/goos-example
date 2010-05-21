package com.alexaitken.auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
	public void sniperStateChanged(SniperSnapshot sniperSnapshot);
}
