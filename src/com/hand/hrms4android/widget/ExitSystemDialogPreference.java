package com.hand.hrms4android.widget;

import com.hand.hrms4android.util.StorageUtil;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class ExitSystemDialogPreference extends DialogPreference {

	public ExitSystemDialogPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ExitSystemDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			// 点击确定，退出系统

			// 发送退出请求

			// 清理本地信息
			StorageUtil.deleteDB();
			StorageUtil.removeSavedInfo();
			StorageUtil.removeCachedConfigFile();
			// 结束进程
			// android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);

		} else {

		}

	}

}
