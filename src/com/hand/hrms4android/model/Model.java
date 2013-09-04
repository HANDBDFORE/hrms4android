package com.hand.hrms4android.model;

public interface Model<E extends Object> {

	public enum LoadType {
		Local, Network, LocalAndNetwork,NetworkMore
	};

	void load(LoadType loadType, Object param);

	/**
	 * 得到处理结果
	 * 
	 * @return
	 */
	public E getProcessData();

	public int getModelId();
}
