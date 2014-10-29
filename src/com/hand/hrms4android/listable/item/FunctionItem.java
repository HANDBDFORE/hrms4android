package com.hand.hrms4android.listable.item;

import com.hand.hrms4android.R;
import com.hand.hrms4android.application.HrmsApplication;

import 	android.content.Context;

public class FunctionItem {

	/**
	 * 待办事项
	 */
	public static final String TODO_ITEM_ID = "com.hand.hrms4android.activity.TodoListFragment";

	/**
	 * 已完成事项
	 */
	public static final String DONE_ITEM_ID = "com.hand.hrms4android.activity.DoneListFragment";

	/**
	 * 其他
	 */
	public static final String OTHER_ITEM_ID = "com.hand.hrms4android.activity.HTMLFragment";
	
	/**
	 *下载管理 
	 */
	public static final String DOWNLOAD_ITEM_ID	= "com.hand.hrms4android.activity.DownLoadListFragment";
	
	public static final FunctionItem todoItem = new FunctionItem(TODO_ITEM_ID,  HrmsApplication.getApplication().getResources().getString(R.string.item_todo), "bundle://envelope_info.png",
	        "", R.drawable.hd_ic_todo);
	public static final FunctionItem doneItem = new FunctionItem(DONE_ITEM_ID, HrmsApplication.getApplication().getResources().getString(R.string.item_done), "bundle://cancel_red.png", "",
	        R.drawable.hd_ic_done);
	
	public static final FunctionItem downItem = new FunctionItem(DOWNLOAD_ITEM_ID, HrmsApplication.getApplication().getResources().getString(R.string.download), "bundle://cancel_red.png", "",
	        R.drawable.hd_ic_done);
	
	private String functionId;
	private String text;
	private String imageUrl;
	private String url;
	private int iconRes;

	public FunctionItem() {

	}

	public FunctionItem(String functionId, String text, String imageUrl, String url) {
		this(functionId, text, imageUrl, url, 0);
	}

	public FunctionItem(String functionId, String text, String imageUrl, String url, int icoRes) {
		this.functionId = functionId;
		this.text = text;
		this.imageUrl = imageUrl;
		this.url = url;
		this.iconRes = icoRes;
	}

	public String getFunctionId() {
		return functionId;
	}

	public void setFunctionId(String functionId) {
		this.functionId = functionId;
	}

	public String getText(Context context) {
//		boolean flag = text.matches("\\d+");
//		if(flag){
//			String title = context.getString(Integer.parseInt(text));	
//			return title;
//		}else{
			return text;
//		}
		
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getIconRes() {
		return iconRes;
	}

	public void setIconRes(int iconRes) {
		this.iconRes = iconRes;
	}

}
