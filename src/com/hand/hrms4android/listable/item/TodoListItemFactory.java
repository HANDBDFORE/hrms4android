package com.hand.hrms4android.listable.item;

import java.util.ArrayList;
import java.util.List;

import com.hand.hrms4android.listable.doman.TodoListDomain;

public class TodoListItemFactory implements ItemFactory<TodoListItem,TodoListDomain> {

	public TodoListItemFactory() {
	}

	@Override
	public TodoListItem getItem(TodoListDomain record) {
		TodoListItem item = new TodoListItem();
		
		item.setId(Integer.valueOf(record.getId()).intValue());

		item.setTitle(record.getItem1());
		item.setTitleRight(record.getItem2());
		item.setSubTitle(record.getItem3());
		item.setInformation(record.getItem4());
		item.setErrorMessage(record.getServerMessage());
		item.setStatus(record.getStatus());
		item.setVerificationId(record.getVerificationId());
		item.setLate(false);
		return item;
	}

	@Override
	public List<TodoListItem> getItemList(List<TodoListDomain> dataset) {
		List<TodoListItem> items = new ArrayList<TodoListItem>();

		for (int i = 0; i < dataset.size(); i++) {
			TodoListItem item = getItem(dataset.get(i));
			items.add(item);
		}

		return items;
	}

}
