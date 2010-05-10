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

import com.alexaitken.auctionsniper.Defect;
import com.alexaitken.auctionsniper.SniperSnapshot;
import com.alexaitken.auctionsniper.SniperState;
import com.alexaitken.auctionsniper.ui.Column;
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
		SniperSnapshot joining = SniperSnapshot.joining("item id");
		SniperSnapshot bidding = new SniperSnapshot("item id", 555, 666, SniperState.BIDDING);
		
		context.checking(new Expectations() {{
				ignoring(listener);
			}});
		
		model.addSniper(joining);
		
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
		SniperSnapshot joining = SniperSnapshot.joining("item id");
		SniperSnapshot bidding = new SniperSnapshot("item id", 555, 666, SniperState.BIDDING);
		context.checking(new Expectations() {{
				allowing(listener).tableChanged(with(anyInsertionEvent()));
				one(listener).tableChanged(with(aChangeInRow(0)));
			}});
		
		model.addSniper(joining );
		model.sniperStateChanged(bidding);
	}
	
	@Test
	public void should_notify_listeners_when_adding_a_sniper() {
		SniperSnapshot joining = SniperSnapshot.joining("item123");
		context.checking(new Expectations() {{
			one(listener).tableChanged(with(anInsertionAtRow(0)));
		}});
		
		assertEquals(0, model.getRowCount());
		
		model.addSniper(joining);
		
		assertEquals(1, model.getRowCount());
		assertRowMatchesSnapshot(0, joining);
		
	}
	
	
	@Test
	public void should_hold_snipers_in_addition_order() throws Exception {
		context.checking(new Expectations() {{
			ignoring(listener);
		}});
		
		model.addSniper(SniperSnapshot.joining("item 0"));
		model.addSniper(SniperSnapshot.joining("item 1"));
		
		assertEquals("item 0", cellValue(0, Column.ITEM_IDENTIFIER));
		assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
	}
	
	
	@Test
	public void should_update_correct_row_for_sniper() throws Exception {
	    
		context.checking(new Expectations() {{
			allowing(listener).tableChanged(with(anyInsertionEvent()));
			
			one(listener).tableChanged(with(aChangeInRow(1)));
		}});
		
		model.addSniper(SniperSnapshot.joining("item 0"));
		model.addSniper(SniperSnapshot.joining("item 1"));
		
		model.sniperStateChanged(new SniperSnapshot("item 1", 1, 2, SniperState.BIDDING));
	}
	
	@Test(expected=Defect.class)
	public void should_throw_defect_if_no_you_update_sniper_that_does_not_exist() throws Exception {
		context.checking(new Expectations() {{
			ignoring(listener);
		}});
		
		model.addSniper(SniperSnapshot.joining("item 0"));
		
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
