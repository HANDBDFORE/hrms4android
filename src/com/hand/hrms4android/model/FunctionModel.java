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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.hand.hrms4android.R;
import com.hand.hrms4android.core.HDAbstractModel;
import com.hand.hrms4android.core.ModelViewController;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.listable.item.FunctionItem;
import com.hand.hrms4android.listable.item.FunctionSection;
import com.hand.hrms4android.network.HDJsonObjectRequest;
import com.hand.hrms4android.network.HDRequest;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.hand.hrms4android.util.LogUtil;

public class FunctionModel extends HDAbstractModel {

	private List<Object> items;
	private ConfigReader configReader;

	public FunctionModel(int id, ModelViewController activity) {
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

//			NetworkUtil.post(queryUrl, null, new UMJsonHttpResponseHandler() {
//
//				@Override
//				public void onSuccess(int statusCode, JSONObject response) {
//					items = buildFixedItems(items);
//
//					JSONArray sections = null;
//					try {
//						sections = response.getJSONObject("body").getJSONArray("list");
//						items.addAll(buildItems(sections));
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//
//					activity.modelDidFinishedLoad(FunctionModel.this);
//				}
//			});
			
			requestQueue.add(genRequest(this, param, queryUrl));
		} catch (ParseExpressionException e1) {
			handleError(e1);
		}
	}
	
	private Request<JSONObject> genRequest(Object tag, Object param, String url) {
		Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				onLoadingEnd();
				items = buildFixedItems(items);

				JSONArray sections = null;
				try {
					sections = response.getJSONObject("body").getJSONArray("list");
					items.addAll(buildItems(sections));
				} catch (JSONException e) {
					e.printStackTrace();
				}

				controller.modelDidFinishedLoad(FunctionModel.this);
			}
		};
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				onLoadingEnd();
				handleError(error);
			}
		};

		HDRequest<JSONObject> request = new HDJsonObjectRequest(controller.getContext(), Method.POST,
		        NetworkUtil.getAbsoluteUrl(url), param, listener, errorListener);

		request.setTag(tag);

		return request;
	}

	private List<Object> buildFixedItems(List<Object> item) {
		if (item == null) {
			item = new ArrayList<Object>();
		}

		item.clear();

		item.add(new FunctionSection("工作流"));
		item.add(todoItem);
		item.add(doneItem);
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
