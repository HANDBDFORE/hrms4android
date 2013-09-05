package com.hand.hrms4android.network;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public class HDStringRequest extends HDRequest<String> {

	private final Listener<String> mListener;
	private final Map<String, String> params;

	public HDStringRequest(Context context, int method, String url, RequestParameterSource source,
	        Listener<String> listener, ErrorListener errorListener) {
		super(context, method, url, errorListener);
		mListener = listener;
		params = new HashMap<String, String>(source.getParams());

	}

	public HDStringRequest(Context context, int method, String url, Listener<String> listener,
	        ErrorListener errorListener) {
		super(context, method, url, errorListener);
		mListener = listener;
		params = new HashMap<String, String>();
	}

	@Override
	protected Response<String> parseResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return params;
	}

	@Override
	protected void deliverResponse(String response) {
		mListener.onResponse(response);
	}

}
