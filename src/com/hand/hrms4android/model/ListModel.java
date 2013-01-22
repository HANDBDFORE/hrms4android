package com.hand.hrms4android.model;

import java.util.Map;

import com.hand.hrms4android.util.data.IndexPath;

public interface ListModel<E extends Map<String, String>> extends Model {
	public boolean deleteData(E record, IndexPath indexpath);
}
