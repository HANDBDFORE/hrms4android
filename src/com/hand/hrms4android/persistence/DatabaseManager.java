package com.hand.hrms4android.persistence;

import com.hand.hrms4android.application.HrmsApplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper implements DataManage {
	/**
	 * 数据库文件名称
	 */
	public static final String DB_NAME = "db";

	/**
	 * 数据库版本
	 */
	public static final int DB_VERSION = 1;

	private static DatabaseManager manager;

	/**
	 * 获取实例
	 * 
	 * @return
	 */
	public static DatabaseManager getInstance() {
		if (manager == null) {
			Context context = HrmsApplication.getApplication();
			manager = new DatabaseManager(context, DB_NAME, null, DB_VERSION);
		}
		return manager;
	}

	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	private DatabaseManager(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 建代办列表的 列名表
		db.execSQL("CREATE TABLE  todo_column  ( todo_column_id INTEGER PRIMARY KEY, todo_column_key  TEXT NOT NULL, todo_column_value_id INTEGER NOT NULL)");

		// 建代办列表 值表
		db.execSQL("CREATE TABLE todo_value ( todo_value_id INTEGER PRIMARY KEY , todo_value_0 TEXT NOT NULL, todo_value_1 TEXT NOT NULL, todo_value_2 TEXT NOT NULL, todo_value_3 TEXT NOT NULL, todo_value_4 TEXT NOT NULL, todo_value_5 TEXT NOT NULL, todo_value_6 TEXT NOT NULL, todo_value_7 TEXT NOT NULL, todo_value_8 TEXT NOT NULL, todo_value_9 TEXT NOT NULL, todo_value_10 TEXT NOT NULL, todo_value_11 TEXT NOT NULL, todo_value_12 TEXT NOT NULL, todo_value_13 TEXT NOT NULL, todo_value_14 TEXT NOT NULL, todo_value_15 TEXT NOT NULL, todo_value_16 TEXT NOT NULL, todo_value_17 TEXT NOT NULL, todo_value_18 TEXT NOT NULL, todo_value_19 TEXT NOT NULL, todo_value_20 TEXT NOT NULL, todo_value_21 TEXT NOT NULL, todo_value_22 TEXT NOT NULL, todo_value_23 TEXT NOT NULL, todo_value_24 TEXT NOT NULL, todo_value_25 TEXT NOT NULL, todo_value_26 TEXT NOT NULL, todo_value_27 TEXT NOT NULL, todo_value_28 TEXT NOT NULL, todo_value_29 TEXT NOT NULL )");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 数据库版本更新，于迁移数据有关
	}

	@Override
	public void query(String sql, String[] selectionArgs, QueryCallback callback) {

		if (callback == null) {
			throw new IllegalArgumentException("Callback cannot be null!");
		}

		// 模板化过程，建立连接和查询
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, selectionArgs);

		// 调用回调
		callback.onQuerySuccess(cursor);

		// 模板化过程,关闭数据库
		cursor.close();
		db.close();
	}

	@Override
	public void execute(String sql, Object[] bindArgs) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(sql, bindArgs);
		db.close();
	}

	@Override
	public long insert(String table, String nullColumnHack, ContentValues values) {
		SQLiteDatabase db = this.getWritableDatabase();
		long rowid = db.insertOrThrow(table, nullColumnHack, values);
		db.close();
		return rowid;
	}

	@Override
	public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
		SQLiteDatabase db = this.getWritableDatabase();
		int affectedRows = db.update(table, values, whereClause, whereArgs);
		db.close();
		return affectedRows;
	}

	@Override
	public int delete(String table, String whereClause, String[] whereArgs) {
		SQLiteDatabase db = this.getWritableDatabase();
		int affectedRows = db.delete(table, whereClause, whereArgs);
		db.close();
		return affectedRows;
	}
}
