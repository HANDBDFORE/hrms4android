package com.hand.hrms4android.model;

import java.util.List;
import java.util.Map;

public interface Model {
	void load(int type);

	void load(int type, Object param);

	List<Map<String, String>> getResult();
}
