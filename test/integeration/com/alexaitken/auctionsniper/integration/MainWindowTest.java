package com.alexaitken.auctionsniper.integration;

import org.hamcrest.Matchers;
import org.junit.Test;

import com.alexaitken.auctionsniper.endtoend.api.drivers.AuctionSniperDriver;
import com.alexaitken.auctionsniper.ui.MainWindow;
import com.alexaitken.auctionsniper.ui.SnipersTableModel;
import com.alexaitken.auctionsniper.ui.UserRequestListener;
import com.objogate.wl.swing.probe.ValueMatcherProbe;

public class MainWindowTest {

	
	@Test
	public void should_make_user_request_when_join_button_clicked() throws Exception {
		final ValueMatcherProbe<String> buttonProbe = new ValueMatcherProbe<String>(Matchers.equalTo("an-item-id"), "join request");
		MainWindow sut = new MainWindow(new SnipersTableModel());
		
		sut.addUserRequestListener(new UserRequestListener() {
			
			@Override
			public void joinAuction(String itemId) {
				buttonProbe.setReceivedValue(itemId);
			}
		});
		
		AuctionSniperDriver driver = new AuctionSniperDriver(100);
		driver.startBiddingFor("an-item-id");
		driver.check(buttonProbe);
		
	}
	
}
