package com.hand.hrms4android.listable.item;

// *****************CELL*************************
// * "title" here .......... "titleRight" here*
// *****************CELL*************************
public class SimpleTextListItem extends BaseListItem {
	/**
	 * 标题
	 */
	protected String title;

	/**
	 * 与标题同行的右侧
	 */
	protected String titleRight;

	public SimpleTextListItem() {
	}

	public SimpleTextListItem(String title, String titleRight) {
		this.title = title;
		this.titleRight = titleRight;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleRight() {
		return titleRight;
	}

	public void setTitleRight(String titleRight) {
		this.titleRight = titleRight;
	}

}
