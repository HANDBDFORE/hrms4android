package com.hand.hrms4android.listable.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.exception.ParseExpressionException;

public class TodoListItemFactory implements ItemFactory<TodoListItem> {

	private ConfigReader configReader;

	private String titlePlaceHolder;
	private String datePlaceHolder;
	private String subTitlePlaceHolder;
	private String informationPlaceHolder;

	public TodoListItemFactory(ConfigReader reader) {
		this.configReader = reader;

		try {
			titlePlaceHolder = configReader
			        .getAttr(new Expression(
			                "/config/activity[@name='todo_list_activity']/view/listview_cell/title_textview",
			                "text"));

			datePlaceHolder = configReader.getAttr(new Expression(
			        "/config/activity[@name='todo_list_activity']/view/listview_cell/date_textview",
			        "text"));

			subTitlePlaceHolder = configReader.getAttr(new Expression(
			        "/config/activity[@name='todo_list_activity']/view/listview_cell/subtitle_textview",
			        "text"));

			informationPlaceHolder = configReader.getAttr(new Expression(
			        "/config/activity[@name='todo_list_activity']/view/listview_cell/information_textview",
			        "text"));
		} catch (ParseExpressionException e) {
			titlePlaceHolder = "";
			datePlaceHolder = "";
			subTitlePlaceHolder = "";
			informationPlaceHolder = "";
			e.printStackTrace();
		}
	}

	@Override
	public TodoListItem getItem(Map<String, String> record) {
		TodoListItem item = new TodoListItem();

		item.setTitle(searchForValue(record, titlePlaceHolder));
		item.setDate(searchForValue(record, datePlaceHolder));
		item.setSubTitle(searchForValue(record, subTitlePlaceHolder));
		item.setInformation((searchForValue(record, informationPlaceHolder)));

		return item;
	}

	private String searchForValue(Map<String, String> record, String placeHolder) {
		Set<String> keys = record.keySet();

		for (String key : keys) {
			StringBuilder sb = new StringBuilder("${");
			sb.append(key);
			sb.append("}");
			String target = sb.toString();

			placeHolder = placeHolder.replace(target, record.get(key));
		}
		return placeHolder;
	}

	@Override
	public List<TodoListItem> getItemList(List<Map<String, String>> dataset) {
		List<TodoListItem> items = new ArrayList<TodoListItem>();

		for (int i = 0; i < dataset.size(); i++) {
			TodoListItem item = getItem(dataset.get(i));
			items.add(item);
		}

		return items;
	}

}
