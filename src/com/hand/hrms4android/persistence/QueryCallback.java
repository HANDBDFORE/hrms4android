package com.hand.hrms4android.persistence;

import android.database.Cursor;

/**
 * 查询成功的回调接口，用于在发起查询时传入。系统会将查询后的结果返回
 * 
 * @author emerson
 * 
 */
public interface QueryCallback {

	/**
	 * @param cursor
	 *            结果游标
	 */
	public void onQuerySuccess(Cursor cursor);
}
