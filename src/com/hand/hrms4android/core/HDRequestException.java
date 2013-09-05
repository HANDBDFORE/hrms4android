package com.hand.hrms4android.core;

public class HDRequestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4252794807133418345L;

	public HDRequestException() {
		super();
	}

	public HDRequestException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public HDRequestException(String detailMessage) {
		super(detailMessage);
	}

	public HDRequestException(Throwable throwable) {
		super(throwable);
	}

}
