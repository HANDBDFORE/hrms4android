package com.hand.hrms4android.model;

import java.util.LinkedList;
import java.util.List;

import com.hand.hrms4android.activity.ModelActivity;

public abstract class AbstractPageableQueryModel<ListDataType> extends AbstractPageableModel<ListDataType> implements
        QueryFilter<ListDataType> {

	public AbstractPageableQueryModel(int id, ModelActivity activity) {
		super(id, activity);
	}

	@Override
	public void filtData(List<ListDataType> list, Query<ListDataType> matcher) {
		List<ListDataType> result = new LinkedList<ListDataType>();

		for (ListDataType listDataType : list) {
			if (matcher.isMatchCondition(listDataType)) {
				result.add(listDataType);
			}
		}

		list.clear();

		for (ListDataType listDataType : result) {
			list.add(listDataType);
		}

		result = null;
	}

}
