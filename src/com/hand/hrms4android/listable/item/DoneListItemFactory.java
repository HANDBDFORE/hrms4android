package com.hand.hrms4android.listable.item;

import java.util.ArrayList;
import java.util.List;

import com.hand.hrms4android.listable.doman.TodoListDomain;

public class DoneListItemFactory implements ItemFactory<DoneListItem> {

	@Override
	public DoneListItem getItem(TodoListDomain record) {
		DoneListItem item = new DoneListItem();

		item.setTitle(record.getItem1());
		item.setTitleRight(record.getItem2());
		item.setSubTitle(record.getItem3());
		item.setInformation(record.getItem4());
		// item.setScreenName(record.get("screen_name"));
		return item;
	}

	@Override
	public List<DoneListItem> getItemList(List<TodoListDomain> dataset) {
		List<DoneListItem> result = new ArrayList<DoneListItem>();

		for (TodoListDomain record : dataset) {
			result.add(getItem(record));
		}
		return result;
	}

}
