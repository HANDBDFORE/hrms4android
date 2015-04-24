package com.hand.hrms4android.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.dao.ActionsDao;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.listable.doman.TodoListDomain;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.hand.hrms4android.pojo.ApproveAction;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.UMJsonHttpResponseHandler;

public class ApproveDetailActionModel extends AbstractBaseModel<List<ApproveAction>> {
	private ConfigReader configReader;
	private ActionsDao actionsDao;
	private List<ApproveAction> actions;
	
	private String signature;

	public ApproveDetailActionModel(int id, ModelActivity activity) {
		super(id, activity);
		this.configReader = XmlConfigReader.getInstance();
		actionsDao = new ActionsDao();
		actions = new ArrayList<ApproveAction>();
	}

	@Override
	public void load(LoadType loadType, Object param) {
		final TodoListDomain record = (TodoListDomain) param;

//		List<ApproveAction> storedActions = actionsDao.getAllActionsByRecordId(record.getId());

		// 没有存储此项动作
		if (true) {

			String service;
			try {
				service = configReader
				        .getAttr(new Expression(
				                "/config/application/activity[@name='approve_detail_activity']/request/url[@name='action_query_url']",
				                "value"));

				RequestParams p = new RequestParams();
				p.put("localId", record.getLocalId());
				p.put("sourceSystemName", record.getSourceSystemName());
				String ca_verification_necessity = record.getVerificationId() == 0 ? "0" : "1";
				p.put("ca_verification_necessity", ca_verification_necessity);
				NetworkUtil.post(service, p, new UMJsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, JSONObject response) {
						try {
							signature = response.getJSONObject("body").getString("signature");
							JSONArray responseActionJson = response.getJSONObject("body").getJSONArray("list");
							List<ApproveAction> responseActions = new ArrayList<ApproveAction>(responseActionJson
							        .length());
							for (int i = 0; i < responseActionJson.length(); i++) {
								JSONObject responseAction = responseActionJson.getJSONObject(i);
								responseActions.add(new ApproveAction(0, record.getId(), responseAction
								        .getString("actionType"), responseAction.getString("action"), responseAction
								        .getString("actionTitle")));
							}

//							actionsDao.insertAllActions(responseActions);
							actions = responseActions;
							activity.modelDidFinishedLoad(ApproveDetailActionModel.this);
						} catch (JSONException e) {
							e.printStackTrace();
							activity.modelFailedLoad(new Exception("action解析出错"), ApproveDetailActionModel.this);
						}
					}

					@Override
					public void onFailure(Throwable error, String content) {
						activity.modelFailedLoad(new Exception("获取action出错"), ApproveDetailActionModel.this);
					}
				});
			} catch (ParseExpressionException e1) {
				e1.printStackTrace();
				activity.modelFailedLoad(new Exception("无法找到url"), ApproveDetailActionModel.this);
			}

		}

		// 已经存在存储动作
//		else {
//			actions = storedActions;
//			storedActions = null;
//			activity.modelDidFinishedLoad(this);
//		}
	}

	@Override
	public List<ApproveAction> getProcessData() {
		return actions;
	}
	
	public String getSignature(){
		
		return this.signature;
	}
}
