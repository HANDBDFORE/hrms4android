package com.hand.hrms4android.core;

import android.content.Context;
import android.support.v4.app.Fragment;

public abstract class HDAbstractFragmentController extends Fragment implements ModelViewController {

	protected Model model;

	@Override
	public void modelFailedLoad(Exception e, Model model) {
		e.printStackTrace();
	}

	@Override
	public void setModel(Model model) {
		this.model = model;
	}

	@Override
	public Context getContext() {
		return this.getActivity();
	}

}
