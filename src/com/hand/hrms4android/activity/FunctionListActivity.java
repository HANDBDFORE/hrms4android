package com.hand.hrms4android.activity;

import static com.hand.hrms4android.listable.item.FunctionItem.todoItem;

import java.util.ArrayList;
import java.util.List;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.hand.hrms4android.R;
import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.listable.adapter.FunctionListAdapter;
import com.hand.hrms4android.listable.item.FunctionItem;
import com.hand.hrms4android.model.FunctionModel;
import com.hand.hrms4android.model.Model;
import com.hand.hrms4android.model.Model.LoadType;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.util.Constrants;
import com.hand.hrms4android.util.StorageUtil;

public class FunctionListActivity extends SherlockFragmentActivity implements ModelActivity, OnItemClickListener {

	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;

	protected MenuDrawer mMenuDrawer;
	private TextView userTextView;

	private FunctionListAdapter mFunctionListAdapter;
	private ListView mFunctionList;

	private FunctionItem currentFunctionItem = todoItem;

	private Model functionListModel;
	public boolean flag;

	@Override
	protected void onCreate(Bundle arg0) {	 
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(arg0);
		getSupportActionBar();

		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, getDrawerPosition(), getDragMode());
		mMenuDrawer.setMenuView(R.layout.activity_function_list);
		mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_BEZEL);
		mMenuDrawer.setSlideDrawable(R.drawable.ic_drawer);
		mMenuDrawer.setDrawerIndicatorEnabled(true);
		mMenuDrawer.setOnInterceptMoveEventListener(new MenuDrawer.OnInterceptMoveEventListener() {
			@Override
			public boolean isViewDraggable(View v, int dx, int x, int y) {
				return v instanceof SeekBar;
			}
		});
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
		bindAllViews(mMenuDrawer.getMenuView());

		

		this.functionListModel = new FunctionModel(0, this);

		System.out.println("onCreateonCreateonCreateonCreateonCreateonCreateonCreate");
		this.functionListModel.load(LoadType.Network, null);
		
		//保存登录状态
		SharedPreferences savedPreferences = PreferenceManager.getDefaultSharedPreferences(HrmsApplication
		        .getApplication());
		Editor editor = savedPreferences.edit();
		editor.putBoolean(Constrants.SYS_LOGIN_STATUS, true);
		editor.commit();
		
	}

	

	private void bindAllViews(View base) {

		userTextView = (TextView) base.findViewById(R.id.function_activity_user);
		userTextView.setText(PreferenceManager.getDefaultSharedPreferences(this).getString(
		        Constrants.SYS_PREFRENCES_USERNAME, ""));

		mFunctionList = (ListView) base.findViewById(android.R.id.list);
		mFunctionListAdapter = new FunctionListAdapter(this, new ArrayList<Object>(), mFunctionList);
		mFunctionList.setAdapter(mFunctionListAdapter);
		mFunctionList.setOnItemClickListener(this);

		mFunctionListAdapter.setDatas(new ArrayList<Object>());
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
			mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_NONE);
		}

		return mFragmentTransaction;
	}

	private Fragment getFragment(FunctionItem function) {
		Fragment f = mFragmentManager.findFragmentByTag(function.getFunctionId());

		if (f == null) {

			if (function.getFunctionId().equals(FunctionItem.OTHER_ITEM_ID)) {
				Bundle bundle = new Bundle();
				bundle.putString("url", function.getUrl());
				bundle.putString("title", function.getText());
				return Fragment.instantiate(this, "com.hand.hrms4android.activity.HTMLFragment", bundle);
			}

			return Fragment.instantiate(this, function.getFunctionId());
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

	private void hideFragment(Fragment current, Fragment target, int layout, String tag) {
		if (current != target) {
			ensureTransaction();
			if (!target.isAdded()) { // 先判断是否被add过
				mFragmentTransaction.hide(current).add(layout, target, tag); // 隐藏当前的fragment，add下一个到Activity中
			} else {
				target.onResume();
				mFragmentTransaction.hide(current).show(target); // 隐藏当前的fragment，显示下一个
			}
		}

		commitTransactions();
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
	public void modelDidFinishedLoad(Model model) {
		List<Object> items = (List<Object>) model.getProcessData();
		mFunctionListAdapter.setDatas(items);
		selectFragment(items);
	}
	
	/**
	 * 
	 */
    private void selectFragment(List<Object> items) {
	    //
    	
    	for (int i = 0; i < items.size(); i++) {
	        Object o=items.get(i);
	        if (o instanceof FunctionItem) {
	        	//列表中第一个不是section的数据项
	        	currentFunctionItem = (FunctionItem) o;
	        	break;
            }
        }
    	
		mFragmentManager = getSupportFragmentManager();
		// mCurrentFragmentTag = ((Item) mAdapter.getItem(0)).mTitle;
		attachFragment(mMenuDrawer.getContentContainer().getId(), getFragment(currentFunctionItem),
		        FunctionItem.TODO_ITEM_ID);
		commitTransactions();
    }

	@Override
	public void onItemClick(AdapterView<?> listview, View row, int position, long id) {
		Object o = mFunctionListAdapter.getItem(position);
		
		if (o instanceof FunctionItem) {
			FunctionItem item = (FunctionItem) o;
			Fragment currentFragment = getFragment(currentFunctionItem);
			Fragment targetFragment = getFragment(item);
			currentFunctionItem = item;

			hideFragment(currentFragment, targetFragment, mMenuDrawer.getContentContainer().getId(),
			        item.getFunctionId());
//			commitTransactions();

			if (targetFragment instanceof OnFragmentSelectListener) {
				((OnFragmentSelectListener) targetFragment).onSelected(o);
			}

		}
		mMenuDrawer.closeMenu();
	}

	/**
	 * 退出系统
	 * 
	 * @param v
	 */
	public void exitSystem(View v) {
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
	}

	@Override
	public void onBackPressed() {
		final int drawerState = mMenuDrawer.getDrawerState();
		if (drawerState == MenuDrawer.STATE_CLOSED || drawerState == MenuDrawer.STATE_CLOSING) {
			mMenuDrawer.openMenu();
			return;
		}

		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mMenuDrawer.toggleMenu();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void modelFailedLoad(Exception e, Model<? extends Object> model) {
		Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void setModel(Model<? extends Object> model) {

	}



	@Override
	protected void onDestroy() {
		//删除登录状态
		SharedPreferences savedPreferences = PreferenceManager.getDefaultSharedPreferences(HrmsApplication
		        .getApplication());
		Editor editor = savedPreferences.edit();
		editor.remove(Constrants.SYS_LOGIN_STATUS);
		editor.commit();
		super.onDestroy();
	}
	
	

}
