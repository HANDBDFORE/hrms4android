package com.hand.hrms4android.network;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KVParameterSource implements RequestParameterSource {

	private final Map<String, String> nameValuePare;

	public KVParameterSource() {
		nameValuePare = new HashMap<String, String>();
	}

	public KVParameterSource(Map<String, String> nameValuePare) {
		this.nameValuePare = new HashMap<String, String>(nameValuePare);
	}

	public KVParameterSource addParam(String key, String value) {
		nameValuePare.put(key, value);
		return this;
	}

	public KVParameterSource addParam(String key, int value) {
		nameValuePare.put(key, String.valueOf(value));
		return this;
	}

	@Override
	public Map<String, String> getParams() {
		return nameValuePare;
	}

	@Override
	public String getParamsAsString() {
		StringBuilder sb = new StringBuilder();

		Set<String> keys = nameValuePare.keySet();
		for (String key : keys) {
			sb.append(key);
			sb.append("=");
			try {
				sb.append(URLEncoder.encode(nameValuePare.get(key), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
			sb.append("&");
		}

		return sb.toString();
	}

}
