package com.hand.hrms4android.model;

import com.hand.hrms4android.activity.ModelActivity;

public abstract class AbstractPageableQueryModel<ListDataType, Criteria, Result> extends
        AbstractPageableModel<ListDataType> implements QueryFilter<Result, Criteria> {

	public AbstractPageableQueryModel(int id, ModelActivity activity) {
		super(id, activity);
	}

	@Override
	public Result filtData(Query<Criteria> query) {
		throw new IllegalStateException("Must override before invoke");
	}

}
