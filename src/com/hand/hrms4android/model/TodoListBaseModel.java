package com.hand.hrms4android.model;

import java.util.Map;

import com.hand.hrms4android.activity.ModelActivity;

public class TodoListBaseModel extends AbstractPageableQueryModel<Map<String, String>> {

	public TodoListBaseModel(int id, ModelActivity activity) {
		super(id, activity);
	}

	@Override
	public void load(com.hand.hrms4android.model.Model.LoadType loadType, Object param) {

	}

}
