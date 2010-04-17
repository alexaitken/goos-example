package com.alexaitken.test.auctionsniper;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.alexaitken.auctionsniper.SniperSnapshot;
import com.alexaitken.auctionsniper.SniperState;
import com.alexaitken.auctionsniper.ui.Column;
import com.alexaitken.auctionsniper.ui.MainWindow;
import com.alexaitken.auctionsniper.ui.SnipersTableModel;

@RunWith(JMock.class)
public class SnipersTableModelTest {
	private final Mockery context = new Mockery();
	private final TableModelListener listener = context.mock(TableModelListener.class);
	private final SnipersTableModel model = new SnipersTableModel();
	
	@Before
	public void attachListener() {
		model.addTableModelListener(listener);
	}
	
	@Test
	public void has_enough_columns() {
		assertThat(model.getColumnCount(), equalTo(Column.values().length));
	}
	
	@Test
	public void changing_sniper_status_should_update_values_in_columns() throws Exception {
		context.checking(new Expectations() {{
				ignoring(listener);
			}});
		model.sniperStatusChanged(new SniperSnapshot("item id", 555, 666, SniperState.BIDDING));
		
		assertColumnEquals(Column.ITEM_IDENTIFIER, "item id");
		assertColumnEquals(Column.LAST_PRICE, 555);
		assertColumnEquals(Column.LAST_BID, 666);
		assertColumnEquals(Column.SNIPER_STATE, MainWindow.STATUS_BIDDING);
	}
	
	@Test
	public void changing_sniper_status_should_fire_row_update_event() throws Exception {
		context.checking(new Expectations() {{
				one(listener).tableChanged(with(aRowChangedEvent()));
			}});
		
		model.sniperStatusChanged(new SniperSnapshot("item id", 555, 666, SniperState.BIDDING));
	}

	private void assertColumnEquals(Column column, Object expected) {
		final int rowIndex = 0;
		final int columnIndex = column.ordinal();
		
		assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
		
	}
	private Matcher<TableModelEvent> aRowChangedEvent() {
		
		return samePropertyValuesAs(new TableModelEvent(model, 0));
	}
}
