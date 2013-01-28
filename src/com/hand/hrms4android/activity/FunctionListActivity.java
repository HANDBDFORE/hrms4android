package com.hand.hrms4android.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.hand.hrms4android.R;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.listable.adapter.FunctionListAdapter;
import com.hand.hrms4android.listable.item.FunctionListItem;
import com.hand.hrms4android.model.FunctionListModel;
import com.hand.hrms4android.model.Model;
import com.hand.hrms4android.model.Model.LoadType;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.hand.hrms4android.util.StorageUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FunctionListActivity extends ActionBarActivity implements OnItemClickListener {
	private Model functionListModel;
	private ListView mListView;
	private ConfigReader configReader;
	private FunctionListAdapter listAdapter;

	private List<FunctionListItem> fixItem;

	private static final String TODO_ITEM_ID = "todo";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_function_list);

		functionListModel = new FunctionListModel(0, this);
		mListView = (ListView) findViewById(android.R.id.list);
		configReader = XmlConfigReader.getInstance();
		buildFixedItems();
		listAdapter = new FunctionListAdapter(this, fixItem, mListView);
		mListView.setAdapter(listAdapter);
		mListView.setOnItemClickListener(this);

		String queryUrl = null;
		try {
			queryUrl = configReader
			        .getAttr(new Expression(
			                "/config/application/activity[@name='function_list_activity']/request/url[@name='function_query_url']",
			                "value"));
			functionListModel.load(LoadType.Network, queryUrl);
		} catch (ParseExpressionException e) {
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}

	}

	private void buildFixedItems() {
		fixItem = new ArrayList<FunctionListItem>();
		fixItem.add(new FunctionListItem("SECTION", "工作流", "", "", "fixed"));
		fixItem.add(new FunctionListItem("ITEM", "待办事项", "bundle://todo_unread.png", "", TODO_ITEM_ID));
	}

	@Override
	public void modelDidFinishedLoad(Model model) {
		List<FunctionListItem> items = new ArrayList<FunctionListItem>(fixItem);
		List<Map<String, String>> rawData = model.getAuroraDataset();

		int rawDataSize = rawData.size();
		for (int i = 0; i < rawDataSize; i++) {
			Map<String, String> record = rawData.get(i);
			items.add(new FunctionListItem(record));
		}

		listAdapter.setDatas(items);
		listAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> listview, View row, int position, long id) {
		FunctionListItem item = listAdapter.getItem(position);

		if (item.getParentId() != null && item.getParentId().equalsIgnoreCase(TODO_ITEM_ID)) {
			startActivity(new Intent(this, TodoListActivity.class));
			return;
		}

		Intent i = new Intent(this, HTMLActivity.class);
		i.putExtra("url", item.getUrl());
		i.putExtra("title", item.getText());
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		int index = 0;
		menu.add(0, R.id.todo_list_menu_logout, index++, "退出登录").setIcon(R.drawable.ic_exit)
		        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.todo_list_menu_logout: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setTitle("退出系统");
			builder.setMessage("确认退出系统吗？退出后所有本地保存的数据将被清空！");
			builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					StorageUtil.deleteDB();
					StorageUtil.removeSavedInfo();
					NetworkUtil.setCookieStore(null);
					finish();
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			});
			builder.show();

			break;
		}
		default:
			break;
		}

		return true;

	}
}
