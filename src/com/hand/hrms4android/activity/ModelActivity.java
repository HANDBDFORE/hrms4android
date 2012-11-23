package com.hand.hrms4android.activity;

import com.hand.hrms4android.model.Model;

public interface ModelActivity {

	void modelDidFinishedLoad(Model model);

	void modelFailedLoad(Exception e, Model model);

	void setModel(Model model);
}
