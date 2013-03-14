package com.hand.hrms4android.listable.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hand.hrms4android.R;
import com.hand.hrms4android.listable.item.DoneListItemFactory;
import com.hand.hrms4android.listable.item.ItemFactory;
import com.hand.hrms4android.listable.item.TextListItem;
import com.hand.hrms4android.model.Model;
import com.hand.hrms4android.parser.xml.XmlConfigReader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DoneListAdapter extends BaseAdapter {
	private Model model;
	private LayoutInflater mInflater;
	private ItemFactory<TextListItem> itemFactory;
	private List<TextListItem> items;

	public DoneListAdapter(Context context, Model model) {
		this.model = model;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		items = new ArrayList<TextListItem>();
		itemFactory = new DoneListItemFactory(XmlConfigReader.getInstance());

	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public TextListItem getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return items.get(position).getId();
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

		TextListItem item = getItem(position);
		wrapper.getTitleTextView().setText(item.getTitle());
		wrapper.getTitleRightTextView().setText(item.getTitleRight());
		wrapper.getSubTitleTextView().setText(item.getSubTitle());
		wrapper.getInformationTextView().setText(item.getInformation());

		return row;
	}

	public void reFetchData() {
		List<TextListItem> newItems = itemFactory.getItemList(model.getAuroraDataset());
		this.items = newItems;
		this.notifyDataSetChanged();
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
