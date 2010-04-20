package com.alexaitken.auctionsniper.ui;

import static com.alexaitken.auctionsniper.ui.SnipersTableModel.*;

import com.alexaitken.auctionsniper.SniperSnapshot;

public enum Column {
	ITEM_IDENTIFIER("Item") {
		public Object valueIn(SniperSnapshot snapshot) {
			return snapshot.itemId;
		}

	},
	LAST_PRICE("Last Price") {
		public Object valueIn(SniperSnapshot snapshot) {
			return snapshot.lastPrice;
		}
	},
	LAST_BID("Last Bid") {
		public Object valueIn(SniperSnapshot snapshot) {
			return snapshot.lastBid;
		}
	},
	SNIPER_STATE("State") {
		public Object valueIn(SniperSnapshot snapshot) {
			return textFor(snapshot.state);
		}
	};

	public final String name;
	
	
	
	private Column(String name) {
		this.name = name;
	}

	public abstract Object valueIn(SniperSnapshot snapshot);

	public static Column at(int offset) {
		return values()[offset];
	}
}
