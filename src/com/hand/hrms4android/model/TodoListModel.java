package com.hand.hrms4android.model;

import java.util.List;
import java.util.Map;

import android.util.Log;

import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.exception.ParseExpressionException;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.loopj.android.http.HDJsonHttpResponseHandler;

public class TodoListModel extends BaseModel {

	private ConfigReader configReader;

	public TodoListModel() {
		this(null);
	}

	public TodoListModel(ModelActivity activity) {
		super(activity);
		configReader = XmlConfigReader.getInstance();
	}

	@Override
	public void load(LoadType loadType, Object param) {
		String service = "";
		try {
			service = configReader
			        .getAttr(new Expression(
			                "/config/activity[@name='login_activity']/request/url[@name='todo_list_query_url']",
			                "value"));
		} catch (ParseExpressionException e) {
			e.printStackTrace();
			activity.modelFailedLoad(new Exception("Cannot get url from config file! "), this);
			return;
		}

		NetworkUtil.post(service, null, new HDJsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, List<Map<String, String>> ds) {
				dataset = ds;
				activity.modelDidFinishedLoad(TodoListModel.this);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				Log.e("request", "onFailure:" + content);
				activity.modelFailedLoad(new Exception(error), TodoListModel.this);
			}
		});
	}
}
