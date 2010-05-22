package com.alexaitken.auctionsniper.integration.ui;

import static org.hamcrest.Matchers.*;

import org.junit.Test;

import com.alexaitken.auctionsniper.Item;
import com.alexaitken.auctionsniper.SniperPortfolio;
import com.alexaitken.auctionsniper.endtoend.api.drivers.AuctionSniperDriver;
import com.alexaitken.auctionsniper.ui.MainWindow;
import com.alexaitken.auctionsniper.ui.UserRequestListener;
import com.objogate.wl.swing.probe.ValueMatcherProbe;

public class MainWindowTest {

	
	@Test
	public void should_make_user_request_when_join_button_clicked() throws Exception {
		final ValueMatcherProbe<Item> itemProbe = new ValueMatcherProbe<Item>(equalTo(new Item("an-item-id", 789)), "join request");
		MainWindow sut = new MainWindow(new SniperPortfolio());
		
		sut.addUserRequestListener(new UserRequestListener() {
			
			@Override
			public void joinAuction(Item item) {
				itemProbe.setReceivedValue(item);
			}
		});
		
		AuctionSniperDriver driver = new AuctionSniperDriver(100);
		driver.startBiddingFor("an-item-id", 789);
		driver.check(itemProbe);
		
	}
	
}
