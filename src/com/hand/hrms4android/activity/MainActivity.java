package com.hand.hrms4android.activity;

import com.hand.hrms4android.R;
import com.hand.hrms4android.R.id;
import com.hand.hrms4android.R.layout;
import com.hand.hrms4android.persistence.DataManage;
import com.hand.hrms4android.persistence.DatabaseManager;
import com.hand.hrms4android.persistence.QueryCallback;

import android.os.Bundle;
import android.widget.TextView;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;

public class MainActivity extends Activity {

	private TextView textView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		bindAllViews();

		DataManage manager = DatabaseManager.getInstance();

		ContentValues values = new ContentValues();
		values.put("todo_column_key", "hello");
		values.put("todo_column_value_id", 1);
		long newid = manager.insert("todo_column", null, values);
		System.out.println("newid is: " + newid);

		QueryCallback callback = new QueryCallback() {
			@Override
			public void onQuerySuccess(Cursor cursor) {
				textView.setText(String.valueOf(cursor.getCount()));
			}
		};

		manager.query("select * from  todo_column ", null, callback);
	}

	private void bindAllViews() {
		textView = (TextView) findViewById(R.id.main_text);
	}
}
