package com.hand.hrms4android.util.data;

public class IndexPath {
	private int section;
	private int row;

	public IndexPath() {
	}

	public IndexPath(int section, int row) {
		this.section = section;
		this.row = row;
	}

	public IndexPath(IndexPath path) {
		this.setSection(path.getSection());
		this.setRow(path.getRow());
	}

	public int getSection() {
		return section;
	}

	public void setSection(int section) {
		this.section = section;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

}
