package com.hand.hrms4android.activity;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.hand.hrms4android.R;
import com.hand.hrms4android.persistence.DataBaseMetadata;

import android.os.Bundle;
import android.content.Intent;
import android.widget.EditText;

public class ApproveOpinionActivity extends ActionBarActivity {
	public static final String EXTRA_TITLE = "title";

	private EditText opinionEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = getIntent();
		if (i != null) {
			String title = i.getStringExtra(EXTRA_TITLE);
			if (title != null && title.length() != 0) {
				setTitle("审批动作：" + title);
			}
		}

		setContentView(R.layout.activity_approve_opinion);

		opinionEditText = (EditText) findViewById(R.id.approve_opinion_comments);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, R.id.approve_opinion_ok, 0, "确定").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(0, R.id.approve_opinion_cancel, 1, "取消").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.approve_opinion_ok: {
			String comments = opinionEditText.getText().toString();

			Intent i = new Intent(getIntent());
			i.putExtra(DataBaseMetadata.TodoListLogical.COMMENTS, comments);
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
