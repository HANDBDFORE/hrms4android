package com.hand.hrms4android.model;

import java.util.List;
import java.util.Map;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.hand.hrms4android.util.Constrants;
import com.hand.hrms4android.util.LogUtil;
import com.loopj.android.http.HDJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class LoginModel extends AbstractModel {
	private ConfigReader configReader;

	public LoginModel(int id) {
		this(id, null);
	}

	public LoginModel(int id, ModelActivity activity) {
		super(id, activity);
		configReader = XmlConfigReader.getInstance();
	}

	@Override
	public void load(Model.LoadType loadType, Object param) {
		String service = "";
		try {
			service = configReader.getAttr(new Expression(
			        "/config/activity[@name='login_activity']/request/url[@name='login_submit_url']", "value"));
		} catch (ParseExpressionException e) {
			e.printStackTrace();
			activity.modelFailedLoad(new Exception("Cannot get url from config file! "), this);
			return;
		}

		NetworkUtil.removeAllCookies();

		NetworkUtil.post(service, (RequestParams) param, new HDJsonHttpResponseHandler() {

			@Override
			public void onFailure(Throwable error, String content) {
				LogUtil.error(this, "request", "onFailure:" + content);
				activity.modelFailedLoad(new Exception(error), LoginModel.this);
			}

			@Override
			public void onSuccess(int statusCode, List<Map<String, String>> ds) {

				// 存储相关数据
				LoginModel.this.storeSomething(ds.get(0));

				// // 设置请求头
				// LoginModel.this.updateRequestHeaders(ds.get(0));

				// 通知Activity已完成加载
				LoginModel.this.activity.modelDidFinishedLoad(LoginModel.this);
			}
		});
	}

	/**
	 * 将服务器返回的有效信息保存本地
	 * 
	 * @param record
	 */
	private void storeSomething(Map<String, String> record) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HrmsApplication.getApplication());
		Editor editor = preferences.edit();

		String sid = record.get("sid");
		String encryted_session_id = record.get("encryted_session_id");

		editor.putString(Constrants.SYS_PREFRENCES_SID, sid);
		editor.putString(Constrants.SYS_PREFRENCES_ENCRYTED_SESSION_ID, encryted_session_id);
		editor.commit();

	}

	// /**
	// * 将服务器返回有效信息存请求头
	// *
	// * @param responseData
	// */
	// private void updateRequestHeaders(Map<String, String> responseData) {
	// String sid = responseData.get("sid");
	// String encryted_session_id = responseData.get("encryted_session_id");
	//
	// NetworkUtil.addHeader("sid", sid);
	// NetworkUtil.addHeader("encryted_session_id", encryted_session_id);
	// }

	@Override
	public int getModelId() {
		return 0;
	}

}
