package com.hand.hrms4android.network.handler;

import android.os.Handler;
import android.os.Message;

public class HDHandler extends Handler {
	
	public static final int OK = 0;
	public static final int EXCEPTION = 1;
	public static final int CANCEL = 2;
	
	public void handleMessage(Message msg) {
		switch(msg.what)
		{
		case EXCEPTION:
			System.out.println("B5MHandler:EXCEPTION");
			break;
		case CANCEL:
			System.out.println("B5MHandler:CANCEL");
			break;
		default:
			break;
		}
	}
}
