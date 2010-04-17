package com.alexaitken.auctionsniper;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class SniperSnapshot {

	public final String itemId;
	public final int lastPrice;
	public final int lastBid;
	public final SniperState state;
	
	public SniperSnapshot(String itemId, int lastPrice, int lastBid) {
		this(itemId, lastPrice, lastBid, null);
	}

	public SniperSnapshot(String itemId, int lastPrice, int lastBid, SniperState state) {
		this.itemId = itemId;
		this.lastPrice = lastPrice;
		this.lastBid = lastBid;
		this.state = state;
		
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public static SniperSnapshot joining(String itemId) {
		return new SniperSnapshot(itemId, 0, 0, SniperState.JOINING);
	}

	public SniperSnapshot bidding(AuctionSniper auctionSniper, int price, int bid) {
		return new SniperSnapshot(auctionSniper.itemId, price, bid, SniperState.BIDDING);
	}

	public SniperSnapshot winning(AuctionSniper auctionSniper, int price) {
		return new SniperSnapshot(auctionSniper.itemId, price, price, SniperState.WINNING);
	}
	
	
	
}