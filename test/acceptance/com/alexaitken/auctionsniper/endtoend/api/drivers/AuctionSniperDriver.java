package com.alexaitken.auctionsniper.endtoend.api.drivers;

import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.*;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.*;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;

import com.alexaitken.auctionsniper.ui.MainWindow;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JButtonDriver;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.driver.JTableHeaderDriver;
import com.objogate.wl.swing.driver.JTextFieldDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

public class AuctionSniperDriver extends JFrameDriver {

	@SuppressWarnings("unchecked")
	public AuctionSniperDriver(int timeOutMillis) {
		super(new GesturePerformer(),
				JFrameDriver.topLevelFrame(named(MainWindow.MAIN_WINDOW_NAME), showingOnScreen()),
				new AWTEventQueueProber(timeOutMillis, 100));
		
	}

	@SuppressWarnings("unchecked")
	public void showsSniperStatus(String itemId, int lastPrice, int lastBid, String sniperState) {
		new JTableDriver(this).hasRow(
				matching(withLabelText(itemId), withLabelText(String.valueOf(lastPrice)),
						withLabelText(String.valueOf(lastBid)), withLabelText(sniperState)));
		
	}

	@SuppressWarnings("unchecked")
	public void hasColumnTitles() {
		JTableHeaderDriver headers = new JTableHeaderDriver(this, JTableHeader.class);
		headers.hasHeaders(matching(withLabelText("Item"), withLabelText("Last Price"), 
				withLabelText("Last Bid"), withLabelText("State")));
		
	}

	
	
	public void startBiddingFor(String itemId, int stopPrice) {
		textField(MainWindow.NEW_ITEM_ID_NAME).replaceAllText(itemId);
		textField(MainWindow.NEW_ITEM_STOP_PRICE_NAME).replaceAllText(String.valueOf(stopPrice));
		bidButton().click();
	}

	@SuppressWarnings("unchecked")
	private JButtonDriver bidButton() {
		return new JButtonDriver(this, JButton.class, named(MainWindow.JOIN_BUTTON_NAME));
	}
	
	@SuppressWarnings("unchecked")
	private JTextFieldDriver textField(String fieldName) {
		JTextFieldDriver newItemId = new JTextFieldDriver(this, JTextField.class, named(fieldName));
		newItemId.focusWithMouse();
		return newItemId;
	}

	

}
