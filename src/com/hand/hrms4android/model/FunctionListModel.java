package com.hand.hrms4android.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hand.hrms4android.activity.FunctionListActivity;
import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.listable.item.FunctionListItem;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.util.Constrants;
import com.loopj.android.http.HDJsonHttpResponseHandler;

public class FunctionListModel extends AbstractListModel<FunctionListItem> {

	private List<FunctionListItem> items;

	public FunctionListModel(int id, ModelActivity activity) {
		super(id, activity);
	}

	@Override
	public void load(LoadType loadType, Object param) {
		String url = param.toString();

		NetworkUtil.post(url, null, new HDJsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, List<Map<String, String>> dataset) {
				// TODO 重构
				items = buildFixedItems(items);

				int rawDataSize = dataset.size();
				for (int i = 0; i < rawDataSize; i++) {
					Map<String, String> record = dataset.get(i);
					items.add(new FunctionListItem(record));
				}

				activity.modelDidFinishedLoad(FunctionListModel.this);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				activity.modelFailedLoad(new Exception(error.getMessage()), FunctionListModel.this);
			}
		});
	}

	private List<FunctionListItem> buildFixedItems(List<FunctionListItem> item) {
		if (item == null) {
			item = new ArrayList<FunctionListItem>();
		}

		item.clear();

		item.add(new FunctionListItem(Constrants.FUNCTION_LIST_ITEM_TYPE_SECTION, "工作流", "", "", "fixed"));
		item.add(new FunctionListItem(Constrants.FUNCTION_LIST_ITEM_TYPE_ITEM, "待办事项", "bundle://todo_unread.png", "",
		        FunctionListActivity.TODO_ITEM_ID));
		item.add(new FunctionListItem(Constrants.FUNCTION_LIST_ITEM_TYPE_ITEM, "已审批", "bundle://todo_unread.png", "",
		        FunctionListActivity.DONE_ITEM_ID));
		return item;
	}

	@Override
	public List<FunctionListItem> getProcessData() {
		return items;
	}
}
