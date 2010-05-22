package com.alexaitken.auctionsniper;




public class AuctionSniper implements AuctionEventListener {
	private final Announcer<SniperListener> listeners = Announcer.to(SniperListener.class);
	private final Auction auction;

	private SniperSnapshot snapshot;
	private final Item item;

	public AuctionSniper(Auction auction, Item item) {
		this.auction = auction;
		this.item = item;
		this.snapshot = SniperSnapshot.joining(item.identifier);
		
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
			
			if (item.allowsBid(bid)) {
				auction.bid(bid);
				snapshot = snapshot.bidding(price, bid);
				
			} else {
				snapshot = snapshot.losing(price);
			}
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
