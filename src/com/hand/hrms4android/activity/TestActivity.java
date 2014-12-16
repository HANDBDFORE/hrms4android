package com.hand.hrms4android.activity;

import java.util.LinkedList;
import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.hand.hrms4android.ems.R;
import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.model.Model;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TestActivity extends ActionBarActivity {
	private static final String[] datas = new String[] { "Emerson", "Mona", "Edison" };
	private ListView nameListView;
	private SearchView searchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		HrmsApplication.getApplication().addActivity(this);
		
		setContentView(R.layout.activity_test);
		nameListView = (ListView) findViewById(android.R.id.list);
		nameListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datas));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		int index = 0;
		searchView = new SearchView(getSupportActionBar().getThemedContext());
		searchView.setQueryHint("Search for countries…");
		searchView.setOnQueryTextListener(new X());

		menu.add(0, R.id.todo_list_menu_search, index++, getResources().getString(R.string.activity_test_search)).setIcon(R.drawable.ic_search_inverse)
		        .setActionView(searchView)
		        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		return true;
	}

	@Override
	public void modelDidFinishedLoad(Model<? extends Object> model) {
		// TODO Auto-generated method stub
	}

	class X implements OnQueryTextListener {

		@Override
		public boolean onQueryTextSubmit(String query) {
			System.out.println(query);
			return true;
		}

		@Override
		public boolean onQueryTextChange(String newText) {
			System.out.println(newText);
			List<String> result = new LinkedList<String>();
			for (String string : datas) {
				if (string.toLowerCase().contains(newText.toLowerCase())) {
					result.add(string);
				}
			}
			nameListView.setAdapter(new ArrayAdapter<String>(TestActivity.this, android.R.layout.simple_list_item_1, result));
			return true;
		}

	}

}
