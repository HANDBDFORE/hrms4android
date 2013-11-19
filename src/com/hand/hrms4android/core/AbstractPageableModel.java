package com.hand.hrms4android.core;

import java.util.List;

import com.hand.hrms4android.util.Aggregate;
import com.hand.hrms4android.util.Iterator;
import com.hand.hrms4android.util.data.IndexPath;

public abstract class AbstractPageableModel<ListDataType> extends HDAbstractModel implements
        Iterator<ListDataType>, Aggregate<ListDataType> {

	protected IndexPath currentSelectedIndex;
	protected List<ListDataType> data;

	public AbstractPageableModel(int id, ModelViewController activity) {
		super(id, activity);
		this.currentSelectedIndex = new IndexPath(0, 0);
	}

	// ///////////////////////////////////////////////////
	// 实现迭代
	// ///////////////////////////////////////////////////
	@Override
	public void moveToFirst() {
		currentSelectedIndex.setSection(0);
		currentSelectedIndex.setRow(0);

	}

	@Override
	public void next() {
		int row = currentSelectedIndex.getRow();
		if (row < getSize()) {
			currentSelectedIndex.setRow(++row);
		}
	}

	@Override
	public boolean hasNext() {
		int row = currentSelectedIndex.getRow();
		if ((++row) >= getSize()) {
			return false;
		}
		return true;
	}

	@Override
	public ListDataType currentItem() {
		if (currentSelectedIndex.getRow() >= getSize()) {
			return null;
		}
		return getItemAtIndex(currentSelectedIndex.getRow());
	}

	@Override
	public boolean hasPrevious() {
		int row = currentSelectedIndex.getRow();
		if ((--row) < 0) {
			return false;
		}
		return true;
	}

	@Override
	public void previous() {
		int row = currentSelectedIndex.getRow();
		if (row > 0) {
			currentSelectedIndex.setRow(--row);
		}
	}

	@Override
	public Iterator<ListDataType> createIterator() {
		return this;
	}

	public void setRecordAsSelected(IndexPath selectedIndex) {
		this.currentSelectedIndex = new IndexPath(selectedIndex);
	}

	/**
	 * @return
	 */
	protected int getSize() {
		return data.size();
	}

	/**
	 * @return
	 */
	protected ListDataType getItemAtIndex(int index) {
		return data.get(index);
	}

}