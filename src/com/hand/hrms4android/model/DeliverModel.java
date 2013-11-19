package com.hand.hrms4android.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.hand.hrms4android.core.HDAbstractModel;
import com.hand.hrms4android.core.ModelViewController;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.network.HDJsonObjectRequest;
import com.hand.hrms4android.network.HDRequest;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;

public class DeliverModel extends HDAbstractModel {
	
	private List<Map<String,String>> data;
	

	public DeliverModel(int id, ModelViewController activity) {
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

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("sourceSystemName", ps[0]);
			params.put("keyword", ps[1]);

//			NetworkUtil.post(actionName, params, new UMJsonHttpResponseHandler(){
//				@Override
//				public void onSuccess(int statusCode, JSONObject response) {
//					
//					try {
//						JSONArray list = response.getJSONObject("body").getJSONArray("list");
//						loadAuroraDataset =new ArrayList<Map<String,String>>();  
//						for (int i = 0; i < list.length(); i++) {
//	                        JSONObject jsonRecord = list.getJSONObject(i);
//	                        Map<String, String> record = new HashMap<String, String>();
//	                        record.put("realEmployeeId", jsonRecord.getString("realEmployeeId"));
//	                        record.put("name", jsonRecord.getString("name"));
//	                        record.put("description", jsonRecord.getString("description"));
//	                        loadAuroraDataset.add(record);
//                        }
//						
//						activity.modelDidFinishedLoad(DeliverModel.this);
//					} catch (JSONException e) {
//						onFailure(e, "返回数据不正确");
//					}
//				}
//				
//				@Override
//				public void onFailure(Throwable error, String content) {
//					activity.modelFailedLoad(new IOException(error.getMessage()), DeliverModel.this);
//				}
//			});
//			
			requestQueue.add(genRequest(this, param, NetworkUtil.getAbsoluteUrl(actionName)));
//
		} catch (ParseExpressionException e) {
			e.printStackTrace();
			handleError(e);
			return;
		}
	}
	
	private Request<JSONObject> genRequest(Object tag, Object param, String url) {
		Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				onLoadingEnd();
				try {
					JSONArray list = response.getJSONObject("body").getJSONArray("list");
					data =new ArrayList<Map<String,String>>();  
					for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonRecord = list.getJSONObject(i);
                        Map<String, String> record = new HashMap<String, String>();
                        record.put("realEmployeeId", jsonRecord.getString("realEmployeeId"));
                        record.put("name", jsonRecord.getString("name"));
                        record.put("description", jsonRecord.getString("description"));
                        data.add(record);
                    }
					
					controller.modelDidFinishedLoad(DeliverModel.this);
				} catch (JSONException e) {
					handleError(new RuntimeException("返回数据不正确 "+ e.getMessage()));
				}
			}
		};
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				onLoadingEnd();
				handleError(error);
			}
		};

		HDRequest<JSONObject> request = new HDJsonObjectRequest(controller.getContext(), Method.POST,
		        NetworkUtil.getAbsoluteUrl(url), param, listener, errorListener);

		request.setTag(tag);

		return request;
	}

	@Override
	public List<Map<String, String>> getProcessData() {
		return data;
	}

}
