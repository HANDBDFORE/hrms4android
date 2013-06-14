package com.hand.hrms4android.util;

import java.util.Map;
import java.util.Set;

public class PlaceHolderReplacer {
//	public static String replaceForValue(Map<String, String> record, String placeHolder) {
//		Set<String> keys = record.keySet();
//
//		for (String key : keys) {
//			if (record.get(key) == null) {
//				continue;
//			}
//
//			StringBuilder sb = new StringBuilder("${");
//			sb.append(key);
//			sb.append("}");
//			String target = sb.toString();
//			String targetValue = record.get(key) == null ? "" : record.get(key);
//
//			placeHolder = placeHolder.replace(target, targetValue);
//		}
//		return placeHolder;
//	}
	
	public static String replaceForValue(Map<String, String> record, String placeHolder) {
		return replaceForValue(record, placeHolder, true);
	}

	public static String replaceForValue(Map<String, String> record, String placeHolder,
	        boolean outputPlaceHolderWhenNotFound) {
		Set<String> keys = record.keySet();

		for (String key : keys) {
			if (outputPlaceHolderWhenNotFound) {
				if (record.get(key) == null) {
					continue;
				}
            }

			StringBuilder sb = new StringBuilder("${");
			sb.append(key);
			sb.append("}");
			String target = sb.toString();
			String targetValue = record.get(key) == null ? "" : record.get(key);

			placeHolder = placeHolder.replace(target, targetValue);
		}
		return placeHolder;
	}

}
