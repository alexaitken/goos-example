package com.alexaitken.auctionsniper;

import static com.alexaitken.auctionsniper.SniperState.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;


public class SniperStateTest {

	
	@Test
	public void should_have_lost_state_if_auction_closes_when_bidding() throws Exception {
		assertThat(BIDDING.whenAuctionClosed(), equalTo(LOST));
	}
	
	@Test
	public void should_have_lost_state_if_auction_closes_when_joining() throws Exception {
		assertThat(JOINING.whenAuctionClosed(), equalTo(LOST));
	}
	
	@Test
	public void should_have_won_state_if_auction_closes_when_winning() throws Exception {
		assertThat(WINNING.whenAuctionClosed(), equalTo(WON));
	}
	
	@Test(expected=Defect.class)
	public void should_there_is_programming_error_if_auction_closes_when_won() throws Exception {
		WON.whenAuctionClosed();
	}
	
	@Test(expected=Defect.class)
	public void should_there_is_programming_error_if_auction_closes_when_lost() throws Exception {
		LOST.whenAuctionClosed();
	}
}
