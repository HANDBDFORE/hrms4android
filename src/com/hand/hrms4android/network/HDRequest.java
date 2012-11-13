package com.hand.hrms4android.network;

public interface HDRequest<T extends HDResponse> {

	public String getApiMethodName();

	public Class<T> getResponseClass();

	public void check() throws NetworkRuleException;

	public String getBody();
}
