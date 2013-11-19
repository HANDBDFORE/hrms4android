package com.hand.hrms4android.core;

import com.android.volley.RequestQueue;
import com.hand.hrms4android.network.RequestManager;
import com.hand.hrms4android.util.LogUtil;

public abstract class HDAbstractModel implements Model {
	private int modelId;
	protected ModelViewController controller;

	protected boolean loading;
	protected RequestQueue requestQueue;
	
	protected final Object requestTag = this.getClass();

	public HDAbstractModel(int modelId, ModelViewController controller) {
		this.modelId = modelId;
		this.controller = controller;
		this.requestQueue = RequestManager.getRequestQueue();

		initStatus();
	}

	private void initStatus() {
		loading = false;
	}

	@Override
	public void load(LoadType loadType, Object param) {
		if (isLoading()) {
			cancelLoad(true);
		}

		onLoadingStart();
	}

	@Override
	public int getModelId() {
		return modelId;
	}

	@Override
	public boolean isLoading() {
		return loading;
	}

	@Override
	public void cancelLoad(Object tag) {
		requestQueue.cancelAll(tag);
	}

	@Override
	public void onLoadingStart() {
		loading = true;
	}

	@Override
	public void onLoadingEnd() {
		loading = false;
	}

	protected void handleError(Throwable e) {
		controller.modelFailedLoad(new RuntimeException(e), this);
		LogUtil.error(this, requestTag.getClass().getName(), e.getMessage());
	}

}
