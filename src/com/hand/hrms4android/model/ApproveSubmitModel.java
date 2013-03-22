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

public class ApproveSubmitModel extends AbstractBaseModel<Void> {
	private ConfigReader configReader;

	public ApproveSubmitModel(int id, ModelActivity activity) {
		super(id, activity);

		configReader = XmlConfigReader.getInstance();
	}

	@Override
	public void load(LoadType loadType, Object param) {
		try {
			String actionURL = configReader.getAttr(new Expression(
			        "/config/application/activity[@name='todo_list_activity']/request/url[@name='action_submit_url']",
			        "value"));

			NetworkUtil.post(actionURL, null, new HDJsonHttpResponseHandler(param) {
				@Override
				public void onSuccess(int statusCode, List<Map<String, String>> dataset) {
					super.onSuccess(statusCode, dataset);
				}
			});
		} catch (ParseExpressionException e) {
			e.printStackTrace();
		}
	}

}
