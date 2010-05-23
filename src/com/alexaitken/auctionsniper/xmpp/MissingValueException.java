package com.alexaitken.auctionsniper.xmpp;

public class MissingValueException extends RuntimeException {

	public MissingValueException() {
	}

	public MissingValueException(String arg0) {
		super(arg0);
	}

	public MissingValueException(Throwable arg0) {
		super(arg0);
	}

	public MissingValueException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
