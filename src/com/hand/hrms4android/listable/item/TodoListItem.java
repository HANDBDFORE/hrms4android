package com.hand.hrms4android.listable.item;

public class TodoListItem extends BaseListItem {

	private int id;
	private String title;
	private String date;
	private String subTitle;
	private String information;
	private String errorMessage;
	private String status;
	private boolean isLate;

	public TodoListItem() {
		this(0, null, null, null, null, null, null,false);
	}

	public TodoListItem(int id, String title, String date, String subTitle, String information, String errorMessage,
	        String status,boolean isLate) {
		super();
		this.id = id;
		this.title = title;
		this.date = date;
		this.subTitle = subTitle;
		this.information = information;
		this.errorMessage = errorMessage;
		this.status = status;
		this.isLate = isLate;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isLate() {
		return isLate;
	}

	public void setLate(boolean isLate) {
		this.isLate = isLate;
	}
}
