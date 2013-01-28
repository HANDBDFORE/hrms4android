package com.hand.hrms4android.util;

import android.util.Log;

public class LogUtil {

	public static void error(Object obj, String tag, String message) {
		if (isDebug()) {
			Log.e(obj.getClass().getName() + "  " + tag, message);
		}
	}

	public static void info(Object obj, String tag, String message) {
		if (isDebug()) {
			Log.i(obj.getClass().getName() + "  " + tag, message);
		}
	}

	public static void debug(Object obj, String tag, String message) {
		if (isDebug()) {
			Log.d(obj.getClass().getName() + "  " + tag, message);
		}
	}

	public static void verbose(Object obj, String tag, String message) {
		if (isDebug()) {
			Log.v(obj.getClass().getName() + "  " + tag, message);
		}
	}

	private static boolean isDebug() {
		return false;
	}
}
