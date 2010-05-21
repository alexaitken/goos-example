/**
 * 
 */
package com.alexaitken.auctionsniper.ui;

import javax.swing.SwingUtilities;

import com.alexaitken.auctionsniper.SniperListener;
import com.alexaitken.auctionsniper.SniperSnapshot;

public class SwingThreadSniperListener implements SniperListener {
	private final SniperListener delagate;

	public SwingThreadSniperListener(SniperListener delagate) {
		this.delagate = delagate;
	}

	@Override
	public void sniperStateChanged(final SniperSnapshot sniperSnapshot) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				delagate.sniperStateChanged(sniperSnapshot);
			}
		});

	}

}