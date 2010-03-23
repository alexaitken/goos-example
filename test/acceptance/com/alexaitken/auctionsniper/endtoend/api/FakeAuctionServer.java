package com.alexaitken.auctionsniper.endtoend.api;

import static org.hamcrest.Matchers.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.junit.Assert;

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

	public void hasReceivedJoinRequestFromSniper() throws InterruptedException {
		messageListener.receivesAMessage();
	}

	public void announceClosed() throws XMPPException {
		currentChat.sendMessage(new Message());
		
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
		
		
		public void receivesAMessage() throws InterruptedException {
			Assert.assertThat("Message", messages.poll(20, TimeUnit.SECONDS), is(notNullValue()));
		}
	}
}
