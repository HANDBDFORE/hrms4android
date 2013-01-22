package com.hand.hrms4android.util;


public interface Aggregate<E> {
	Iterator<E> createIterator();
}
