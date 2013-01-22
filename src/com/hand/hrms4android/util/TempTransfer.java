package com.hand.hrms4android.util;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

public class TempTransfer {
	public static final Integer KEY_TODO_LIST_MODEL = Integer.valueOf(0);

	@SuppressLint("UseSparseArrays")
	public static Map<Integer, Object> container = new HashMap<Integer, Object>();

	public static String todoListAuroraRecordPKKey;

}
