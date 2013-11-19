package com.hand.hrms4android.model;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.hand.hrms4android.core.HDAbstractModel;
import com.hand.hrms4android.core.ModelViewController;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.network.HDJsonObjectRequest;
import com.hand.hrms4android.network.HDRequest;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.hand.hrms4android.util.LogUtil;

public class AutoLoginModel extends HDAbstractModel {

	private ConfigReader configReader;

	public AutoLoginModel(int id, ModelViewController controller) {
		super(id, controller);
		configReader = XmlConfigReader.getInstance();
	}

	@Override
	public void load(LoadType loadType, Object param) {
		@SuppressWarnings("unchecked")
		Map<String, Object> params = new HashMap<String, Object>((Map<String, String>) param);
		NetworkUtil.removeAllCookies();
		String service = "";

		try {
			service = configReader
			        .getAttr(new Expression(
			                "/config/application/activity[@name='login_activity']/request/url[@name='auto_login_url']",
			                "value"));
		} catch (ParseExpressionException e) {
			handleError(new Exception("Cannot get url from config file! "));
			return;
		}

//		NetworkUtil.post(service, params, new UMJsonHttpResponseHandler() {
//			@Override
//			public void onSuccess(int statusCode, JSONObject response) {
//				controller.modelDidFinishedLoad(AutoLoginModel.this);
//			}
//
//			@Override
//			public void onFailure(Throwable error, String content) {
//				controller.modelFailedLoad(new Exception(error.getMessage()), AutoLoginModel.this);
//			}
//
//		});
		
		requestQueue.add(genRequest(this, param, service));
	}
	
	private Request<JSONObject> genRequest(Object tag, Object param, String url) {
		Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				onLoadingEnd();
				controller.modelDidFinishedLoad(AutoLoginModel.this);
			}
		};
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				onLoadingEnd();
				handleError(error);
			}
		};

		HDRequest<JSONObject> request = new HDJsonObjectRequest(controller.getContext(), Method.POST,
		        NetworkUtil.getAbsoluteUrl(url), param, listener, errorListener);

		request.setTag(tag);

		return request;
	}

	@Override
	public <T> T getProcessData() {
		throw new UnsupportedOperationException("不支持");
	}

}
