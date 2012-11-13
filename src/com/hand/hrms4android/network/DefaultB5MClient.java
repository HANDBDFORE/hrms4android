package com.hand.hrms4android.network;

import java.io.IOException;
import java.util.Date;

import com.hand.hrms4android.network.HDHeader;
import com.hand.hrms4android.network.util.HDHashMap;
import com.hand.hrms4android.network.util.ResponseCheckUtils;
import com.hand.hrms4android.network.util.WebUtils;

public class DefaultB5MClient implements HDClient {

	private Boolean needCheckRequest = true;

	private String serverUrl;
	private String appKey;
	private String appSecret;

	private static final int DEFAULT_CONNECT_TIMEOUT = 10 * 1000; // 10s
	private static final int DEFAULT_READ_TIMEOUT = 10 * 1000; // 10s
	private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
	private int readTimeout = DEFAULT_READ_TIMEOUT;

	private static final String TEST_DOMAIN = "172.16.7.2";
	private static final String DOMAIN = "http://a.beta.bang5mai.com";
	private static final int SERVER_PORT = 8080;
	private static final String APP_SERVER = DOMAIN + ":" + SERVER_PORT
			+ "/appserver.do";
	private static final String TUAN_URL = DOMAIN
			+ "/detail.do?channel=tuan&p=android&docid=";
	private static final String PRODUCT_URL = DOMAIN
			+ "/detail.do?channel=product&p=android&docid=";

	private static final String APP_KEY = "123456";
	private static final String APP_SECRET = "654321";
	private HDHeader header;

	public DefaultB5MClient(String serverUrl, String appKey, String appSecret) {
		this.appKey = appKey;
		this.appSecret = appSecret;
		this.serverUrl = serverUrl;
	}

	public DefaultB5MClient() {
		this(APP_SERVER, APP_KEY, APP_SECRET);
		header = HDHeader.getInstance();
	}

	public void setB5MHeader(HDHeader header) {
		this.header = header;
	}

	public static String tuanDocidToURL(String docid) {
		return TUAN_URL + docid;
	}

	public static String productDocidToURL(String docid) {
		return PRODUCT_URL + docid;
	}

	@Override
	public <T extends HDResponse> T execute(HDRequest<T> request)
			throws NetworkException {

		T t = null;
		try {
			t = request.getResponseClass().newInstance();
		} catch (InstantiationException e2) {
			throw new NetworkException(e2);
		} catch (IllegalAccessException e3) {
			throw new NetworkException(e3);
		}

		if (this.needCheckRequest == true) {
			try {
				request.check();// if check failed,will throw ApiRuleException.
			} catch (NetworkRuleException e) {
				t.setErrorCode(Integer.valueOf(e.getErrCode()));
				t.setMsg(e.getErrMsg());
				return t;
			}
		}

		HDHashMap headers = header.getCurrentHead();
		// map.put(B5MHeader.METHOD, request.getApiMethodName());
		headers.put(HDHeader.CHANNEL, APP_KEY);
		Long timestamp = new Date().getTime();
		headers.put(HDHeader.TIMESTAMP, timestamp);
		headers.put(HDHeader.SIGN, "test");

		StringBuffer urlSb = new StringBuffer(serverUrl);
		urlSb.append("?").append("m=").append(request.getApiMethodName());
		String url = urlSb.toString();

		String rsp = null;
		try {
			String body = request.getBody();
			rsp = WebUtils.doPost(url, headers, body, connectTimeout,
					readTimeout);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new NetworkException(e);
		}

		//
		if (null == rsp) {
			ResponseCheckUtils.responseErrorException(request
					.getApiMethodName());
		}

		t.setBody(rsp);
		if (!t.parser()) {
			ResponseCheckUtils.parseErrorException(request.getApiMethodName(),
					rsp);
		}
		return t;
	}

	public Boolean getNeedCheckRequest() {
		return needCheckRequest;
	}

	public void setNeedCheckRequest(Boolean needCheckRequest) {
		this.needCheckRequest = needCheckRequest;
	}
}
