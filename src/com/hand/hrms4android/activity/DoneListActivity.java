package com.hand.hrms4android.activity;

import java.util.List;
import java.util.Map;

import com.hand.hrms4android.R;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.listable.adapter.DoneListAdapter;
import com.hand.hrms4android.model.DoneListModel;
import com.hand.hrms4android.model.Model;
import com.hand.hrms4android.model.Model.LoadType;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

public class DoneListActivity extends ActionBarActivity {
	private ListView doneList;
	private ConfigReader configReader;
	private String loadURL;
	private DoneListAdapter listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_done_list);

		doneList = (ListView) findViewById(android.R.id.list);

		this.model = new DoneListModel(this, 0);
		listAdapter = getAdapter();
		doneList.setAdapter(listAdapter);

		this.configReader = XmlConfigReader.getInstance();

		try {
			loadURL = configReader
			        .getAttr(new Expression(
			                "/config/application/activity[@name='done_list_activity']/request/url[@name='done_list_query_url']",
			                "value"));
		} catch (ParseExpressionException e) {
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			loadURL = "";
		}

		model.load(LoadType.Network, loadURL);
	}

	@Override
	public void modelDidFinishedLoad(Model model) {

		listAdapter.reFetchData();
	}

	private DoneListAdapter getAdapter() {
		if (listAdapter == null) {
			listAdapter = new DoneListAdapter(this, model);
		}
		return listAdapter;
	}

}
