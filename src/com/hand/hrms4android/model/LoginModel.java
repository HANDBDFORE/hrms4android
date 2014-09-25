package com.hand.hrms4android.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.hand.hrms4android.util.Constrants;
import com.hand.hrms4android.util.LogUtil;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.UMJsonHttpResponseHandler;

public class LoginModel extends AbstractBaseModel<Void> {
	private ConfigReader configReader;

	public LoginModel(int id) {
		this(id, null);
	}

	public LoginModel(int id, ModelActivity activity) {
		super(id, activity);
		configReader = XmlConfigReader.getInstance();
	}

	@Override
	public void load(Model.LoadType loadType, Object param) {
		String service = "";
		try {
			service = configReader.getAttr(new Expression(
			        "/config/application/activity[@name='login_activity']/request/url[@name='login_submit_url']",
			        "value"));
		} catch (ParseExpressionException e) {
			e.printStackTrace();
			activity.modelFailedLoad(new Exception("Cannot get url from config file! "), this);
			return;
		}

		NetworkUtil.removeAllCookies();

		NetworkUtil.post(service, (RequestParams) param, new UMJsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				// 存储相关数据
				try {
					LoginModel.this.storeSomething(response.getJSONObject("body"));
				} catch (JSONException e) {
					e.printStackTrace();
					activity.modelFailedLoad(new Exception("error data"), LoginModel.this);
				}

				
				
				// 通知Activity已完成加载
				HrmsApplication.getApplication().initTimer();
				LoginModel.this.activity.modelDidFinishedLoad(LoginModel.this);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				LogUtil.error(this, "request", "onFailure:" + content);
				activity.modelFailedLoad(new Exception(error), LoginModel.this);
			}
		});
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
				editor.putString(Constrants.SYS_PREFRENCES_USER_DESCRIPTION, record.getString(Constrants.SYS_PREFRENCES_USER_DESCRIPTION));
		} finally {
			editor.commit();
		}

	}

	@Override
	public int getModelId() {
		return 0;
	}

}
