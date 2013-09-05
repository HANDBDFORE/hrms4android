package com.hand.hrms4android.network;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

public class HDJsonObjectRequest extends HDJsonRequest<JSONObject> {

	public HDJsonObjectRequest(Context context, int method, String url, JSONObject jsonRequest,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super(context, method, url, jsonRequest.toString(), listener, errorListener);
	}

	public HDJsonObjectRequest(Context context, int method, String url, Object param, Listener<JSONObject> listener,
			ErrorListener errorListener) {
		super(context, method, url, new Gson().toJson(param), listener, errorListener);
		System.out.println(new Gson().toJson(param));
	}

	@Override
	protected Response<JSONObject> parseResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			JSONObject responseJson = new JSONObject(jsonString);
			return Response.success(responseJson, HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}

}
