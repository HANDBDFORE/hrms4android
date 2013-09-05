package com.hand.hrms4android.core;

public interface Model {

	public enum LoadType {
		Local, Network, LocalAndNetwork
	};

	public void load(LoadType loadType, Object param);

	/**
	 * 得到处理结果
	 * 
	 * @param <T>
	 * 
	 * @return
	 */
	public <T> T getProcessData();

	public int getModelId();

	public void cancelLoad(Object tag);

	public boolean isLoading();

	public void onLoadingStart();

	public void onLoadingEnd();

}
