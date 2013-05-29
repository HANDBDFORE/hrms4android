package com.hand.hrms4android.model;

import java.util.List;
import java.util.Map;

import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.loopj.android.http.HDJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AutoLoginModel extends AbstractBaseModel<Void> {
	
	private ConfigReader configReader;

	public AutoLoginModel(int id, ModelActivity activity) {
		super(id, activity);
		configReader = XmlConfigReader.getInstance();
	}

	@Override
	public void load(LoadType loadType, Object param) {
		RequestParams params = new RequestParams((Map<String, String>)param);
		NetworkUtil.removeAllCookies();
		String service = "";
		
		try {
			service = configReader.getAttr(new Expression(
			        "/config/application/activity[@name='login_activity']/request/url[@name='auto_login_url']", "value"));
		} catch (ParseExpressionException e) {
			e.printStackTrace();
			activity.modelFailedLoad(new Exception("Cannot get url from config file! "), this);
			return;
		}
		
		NetworkUtil.post(service, params, new HDJsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, List<Map<String, String>> dataset) {
			    activity.modelDidFinishedLoad(AutoLoginModel.this);
			}
			
			@Override
			public void onFailure(Throwable error, String content) {
				 activity.modelFailedLoad(new Exception(error.getMessage()), AutoLoginModel.this);
			}
		});
	}

}
