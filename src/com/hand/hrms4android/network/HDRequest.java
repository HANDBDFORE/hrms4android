package com.hand.hrms4android.network;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.hand.hrms4android.activity.LoginActivity;
import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.core.HDRequestException;
import com.hand.hrms4android.util.LogUtil;

public abstract class HDRequest<T> extends Request<T> {

	private final Context mContext;

	public HDRequest(Context context, int method, String url, ErrorListener errorListener) {
		super(method, url, errorListener);
		mContext = context;
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		String x_hand_status = response.headers.get("X-hand-status");
		String serverMessage = response.headers.get("X-hand-message");
		LogUtil.info(this, "response header", "s: " + x_hand_status + ", m: " + serverMessage);

		// 正常处理
		if ("ok".equals(x_hand_status)) {
			HrmsApplication.getApplication().updateCoockies(response.headers);
			return parseResponse(response);
		}

		// 请求有错误

		if ("failure".equals(x_hand_status)) {
			return Response.error(new ParseError(new HDRequestException(serverMessage)));
		}

		// 需要登录
		if (x_hand_status.equals("login required")) {
			mContext.startActivity(new Intent(mContext, LoginActivity.class));
			return Response.error(new ParseError(new HDRequestException(serverMessage)));
		}

		return Response.error(new ParseError(new RuntimeException("无法解析的响应状态")));

	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = super.getHeaders();
		if (headers == null || headers.equals(Collections.emptyMap())) {
			headers = new HashMap<String, String>();
		}

		headers.put("Accept", "application/json");

		HrmsApplication.getApplication().addSessionCookie(headers);

		return headers;
	}

	protected abstract Response<T> parseResponse(NetworkResponse response);
}
