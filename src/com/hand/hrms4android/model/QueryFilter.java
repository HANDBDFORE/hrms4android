package com.hand.hrms4android.model;

public interface QueryFilter<Result, Criteria> {
	public Result filtData(Query<Criteria> query);
}
