package com.hand.hrms4android.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.CookieStore;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.util.Constrants;
import com.hand.hrms4android.util.LogUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class NetworkUtil {
	private static SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(HrmsApplication
	        .getApplication());

	private static Map<String, String> headers = new HashMap<String, String>();

	private static CookieStore cookieStore = null;

	/**
	 * 以get方式请求
	 * 
	 * @param url
	 *            除了基础地址之外的部分
	 * @param params
	 * @param responseHandler
	 *            回调
	 */
	public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {

		AsyncHttpClient client = new AsyncHttpClient();
		client.setCookieStore(cookieStore);
		String token = mPreferences.getString("token", "");
		if (token.length() > 0) {
			addHeader("token", token);
		}
		client = addClientHeaders(client, headers);
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
	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		AsyncHttpClient client = new AsyncHttpClient();

		client.setCookieStore(cookieStore);

		String token = mPreferences.getString("token", "");
		if (token.length() > 0) {
			addHeader("token", token);
			addClientHeaders(client, headers);
		}

		if (params != null) {
			LogUtil.debug(NetworkUtil.class, "send", params.toString());
		}

		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	/**
	 * 转换路径
	 * 
	 * @param relativeUrl
	 *            除去基础地址之外的部分
	 * @return
	 */
	public static String getAbsoluteUrl(String relativeUrl) {
		LogUtil.info(NetworkUtil.class, "request", getBaseUrl() + relativeUrl);
		return getBaseUrl() + relativeUrl;
	}

	/**
	 * @return
	 */
	private static String getBaseUrl() {
		String base = mPreferences.getString(Constrants.SYS_PREFRENCES_SERVER_BASE_URL, "");
		if (base.length() != 0) {
			if (base.charAt(base.length() - 1) != '/') {
				base += "/";
			}
		}
		return base;
	}

	private static AsyncHttpClient addClientHeaders(AsyncHttpClient client, Map<String, String> headers) {
		Set<String> keys = headers.keySet();
		for (String key : keys) {
			client.addHeader(key, headers.get(key));
		}
		return client;
	}

	public static void addHeader(String key, String value) {
		headers.put(key, value);
	}

	public static void setHeader(Map<String, String> header) {
		if (header == null) {
			return;
		}
		NetworkUtil.headers = header;
	}

	public static CookieStore getCookieStore() {
		return cookieStore;
	}

	public static void setCookieStore(CookieStore cookieStore) {
		NetworkUtil.cookieStore = cookieStore;
	}

	public static void removeAllCookies() {
		cookieStore = null;
		CookieSyncManager.createInstance(HrmsApplication.getApplication());
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		CookieSyncManager.getInstance().sync();
	}
}
