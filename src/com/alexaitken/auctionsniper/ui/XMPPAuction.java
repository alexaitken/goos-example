/**
 * 
 */
package com.alexaitken.auctionsniper.ui;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;

import com.alexaitken.auctionsniper.Auction;

public class XMPPAuction implements Auction {
	private final Chat chat;

	XMPPAuction(Chat chat) {
		this.chat = chat;
	}

	public void bid(int amount) {
		sendMessage(String.format(Main.BID_COMMAND_FORMAT, amount));
	}

	public void join() {
		sendMessage(Main.JOIN_COMMAND_FORMAT);
	}
	
	private void sendMessage(String message) {
		try {
			chat.sendMessage(message);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}
}