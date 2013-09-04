package com.hand.hrms4android.listable.item;

public class DoneListItem extends TextListItem {
	private String screenName;

	public DoneListItem() {
	}

	public DoneListItem(String item1, String item2, String item3, String item4, String screenName) {
		super(item1, item2, item3, item4);
		this.screenName = screenName;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

}
