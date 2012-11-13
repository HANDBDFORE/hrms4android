package com.hand.hrms4android.network.response;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hand.hrms4android.network.HDResponse;
import com.hand.hrms4android.network.response.entity.AutoFillRecord;

public class AutoFillResponse extends HDResponse {
	private List<AutoFillRecord> mResult = new LinkedList<AutoFillRecord>();
	
	@Override
	public boolean parserAgain() {
		// TODO Auto-generated method stub
		JSONObject json = null;
		try {
			json = new JSONObject(body);
			JSONArray keywords = json.getJSONArray("keywords");
			
			for (int i = 0; i < keywords.length(); i++) {
				JSONObject obj=keywords.getJSONObject(i);
				AutoFillRecord record = new AutoFillRecord();
				record.name = obj.getString("name");
				record.count = obj.getInt("count");
				mResult.add(record);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	} 
	
	public List<AutoFillRecord> getAutoFillRecords()
	{
		return mResult;
	}
}
