package com.alexaitken.test.auctionsniper;

import static com.alexaitken.auctionsniper.ui.SnipersTableModel.*;
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

import com.alexaitken.auctionsniper.AuctionSniper;
import com.alexaitken.auctionsniper.Defect;
import com.alexaitken.auctionsniper.Item;
import com.alexaitken.auctionsniper.SniperSnapshot;
import com.alexaitken.auctionsniper.SniperState;
import com.alexaitken.auctionsniper.ui.Column;
import com.alexaitken.auctionsniper.ui.SnipersTableModel;

@RunWith(JMock.class)
public class SnipersTableModelTest {
	private final Mockery context = new Mockery();
	private final TableModelListener listener = context.mock(TableModelListener.class);
	private final SnipersTableModel model = new SnipersTableModel();
	private final AuctionSniper sniper = new AuctionSniper(null, new Item("item id", Integer.MAX_VALUE));
	
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
		
		SniperSnapshot bidding = sniper.getSnapshot().bidding(555, 666);
		
		context.checking(new Expectations() {{
				ignoring(listener);
			}});
		
		
		model.sniperAdded(sniper);
		
		model.sniperStateChanged(bidding);
		
		assertRowMatchesSnapshot(0, bidding);
	}
	
	@Test
	public void should_set_up_the_columns_correctly() throws Exception {
		for (Column column: Column.values()) {
			assertEquals(column.name, model.getColumnName(column.ordinal()));
		}
	}
	
	@Test
	public void changing_sniper_status_should_fire_row_update_event() throws Exception {
		
		SniperSnapshot bidding = sniper.getSnapshot().bidding(555, 666);
		context.checking(new Expectations() {{
				allowing(listener).tableChanged(with(anyInsertionEvent()));
				one(listener).tableChanged(with(aChangeInRow(0)));
			}});
		
		model.sniperAdded(sniper);
		model.sniperStateChanged(bidding);
	}
	
	@Test
	public void should_notify_listeners_when_adding_a_sniper() {
		context.checking(new Expectations() {{
			one(listener).tableChanged(with(anInsertionAtRow(0)));
		}});
		
		assertEquals(0, model.getRowCount());
		
		model.sniperAdded(sniper);
		
		assertEquals(1, model.getRowCount());
		assertRowMatchesSnapshot(0, sniper.getSnapshot());
		
	}
	
	
	@Test
	public void should_hold_snipers_in_addition_order() throws Exception {
		context.checking(new Expectations() {{
			ignoring(listener);
		}});
		
		model.sniperAdded(sniper);
		model.sniperAdded(new AuctionSniper(null, new Item("item 2", Integer.MAX_VALUE)));
		
		assertEquals(sniper.getSnapshot().itemId, cellValue(0, Column.ITEM_IDENTIFIER));
		assertEquals("item 2", cellValue(1, Column.ITEM_IDENTIFIER));
	}
	
	
	@Test
	public void should_update_correct_row_for_sniper() throws Exception {
		AuctionSniper sniper2 = new AuctionSniper(null, new Item("item 2", Integer.MAX_VALUE));
		context.checking(new Expectations() {{
			allowing(listener).tableChanged(with(anyInsertionEvent()));
			
			one(listener).tableChanged(with(aChangeInRow(1)));
		}});
		
		
		model.sniperAdded(sniper);
		model.sniperAdded(sniper2);
		
		model.sniperStateChanged(sniper2.getSnapshot().bidding(1, 2));
	}
	
	@Test(expected=Defect.class)
	public void should_throw_defect_if_no_you_update_sniper_that_does_not_exist() throws Exception {
		context.checking(new Expectations() {{
			ignoring(listener);
		}});
		
		model.sniperAdded(sniper);
		
		model.sniperStateChanged(new SniperSnapshot("item 4", 1, 2, SniperState.BIDDING)); 
	}
	
	private void assertRowMatchesSnapshot(int row, SniperSnapshot snapshot) {
		assertColumnEquals(row, Column.ITEM_IDENTIFIER, snapshot.itemId);
		assertColumnEquals(row, Column.LAST_PRICE, snapshot.lastPrice);
		assertColumnEquals(row, Column.LAST_BID, snapshot.lastBid);
		assertColumnEquals(row, Column.SNIPER_STATE, textFor(snapshot.state));
		
		
	}

	private Matcher<TableModelEvent> anInsertionAtRow(int row) {
		return samePropertyValuesAs(new TableModelEvent(model, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}

	private Matcher<TableModelEvent> anyInsertionEvent() {
		return hasProperty("type", equalTo(TableModelEvent.INSERT));
	}
	

	private void assertColumnEquals(int rowIndex, Column column, Object expected) {
		assertEquals(expected, cellValue(rowIndex, column));
		
	}

	private Object cellValue(int rowIndex, Column column) {
		int columnIndex = column.ordinal();
		return model.getValueAt(rowIndex, columnIndex);
	}
	private Matcher<TableModelEvent> aChangeInRow(int row) {
		
		return samePropertyValuesAs(new TableModelEvent(model, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
	}
}
