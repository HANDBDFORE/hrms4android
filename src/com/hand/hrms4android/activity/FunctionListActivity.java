package com.hand.hrms4android.activity;

import static com.hand.hrms4android.listable.item.FunctionItem.todoItem;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hand.hrms4android.R;
import com.hand.hrms4android.core.HDAbstractActivityController;
import com.hand.hrms4android.core.Model;
import com.hand.hrms4android.core.Model.LoadType;
import com.hand.hrms4android.listable.adapter.FunctionListAdapter;
import com.hand.hrms4android.listable.item.FunctionItem;
import com.hand.hrms4android.model.FunctionModel;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.util.Constrants;
import com.hand.hrms4android.util.StorageUtil;

public class FunctionListActivity extends HDAbstractActivityController implements OnItemClickListener {

	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;

	protected DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private TextView userTextView;

	private FunctionListAdapter mFunctionListAdapter;
	private ListView mFunctionList;

	private FunctionItem currentFunctionItem = todoItem;

	private Model functionListModel;

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(arg0);

		setContentView(R.layout.activity_function_list);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description */
		R.string.drawer_close /* "close drawer" description */
		) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				commitTransactions();
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				commitTransactions();
			}
		};
		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		bindAllViews();

		mFragmentManager = getSupportFragmentManager();
		// mCurrentFragmentTag = ((Item) mAdapter.getItem(0)).mTitle;
		attachFragment(R.id.content_frame, getFragment(currentFunctionItem), FunctionItem.TODO_ITEM_ID);
		commitTransactions();

		this.functionListModel = new FunctionModel(0, this);

		System.out.println("onCreateonCreateonCreateonCreateonCreateonCreateonCreate");
		this.functionListModel.load(LoadType.Network, null);
	}

	private void bindAllViews() {

		userTextView = (TextView) findViewById(R.id.function_activity_user);
		userTextView.setText(PreferenceManager.getDefaultSharedPreferences(this).getString(
		        Constrants.SYS_PREFRENCES_USERNAME, ""));

		mFunctionList = (ListView) findViewById(R.id.left_drawer);
		mFunctionListAdapter = new FunctionListAdapter(this, new ArrayList<Object>(), mFunctionList);
		mFunctionList.setAdapter(mFunctionListAdapter);
		mFunctionList.setOnItemClickListener(this);

		mFunctionListAdapter.setDatas(new ArrayList<Object>());

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
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
		List<Object> items = model.getProcessData();
		mFunctionListAdapter.setDatas(items);
	}

	@Override
	public void onItemClick(AdapterView<?> listview, View row, int position, long id) {
		Object o = mFunctionListAdapter.getItem(position);
		if (o instanceof FunctionItem) {
			FunctionItem item = (FunctionItem) o;
			Fragment currentFragment = getFragment(currentFunctionItem);
			Fragment targetFragment = getFragment(item);
			currentFunctionItem = item;

			hideFragment(currentFragment, targetFragment,R.id.content_frame,
			        item.getFunctionId());
			commitTransactions();

			if (targetFragment instanceof OnFragmentSelectListener) {
				((OnFragmentSelectListener) targetFragment).onSelected(o);
			}

		}
		mDrawerLayout.closeDrawer(mFunctionList);
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
		boolean isOpen=mDrawerLayout.isDrawerOpen(mFunctionList);
		if (!isOpen) {
			mDrawerLayout.openDrawer(mFunctionList);
        }
//		if (drawerState == MenuDrawer.STATE_CLOSED || drawerState == MenuDrawer.STATE_CLOSING) {
//			mDrawerLayout.openMenu();
//			return;
//		}

		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			boolean isOpen=mDrawerLayout.isDrawerOpen(mFunctionList);
			if (isOpen) {
				mDrawerLayout.closeDrawer(mFunctionList);
	        }else{
	        	mDrawerLayout.openDrawer(mFunctionList);
	        }
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
