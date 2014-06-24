package com.hand.hrms4android.activity;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.hand.hrms4android.R;
import com.hand.hrms4android.persistence.DataBaseMetadata.TodoList;

public class ApproveOpinionActivity extends ActionBarActivity {
	public static final String EXTRA_TITLE = "title";

	private Animation shake;
	private EditText opinionEditText;
	private SharedPreferences mPreferences;

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
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String default_approve_option = mPreferences.getString("default_approve_option", "");
		opinionEditText = (EditText) findViewById(R.id.approve_opinion_comments);
		opinionEditText.setText(default_approve_option);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		shake = AnimationUtils.loadAnimation(this, R.anim.shake);
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


			if (StringUtils.isEmpty(comments)) {
				opinionEditText.requestFocus();
				opinionEditText.startAnimation(shake);
				return true;
			}

			Intent i = new Intent(getIntent());
			i.putExtra(TodoList.COMMENTS, comments);
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
