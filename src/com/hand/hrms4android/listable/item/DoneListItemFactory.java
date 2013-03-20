package com.hand.hrms4android.listable.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.util.PlaceHolderReplacer;

public class DoneListItemFactory implements ItemFactory<DoneListItem> {

	private String titlePlaceHolder;
	private String datePlaceHolder;
	private String subTitlePlaceHolder;
	private String informationPlaceHolder;

	public DoneListItemFactory(ConfigReader configReader) {
		try {
			titlePlaceHolder = configReader.getAttr(new Expression(
			        "/config/application/activity[@name='done_list_activity']/view/listview_cell/title_textview",
			        "text"));

			datePlaceHolder = configReader
			        .getAttr(new Expression(
			                "/config/application/activity[@name='done_list_activity']/view/listview_cell/date_textview",
			                "text"));

			subTitlePlaceHolder = configReader.getAttr(new Expression(
			        "/config/application/activity[@name='done_list_activity']/view/listview_cell/subtitle_textview",
			        "text"));

			informationPlaceHolder = configReader.getAttr(new Expression(
			        "/config/application/activity[@name='done_list_activity']/view/listview_cell/information_textview",
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
	public DoneListItem getItem(Map<String, String> record) {
		DoneListItem item = new DoneListItem();

		item.setTitle(PlaceHolderReplacer.replaceForValue(record, titlePlaceHolder));
		item.setTitleRight(PlaceHolderReplacer.replaceForValue(record, datePlaceHolder));
		item.setSubTitle(PlaceHolderReplacer.replaceForValue(record, subTitlePlaceHolder));
		item.setInformation((PlaceHolderReplacer.replaceForValue(record, informationPlaceHolder)));
		item.setScreenName(record.get("screen_name"));
		return item;
	}

	@Override
	public List<DoneListItem> getItemList(List<Map<String, String>> dataset) {
		List<DoneListItem> result = new ArrayList<DoneListItem>();

		for (Map<String, String> record : dataset) {
			result.add(getItem(record));
		}
		return result;
	}

}
