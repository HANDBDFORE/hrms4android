package com.hand.hrms4android.activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;

import com.example.jpushdemo.ExampleUtil;
import com.hand.hrms4android.R;
import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.exception.ParseException;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.model.AbstractBaseModel;
import com.hand.hrms4android.model.AutoLoginModel;
import com.hand.hrms4android.model.LoadingModel;
import com.hand.hrms4android.model.LoginModel;
import com.hand.hrms4android.model.Model;
import com.hand.hrms4android.model.Model.LoadType;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.hand.hrms4android.util.Constrants;


public class LoadingActivity extends ActionBarActivity {

	private static final int MODEL_LOADING = 0;
	private static final int MODEL_AUTO_LOGIN = 1;

	private SharedPreferences mPreferences;
	private String baseUrl;

	private Button reloadButton;
	private TextView informationTextView;
	private ImageView alertImage;

	private AbstractBaseModel<Void> autoLoginModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		

		bindAllViews(); 
		
		HrmsApplication.getApplication().addActivity(this);
		
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		model = new LoadingModel(MODEL_LOADING, this);
		autoLoginModel = new AutoLoginModel(MODEL_AUTO_LOGIN, this);

		baseUrl = mPreferences.getString("sys_basic_url", "");
		
		if (!checkBaseUrl(baseUrl)) {
			// url地址不合理，弹出配置页面
			startSettingsActivity();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_loading, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		if (item.getItemId() == R.id.menu_settings) {
			startSettingsActivity();
		}
		return true;
	}

	private void bindAllViews() {
		informationTextView = (TextView) findViewById(R.id.activity_loading_infomation);
		reloadButton = (Button) findViewById(R.id.activity_loading_reload_button);
		alertImage = (ImageView) findViewById(R.id.activity_loading_alert);
		reloadButton.setOnClickListener(new ButtonClickListener());
		
		   
	}

	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("i am on resume");
		setViewAsNew();

		baseUrl = mPreferences.getString("sys_basic_url", "");
		if (checkBaseUrl(baseUrl)) {
			doReload();
		}
	}

	@Override
	public void modelDidFinishedLoad(Model model) {
		
		//每次获取超时时间
		try {
		String time  = XmlConfigReader.getInstance().getAttr(new Expression(
				        "/config/application/overtime",
				        "value"));
		
		try{
			int paraInt  =  Integer.parseInt(time);
			HrmsApplication.getApplication().execTime	= Integer.parseInt(time);
		}catch(NumberFormatException  e ){
			HrmsApplication.getApplication().enableTime = false;
			
		}
		

		
		
		} catch (ParseExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if (model.getModelId() == MODEL_LOADING) {

			
			
//			TODO 自动登录
			if (mPreferences.getString(Constrants.SYS_PREFRENCES_TOKEN, "").length() != 0) {
				System.out.println("auto login");
				autoLoginModel.load(LoadType.Network, getAutoLoginParams());
				return;
			}

			else {
				startLoginActivity();
				finish();
			}

		}

		if (model.getModelId() == MODEL_AUTO_LOGIN) {
			startFunctionListActivity();
			finish();
			return;
		}
	}

	/**
     * 
     */
	private void startLoginActivity() {
		Intent i = new Intent(this, LoginActivity.class);
		startActivity(i);
		this.finish();
	}

	private void startFunctionListActivity() {
		Intent i = new Intent(this, FunctionListActivity.class);
		startActivity(i);
		this.finish();
	}

	private Map<String, String> getAutoLoginParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put(Constrants.SYS_PREFRENCES_TOKEN, mPreferences.getString(Constrants.SYS_PREFRENCES_TOKEN, ""));
		
		String device_token = mPreferences.getString(Constrants.SYS_PREFRENCES_PUSH_TOKEN, "");
		params.put("device_type", Constrants.SYS_ATTS_DEVICE_TYPE);
		params.put(Constrants.SYS_ATTS_DEVICE_ID, ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
				.getDeviceId());
		
		if (device_token.length()!=0) {
			params.put("device_token", device_token);
        }
		return params;
	}

	@Override
	public void modelFailedLoad(Exception e, Model model) {
		setErrorViews();

		if (e instanceof IOException) {
			// 提示网络有问题
			informationTextView.setText("Network problems, please try again later, or adjust the Settings");
			Toast.makeText(this, "Network problems. Please try again later", Toast.LENGTH_LONG).show();
			return;
		}

		if (e instanceof ParseException) {
			informationTextView.setText("error occurs after reading configuration file,please check the configuration address");
			Toast.makeText(this, "error occurs after reading configuration file", Toast.LENGTH_LONG).show();
			// 配置文件错误，弹出配置界面
			startSettingsActivity();
			return;
		}

		// 自动登陆发生错误，跳转到手动登陆
		if (model.getModelId() == MODEL_AUTO_LOGIN) {
			startLoginActivity();
			return;
		}

	}

	private boolean checkBaseUrl(String url) {
		if (url == null) {
			return false;
		}

		if (url.length() == 0) {
			return false;
		}

		if (url.equals("http://")) {
			return false;
		}

		return true;
	}

	public void doReload() {
		System.out.println("doreload");
		setViewAsNew();
		model.load(LoadType.Network, baseUrl);
	}

	private void startSettingsActivity() {
		Intent i = new Intent(this, SettingsActivity.class);
		startActivity(i);
	}

	private class ButtonClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			System.out.println("in click");
			if (v.getId() == R.id.activity_loading_reload_button) {
				//
				doReload();
			}
		}
	}

	private void setErrorViews() {
		reloadButton.setVisibility(View.VISIBLE);
		alertImage.setVisibility(View.VISIBLE);
	}

	private void setViewAsNew() {
		informationTextView.setText(R.string.activity_loading_text);
		reloadButton.setVisibility(View.INVISIBLE);
		alertImage.setVisibility(View.INVISIBLE);
	}
}
