package com.hand.hrms4android.activity;
 
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
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
	
	//add by jtt 
	private DownloadManager manager;
	private Handler handler;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_html_base, container, false);
		bindAllViews(rootView);
		return rootView;
	}

	private void bindAllViews(View root) {
		contentWebView = (WebView) root.findViewById(R.id.html_base_activity_webview);
		contentWebView.setWebViewClient(new ContentWebClient());
		contentWebView.setWebChromeClient(new AlertWebChromeClient());
		WebSettings webSettings = contentWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		

		loadingProgress = (ProgressBar) root.findViewById(R.id.html_base_activity_loading_progress);
		getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		//add by jtt
		 this.manager =(DownloadManager)getActivity().getSystemService("download");
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
	
	private class AlertWebChromeClient extends WebChromeClient{
		@Override
	    public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
	    {
	        new AlertDialog.Builder(HTMLFragment.this.getActivity())
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
	        new AlertDialog.Builder(HTMLFragment.this.getActivity())
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
    public void onSelected(Object source) {
	    if (source instanceof FunctionItem) {
//	        load(((FunctionItem) source).getUrl());
	        getArguments().putString("url", ((FunctionItem) source).getUrl());
	        if (contentWebView!=null) {
	            load(((FunctionItem) source).getUrl());
            }
        }
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
				  //download at  /sdcard/download
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
	}
    //add by jtt
}
