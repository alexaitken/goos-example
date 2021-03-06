/**
 * 
 */
package com.alexaitken.auctionsniper.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.alexaitken.auctionsniper.Announcer;
import com.alexaitken.auctionsniper.Auction;
import com.alexaitken.auctionsniper.AuctionEventListener;

public class XMPPAuction implements Auction {
	private final Chat chat;
	private final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);

	public XMPPAuction(XMPPConnection connection, String auctionId) {
		AuctionMessageTranslator translator = translatorFor(connection);
		chat = connection.getChatManager().createChat(auctionId, translator);
		addAuctionEventListener(chatDisconnectorFor(translator));
	}


	private AuctionEventListener chatDisconnectorFor(final AuctionMessageTranslator translator) {
		return new AuctionEventListener() {
			
			public void currentPrice(int price, int increment, PriceSource priceSource) {}
			
			public void auctionFailed() {
				chat.removeMessageListener(translator);
			}
			
			public void auctionClosed() {}
		};
	}


	private AuctionMessageTranslator translatorFor(XMPPConnection connection) {
		return new AuctionMessageTranslator(auctionEventListeners.announce(), connection.getUser());
	}


	public void bid(int amount) {
		sendMessage(String.format(XMPPAuctionHouse.BID_COMMAND_FORMAT, amount));
	}

	public void join() {
		sendMessage(XMPPAuctionHouse.JOIN_COMMAND_FORMAT);
	}
	
	private void sendMessage(String message) {
		try {
			chat.sendMessage(message);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}



	@Override
	public void addAuctionEventListener(AuctionEventListener listener) {
		auctionEventListeners.addListener(listener);
		
	}
}