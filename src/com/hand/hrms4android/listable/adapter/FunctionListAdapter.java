package com.hand.hrms4android.listable.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hand.hrms4android.R;
import com.hand.hrms4android.listable.item.FunctionListItem;
import com.hand.hrms4android.util.imageLoader.AsyncListImageManager;
import com.hand.hrms4android.util.imageLoader.ScrollableViewImageLoadListener;

public class FunctionListAdapter extends BaseAdapter {
	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SEPARATOR = 1;

	private LayoutInflater mInflater;
	private List<FunctionListItem> datas;
	private ListView mListView;
	private ImageLoadCompleteListener imageLoadListener;
	private AsyncListImageManager asyncImageManager;

	public FunctionListAdapter(Context context, List<FunctionListItem> datas, ListView listview) {
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
	public FunctionListItem getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return datas.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		// System.out.println("getView " + position + " " + convertView +
		// " type = " + type);

		View row = convertView;
		FunctionListCellWrapper wrapper = null;
		FunctionListItem record = getItem(position);

		if (row == null) {
			switch (type) {
			case TYPE_ITEM: {
				row = mInflater.inflate(R.layout.function_list_cell, parent, false);
				wrapper = new FunctionListCellWrapper(row);
				break;
			}

			case TYPE_SEPARATOR: {
				row = mInflater.inflate(R.layout.function_list_separator, parent, false);
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
			// 设置默认图片
			wrapper.getImage().setImageResource(R.drawable.picture_placeholder);
			// 然后尝试加载
			asyncImageManager
			        .prepareLoadImageThread(Integer.valueOf(position), record.getImageUrl(), imageLoadListener);
		}

		case TYPE_SEPARATOR: {
			wrapper.getTitle().setText(record.getText());
			break;
		}
		default:
			break;
		}

		return row;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).getFunctionType().equalsIgnoreCase("SECTION") ? TYPE_SEPARATOR : TYPE_ITEM;
	}

	@Override
	public boolean isEnabled(int position) {
		return getItem(position).getFunctionType().equalsIgnoreCase("ITEM");
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
				// TODO 临时解决方案，有点乱
				if (wrapper.getImage() != null) {
					wrapper.getImage().setImageBitmap(bitmap);
				}
			}
		}

		@Override
		public void onError(Integer rowPosition) {
			Log.d("onImageLoad", "onError! " + rowPosition);
		}
	}

	public void setDatas(List<FunctionListItem> datas) {
		this.datas = datas;
	}
}

class FunctionListCellWrapper {
	private View base;
	private ImageView image;
	private TextView title;

	public FunctionListCellWrapper(View base) {
		this.base = base;
	}

	public ImageView getImage() {
		if (image == null) {
			image = (ImageView) base.findViewById(R.id.function_list_cell_image);
		}
		return image;
	}

	public TextView getTitle() {
		if (title == null) {
			title = (TextView) base.findViewById(R.id.function_list_cell_title);
		}
		return title;
	}
}
