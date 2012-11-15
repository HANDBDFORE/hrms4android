package com.hand.hrms4android.network;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class NetworkUtil {
	// private static final String BASE_URL =
	// "http://172.20.0.72:8080/hr_new/modules/ios/public/login_iphone.svc";
	private static final String BASE_URL = "http://192.168.10.100:8080/TestServer/Test";
//	private static final String BASE_URL = "http://10.213.214.79:8080/TestServer/Test";

	/**
	 * 以get方式请求
	 * 
	 * @param url
	 *            除了基础地址之外的部分
	 * @param params
	 * @param responseHandler
	 *            回调
	 */
	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {

		AsyncHttpClient client = new AsyncHttpClient();
		client.get(getAbsoluteUrl(url), params, responseHandler);

	}

	/**
	 * 以post方式提交数据（非文件）
	 * 
	 * @param url
	 *            除了基础地址之外的部分
	 * @param params
	 * @param responseHandler
	 *            回调
	 */
	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {

		AsyncHttpClient client = new AsyncHttpClient();
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	/**
	 * 转换路径
	 * 
	 * @param relativeUrl
	 *            除去基础地址之外的部分
	 * @return
	 */
	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}
}
