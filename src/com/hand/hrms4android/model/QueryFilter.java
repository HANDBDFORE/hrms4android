package com.hand.hrms4android.model;

import java.util.List;

public interface QueryFilter<T> {
	public void filtData(List<T> list, Query<T> matcher);
}
