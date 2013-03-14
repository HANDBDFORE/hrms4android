package com.hand.hrms4android.listable.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.persistence.DataBaseMetadata;
import com.hand.hrms4android.util.PlaceHolderReplacer;

public class TodoListItemFactory implements ItemFactory<TodoListItem> {

	private ConfigReader configReader;

	private String titlePlaceHolder;
	private String datePlaceHolder;
	private String subTitlePlaceHolder;
	private String informationPlaceHolder;

	private String isLateKey;
	private String isLateTrueValue;

	public TodoListItemFactory(ConfigReader reader) {
		this.configReader = reader;

		try {
			titlePlaceHolder = configReader.getAttr(new Expression(
			        "/config/application/activity[@name='todo_list_activity']/view/listview_cell/title_textview", "text"));

			datePlaceHolder = configReader.getAttr(new Expression(
			        "/config/application/activity[@name='todo_list_activity']/view/listview_cell/date_textview", "text"));

			subTitlePlaceHolder = configReader.getAttr(new Expression(
			        "/config/application/activity[@name='todo_list_activity']/view/listview_cell/subtitle_textview", "text"));

			informationPlaceHolder = configReader.getAttr(new Expression(
			        "/config/application/activity[@name='todo_list_activity']/view/listview_cell/information_textview", "text"));

			// 是否崔办
			isLateKey = configReader.getAttr(new Expression(
			        "/config/application/activity[@name='todo_list_activity']/request/url[@name='todo_list_query_url']/is_late",
			        "name"));
			isLateTrueValue = configReader.getAttr(new Expression(
			        "/config/application/activity[@name='todo_list_activity']/request/url[@name='todo_list_query_url']/is_late",
			        "true_value"));

		} catch (ParseExpressionException e) {
			titlePlaceHolder = "";
			datePlaceHolder = "";
			subTitlePlaceHolder = "";
			informationPlaceHolder = "";
			isLateKey = "";
			e.printStackTrace();
		}

	}

	@Override
	public TodoListItem getItem(Map<String, String> record) {
		TodoListItem item = new TodoListItem();

		String localID = record.get(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_PHYSICAL_PK);
		// 把本地存储的自动生成id传入
		if (!StringUtils.isEmpty(localID)) {
			item.setId(Integer.parseInt(localID));
		}

		item.setTitle(PlaceHolderReplacer.replaceForValue(record, titlePlaceHolder));
		item.setTitleRight(PlaceHolderReplacer.replaceForValue(record, datePlaceHolder));
		item.setSubTitle(PlaceHolderReplacer.replaceForValue(record, subTitlePlaceHolder));
		item.setInformation((PlaceHolderReplacer.replaceForValue(record, informationPlaceHolder)));
		item.setErrorMessage(record.get(DataBaseMetadata.TodoListLogical.SERVER_MESSAGE));
		item.setStatus(record.get(DataBaseMetadata.TodoListLogical.STATUS));

		String lateValue = record.get(isLateKey);
		if ((isLateKey.length() != 0) && (!StringUtils.isEmpty(lateValue))) {
			item.setLate(lateValue.equals(isLateTrueValue));
		} else {
			item.setLate(false);
		}

		return item;
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
