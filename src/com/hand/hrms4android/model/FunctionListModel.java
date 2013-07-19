package com.hand.hrms4android.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hand.hrms4android.activity.FunctionListActivity;
import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.listable.item.FunctionListItem;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.loopj.android.http.HDJsonHttpResponseHandler;
import com.loopj.android.http.UMJsonHttpResponseHandler;

public class FunctionListModel extends AbstractListModel<FunctionListItem> {

	private List<FunctionListItem> items;
	private ConfigReader configReader;

	public FunctionListModel(int id, ModelActivity activity) {
		super(id, activity);
		configReader = XmlConfigReader.getInstance();
	}

	@Override
	public void load(LoadType loadType, Object param) {
		try {
			String queryUrl = configReader
			        .getAttr(new Expression(
			                "/config/application/activity[@name='function_list_activity']/request/url[@name='function_query_url']",
			                "value"));

			NetworkUtil.post(queryUrl, null, new UMJsonHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, JSONObject response) {
					items = buildFixedItems(items);

					JSONArray sections = null;
					try {
						sections = response.getJSONObject("body").getJSONArray("list");
						items.addAll(buildItems(sections));
					} catch (JSONException e) {
						e.printStackTrace();
					}

					activity.modelDidFinishedLoad(FunctionListModel.this);
				}
			});
		} catch (ParseExpressionException e1) {
			e1.printStackTrace();
			activity.modelFailedLoad(e1, FunctionListModel.this);
		}
	}

	private List<FunctionListItem> buildFixedItems(List<FunctionListItem> item) {
		if (item == null) {
			item = new ArrayList<FunctionListItem>();
		}

		item.clear();

		item.add(new FunctionListItem(FunctionListItem.FUNCTION_LIST_ITEM_TYPE_SECTION, "工作流", "", "", "fixed"));
		item.add(new FunctionListItem(FunctionListItem.FUNCTION_LIST_ITEM_TYPE_ITEM, "待办事项",
		        "bundle://todo_unread.png", "", FunctionListActivity.TODO_ITEM_ID));
		item.add(new FunctionListItem(FunctionListItem.FUNCTION_LIST_ITEM_TYPE_ITEM, "已审批", "bundle://todo_unread.png",
		        "", FunctionListActivity.DONE_ITEM_ID));
		return item;
	}

	private List<FunctionListItem> buildItems(JSONArray sectionArray) throws JSONException {
		List<FunctionListItem> listItems = new LinkedList<FunctionListItem>();
		for (int i = 0; i < sectionArray.length(); i++) {
			JSONObject sectionJson = sectionArray.getJSONObject(i);

			items.add(new FunctionListItem(FunctionListItem.FUNCTION_LIST_ITEM_TYPE_SECTION, sectionJson
			        .getString("title"), null, null, null));

			JSONArray items = sectionJson.getJSONArray("items");
			for (int j = 0; j < items.length(); j++) {
				JSONObject itemJson = items.getJSONObject(j);
				listItems.add(new FunctionListItem(FunctionListItem.FUNCTION_LIST_ITEM_TYPE_ITEM, itemJson
				        .getString("title"), itemJson.getString("image_url"), itemJson.getString("url"), null));
			}
		}
		return listItems;
	}

	@Override
	public List<FunctionListItem> getProcessData() {
		return items;
	}
}
