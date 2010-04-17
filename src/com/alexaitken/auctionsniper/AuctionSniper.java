package com.alexaitken.auctionsniper;


public class AuctionSniper implements AuctionEventListener {

	private final SniperListener sniperListener;
	private final Auction auction;

	private boolean isWinning = false;
	String itemId;
	
	private SniperSnapshot snapshot;

	public AuctionSniper(Auction auction, SniperListener sniperListener, String itemId) {
		this.auction = auction;
		this.sniperListener = sniperListener;
		this.itemId = itemId;
		this.snapshot = SniperSnapshot.joining(itemId);
	}


	public void auctionClosed() {
		if (isWinning) {
			sniperListener.sniperWon();
		} else {
			sniperListener.sniperLost();
		}
	}

	@Override
	public void currentPrice(int price, int increment, PriceSource priceSource) {
		isWinning = priceSource == PriceSource.FromSniper;
		if (isWinning) {
			snapshot = snapshot.winning(this, price);
		} else {
			int bid = price + increment;
			auction.bid(bid);
			snapshot = snapshot.bidding(this, price, bid);
		}
		sniperListener.sniperStateChanged(snapshot);
		
	}

}
