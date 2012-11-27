package com.hand.hrms4android.activity;

import com.hand.hrms4android.model.Model;

import android.app.Activity;

public class BaseActivity extends Activity implements ModelActivity {
	protected Model model;

	@Override
	public void modelDidFinishedLoad(Model model) {
	}

	@Override
	public void setModel(Model model) {
		this.model = model;
		model.load(Model.LoadType.Network);
	}

	@Override
	public void modelFailedLoad(Exception e, Model model) {
	}
}
