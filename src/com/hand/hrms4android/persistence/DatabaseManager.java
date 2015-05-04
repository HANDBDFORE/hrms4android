package com.hand.hrms4android.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.persistence.DataBaseMetadata.TodoList;

public class DatabaseManager extends SQLiteOpenHelper implements DataManage {

	/**
	 * 数据库文件名称
	 */
	public static final String DB_NAME = "hand_db";

	/**
	 * 数据库版本
	 */
	public static final int DB_VERSION = 2;

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
		db.execSQL("CREATE TABLE  " + TodoList.TABLENAME + "  ( " + TodoList.ID + " INTEGER PRIMARY KEY, "
		        + TodoList.STATUS + "  TEXT , " + TodoList.SERVER_MESSAGE + "  TEXT , " + TodoList.ACTION + "  TEXT , "
		        + TodoList.ACTION_TYPE + " TEXT, " + TodoList.COMMENTS + "  TEXT , " + TodoList.DELIVEREE + " TEXT, "
		        + TodoList.LOCALID + "  TEXT , " + TodoList.ITEM1 + "  TEXT , " + TodoList.ITEM2 + "  TEXT , "
		        + TodoList.ITEM3 + "  TEXT , " + TodoList.ITEM4 + "  TEXT , " + TodoList.SCREENNAME + " TEXT , "
		        + TodoList.SOURCE_SYSTEM_NAME + " TEXT ,"
		        + TodoList.VERIFICATIONID + "  INTEGER ,"
		        + TodoList.SIGNATURE + " TEXT)");

		// 审批动作表
		db.execSQL("CREATE TABLE  " + DataBaseMetadata.TableActions.TABLENAME + "  ( "
		        + DataBaseMetadata.TableActions.COLUMN_ID + " INTEGER PRIMARY KEY, "
		        + DataBaseMetadata.TableActions.COLUMN_TODO_LIST_ID + "  TEXT , "
		        + DataBaseMetadata.TableActions.COLUMN_ACTION_ID + " TEXT ,"
		        + DataBaseMetadata.TableActions.COLUMN_ACTION_TYPE + " TEXT ,"
		        + DataBaseMetadata.TableActions.COLUMN_ACTION_TITLE + " TEXT )");
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
