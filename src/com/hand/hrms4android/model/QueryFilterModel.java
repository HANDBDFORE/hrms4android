package com.hand.hrms4android.model;

public interface QueryFilterModel<E> {
	public <T> E filtData(Query<T> query);
}
