package com.hand.hrms4android.model;

import java.util.ArrayList;
import java.util.Map;

import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.util.Iterator;
import com.hand.hrms4android.util.data.IndexPath;

public abstract class AbstractBasePageableModel extends AbstractPageableModel<Map<String, String>> {
	protected IndexPath currentSelectedIndex;

	public AbstractBasePageableModel(int id, ModelActivity activity) {
		super(id, activity);
		this.loadAuroraDataset = new ArrayList<Map<String, String>>();
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
		if (row < loadAuroraDataset.size()) {
			currentSelectedIndex.setRow(++row);
		}
	}

	@Override
	public boolean hasNext() {
		int row = currentSelectedIndex.getRow();
		if ((++row) >= loadAuroraDataset.size()) {
			return false;
		}
		return true;
	}

	@Override
	public Map<String, String> currentItem() {
		if (currentSelectedIndex.getRow() >= loadAuroraDataset.size()) {
			return null;
		}
		return loadAuroraDataset.get(currentSelectedIndex.getRow());
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
	public Iterator<Map<String, String>> createIterator() {
		return this;
	}

	public void setRecordAsSelected(IndexPath selectedIndex) {
		this.currentSelectedIndex = new IndexPath(selectedIndex);
	}
}
