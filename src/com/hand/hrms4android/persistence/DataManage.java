package com.hand.hrms4android.persistence;

import android.content.ContentValues;

/**
 * 规定了持久化操作的行为
 * 
 * @author emerson
 * 
 */
public interface DataManage {

	/**
	 * @param sql
	 * @param selectionArgs
	 * @param callback
	 */
	public void query(String sql, String[] selectionArgs, QueryCallback callback);

	/**
	 * 不鼓励用于query,insert,update,delete用途
	 * 
	 * @param sql
	 * @param bindArgs
	 *            参数值（若无需此项请传 new Object[]{}）
	 */
	public void execute(String sql, Object[] bindArgs);

	/**
	 * @param table
	 * @param nullColumnHack
	 * @param values
	 * @return
	 */
	public long insert(String table, String nullColumnHack, ContentValues values);

	public int update(String table, ContentValues values, String whereClause, String[] whereArgs);

	public int delete(String table, String whereClause, String[] whereArgs);

}
