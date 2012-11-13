package com.hand.hrms4android.network.task;

import com.hand.hrms4android.network.DefaultHDClient;
import com.hand.hrms4android.network.NetworkException;
import com.hand.hrms4android.network.handler.HDHandler;

public abstract class HDTask {

	protected DefaultHDClient client = new DefaultHDClient();
	private boolean flag = true;
	protected HDHandler mHandler;

	public boolean cancel() {
		flag = false;
		return true;
	}

	public void start() {
		// 初始化
		setup();

		Thread t = new Thread() {
			@Override
			public void run() {

				try {
					perform();
				} catch (NetworkException e) {
					e.printStackTrace();
					exception(e);
					return;
				}

				// 若正常結束,調用completed
				if (flag) {
					completed();
				} else {
					cancelCompleted();
				}
			}
		};

		t.start();
		return;
	}

	public boolean syncRun() {
		// 初始化
		setup();

		try {
			perform();
		} catch (NetworkException e) {
			return false;
		}

		completed();
		return true;
	}

	protected void setup() {
		if (null == mHandler) {
			mHandler = new HDHandler();
		}
	}

	protected abstract void perform() throws NetworkException;

	protected abstract void completed();

	protected void exception(NetworkException e) {
		mHandler.sendEmptyMessage(HDHandler.EXCEPTION);
	}

	protected void cancelCompleted() {
		mHandler.sendEmptyMessage(HDHandler.CANCEL);
	}
}
