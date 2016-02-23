package com.hand.hrms4android.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.Toast;

import com.hand.hrms4android.activity.DeliverActivity;
import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.UMJsonHttpResponseHandler;

public class AssginModel extends AbstractListModel<Map<String, String>> {

//	public ArrayList<Map<String,String>> loadAuroraDataset;
	
	public AssginModel(int id, ModelActivity activity) {
		super(id, activity);
		configReader = XmlConfigReader.getInstance();
	}

	private ConfigReader configReader;

	public static boolean isNumeric(String str){
		   for(int i=str.length();--i>=0;){
		      int chr=str.charAt(i);
		      if(chr<48 || chr>57)
		         return false;
		   }
		   return true;
		}	
	
	@Override
	public void load(LoadType loadType, Object param) {
		String[] ps=(String[]) param;
//		try {
//			String actionName = configReader.getAttr(new Expression(
//			        "/config/application/activity[@name='assgin_activity']/request/url[@name='employee_query_url']",
//			        "value"));
			String actionName = "/modules/mobile_um/client/commons/todo/mobile_employee_synch.svc";

			RequestParams params = new RequestParams();
//			params.put("sourceSystemName", ps[0]);
//			params.put("keyword", ps[1]);

//			params.put("mobile_employee_keyword", ps[1]);
			params.put("localId", ps[2]);

			
			NetworkUtil.post(actionName, params, new UMJsonHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, JSONObject response) {
					
					try {
						JSONArray list = response.getJSONObject("body").getJSONArray("list");
						

						loadAuroraDataset =new ArrayList<Map<String,String>>();  
						
						for (int i = 0; i < list.length(); i++) {
	                        JSONObject jsonRecord = list.getJSONObject(i);
	                        Map<String, String> record = new HashMap<String, String>();
	                        record.put("employee_id", jsonRecord.getString("employee_id"));
	                        record.put("employee_code", jsonRecord.getString("employee_code"));
	                        record.put("name", jsonRecord.getString("name"));
	                        record.put("email", jsonRecord.getString("email"));
	                        record.put("mobile", jsonRecord.getString("mobile"));
	                        record.put("job", jsonRecord.getString("job"));
	                        loadAuroraDataset.add(record);
	                        
	                        Log.d("LEN",Integer.toString(list.length()));
                        }
						
						activity.modelDidFinishedLoad(AssginModel.this);
					} catch (JSONException e) {
						onFailure(e, "返回数据不正确");
					}
				}
				
				@Override
				public void onFailure(Throwable error, String content) {
					activity.modelFailedLoad(new IOException(error.getMessage()), AssginModel.this);
				}
			});

//		}catch (ParseExpressionException e) {
//			e.printStackTrace();
//			activity.modelFailedLoad(e, AssginModel.this);
//			return;
//		}
	}

	@Override
	public List<Map<String, String>> getProcessData() {
		
		return loadAuroraDataset;
		
	}

}

