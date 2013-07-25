package com.hand.hrms4android.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.UMJsonHttpResponseHandler;

public class DeliverModel extends AbstractListModel<Map<String, String>> {

	public DeliverModel(int id, ModelActivity activity) {
		super(id, activity);
		configReader = XmlConfigReader.getInstance();
	}

	private ConfigReader configReader;

	@Override
	public void load(LoadType loadType, Object param) {
		String[] ps=(String[]) param;

		try {
			String actionName = configReader.getAttr(new Expression(
			        "/config/application/activity[@name='deliver_activity']/request/url[@name='employee_query_url']",
			        "value"));

			RequestParams params = new RequestParams();
			params.put("sourceSystemName", ps[0]);
			params.put("keyword", ps[1]);

			NetworkUtil.post(actionName, params, new UMJsonHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, JSONObject response) {
					
					try {
						JSONArray list = response.getJSONObject("body").getJSONArray("list");
						loadAuroraDataset =new ArrayList<Map<String,String>>();  
						for (int i = 0; i < list.length(); i++) {
	                        JSONObject jsonRecord = list.getJSONObject(i);
	                        Map<String, String> record = new HashMap<String, String>();
	                        record.put("realEmployeeId", jsonRecord.getString("realEmployeeId"));
	                        record.put("name", jsonRecord.getString("name"));
	                        record.put("description", jsonRecord.getString("description"));
	                        loadAuroraDataset.add(record);
                        }
						
						activity.modelDidFinishedLoad(DeliverModel.this);
					} catch (JSONException e) {
						onFailure(e, "返回数据不正确");
					}
				}
				
				@Override
				public void onFailure(Throwable error, String content) {
					activity.modelFailedLoad(new IOException(error.getMessage()), DeliverModel.this);
				}
			});

		} catch (ParseExpressionException e) {
			e.printStackTrace();
			activity.modelFailedLoad(e, DeliverModel.this);
			return;
		}
	}

	@Override
	public List<Map<String, String>> getProcessData() {
		return loadAuroraDataset;
	}

}
