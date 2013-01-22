package com.hand.hrms4android.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;

import com.hand.hrms4android.exception.PersistanceException;
import com.hand.hrms4android.persistence.AdditionalInformation;
import com.hand.hrms4android.persistence.DataBaseMetadata;
import com.hand.hrms4android.persistence.DataManage;
import com.hand.hrms4android.persistence.DatabaseManager;
import com.hand.hrms4android.persistence.QueryCallback;
import com.hand.hrms4android.util.Constrants;
import com.hand.hrms4android.util.LogUtil;

public class TodoListDao {
	private DataManage dataManager;

	public TodoListDao() {
		dataManager = DatabaseManager.getInstance();
	}

	/**
	 * @param data
	 * @param additional
	 * @return
	 * @throws PersistanceException
	 */
	public int insertData(List<Map<String, String>> data, AdditionalInformation additional) throws PersistanceException {
		int affectiveRows = 0;

		if ((data == null) || (data.size() == 0)) {
			return 0;
		}

		// 写数据库
		// 首先将所有的列名写入todo_column表

		// 取第一行，
		Map<String, String> firstRecord = data.get(0);

		// 获取所有列(副本)
		Set<String> recordFields = new HashSet<String>(firstRecord.keySet());
		List<String> toInsertFields = new ArrayList<String>();

		final String valueColumnPrefix = "todo_value_";

		// 头行对照字典
		Map<String, String> recordDictionary = new HashMap<String, String>();

		boolean PKnotFound = true;
		// 检测主键，为了将主键插入第一行
		for (String fieldName : recordFields) {
			// 检测到主键
			if (fieldName.equalsIgnoreCase(additional.primaryKeyName)) {
				// 加入字典
				recordDictionary.put(fieldName,
				        DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_LOGICAL_PK);

				recordFields.remove(fieldName);
				toInsertFields.add(fieldName);
				PKnotFound = false;
				break;
			}
		}

		// 如果没有按要求找到pk，抛错
		if (PKnotFound) {
			throw new PersistanceException("Primary key is not found");
		}

		// 把本地特征列加入返回数据
		// status，serverMessage,action,comments,employeeId
		// 本地状态
		recordDictionary.put(DataBaseMetadata.TodoListLogical.STATUS,
		        DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_STATUS);
		// 服务器返回信息
		recordDictionary.put(DataBaseMetadata.TodoListLogical.SERVER_MESSAGE,
		        DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_SERVERMESSAGE);
		// 动作
		recordDictionary.put(DataBaseMetadata.TodoListLogical.ACTION,
		        DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_ACTION);
		// 审批语
		recordDictionary.put(DataBaseMetadata.TodoListLogical.COMMENTS,
		        DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_COMMENTS);
		// 员工id
		recordDictionary.put(DataBaseMetadata.TodoListLogical.EMPLOYEE_ID,
		        DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_EMPLOYEE_ID);

		//
		toInsertFields.add(DataBaseMetadata.TodoListLogical.STATUS);
		toInsertFields.add(DataBaseMetadata.TodoListLogical.SERVER_MESSAGE);
		toInsertFields.add(DataBaseMetadata.TodoListLogical.COMMENTS);
		toInsertFields.add(DataBaseMetadata.TodoListLogical.ACTION);
		toInsertFields.add(DataBaseMetadata.TodoListLogical.EMPLOYEE_ID);

		recordFields.remove(DataBaseMetadata.TodoListLogical.STATUS);
		recordFields.remove(DataBaseMetadata.TodoListLogical.SERVER_MESSAGE);
		recordFields.remove(DataBaseMetadata.TodoListLogical.ACTION);
		recordFields.remove(DataBaseMetadata.TodoListLogical.COMMENTS);
		recordFields.remove(DataBaseMetadata.TodoListLogical.EMPLOYEE_ID);

		int columnStart = 7;
		// 循环除主键和保留列之外的数据列，放入字典
		LogUtil.error(this, "recordFields", recordFields.toString());

		for (String fieldName : recordFields) {
			String columnValue = valueColumnPrefix + columnStart;
			recordDictionary.put(fieldName, columnValue);
			toInsertFields.add(fieldName);
			columnStart += 1;
		}

		for (String field : toInsertFields) {
			ContentValues cv = new ContentValues();
			cv.put(DataBaseMetadata.TableTodoListColumnMetadata.COLUMN_TODO_COLUMN_KEY, field);
			cv.put(DataBaseMetadata.TableTodoListColumnMetadata.COLUMN_TODO_COLUMN_VALUE_ID,
			        recordDictionary.get(field));
			dataManager.insert(DataBaseMetadata.TableTodoListColumnMetadata.TABLENAME, null, cv);
		}

		// 插行表
		for (Map<String, String> record : data) {
			record.put(DataBaseMetadata.TodoListLogical.STATUS, Constrants.APPROVE_RECORD_STATUS_NORMAL);
			ContentValues cv = new ContentValues();
			for (String fieldName : firstRecord.keySet()) {
				cv.put(recordDictionary.get(fieldName), record.get(fieldName));
			}
			dataManager.insert(DataBaseMetadata.TableTodoListValueMetadata.TABLENAME, null, cv);
			affectiveRows += 1;
		}

		return affectiveRows;
	}

	/**
	 * 插头表
	 * 
	 * @param data
	 * @param additional
	 * @return 数据字典
	 * @throws PersistanceException
	 */
	public Map<String, String> insertTodoListColumns(List<Map<String, String>> data, AdditionalInformation additional)
	        throws PersistanceException {
		if ((data == null) || (data.size() == 0)) {
			return new HashMap<String, String>();
		}

		// 写数据库
		// 首先将所有的列名写入todo_column表

		// 取第一行，
		Map<String, String> firstRecord = data.get(0);

		// 获取所有列(副本)
		Set<String> recordFields = new HashSet<String>(firstRecord.keySet());
		List<String> toInsertFields = new ArrayList<String>();

		final String valueColumnPrefix = "todo_value_";

		// 头行对照字典
		Map<String, String> recordDictionary = new HashMap<String, String>();

		boolean PKnotFound = true;
		// 检测主键，为了将主键插入第一行
		for (String fieldName : recordFields) {
			// 检测到主键
			if (fieldName.equalsIgnoreCase(additional.primaryKeyName)) {
				// 加入字典
				recordDictionary.put(fieldName,
				        DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_LOGICAL_PK);

				recordFields.remove(fieldName);
				toInsertFields.add(fieldName);
				PKnotFound = false;
				break;
			}
		}

		// 如果没有按要求找到pk，抛错
		if (PKnotFound) {
			throw new PersistanceException("Primary key is not found");
		}

		// 把本地特征列加入返回数据
		// status，serverMessage,action,comments,employeeId
		// 本地状态
		recordDictionary.put(DataBaseMetadata.TodoListLogical.STATUS,
		        DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_STATUS);
		// 服务器返回信息
		recordDictionary.put(DataBaseMetadata.TodoListLogical.SERVER_MESSAGE,
		        DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_SERVERMESSAGE);
		// 动作
		recordDictionary.put(DataBaseMetadata.TodoListLogical.ACTION,
		        DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_ACTION);
		// 审批语
		recordDictionary.put(DataBaseMetadata.TodoListLogical.COMMENTS,
		        DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_COMMENTS);
		// 员工id
		recordDictionary.put(DataBaseMetadata.TodoListLogical.EMPLOYEE_ID,
		        DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_EMPLOYEE_ID);

		//
		toInsertFields.add(DataBaseMetadata.TodoListLogical.STATUS);
		toInsertFields.add(DataBaseMetadata.TodoListLogical.SERVER_MESSAGE);
		toInsertFields.add(DataBaseMetadata.TodoListLogical.COMMENTS);
		toInsertFields.add(DataBaseMetadata.TodoListLogical.ACTION);
		toInsertFields.add(DataBaseMetadata.TodoListLogical.EMPLOYEE_ID);

		recordFields.remove(DataBaseMetadata.TodoListLogical.STATUS);
		recordFields.remove(DataBaseMetadata.TodoListLogical.SERVER_MESSAGE);
		recordFields.remove(DataBaseMetadata.TodoListLogical.ACTION);
		recordFields.remove(DataBaseMetadata.TodoListLogical.COMMENTS);
		recordFields.remove(DataBaseMetadata.TodoListLogical.EMPLOYEE_ID);

		int columnStart = 7;
		// 循环除主键和保留列之外的数据列，放入字典
		LogUtil.error(this, "recordFields", recordFields.toString());

		for (String fieldName : recordFields) {
			String columnValue = valueColumnPrefix + columnStart;
			recordDictionary.put(fieldName, columnValue);
			toInsertFields.add(fieldName);
			columnStart += 1;
		}

		for (String field : toInsertFields) {
			ContentValues cv = new ContentValues();
			cv.put(DataBaseMetadata.TableTodoListColumnMetadata.COLUMN_TODO_COLUMN_KEY, field);
			cv.put(DataBaseMetadata.TableTodoListColumnMetadata.COLUMN_TODO_COLUMN_VALUE_ID,
			        recordDictionary.get(field));
			dataManager.insert(DataBaseMetadata.TableTodoListColumnMetadata.TABLENAME, null, cv);
		}

		return recordDictionary;
	}

	/**
	 * @param data
	 * @param dictionary
	 * @return 受影响行数
	 * @throws PersistanceException
	 */
	public int insertTodoListRowData(List<Map<String, String>> data, Map<String, String> dictionary)
	        throws PersistanceException {
		int affectiveRows = 0;

		if ((data == null) || (data.size() == 0)) {
			return 0;
		}

		// 插行表
		for (Map<String, String> record : data) {
			record.put(DataBaseMetadata.TodoListLogical.STATUS, Constrants.APPROVE_RECORD_STATUS_NORMAL);
			ContentValues cv = new ContentValues();
			for (String fieldName : dictionary.keySet()) {
				cv.put(dictionary.get(fieldName), record.get(fieldName));
			}
			dataManager.insert(DataBaseMetadata.TableTodoListValueMetadata.TABLENAME, null, cv);
			affectiveRows += 1;
		}

		return affectiveRows;
	}

	/**
	 * 获得已经存储的对照字典
	 * 
	 * @return
	 * @throws PersistanceException
	 */
	public Map<String, String> getTodoListColumns() {
		final Map<String, String> dictionary = new HashMap<String, String>();

		QueryCallback callback = new QueryCallback() {
			@Override
			public void onQuerySuccess(Cursor cursor) {

				if (cursor.moveToFirst()) {
					int index_key = cursor
					        .getColumnIndex(DataBaseMetadata.TableTodoListColumnMetadata.COLUMN_TODO_COLUMN_KEY);
					int index_value = cursor
					        .getColumnIndex(DataBaseMetadata.TableTodoListColumnMetadata.COLUMN_TODO_COLUMN_VALUE_ID);

					do {
						if (cursor.getString(index_key).equalsIgnoreCase("todo_column_id")) {
							continue;
						} else {
							dictionary.put(cursor.getString(index_key), cursor.getString(index_value));
						}
					} while (cursor.moveToNext());
				}
			}
		};

		dataManager.query("select * from " + DataBaseMetadata.TableTodoListColumnMetadata.TABLENAME + " order by "
		        + DataBaseMetadata.TableTodoListColumnMetadata.COLUMN_TODO_COLUMN_VALUE_ID, null, callback);

		return dictionary;
	}

	/**
	 * 取所有key
	 * 
	 * @return
	 */
	public List<String> getAllKeys() {
		final List<String> result = new ArrayList<String>();

		QueryCallback callback = new QueryCallback() {
			@Override
			public void onQuerySuccess(Cursor cursor) {

				if (cursor.moveToFirst()) {
					do {
						result.add(cursor.getString(0));
					} while (cursor.moveToNext());
				}
			}
		};

		dataManager.query("select " + DataBaseMetadata.TableTodoListColumnMetadata.COLUMN_TODO_COLUMN_KEY + " from "
		        + DataBaseMetadata.TableTodoListColumnMetadata.TABLENAME + " ", null, callback);
		return result;
	}

	/**
	 * 取所有值列的主键
	 * 
	 * @return
	 */
	public List<String> getAllColumnRecordPK() {
		final List<String> result = new ArrayList<String>();

		QueryCallback callback = new QueryCallback() {

			@Override
			public void onQuerySuccess(Cursor cursor) {
				if (cursor.moveToFirst()) {
					do {
						result.add(cursor.getString(0));
					} while (cursor.moveToNext());
				}
			}
		};

		dataManager.query("select " + DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_LOGICAL_PK
		        + " from " + DataBaseMetadata.TableTodoListValueMetadata.TABLENAME, null, callback);

		return result;
	}

	/**
	 * 把本地数据标记为已在其他地方处理
	 * 
	 * @param dirtyRecordPKs
	 * @return
	 */
	public int markRecordAsBeingApproved(List<String> dirtyRecordPKs) {
		int affectiveRows = 0;
		for (String pk : dirtyRecordPKs) {
			ContentValues cv = new ContentValues();
			cv.put(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_STATUS,
			        Constrants.APPROVE_RECORD_STATUS_DIFFERENT);
			cv.put(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_SERVERMESSAGE, "已在其他地方处理");
			affectiveRows += dataManager.update(DataBaseMetadata.TableTodoListValueMetadata.TABLENAME, cv,
			        DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_LOGICAL_PK + "=?",
			        new String[] { pk });
		}

		return affectiveRows;
	}

	public int markAllRecordsAsBeingApproved() {
		int affectiveRows = 0;
		ContentValues cv = new ContentValues();
		cv.put(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_STATUS,
		        Constrants.APPROVE_RECORD_STATUS_DIFFERENT);
		cv.put(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_SERVERMESSAGE, "已在其他地方处理");
		affectiveRows = dataManager.update(DataBaseMetadata.TableTodoListValueMetadata.TABLENAME, cv, null, null);
		return affectiveRows;
	}

	/**
	 * @return
	 */
	public List<Map<String, String>> getAllTodoRecords() {

		final Map<String, String> dictionary = new HashMap<String, String>();
		final List<Map<String, String>> dataset = new ArrayList<Map<String, String>>();
		final StringBuffer sqlBuffer = new StringBuffer();

		// 先查出数据字段
		QueryCallback getDictionaryCallback = new QueryCallback() {
			final int index_todo_column_key = 0;
			final int index_todo_column_value_id = 1;

			@Override
			public void onQuerySuccess(Cursor cursor) {
				if (cursor.moveToFirst()) {
					sqlBuffer.append("select ");
					do {
						String key = cursor.getString(index_todo_column_key);
						String value = cursor.getString(index_todo_column_value_id);
						sqlBuffer.append(" ");
						sqlBuffer.append(value);
						sqlBuffer.append(" ");
						sqlBuffer.append(" as ");
						sqlBuffer.append(key);
						sqlBuffer.append(" ");

						if (cursor.isLast()) {
							sqlBuffer.append(", ");
							sqlBuffer.append(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_PHYSICAL_PK);
							sqlBuffer.append(" as ");
							sqlBuffer.append(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_PHYSICAL_PK);
							sqlBuffer.append(" ");
						} else {
							sqlBuffer.append(", ");
						}

						dictionary.put(key, value);

					} while (cursor.moveToNext());

					sqlBuffer.append(" from todo_value");
				}
			}
		};
		// 找出所有列
		dataManager.query("select todo_column_key,todo_column_value_id from todo_column", null, getDictionaryCallback);

		if (dictionary.size() == 0) {
			// 说明没有查到数据，返回空集合
			return dataset;
		}

		// 加入本地id

		QueryCallback allRecordsCallback = new QueryCallback() {
			@Override
			public void onQuerySuccess(Cursor cursor) {

				// 所有列名
				if (cursor.moveToFirst()) {
					do {
						Map<String, String> record = new HashMap<String, String>();

						for (int i = 0; i < cursor.getColumnCount(); i++) {
							String columnName = cursor.getColumnName(i);
							record.put(columnName, cursor.getString(i));
						}
						dataset.add(record);
					} while (cursor.moveToNext());
				}
			}
		};

		dataManager.query(sqlBuffer.toString(), null, allRecordsCallback);

		return dataset;
	}

	/**
	 * @param physicalRecordId
	 * @param serverMessage
	 * @return
	 */
	public int updateApproveRecordAsError(String physicalRecordId, String serverMessage) {
		ContentValues cv = new ContentValues();
		cv.put(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_STATUS,
		        Constrants.APPROVE_RECORD_STATUS_ERROR);
		cv.put(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_SERVERMESSAGE, serverMessage);

		int affectiveRows = dataManager.update(DataBaseMetadata.TableTodoListValueMetadata.TABLENAME, cv,
		        DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_PHYSICAL_PK + "= ?",
		        new String[] { physicalRecordId });
		return affectiveRows;
	}

	/**
	 * 保存审批动作
	 * 
	 * @param record
	 * @return
	 */
	public int saveApproveAction(Map<String, String> record) {
		ContentValues updateValues = new ContentValues();
		// 动作
		updateValues.put(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_ACTION,
		        record.get(DataBaseMetadata.TodoListLogical.ACTION));
		// 意见
		updateValues.put(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_COMMENTS,
		        record.get(DataBaseMetadata.TodoListLogical.COMMENTS));
		// 转交id
		updateValues.put(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_EMPLOYEE_ID,
		        record.get(DataBaseMetadata.TodoListLogical.EMPLOYEE_ID));
		// 状态
		updateValues.put(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_STATUS,
		        Constrants.APPROVE_RECORD_STATUS_WAITING);

		int affectiveRows = dataManager.update(DataBaseMetadata.TableTodoListValueMetadata.TABLENAME, updateValues,
		        DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_PHYSICAL_PK + "=?",
		        new String[] { record.get(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_PHYSICAL_PK) });
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
			affectiveRows += dataManager.delete(DataBaseMetadata.TableTodoListValueMetadata.TABLENAME,
			        DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_PHYSICAL_PK + "=?",
			        new String[] { ids[i] });
		}
		return affectiveRows;
	}

}
