package com.hand.hrms4android.activity;

import com.hand.hrms4android.model.Model;

import android.app.Activity;
import android.util.Log;

public class BaseActivity extends Activity implements ModelActivity {
	private static final String TAG = "";
	protected Model model;

	@Override
	public void modelDidFinishedLoad(Model model) {
	}

	@Override
	public void setModel(Model model) {
		this.model = model;
		model.load(Model.LoadType.Network,null);
	}

	@Override
	public void modelFailedLoad(Exception e, Model model) {
		Log.e(TAG, e.getMessage());
	}
}
