package com.hand.hrms4android.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hand.hrms4android.exception.PersistanceException;
import com.hand.hrms4android.persistence.AdditionalInformation;
import com.hand.hrms4android.persistence.DataBaseMetadata;
import com.hand.hrms4android.persistence.DataManage;
import com.hand.hrms4android.persistence.DatabaseManager;
import com.hand.hrms4android.persistence.QueryCallback;
import com.hand.hrms4android.pojo.ApproveAction;

import android.content.ContentValues;
import android.database.Cursor;

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
	public List<ApproveAction> insertAllActions(List<Map<String, String>> datas, AdditionalInformation additional)
	        throws PersistanceException {

		List<ApproveAction> result = new ArrayList<ApproveAction>();

		if (datas == null || datas.size() == 0) {
			return result;
		}

		if (additional == null || additional.primaryKeyName == null || additional.primaryKeyName.length() == 0) {
			throw new PersistanceException("找不到主键");
		}

		boolean pkNotFound = true;
		Set<String> fields = datas.get(0).keySet();
		for (String field : fields) {
			if (field.equalsIgnoreCase(additional.primaryKeyName)) {
				pkNotFound = false;
				break;
			}
		}
		if (pkNotFound) {
			throw new PersistanceException("找不到主键");
		}

		for (Map<String, String> record : datas) {
			ContentValues cv = new ContentValues();

			ApproveAction action = new ApproveAction();
			action.recordLogicalPK = record.get(additional.primaryKeyName);
			action.actionId = record.get(DataBaseMetadata.TableActions.COLUMN_ACTION_ID);
			action.actionTitle = record.get(DataBaseMetadata.TableActions.COLUMN_ACTION_TITLE).trim();
			action.actionType = record.get(DataBaseMetadata.TableActions.COLUMN_ACTION_TYPE);

			cv.put(DataBaseMetadata.TableActions.COLUMN_TODO_COLUMN_LOGICAL_PK, action.recordLogicalPK);
			cv.put(DataBaseMetadata.TableActions.COLUMN_ACTION_ID, action.actionId);
			cv.put(DataBaseMetadata.TableActions.COLUMN_ACTION_TITLE, action.actionTitle);
			cv.put(DataBaseMetadata.TableActions.COLUMN_ACTION_TYPE, action.actionType);

			action.localId = (int) (dataManager.insert(DataBaseMetadata.TableActions.TABLENAME, null, cv));

			result.add(action);
		}
		return result;
	}

	public int getActionCountByRecordId(String recordLogicalPK) {
		// select count(*) from actions where local_action_id = ?
		String sql = "select count(*) from actions where "
		        + DataBaseMetadata.TableActions.COLUMN_TODO_COLUMN_LOGICAL_PK + " = ?";

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
		String sql = "SELECT local_action_id, record_key, action_id, action_type, action_title FROM \"actions\" where "
		        + DataBaseMetadata.TableActions.COLUMN_TODO_COLUMN_LOGICAL_PK + " = ?";

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
