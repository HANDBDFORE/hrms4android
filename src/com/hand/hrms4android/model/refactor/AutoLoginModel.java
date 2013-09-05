package com.hand.hrms4android.model.refactor;

import java.util.Map;

import org.json.JSONObject;

import com.hand.hrms4android.core.HDAbstractModel;
import com.hand.hrms4android.core.ModelViewController;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.UMJsonHttpResponseHandler;

public class AutoLoginModel extends HDAbstractModel {

	private ConfigReader configReader;

	public AutoLoginModel(int id, ModelViewController controller) {
		super(id, controller);
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
			controller.modelFailedLoad(new Exception("Cannot get url from config file! "), this);
			return;
		}

		NetworkUtil.post(service, params, new UMJsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				controller.modelDidFinishedLoad(AutoLoginModel.this);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				controller.modelFailedLoad(new Exception(error.getMessage()), AutoLoginModel.this);
			}

		});
	}

	@Override
	public <T> T getProcessData() {
		throw new UnsupportedOperationException("不支持");
	}

}
