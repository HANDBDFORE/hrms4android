package com.hand.hrms4android.application;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

import com.hand.hrms4android.util.PlatformUtil;
import com.igexin.slavesdk.MessageManager;

public class HrmsApplication extends Application {
	private static HrmsApplication instance;

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
			//初始化极光推送
	         JPushInterface.setDebugMode(true);
	         JPushInterface.init(this);
//		// 推送
//		MessageManager.getInstance().initialize(instance);

	}

}
