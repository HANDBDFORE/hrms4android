package com.hand.hrms4android.model;

import java.util.List;
import java.util.Map;

import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.util.LogUtil;
import com.loopj.android.http.HDJsonHttpResponseHandler;

public class FunctionListModel extends AbstractModel {

	public FunctionListModel(int id, ModelActivity activity) {
		super(id, activity);

	}

	@Override
	public void load(LoadType loadType, Object param) {
		String url = param.toString();

		NetworkUtil.post(url, null, new HDJsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, List<Map<String, String>> dataset) {
				LogUtil.info(this, "onSuccess", dataset.toString());
				loadAuroraDataset = dataset;
				activity.modelDidFinishedLoad(FunctionListModel.this);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				LogUtil.info(this, "onFailure", content);
				// activity.modelFailedLoad(e, model)
			}
		});
	}

}
