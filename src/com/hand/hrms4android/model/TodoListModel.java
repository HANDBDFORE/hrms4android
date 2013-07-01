package com.hand.hrms4android.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.dao.TodoListDao;
import com.hand.hrms4android.exception.AuroraServerFailure;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.exception.PersistanceException;
import com.hand.hrms4android.listable.doman.TodoListDomain;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.hand.hrms4android.persistence.DataBaseMetadata.TodoList;
import com.hand.hrms4android.util.Constrants;
import com.hand.hrms4android.util.Iterator;
import com.hand.hrms4android.util.LogUtil;
import com.hand.hrms4android.util.data.IndexPath;
import com.loopj.android.http.HDJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.UMJsonHttpResponseHandler;

public class TodoListModel extends AbstractPageableQueryModel<TodoListDomain> {

	private ConfigReader configReader;
	private TodoListDao dao;
	private boolean firstLoadFromInternet;

	private List<TodoListDomain> submitRecordsList;

	public TodoListModel(int id) {
		this(id, null);
	}

	public TodoListModel(int id, ModelActivity activity) {
		super(id, activity);
		configReader = XmlConfigReader.getInstance();
		dao = new TodoListDao();
		currentSelectedIndex = new IndexPath(0, 0);
		submitRecordsList = new ArrayList<TodoListDomain>();
		firstLoadFromInternet = true;
	}

	@Override
	public void load(LoadType loadType, Object param) {

		if (loadType == LoadType.Local) {
			List<TodoListDomain> allLocalRecords = dao.getAllTodoRecords();
			loadAuroraDataset = allLocalRecords;

			// 检查是否有需要提交的数据，加入提交列表
			for (TodoListDomain record : allLocalRecords) {
				if (record.getStatus().equals(Constrants.APPROVE_RECORD_STATUS_WAITING)) {
					submitRecordsList.add(new TodoListDomain(record));
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

	// /////////////////////////////////////////////////////////////////////////////
	// 提交数据
	// /////////////////////////////////////////////////////////////////////////////
	private void submitData() {
		if (!isSubmiting()) {
			return;
		}

		// 提交数据的副本
		final TodoListDomain record = new TodoListDomain(submitRecordsList.get(0));

		try {
			// 读取地址
			String actionURL = configReader.getAttr(new Expression(
			        "/config/application/activity[@name='todo_list_activity']/request/url[@name='action_submit_url']",
			        "value"));

			RequestParams params = new RequestParams();
			params.put(TodoList.ACTION, record.getAction());
			params.put(TodoList.COMMENTS, record.getComments());
			params.put(TodoList.SOURCE_SYSTEM_NAME, record.getSourceSystemName());
			params.put(TodoList.LOCALID, record.getLocalId());

			NetworkUtil.post(actionURL, params, new UMJsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, JSONObject response) {
					// 提交成功！

					// 移除内存中数据 TODO 考虑此步骤是否需要，因为是提交完成后重新读取了本地数据
					// loadAuroraDataset.remove(submitRecord);

					// 删除数据库数据
					dao.deleteRecord(record.getId());
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
						record.setServerMessage(content);
						record.setStatus(Constrants.APPROVE_RECORD_STATUS_ERROR);
						// 写本地异常
						dao.updateApproveRecordAsError(record.getId(), content);
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

		NetworkUtil.post(service, null, new UMJsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				//
				try {
					JSONArray serverResponseDataset = response.getJSONObject("body").getJSONArray("list");
					// 返回空数据
					if (serverResponseDataset.length() == 0) {
						dao.markAllRecordsAsBeingApproved();
						// 从数据库读取更新后的全部数据，传给界面
						loadAuroraDataset = dao.getAllTodoRecords();

						// 通知界面开始更新
						activity.modelDidFinishedLoad(TodoListModel.this);
						return;
					}

					List<TodoListDomain> serverTodoRecords = new ArrayList<TodoListDomain>();
					for (int i = 0; i < serverResponseDataset.length(); i++) {
						serverTodoRecords.add(new TodoListDomain(serverResponseDataset.getJSONObject(i)));
					}
					// 把本地特征列加入返回数据
					// status，serverMessage,action,comments

					// // 五列

					// 先检测是否有新记录
					// 读取本地所有已存储的数据主键
					List<TodoListDomain> localRecords = dao.getAllTodoRecords();

					// 比较数据，找出新记录 声明一个存放新数据的数组
					List<TodoListDomain> newRecords = getAllNewRecords(serverTodoRecords, localRecords);

					// 存数据表
					dao.insertTodoListRowData(newRecords);

					// 比较数据，找出已经被处理的记录
					List<String> dirtyLocalRecordPKs = getAllLocalDirtyRecordPKs(serverTodoRecords, localRecords);
					// 将已经处理的记录在数据库中更新
					dao.markRecordAsBeingApproved(dirtyLocalRecordPKs);

				} catch (PersistanceException e) {
					activity.modelFailedLoad(e, TodoListModel.this);
					e.printStackTrace();
					return;
				} catch (JSONException e) {
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
					error = new IOException("通讯失败");
				}
				activity.modelFailedLoad(new Exception(error.getMessage()), TodoListModel.this);
			}

		});
	}

	/**
	 * 找出服务器新增数据
	 * 
	 * @param source
	 * @param localPKs
	 * @param pkFiledName
	 * @return
	 */
	private List<TodoListDomain> getAllNewRecords(List<TodoListDomain> source, List<TodoListDomain> localRecords) {
		List<TodoListDomain> newRecords = new ArrayList<TodoListDomain>();

		for (int i = 0; i < source.size(); i++) {
			boolean foundSame = false;
			TodoListDomain serverRecord = source.get(i);

			// 循环本地，看是否能找到相同项
			for (TodoListDomain local : localRecords) {
				if (serverRecord.getLocalId().equals(local.getLocalId())
				        && serverRecord.getSourceSystemName().equalsIgnoreCase(local.getSourceSystemName())) {
					// 找到相同列
					foundSame = true;
					break;
				}
			}
			if (!foundSame) {
				// 没找到相同项，说明新数据
				serverRecord.setStatus(Constrants.APPROVE_RECORD_STATUS_NORMAL);
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
	private List<String> getAllLocalDirtyRecordPKs(List<TodoListDomain> source, List<TodoListDomain> localRecords) {
		List<String> dirtyRecords = new ArrayList<String>();

		for (TodoListDomain local : localRecords) {
			boolean foundSame = false;
			for (TodoListDomain serverRecord : source) {
				if (serverRecord.getLocalId().equals(local.getLocalId())
				        && serverRecord.getSourceSystemName().equalsIgnoreCase(local.getSourceSystemName())) {
					// 找到相同项
					foundSame = true;
					break;
				}
			}
			if (!foundSame) {
				// 没有找到相同项，说明已被处理
				dirtyRecords.add(local.getId());
			}
		}

		return dirtyRecords;
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

	private void removeRecordFromSubmitList(TodoListDomain record) {
		TodoListDomain target = null;
		for (TodoListDomain submiting : submitRecordsList) {
			if (submiting.getId().equals(record.getId())) {
				target = submiting;
				break;
			}
		}

		submitRecordsList.remove(target);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// 实现集合
	// /////////////////////////////////////////////////////////////////////////////
	@Override
	public Iterator<TodoListDomain> createIterator() {
		return this;
	}

	// /////////////////////////////////////////////////////////////////////////////
	// 放入审批数据
	// /////////////////////////////////////////////////////////////////////////////
	public void addRecordToSubmitQueue(Map<String, String> options, String... ids) {
		for (int i = 0; i < ids.length; i++) {
			for (TodoListDomain record : loadAuroraDataset) {
				if (record.getId().equals(ids[i])) {
					// 找到目标
					// 加入附加信息
					record.setAction(options.get(TodoList.ACTION));
					record.setComments(options.get(TodoList.COMMENTS));
					// 放入拷贝数据
					this.submitRecordsList.add(new TodoListDomain(record));

					// 即时存表
					dao.saveApproveAction(record);
				}
			}
		}
		// 通知界面完成,让界面刷新
		activity.modelDidFinishedLoad(this);
	}

	public int removeRowByID(String itemID) {
		int affectiveCount = dao.deleteRecord(itemID);
		loadAuroraDataset = dao.getAllTodoRecords();
		return affectiveCount;
	}

}
