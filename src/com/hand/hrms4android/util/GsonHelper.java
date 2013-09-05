package com.hand.hrms4android.util;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GsonHelper {
	/**
	 * Gson parser
	 */
	public static <T> T convert(String json, Class<T> type) {
		Gson gson = new Gson();
		return gson.fromJson(json, type);
	}

	public static <T> T convert(String json, TypeToken<T> type) {
		Gson gson = new Gson();
		return gson.fromJson(json, type.getType());
	}

	public static <T> List<T> jsonToObject(String json, Type type) {
		Gson gson = new Gson();
		return gson.fromJson(json, type);
	}

	public static String objectToJson(Object object) {
		Gson gson = new Gson();
		return gson.toJson(object);
	}
}
