package com.hand.hrms4android.network;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.CookieStore;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.util.Constrants;
import com.hand.hrms4android.util.LogUtil;

public class NetworkUtil {
	private static SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(HrmsApplication
	        .getApplication());

	private static Map<String, String> headers = new HashMap<String, String>();

	static {
		headers.put("product", android.os.Build.PRODUCT);
		headers.put("model", android.os.Build.MODEL);
		headers.put("SDK_VERSION", String.valueOf(android.os.Build.VERSION.SDK_INT));
		headers.put("DEVICE:", android.os.Build.DEVICE);
		headers.put("MANUFACTURER", android.os.Build.MANUFACTURER);
	}

	private static CookieStore cookieStore = null;


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
