package com.hand.hrms4android.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.hand.hrms4android.exception.PersistanceException;
import com.hand.hrms4android.listable.doman.TodoListDomain;
import com.hand.hrms4android.persistence.DataBaseMetadata.TodoList;
import com.hand.hrms4android.persistence.DataManage;
import com.hand.hrms4android.persistence.DatabaseManager;
import com.hand.hrms4android.persistence.QueryCallback;
import com.hand.hrms4android.util.Constrants;

public class TodoListDao {
	private DataManage dataManager;

	public TodoListDao() {
		dataManager = DatabaseManager.getInstance();
	}

	/**
	 * @param data
	 * @param dictionary
	 * @return 受影响行数
	 * @throws PersistanceException
	 */
	public int insertTodoListRowData(List<TodoListDomain> data) throws PersistanceException {
		int affectiveRows = 0;

		if ((data == null) || (data.size() == 0)) {
			return 0;
		}

		// 插行表
		for (TodoListDomain record : data) {
			record.setStatus(Constrants.APPROVE_RECORD_STATUS_NORMAL);
			ContentValues cv = new ContentValues();
			cv.put(TodoList.STATUS, Constrants.APPROVE_RECORD_STATUS_NORMAL);
			cv.put(TodoList.SERVER_MESSAGE, record.getServerMessage());
			cv.put(TodoList.ACTION, record.getAction());
			cv.put(TodoList.ACTION_TYPE, record.getActionType());
			cv.put(TodoList.COMMENTS, record.getComments());
			cv.put(TodoList.LOCALID, record.getLocalId());
			cv.put(TodoList.ITEM1, record.getItem1());
			cv.put(TodoList.ITEM2, record.getItem2());
			cv.put(TodoList.ITEM3, record.getItem3());
			cv.put(TodoList.ITEM4, record.getItem4());
			cv.put(TodoList.SCREENNAME, record.getScreenName());
			cv.put(TodoList.SOURCE_SYSTEM_NAME, record.getSourceSystemName());
			cv.put(TodoList.VERIFICATIONID, record.getVerificationId());
			record.setId(String.valueOf(dataManager.insert(TodoList.TABLENAME, null, cv)));
			affectiveRows += 1;
		}

		return affectiveRows;
	}

	/**
	 * 把本地数据标记为已在其他地方处理
	 * 
	 * @param dirtyRecordPKs
	 * @return
	 */
	public int markRecordAsBeingApproved(List<String> dirtyRecordPKs) {
		int affectiveRows = 0;
		for (String id : dirtyRecordPKs) {
			ContentValues cv = new ContentValues();
			cv.put(TodoList.STATUS, Constrants.APPROVE_RECORD_STATUS_DIFFERENT);
			cv.put(TodoList.SERVER_MESSAGE, "已在其他地方处理");
			affectiveRows += dataManager.update(TodoList.TABLENAME, cv, TodoList.ID + "=?", new String[] { id });
		}

		return affectiveRows;
	}

	public List<TodoListDomain> queryLocalLogicalId() {
		final List<TodoListDomain> datas = new ArrayList<TodoListDomain>();

		// 先查出数据字段
		QueryCallback getDictionaryCallback = new QueryCallback() {
			int index = 0;

			final int index_LOCALID = index++;

			final int index_SOURCE_SYSTEM_NAME = index++;

			@Override
			public void onQuerySuccess(Cursor cursor) {
				if (cursor.moveToFirst()) {
					do {
						TodoListDomain domain = new TodoListDomain();

						domain.setLocalId(cursor.getString(index_LOCALID));
						domain.setSourceSystemName(cursor.getString(index_SOURCE_SYSTEM_NAME));
						datas.add(domain);
					} while (cursor.moveToNext());

				}

			}
		};

		// 找出所有列
		String sql = "select " + TodoList.LOCALID + ", " + TodoList.SOURCE_SYSTEM_NAME + "  from " + TodoList.TABLENAME;
		dataManager.query(sql, null, getDictionaryCallback);
		return datas;
	}

	/**
	 * @return
	 */

	public List<TodoListDomain> getAllTodoRecords() {

		final List<TodoListDomain> datas = new ArrayList<TodoListDomain>();

		// 先查出数据字段
		QueryCallback getDictionaryCallback = new QueryCallback() {
			int index = 0;

			final int index_ID = index++;
			final int index_STATUS = index++;
			final int index_SERVER_MESSAGE = index++;
			final int index_ACTION = index++;
			final int index_ACTION_TYPE = index++;
			final int index_COMMENTS = index++;
			final int index_LOCALID = index++;
			final int index_ITEM1 = index++;
			final int index_ITEM2 = index++;
			final int index_ITEM3 = index++;
			final int index_ITEM4 = index++;
			final int index_screenName = index++;
			final int index_SOURCE_SYSTEM_NAME = index++;
			final int index_deliveree = index++;
			final int index_verificationId = index++;
			final int index_signature = index++;

			@Override
			public void onQuerySuccess(Cursor cursor) {
				if (cursor.moveToFirst()) {
					do {
//						int test = cursor.getInt(index_verificationId);
						
						datas.add(new TodoListDomain(cursor.getString(index_ID), cursor.getString(index_STATUS), cursor
						        .getString(index_SERVER_MESSAGE), cursor.getString(index_ACTION), cursor
						        .getString(index_ACTION_TYPE), cursor.getString(index_COMMENTS), cursor
						        .getString(index_LOCALID), cursor.getString(index_ITEM1),
						        cursor.getString(index_ITEM2), cursor.getString(index_ITEM3), cursor
						                .getString(index_ITEM4), cursor.getString(index_screenName), cursor
						                .getString(index_SOURCE_SYSTEM_NAME), cursor.getString(index_deliveree),
						                cursor.getInt(index_verificationId),
						                cursor.getString(index_signature)));
					} while (cursor.moveToNext());

				}

			}
		};

		// 找出所有列
		String sql = "select " + TodoList.ID + ", " + TodoList.STATUS + ", " + TodoList.SERVER_MESSAGE + ", "
		        + TodoList.ACTION + ", " + TodoList.ACTION_TYPE + ", " + TodoList.COMMENTS + ", " + TodoList.LOCALID
		        + ", " + TodoList.ITEM1 + ", " + TodoList.ITEM2 + ", " + TodoList.ITEM3 + ", " + TodoList.ITEM4 + ", "
		        + TodoList.SCREENNAME + "," + TodoList.SOURCE_SYSTEM_NAME + ", " + TodoList.DELIVEREE + ", " + TodoList.VERIFICATIONID + "," + TodoList.SIGNATURE  + " from "
		        + TodoList.TABLENAME;
		dataManager.query(sql, null, getDictionaryCallback);
		return datas;
	}

	/**
	 * @param physicalRecordId
	 * @param serverMessage
	 * @return
	 */
	public int updateApproveRecordAsError(String id, String serverMessage) {
		ContentValues cv = new ContentValues();
		cv.put(TodoList.STATUS, Constrants.APPROVE_RECORD_STATUS_ERROR);
		cv.put(TodoList.SERVER_MESSAGE, serverMessage);

		int affectiveRows = dataManager.update(TodoList.TABLENAME, cv, TodoList.ID + "= ?", new String[] { id });
		return affectiveRows;
	}
	
	/**
	 * 更新signature
	 * @param id
	 * @param signature
	 * @return
	 */
	public int updateSignatureRecord(String id, String signature){
		ContentValues cv = new ContentValues();
		cv.put(TodoList.SIGNATURE, signature);

		int affectiveRows = dataManager.update(TodoList.TABLENAME, cv, TodoList.ID + "= ?", new String[] { id });
		return affectiveRows;		
	}
	
	/**
	 * 保存审批动作
	 * 
	 * @param record
	 * @return
	 */
	public int saveApproveAction(TodoListDomain record) {
		ContentValues updateValues = new ContentValues();
		// 动作
		updateValues.put(TodoList.ACTION, record.getAction());
		updateValues.put(TodoList.ACTION_TYPE, record.getActionType());
		// 意见
		updateValues.put(TodoList.COMMENTS, record.getComments());
		// 转交id
		updateValues.put(TodoList.DELIVEREE, record.getDeliveree());
		// 状态
		updateValues.put(TodoList.STATUS, Constrants.APPROVE_RECORD_STATUS_WAITING);

		int affectiveRows = dataManager.update(TodoList.TABLENAME, updateValues, TodoList.ID + "=?",
		        new String[] { String.valueOf(record.getId()) });
		return affectiveRows;
	}

	/**
	 * 删除行
	 * 
	 * @param ids
	 *            物理id
	 * @return 成功的行数
	 */
	public int deleteRecord(String... ids) {
		int affectiveRows = 0;
		for (int i = 0; i < ids.length; i++) {
			affectiveRows += dataManager.delete(TodoList.TABLENAME, TodoList.ID + "=?", new String[] { ids[i] });
		}

		return affectiveRows;
	}

	/**
	 * 删除行
	 * 
	 * @param ids
	 *            物理id
	 * @return 成功的行数
	 */
	public int deleteRecordByLogicalID(String localId, String sourceSystemName) {
		return dataManager.delete(TodoList.TABLENAME, TodoList.LOCALID + "=? and " + TodoList.SOURCE_SYSTEM_NAME
		        + " =?", new String[] { localId, sourceSystemName });
	}

}
