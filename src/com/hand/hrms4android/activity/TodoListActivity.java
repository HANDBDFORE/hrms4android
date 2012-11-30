package com.hand.hrms4android.activity;

import java.util.List;
import java.util.Map;

import com.hand.hrms4android.R;
import com.hand.hrms4android.listable.adapter.TodoListAdapter;
import com.hand.hrms4android.listable.item.ItemFactory;
import com.hand.hrms4android.listable.item.TodoListItem;
import com.hand.hrms4android.listable.item.TodoListItemFactory;
import com.hand.hrms4android.model.Model;
import com.hand.hrms4android.model.TodoListModel;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.xml.XmlConfigReader;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TodoListActivity extends BaseActivity {

	private ListView todoListView;
	
	private ItemFactory<TodoListItem> itemFactory ;
	private ConfigReader configReader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list);

		bindAllViews();

		setModel(new TodoListModel(this));
		configReader = XmlConfigReader.getInstance();
		itemFactory = new TodoListItemFactory(configReader);
	}

	private void bindAllViews() {
		todoListView = (ListView) findViewById(android.R.id.list);
	}

	@Override
	public void modelDidFinishedLoad(Model model) {
		super.modelDidFinishedLoad(model);
		//取结果
		List<Map<String, String>> dataset = model.getResult();
		
		//转换为ListItem
		List<TodoListItem> items =  itemFactory.getItemList(dataset);
		ArrayAdapter<TodoListItem> listAdapter = new TodoListAdapter(this, items);
		todoListView.setAdapter(listAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_todo_list, menu);
		return true;
	}

}
