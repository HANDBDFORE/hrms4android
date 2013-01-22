package com.hand.hrms4android.widget;

import com.hand.hrms4android.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

	private void bindAllViews(Context context) {
		cardContent = (WebView) findViewById(R.id.dialog_employee_card_webview);
		loadingProgress = (ProgressBar) findViewById(R.id.dialog_employee_card_loading);
		// 设置缓存策略
		WebSettings webSettings = cardContent.getSettings();
		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

		cardContent.loadUrl(cardUrl);
		cardContent.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				loadingProgress.setVisibility(View.GONE);
			}
		});
	}
}
