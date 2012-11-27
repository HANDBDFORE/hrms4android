package com.hand.hrms4android.activity;

import java.util.List;
import java.util.Map;

import com.hand.hrms4android.R;
import com.hand.hrms4android.network.NetworkUtil;
import com.loopj.android.http.HDJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivityBak extends Activity {

	private EditText usernameEditText;
	private EditText passwordEditText;
	private Button loginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		bindAllViews();
	}

	private void bindAllViews() {
		usernameEditText = (EditText) findViewById(R.id.activity_login_textedit_username);
		passwordEditText = (EditText) findViewById(R.id.activity_login_textedit_password);
		loginButton = (Button) findViewById(R.id.activity_login_textedit_loginButton);

		loginButton.setOnClickListener(new LoginButtonClickListener());
	}

	private class LoginButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String username = usernameEditText.getText().toString();
			String password = passwordEditText.getText().toString();

			RequestParams params = new RequestParams();
			params.put("user_name", username);
			params.put("user_password", password);
			params.put("langugae", "简体中文");
			params.put("user_language", "ZHS");
			params.put("is_ipad", "N");
			params.put("device_type", "Android");
			params.put("company_id", "1");
			params.put("role_id", "41");

			NetworkUtil.addHeader("hello", "world");
			NetworkUtil.post("/modules/ios/public/login_iphone.svc", params, new HDJsonHttpResponseHandler() {

				@Override
				public void onStart() {
					super.onStart();
					Log.i("request", "onStart:");
				}

				@Override
				public void onFailure(Throwable error, String content) {
					super.onFailure(error, content);
					Log.e("request", "onFailure:" + content);
				}

				@Override
				public void onSuccess(int statusCode, List<Map<String, String>> dataset) {
					super.onSuccess(statusCode, dataset);
					Log.i("request", dataset.toString());
				}

				@Override
				public void onFinish() {
					super.onFinish();
					Log.i("request", "onFinish:");
				}
			});

		}

	}

}
