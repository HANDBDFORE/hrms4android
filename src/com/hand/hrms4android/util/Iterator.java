package com.hand.hrms4android.util;

public interface Iterator<E> {
	void moveToFirst();

	void next();

	boolean hasNext();

	E currentItem();

	void previous();

	boolean hasPrevious();
}
