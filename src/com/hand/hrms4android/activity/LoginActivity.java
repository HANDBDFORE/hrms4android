package com.hand.hrms4android.activity;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;

import com.actionbarsherlock.view.MenuItem;
import com.cfca.srcbulanview.SigActivity;
import com.cfist.mobile.ulan.UlanKey;
import com.cfist.mobile.ulan.util.Consts;
import com.hand.hrms4android.ems.R;
import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.exception.AuroraServerFailure;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.model.CheckNumModel;
import com.hand.hrms4android.model.LoginModel;
import com.hand.hrms4android.model.Model;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.hand.hrms4android.util.Constrants;
import com.hand.hrms4android.util.StorageUtil;
import com.loopj.android.http.RequestParams;

public class LoginActivity extends ActionBarActivity {

	private static final int ACTIVITY_SETTINGS = 1;
	public static final String KEY_INTERCEPTED = "intercept";

	private TextView titleTextView;
	private EditText usernameEditText;
	private EditText passwordEditText;
	private Button loginButton;
	private SharedPreferences mPreferences;
	private Animation shake;
   
	private String p_res;
	private String key_id;
	/*加密*/
	private int connectType;
	String certType;
	String signHash;
	String pin;
	String signFormat;	
	
	RequestParams loginParams;
	/* 两次退出 */
	boolean mFlag;
	
	boolean isLogining = false;
	

	private ConfigReader configReader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		isIntercepted();
		
		
		setContentView(R.layout.activity_login);
		// TODO 保留
//		this.model = new LoginModel(0, this);
		this.model = new CheckNumModel(0, this);
		configReader = XmlConfigReader.getInstance();

		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		HrmsApplication.getApplication().addActivity(this);
		bindAllViews();
		readConfig();
	}

	

	@Override
	protected void onResume() {
		// TODO 自动生成的方法存根
		super.onResume();
		if(isLogining == false)
			resetButton();
	}



	private void bindAllViews() {
//		titleTextView = (TextView) findViewById(R.id.activity_login_textview_title);
		usernameEditText = (EditText) findViewById(R.id.activity_login_textedit_username);
		passwordEditText = (EditText) findViewById(R.id.activity_login_textedit_password);
		loginButton = (Button) findViewById(R.id.activity_login_textedit_loginButton);

		loginButton.setOnClickListener(new LoginButtonClickListener());
		passwordEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO) {
					doCheck();
					return true;
				}

				return false;
			}
		});

		String storedUsername = mPreferences.getString(Constrants.SYS_PREFRENCES_USERNAME, "");
		usernameEditText.setText(storedUsername);

		shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
	}

	private void readConfig() {
//		try {
//			titleTextView.setText(configReader.getAttr(new Expression(
//			        "/config/application/activity[@name='login_activity']/view/title_textview", "text")));
//		} catch (ParseExpressionException pe) {
//			pe.printStackTrace();
//		}
	}

	@Override
	public void modelDidFinishedLoad(Model model) {
	
		if(model instanceof CheckNumModel){
			p_res = ((CheckNumModel) model).getRes();
			key_id = ((CheckNumModel) model).getKeyId();
			HrmsApplication.getApplication().setKeyId(key_id);
			if(p_res.equals("0") || p_res.equals("1")){
				doLogin();
			}
//			loginButton.setEnabled(true);
//			loginButton.setText(R.string.activity_login_loginbutton_text);	
			return;
		}
		
		if (isIntercepted()) {
			//某操作被拦截，被迫弹出登录页面，登录完成后结束自身即可
			finish();
        }else{
        	//正常进入,启动功能列表
        	if(((LoginModel)model).getCode().equals("ok")){
        		startActivity(new Intent(this, FunctionListActivity.class));
        	}else{
        		Toast.makeText(LoginActivity.this, "Error！", Toast.LENGTH_SHORT).show();
        	}
        	 
			
			
        	finish();
        }

		
	}

	@Override
	public void modelFailedLoad(Exception e, Model model) {
		if (e.getCause() instanceof IOException) {
			Toast.makeText(this, getResources().getString(R.string.activity_login_communication_error), Toast.LENGTH_LONG).show();
		} else if (e.getCause() instanceof AuroraServerFailure) {
			Toast.makeText(this, e.getCause().getMessage(), Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, getResources().getString(R.string.activity_login_unknown_error), Toast.LENGTH_LONG).show();
		}
		isLogining = false;
		resetButton();
		
	
	}

	private class LoginButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			
			doCheck();
			
//			doLogin();
		}
	}

	private void doCheck(){
		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		// 检查
		if (StringUtils.isEmpty(username)) {
			usernameEditText.requestFocus();
			usernameEditText.startAnimation(shake);
			return;
		}	
		if (StringUtils.isEmpty(password)) {
			passwordEditText.requestFocus();
			passwordEditText.startAnimation(shake);
			return;
		}
		loginButton.setEnabled(false);
		loginButton.setText(R.string.activity_login_loginbutton_loading);
		RequestParams params = generateCheckParams(username);
		if(!(model instanceof CheckNumModel)){
			model = new CheckNumModel(0, LoginActivity.this);
		}
		model.load(Model.LoadType.Network, params);
		
	}
	
	private void doLogin() {
		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		

		// 检查
		if (StringUtils.isEmpty(username)) {
			usernameEditText.requestFocus();
			usernameEditText.startAnimation(shake);
			return;
		}
		if (StringUtils.isEmpty(password)) {
			passwordEditText.requestFocus();
			passwordEditText.startAnimation(shake);
			return;
		}
		// 检查结束

		loginButton.setEnabled(false);
		loginButton.setText(R.string.activity_login_loginbutton_loading);

		// 对比登录的帐号是否和上次相同
		String lastUserName = mPreferences.getString(Constrants.SYS_PREFRENCES_USERNAME, "");

		// 如果不同
		if (!lastUserName.equals(username)) {
			StorageUtil.deleteDB();
		}

		// 
		Editor editor = mPreferences.edit();
		editor.putString(Constrants.SYS_PREFRENCES_USERNAME, username);
		//初始化token
		editor.putString(Constrants.SYS_PREFRENCES_PUSH_TOKEN, JPushInterface.getRegistrationID(this.getApplicationContext()));
		editor.commit();

		RequestParams params = generateLoginParams(username, password);
		if(!(model instanceof LoginModel)){
			model = new LoginModel(0, LoginActivity.this);
		}
		if (p_res.equals("1")) {
			//检查PIN
			byte[] data = "abc".getBytes();
			//连接方式
			connectType = UlanKey.BLE;
			//证书类型
			certType = Consts.ALGORITHM_RSA2048;
			//hash算法
			signHash = Consts.ALGORITHM_SHA1;
			//签名格式
			signFormat = Consts.SignFormat_PKCS7Att;
			
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, SigActivity.class);
			
			intent.putExtra(SigActivity.SIGNATURE_CONNECT_TYPE, connectType);
			if(certType == null) {
				intent.putExtra(SigActivity.SIGNATURE_ACTION, SigActivity.ACTION_SIGN_AUTO);
			}
			else {
				intent.putExtra(SigActivity.SIGNATURE_ACTION, SigActivity.ACTION_SIGN_MANUAL);
				intent.putExtra(SigActivity.SIGNATURE_CERT_TYPE, certType);
				intent.putExtra(SigActivity.SIGNATURE_HASH, signHash);
			}
											
			intent.putExtra(SigActivity.SIGNATURE_DATA, data);
			intent.putExtra(SigActivity.SIGNATURE_KEYID, key_id);
			intent.putExtra(SigActivity.SIGNATURE_FORMAT, signFormat);
			LoginActivity.this.startActivityForResult(intent, 0);
			loginParams = params;
		}else{
			model.load(Model.LoadType.Network, params);
		}
		
	}
	
	/**
	 * 检查是否需要加密
	 * @param uesrname
	 * @return
	 */
	private RequestParams generateCheckParams(String username){
		RequestParams params = new RequestParams();
		params.put("username", username);
		return params;
		
	}
	
	/**
	 * 组装登陆请求使用的params
	 * 
	 * @param username
	 * @param password
	 * @return 组装好的参数
	 */
	private RequestParams generateLoginParams(String username, String password) {
		// 拼参数
		
		RequestParams params = new RequestParams();
		params.put("user_name", username);
		params.put("user_password", password);
		// 系统语言环境
		String language = getResources().getConfiguration().locale.getLanguage();
		params.put("language", language);
		// 设备系统类型
		params.put("device_type", Constrants.SYS_ATTS_DEVICE_TYPE);

		// 消息推送token
		String token = mPreferences.getString(Constrants.SYS_PREFRENCES_PUSH_TOKEN, "");
		if (token.length() != 0) {
			params.put("push_token", token);
		}else{
			params.put("push_token", "-1");


		}
		
		System.out.println(token);
		
		// 设备imei，作为设备ID使用
		params.put(Constrants.SYS_ATTS_DEVICE_ID,
		        ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId());
		return params;
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_login, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		if (item.getItemId() == R.id.setting) {
			Intent i = new Intent(this, SettingsActivity.class);
			startActivityForResult(i, ACTIVITY_SETTINGS);
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTIVITY_SETTINGS) {
			if (resultCode == RESULT_OK) {
				// 改变了地址，结束自身，回到loading页
				startActivity(new Intent(this, LoadingActivity.class));
				finish();
			}
		}else if(requestCode == 0){
			if(data == null) return;
			if(!(model instanceof LoginModel)){
				model = new LoginModel(0, LoginActivity.this);
			}
			if(data.getBooleanExtra(SigActivity.SIGNATURE_SUCCESS, false)){
				isLogining = true;
				if(p_res.equals("1")){
					loginParams.put("ca_verification_necessity", "1"); 
					loginParams.put("Signature", data.getStringExtra(SigActivity.SIGNATURE_RESULT));
				}
				String pinCode = data.getStringExtra(SigActivity.SIGNATURE_PINCODE);
				HrmsApplication.getApplication().setPinCode(pinCode);
				model.load(Model.LoadType.Network, loginParams);
			}else{
				HrmsApplication.getApplication().setPinCode(null);
				showErrorMsg();
			}
			
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void showErrorMsg(){
		Toast.makeText(LoginActivity.this, "有错误发生，请检查BlueTooth是否打开", Toast.LENGTH_SHORT).show();
		resetButton();
	}
	private void resetButton(){
		loginButton.setEnabled(true);
		loginButton.setText(R.string.activity_login_loginbutton_text);
	}
	/**
	 * 
	 */
    private boolean isIntercepted() {
	    Intent startIntent = getIntent();
		return startIntent.getBooleanExtra(KEY_INTERCEPTED, false);
    }  
    
}
