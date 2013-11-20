package com.hand.hrms4android.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.hand.hrms4android.core.HDAbstractModel;
import com.hand.hrms4android.core.ModelViewController;
import com.hand.hrms4android.dao.ActionsDao;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.listable.doman.TodoListDomain;
import com.hand.hrms4android.network.HDJsonObjectRequest;
import com.hand.hrms4android.network.HDOtherRequest;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.network.RequestManager;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.hand.hrms4android.pojo.ApproveAction;

public class ApproveDetailActionModel extends HDAbstractModel {
	private ConfigReader configReader;
	private ActionsDao actionsDao;
	private List<ApproveAction> actions;

	public ApproveDetailActionModel(int id, ModelViewController activity) {
		super(id, activity);
		this.configReader = XmlConfigReader.getInstance();
		actionsDao = new ActionsDao();
		actions = new ArrayList<ApproveAction>();
	}

	@Override
	public void load(LoadType loadType, Object param) {
		final TodoListDomain record = (TodoListDomain) param;

		List<ApproveAction> storedActions = actionsDao.getAllActionsByRecordId(record.getId());

		// 没有存储此项动作
		if (storedActions.size() == 0) {

			String service;
			try {
				service = configReader
				        .getAttr(new Expression(
				                "/config/application/activity[@name='approve_detail_activity']/request/url[@name='action_query_url']",
				                "value"));

				Map<String, String> p = new HashMap<String, String>();
				p.put("localId", record.getLocalId());
				p.put("sourceSystemName", record.getSourceSystemName());
				
				RequestManager.getRequestQueue().add(genSubmitRequest(requestTag, p, NetworkUtil.getAbsoluteUrl(service), record));
				
//				NetworkUtil.post(service, p, new UMJsonHttpResponseHandler() {
//					@Override
//					public void onSuccess(int statusCode, JSONObject response) {
//						try {
//							JSONArray responseActionJson = response.getJSONObject("body").getJSONArray("list");
//							List<ApproveAction> responseActions = new ArrayList<ApproveAction>(responseActionJson
//							        .length());
//							for (int i = 0; i < responseActionJson.length(); i++) {
//								JSONObject responseAction = responseActionJson.getJSONObject(i);
//								responseActions.add(new ApproveAction(0, record.getId(), responseAction
//								        .getString("actionType"), responseAction.getString("action"), responseAction
//								        .getString("actionTitle")));
//							}
//
//							actionsDao.insertAllActions(responseActions);
//							actions = responseActions;
//							activity.modelDidFinishedLoad(ApproveDetailActionModel.this);
//						} catch (JSONException e) {
//							e.printStackTrace();
//							activity.modelFailedLoad(new Exception("action解析出错"), ApproveDetailActionModel.this);
//						}
//					}
//
//					@Override
//					public void onFailure(Throwable error, String content) {
//						activity.modelFailedLoad(new Exception("获取action出错"), ApproveDetailActionModel.this);
//					}
//				});
			} catch (ParseExpressionException e1) {
				e1.printStackTrace();
				handleError(new Exception("无法找到url"));
			}

		}

		// 已经存在存储动作
		else {
			actions = storedActions;
			storedActions = null;
			controller.modelDidFinishedLoad(this);
		}
	}
	
	private Request<JSONObject> genSubmitRequest(Object tag, Map<String, String>  params, String url,final TodoListDomain record) {
		Listener<JSONObject> listener = new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONArray responseActionJson = response.getJSONObject("body").getJSONArray("list");
					List<ApproveAction> responseActions = new ArrayList<ApproveAction>(responseActionJson
					        .length());
					for (int i = 0; i < responseActionJson.length(); i++) {
						JSONObject responseAction = responseActionJson.getJSONObject(i);
						responseActions.add(new ApproveAction(0, record.getId(), responseAction
						        .getString("actionType"), responseAction.getString("action"), responseAction
						        .getString("actionTitle")));
					}

					actionsDao.insertAllActions(responseActions);
					actions = responseActions;
					controller.modelDidFinishedLoad(ApproveDetailActionModel.this);
				} catch (JSONException e) {
					e.printStackTrace();
					handleError(new Exception("action解析出错"));
					return;
				}

			}
		};
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// 其他异常，由于审批内容已被保存，所以直接告诉主界面出错
				handleError(new Exception("获取action出错 "+error.getMessage()));
			}
		};

		Request<JSONObject> request = new HDOtherRequest(controller.getContext(), Method.POST, url, params, listener, errorListener);
		request.setTag(tag);

		return request;
	}

	@Override
	public List<ApproveAction> getProcessData() {
		return actions;
	}
}
