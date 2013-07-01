package com.loopj.android.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Message;

import com.hand.hrms4android.exception.AuroraServerFailure;
import com.hand.hrms4android.util.LogUtil;

/**
 * 适合于aurora框架的网络请求，对返回数据进行加工组装
 * 
 * @author emerson
 */
public class UMJsonHttpResponseHandler extends AsyncHttpResponseHandler {

	private static final int WHAT_BASE = 2652 + 3752;

	/**
	 * 调用方根据需要传入对象
	 */
	protected Object info;

	public UMJsonHttpResponseHandler() {
		this(null);
	}

	/**
	 * @param info
	 *            根据调用方需要，传入需要的对象，随后可以在onSuccess或者其他任何位置拿到此对象
	 */
	public UMJsonHttpResponseHandler(Object info) {
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
				LogUtil.debug(this, "server response", responseBody);
			}
		} catch (IOException e) {
			sendFailureMessage(e, (String) null);
		}

		if (status.getStatusCode() >= 300) {
			sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()),
			        responseBody);
		} else {
			/*
			 * var response = { "head" : { "message" : "login success", "code" :
			 * "ok" }, "body" : { "token" : "",// 登陆成功后返回的凭证 } };
			 */
			try {
				// 对结果json进行处理
				JSONObject responseJson = new JSONObject(responseBody);
				JSONObject headObject = responseJson.getJSONObject("head");
				boolean success = headObject.getString("code").equals("ok");

				// 判断服务器返回的处理结果成功与否
				if (success) {
					// 成功，发送给需要处理的地方
					sendSuccessMessage(status.getStatusCode(), responseBody);
				} else {
					// 失败，得到失败信息
					String errorMessage = null;
					if (headObject.has("message")) {
						errorMessage = headObject.getString("message");
					} else {
						errorMessage = "Unknown Error";
					}
					sendFailureMessage(new AuroraServerFailure(errorMessage), errorMessage);
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
			JSONObject jsonResponse = new JSONObject(responseBody);
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
			onSuccess(((Integer) response[0]).intValue(), (JSONObject) response[1]);
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
	public void onSuccess(int statusCode, JSONObject response) {
	}
}
