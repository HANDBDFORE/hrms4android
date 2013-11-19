package com.hand.hrms4android.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.hand.hrms4android.core.AbstractPageableModel;
import com.hand.hrms4android.core.ModelViewController;
import com.hand.hrms4android.dao.TodoListDao;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.exception.PersistanceException;
import com.hand.hrms4android.listable.doman.TodoListDomain;
import com.hand.hrms4android.network.HDJsonObjectRequest;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.network.RequestManager;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.hand.hrms4android.persistence.DataBaseMetadata.TodoList;
import com.hand.hrms4android.util.Constrants;
import com.hand.hrms4android.util.Iterator;
import com.hand.hrms4android.util.data.IndexPath;

public class TodoListModel extends AbstractPageableModel<TodoListDomain> {

	private ConfigReader configReader;
	private TodoListDao dao;
	private boolean firstLoadFromInternet;

	private List<TodoListDomain> submitRecordsList;

	public TodoListModel(int id) {
		this(id, null);
	}

	public TodoListModel(int id, ModelViewController activity) {
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
			data = allLocalRecords;

			// 检查是否有需要提交的数据，加入提交列表
			for (TodoListDomain record : allLocalRecords) {
				if (record.getStatus().equals(Constrants.APPROVE_RECORD_STATUS_WAITING)) {
					submitRecordsList.add(new TodoListDomain(record));
				}
			}
			controller.modelDidFinishedLoad(this);
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

		JSONArray submitArray = new JSONArray();
		for (TodoListDomain sr : submitRecordsList) {
			JSONObject jsonSubmitRecord = new JSONObject();
			try {
				jsonSubmitRecord.put(TodoList.LOCALID, sr.getLocalId());
				jsonSubmitRecord.put(TodoList.ACTION, sr.getAction());
				jsonSubmitRecord.put(TodoList.ACTION_TYPE, sr.getActionType());
				jsonSubmitRecord.put(TodoList.COMMENTS, sr.getComments());
				jsonSubmitRecord.put(TodoList.SOURCE_SYSTEM_NAME, sr.getSourceSystemName());

				if (!StringUtils.isEmpty(sr.getDeliveree())) {
					JSONObject otherParamsJson = new JSONObject();
					otherParamsJson.put(TodoList.DELIVEREE, sr.getDeliveree());
					jsonSubmitRecord.put("otherParams", otherParamsJson);
				}

				submitArray.put(jsonSubmitRecord);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		try {
			// 读取地址
			String actionURL = configReader.getAttr(new Expression(
			        "/config/application/activity[@name='todo_list_activity']/request/url[@name='action_submit_url']",
			        "value"));

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("actions", submitArray);

//			// NetworkUtil.post(actionURL, params, new
//			// UMJsonHttpResponseHandler() {
//			NetworkUtil.post(actionURL, params, new UMJsonHttpResponseHandler() {
//				@Override
//				public void onSuccess(int statusCode, JSONObject response) {
//					// 提交成功！
//					// new
//					// "message": "SUCCESS",
//					// "status": "S",
//					// "localId": 25908,
//					// "sourceSystemName": "HR"
//					try {
//
//						JSONArray resultJsonArray = response.getJSONObject("body").getJSONArray("list");
//						for (int i = 0; i < resultJsonArray.length(); i++) {
//							// 拿到每条提交记录的返回信息
//							JSONObject responseRecord = resultJsonArray.getJSONObject(i);
//							// 处理状态
//							String responseStatus = responseRecord.getString("status");
//							String responseMessage = null;
//							if (responseRecord.has("message")) {
//								responseMessage = responseRecord.getString("message");
//							}
//							String responseSourceSystemName = responseRecord.getString("sourceSystemName");
//							String responseRecordLocalId = responseRecord.getString("localId");
//
//							TodoListDomain submitRecord = findRecord(submitRecordsList, responseRecordLocalId,
//							        responseSourceSystemName);
//
//							if ("S".equals(responseStatus)) {
//								// 成功
//								// 删除数据库数据
//								dao.deleteRecord(submitRecord.getId());
//							} else {
//								// 失败
//								submitRecord.setServerMessage(responseMessage);
//								submitRecord.setStatus(Constrants.APPROVE_RECORD_STATUS_ERROR);
//								// 写本地异常
//								dao.updateApproveRecordAsError(submitRecord.getId(), responseMessage);
//							}
//							// 从待提交列表中移除记录
//							removeRecordFromSubmitList(submitRecord);
//						}
//						// 重新加载数据
//						loadAuroraDataset = dao.getAllTodoRecords();
//						// 通知界面可以更新
//						activity.modelDidFinishedLoad(TodoListModel.this);
//
//					} catch (JSONException e) {
//						e.printStackTrace();
//						this.onFailure(new IllegalStateException("服务器返回数据格式不正确"), response.toString());
//						return;
//					}
//
//				}
//
//				@Override
//				public void onFailure(Throwable error, String content) {
//					// 其他异常，由于审批内容已被保存，所以直接告诉主界面出错
//					loadAuroraDataset = dao.getAllTodoRecords();
//					activity.modelFailedLoad(new Exception("发生意外错误，审批内容已被保存，请稍后重试"), TodoListModel.this);
//				}
//			});

			RequestManager.getRequestQueue().add(genSubmitRequest(requestTag, params, NetworkUtil.getAbsoluteUrl(actionURL)));

		} catch (ParseExpressionException e) {
			handleError(new Exception("无法读取指定URL:", e));
			return;
		}

	}

	private Request<JSONObject> genSubmitRequest(Object tag, Object params, String url) {
		Listener<JSONObject> listener = new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {

				// 提交成功！
				// new
				// "message": "SUCCESS",
				// "status": "S",
				// "localId": 25908,
				// "sourceSystemName": "HR"
				try {

					JSONArray resultJsonArray = response.getJSONObject("body").getJSONArray("list");
					for (int i = 0; i < resultJsonArray.length(); i++) {
						// 拿到每条提交记录的返回信息
						JSONObject responseRecord = resultJsonArray.getJSONObject(i);
						// 处理状态
						String responseStatus = responseRecord.getString("status");
						String responseMessage = null;
						if (responseRecord.has("message")) {
							responseMessage = responseRecord.getString("message");
						}
						String responseSourceSystemName = responseRecord.getString("sourceSystemName");
						String responseRecordLocalId = responseRecord.getString("localId");

						TodoListDomain submitRecord = findRecord(submitRecordsList, responseRecordLocalId,
						        responseSourceSystemName);

						if ("S".equals(responseStatus)) {
							// 成功
							// 删除数据库数据
							dao.deleteRecord(submitRecord.getId());
						} else {
							// 失败
							submitRecord.setServerMessage(responseMessage);
							submitRecord.setStatus(Constrants.APPROVE_RECORD_STATUS_ERROR);
							// 写本地异常
							dao.updateApproveRecordAsError(submitRecord.getId(), responseMessage);
						}
						// 从待提交列表中移除记录
						removeRecordFromSubmitList(submitRecord);
					}
					// 重新加载数据
					data = dao.getAllTodoRecords();
					// 通知界面可以更新
					controller.modelDidFinishedLoad(TodoListModel.this);

				} catch (JSONException e) {
					e.printStackTrace();
					handleError(new IllegalStateException("服务器返回数据格式不正确 "+ response.toString()));
					return;
				}

			}
		};
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// 其他异常，由于审批内容已被保存，所以直接告诉主界面出错
				data = dao.getAllTodoRecords();
				handleError(new Exception("发生意外错误，审批内容已被保存，请稍后重试 "+error.getMessage()));
			}
		};

		Request<JSONObject> request = new HDJsonObjectRequest(controller.getContext(), Method.GET, url, params, listener, errorListener);
		request.setTag(tag);

		return request;
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
			handleError(new Exception("Cannot get url from config file! "));
			return;
		}

		List<TodoListDomain> localData = dao.queryLocalLogicalId();
		JSONArray submitJsonArray = new JSONArray();

		try {
			for (TodoListDomain localRecord : localData) {
				JSONObject record = new JSONObject();
				record.put(TodoList.LOCALID, localRecord.getLocalId());
				record.put(TodoList.SOURCE_SYSTEM_NAME, localRecord.getSourceSystemName());
				submitJsonArray.put(record);
			}
		} catch (JSONException e) {
			handleError(e);
			e.printStackTrace();
			return;
		} 

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("localIds", submitJsonArray.toString());
//		NetworkUtil.post(service, params, new UMJsonHttpResponseHandler() {
//
//			@Override
//			public void onSuccess(int statusCode, JSONObject response) {
//				//
//				try {
//					JSONObject responseCategory = response.getJSONObject("body");
//
//					// 删除旧数据
//					if (responseCategory.has("delete")) {
//						JSONArray deleteJsonRecords = responseCategory.getJSONArray("delete");
//						for (int i = 0; i < deleteJsonRecords.length(); i++) {
//							JSONObject o = deleteJsonRecords.getJSONObject(i);
//							dao.deleteRecordByLogicalID(o.getString(TodoList.LOCALID),
//							        o.getString(TodoList.SOURCE_SYSTEM_NAME));
//						}
//					}
//
//					if (responseCategory.has("new")) {
//						JSONArray newJsonRecords = responseCategory.getJSONArray("new");
//
//						List<TodoListDomain> serverTodoNewRecords = new ArrayList<TodoListDomain>();
//						for (int i = 0; i < newJsonRecords.length(); i++) {
//							serverTodoNewRecords.add(new TodoListDomain(newJsonRecords.getJSONObject(i)));
//						}
//
//						// 存数据表
//						dao.insertTodoListRowData(serverTodoNewRecords);
//					}
//
//				} catch (PersistanceException e) {
//					activity.modelFailedLoad(e, TodoListModel.this);
//					e.printStackTrace();
//					return;
//				} catch (JSONException e) {
//					activity.modelFailedLoad(e, TodoListModel.this);
//					e.printStackTrace();
//					return;
//				}
//
//				// 从数据库读取更新后的全部数据，传给界面
//				loadAuroraDataset = dao.getAllTodoRecords();
//
//				// 通知界面开始更新
//				activity.modelDidFinishedLoad(TodoListModel.this);
//			}
//
//			@Override
//			public void onFailure(Throwable error, String content) {
//				LogUtil.error(this, "request", "onFailure:" + content);
//				if (error instanceof IOException) {
//					error = new IOException("通讯失败");
//				}
//				activity.modelFailedLoad(new Exception(error.getMessage()), TodoListModel.this);
//			}
//
//		});
		
		RequestManager.getRequestQueue().add(genQueryRequest(requestTag, params, NetworkUtil.getAbsoluteUrl(service)));
	}
	
	private Request<JSONObject> genQueryRequest(Object tag, Object params, String url) {
		Listener<JSONObject> listener = new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				//
				try {
					JSONObject responseCategory = response.getJSONObject("body");

					// 删除旧数据
					if (responseCategory.has("delete")) {
						JSONArray deleteJsonRecords = responseCategory.getJSONArray("delete");
						for (int i = 0; i < deleteJsonRecords.length(); i++) {
							JSONObject o = deleteJsonRecords.getJSONObject(i);
							dao.deleteRecordByLogicalID(o.getString(TodoList.LOCALID),
							        o.getString(TodoList.SOURCE_SYSTEM_NAME));
						}
					}

					if (responseCategory.has("new")) {
						JSONArray newJsonRecords = responseCategory.getJSONArray("new");

						List<TodoListDomain> serverTodoNewRecords = new ArrayList<TodoListDomain>();
						for (int i = 0; i < newJsonRecords.length(); i++) {
							serverTodoNewRecords.add(new TodoListDomain(newJsonRecords.getJSONObject(i)));
						}

						// 存数据表
						dao.insertTodoListRowData(serverTodoNewRecords);
					}

				} catch (PersistanceException e) {
					handleError(e);
					e.printStackTrace();
					return;
				} catch (JSONException e) {
					handleError(e);
					e.printStackTrace();
					return;
				}

				// 从数据库读取更新后的全部数据，传给界面
				data = dao.getAllTodoRecords();

				// 通知界面开始更新
				controller.modelDidFinishedLoad(TodoListModel.this);

			}
		};
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				handleError(new Exception(error.getMessage()));
			}
		};

		Request<JSONObject> request = new HDJsonObjectRequest(controller.getContext(), Method.GET, url, params, listener, errorListener);
		request.setTag(tag);

		return request;
	}

	/**
	 * 是否需要加载更多信息
	 * 
	 * 
	 * @return 如果是第一次进入程序，或者有需要提交的数据，返回true，否则false
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
			if (submiting.getId() == record.getId()) {
				target = submiting;
				break;
			}
		}

		submitRecordsList.remove(target);
	}

	private TodoListDomain findRecord(List<TodoListDomain> source, String localId, String sourceSystemName) {
		for (TodoListDomain record : source) {
			if ((record.getLocalId().equals(localId)) && sourceSystemName.equals(record.getSourceSystemName())) {
				return record;
			}
		}
		return null;
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
			for (TodoListDomain record : data) {
				if (record.getId().equals(ids[i])) {
					// 找到目标
					// 加入附加信息
					record.setAction(options.get(TodoList.ACTION));
					record.setActionType(options.get(TodoList.ACTION_TYPE));
					record.setComments(options.get(TodoList.COMMENTS));
					record.setDeliveree(options.get(TodoList.DELIVEREE));
					// 放入拷贝数据
					this.submitRecordsList.add(new TodoListDomain(record));

					// 即时存表
					dao.saveApproveAction(record);
				}
			}
		}
		// 通知界面完成,让界面刷新
		controller.modelDidFinishedLoad(this);
	}

	public int removeRowByID(String itemID) {
		int affectiveCount = dao.deleteRecord(itemID);
		data = dao.getAllTodoRecords();
		return affectiveCount;
	}

	@SuppressWarnings("unchecked")
    @Override
	public List<TodoListDomain> getProcessData() {
		return data;
	}

}
