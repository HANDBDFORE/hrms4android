package com.hand.hrms4android.widget;

//import android.R;
import com.hand.hrms4android.R;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class ContentWebClient extends WebViewClient {

	private Context context;
	private OnDetailPageFinishedListener finishedListener;

	public ContentWebClient(Context context) {
		this.context = context;
	}

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
			context.startActivity(intent);
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
				context.startActivity(sendIntent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(context, context.getString(R.string.activity_html_base_no_email_client), Toast.LENGTH_SHORT).show();
			}
			return true;
		}
		return false;
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
		finishedListener.onPageFinished(view, url);
	}

	public interface OnDetailPageFinishedListener {
		public void onPageFinished(WebView view, String url);

	}

	public void setFinishedListener(OnDetailPageFinishedListener finishedListener) {
		this.finishedListener = finishedListener;
	}

}