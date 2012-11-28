package com.hand.hrms4android.model;

import java.util.List;
import java.util.Map;

import com.hand.hrms4android.activity.ModelActivity;

public class BaseModel implements Model {

	protected ModelActivity activity;
	protected List<Map<String, String>> dataset;

	public BaseModel() {
	}

	public BaseModel(ModelActivity activity) {
		this.activity = activity;
	}

	public ModelActivity getActivity() {
		return activity;
	}

	public void setActivity(ModelActivity activity) {
		this.activity = activity;
	}

	@Override
	public List<Map<String, String>> getResult() {
		return dataset;
	}

	@Override
	public void load(LoadType loadType, Object param) {

	}
}
