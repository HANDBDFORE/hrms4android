package com.hand.hrms4android.core;

public class Page {
	public int start;
	public int end;

	public Page() {
	}

	public Page(int stard, int end) {
		this.start = stard;
		this.end = end;
	}

	@Override
	public String toString() {
		return "start: " + start + ", end: " + end;
	}
}
