package com.hand.hrms4android.activity;

import java.util.ArrayList;
import java.util.List;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.hand.hrms4android.R;
import com.hand.hrms4android.listable.adapter.FunctionListAdapter;
import com.hand.hrms4android.model.FunctionModel;
import com.hand.hrms4android.model.Model;
import com.hand.hrms4android.model.Model.LoadType;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.util.StorageUtil;

public class FunctionListFragment extends SherlockFragmentActivity implements ModelActivity,OnItemClickListener {

	/**
	 * 待办事项
	 */
	public static final String TODO_ITEM_ID = "todo";

	/**
	 * 已完成事项
	 */
	public static final String DONE_ITEM_ID = "done";
	
	private static final String STATE_CURRENT_FRAGMENT = "net.simonvt.menudrawer.samples.FragmentSample";

	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;

	private String mCurrentFragmentTag;

	protected MenuDrawer mMenuDrawer;

	protected FunctionListAdapter mAdapter;
	protected ListView mList;

	private int mActivePosition = 0;

	private Model functionListModel;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, getDrawerPosition(), getDragMode());
		mMenuDrawer.setMenuView(R.layout.activity_function_list);
		bindAllViews(mMenuDrawer.getMenuView());

		mFragmentManager = getSupportFragmentManager();
		// mCurrentFragmentTag = ((Item) mAdapter.getItem(0)).mTitle;
		attachFragment(mMenuDrawer.getContentContainer().getId(), getFragment(mCurrentFragmentTag), mCurrentFragmentTag);
		commitTransactions();

		mMenuDrawer.setOnDrawerStateChangeListener(new MenuDrawer.OnDrawerStateChangeListener() {
			@Override
			public void onDrawerStateChange(int oldState, int newState) {
				if (newState == MenuDrawer.STATE_CLOSED) {
					commitTransactions();
				}
			}

			@Override
			public void onDrawerSlide(float openRatio, int offsetPixels) {
				// Do nothing
			}
		});

		this.functionListModel = new FunctionModel(0, this);
		functionListModel.load(LoadType.Network, null);
	}

	private void bindAllViews(View base) {
		mList = (ListView) base.findViewById(android.R.id.list);
		mAdapter = new FunctionListAdapter(this, new ArrayList<Object>(), mList);
		mList.setAdapter(mAdapter);
	}

	protected int getDragMode() {
		return MenuDrawer.MENU_DRAG_WINDOW;
	}

	protected Position getDrawerPosition() {
		return Position.LEFT;
	}

	protected FragmentTransaction ensureTransaction() {
		if (mFragmentTransaction == null) {
			mFragmentTransaction = mFragmentManager.beginTransaction();
			mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		}

		return mFragmentTransaction;
	}

	private Fragment getFragment(String tag) {
		Fragment f = mFragmentManager.findFragmentByTag(tag);

		if (f == null) {
			// f = SampleFragment.newInstance(tag);
			return new TodoListFragment();
		}

		return f;
	}

	protected void attachFragment(int layout, Fragment f, String tag) {
		if (f != null) {
			if (f.isDetached()) {
				ensureTransaction();
				mFragmentTransaction.attach(f);
			} else if (!f.isAdded()) {
				ensureTransaction();
				mFragmentTransaction.add(layout, f, tag);
			}
		}
	}

	protected void detachFragment(Fragment f) {
		if (f != null && !f.isDetached()) {
			ensureTransaction();
			mFragmentTransaction.detach(f);
		}
	}

	protected void commitTransactions() {
		if (mFragmentTransaction != null && !mFragmentTransaction.isEmpty()) {
			mFragmentTransaction.commit();
			mFragmentTransaction = null;
		}
	}

	@Override
	public void modelDidFinishedLoad(Model<? extends Object> model) {
		List<Object> items = (List<Object>) model.getProcessData();
		mAdapter.setDatas(items);
		mAdapter.notifyDataSetChanged();

	}
	
	@Override
	public void onItemClick(AdapterView<?> listview, View row, int position, long id) {
		Object item = mAdapter.getItem(position);

//		if (item.getParentId() != null && item.getParentId().equalsIgnoreCase(TODO_ITEM_ID)) {
//			startActivity(new Intent(this, TodoListActivity.class));
//			return;
//		}
//
//		if (item.getParentId() != null && item.getParentId().equalsIgnoreCase(DONE_ITEM_ID)) {
//			startActivity(new Intent(this, DoneListActivity.class));
//			return;
//		}
//
//		Intent i = new Intent(this, HTMLActivity.class);
//		i.putExtra("url", item.getUrl());
//		i.putExtra("title", item.getText());
//		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		int index = 0;
		menu.add(0, R.id.todo_list_menu_logout, index++, "退出登录").setIcon(R.drawable.ic_exit)
		        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.todo_list_menu_logout: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setTitle("退出系统");
			builder.setMessage("确认退出系统吗？退出后所有本地保存的数据将被清空！");
			builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					StorageUtil.deleteDB();
					StorageUtil.removeSavedInfo();
					NetworkUtil.setCookieStore(null);
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
		default:
			break;
		}

		return true;

	}

	@Override
	public void modelFailedLoad(Exception e, Model<? extends Object> model) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setModel(Model<? extends Object> model) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

}
