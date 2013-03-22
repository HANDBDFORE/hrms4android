package com.hand.hrms4android.activity;

import com.hand.hrms4android.model.AbstractListModel;
import com.hand.hrms4android.model.Model;

public class BaseListModelActivity implements ModelActivity {
	// protected AbstractListModel<E> listModel;

	@Override
	public void modelDidFinishedLoad(Model<? extends Object> model) {

	}

	@Override
	public void modelFailedLoad(Exception e, Model<? extends Object> model) {

	}

	@Override
	public void setModel(Model<? extends Object> model) {

	}

}
