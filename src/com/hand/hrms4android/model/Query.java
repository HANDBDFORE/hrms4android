package com.hand.hrms4android.model;

import java.util.Map;

public interface Query<T> {
	boolean isMatchCondition(T target);

}

class StringQuery implements Query<Map<String, String>> {

	private String criteria;

	@Override
	public boolean isMatchCondition(Map<String, String> record) {
		if (record.get("xx").equals(criteria)) {
			return true;
		}
		return false;
	}
}
