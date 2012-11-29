package com.hand.hrms4android.listable.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hand.hrms4android.R;

public class TodoListAdapter extends ArrayAdapter<Map<String, String>> {

	private LayoutInflater mLayoutInflater;

	public TodoListAdapter(Context context, int textViewResourceId, List<Map<String, String>> datas) {
		super(context, 0, datas);
		mLayoutInflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		TodoListCellWrapper wrapper = null;
		if (row == null) {
			row = mLayoutInflater.inflate(R.layout.todo_list_cell, parent, false);
			wrapper = new TodoListCellWrapper(row);
			row.setTag(wrapper);
		} else {
			wrapper = (TodoListCellWrapper) row.getTag();
		}

		Map<String, String> record = getItem(position);

		wrapper.getTitleTextView().setText(record.get("workflow_name"));
		wrapper.getDateTextView().setText(record.get("date_limit"));
		wrapper.getSubTitleTextView().setText(record.get("node_name"));
		wrapper.getInformationTextView().setText(record.get("workflow_desc"));

		return row;
	}

}

/**
 * @author emerson
 *
 */
class TodoListCellWrapper {
	private View base;

	private TextView titleTextView;
	private TextView dateTextView;
	private TextView subTitleTextView;
	private TextView informationTextView;

	public TodoListCellWrapper(View base) {
		this.base = base;
	}

	public TextView getTitleTextView() {
		if (titleTextView == null) {
			titleTextView = (TextView) base.findViewById(R.id.todo_list_cell_title);
		}
		return titleTextView;
	}

	public TextView getDateTextView() {
		if (dateTextView == null) {
			dateTextView = (TextView) base.findViewById(R.id.todo_list_cell_date);
		}
		return dateTextView;
	}

	public TextView getSubTitleTextView() {
		if (subTitleTextView == null) {
			subTitleTextView = (TextView) base.findViewById(R.id.todo_list_cell_subtitle);
		}
		return subTitleTextView;
	}

	public TextView getInformationTextView() {
		if (informationTextView == null) {
			informationTextView = (TextView) base.findViewById(R.id.todo_list_cell_information);
		}
		return informationTextView;
	}
}
