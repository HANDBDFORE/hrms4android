package com.hand.hrms4android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.util.Constrants;
import com.hand.hrms4android.util.LogUtil;
import com.igexin.sdk.Consts;

public class HDMessageReceiver extends BroadcastReceiver {
	public HDMessageReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		LogUtil.debug(this, "IGetuiMessageReceiver", "onReceive() action=" + bundle.getInt("action"));

		switch (bundle.getInt(Consts.CMD_ACTION)) {
		case Consts.GET_MSG_DATA:
			// 获取透传(payload)数据
			break;
		case Consts.GET_CLIENTID:
			// 获取 ClientID(CID)
			String cid = bundle.getString("clientid");
			LogUtil.debug(this, "IGetuiMessageReceiver", "Got ClientID:" + cid);

			persistencePushToken(cid);

			break;
		case Consts.BIND_CELL_STATUS:
			break;
		default:
			break;
		}
	}

	private void persistencePushToken(String token) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(HrmsApplication.getApplication()).edit();

		editor.putString(Constrants.SYS_PREFRENCES_PUSH_TOKEN, token);
		editor.commit();

	}

	// private void registerDeviceTokenOnServer(String token){
	// NetworkUtil.post(url, params, responseHandler);
	// }
	//
	// private String getTokenRegisterUrl(String pattern){
	//
	// }
}
