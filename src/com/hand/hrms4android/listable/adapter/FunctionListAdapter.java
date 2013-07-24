package com.hand.hrms4android.listable.adapter;

import static com.hand.hrms4android.listable.item.FunctionItem.OTHER_ITEM_ID;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hand.hrms4android.R;
import com.hand.hrms4android.listable.item.FunctionItem;
import com.hand.hrms4android.listable.item.FunctionSection;
import com.hand.hrms4android.util.imageLoader.AsyncListImageManager;
import com.hand.hrms4android.util.imageLoader.ScrollableViewImageLoadListener;

public class FunctionListAdapter extends BaseAdapter {
	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SEPARATOR = 1;

	private LayoutInflater mInflater;
	private List<Object> datas;
	private ListView mListView;
	private ImageLoadCompleteListener imageLoadListener;
	private AsyncListImageManager asyncImageManager;

	public FunctionListAdapter(Context context, List<Object> datas, ListView listview) {
		this.datas = datas;
		this.mListView = listview;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoadListener = new ImageLoadCompleteListener();
		asyncImageManager = new AsyncListImageManager();
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
				wrapper.getTitle().setCompoundDrawablesWithIntrinsicBounds(R.drawable.picture_placeholder, 0, 0, 0);
				// 然后尝试加载
				asyncImageManager.prepareLoadImageThread(Integer.valueOf(position), item.getImageUrl(),
				        imageLoadListener);
			} else {
				wrapper.getTitle().setCompoundDrawablesWithIntrinsicBounds(item.getIconRes(), 0, 0, 0);
			}

			wrapper.getTitle().setText(item.getText());

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

	/**
	 * [简要描述]: [详细描述]:
	 * 
	 * @author [Emerson]
	 * 
	 * @version [版本号,Aug 22, 2012]
	 */
	private class ImageLoadCompleteListener implements ScrollableViewImageLoadListener {
		@Override
		public void onImageLoad(Integer rowPosition, Bitmap bitmap) {
			// 找到图像所在行
			View row = mListView.findViewWithTag(rowPosition);
			if (row != null) {
				FunctionListCellWrapper wrapper = (FunctionListCellWrapper) row
				        .getTag(R.id.function_list_row_tag_wrapper);
				wrapper.getTitle()
				        .setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(bitmap), null, null, null);
			}
		}

		@Override
		public void onError(Integer rowPosition) {
			Log.d("onImageLoad", "onError! " + rowPosition);
		}
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
