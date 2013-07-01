package com.hand.hrms4android.listable.item;

import java.util.Map;

public class FunctionListItem extends BaseListItem {
	private String functionType;
	private String text;
	private String imageUrl;
	private String url;
	private String parentId;
	public static final String FUNCTION_LIST_ITEM_TYPE_ITEM = "ITEM";
	public static final String FUNCTION_LIST_ITEM_TYPE_SECTION = "SECTION";

	public FunctionListItem() {
	}

	public FunctionListItem(Map<String, String> record) {
		this(record.get("function_type"), record.get("text"), record.get("image_url"), record.get("url"), record
		        .get("parent_id"));
	}

	public FunctionListItem(String functionType, String text, String imageUrl, String url, String parentId) {
		this.functionType = functionType;
		this.text = text;
		this.imageUrl = imageUrl;
		this.url = url;
		this.parentId = parentId;
	}

	public String getFunctionType() {
		return functionType;
	}

	public void setFunctionType(String functionType) {
		this.functionType = functionType;
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

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

}
