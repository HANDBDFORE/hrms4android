package com.hand.hrms4android.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.hand.hrms4android.R;
import com.hand.hrms4android.app.ApproveDetailActivity;
import com.hand.hrms4android.app.ApproveOpinionActivity;
import com.hand.hrms4android.core.HDAbsRefreshableListFragmentController;
import com.hand.hrms4android.core.Model;
import com.hand.hrms4android.core.Model.LoadType;
import com.hand.hrms4android.listable.adapter.TodoListAdapter;
import com.hand.hrms4android.model.TodoListModel;
import com.hand.hrms4android.persistence.DataBaseMetadata.TodoList;
import com.hand.hrms4android.util.TempTransfer;
import com.hand.hrms4android.util.data.IndexPath;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class TodoListFragment extends HDAbsRefreshableListFragmentController implements OnItemClickListener, OnItemLongClickListener {
	private static final int TODOLIST_ACTIVITY_BASE = 10;
	private static final int REQUEST_ACTIVITY_DETAIL = TODOLIST_ACTIVITY_BASE + 1;
	private static final int REQUEST_ACTIVITY_OPINION = TODOLIST_ACTIVITY_BASE + 2;

	private static final int MODEL_TODO = 1;

	/**
	 * listview 的 header ,会影响 onItemClick时的position，使用时减去此值
	 */
	private static int LISTVIEW_HEADER_COUNT = 1;

	private PullToRefreshListView todoListViewWrapper;
	private ImageButton reloadButton;
	private TextView reloadText;

	private ActionBarActivity actionBarActivity;
	private ActionMode.Callback actionModeCallback;
	private TodoListAdapter listAdapter;
	private ActionMode mActionMode;
	private TodoListModel listModel;

	private boolean multiChoiceMode;

	@Override
	public void onAttach(Activity activity) {
		if(activity instanceof ActionBarActivity){
			actionBarActivity = (ActionBarActivity)activity;
		}else{
			throw new RuntimeException("请使用 ActionBarActivity");
		}
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_todo_list, container, false);
		bindAllViews(view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		buildResource();
		actionBarActivity.setSupportProgressBarIndeterminateVisibility(true);
		this.model.load(LoadType.Local, null);
	}

	private void bindAllViews(View root) {

		/*
		 * 原来
		 */
		todoListViewWrapper = (PullToRefreshListView) root.findViewById(R.id.activity_todo_list_listviewwrapper);
		todoListViewWrapper.getRefreshableView().setOnItemLongClickListener(this);
		todoListViewWrapper.setOnItemClickListener(this);
		todoListViewWrapper.getRefreshableView().setChoiceMode(ListView.CHOICE_MODE_NONE);
		todoListViewWrapper.setOnRefreshListener(new PulldownListener());

		actionBarActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		reloadButton = (ImageButton) root.findViewById(R.id.activity_todo_list_reload_button);
		reloadButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				model.load(LoadType.Network, null);
				reloadButton.setEnabled(false);
				actionBarActivity.setSupportProgressBarIndeterminateVisibility(true);
			}
		});
		reloadText = (TextView) root.findViewById(R.id.activity_todo_list_reload_text);

	}

	private void buildResource() {
		actionBarActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		listModel = new TodoListModel(MODEL_TODO, this);
		this.model = listModel;

		actionModeCallback = new ActionModeOfApproveCallback();
		mActionMode = null;

		listAdapter = new TodoListAdapter(this.getActivity(), listModel);
		todoListViewWrapper.setAdapter(listAdapter);

	}

	@Override
	public void modelDidFinishedLoad(Model model) {
		// 重新绘制界面
		listAdapter.reFetchData();

		if ((listAdapter.getCount() == 0) && (!listModel.needLoadOnceMore())) {
			showEmptyTip("暂时没有待审批事项");
		} else {
			showList();
		}

		if (listModel.needLoadOnceMore()) {
			actionBarActivity.setSupportProgressBarIndeterminateVisibility(true);
			listModel.load(LoadType.Network, null);
		} else {
			actionBarActivity.setSupportProgressBarIndeterminateVisibility(false);
			todoListViewWrapper.onRefreshComplete();
		}

	}

	@Override
	public void modelFailedLoad(Exception e, Model model) {
		actionBarActivity.setSupportProgressBarIndeterminateVisibility(false);
		todoListViewWrapper.onRefreshComplete();

		// 如果没有数据，显示重试按钮
		if ((listAdapter.getCount() == 0) && (!listModel.needLoadOnceMore())) {
			showEmptyTip(e.getMessage());
		}

		super.modelFailedLoad(e, model);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		// 启动多选
		mActionMode = actionBarActivity.startSupportActionMode(actionModeCallback);
		mActionMode.setTitle("批量审批");
		multiChoiceMode = true;

		// 选中长按项
		selectRow(String.valueOf(id));
		mActionMode.setSubtitle(String.valueOf(getSelectedRowCount()));

		// 将当前监听器删除，防止再次长按
		todoListViewWrapper.getRefreshableView().setOnItemLongClickListener(null);

		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// 判断是否处于多选状态
		if (multiChoiceMode) {
			boolean isChecked = listAdapter.isSelected(String.valueOf(id));
			// 判断是否已经被选中
			if (isChecked) {
				// 已经被选中，取消选中。
				deSelectRow(String.valueOf(id));
			} else {
				// 未被选中，选中
				selectRow(String.valueOf(id));
			}

			if (getSelectedRowCount() == 0) {
				mActionMode.finish();
				multiChoiceMode = false;
			} else {
				mActionMode.setSubtitle(String.valueOf(getSelectedRowCount()));
			}
		}
		// 非多选状态
		else {
			// 启动明细页面
			Intent intent = new Intent(getActivity(), ApproveDetailActivity.class);
			listModel.setRecordAsSelected(new IndexPath(0, getCorrectRowPosition(position)));

			// 因为此model不是可序列化，因此不能以extra形式传送
			// TODO 考虑重构，放入application?
			TempTransfer.container.put(TempTransfer.KEY_TODO_LIST_MODEL, listModel);
			startActivityForResult(intent, REQUEST_ACTIVITY_DETAIL);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// 如果是从 明细页面 或者是 意见页
		if ((requestCode == REQUEST_ACTIVITY_DETAIL) || (requestCode == REQUEST_ACTIVITY_OPINION)) {
			// 确定提交动作
			if (resultCode == Activity.RESULT_OK) {
				// 参数
				Map<String, String> options = new HashMap<String, String>();

				options.put(TodoList.ACTION, data.getStringExtra(TodoList.ACTION));
				options.put(TodoList.ACTION_TYPE, data.getStringExtra(TodoList.ACTION_TYPE));
				options.put(TodoList.COMMENTS, data.getStringExtra(TodoList.COMMENTS));
				options.put(TodoList.DELIVEREE, data.getStringExtra(TodoList.DELIVEREE));

				if (requestCode == REQUEST_ACTIVITY_DETAIL) {
					listModel.addRecordToSubmitQueue(options, data.getStringExtra(TodoList.ID));
				} else if (requestCode == REQUEST_ACTIVITY_OPINION) {
					listModel.addRecordToSubmitQueue(options, listAdapter.getSelectedItemIDs());
					mActionMode.finish();
				}
			}
		}

	}

	/**
	 * 因为有listview 的 header ,会影响 onItemClick时的position
	 * 
	 * @param position
	 * @return
	 */
	private int getCorrectRowPosition(int position) {
		return position - LISTVIEW_HEADER_COUNT;
	}

	/**
	 * 选中行
	 * 
	 * @param itemID
	 */
	private void selectRow(String itemID) {
		listAdapter.selectItem(itemID);
	}

	/**
	 * 取消行
	 * 
	 * @param itemID
	 */
	private void deSelectRow(String itemID) {
		listAdapter.deSelectItem(itemID);
	}

	/**
	 * 取消所有行
	 */
	private void deSelectAllRow() {
		listAdapter.deSelectAllItem();
	}

	private int getSelectedRowCount() {
		return listAdapter.getSelectedRowCount();
	}

	/**
	 * 显示列表组件
	 */
	private void showList() {
		todoListViewWrapper.setVisibility(View.VISIBLE);
		todoListViewWrapper.bringToFront();
		reloadButton.setVisibility(View.INVISIBLE);
		reloadText.setVisibility(View.INVISIBLE);
	}

	private void showEmptyTip(String message) {
		todoListViewWrapper.setVisibility(View.INVISIBLE);
		reloadButton.setVisibility(View.VISIBLE);
		reloadButton.setEnabled(true);
		reloadButton.bringToFront();
		reloadText.setVisibility(View.VISIBLE);
		reloadText.setText(message);
		reloadText.bringToFront();
	}

	/**
	 * action回调
	 * 
	 * @author emerson
	 * 
	 */
	class ActionModeOfApproveCallback implements ActionMode.Callback {
		private static final int MENU_ID_APPROVE = 0;
		private static final int MENU_ID_DENY = 1;

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			//TODO 分析如何做到
//			menu.add(0, MENU_ID_APPROVE, 0, R.string.activity_todo_list_actionitem_approve)
//			        .setIcon(R.drawable.ic_approve_agree_dark).setTitle("同意")
//			        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//			menu.add(0, MENU_ID_DENY, 1, R.string.activity_todo_list_actionitem_deny)
//			        .setIcon(R.drawable.ic_approve_refuse_dark).setTitle("拒绝")
//			        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			if ((item.getItemId() == MENU_ID_APPROVE) || (item.getItemId() == MENU_ID_DENY)) {

				Intent opinionIntent = new Intent(getActivity(), ApproveOpinionActivity.class);

				if (item.getItemId() == MENU_ID_APPROVE) {
					// 同意
					opinionIntent.putExtra(TodoList.ACTION_TYPE, "approve");
				} else if (item.getItemId() == MENU_ID_DENY) {
					// 拒绝
					opinionIntent.putExtra(TodoList.ACTION_TYPE, "reject");
				}
				opinionIntent.putExtra(ApproveOpinionActivity.EXTRA_TITLE, item.getTitle());
				// 弹出意见
				startActivityForResult(opinionIntent, REQUEST_ACTIVITY_OPINION);
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			// 恢复长按监听
			todoListViewWrapper.getRefreshableView().setOnItemLongClickListener(TodoListFragment.this);

			// 取消所有
			TodoListFragment.this.deSelectAllRow();
			// 关闭多选
			multiChoiceMode = false;
		}
	}

	private class PulldownListener implements OnRefreshListener<ListView> {

		@Override
		public void onRefresh(PullToRefreshBase<ListView> refreshView) {
			listModel.load(LoadType.Network, null);
		}
	}

	@Override
    protected int pulldownViewId() {
	    // TODO Auto-generated method stub
	    return 0;
    }

}
