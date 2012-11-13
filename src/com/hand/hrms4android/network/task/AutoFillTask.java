package com.hand.hrms4android.network.task;

import java.util.List;

import com.hand.hrms4android.network.DefaultHDClient;
import com.hand.hrms4android.network.NetworkException;
import com.hand.hrms4android.network.handler.HDHandler;
import com.hand.hrms4android.network.request.AutoFillRequest;
import com.hand.hrms4android.network.response.AutoFillResponse;
import com.hand.hrms4android.network.response.entity.AutoFillRecord;

import android.os.Message;

public class AutoFillTask extends HDTask {

	private String key;
	private List<AutoFillRecord> lst;

	public AutoFillTask(String key) {
		this.key = key;
	}

	public AutoFillTask(String key, HDHandler handler) {
		this(key);
		mHandler = handler;
	}

	protected void setup() {
		if (mHandler == null) {
			// mHandler = new AutoFillHandler();
		}
	}

	@Override
	protected void perform() throws NetworkException {
		// TODO Auto-generated method stub
		AutoFillRequest req = new AutoFillRequest();
		req.setKeyword(key);

		DefaultHDClient client = new DefaultHDClient();
		AutoFillResponse response = client.execute(req);
		lst = response.getAutoFillRecords();
	}

	@Override
	protected void completed() {
		// TODO Auto-generated method stub

		Message msg = new Message();
		msg.what = HDHandler.OK;
		msg.obj = lst;
		mHandler.sendMessage(msg);
	}
}
