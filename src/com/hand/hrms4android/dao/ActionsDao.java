package com.hand.hrms4android.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.hand.hrms4android.exception.PersistanceException;
import com.hand.hrms4android.persistence.DataBaseMetadata;
import com.hand.hrms4android.persistence.DataBaseMetadata.TableActions;
import com.hand.hrms4android.persistence.DataManage;
import com.hand.hrms4android.persistence.DatabaseManager;
import com.hand.hrms4android.persistence.QueryCallback;
import com.hand.hrms4android.pojo.ApproveAction;

public class ActionsDao {

	private DataManage dataManager;

	public ActionsDao() {
		dataManager = DatabaseManager.getInstance();
	}

	/**
	 * 将数据插表
	 * 
	 * @param datas
	 * @param additional
	 * @return
	 * @throws PersistanceException
	 */
	public void insertAllActions(List<ApproveAction> datas) {

		if (datas == null || datas.size() == 0) {
			return;
		}

		for (ApproveAction record : datas) {
			ContentValues cv = new ContentValues();

			cv.put(DataBaseMetadata.TableActions.COLUMN_ACTION_ID, record.action);
			cv.put(DataBaseMetadata.TableActions.COLUMN_TODO_LIST_ID, record.todolistId);
			cv.put(DataBaseMetadata.TableActions.COLUMN_ACTION_TITLE, record.actionTitle);
			cv.put(DataBaseMetadata.TableActions.COLUMN_ACTION_TYPE, record.actionType);

			record.id = (int) (dataManager.insert(DataBaseMetadata.TableActions.TABLENAME, null, cv));
		}
	}

	public int getActionCountByRecordId(String recordLogicalPK) {
		String sql = "select count(*) from actions where " + DataBaseMetadata.TableActions.COLUMN_TODO_LIST_ID + " = ?";

		final int[] count = new int[1];

		QueryCallback callback = new QueryCallback() {

			@Override
			public void onQuerySuccess(Cursor cursor) {
				if (cursor.moveToFirst()) {
					count[0] = cursor.getInt(0);
				}
			}
		};

		dataManager.query(sql, new String[] { recordLogicalPK }, callback);
		return count[0];
	}

	public List<ApproveAction> getAllActionsByRecordId(String recordLogicalPK) {
		String sql = "SELECT " + TableActions.COLUMN_ID + ",  " + TableActions.COLUMN_TODO_LIST_ID + ",  "
		        + TableActions.COLUMN_ACTION_TYPE + ",  " + TableActions.COLUMN_ACTION_ID + ",  "
		        + TableActions.COLUMN_ACTION_TITLE + "  FROM " + TableActions.TABLENAME + " where "
		        + TableActions.COLUMN_TODO_LIST_ID + " = ?";

		final List<ApproveAction> result = new ArrayList<ApproveAction>();

		QueryCallback callback = new QueryCallback() {

			@Override
			public void onQuerySuccess(Cursor cursor) {
				if (cursor.moveToFirst()) {

					do {
						result.add(new ApproveAction(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor
						        .getString(3), cursor.getString(4)));
					} while (cursor.moveToNext());
				}
			}
		};

		dataManager.query(sql, new String[] { recordLogicalPK }, callback);
		return result;
	}

}
