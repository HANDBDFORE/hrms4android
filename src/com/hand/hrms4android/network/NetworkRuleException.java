package com.hand.hrms4android.network;

public class NetworkRuleException extends NetworkException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7787145910600194272L;
	public NetworkRuleException(String errCode, String errMsg) {
		super(errCode , errMsg);
	}
}
