package com.hand.hrms4android.model;

import java.util.List;
import java.util.Map;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

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
	public void load(int type, Object param) {

		NetworkUtil.post("", (RequestParams) param, new HDJsonHttpResponseHandler() {

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
				Log.i("request", ds.toString());
				dataset = ds;

				LoginModel.this.storeSomething();

				LoginModel.this.activity.modelDidFinishedLoad(LoginModel.this);
			}

			@Override
			public void onFinish() {
				Log.i("request", "onFinish:");
			}
		});
	}

	private void storeSomething() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(HrmsApplication.getApplication());
		Editor editor = preferences.edit();

		Map<String, String> record = dataset.get(0);
		String sid = record.get("sid");
		String encryted_session_id = record.get("encryted_session_id");

		editor.putString("sid", sid);
		editor.putString("encryted_session_id", encryted_session_id);
		editor.commit();

	}
}
