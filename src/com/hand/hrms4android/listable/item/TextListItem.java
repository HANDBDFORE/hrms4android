package com.hand.hrms4android.listable.item;

// *****************CELL*************************
// * "title" ...... .......... "titleRight" ....*
// *---(inhered from superclass above)----------*
// * "subTitle" ................................*
// * "information"..............................*
// *****************CELL*************************
public class TextListItem extends SimpleTextListItem {

	protected String subTitle;
	protected String information;

	public TextListItem() {
		super();
	}

	public TextListItem(String subTitle, String information) {
		this.subTitle = subTitle;
		this.information = information;
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
