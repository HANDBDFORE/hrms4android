package com.hand.hrms4android.model;

import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.util.Aggregate;
import com.hand.hrms4android.util.Iterator;

public abstract class AbstractPageableModel<E> extends AbstractModel implements Iterator<E>, Aggregate<E> {

	public AbstractPageableModel(int id, ModelActivity activity) {
		super(id, activity);
	}

}
