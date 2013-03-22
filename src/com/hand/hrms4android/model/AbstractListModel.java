package com.hand.hrms4android.model;

import java.util.ArrayList;
import java.util.List;

import com.hand.hrms4android.activity.ModelActivity;

public abstract class AbstractListModel<ListDataType> extends AbstractBaseModel<List<ListDataType>> {
	protected List<ListDataType> loadAuroraDataset;

	public AbstractListModel(int id, ModelActivity activity) {
		super(id, activity);
		this.loadAuroraDataset = new ArrayList<ListDataType>();
	}

	@Override
	public List<ListDataType> getProcessData() {
		return loadAuroraDataset;
	}
}
