package com.hand.hrms4android.util;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

public class TempTransfer {
	public static final Integer KEY_TODO_LIST_MODEL = Integer.valueOf(0);
	public static final Integer KEY_DONE_LIST_MODEL = Integer.valueOf(1);

	@SuppressLint("UseSparseArrays")
	public static Map<Integer, Object> container = new HashMap<Integer, Object>();

}
