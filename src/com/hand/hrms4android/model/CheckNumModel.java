package com.hand.hrms4android.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.InputFilter.LengthFilter;
import android.widget.Toast;

import com.cfca.srcbulanview.R;
import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.UMJsonHttpResponseHandler;

public class CheckNumModel extends AbstractBaseModel<Void> {
	private ConfigReader configReader;
	private String p_res = null;
	private String key_id = null;
	
	public CheckNumModel(int id) {
		this(id,null);
		
	}
	
	public CheckNumModel(int id, ModelActivity activity) {
		super(id, activity);
		configReader = XmlConfigReader.getInstance();
	}

	@Override
	public void load(com.hand.hrms4android.model.Model.LoadType loadType,
			Object param) {
		// TODO 自动生成的方法存根
		try {
			String queryUrl = "modules/mobile/client/commons/login/check_number.svc";
			NetworkUtil.post(queryUrl, (RequestParams)param, new UMJsonHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, JSONObject response) {

					try {
						p_res = response.getJSONObject("body").getString("p_res");
						if(p_res.equals("-1")){
//							Toast.makeText(HrmsApplication.getApplication(), HrmsApplication.getApplication().getResources().getString(R.string.resmin1),Toast.LENGTH_SHORT).show();
						}else if(p_res.equals("0")){
//							Toast.makeText(HrmsApplication.getApplication(), HrmsApplication.getApplication().getResources().getString(R.string.res0),Toast.LENGTH_SHORT).show();
						}else if(p_res.equals("1")){
//							Toast.makeText(HrmsApplication.getApplication(), HrmsApplication.getApplication().getResources().getString(R.string.res1),Toast.LENGTH_SHORT).show();
						}
						key_id = response.getJSONObject("body").getString("key_id");;
					} catch (JSONException e) {
						e.printStackTrace();
					}

					activity.modelDidFinishedLoad(CheckNumModel.this);
				}
			});			
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
		
	}
	
	public String getRes(){
		return p_res;
	}

	public String getKeyId(){
		return key_id;
	}
}
