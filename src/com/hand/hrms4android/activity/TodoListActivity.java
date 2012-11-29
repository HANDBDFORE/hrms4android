package com.hand.hrms4android.activity;

import java.util.List;
import java.util.Map;

import com.hand.hrms4android.R;
import com.hand.hrms4android.listable.adapter.TodoListAdapter;
import com.hand.hrms4android.model.Model;
import com.hand.hrms4android.model.TodoListModel;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TodoListActivity extends BaseActivity {

	private ListView todoListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list);

		bindAllViews();

		setModel(new TodoListModel(this));
	}

	private void bindAllViews() {
		todoListView = (ListView) findViewById(android.R.id.list);
	}

	@Override
	public void modelDidFinishedLoad(Model model) {
		super.modelDidFinishedLoad(model);
		List<Map<String, String>> dataset = model.getResult();

		ArrayAdapter<Map<String, String>> listAdapter = new TodoListAdapter(this, R.layout.todo_list_cell, dataset);
		todoListView.setAdapter(listAdapter);
//		todoListView.invalidate();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_todo_list, menu);
		return true;
	}

}
