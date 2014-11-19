package com.hand.hrms4android.model;

import static com.hand.hrms4android.listable.item.FunctionItem.*;
import static com.hand.hrms4android.listable.item.FunctionItem.OTHER_ITEM_ID;
import static com.hand.hrms4android.listable.item.FunctionItem.TODO_ITEM_ID;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hand.hrms4android.R;
import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.listable.item.FunctionItem;
import com.hand.hrms4android.listable.item.FunctionSection;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.loopj.android.http.UMJsonHttpResponseHandler;

public class FunctionModel extends AbstractListModel<Object> {

	private List<Object> items;
	private ConfigReader configReader;

	public FunctionModel(int id, ModelActivity activity) {
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

					activity.modelDidFinishedLoad(FunctionModel.this);
				}
			});
		} catch (ParseExpressionException e1) {
			e1.printStackTrace();
			activity.modelFailedLoad(e1, FunctionModel.this);
		}
	}

	private List<Object> buildFixedItems(List<Object> item) {
		if (item == null) {
			item = new ArrayList<Object>();
		}

		item.clear();

		

		// ++++++++++++++++++是否显示工作流标题
		boolean display = true;
		try {
			display = Boolean.parseBoolean(configReader.getAttr(new Expression(
			        "/config/application/activity[@name='function_list_activity']/view/workflow_item_section",
			        "display")));
		} catch (ParseExpressionException e) {
			e.printStackTrace();
			// 如果没有找到这一项，说明是老版本，应该显示
			display = true;
		}
		
		if (display) {
			item.add(new FunctionSection(HrmsApplication.getApplication().getResources().getString(R.string.workflow)));
		}
		// ++++++++++++++++++

		// ++++++++++++++++++是否显示待办事项
		display = true;
		try {
			display = Boolean.parseBoolean(configReader.getAttr(new Expression(
			        "/config/application/activity[@name='function_list_activity']/view/todo_item", "display")));
		} catch (ParseExpressionException e) {
			e.printStackTrace();
			// 如果没有找到这一项，说明是老版本，应该显示
			display = true;
		}

		if (display) {
			item.add(todoItem);
		}

		// ++++++++++++++++++是否显示完成事项
		display = true;
		try {
			display = Boolean.parseBoolean(configReader.getAttr(new Expression(
			        "/config/application/activity[@name='function_list_activity']/view/done_item", "display")));
		} catch (ParseExpressionException e) {
			e.printStackTrace();
			// 如果没有找到这一项，说明是老版本，应该显示
			display = true;
		}
		if (display) {
			item.add(doneItem);
		}
		
		//+++++++++++++++++++++++++显示下载管理
		item.add(new FunctionSection(HrmsApplication.getApplication().getResources().getString(R.string.systemsetting)));
		item.add(downItem);
		
		return item;
	}

	private List<Object> buildItems(JSONArray sectionArray) throws JSONException {

		List<Object> listItems = new LinkedList<Object>();
		for (int i = 0; i < sectionArray.length(); i++) {
			JSONObject sectionJson = sectionArray.getJSONObject(i);

			items.add(new FunctionSection(sectionJson.getString("title")));

			JSONArray items = sectionJson.getJSONArray("items");
			for (int j = 0; j < items.length(); j++) {
				JSONObject itemJson = items.getJSONObject(j);
				listItems.add(new FunctionItem(OTHER_ITEM_ID, itemJson.getString("title"), itemJson
				        .getString("image_url"), itemJson.getString("url")));
			}
		}
		return listItems;
	}

	@Override
	public List<Object> getProcessData() {
		return items;
	}
}
