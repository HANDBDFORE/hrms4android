package com.loopj.android.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 只在bm为bach_update时使用，其他用途不支持
 * 
 * @author emerson
 * 
 */
public class HDRequestParamsBatch extends RequestParams {

	public HDRequestParamsBatch() {
		super();
	}

	public HDRequestParamsBatch(Map<String, String> source) {
		super(source);
	}

	public HDRequestParamsBatch(Object... keysAndValues) {
		super(keysAndValues);
	}

	public HDRequestParamsBatch(String key, String value) {
		super(key, value);
	}

	@Override
	protected List<BasicNameValuePair> getParamsList() {
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

		JSONObject object = new JSONObject(urlParams);
		JSONObject submitObject = new JSONObject();
		JSONArray array = new JSONArray();
		array.put(object);
		try {
			submitObject.put("parameter", array);
			params.add(new BasicNameValuePair("_request_data", submitObject.toString()));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		return params;

	}
}
