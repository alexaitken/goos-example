package com.alexaitken.test.auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.alexaitken.auctionsniper.AuctionEventListener;
import com.alexaitken.auctionsniper.AuctionEventListener.PriceSource;
import com.alexaitken.auctionsniper.xmpp.AuctionMessageTranslator;


@RunWith(JMock.class)
public class AuctionMessageTranslatorTest {
	private final Mockery context = new Mockery();
	public static final Chat UNUSED_CHAT = null; 
	public static final String SNIPER_ID = "sniper";
	
	private final AuctionEventListener listener = context.mock(AuctionEventListener.class);
	private final AuctionMessageTranslator translator = new AuctionMessageTranslator(listener, SNIPER_ID);
	
	
	@Test
	public void notifies_auction_closed_when_close_message_received() throws Exception {
		context.checking(new Expectations() {{
				oneOf(listener).auctionClosed();
			}});
		
		Message message = new Message();
		message.setBody("SOLVersion: 1.1; Event: CLOSE;");
		
		translator.processMessage(UNUSED_CHAT, message);
	}
	
	
	@Test
	public void notifies_bid_details_when_current_price_message_received_from_other_bidder() throws Exception {
		context.checking(new Expectations() {{
			exactly(1).of(listener).currentPrice(192, 7, PriceSource.FromOtherBidder);
		}});
		
		Message message = new Message();
		message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;");
		
		translator.processMessage(UNUSED_CHAT, message);
	}
	
	@Test
	public void notifies_bid_details_when_current_price_message_received_from_the_sniper() throws Exception {
		context.checking(new Expectations() {{
			exactly(1).of(listener).currentPrice(192, 7, PriceSource.FromSniper);
		}});
		
		Message message = new Message();
		message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: " + SNIPER_ID + ";");
		
		translator.processMessage(UNUSED_CHAT, message);
	}
}
