package com.hand.hrms4android.activity;

import org.apache.commons.lang3.StringUtils;

import android.os.Bundle;

import com.hand.hrms4android.R;
import com.hand.hrms4android.network.NetworkUtil;

public class HTMLActivity extends HTMLBaseActivity {

	@Override
	protected void setCurrentContentView(Bundle savedInstanceState) {
		this.setContentView(R.layout.activity_html_base);
	}

	@Override
	protected void afterSuperOnCreateFinish(Bundle savedInstanceState) {
		String url = getIntent().getStringExtra("url");
		contentWebView.loadUrl(NetworkUtil.getAbsoluteUrl(url.replace("${base_url}", "")));
		String title = getIntent().getStringExtra("title");
		if (!StringUtils.isEmpty(title)) {
			setTitle(title);
		}
	}

}
