package com.hand.hrms4android.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.dao.ActionsDao;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.exception.PersistanceException;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.hand.hrms4android.persistence.AdditionalInformation;
import com.hand.hrms4android.pojo.ApproveAction;
import com.hand.hrms4android.util.PlaceHolderReplacer;
import com.loopj.android.http.HDJsonHttpResponseHandler;

public class ApproveDetailActionModel extends AbstractModel {
	private ConfigReader configReader;
	private ActionsDao actionsDao;
	private List<ApproveAction> actions;

	public ApproveDetailActionModel(int id,ModelActivity activity) {
		super(id, activity);
		this.configReader = XmlConfigReader.getInstance();
		actionsDao = new ActionsDao();
		actions = new ArrayList<ApproveAction>();
	}

	@Override
	public void load(LoadType loadType, Object param) {
		@SuppressWarnings("unchecked")
		Map<String, String> record = (Map<String, String>) param;

		try {
			final String recordLogicalPK = configReader
			        .getAttr(new Expression(
			                "/config/activity[@name='todo_list_activity']/request/url[@name='todo_list_query_url']/pk",
			                "name"));
			String recordLogicalPKValue = record.get(recordLogicalPK);
			List<ApproveAction> storedActions = actionsDao.getAllActionsByRecordId(recordLogicalPKValue);

			// 没有存储此项动作
			if (storedActions.size() == 0) {

				String urlValueAtConfigFile = configReader.getAttr(new Expression(
				        "/config/activity[@name='approve_detail_activity']/request/url[@name='action_query_url']",
				        "value"));
				String service = PlaceHolderReplacer.replaceForValue(record, urlValueAtConfigFile);

				NetworkUtil.post(service, null, new HDJsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, List<Map<String, String>> dataset) {
						super.onSuccess(statusCode, dataset);

						try {
							// 将取得结果存表
							AdditionalInformation additionalInformation = new AdditionalInformation();
							additionalInformation.primaryKeyName = recordLogicalPK;
							actions = actionsDao.insertAllActions(dataset, additionalInformation);
							activity.modelDidFinishedLoad(ApproveDetailActionModel.this);
						} catch (PersistanceException e) {
							activity.modelFailedLoad(new Exception("无法找到主键，无法存储动作"), ApproveDetailActionModel.this);
							e.printStackTrace();
						}
					}
				});
			}

			// 已经存在存储动作
			else {
				actions = storedActions;
				storedActions = null;
				activity.modelDidFinishedLoad(this);
			}
		} catch (ParseExpressionException e) {
			e.printStackTrace();
			activity.modelFailedLoad(new Exception("无法识别主键"), this);
			return;
		}
	}

	@Override
	public Object getProcessResult() {
		return actions;
	}
}
