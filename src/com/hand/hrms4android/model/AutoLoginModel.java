package com.hand.hrms4android.model;

import java.util.Map;

import org.json.JSONObject;

import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.UMJsonHttpResponseHandler;

public class AutoLoginModel extends AbstractBaseModel<Void> {

	private ConfigReader configReader;

	public AutoLoginModel(int id, ModelActivity activity) {
		super(id, activity);
		configReader = XmlConfigReader.getInstance();
	}

	@Override
	public void load(LoadType loadType, Object param) {
		@SuppressWarnings("unchecked")
		RequestParams params = new RequestParams((Map<String, String>) param);
		NetworkUtil.removeAllCookies();
		String service = "";

		try {
			service = configReader
			        .getAttr(new Expression(
			                "/config/application/activity[@name='login_activity']/request/url[@name='auto_login_url']",
			                "value"));
		} catch (ParseExpressionException e) {
			e.printStackTrace();
			activity.modelFailedLoad(new Exception("Cannot get url from config file! "), this);
			return;
		}

		NetworkUtil.post(service, params, new UMJsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				activity.modelDidFinishedLoad(AutoLoginModel.this);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				activity.modelFailedLoad(new Exception(error.getMessage()), AutoLoginModel.this);
			}

		});
	}

}
