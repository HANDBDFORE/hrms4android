package com.hand.hrms4android.activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.hand.hrms4android.R;

public class HTMLBaseActivity extends ActionBarActivity {
	protected WebView contentWebView;
	protected ProgressBar loadingProgress;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setCurrentContentView(savedInstanceState);

		contentWebView = (WebView) findViewById(R.id.html_base_activity_webview);
		contentWebView.setWebViewClient(new ContentWebClient());
		WebSettings webSettings = contentWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);

		loadingProgress = (ProgressBar) findViewById(R.id.html_base_activity_loading_progress);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		afterSuperOnCreateFinish(savedInstanceState);
	}

	/**
	 * 设定布局文件，必须是 继承自 activity_html_base的布局文件
	 * 
	 * @param activity
	 */
	protected void setCurrentContentView(Bundle savedInstanceState) {
		this.setContentView(R.layout.activity_html_base);
	}

	protected void afterSuperOnCreateFinish(Bundle savedInstanceState) {
	};

	private class ContentWebClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Uri query_string = Uri.parse(url);
			String query_scheme = query_string.getScheme();
			String query_host = query_string.getHost();

			if ((query_scheme.equalsIgnoreCase("https") || query_scheme.equalsIgnoreCase("http")) && query_host != null
			        && query_string.getQueryParameter("new_window") == null) {
				return false;// handle the load by webview
			}

			if (query_scheme.equalsIgnoreCase("tel")) {
				Intent intent = new Intent(Intent.ACTION_DIAL, query_string);
				startActivity(intent);
				return true;
			}

			if (query_scheme.equalsIgnoreCase("mailto")) {

				android.net.MailTo mailTo = android.net.MailTo.parse(url);

				// Create a new Intent to send messages
				// 系统邮件系统的动作为Android.content.Intent.ACTION_SEND

				Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
				sendIntent.setType("plain/text");

				// 设置邮件默认地址
				sendIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { mailTo.getTo() });
				// 调用系统的邮件系统
				try {
					startActivity(sendIntent);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(HTMLBaseActivity.this, "没有找到邮件客户端", Toast.LENGTH_SHORT).show();
				}
				return true;
			}
			return false;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			loadingProgress.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
