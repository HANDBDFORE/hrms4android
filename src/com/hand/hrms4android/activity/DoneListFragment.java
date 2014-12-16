package com.hand.hrms4android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.hand.hrms4android.ems.R;
import com.hand.hrms4android.listable.adapter.DoneListAdapter;
import com.hand.hrms4android.listable.item.DoneListItem;
import com.hand.hrms4android.model.AbstractPageableModel;
import com.hand.hrms4android.model.DoneListModel;
import com.hand.hrms4android.model.Model;
import com.hand.hrms4android.model.Model.LoadType;
import com.hand.hrms4android.util.TempTransfer;
import com.hand.hrms4android.util.data.IndexPath;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class DoneListFragment extends BaseSherlockFragment implements OnItemClickListener {
	private PullToRefreshListView listViewWrapper;
	private ListView doneList;

	private ImageButton reloadButton;
	private TextView reloadText;

	private DoneListAdapter listAdapter;
	private AbstractPageableModel<DoneListItem> doneModel;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View base = inflater.inflate(R.layout.activity_done_list, container, false);

		return base;
	}

	private void buildViews(View base, Bundle savedInstanceState) {
		getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
		listViewWrapper = (PullToRefreshListView) base.findViewById(R.id.activity_done_listviewwrapper);
		listViewWrapper.setMode(Mode.BOTH); // mode refresh for top and
		listViewWrapper.setShowIndicator(false); // disable indicator
		listViewWrapper.setOnRefreshListener(new PulldownListener());
		doneList = listViewWrapper.getRefreshableView();
		doneList.setOnItemClickListener(this);

		reloadButton = (ImageButton) base.findViewById(R.id.activity_done_list_reload_button);
		reloadButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				model.load(LoadType.Network, null);
				reloadButton.setEnabled(false);
				getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
			}
		});
		reloadText = (TextView) base.findViewById(R.id.activity_done_list_reload_text);

		listAdapter = getAdapter();
		doneModel = new DoneListModel(this, 0, listAdapter);
		this.model = doneModel;
		doneList.setAdapter(listAdapter);

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		buildViews(view, savedInstanceState);
		load(LoadType.Network);
	}

	/**
	 * 
	 */
	private void load(LoadType type) {
		model.load(type, null);
		getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);

	}

	@Override
	public void modelDidFinishedLoad(Model model) {
		getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);

		listViewWrapper.onRefreshComplete();

		if (listAdapter.getCount() == 0) {
			showEmptyTip(getResources().getString(R.string.activity_done_list_fragment_no_matters));
		} else {
			showList();
		}
	}

	@Override
	public void modelFailedLoad(Exception e, Model<? extends Object> model) {
		super.modelFailedLoad(e, model);
		getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);
		listViewWrapper.onRefreshComplete();
	}

	private DoneListAdapter getAdapter() {
		if (listAdapter == null) {
			listAdapter = new DoneListAdapter(getActivity());
		}
		return listAdapter;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		position -= 1;// 排除header坐标
		TempTransfer.container.put(TempTransfer.KEY_TODO_LIST_MODEL, model);
		doneModel.setRecordAsSelected(new IndexPath(0, position));
		startActivity(new Intent(getActivity(), DoneReceiptActivity.class));
	}

	private class PulldownListener implements OnRefreshListener<ListView> {

		@Override
		public void onRefresh(PullToRefreshBase<ListView> refreshView) {
			if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
				load(LoadType.Network);
			} else {
				load(LoadType.NetworkMore);
			}

		}
	}

	/**
	 * 显示列表组件
	 */
	private void showList() {
		listViewWrapper.setVisibility(View.VISIBLE);
		listViewWrapper.bringToFront();
		reloadButton.setVisibility(View.INVISIBLE);
		reloadText.setVisibility(View.INVISIBLE);
	}

	private void showEmptyTip(String message) {
		listViewWrapper.setVisibility(View.INVISIBLE);
		reloadButton.setVisibility(View.VISIBLE);
		reloadButton.setEnabled(true);
		reloadButton.bringToFront();
		reloadText.setVisibility(View.VISIBLE);
		reloadText.setText(message);
		reloadText.bringToFront();
	}

}
