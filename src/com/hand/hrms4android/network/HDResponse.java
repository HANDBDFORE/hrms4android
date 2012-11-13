package com.hand.hrms4android.network;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class HDResponse {

	public static final String SUCCESS = "succ";
	public static final String MESSAGE = "msg";

	private int errorCode;

	private String msg;

	protected String body;

	private Map<String, String> params;

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	private void parserHead() throws JSONException {
		JSONObject json = new JSONObject(body);
		setErrorCode(json.getInt(SUCCESS));
		setMsg(json.getString(MESSAGE));
		json = null;
		return;
	}

	public boolean parser() {
		try {
			parserHead();
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		// 若返回CODE值是1,則解析后面的content
		if (errorCode == 1) {
			return parserAgain();
		}
		// 返回true指示网絡上有回复
		return true;
	}

	public abstract boolean parserAgain();
}
