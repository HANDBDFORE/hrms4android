package com.hand.hrms4android.model;

import java.io.IOException;
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

public class DeliverModel extends AbstractListModel<Map<String, String>> {

	public DeliverModel(int id, ModelActivity activity) {
		super(id, activity);
		configReader = XmlConfigReader.getInstance();
	}

	private ConfigReader configReader;

	@Override
	public void load(LoadType loadType, Object param) {
		String userInput = param.toString();

		try {
			String actionName = configReader.getAttr(new Expression(
			        "/config/application/activity[@name='deliver_activity']/request/url[@name='employee_query_url']",
			        "value"));

			RequestParams params = new RequestParams();
			params.put("parameter", userInput);

			NetworkUtil.post(actionName, params, new HDJsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, List<Map<String, String>> dataset) {
					DeliverModel.this.loadAuroraDataset = dataset;
					activity.modelDidFinishedLoad(DeliverModel.this);
					return;
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
