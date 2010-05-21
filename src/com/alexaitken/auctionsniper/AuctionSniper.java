package com.alexaitken.auctionsniper;




public class AuctionSniper implements AuctionEventListener {
	private final Announcer<SniperListener> listeners = Announcer.to(SniperListener.class);
	private final Auction auction;

	private SniperSnapshot snapshot;

	public AuctionSniper(Auction auction, String itemId) {
		this.auction = auction;
		this.snapshot = SniperSnapshot.joining(itemId);
		
	}


	public void auctionClosed() {
		snapshot = snapshot.closed();
		notifyChange();
	}


	private void notifyChange() {
		listeners.announce().sniperStateChanged(snapshot);
	}


	@Override
	public void currentPrice(int price, int increment, PriceSource priceSource) {
		switch(priceSource) {
		case FromSniper:
			snapshot = snapshot.winning(price);
			break;
		case FromOtherBidder:
			int bid = price + increment;
			auction.bid(bid);
			snapshot = snapshot.bidding(price, bid);
			break;
		}
		notifyChange();
		
	}


	public SniperSnapshot getSnapshot() {
		return snapshot;
	}


	public void addSniperListener(SniperListener sniperListener) {
		listeners.addListener(sniperListener);
	}


}
