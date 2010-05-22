package com.alexaitken.auctionsniper.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.alexaitken.auctionsniper.Auction;
import com.alexaitken.auctionsniper.AuctionHouse;
import com.alexaitken.auctionsniper.Item;

public class XMPPAuctionHouse implements AuctionHouse {
	public static final String AUCTION_RESOURCE = "Auction";
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";
	public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
	
	
	public static XMPPAuctionHouse connect(String hostName, String signIn, String password) throws XMPPException {
		XMPPConnection xmppConnection = new XMPPConnection(hostName);
		xmppConnection.connect();
		xmppConnection.login(signIn, password);
		return new XMPPAuctionHouse(xmppConnection);
	}
	
		
	private XMPPConnection connection;
	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Event: JOIN;";
	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Event: BID; Price: %d;";
	
	
	public XMPPAuctionHouse(XMPPConnection connection) {
		this.connection = connection;
		
	}
	
	@Override
	public Auction auctionFor(Item item) {
		return new XMPPAuction(connection, autionId(connection, item.identifier));
		
	}



	public void disconnect() {
		connection.disconnect();
	}

	public static String autionId(XMPPConnection connection, String itemNumber) {
		return String.format(XMPPAuctionHouse.AUCTION_ID_FORMAT, itemNumber, connection.getServiceName());
	}

}
