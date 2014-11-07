package com.hand.hrms4android.application;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;

import com.hand.hrms4android.activity.LoadingActivity;
import com.hand.hrms4android.activity.LoginActivity;
import com.hand.hrms4android.util.PlatformUtil;
import com.igexin.slavesdk.MessageManager;

public class HrmsApplication extends Application {
	private static HrmsApplication instance;

	public List<Activity> activityList;
	public Timer timer;
	public int execTime;
	public Boolean enableTime;

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

		activityList = new ArrayList();
		timer = new Timer("overtime");
		enableTime = false;

		if (PlatformUtil.getAndroidSDKVersion() == 14) {
			// 怪异的问题
			try {
				Class.forName("android.os.AsyncTask");
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		// 初始化极光推送
		 JPushInterface.setDebugMode(true);
		 JPushInterface.init(this);

	}
	


	public void initTimer() {
		if (!enableTime) {
			return;
		}

		if (timer == null) {
			timer = new Timer();
		} else {
			timer.cancel();
			timer = new Timer();

		}

		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				goback();
				timer.cancel();
				timer = null;
			}
		}, this.execTime * 60 * 1000);
		// timer.cancel();
	}

	public void addActivity(Activity activity) {
		activityList.add(activity);

	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public boolean isBackground(Context context) {

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {

					return true;
				} else {

					return false;
				}
			}
		}
		return false;
	}

	public void goback() {
		Iterator activityList = this.activityList.iterator();
		while (activityList.hasNext()) {
			Activity activity = (Activity) activityList.next();
			if (activity != null) {
				activity.finish();
			}
		}

		while(isBackground(this)){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Intent intent = new Intent(this, LoadingActivity.class);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
//		if (isBackground(this)) {
//			Intent intent = new Intent(this, LoadingActivity.class);
//			 intent.addCategory(Intent.CATEGORY_DEFAULT);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//			 startActivity(intent);
//		} else {
//			Intent intent = new Intent(this, LoadingActivity.class);
//			intent.addCategory(Intent.CATEGORY_HOME);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			
//			startActivity(intent);
//
//		}
	}

}
