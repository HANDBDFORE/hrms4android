package com.hand.hrms4android.network;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.hand.hrms4android.app.LoginActivity;
import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.core.HDRequestException;
import com.hand.hrms4android.util.LogUtil;

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
			LogUtil.info(this, "",jsonString);
			JSONObject responseJson = new JSONObject(jsonString);
			JSONObject head = responseJson.getJSONObject("head");
			
			String status = head.getString("code"); 
			
			// 正常处理
			if ("ok".equals(status)) {
				HrmsApplication.getApplication().updateCoockies(response.headers);
				return Response.success(responseJson, HttpHeaderParser.parseCacheHeaders(response));
			}

			// 请求有错误

			if ("failure".equals(status)) {
				return Response.error(new ParseError(new HDRequestException(head.getString("message"))));
			}

			// 需要登录
			if ("login required".equals(status)) {
				mContext.startActivity(new Intent(mContext, LoginActivity.class));
				return Response.error(new ParseError(new HDRequestException(head.getString("message"))));
			}

			return Response.error(new ParseError(new RuntimeException("无法解析的响应状态")));
			
			
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}


}
