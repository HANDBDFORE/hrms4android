package com.hand.hrms4android.activity;

import com.hand.hrms4android.model.Model;
import com.hand.hrms4android.util.LogUtil;

import android.app.Activity;
import android.widget.Toast;

public class BaseModelActivity extends Activity implements ModelActivity {
	private static final String TAG = "";
	protected Model<?> model;

	@Override
	public void modelDidFinishedLoad(Model<?> model) {

	}

	@Override
	public void modelFailedLoad(Exception e, Model<? extends Object> model) {
		Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		LogUtil.error(this, TAG, e.getMessage());
	}

	@Override
	public void setModel(Model<? extends Object> model) {
		this.model = model;
		model.load(Model.LoadType.Network, null);
	}

}
