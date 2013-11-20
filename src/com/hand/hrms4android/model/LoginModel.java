package com.hand.hrms4android.model;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.core.HDAbstractModel;
import com.hand.hrms4android.core.Model;
import com.hand.hrms4android.core.ModelViewController;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.network.HDJsonObjectRequest;
import com.hand.hrms4android.network.HDOtherRequest;
import com.hand.hrms4android.network.HDRequest;
import com.hand.hrms4android.network.KVParameterSource;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.hand.hrms4android.util.Constrants;
import com.hand.hrms4android.util.LogUtil;

public class LoginModel extends HDAbstractModel {
	private ConfigReader configReader;

	public LoginModel(int id) {
		this(id, null);
	}

	public LoginModel(int id, ModelViewController activity) {
		super(id, activity);
		configReader = XmlConfigReader.getInstance();
	}

	@Override
	public void load(Model.LoadType loadType, Object param) {
		super.load(loadType, param);

		String service = "";
		try {
			service = configReader.getAttr(new Expression(
			        "/config/application/activity[@name='login_activity']/request/url[@name='login_submit_url']",
			        "value"));
		} catch (ParseExpressionException e) {
			e.printStackTrace();
			controller.modelFailedLoad(new Exception("Cannot get url from config file! "), this);
			return;
		}

		NetworkUtil.removeAllCookies();

		requestQueue.add(genRequest(this, (Map<String, String>)param, service));

		// NetworkUtil.post(service, (RequestParams) param, new
		// UMJsonHttpResponseHandler() {
		//
		// @Override
		// public void onSuccess(int statusCode, JSONObject response) {
		// // 存储相关数据
		// try {
		// LoginModel.this.storeSomething(response.getJSONObject("body"));
		// } catch (JSONException e) {
		// e.printStackTrace();
		// activity.modelFailedLoad(new Exception("error data"),
		// LoginModel.this);
		// }
		//
		// // 通知Activity已完成加载
		// LoginModel.this.activity.modelDidFinishedLoad(LoginModel.this);
		// }
		//
		// @Override
		// public void onFailure(Throwable error, String content) {
		// LogUtil.error(this, "request", "onFailure:" + content);
		// activity.modelFailedLoad(new Exception(error), LoginModel.this);
		// }
		// });
	}

	private Request<JSONObject> genRequest(Object tag, Map<String, String> param, String url) {
		Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				onLoadingEnd();
				try {
					// 存储相关数据
					storeSomething(response);
					// 通知Activity已完成加载
					controller.modelDidFinishedLoad(LoginModel.this);
				} catch (JSONException e) {
					LogUtil.error(this, "request", "onFailure:" + response);
					controller.modelFailedLoad(e, LoginModel.this);
					e.printStackTrace();
				}
			}
		};
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				onLoadingEnd();
				controller.modelFailedLoad(error, LoginModel.this);
			}
		};

//		HDRequest<JSONObject> request = new HDJsonObjectRequest(controller.getContext(), Method.POST,
//		        NetworkUtil.getAbsoluteUrl(url), param, listener, errorListener);
		HDRequest<JSONObject> request = new HDOtherRequest(controller.getContext(), Method.POST,
				NetworkUtil.getAbsoluteUrl(url), param, listener, errorListener);

		request.setTag(tag);

		return request;
	}

	/**
	 * 将服务器返回的有效信息保存本地
	 * 
	 * @param record
	 */
	private void storeSomething(JSONObject record) throws JSONException {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HrmsApplication.getApplication());
		Editor editor = preferences.edit();

		try {
			if (record.has(Constrants.SYS_PREFRENCES_TOKEN))
				editor.putString(Constrants.SYS_PREFRENCES_TOKEN, record.getString(Constrants.SYS_PREFRENCES_TOKEN));

			if (record.has("encryted_session_id"))
				editor.putString(Constrants.SYS_PREFRENCES_ENCRYTED_SESSION_ID, record.getString("encryted_session_id"));

			if (record.has(Constrants.SYS_PREFRENCES_USER_DESCRIPTION))
				editor.putString(Constrants.SYS_PREFRENCES_USER_DESCRIPTION,
				        record.getString(Constrants.SYS_PREFRENCES_USER_DESCRIPTION));
		} finally {
			editor.commit();
		}

	}

	@Override
	public <T> T getProcessData() {
		return null;
	}

}
