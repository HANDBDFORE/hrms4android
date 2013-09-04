package com.hand.hrms4android.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.hand.hrms4android.R;
import com.hand.hrms4android.listable.item.FunctionItem;
import com.hand.hrms4android.network.NetworkUtil;

public class HTMLFragment extends SherlockFragment implements OnFragmentSelectListener{

	protected WebView contentWebView;
	protected ProgressBar loadingProgress;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_html_base, container, false);
		bindAllViews(rootView);
		return rootView;
	}

	private void bindAllViews(View root) {
		contentWebView = (WebView) root.findViewById(R.id.html_base_activity_webview);
		contentWebView.setWebViewClient(new ContentWebClient());
		WebSettings webSettings = contentWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);

		loadingProgress = (ProgressBar) root.findViewById(R.id.html_base_activity_loading_progress);
		getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		String url = getArguments().getString("url");
		load(url);
	}

	/**
	 * @param url
	 */
    protected void load(String url) {
	    contentWebView.loadUrl(NetworkUtil.getAbsoluteUrl(url.replace("${base_url}", "")));
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
					startActivity(sendIntent);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(getSherlockActivity(), "没有找到邮件客户端", Toast.LENGTH_SHORT).show();
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
    public void onSelected(Object source) {
	    if (source instanceof FunctionItem) {
//	        load(((FunctionItem) source).getUrl());
	        getArguments().putString("url", ((FunctionItem) source).getUrl());
	        if (contentWebView!=null) {
	            load(((FunctionItem) source).getUrl());
            }
        }
    }
}
