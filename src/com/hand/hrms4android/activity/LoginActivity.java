package com.hand.hrms4android.activity;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hand.hrms4android.R;
import com.hand.hrms4android.model.LoginModel;
import com.hand.hrms4android.model.Model;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.exception.ParseExpressionException;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.loopj.android.http.HDJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class LoginActivity extends BaseActivity {

	private TextView titleTextView;
	private EditText usernameEditText;
	private EditText passwordEditText;
	private Button loginButton;

	private ConfigReader configReader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		//
		this.model = new LoginModel(this);
		configReader = XmlConfigReader.getInstance();
		bindAllViews();
		readConfig();
	}

	private void bindAllViews() {
		titleTextView = (TextView) findViewById(R.id.activity_login_textview_title);
		usernameEditText = (EditText) findViewById(R.id.activity_login_textedit_username);
		passwordEditText = (EditText) findViewById(R.id.activity_login_textedit_password);
		loginButton = (Button) findViewById(R.id.activity_login_textedit_loginButton);

		loginButton.setOnClickListener(new LoginButtonClickListener());
	}

	private void readConfig() {
		try {
			titleTextView.setText(configReader.getAttr(new Expression(
			        "/config/activity[@name='login_activity']/view/title_textview", "text")));
		} catch (ParseExpressionException pe) {
			pe.printStackTrace();
		}
	}

	@Override
	public void modelDidFinishedLoad(Model model) {
		startActivity(new Intent(this, TodoListActivity.class));
	}

	private class LoginButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			String username = usernameEditText.getText().toString();
			String password = passwordEditText.getText().toString();

			RequestParams params = new RequestParams();
			params.put("user_name", username);
			params.put("user_password", password);
			params.put("langugae", "简体中文");
			params.put("user_language", "ZHS");
			params.put("is_ipad", "N");
			params.put("device_type", "Android");
			params.put("company_id", "1");
			params.put("role_id", "41");

			model.load(Model.LoadType.Network, params);
		}
	}
	

	public void oneTest(View v) {
		NetworkUtil.post("/autocrud/ios.ios_test.ios_todo_list_test/query?_fetchall=true&amp;_autocount=false", null, new HDJsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, List<Map<String, String>> dataset) {
			    super.onSuccess(statusCode, dataset);
			    Log.i("success", "result :"+dataset.toString());
			}
			
			@Override
			public void onFailure(Throwable error, String content) {
			    super.onFailure(error, content);
			    Log.i("onFailure", "reason :"+content);
			}
		});
	}

}
