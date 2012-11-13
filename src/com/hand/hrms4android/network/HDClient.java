package com.hand.hrms4android.network;

public interface HDClient {
	public <T extends HDResponse> T execute(HDRequest<T> request)
			throws NetworkException;
}
