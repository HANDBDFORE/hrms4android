package com.hand.hrms4android.activity;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.hand.hrms4android.R;
import com.hand.hrms4android.exception.AuroraServerFailure;
import com.hand.hrms4android.exception.ParseExpressionException;
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

	private TextView titleTextView;
	private EditText usernameEditText;
	private EditText passwordEditText;
	private Button loginButton;
	private SharedPreferences mPreferences;
	private Animation shake;

	private ConfigReader configReader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// TODO 保留
		this.model = new LoginModel(0, this);
		configReader = XmlConfigReader.getInstance();

		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		bindAllViews();
		readConfig();
	}

	private void bindAllViews() {
		titleTextView = (TextView) findViewById(R.id.activity_login_textview_title);
		usernameEditText = (EditText) findViewById(R.id.activity_login_textedit_username);
		passwordEditText = (EditText) findViewById(R.id.activity_login_textedit_password);
		loginButton = (Button) findViewById(R.id.activity_login_textedit_loginButton);

		loginButton.setOnClickListener(new LoginButtonClickListener());
		passwordEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO) {
					doLogin();
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
		try {
			titleTextView.setText(configReader.getAttr(new Expression(
			        "/config/application/activity[@name='login_activity']/view/title_textview", "text")));
		} catch (ParseExpressionException pe) {
			pe.printStackTrace();
		}
	}

	@Override
	public void modelDidFinishedLoad(Model model) {
		startActivity(new Intent(this, FunctionListActivity.class));

		finish();
	}

	@Override
	public void modelFailedLoad(Exception e, Model model) {
		if (e.getCause() instanceof IOException) {
			Toast.makeText(this, "通信出现错误，请检查网络连接或服务", Toast.LENGTH_LONG).show();
		} else if (e.getCause() instanceof AuroraServerFailure) {
			Toast.makeText(this, e.getCause().getMessage(), Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "未知错误", Toast.LENGTH_LONG).show();
		}

		loginButton.setEnabled(true);
		loginButton.setText(R.string.activity_login_loginbutton_text);
	}

	private class LoginButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			doLogin();
		}
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

		// 记录此次登陆用户名
		Editor editor = mPreferences.edit();
		editor.putString(Constrants.SYS_PREFRENCES_USERNAME, username);
		editor.commit();

		RequestParams params = generateLoginParams(username, password);

		model.load(Model.LoadType.Network, params);
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
		params.put("langugae", "简体中文");
		params.put("user_language", "ZHS");
		params.put("is_ipad", "N");

		// 设备系统类型
		params.put("device_type", Constrants.SYS_ATTS_DEVICE_TYPE);

		// 消息推送token
		String token = mPreferences.getString(Constrants.SYS_PREFRENCES_PUSH_TOKEN, "");
		if (token.length() != 0) {
			params.put("device_token", token);
		}

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
		if (item.getItemId() == R.id.menu_settings) {
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
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
