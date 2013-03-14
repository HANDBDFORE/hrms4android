package com.hand.hrms4android.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hand.hrms4android.activity.ModelActivity;

public abstract class AbstractModel implements Model {

	protected ModelActivity activity;
	protected List<Map<String, String>> loadAuroraDataset;
	private final int modelID;

	public AbstractModel(int id) {
		this(id, null);
	}

	public AbstractModel(int id, ModelActivity activity) {
		modelID = id;
		this.activity = activity;
		loadAuroraDataset = new ArrayList<Map<String, String>>();
	}

	public ModelActivity getActivity() {
		return activity;
	}

	public void setActivity(ModelActivity activity) {
		this.activity = activity;
	}

	@Override
	public List<Map<String, String>> getAuroraDataset() {
		return loadAuroraDataset;
	}

	@Override
	public Object getProcessResult() {
		return null;
	}

	@Override
	public int getModelId() {
		return modelID;
	}
}
