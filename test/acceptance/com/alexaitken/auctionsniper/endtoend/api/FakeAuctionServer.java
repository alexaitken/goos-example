package com.alexaitken.auctionsniper.endtoend.api;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import com.alexaitken.auctionsniper.ui.Main;

public class FakeAuctionServer {
	

	private static final String XMPP_HOSTNAME = "localhost";
	private static final String AUCTION_RESOURCE = "Auction";
	private static final String AUCTION_PASSWORD = "auction";
	private final String itemId;
	private final SingleMessageListener messageListener = new SingleMessageListener();
	private XMPPConnection connection;
	protected Chat currentChat;

	public FakeAuctionServer(String itemId) {
		this.itemId = itemId;
		this.connection = new XMPPConnection(XMPP_HOSTNAME);
	}

	public void startSellingItem() throws XMPPException {
		connection.connect();
		connection.login(String.format(Main.ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, AUCTION_RESOURCE);
		connection.getChatManager().addChatListener(
				new ChatManagerListener() {
					@Override
					public void chatCreated(Chat chat, boolean createdLocally) {
						currentChat = chat;
						chat.addMessageListener(messageListener);
					}
				});
	}
	
	
	public void reportPrice(int price, int increment, String bidder) throws XMPPException {
		currentChat.sendMessage(
				String.format("SOLVersion: 1.1; Event: PRICE; "
						+ "CurrentPrice: %d; Increment: %d; Bidder: %s;",
						price, increment, bidder));
		
	}

	public void hasReceivedJoinRequestFromSniper(String sniperId) throws InterruptedException {
		hasReceivedAMessageMatching(sniperId, equalTo(Main.JOIN_COMMAND_FORMAT));
	}

	public void announceClosed() throws XMPPException {
		currentChat.sendMessage("SOLVersion: 1.1; Event: CLOSE;");
	}
	
	public void hasReceivedBid(int bid, String sniperId) throws InterruptedException {
		hasReceivedAMessageMatching(sniperId, equalTo(String.format(Main.BID_COMMAND_FORMAT, bid)));
	}
	
	private void hasReceivedAMessageMatching(String sniperId, Matcher<? super String> messageMather) throws InterruptedException {
		messageListener.receivesAMessage(messageMather);
		String participant = currentChat.getParticipant();
		assertThat(participant, equalTo(sniperId));

	}
	

	public void stop() {
		connection.disconnect();
		
	}

	public String getItemId() {
		return itemId;
	}

	
	public class SingleMessageListener implements MessageListener {

		private final ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<Message>(1);

		@Override
		public void processMessage(Chat chat, Message message) {
			messages.add(message);
		}
		
		
		public void receivesAMessage(Matcher<? super String> messageMatcher) throws InterruptedException {
			final Message message = messages.poll(10, TimeUnit.SECONDS);
			assertThat("Message", message, is(notNullValue()));
			assertThat(message.getBody(), messageMatcher);
		}


		
	}


	
}
