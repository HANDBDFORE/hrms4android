package com.hand.hrms4android.listable.item;

import java.util.List;

import com.hand.hrms4android.listable.doman.TodoListDomain;

public interface ItemFactory<E extends BaseListItem> {
	E getItem(TodoListDomain record);

	List<E> getItemList(List<TodoListDomain> dataset);
}
