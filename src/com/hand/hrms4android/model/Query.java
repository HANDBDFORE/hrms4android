package com.hand.hrms4android.model;

public interface Query<T> {
	boolean isMatchCondition(T t);
}
