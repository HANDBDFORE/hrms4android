package com.hand.hrms4android.activity;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.view.Window;
import com.hand.hrms4android.R;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.listable.adapter.DoneListAdapter;
import com.hand.hrms4android.listable.doman.TodoListDomain;
import com.hand.hrms4android.model.AbstractPageableModel;
import com.hand.hrms4android.model.DoneListModel;
import com.hand.hrms4android.model.Model;
import com.hand.hrms4android.model.Model.LoadType;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.hand.hrms4android.util.TempTransfer;
import com.hand.hrms4android.util.data.IndexPath;

public class DoneListActivity extends ActionBarActivity implements OnItemClickListener {
	private ListView doneList;
	private ConfigReader configReader;
	private String loadURL;
	private DoneListAdapter listAdapter;
	private AbstractPageableModel<TodoListDomain> doneModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_done_list);

		doneList = (ListView) findViewById(android.R.id.list);
		doneList.setOnItemClickListener(this);

		doneModel = new DoneListModel(this, 0);
		this.model = doneModel;
		listAdapter = getAdapter();
		doneList.setAdapter(listAdapter);

		this.configReader = XmlConfigReader.getInstance();

	}

	/**
     * 
     */
    private String getLoadUrl() {
    	if (!StringUtils.isEmpty(loadURL)) {
	        return loadURL;
        }
    	
    	
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
	    
	    return loadURL;
    }

	@Override
	protected void onResume() {
		super.onResume();
		model.load(LoadType.Network, getLoadUrl());
		setSupportProgressBarIndeterminateVisibility(true);
	}

	@Override
	public void modelDidFinishedLoad(Model model) {
		setSupportProgressBarIndeterminateVisibility(false);
		listAdapter.reFetchData();
	}

	@Override
	public void modelFailedLoad(Exception e, Model<? extends Object> model) {
		super.modelFailedLoad(e, model);
		setSupportProgressBarIndeterminateVisibility(false);
	}

	private DoneListAdapter getAdapter() {
		if (listAdapter == null) {
			listAdapter = new DoneListAdapter(this, doneModel);
		}
		return listAdapter;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TempTransfer.container.put(TempTransfer.KEY_TODO_LIST_MODEL, model);
		doneModel.setRecordAsSelected(new IndexPath(0, position));
		startActivity(new Intent(this, DoneReceiptActivity.class));
	}

}
