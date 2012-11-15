package com.hand.hrms4android;

import java.util.List;
import java.util.Map;

import com.hand.hrms4android.network.Network;
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

		// HDTask task = new TestTask();
		// task.start();

		// TestClient.post("", null, new AsyncHttpResponseHandler() {
		//
		// @Override
		// public void onStart() {
		// super.onStart();
		// System.out.println("onStart()");
		// }
		//
		// @Override
		// public void onFinish() {
		// super.onFinish();
		// System.out.println("onFinish()");
		// }
		//
		// @Override
		// public void onSuccess(String content) {
		// super.onSuccess(content);
		// System.out.println("onSuccess:==========" + content);
		//
		// Message m = new Message();
		// m.obj = content;
		// new MyHandler(MainActivity.this).sendMessage(m);
		// }
		//
		// });

		RequestParams params = new RequestParams();
		params.put("user_name", "2994");
		params.put("user_password", "handhand");

		Network.post("", params, new HDJsonHttpResponseHandler() {

			@Override
			public void onStart() {
				System.out.println("onStart()");
			}

			@Override
			public void onFinish() {
				System.out.println("onFinish()");
			}

			@Override
			public void onSuccess(String content) {
				// Log.i("tag", content);
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

		// new Thread() {
		//
		// @Override
		// public void run() {
		// try {
		// HttpConnector connector = new HttpConnector();
		//
		// ArrayList<NameValuePair> nvs = new ArrayList<NameValuePair>();
		// nvs.add(new BasicNameValuePair("user_name", "2652"));
		// nvs.add(new BasicNameValuePair("user_password", "handhand"));
		// nvs.add(new BasicNameValuePair("user_language", "ZHS"));
		//
		// String s = connector
		// .sendRequest(
		// "http://172.20.0.20:8080/hr_new/modules/ios/public/login_iphone.svc",
		// nvs);
		// System.out.println(s);
		// } catch (InteractException e) {
		// e.printStackTrace();
		// }
		// }
		//
		// }.start();

	}

}
