package com.hand.hrms4android.listable.adapter;

import static com.hand.hrms4android.listable.item.FunctionItem.OTHER_ITEM_ID;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hand.hrms4android.ems.R;
import com.hand.hrms4android.listable.item.FunctionItem;
import com.hand.hrms4android.listable.item.FunctionSection;

public class FunctionListAdapter extends BaseAdapter {
	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SEPARATOR = 1;

	private LayoutInflater mInflater;
	private List<Object> datas;
	private ListView mListView;

	private Context context;
	public FunctionListAdapter(Context context, List<Object> datas, ListView listview) {
		this.datas = datas;
		this.mListView = listview;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return datas.get(position).hashCode();
	}

	@Override
	public int getViewTypeCount() {
		return super.getViewTypeCount() + 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println("getViewgetViewgetViewgetViewgetViewgetViewgetViewgetView");
		int type = getItemViewType(position);

		View row = convertView;
		FunctionListCellWrapper wrapper = null;
		Object record = getItem(position);

		if (row == null) {
			switch (type) {
			case TYPE_ITEM: {
				row = mInflater.inflate(R.layout.menu_row_item, parent, false);
				wrapper = new FunctionListCellWrapper(row);
				break;
			}

			case TYPE_SEPARATOR: {
				row = mInflater.inflate(R.layout.menu_row_category, parent, false);
				wrapper = new FunctionListCellWrapper(row);
				break;
			}
			default:
				break;
			}

			row.setTag(R.id.function_list_row_tag_wrapper, wrapper);

		} else {
			wrapper = (FunctionListCellWrapper) row.getTag(R.id.function_list_row_tag_wrapper);
		}

		row.setTag(Integer.valueOf(position));
		row.setTag(R.id.function_list_row_tag_is_item, Boolean.valueOf(type == TYPE_ITEM));

		switch (type) {
		case TYPE_ITEM: {
			FunctionItem item = (FunctionItem) record;

			if (item.getFunctionId().equals(OTHER_ITEM_ID)) {
				// 设置默认图片
				wrapper.getTitle().setCompoundDrawablesWithIntrinsicBounds(R.drawable.hd_ic_other, 0, 0, 0);
			} else {
				wrapper.getTitle().setCompoundDrawablesWithIntrinsicBounds(item.getIconRes(), 0, 0, 0);
			}
			
			wrapper.getTitle().setText(item.getText(context));
//			String title = context.getString(Integer.parseInt(item.getText()));
//			wrapper.getTitle().setText(title);

			break;
		}

		case TYPE_SEPARATOR: {
			FunctionSection section = (FunctionSection) record;
			wrapper.getTitle().setText(section.getTitle());
			break;
		}
		default:
			break;
		}

		return row;
	}

	@Override
	public int getItemViewType(int position) {
		if (getItem(position) instanceof FunctionItem) {
			return TYPE_ITEM;
		} else {
			return TYPE_SEPARATOR;
		}
	}

	@Override
	public boolean isEnabled(int position) {
		return getItem(position) instanceof FunctionItem;
	}

	public void setDatas(List<Object> datas) {
		this.datas.clear();
		this.datas.addAll(datas);
		this.notifyDataSetChanged();
	}

}

class FunctionListCellWrapper {
	private View base;

	public FunctionListCellWrapper(View base) {
		this.base = base;
	}

	public TextView getTitle() {
		return (TextView) base;
	}
}
