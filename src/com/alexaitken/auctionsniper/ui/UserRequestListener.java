package com.alexaitken.auctionsniper.ui;

import java.util.EventListener;

import com.alexaitken.auctionsniper.Item;

public interface UserRequestListener extends EventListener {
	public void joinAuction(Item item);
}
