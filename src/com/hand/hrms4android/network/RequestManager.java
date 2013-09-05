package com.hand.hrms4android.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestManager {
	/**
	 * the queue :-)
	 */
	private static RequestQueue mRequestQueue;
	
	public static final String BASE_URL="http://10.213.212.58:8080/sns";

	/**
	 * Nothing to see here.
	 */
	private RequestManager() {
		// no instances
	}

	/**
	 * @param context
	 *            application context
	 */
	public static void init(Context context) {
		mRequestQueue = Volley.newRequestQueue(context);
	}

	/**
	 * @return instance of the queue
	 * @throws IllegalStatException
	 *             if init has not yet been called
	 */
	public static RequestQueue getRequestQueue() {
		if (mRequestQueue != null) {
			return mRequestQueue;
		} else {
			throw new IllegalStateException("Not initialized");
		}
	}
}
