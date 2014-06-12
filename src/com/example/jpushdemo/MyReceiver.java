package com.example.jpushdemo;

import org.json.JSONException;
import org.json.JSONObject;

import com.hand.hrms4android.activity.FunctionListActivity;
import com.hand.hrms4android.activity.LoadingActivity;
import com.hand.hrms4android.activity.LoginActivity;
import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.util.Constrants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";
   
	public MyReceiver() {
	}
	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            persistencePushToken(regId);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...
                        
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

            
            JPushInterface.reportNotificationOpened(context, bundle.getString(JPushInterface.EXTRA_MSG_ID));
            
    		SharedPreferences savedPreferences = PreferenceManager.getDefaultSharedPreferences(HrmsApplication
    		        .getApplication());
    		boolean LoginStatus = savedPreferences.getBoolean(Constrants.SYS_LOGIN_STATUS, false);
    		
    		if (LoginStatus){
	        	Intent i = new Intent(context, FunctionListActivity.class);
	        	i.putExtras(bundle);
	        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        	context.startActivity(i);
    		}else {
	        	Intent i = new Intent(context, LoadingActivity.class);
	        	i.putExtras(bundle);
	        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        	context.startActivity(i);
    			
    		}
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        	
        } else {
        	Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
	}


	private void persistencePushToken(String token) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(HrmsApplication.getApplication()).edit();

		editor.putString(Constrants.SYS_PREFRENCES_PUSH_TOKEN, token);
		editor.commit();

	}
	

}
