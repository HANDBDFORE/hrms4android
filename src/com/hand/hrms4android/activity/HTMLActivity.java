package com.hand.hrms4android.activity;

import org.apache.commons.lang3.StringUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.hand.hrms4android.R;
import com.hand.hrms4android.network.NetworkUtil;

public class HTMLActivity extends Activity {
	private WebView contentView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_html);

		contentView = (WebView) findViewById(R.id.html_activity_webview);
		contentView.setWebViewClient(new ContentWebClient());
		WebSettings webSettings = contentView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		String url = getIntent().getStringExtra("url");
		contentView.loadUrl(NetworkUtil.getAbsoluteUrl(url.replace("${base_url}", "")));

		String title = getIntent().getStringExtra("title");
		if (!StringUtils.isEmpty(title)) {
			setTitle(title);
		}
	}

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
					startActivity(Intent.createChooser(sendIntent, "请选择邮件发送软件"));
				} catch (ActivityNotFoundException e) {
					Toast.makeText(HTMLActivity.this, "无法找到邮件客户端", Toast.LENGTH_LONG).show();
				}
				return true;
			}

			return false;

		}
	}

}
