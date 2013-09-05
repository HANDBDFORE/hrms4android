package com.hand.hrms4android.core;

import android.content.Context;

public interface ModelViewController {

	/**
	 * model读取完成
	 * 
	 * @param <E>
	 * 
	 * @param <E>
	 * 
	 * @param <E>
	 * 
	 * @param model
	 */
	void modelDidFinishedLoad(Model model);

	/**
	 * model读取失败
	 * 
	 * @param <E>
	 * 
	 * @param <E>
	 * 
	 * @param <E>
	 * 
	 * @param e
	 * @param model
	 */
	void modelFailedLoad(Exception e, Model model);

	void setModel(Model model);

	/**
	 * 获取context
	 * 
	 * @return
	 */
	Context getContext();
}
