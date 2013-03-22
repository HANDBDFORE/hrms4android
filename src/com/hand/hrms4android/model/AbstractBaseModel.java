package com.hand.hrms4android.model;

import com.hand.hrms4android.activity.ModelActivity;

public abstract class AbstractBaseModel<E> implements Model<E> {
	protected ModelActivity activity;
	private final int modelID;

	public AbstractBaseModel(int id) {
		this(id, null);
	}

	public AbstractBaseModel(int id, ModelActivity activity) {
		modelID = id;
		this.activity = activity;
	}

	@Override
	public E getProcessData() {
		return null;
	}

	@Override
	public int getModelId() {
		return modelID;
	}

}
