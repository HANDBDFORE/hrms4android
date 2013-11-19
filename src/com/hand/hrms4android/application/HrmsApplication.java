package com.hand.hrms4android.application;

import java.util.Map;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.hand.hrms4android.network.RequestManager;
import com.hand.hrms4android.util.PlatformUtil;
import com.igexin.slavesdk.MessageManager;

public class HrmsApplication extends Application {
	private static HrmsApplication instance;

	private static final String SET_COOKIE_KEY = "Set-Cookie";
	private static final String COOKIE_KEY = "Cookie";
	private static final String SESSION_COOKIE = "JSESSIONID";

	/**
	 * 为了在程序任何位置获得context对象
	 * 
	 * @return 程序的application实例
	 */
	public static HrmsApplication getApplication() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		if (PlatformUtil.getAndroidSDKVersion() == 14) {
			// 怪异的问题
			try {
				Class.forName("android.os.AsyncTask");
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		// 推送
		MessageManager.getInstance().initialize(instance);
		
		RequestManager.init(this);

	}

	/**
	 * Checks the response headers for session cookie and saves it if it finds
	 * it.
	 * 
	 * @param headers
	 *            Response Headers.
	 */
	public void updateCoockies(Map<String, String> headers) {
		if (headers.containsKey(SET_COOKIE_KEY) && headers.get(SET_COOKIE_KEY).startsWith(SESSION_COOKIE)) {
			String cookie = headers.get(SET_COOKIE_KEY);
			if (cookie.length() > 0) {
				String[] splitCookie = cookie.split(";");
				String[] splitSessionId = splitCookie[0].split("=");
				cookie = splitSessionId[1];
				Editor prefEditor = getPreferences().edit();
				prefEditor.putString(SESSION_COOKIE, cookie);
				prefEditor.commit();
			}
		}
	}

	/**
	 * Adds session cookie to headers if exists.
	 * 
	 * @param headers
	 */
	public final void addSessionCookie(Map<String, String> headers) {
		String sessionId = getPreferences().getString(SESSION_COOKIE, "");
		if (sessionId.length() > 0) {
			StringBuilder builder = new StringBuilder();
			builder.append(SESSION_COOKIE);
			builder.append("=");
			builder.append(sessionId);
			if (headers.containsKey(COOKIE_KEY)) {
				builder.append("; ");
				builder.append(headers.get(COOKIE_KEY));
			}
			headers.put(COOKIE_KEY, builder.toString());
		}
	}

	private SharedPreferences getPreferences() {
		return this.getSharedPreferences("coockie", MODE_MULTI_PROCESS);
	}

}
