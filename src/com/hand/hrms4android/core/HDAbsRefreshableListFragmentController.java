package com.hand.hrms4android.core;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;

import com.hand.hrms4android.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public abstract class HDAbsRefreshableListFragmentController extends HDAbstractFragmentController implements
        OnItemClickListener, OnItemLongClickListener, OnItemSelectedListener, OnRefreshListener<ListView> {

	protected final Page page = new Page();
	protected PullToRefreshListView pullToRefreshListView;
	/**
	 * 单屏
	 */
	protected boolean isSinglePanel;
	

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		bindAllViews(view, savedInstanceState);
	}

	/**
	 * 绑定视图
	 * 
	 * @param view
	 *            已经创建的根视图
	 * @param savedInstanceState
	 */
	private void bindAllViews(View view, Bundle savedInstanceState) {
		pullToRefreshListView = (PullToRefreshListView) view.findViewById(pulldownViewId());
		pullToRefreshListView.setOnRefreshListener(this);
		pullToRefreshListView.getRefreshableView().setOnItemClickListener(this);
		pullToRefreshListView.getRefreshableView().setOnItemLongClickListener(this);
		pullToRefreshListView.getRefreshableView().setOnItemSelectedListener(this);

		pullToRefreshListView.setMode(Mode.BOTH); // mode refresh for top and
		pullToRefreshListView.setShowIndicator(false); // disable indicator

		page.end = pageSize();

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		isSinglePanel = getActivity().findViewById(R.id.activity_main_content) == null;

	}

	public ListView getListView() {
		return pullToRefreshListView.getRefreshableView();
	}

	@Override
	public void modelDidFinishedLoad(Model model) {
		pullToRefreshListView.onRefreshComplete();
	}

	@Override
	public void modelFailedLoad(Exception e, Model model) {
		super.modelFailedLoad(e, model);
		pullToRefreshListView.onRefreshComplete();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
			page.start = 0;
			page.end = pageSize();
		} else {
			page.start = page.end;
			page.end += pageSize();
		}
	}

	protected int pageSize() {
		return 20;
	}

	protected abstract int pulldownViewId();

}
