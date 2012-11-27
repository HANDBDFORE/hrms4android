package com.hand.hrms4android.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.network.NetworkUtil;
import com.loopj.android.http.HDJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class LoginModel extends BaseModel {

	public LoginModel() {
		this(null);
	}

	public LoginModel(ModelActivity activity) {
		super(activity);
	}

	@Override
	public void load(Model.LoadType loadType, Object param) {
		String service = "/modules/ios/public/login_iphone.svc";
		// String service = "/Test";
		NetworkUtil.post(service, (RequestParams) param, new HDJsonHttpResponseHandler() {

			@Override
			public void onStart() {
				Log.i("request", "onStart:");
			}

			@Override
			public void onFailure(Throwable error, String content) {
				Log.e("request", "onFailure:" + content);
			}

			@Override
			public void onSuccess(int statusCode, List<Map<String, String>> ds) {
				Log.i("request", "onSuccess  " + ds.toString());
				dataset = ds;

				// 存储相关数据
				LoginModel.this.storeSomething(ds.get(0));

				// 设置请求头
				LoginModel.this.updateRequestHeaders(ds.get(0));

				// 通知Activity已完成加载
				LoginModel.this.activity.modelDidFinishedLoad(LoginModel.this);
			}
		});
	}

	/**
	 * 将服务器返回的有效信息保存本地
	 * 
	 * @param record
	 */
	private void storeSomething(Map<String, String> record) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(HrmsApplication.getApplication());
		Editor editor = preferences.edit();

		String sid = record.get("sid");
		String encryted_session_id = record.get("encryted_session_id");

		editor.putString("sid", sid);
		editor.putString("encryted_session_id", encryted_session_id);
		editor.commit();

	}

	/**
	 * 将服务器返回有效信息存请求头
	 * 
	 * @param responseData
	 */
	private void updateRequestHeaders(Map<String, String> responseData) {
		String sid = responseData.get("sid");
		String encryted_session_id = responseData.get("encryted_session_id");

		NetworkUtil.addHeader("sid", sid);
		NetworkUtil.addHeader("encryted_session_id", encryted_session_id);
	}

}
