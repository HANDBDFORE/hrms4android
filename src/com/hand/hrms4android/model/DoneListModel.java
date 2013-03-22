package com.hand.hrms4android.model;

import java.util.List;
import java.util.Map;

import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.network.NetworkUtil;
import com.loopj.android.http.HDJsonHttpResponseHandler;

public class DoneListModel extends AbstractPageableModel<Map<String, String>> {

	public DoneListModel(ModelActivity doneListActivity, int id) {
		super(id, doneListActivity);
	}

	@Override
	public void load(LoadType loadType, Object param) {

		String url = param.toString();

		NetworkUtil.post(url, null, new HDJsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, List<Map<String, String>> dataset) {
				loadAuroraDataset = dataset;

				activity.modelDidFinishedLoad(DoneListModel.this);
			}
		});
	}

}
