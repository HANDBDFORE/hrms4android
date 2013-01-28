package com.hand.hrms4android.activity;

import java.io.IOException;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hand.hrms4android.R;
import com.hand.hrms4android.exception.ParseException;
import com.hand.hrms4android.model.LoadingModel;
import com.hand.hrms4android.model.Model;
import com.hand.hrms4android.model.Model.LoadType;

public class LoadingActivity extends ActionBarActivity {
	private SharedPreferences mPreferences;
	private String baseUrl;

	private Button reloadButton;
	private TextView informationTextView;
	private ImageView alertImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		bindAllViews();

		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		model = new LoadingModel(0, this);

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

		setViewAsNew();

		baseUrl = mPreferences.getString("sys_basic_url", "");
		if (checkBaseUrl(baseUrl)) {
			doReload();
		}
	}

	@Override
	public void modelDidFinishedLoad(Model model) {
		Intent i = new Intent(this, LoginActivity.class);
		startActivity(i);
		this.finish();
	}

	@Override
	public void modelFailedLoad(Exception e, Model model) {
		setErrorViews();

		if (e instanceof IOException) {
			// 提示网络有问题
			informationTextView.setText("网络存在问题，请稍后再试，或者调整设置");
			Toast.makeText(this, "网络存在问题，请稍后再试", Toast.LENGTH_LONG).show();
			return;
		}

		else if (e instanceof ParseException) {
			informationTextView.setText("配置文件读取后发生错误，请检查配置地址");
			Toast.makeText(this, "配置文件读取后发生错误，请检查配置地址", Toast.LENGTH_LONG).show();
			// 配置文件错误，弹出配置界面
			startSettingsActivity();
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
