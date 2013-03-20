package com.hand.hrms4android.listable.item;

public class DoneListItem extends TextListItem {
	private String screenName;

	public DoneListItem() {
	}

	public DoneListItem(String title, String titleRight, String subTitle, String information, String screenName) {
		super(title, titleRight, subTitle, information);
		this.screenName = screenName;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

}
