package com.hand.hrms4android.listable.item;

public class FunctionItem {
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
