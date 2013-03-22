package com.hand.hrms4android.model;

import java.util.List;
import java.util.Map;

import com.hand.hrms4android.activity.ModelActivity;

public class TodoListBaseModel extends
        AbstractPageableQueryModel<Map<String, String>, String, List<Map<String, String>>> {

	public TodoListBaseModel(int id, ModelActivity activity) {
		super(id, activity);
	}

	@Override
	public List<Map<String, String>> filtData(Query<String> query) {
		return null;
	}

	@Override
	public void load(com.hand.hrms4android.model.Model.LoadType loadType, Object param) {

	}

}
