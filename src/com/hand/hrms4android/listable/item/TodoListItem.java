package com.hand.hrms4android.listable.item;

public class TodoListItem extends BaseListItem{
	private String title;
	private String date;
	private String subTitle;
	private String information;

	public TodoListItem() {
		this(null, null, null, null);
	}

	public TodoListItem(String title, String date, String subTitle, String information) {
		this.title = title;
		this.date = date;
		this.subTitle = subTitle;
		this.information = information;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

}
