package com.hand.hrms4android.model;

import java.util.Map;

import com.hand.hrms4android.activity.ModelActivity;

public abstract class AbstractBasePageableQueryModel<E> extends AbstractBasePageableModel implements QueryFilterModel<E> {

	public AbstractBasePageableQueryModel(int id, ModelActivity activity) {
		super(id, activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public <T> E filtData(Query<T> query) {
		// TODO Auto-generated method stub
		return null;
	}

}
