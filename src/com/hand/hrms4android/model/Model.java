package com.hand.hrms4android.model;

import java.util.List;
import java.util.Map;

public interface Model {

	public enum LoadType {
		Local, Network, LocalAndNetwork
	};

	void load(LoadType loadType, Object param);

	/**
	 * 得到适合aurora的数据集
	 * 
	 * @return
	 */
	public List<Map<String, String>> getAuroraDataset();

	/**
	 * 得到处理结果
	 * 
	 * @return
	 */
	public Object getProcessResult();

	public int getModelId();
}
