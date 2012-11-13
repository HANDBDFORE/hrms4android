package com.hand.hrms4android.network.request;

import org.json.JSONException;
import org.json.JSONObject;

import com.hand.hrms4android.network.NetworkRuleException;
import com.hand.hrms4android.network.HDRequest;
import com.hand.hrms4android.network.response.AutoFillResponse;
import com.hand.hrms4android.network.util.RequestCheckUtils;

public class AutoFillRequest implements HDRequest<AutoFillResponse> {

	private static final String KEYWORD = "keyword";
	private static final String LIMIT = "limit";
	
	private static final int DEFAULT_LIMIT = 5;
	
	private long limit = DEFAULT_LIMIT;
	private String keyword;
	
	public void setKeyword(String keyword)
	{
		this.keyword = keyword;
	}
	
	public void setLimit(int limit)
	{
		this.limit = limit;
	}
	
	@Override
	public String getApiMethodName() {
		// TODO Auto-generated method stub
		return "autofill";
	}

	@Override
	public Class<AutoFillResponse> getResponseClass() {
		// TODO Auto-generated method stub
		return AutoFillResponse.class;
	}

	@Override
	public String getBody() {
		// TODO Auto-generated method stub
		JSONObject json=new JSONObject();
		try {
			json.put(KEYWORD, keyword);
			json.put(LIMIT, limit);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		return json.toString();
	}

	@Override
	public void check() throws NetworkRuleException {
		// TODO Auto-generated method stub
		RequestCheckUtils.checkMinValue(limit, 1, LIMIT);
		RequestCheckUtils.checkMaxValue(limit, 10, LIMIT);
	}
}