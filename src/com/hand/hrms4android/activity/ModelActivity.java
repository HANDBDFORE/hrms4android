package com.hand.hrms4android.activity;

import com.hand.hrms4android.model.Model;

public interface ModelActivity {

	/**
	 * model读取完成
	 * 
	 * @param <E>
	 * 
	 * @param <E>
	 * 
	 * @param model
	 */
	void modelDidFinishedLoad(Model<? extends Object> model);

	/**
	 * model读取失败
	 * 
	 * @param <E>
	 * 
	 * @param <E>
	 * 
	 * @param e
	 * @param model
	 */
	void modelFailedLoad(Exception e, Model<? extends Object> model);

	void setModel(Model<? extends Object> model);
}
