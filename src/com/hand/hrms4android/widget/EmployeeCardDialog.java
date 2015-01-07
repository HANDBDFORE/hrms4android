package com.hand.hrms4android.widget;

import com.hand.hrms4android.ems.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class EmployeeCardDialog extends Dialog {
	private WebView cardContent;
	private ProgressBar loadingProgress;
	private String cardUrl;

	public EmployeeCardDialog(Context context, String cardUrl, int theme) {
		super(context);
		setContentView(R.layout.dialog_employee_card);
		setTitle("员工名片");
		this.cardUrl = cardUrl;
		bindAllViews(context);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void bindAllViews(Context context) {
		cardContent = (WebView) findViewById(R.id.dialog_employee_card_webview);
		loadingProgress = (ProgressBar) findViewById(R.id.dialog_employee_card_loading);
		// 设置缓存策略
		WebSettings webSettings = cardContent.getSettings();
		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webSettings.setJavaScriptEnabled(true);
		cardContent.loadUrl(cardUrl);
		cardContent.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if (newProgress == 100) {
					loadingProgress.setVisibility(View.GONE);
				}
			}
		});
	}
}
