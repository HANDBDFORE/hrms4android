package com.hand.hrms4android.activity;

import com.hand.hrms4android.model.Model;

public interface ModelActivity {

	/**
	 * model读取完成
	 * 
	 * @param model
	 */
	void modelDidFinishedLoad(Model model);

	/**
	 * model读取失败
	 * 
	 * @param e
	 * @param model
	 */
	void modelFailedLoad(Exception e, Model model);

	void setModel(Model model);
}
