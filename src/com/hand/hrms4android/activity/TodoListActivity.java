package com.hand.hrms4android.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.hand.hrms4android.R;
import com.hand.hrms4android.listable.adapter.TodoListAdapter;
import com.hand.hrms4android.listable.item.TodoListItem;
import com.hand.hrms4android.model.Model;
import com.hand.hrms4android.model.Model.LoadType;
import com.hand.hrms4android.model.TodoListModel;
import com.hand.hrms4android.persistence.DataBaseMetadata;
import com.hand.hrms4android.util.Constrants;
import com.hand.hrms4android.util.StorageUtil;
import com.hand.hrms4android.util.TempTransfer;
import com.hand.hrms4android.util.data.IndexPath;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class TodoListActivity extends ActionBarActivity implements OnItemClickListener, OnItemLongClickListener {
	private static final int REQUEST_ACTIVITY_DETAIL = 1;
	private static final int REQUEST_ACTIVITY_OPINION = 2;

	/**
	 * listview 的 header ,会影响 onItemClick时的position，使用时减去此值
	 */
	private static int LISTVIEW_HEADER_COUNT = 1;

	private PullToRefreshListView todoListViewWrapper;
	private ImageButton reloadButton;
	private TextView reloadText;

	private ActionMode.Callback actionModeCallback;
	private TodoListAdapter listAdapter;
	private ActionMode mActionMode;
	private TodoListModel listModel;
	private GestureDetector gestureDetector;
	private OnGestureListener gestureListener;

	private boolean multiChoiceMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_todo_list);

		bindAllViews();
		buildResource();

		// 第一次进入程序，从本地读取
		this.model.load(LoadType.Local, null);
	}

	private void bindAllViews() {
		todoListViewWrapper = (PullToRefreshListView) findViewById(R.id.activity_todo_list_listviewwrapper);
		todoListViewWrapper.getRefreshableView().setOnItemLongClickListener(this);
		todoListViewWrapper.setOnItemClickListener(this);
		todoListViewWrapper.getRefreshableView().setChoiceMode(ListView.CHOICE_MODE_NONE);
		todoListViewWrapper.setOnRefreshListener(new PulldownListener());

		reloadButton = (ImageButton) findViewById(R.id.activity_todo_list_reload_button);
		reloadButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				model.load(LoadType.Network, null);
				reloadButton.setEnabled(false);
				setSupportProgressBarIndeterminateVisibility(true);
			}
		});
		reloadText = (TextView) findViewById(R.id.activity_todo_list_reload_text);
	}

	private void buildResource() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		listModel = new TodoListModel(0, this);
		this.model = listModel;

		gestureListener = new GestureRecogniser();
		gestureDetector = new GestureDetector(this, gestureListener);
		todoListViewWrapper.getRefreshableView().setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {

					todoListViewWrapper.getRefreshableView().onInterceptTouchEvent(event);
					return true;
				}
				return false;
			}
		});

		actionModeCallback = new ActionModeOfApproveCallback();
		mActionMode = null;

		listAdapter = new TodoListAdapter(this, listModel);
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
			setSupportProgressBarIndeterminateVisibility(true);
			listModel.load(LoadType.Network, null);
		} else {
			setSupportProgressBarIndeterminateVisibility(false);
			todoListViewWrapper.onRefreshComplete();
		}

	}

	@Override
	public void modelFailedLoad(Exception e, Model model) {
		setSupportProgressBarIndeterminateVisibility(false);
		todoListViewWrapper.onRefreshComplete();
		
		//如果没有数据，显示重试按钮
		if ((listAdapter.getCount() == 0) && (!listModel.needLoadOnceMore())) {
			showEmptyTip(e.getMessage());
		} 
		
		super.modelFailedLoad(e, model);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		// 启动多选
		mActionMode = startActionMode(actionModeCallback);
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
			// Intent intent = new Intent(this, ApproveDetailActivity.class);
			Intent intent = new Intent(this, ApproveDetailActivity.class);
			listModel.setRecordAsSelected(new IndexPath(0, getCorrectRowPosition(position)));

			// 因为此model不是可序列化，因此不能以extra形式传送
			// TODO 考虑重构，放入application?
			TempTransfer.container.put(TempTransfer.KEY_TODO_LIST_MODEL, listModel);
			startActivityForResult(intent, REQUEST_ACTIVITY_DETAIL);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// 如果是从 明细页面 或者是 意见页
		if ((requestCode == REQUEST_ACTIVITY_DETAIL) || (requestCode == REQUEST_ACTIVITY_OPINION)) {
			// 确定提交动作
			if (resultCode == RESULT_OK) {
				// 参数
				Map<String, String> options = new HashMap<String, String>();

				options.put(DataBaseMetadata.TodoListLogical.ACTION,
				        data.getStringExtra(DataBaseMetadata.TodoListLogical.ACTION));
				options.put(DataBaseMetadata.TodoListLogical.COMMENTS,
				        data.getStringExtra(DataBaseMetadata.TodoListLogical.COMMENTS));
				options.put(DataBaseMetadata.TodoListLogical.EMPLOYEE_ID,
				        data.getStringExtra(DataBaseMetadata.TodoListLogical.EMPLOYEE_ID));

				if (requestCode == REQUEST_ACTIVITY_DETAIL) {
					listModel.addRecordToSubmitQueue(options, data
					        .getStringExtra(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_PHYSICAL_PK));
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
			menu.add(0, MENU_ID_APPROVE, 0, R.string.activity_todo_list_actionitem_approve)
			        .setIcon(R.drawable.ic_approve_agree_dark).setTitle("同意")
			        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			menu.add(0, MENU_ID_DENY, 1, R.string.activity_todo_list_actionitem_deny)
			        .setIcon(R.drawable.ic_approve_refuse_dark).setTitle("拒绝")
			        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			if ((item.getItemId() == MENU_ID_APPROVE) || (item.getItemId() == MENU_ID_DENY)) {

				Intent opinionIntent = new Intent(TodoListActivity.this, ApproveOpinionActivity.class);

				if (item.getItemId() == MENU_ID_APPROVE) {
					// 同意
					opinionIntent.putExtra(DataBaseMetadata.TodoListLogical.ACTION, "Y");
				} else if (item.getItemId() == MENU_ID_DENY) {
					// 拒绝
					opinionIntent.putExtra(DataBaseMetadata.TodoListLogical.ACTION, "N");
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
			todoListViewWrapper.getRefreshableView().setOnItemLongClickListener(TodoListActivity.this);

			// 取消所有
			TodoListActivity.this.deSelectAllRow();
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

	private class GestureRecogniser extends SimpleOnGestureListener {
		private static final int SWIPE_MIN_DISTANCE = 80;
		private static final int SWIPE_MAX_OFF_PATH = 250;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
				return false;
			// right to left swipe
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
			        && (Math.abs(velocityX) < Math.abs(velocityY))) {

				return true;
			}
			// 左到右滑动
			else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
			        && (Math.abs(velocityX - 80) > Math.abs(velocityY))) {
				// int position =
				// todoListViewWrapper.getRefreshableView().pointToPosition((int)
				// e1.getX(),
				// (int) e1.getY());
				int position = getCorrectRowPosition(todoListViewWrapper.getRefreshableView().pointToPosition(
				        (int) e1.getX(), (int) e1.getY()));
				if (position >= 0) {
					// 删除行
					// next(null, position);
					deleteRowAtPosition(position);
					return true;
				} else {
					return false;
				}
			}
			return false;
		}
	}

	/**
	 * @param position
	 */
	private void deleteRowAtPosition(int position) {
		TodoListItem item = listAdapter.getItem(position);

		// 只删除在别处处理的
		if (item.getStatus().equals(Constrants.APPROVE_RECORD_STATUS_DIFFERENT)) {
			deleteRowWithItemID(item.getId());
		}
	}

	/**
	 * 以动画形式删除某行
	 * 
	 * @param itemID
	 */
	private void deleteRowWithItemID(final long itemID) {

		// 读取动画
		Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
		anim.setDuration(300);
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 删除本地数据
				listModel.removeRowByID(itemID);
				// 通知更新
				listAdapter.reFetchData();
			}
		});
		int childViewCount = todoListViewWrapper.getRefreshableView().getChildCount();
		for (int i = 0; i < childViewCount; i++) {
			View row = todoListViewWrapper.getRefreshableView().getChildAt(i);
			Integer id = (Integer) row.getTag(R.id.todo_list_row_tag_id);
			if (id != null && (id.intValue() == itemID)) {
				row.startAnimation(anim);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		int index = 0;
		// menu.add(0, R.id.todo_list_menu_search, index++,
		// "搜索").setIcon(R.drawable.ic_search_inverse)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS |
		// MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		// menu.add(0, R.id.todo_list_menu_refresh, index++,
		// "刷新").setIcon(R.drawable.ic_refresh_inverse)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS |
		// MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.todo_list_menu_search: {

			break;
		}

		case R.id.todo_list_menu_settings: {
			Intent settingIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingIntent);
			break;
		}

		case R.id.todo_list_menu_logout: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setTitle("退出系统");
			builder.setMessage("确认退出系统吗？退出后所有本地保存的数据将被清空！");
			builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					StorageUtil.deleteDB();
					StorageUtil.removeSavedInfo();
					finish();
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			});
			builder.show();

			break;
		}

		case android.R.id.home: {
			finish();
			break;
		}
		default:
			break;
		}

		return true;

	}
}
