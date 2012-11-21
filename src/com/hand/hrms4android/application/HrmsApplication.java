package com.hand.hrms4android.application;

import android.app.Application;

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
	}
}
