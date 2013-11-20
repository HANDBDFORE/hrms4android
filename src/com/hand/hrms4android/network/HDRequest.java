package com.hand.hrms4android.network;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.util.LogUtil;

public abstract class HDRequest<T> extends Request<T> {

	protected final Context mContext;

	public HDRequest(Context context, int method, String url, ErrorListener errorListener) {
		super(method, url, errorListener);
		mContext = context;
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		HrmsApplication.getApplication().updateCoockies(response.headers);
			return parseResponse(response);

	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = super.getHeaders();
		if (headers == null || headers.equals(Collections.emptyMap())) {
			headers = new HashMap<String, String>();
		}

		headers.put("Accept", "application/json;charset=UTF-8");

		HrmsApplication.getApplication().addSessionCookie(headers);

		LogUtil.info(this, "headers", headers.toString());
		return headers;
	}

	protected abstract Response<T> parseResponse(NetworkResponse response);
}
