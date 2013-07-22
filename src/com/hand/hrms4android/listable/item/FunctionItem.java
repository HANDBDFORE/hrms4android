package com.hand.hrms4android.listable.item;

public class FunctionItem {

	/**
	 * 待办事项
	 */
	public static final String TODO_ITEM_ID = "com.hand.hrms4android.activity.TodoListFragment";

	/**
	 * 已完成事项
	 */
	public static final String DONE_ITEM_ID = "dd";

	/**
	 * 其他
	 */
	public static final String OTHER_ITEM_ID = "com.hand.hrms4android.activity.HTMLFragment";

	private String functionId;
	private String text;
	private String imageUrl;
	private String url;

	public FunctionItem() {

	}

	public FunctionItem(String functionId, String text, String imageUrl, String url) {
		this.functionId = functionId;
		this.text = text;
		this.imageUrl = imageUrl;
		this.url = url;
	}

	public String getFunctionId() {
		return functionId;
	}

	public void setFunctionId(String functionId) {
		this.functionId = functionId;
	}

	public String getText() {
		return text;
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

}
