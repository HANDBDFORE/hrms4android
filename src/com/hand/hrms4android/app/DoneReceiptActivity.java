package com.hand.hrms4android.app;

import java.util.HashMap;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.core.Model;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.listable.item.DoneListItem;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.Expression;

public class DoneReceiptActivity extends BaseReceiptActivity<DoneListItem> {

	@Override
	protected void afterSuperOnCreateFinish(Bundle savedInstanceState) {
		super.afterSuperOnCreateFinish(savedInstanceState);
		loadResources(getAbsolutePageUrl(listModel.currentItem()));
	}

	//FIXME 考虑如何使用 
//	@Override
//	protected EmployeeCardInfo getEmployeeCardParams() {
//		EmployeeCardInfo info = new EmployeeCardInfo();
//		try {
//			String configUrl = configReader
//			        .getAttr(new Expression(
//			                "/config/application/activity[@name='approve_detail_activity']/request/url[@name='employee_card_url']",
//			                "value"));
//			// TODO
//			// info.cardInfoUrl =
//			// PlaceHolderReplacer.replaceForValue(listModel.currentItem(),
//			// configUrl);
//			info.cardInfoUrl = "";
//		} catch (ParseExpressionException e) {
//			e.printStackTrace();
//			info.cardInfoUrl = "";
//		}
//
//		try {
//			String actionItemTextKeyName = configReader.getAttr(new Expression(
//			        "/config/application/activity[@name='approve_detail_activity']/view/employee_action_item", "text"));
//			// info.displayName =
//			// PlaceHolderReplacer.replaceForValue(listModel.currentItem(),
//			// actionItemTextKeyName);
//			info.displayName = "";
//		} catch (ParseExpressionException e) {
//			e.printStackTrace();
//			info.displayName = "";
//		}
//		return info;
//	}

	@Override
	protected void onPageableOptionsItemSelected(MenuItem item) {
		loadResources(getAbsolutePageUrl(listModel.currentItem()));
	}

	private String getAbsolutePageUrl(DoneListItem record) {
		return NetworkUtil.getAbsoluteUrl(record.getScreenName());
	}

	private void loadResources(String absoluteUrl) {
		// 拿到当前指向的记录
		loadingProgress.setVisibility(View.VISIBLE);
		contentWebView.loadUrl(absoluteUrl,HrmsApplication.getApplication().addSessionCookie(new HashMap<String, String>()));
	}

	@Override
    public void modelDidFinishedLoad(Model model) {
	    
    }
}
