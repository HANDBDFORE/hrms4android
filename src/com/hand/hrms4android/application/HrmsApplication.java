package com.hand.hrms4android.application;

import java.io.IOException;

import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.hand.hrms4android.util.PlatformUtil;

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

		if (PlatformUtil.getAndroidSDKVersion() == 14) {
			// 怪异的问题
			try {
				Class.forName("android.os.AsyncTask");
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		}

		// xmlconfig读取工具
		try {
			XmlConfigReader.createInstanceByInputStream(getAssets().open("android-backend-config.xml"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		
		
		
		
	}
	
	
	
}
