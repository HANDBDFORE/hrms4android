package com.hand.hrms4android.listable.doman;

public class Page {
	public int pageNumber;
	public int size;

	public Page() {
	}

	public Page(int stard, int size) {
		this.pageNumber = stard;
		this.size = size;
	}

	@Override
	public String toString() {
		return "start: " + pageNumber + ", size: " + size;
	}
}
