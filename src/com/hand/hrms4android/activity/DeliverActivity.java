package com.hand.hrms4android.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.hand.hrms4android.R;
import com.hand.hrms4android.model.DeliverModel;
import com.hand.hrms4android.model.Model;
import com.hand.hrms4android.model.Model.LoadType;
import com.hand.hrms4android.persistence.DataBaseMetadata.TodoList;
import com.hand.hrms4android.util.PlatformUtil;

public class DeliverActivity extends ActionBarActivity {
	private static final int[] adapter_to = { android.R.id.text1, android.R.id.text2 };

	private static final String[] adapter_from_place_holder;

	static {
		adapter_from_place_holder = new String[] { "name", "description" };
	}

	private Animation shake;

	private AutoCompleteTextView deliverTo;
	private EditText comment;
	private SimpleAdapter autoCompleteAdapter;
	private String sourceSystemName;

	private Map<String, String> selectedItemData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deliver);

		sourceSystemName = getIntent().getStringExtra("sourceSystemName");

		bindAllViews();
		buildResources();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onResume() {
		deliverTo.requestFocus();
		super.onResume();
	}

	private void bindAllViews() {
		deliverTo = (AutoCompleteTextView) findViewById(R.id.activity_deliver_username);
		deliverTo.setThreshold(1);
		comment = (EditText) findViewById(R.id.activity_deliver_comment);

		deliverTo.addTextChangedListener(new TextChangeListener());
		deliverTo.setOnItemClickListener(new AutoCompleteListItemClickListener());

		if (PlatformUtil.getAndroidSDKVersion() < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			deliverTo.setTextColor(getResources().getColor(android.R.color.black));
		}
	}

	private void buildResources() {
		shake = AnimationUtils.loadAnimation(this, R.anim.shake);
		model = new DeliverModel(0, this);
		autoCompleteAdapter = new SimpleAdapter(this, new ArrayList<Map<String, String>>(),
		        android.R.layout.simple_list_item_2, adapter_from_place_holder, adapter_to);

		deliverTo.setAdapter(autoCompleteAdapter);
	}

	@Override
	public void modelDidFinishedLoad(Model model) {

		autoCompleteAdapter = new SimpleAdapter(this, (List<Map<String, String>>) model.getProcessData(),
		        android.R.layout.simple_list_item_2, adapter_from_place_holder, adapter_to);

		deliverTo.setAdapter(autoCompleteAdapter);
		autoCompleteAdapter.notifyDataSetChanged();
	}

	private class TextChangeListener implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			DeliverActivity.super.invalidateOptionsMenu();
			model.load(LoadType.Network, new String[]{sourceSystemName,s.toString()});
		}
	}

	private class AutoCompleteListItemClickListener implements OnItemClickListener {

		@SuppressWarnings("unchecked")
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (autoCompleteAdapter.getItem(position) instanceof Map<?, ?>) {
				selectedItemData = (Map<String, String>) autoCompleteAdapter.getItem(position);
				deliverTo.setSelection(position);
				deliverTo.setText(selectedItemData.get(adapter_from_place_holder[0]));
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, R.id.approve_opinion_ok, 0, "送出").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(0, R.id.approve_opinion_cancel, 1, "取消").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.approve_opinion_ok);

		String input = deliverTo.getText().toString();
		if (selectedItemData != null && (input.equals(selectedItemData.get(adapter_from_place_holder[0])))) {
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
			String employeeId = selectedItemData.get("realEmployeeId");

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
