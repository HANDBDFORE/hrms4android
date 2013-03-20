package com.hand.hrms4android.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.dao.TodoListDao;
import com.hand.hrms4android.exception.AuroraServerFailure;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.exception.PersistanceException;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.hand.hrms4android.persistence.AdditionalInformation;
import com.hand.hrms4android.persistence.DataBaseMetadata;
import com.hand.hrms4android.persistence.DatabaseManager;
import com.hand.hrms4android.util.Aggregate;
import com.hand.hrms4android.util.Constrants;
import com.hand.hrms4android.util.Iterator;
import com.hand.hrms4android.util.LogUtil;
import com.hand.hrms4android.util.TempTransfer;
import com.hand.hrms4android.util.data.IndexPath;
import com.loopj.android.http.HDJsonHttpResponseHandler;
import com.loopj.android.http.HDRequestParamsBatch;
import com.loopj.android.http.RequestParams;

public class TodoListModel extends AbstractBasePageableModel {

	private ConfigReader configReader;
	private TodoListDao dao;
	private IndexPath currentSelectedIndex;
	private boolean firstLoadFromInternet;

	private List<Map<String, String>> submitRecordsList;

	public TodoListModel(int id) {
		this(id, null);
	}

	public TodoListModel(int id, ModelActivity activity) {
		super(id, activity);
		configReader = XmlConfigReader.getInstance();
		dao = new TodoListDao();
		currentSelectedIndex = new IndexPath(0, 0);
		submitRecordsList = new ArrayList<Map<String, String>>();
		firstLoadFromInternet = true;
	}

	@Override
	public void load(LoadType loadType, Object param) {

		if (loadType == LoadType.Local) {
			List<Map<String, String>> allLocalRecords = dao.getAllTodoRecords();
			loadAuroraDataset = allLocalRecords;

			// 检查是否有需要提交的数据，加入提交列表
			for (Map<String, String> record : allLocalRecords) {
				if ((record.get(DataBaseMetadata.TodoListLogical.STATUS))
				        .equals(Constrants.APPROVE_RECORD_STATUS_WAITING)) {
					submitRecordsList.add(new HashMap<String, String>(record));
				}
			}
			activity.modelDidFinishedLoad(this);
			return;
		}

		// 检查是否有需要提交的数据,进行提交
		if (isSubmiting()) {
			// 有需要提交的数据
			submitData();
		}

		if (!isSubmiting()) {
			// 拉取数据
			queryData();
			firstLoadFromInternet = false;
		}

	}

	@Override
	public void setRecordAsSelected(IndexPath selectedIndex) {
		this.currentSelectedIndex.setRow(selectedIndex.getRow());
		this.currentSelectedIndex.setSection(selectedIndex.getSection());
	}

	// /////////////////////////////////////////////////////////////////////////////
	// 提交数据
	// /////////////////////////////////////////////////////////////////////////////
	private void submitData() {
		if (!isSubmiting()) {
			return;
		}

		// 提交数据的副本
		final Map<String, String> record = new HashMap<String, String>(submitRecordsList.get(0));

		// 组装传输参数
		RequestParams requestParams = new HDRequestParamsBatch(record);

		try {
			// 读取地址
			String actionURL = configReader.getAttr(new Expression(
			        "/config/application/activity[@name='todo_list_activity']/request/url[@name='action_submit_url']",
			        "value"));

			NetworkUtil.post(actionURL, requestParams, new HDJsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, List<Map<String, String>> dataset) {
					// 提交成功！

					// 移除内存中数据 TODO 考虑此步骤是否需要，因为是提交完成后重新读取了本地数据
					// loadAuroraDataset.remove(submitRecord);

					// 删除数据库数据
					dao.deleteRecord(record
					        .get(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_PHYSICAL_PK));
					loadAuroraDataset = dao.getAllTodoRecords();

					removeRecordFromSubmitList(record);

					// 通知界面操作成功
					activity.modelDidFinishedLoad(TodoListModel.this);
				}

				@Override
				public void onFailure(Throwable error, String content) {

					removeRecordFromSubmitList(record);

					if (error instanceof AuroraServerFailure) {
						// 说明bm返回错误消息
						// 更新内存中数据 TODO 考虑此步骤是否需要，因为是提交完成后重新读取了本地数据
						record.put(DataBaseMetadata.TodoListLogical.SERVER_MESSAGE, content);
						record.put(DataBaseMetadata.TodoListLogical.STATUS, "error");
						// 写本地异常
						dao.updateApproveRecordAsError(
						        record.get(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_PHYSICAL_PK),
						        content);
						// 通知界面可以更新
						loadAuroraDataset = dao.getAllTodoRecords();
						activity.modelDidFinishedLoad(TodoListModel.this);
					} else {
						// 其他异常，由于审批内容已被保存，所以直接告诉主界面出错
						loadAuroraDataset = dao.getAllTodoRecords();
						activity.modelFailedLoad(new Exception("发生意外错误，审批内容已被保存，请稍后重试"), TodoListModel.this);
					}
				}
			});

		} catch (ParseExpressionException e) {
			activity.modelFailedLoad(new Exception("无法读取指定URL:", e), TodoListModel.this);
			e.printStackTrace();
			return;
		}
	}

	/**
	 * 
	 */
	private void queryData() {
		// 查询数据
		String service = "";
		try {
			service = configReader
			        .getAttr(new Expression(
			                "/config/application/activity[@name='todo_list_activity']/request/url[@name='todo_list_query_url']",
			                "value"));
		} catch (ParseExpressionException e) {
			e.printStackTrace();
			activity.modelFailedLoad(new Exception("Cannot get url from config file! "), this);
			return;
		}

		NetworkUtil.post(service, null, new HDJsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, List<Map<String, String>> serverResponseDataset) {

				// 返回空数据
				if (serverResponseDataset.size() == 0) {
					dao.markAllRecordsAsBeingApproved();
					// 从数据库读取更新后的全部数据，传给界面
					loadAuroraDataset = dao.getAllTodoRecords();

					// 通知界面开始更新
					activity.modelDidFinishedLoad(TodoListModel.this);
					return;
				}
				// 把本地特征列加入返回数据
				// status，serverMessage,action,comments,employeeId
				// 五列
				for (int i = 0; i < serverResponseDataset.size(); i++) {
					Map<String, String> record = serverResponseDataset.get(i);

					// 增加5列
					record.put(DataBaseMetadata.TodoListLogical.STATUS, "");
					record.put(DataBaseMetadata.TodoListLogical.SERVER_MESSAGE, "");
					record.put(DataBaseMetadata.TodoListLogical.ACTION, "");
					record.put(DataBaseMetadata.TodoListLogical.COMMENTS, "");
					record.put(DataBaseMetadata.TodoListLogical.EMPLOYEE_ID, "");
				}

				try {
					// 取返回数据的所有列名
					Set<String> fields = serverResponseDataset.get(0).keySet();
					boolean sameAsLocal = checkLocalData(fields);

					// 获取主键
					String primaryKey = configReader
					        .getAttr(new Expression(
					                "/config/application/activity[@name='todo_list_activity']/request/url[@name='todo_list_query_url']/pk",
					                "name"));

					// 保存主键
					// TODO：这样写很别扭，要重构
					TempTransfer.todoListAuroraRecordPKKey = primaryKey;

					// 字段和本地相同
					if (sameAsLocal) {
						// 比较服务器返回的数据是否和本地有不同，主要用于检测是否有新数据和已被处理的数据
						// 先检测是否有新记录
						// 读取本地所有已存储的数据主键
						List<String> localPKs = dao.getAllColumnRecordPK();

						// 比较数据，找出新记录 声明一个存放新数据的数组
						List<Map<String, String>> newRecords = getAllNewRecords(serverResponseDataset, localPKs,
						        primaryKey);
						// 存数据表
						AdditionalInformation additional = new AdditionalInformation();
						additional.primaryKeyName = primaryKey;

						Map<String, String> storedDictionary = dao.getTodoListColumns();
						if (storedDictionary == null || storedDictionary.size() == 0) {
							// 说明从来没有进行过插入操作

							storedDictionary = null;
							// 插入头，并获得字典
							storedDictionary = dao.insertTodoListColumns(newRecords, additional);

						}
						// 插入行
						dao.insertTodoListRowData(newRecords, storedDictionary);

						// 比较数据，找出已经被处理的记录
						List<String> dirtyLocalRecordPKs = getAllLocalDirtyRecordPKs(serverResponseDataset, localPKs,
						        primaryKey);
						// 将已经处理的记录在数据库中更新
						dao.markRecordAsBeingApproved(dirtyLocalRecordPKs);

					} else {
						// 和本地存储的列结构不同，把本地数据清除
						deleteDB();

						// 写数据库
						AdditionalInformation additional = new AdditionalInformation();
						additional.primaryKeyName = primaryKey;
						dao.insertData(serverResponseDataset, additional);

					}
				} catch (ParseExpressionException e) {
					activity.modelFailedLoad(new Exception("Cannot get primary key from config file! "),
					        TodoListModel.this);
					e.printStackTrace();
					return;
				} catch (PersistanceException e) {
					activity.modelFailedLoad(e, TodoListModel.this);
					e.printStackTrace();
					return;
				}

				// 从数据库读取更新后的全部数据，传给界面
				loadAuroraDataset = dao.getAllTodoRecords();

				// 通知界面开始更新
				activity.modelDidFinishedLoad(TodoListModel.this);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				LogUtil.error(this, "request", "onFailure:" + content);
				if (error instanceof IOException) {
					// TODO 包装错误
				}
				activity.modelFailedLoad(new Exception(error), TodoListModel.this);
			}

			/**
			 * 检查本地列
			 * 
			 * @param serverDataFields
			 * @return 如果有不同，会返回false；完全相同，返回true;
			 */
			private boolean checkLocalData(Set<String> serverDataFields) {

				// 取得本地存储的keys
				List<String> allLocalKeys = dao.getAllKeys();

				// 看是否本地列都能找到对应项
				boolean allFoundInLocal = serverDataFields.containsAll(allLocalKeys);
				if (!allFoundInLocal) {
					// 找到了服务端返回的列中有本地没有找到的列
					return false;
				}

				// 看是否能在返回列中找到对应项
				boolean allFoundInRemote = allLocalKeys.containsAll(serverDataFields);
				return allFoundInRemote;
			}

			/**
			 * 找出服务器新增数据
			 * 
			 * @param source
			 * @param localPKs
			 * @param pkFiledName
			 * @return
			 */
			private List<Map<String, String>> getAllNewRecords(List<Map<String, String>> source, List<String> localPKs,
			        String pkFiledName) {
				List<Map<String, String>> newRecords = new ArrayList<Map<String, String>>();

				for (int i = 0; i < source.size(); i++) {
					boolean foundSame = false;
					Map<String, String> serverRecord = source.get(i);
					// 取得返回数据的主键值
					String serverRecordPK = serverRecord.get(pkFiledName);

					// 循环本地，看是否能找到相同项
					for (String localPK : localPKs) {
						if (serverRecordPK.equals(localPK)) {
							// 找到相同列
							foundSame = true;
							break;
						}
					}
					if (!foundSame) {
						// 没找到相同项，说明新数据
						serverRecord.put(DataBaseMetadata.TodoListLogical.STATUS,
						        Constrants.APPROVE_RECORD_STATUS_NORMAL);
						// 加入新数据集合
						newRecords.add(0, serverRecord);
					}

				}

				return newRecords;
			}

			/**
			 * 找已经被处理的数据
			 * 
			 * @param source
			 * @param localPKs
			 * @param pkFiledName
			 * @return
			 */
			private List<String> getAllLocalDirtyRecordPKs(List<Map<String, String>> source, List<String> localPKs,
			        String pkFiledName) {
				List<String> dirtyRecords = new ArrayList<String>();

				for (String localPK : localPKs) {
					boolean foundSame = false;
					for (Map<String, String> serverRecord : source) {
						if (serverRecord.get(pkFiledName).equals(localPK)) {
							// 找到相同项
							foundSame = true;
							break;
						}
					}

					if (!foundSame) {
						// 没有找到相同项，说明已被处理
						dirtyRecords.add(localPK);
					}
				}

				return dirtyRecords;
			}

			/**
			 * 移除数据库
			 */
			private void deleteDB() {
				File dbFile = HrmsApplication.getApplication().getDatabasePath(DatabaseManager.DB_NAME);
				dbFile.delete();
			}
		});
	}

	/**
	 * 是否需要加载更多信息
	 * 
	 * @return
	 */
	public boolean needLoadOnceMore() {
		if (firstLoadFromInternet) {
			// 还没有从网络读取过数据
			return true;
		}

		return isSubmiting();
	}

	private boolean isSubmiting() {
		if (submitRecordsList != null && (!submitRecordsList.isEmpty())) {
			return true;
		} else {
			return false;
		}
	}

	private void removeRecordFromSubmitList(Map<String, String> record) {
		submitRecordsList.remove(record);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// 实现集合
	// /////////////////////////////////////////////////////////////////////////////
	@Override
	public Iterator<Map<String, String>> createIterator() {
		return this;
	}
	
	// /////////////////////////////////////////////////////////////////////////////
	// 放入审批数据
	// /////////////////////////////////////////////////////////////////////////////
	public void addRecordToSubmitQueue(Map<String, String> options, String... physicalRecordIDs) {
		for (int i = 0; i < physicalRecordIDs.length; i++) {
			for (Map<String, String> record : loadAuroraDataset) {
				if (record.get(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_PHYSICAL_PK).equals(
				        physicalRecordIDs[i])) {
					// 找到目标
					// 加入附加信息
					record.putAll(options);
					// 放入拷贝数据
					this.submitRecordsList.add(new HashMap<String, String>(record));

					// 即时存表
					dao.saveApproveAction(record);
				}
			}
		}
		// 通知界面完成,让界面刷新
		activity.modelDidFinishedLoad(this);
	}

	public int removeRowByID(long itemID) {
		int affectiveCount = dao.deleteRecord(String.valueOf(itemID));
		loadAuroraDataset = dao.getAllTodoRecords();
		return affectiveCount;
	}

}
