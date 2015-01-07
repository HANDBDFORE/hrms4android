package com.hand.hrms4android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.hand.hrms4android.ems.R;
import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.model.DeliverModel;
import com.hand.hrms4android.model.Model;
import com.hand.hrms4android.model.Model.LoadType;
import com.hand.hrms4android.persistence.DataBaseMetadata.TodoList;
import com.hand.hrms4android.util.PlatformUtil;

public class DeliverActivity extends ActionBarActivity {
	private static final int[] adapter_to = { android.R.id.text1, android.R.id.text2 };

	private static final String[] adapter_from_place_holder;
	
	static final int RETURN_USERNAME = 1;
	
	static {
		adapter_from_place_holder = new String[] { "name", "description" };
	}

	private Animation shake;

	private TextView deliverTo;
	private EditText comment;
	private SimpleAdapter autoCompleteAdapter;
	private String sourceSystemName;
	private SharedPreferences mPreferences;
	
	private Map<String, String> selectedItemData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		HrmsApplication.getApplication().addActivity(this);
		
		setContentView(R.layout.activity_deliver);
		setTitle(getResources().getString(R.string.activity_diliver_action_prompt_to));
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String default_approve_option = mPreferences.getString("default_approve_option", "");
		comment = (EditText) findViewById(R.id.activity_deliver_comment);
		comment.setText(default_approve_option);
		
		selectedItemData = new HashMap<String, String>();
		
		deliverTo = (TextView) findViewById(R.id.activity_deliver_username);
		deliverTo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				if(true){
					Intent intent = new Intent(DeliverActivity.this, EmployeeListActivity.class);
					startActivityForResult(intent, RETURN_USERNAME);					
				}

			}
		});
		
		sourceSystemName = getIntent().getStringExtra("sourceSystemName");

		bindAllViews();
		buildResources();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		comment.requestFocus();

	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	switch (requestCode) {
		case RETURN_USERNAME:
			if(resultCode == RESULT_OK){

				selectedItemData.put("employeeName", data.getStringExtra("employeeName").toString());
				selectedItemData.put("employeeId", data.getStringExtra("employeeId").toString());
				
				this.deliverTo.setText(data.getStringExtra("employeeName").toString());
			}
			break;
		}
    	super.onActivityResult(requestCode, resultCode, data);
    }
	
	private void bindAllViews() {
		deliverTo = (TextView) findViewById(R.id.activity_deliver_username);
//		deliverTo.setThreshold(1);
		comment = (EditText) findViewById(R.id.activity_deliver_comment);

		deliverTo.addTextChangedListener(new TextChangeListener());
//		deliverTo.setOnItemClickListener(new AutoCompleteListItemClickListener());

		if (PlatformUtil.getAndroidSDKVersion() < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			deliverTo.setTextColor(getResources().getColor(android.R.color.black));
		}
	}

	private void buildResources() {
		shake = AnimationUtils.loadAnimation(this, R.anim.shake);
		model = new DeliverModel(0, this);
		autoCompleteAdapter = new SimpleAdapter(this, new ArrayList<Map<String, String>>(),
		        android.R.layout.simple_list_item_2, adapter_from_place_holder, adapter_to);

//		deliverTo.setAdapter(autoCompleteAdapter);
	}

	@Override
	public void modelDidFinishedLoad(Model model) {

		autoCompleteAdapter = new SimpleAdapter(this, (List<Map<String, String>>) model.getProcessData(),
		        android.R.layout.simple_list_item_2, adapter_from_place_holder, adapter_to);

//		deliverTo.setAdapter(autoCompleteAdapter);
		autoCompleteAdapter.notifyDataSetChanged();
	}

	private class TextChangeListener implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
//			Toast.makeText(DeliverActivity.this, "now", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void afterTextChanged(Editable s) {
			DeliverActivity.super.invalidateOptionsMenu();
//			if(!s.toString().isEmpty())
//				model.load(LoadType.Network, new String[]{sourceSystemName,s.toString()});
			
		}
	}

//	private class AutoCompleteListItemClickListener implements OnItemClickListener {
//
//		@SuppressWarnings("unchecked")
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//			if (autoCompleteAdapter.getItem(position) instanceof Map<?, ?>) {
//				selectedItemData = (Map<String, String>) autoCompleteAdapter.getItem(position);
//				deliverTo.setSelection(position);
//				deliverTo.setText(selectedItemData.get(adapter_from_place_holder[0]));
//			}
//		}
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, R.id.approve_opinion_ok, 0, getResources().getString(R.string.activity_diliver_send_out)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(0, R.id.approve_opinion_cancel, 1, getResources().getString(R.string.activity_diliver_cancel)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.approve_opinion_ok);

		String input = deliverTo.getText().toString();
		if (selectedItemData != null && (input.equals(selectedItemData.get("employeeName")))) {
			item.setEnabled(true);
		} else {
			item.setEnabled(false);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.approve_opinion_ok: {
			String comments = comment.getText().toString();
			String employeeId = selectedItemData.get("employeeId");

			// 检查
			if (StringUtils.isEmpty(comments)) {
				comment.requestFocus();
				comment.startAnimation(shake);
				return true;
			}

			Intent i = new Intent(getIntent());
			i.putExtra(TodoList.COMMENTS, comments);
			i.putExtra(TodoList.DELIVEREE, employeeId);
			setResult(RESULT_OK, i);
			finish();
			break;
		}

		case R.id.approve_opinion_cancel:
		case android.R.id.home: {
			setResult(RESULT_CANCELED);
			finish();
			break;
		}

		default:
			break;
		}

		return true;
	}

}
