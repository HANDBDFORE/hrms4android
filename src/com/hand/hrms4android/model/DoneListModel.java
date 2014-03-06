package com.hand.hrms4android.model;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.ArrayAdapter;

import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.listable.doman.Page;
import com.hand.hrms4android.listable.item.DoneListItem;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.UMJsonHttpResponseHandler;

public class DoneListModel extends AbstractPageableModel<DoneListItem> {
	private String loadURL;
	private final ArrayAdapter<DoneListItem> adapter;
	private final Page page;

	public DoneListModel(ModelActivity doneListActivity, int id, ArrayAdapter<DoneListItem> adapter) {
		super(id, doneListActivity);
		this.adapter = adapter;
		page = new Page(1, 10);
	}

	@Override
	public void load(LoadType loadType, Object param) {

		if (loadType == LoadType.Network) {
			page.pageNumber = 1;
		} else {
			page.pageNumber++;
		}

		RequestParams params = new RequestParams();
		params.put("pagenum", String.valueOf(page.pageNumber));
		params.put("pagesize", String.valueOf(page.size));

		try {
			NetworkUtil.post(getLoadUrl(), params, new UMJsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, JSONObject response) {

					if (page.pageNumber == 1) {
						adapter.clear();
					}

					JSONArray array;
					try {
						array = response.getJSONObject("body").getJSONArray("list");
						for (int i = 0; i < array.length(); i++) {
							JSONObject o = array.getJSONObject(i);
							DoneListItem doneListItem = new DoneListItem(o.getString("item1"), o.getString("item2"), o
							        .getString("item3"), o.getString("item4"), o.getString("screenName"));
							adapter.add(doneListItem);
						}

						activity.modelDidFinishedLoad(DoneListModel.this);
					} catch (JSONException e) {
						e.printStackTrace();
						activity.modelFailedLoad(e, DoneListModel.this);
					}

				}

				@Override
				public void onFailure(Throwable error, String content) {
					super.onFailure(error, content);
					activity.modelFailedLoad(new RuntimeException(error), DoneListModel.this);
				}
			});
		} catch (ParseExpressionException e) {
			activity.modelFailedLoad(e, DoneListModel.this);
			e.printStackTrace();
		}

	}

	/**
     * 
     */
	private String getLoadUrl() throws ParseExpressionException {
		if (!StringUtils.isEmpty(loadURL)) {
			return loadURL;
		}

		loadURL = XmlConfigReader
		        .getInstance()
		        .getAttr(
		                new Expression(
		                        "/config/application/activity[@name='done_list_activity']/request/url[@name='done_list_query_url']",
		                        "value"));

		return loadURL;
	}

	@Override
	protected DoneListItem getItemAtIndex(int index) {
		return adapter.getItem(index);
	}

	@Override
	protected int getSize() {
		return adapter.getCount();
	}

}
