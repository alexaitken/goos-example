package com.alexaitken.auctionsniper;

public class Defect extends RuntimeException {

	public Defect() {
		super();
	}

	public Defect(String reason) {
		super(reason);
	}
}
