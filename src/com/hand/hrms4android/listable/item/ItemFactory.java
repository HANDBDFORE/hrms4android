package com.hand.hrms4android.listable.item;

import java.util.List;
import java.util.Map;

public interface ItemFactory<E extends BaseListItem> {
	E getItem(Map<String, String> record);

	List<E> getItemList(List<Map<String, String>> dataset);
}
