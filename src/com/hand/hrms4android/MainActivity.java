package com.hand.hrms4android;

import java.util.List;
import java.util.Map;

import com.hand.hrms4android.network.NetworkUtil;
import com.loopj.android.http.HDJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		RequestParams params = new RequestParams();
		params.put("user_name", "2994");
		params.put("user_password", "handhand");

		NetworkUtil.post("", params, new HDJsonHttpResponseHandler() {

			@Override
			public void onStart() {
				System.out.println("onStart()");
			}

			@Override
			public void onFinish() {
				System.out.println("onFinish()");
			}

			@Override
			public void onFailure(Throwable error, String content) {
				Log.e("tag", content);
			}

			@Override
			public void onSuccess(int statusCode, List<Map<String, String>> dataset) {
				Log.i("tag", dataset.toString());

			}

		});

	}

}
