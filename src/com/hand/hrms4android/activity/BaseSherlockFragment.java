package com.hand.hrms4android.activity;

import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.hand.hrms4android.model.Model;
import com.hand.hrms4android.util.LogUtil;

public class BaseSherlockFragment extends SherlockFragment implements ModelActivity {
	private static final String TAG = "";
	protected Model<?> model;

	@Override
	public void modelDidFinishedLoad(Model<?> model) {

	}

	@Override
	public void modelFailedLoad(Exception e, Model<? extends Object> model) {
		Toast.makeText(this.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
		LogUtil.error(this, TAG, e.getMessage());
	}

	@Override
	public void setModel(Model<? extends Object> model) {
		this.model = model;
		model.load(Model.LoadType.Network, null);
	}

}
