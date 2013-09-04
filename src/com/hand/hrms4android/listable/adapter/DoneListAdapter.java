package com.hand.hrms4android.listable.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hand.hrms4android.R;
import com.hand.hrms4android.listable.item.DoneListItem;

public class DoneListAdapter extends ArrayAdapter<DoneListItem> {
	private LayoutInflater mInflater;

	public DoneListAdapter(Context context) {
		super(context, 0);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		DoneListCellWrapper wrapper = null;

		if (row == null) {
			row = mInflater.inflate(R.layout.done_list_cell, parent, false);
			wrapper = new DoneListCellWrapper(row);
			row.setTag(wrapper);
		} else {
			wrapper = (DoneListCellWrapper) row.getTag();
		}

		DoneListItem item = getItem(position);
		wrapper.getTitleTextView().setText(item.getTitle());
		wrapper.getTitleRightTextView().setText(item.getTitleRight());
		wrapper.getSubTitleTextView().setText(item.getSubTitle());
		wrapper.getInformationTextView().setText(item.getInformation());

		return row;
	}

}

/**
 * @author emerson
 * 
 */
class DoneListCellWrapper {
	private View base;
	private TextView titleTextView;
	private TextView titleRightTextView;
	private TextView subTitleTextView;
	private TextView informationTextView;

	public DoneListCellWrapper(View base) {
		this.base = base;
	}

	public TextView getTitleTextView() {
		if (titleTextView == null) {
			titleTextView = (TextView) base.findViewById(R.id.done_list_cell_title);
		}
		return titleTextView;
	}

	public TextView getTitleRightTextView() {
		if (titleRightTextView == null) {
			titleRightTextView = (TextView) base.findViewById(R.id.done_list_cell_date);
		}
		return titleRightTextView;
	}

	public TextView getSubTitleTextView() {
		if (subTitleTextView == null) {
			subTitleTextView = (TextView) base.findViewById(R.id.done_list_cell_subtitle);
		}
		return subTitleTextView;
	}

	public TextView getInformationTextView() {
		if (informationTextView == null) {
			informationTextView = (TextView) base.findViewById(R.id.done_list_cell_information);
		}
		return informationTextView;
	}

	public View getBase() {
		return base;
	}
}
