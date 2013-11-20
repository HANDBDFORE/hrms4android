package com.hand.hrms4android.listable.adapter;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hand.hrms4android.R;
import com.hand.hrms4android.core.HDAbstractModel;
import com.hand.hrms4android.listable.doman.TodoListDomain;
import com.hand.hrms4android.listable.item.ItemFactory;
import com.hand.hrms4android.listable.item.TodoListItem;
import com.hand.hrms4android.listable.item.TodoListItemFactory;
import com.hand.hrms4android.util.Constrants;

public class TodoListAdapter extends BaseAdapter {

	private LayoutInflater mLayoutInflater;
	private List<String> selectedRecordIDs;
	private HDAbstractModel model;
	private List<TodoListItem> items;
	private ItemFactory<TodoListItem,TodoListDomain> itemFactory;
	private Context context;

	public TodoListAdapter(Context context, HDAbstractModel model) {
		this.context = context;
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		selectedRecordIDs = new LinkedList<String>();
		itemFactory = new TodoListItemFactory();

		this.model = model;

		// 转换显示值
		if (model != null && model.getProcessData() != null) {

			items = itemFactory.getItemList((List<TodoListDomain>) model.getProcessData());
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		TodoListCellWrapper wrapper = null;

		if (row == null) {
			row = mLayoutInflater.inflate(R.layout.todo_list_cell, parent, false);
			wrapper = new TodoListCellWrapper(row);
			row.setTag(R.id.todo_list_row_tag_wrapper, wrapper);
		} else {
			wrapper = (TodoListCellWrapper) row.getTag(R.id.todo_list_row_tag_wrapper);
		}

		TodoListItem todoItem = getItem(position);

		// 恢复正常样子
		setViewsAsNew(wrapper);

		// 设置显示值
		wrapper.getTitleTextView().setText(todoItem.getTitle());
		wrapper.getDateTextView().setText(todoItem.getTitleRight());
		wrapper.getSubTitleTextView().setText(todoItem.getSubTitle());
		wrapper.getInformationTextView().setText(todoItem.getInformation());

		// 如果是被选中项 设置被选中背景色
		if (selectedRecordIDs.contains(String.valueOf(todoItem.getId()))) {
			row.setBackgroundResource(R.color.todo_listview_selected);
		}

		// 判断该行对应数值的状态
		String recordLocalStatus = ((List<TodoListDomain>) model.getProcessData()).get(position).getStatus();
		if (recordLocalStatus.equals(Constrants.APPROVE_RECORD_STATUS_ERROR)) {
			// 说明是出错项
			wrapper.getErrorMessageTextView().setVisibility(View.VISIBLE);
			wrapper.getErrorMessageTextView().setText(todoItem.getErrorMessage());
			// 加背景
			wrapper.getErrorMessageTextView().setBackgroundResource(R.drawable.cell_information_error);

		} else if (recordLocalStatus.equals(Constrants.APPROVE_RECORD_STATUS_DIFFERENT)) {
			// 说明是已处理项
			wrapper.getErrorMessageTextView().setVisibility(View.VISIBLE);
			wrapper.getErrorMessageTextView().setText(todoItem.getErrorMessage());
			// 加背景
			wrapper.getErrorMessageTextView().setBackgroundResource(R.drawable.cell_information_done);
			wrapper.getBase().setBackgroundResource(R.color.todo_listview_disable);

		} else if (recordLocalStatus.equals(Constrants.APPROVE_RECORD_STATUS_WAITING)) {
			wrapper.getSubmitProgressBar().setVisibility(View.VISIBLE);
			// wrapper.getBase().getBackground().setAlpha(20);
		}

		if (todoItem.isLate()) {
			wrapper.getTitleTextView().setTextColor(context.getResources().getColor(R.color.todo_listview_title_red));
		}

		// 设置标记
		row.setTag(R.id.todo_list_row_tag_position, Integer.valueOf(position));
		row.setTag(R.id.todo_list_row_tag_id, Integer.valueOf(todoItem.getId()));

		return row;
	}

	/**
	 * 将视图状态恢复
	 * 
	 * @param wrapper
	 */
	private void setViewsAsNew(TodoListCellWrapper wrapper) {
		wrapper.getTitleTextView().setTextAppearance(context, android.R.style.TextAppearance_Medium);
		wrapper.getBase().setBackgroundResource(R.drawable.todo_listview_selector);

		TextView errorMessageTextView = wrapper.getErrorMessageTextView();
		errorMessageTextView.setVisibility(View.GONE);
		errorMessageTextView.setText("");
		errorMessageTextView.setBackgroundDrawable(null);

		wrapper.getSubmitProgressBar().setVisibility(View.GONE);
	}

	@Override
	public int getCount() {
		if (items == null) {
			return 0;
		} else {
			return items.size();
		}
	}

	@Override
	public TodoListItem getItem(int position) {
		if (items == null) {
			return null;
		}
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		if (items == null) {
			return 0;
		}
		return items.get(position).getId();
	}

	/**
	 * 重新读取数据
	 */
	public void reFetchData() {
		if (this.model != null) {
			this.items = itemFactory.getItemList((List<TodoListDomain>) model.getProcessData());
		}
		notifyDataSetChanged();
	}

	@Override
	public boolean isEnabled(int position) {
		String localStatus = ((List<TodoListDomain>) model.getProcessData()).get(position).getStatus();
		if ((localStatus.equals(Constrants.APPROVE_RECORD_STATUS_WAITING))
		        || (localStatus.equals(Constrants.APPROVE_RECORD_STATUS_DIFFERENT))) {
			return false;
		}
		return true;
	}

	// /////////////////////////////////////////////////////////////////////////////
	// 与选中有关
	// /////////////////////////////////////////////////////////////////////////////
	public void selectItem(String itemID) {
		selectedRecordIDs.add(itemID);
		notifyDataSetChanged();
	}

	public void deSelectItem(String itemID) {
		selectedRecordIDs.remove(itemID);
		notifyDataSetChanged();
	}

	public void deSelectAllItem() {
		selectedRecordIDs.clear();
		notifyDataSetChanged();
	}

	public boolean isSelected(String itemID) {
		if (selectedRecordIDs.contains(itemID)) {
			return true;
		}
		return false;
	}

	public int getSelectedRowCount() {
		return selectedRecordIDs.size();
	}

	public String[] getSelectedItemIDs() {
		String[] result = new String[selectedRecordIDs.size()];
		return selectedRecordIDs.toArray(result);
	}
}

/**
 * @author emerson
 * 
 */
class TodoListCellWrapper {
	private View base;
	private TextView errorMessageTextView;
	private TextView titleTextView;
	private TextView dateTextView;
	private TextView subTitleTextView;
	private TextView informationTextView;
	private ProgressBar submitProgressBar;

	public TodoListCellWrapper(View base) {
		this.base = base;
	}

	public TextView getErrorMessageTextView() {
		if (errorMessageTextView == null) {
			errorMessageTextView = (TextView) base.findViewById(R.id.todo_list_cell_error_message);
		}

		return errorMessageTextView;
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

	public ProgressBar getSubmitProgressBar() {
		if (submitProgressBar == null) {
			submitProgressBar = (ProgressBar) base.findViewById(R.id.todo_list_cell_submit_progressBar);
		}
		return submitProgressBar;
	}

	public View getBase() {
		return base;
	}
}