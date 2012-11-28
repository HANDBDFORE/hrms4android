package com.hand.hrms4android.model;

import java.util.List;
import java.util.Map;

public interface Model {

	public enum LoadType {
		Local, Network, LocalAndNetwork
	};

	void load(LoadType loadType, Object param);

	List<Map<String, String>> getResult();
}
