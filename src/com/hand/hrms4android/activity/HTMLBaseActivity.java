package com.hand.hrms4android.activity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.hand.hrms4android.R;
import com.hand.hrms4android.application.HrmsApplication;


@SuppressLint("JavascriptInterface")
public class HTMLBaseActivity extends ActionBarActivity {
	protected WebView contentWebView;
	protected ProgressBar loadingProgress;
	 
	private DownloadManager manager;
	private Handler handler;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setCurrentContentView(savedInstanceState);

		contentWebView = (WebView) findViewById(R.id.html_base_activity_webview);
		contentWebView.setWebViewClient(new ContentWebClient());

		
		contentWebView.addJavascriptInterface(this, "activity");
		
		
		WebSettings webSettings = contentWebView.getSettings();	
		webSettings.setJavaScriptEnabled(true);
		
		contentWebView.setWebChromeClient(new AlertWebChromeClient());
		
		loadingProgress = (ProgressBar) findViewById(R.id.html_base_activity_loading_progress);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		HrmsApplication.getApplication().addActivity(this);
		//add by jtt	
		 this.manager =(DownloadManager)getSystemService("download");
		 this.handler =new Handler(){
				@Override
				//hand message
				public void handleMessage(Message msg){
					super.handleMessage(msg); 
				}
			};	
			
		 contentWebView.setDownloadListener(new DownloadListener(){
			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				  	//do the download
					new  downloadthread(url).start();
				  

				
			}
			
			
		}); 
		 
		//add by jtt 
		afterSuperOnCreateFinish(savedInstanceState);
	}
	
	@Override
	@JavascriptInterface
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
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
			
			//每次网络请求初始化 timer;
			HrmsApplication.getApplication().initTimer();
			
			loadingProgress.setVisibility(View.GONE);
		}
	}

	
	
	private class AlertWebChromeClient extends WebChromeClient{
		@Override
	    public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
	    {
	        new AlertDialog.Builder(HTMLBaseActivity.this)
	            .setTitle("")
	            .setMessage(message)
	            .setPositiveButton(android.R.string.ok,
	                    new AlertDialog.OnClickListener()
	                    {
	                        public void onClick(DialogInterface dialog, int which)
	                        {
	                            result.confirm();
	                        }
	                    })
	            .setCancelable(false)
	            .create()
	            .show();

	        return true;
	    };
	    
	    @Override
	    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
	        new AlertDialog.Builder(HTMLBaseActivity.this)
	        .setTitle("")
	        .setMessage(message)
	        .setPositiveButton(android.R.string.ok,
	                new DialogInterface.OnClickListener()
	        {
	            public void onClick(DialogInterface dialog, int which)
	            {
	                result.confirm();
	            }
	        })
	        .setNegativeButton(android.R.string.cancel,
	                new DialogInterface.OnClickListener()
	        {
	            public void onClick(DialogInterface dialog, int which)
	            {
	                result.cancel();
	            }
	        })
	        .create()
	        .show();

	        return true;
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
	 
	 
    
    //add by jtt
	public class downloadthread extends Thread{
		String url;
		public downloadthread(String url){
			this.url  =  url;
			
		}
		 
		public void run(){
			URL myURL;
			String  filename = null;
			Uri uri;
			long lastDownloadId;
			try {
				myURL = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(5000);
				conn.connect(); 
				int responseCode = conn.getResponseCode();  
				if(responseCode == 200){
					
				  filename = new String(conn.getHeaderField("Content-Disposition").getBytes("UTF-8"), "iso-8859-1");
				 
				  filename = filename.substring(filename.indexOf('\"')+1,filename.lastIndexOf('\"'));
				  uri = Uri.parse(url);   
				  
				  Environment.getExternalStoragePublicDirectory(DownLoadListFragment.DOWNLOAD_DIR).mkdir();
				  
				  DownloadManager.Request down=new DownloadManager.Request(uri);
				  //enable wifi and NETWORK_MOBILE download
				  down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);  

				  down.setDestinationInExternalPublicDir( DownLoadListFragment.DOWNLOAD_DIR, filename);  
				  lastDownloadId = manager.enqueue(down); 
				}
				//send empty message
				Message msg = new Message();  
                handler.sendMessage(msg); 
                
                
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block 
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		//add by jtt
	}
}
