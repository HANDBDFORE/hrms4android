package com.loopj.android.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Message;

/**
 * 适合于aurora框架的网络请求，对返回数据进行加工组装
 * 
 * @author emerson
 */
public class HDJsonHttpResponseHandler extends AsyncHttpResponseHandler {

	private static final int WHAT_BASE = 10086;

	/**
	 * 调用方根据需要传入对象
	 */
	protected Object info;

	public HDJsonHttpResponseHandler() {
		this(null);
	}

	/**
	 * @param info
	 *            根据调用方需要，传入需要的对象，随后可以在onSuccess或者其他任何位置拿到此对象
	 */
	public HDJsonHttpResponseHandler(Object info) {
		this.info = info;
	}

	/**
	 * 解析数据成功
	 */
	private static final int WHAT_SUCCESS = WHAT_BASE + 1;

	@Override
	protected void sendResponseMessage(HttpResponse response) {
		StatusLine status = response.getStatusLine();
		String responseBody = null;
		try {
			HttpEntity entity = null;
			HttpEntity temp = response.getEntity();
			if (temp != null) {
				entity = new BufferedHttpEntity(temp);
				responseBody = EntityUtils.toString(entity, "UTF-8").trim();
				System.out.println(responseBody);
			}
		} catch (IOException e) {
			sendFailureMessage(e, (String) null);
		}

		if (status.getStatusCode() >= 300) {
			sendFailureMessage(
					new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()),
					responseBody);
		} else {

			try {
				// 对结果json进行处理
				JSONObject responseJson = new JSONObject(responseBody);
				boolean success = responseJson.getBoolean("success");

				// 判断服务器返回的处理结果成功与否
				if (success) {
					// 成功，取得result数据，然后发送给需要处理的地方
					JSONObject resultObject = responseJson.getJSONObject("result");
					sendSuccessMessage(status.getStatusCode(), resultObject.toString());
				} else {
					// 失败，得到失败信息
					String errorMessage = null;
					if (responseJson.has("error")) {
						JSONObject errorJson = responseJson.getJSONObject("error");
						errorMessage = errorJson.getString("message");
					} else {
						errorMessage = "Unknown Error";
					}
					sendFailureMessage(new Exception(errorMessage), errorMessage);
				}

			} catch (JSONException e) {
				e.printStackTrace();
				sendFailureMessage(e, "An error occured when converting server response.");
			}

		}
	}

	@Override
	protected void sendSuccessMessage(int statusCode, String responseBody) {
		try {
			List<Map<String, String>> jsonResponse = parseResponse(responseBody);
			// 将返回状态码和List<Map<String, String>>形式的dataset传给需要的地方处理
			sendMessage(obtainMessage(WHAT_SUCCESS, new Object[] { statusCode, jsonResponse }));
		} catch (JSONException e) {
			sendFailureMessage(e, responseBody);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void handleMessage(Message msg) {
		switch (msg.what) {
		case WHAT_SUCCESS:
			Object[] response = (Object[]) msg.obj;
			onSuccess(((Integer) response[0]).intValue(), (List<Map<String, String>>) response[1]);
			break;
		default:
			super.handleMessage(msg);
		}
	}

	@Override
	protected void handleFailureMessage(Throwable e, String responseBody) {
		super.handleFailureMessage(e, responseBody);
	}

	/**
	 * 成功后的调用，需要覆盖
	 * 
	 * @param statusCode
	 * @param response
	 */
	public void onSuccess(int statusCode, List<Map<String, String>> dataset) {
	}

	/**
	 * 从服务器得到的结果，将dataset转换为json对象，有可能是JsonObject或者是JsonArray
	 * 
	 * @param responseBody
	 * @return 组装好的数据
	 * @throws JSONException
	 */
	protected List<Map<String, String>> parseResponse(String responseBody) throws JSONException {
		// trim the string to prevent start with blank, and test if the string
		// is valid JSON, because the parser don't do this :(. If Json is not
		// valid this will return null
		responseBody = responseBody.trim();

		List<Map<String, String>> dataset = new LinkedList<Map<String, String>>();

		/*
		 * 传入的responseBody是返回结果中的result部分，由于框架特点，
		 * 如果是单条数据，result直接就是结果对象；如果是多条记录， 那么result中会再包含一个record名的jsonarray
		 */

		// 生成result对象
		JSONObject resultObject = new JSONObject(responseBody);

		if (resultObject.has("record")) {
			// 说明是多条记录
			JSONArray records = resultObject.getJSONArray("record");
			dataset = convertJsonArrayToArray(records);
		} else {
			// 说明是单条记录
			dataset.add(convertJsonToMap(resultObject));

		}

		return dataset;
	}

	/**
	 * 将json串转换为map
	 * 
	 * @param jsonObject
	 * @return
	 * @throws JSONException
	 */
	private Map<String, String> convertJsonToMap(JSONObject jsonObject) throws JSONException {
		Map<String, String> record = new HashMap<String, String>();

		// 遍历json串
		for (@SuppressWarnings("unchecked")
		Iterator<String> iter = jsonObject.keys(); iter.hasNext();) {
			String key = iter.next();
			record.put(key, jsonObject.get(key).toString());
		}
		return record;
	}

	/**
	 * 将jsonArray转换为类似dataset的结构
	 * 
	 * @param jsonArray
	 * @return
	 * @throws JSONException
	 */
	private List<Map<String, String>> convertJsonArrayToArray(JSONArray jsonArray)
			throws JSONException {
		List<Map<String, String>> dataset = new LinkedList<Map<String, String>>();

		// 循环数组
		for (int i = 0; i < jsonArray.length(); i++) {
			Map<String, String> record = convertJsonToMap(jsonArray.getJSONObject(i));
			dataset.add(record);
		}

		return dataset;
	}
}
