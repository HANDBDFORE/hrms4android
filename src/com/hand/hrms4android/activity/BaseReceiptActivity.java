package com.hand.hrms4android.activity;

import java.util.Map;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.hand.hrms4android.R;
import com.hand.hrms4android.model.AbstractPageableModel;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.hand.hrms4android.util.TempTransfer;
import com.hand.hrms4android.widget.EmployeeCardDialog;

import android.app.Dialog;
import android.os.Bundle;

public class BaseReceiptActivity<E> extends HTMLBaseActivity {
	protected static final int GROUP_ACTION = 1;
	protected static final int GROUP_TOOLS = 101;

	protected Dialog employeeCard;
	protected ConfigReader configReader;
	protected AbstractPageableModel<E> listModel;

	@Override
	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
		configReader = XmlConfigReader.getInstance();
		listModel = (AbstractPageableModel<E>) TempTransfer.container
		        .get(TempTransfer.KEY_TODO_LIST_MODEL);
		TempTransfer.container.remove(TempTransfer.KEY_TODO_LIST_MODEL);

		super.onCreate(savedInstanceState);
	}

	/**
	 * 用来构建工具栏中显示的员工名称
	 * 
	 * @return
	 */
	protected EmployeeCardInfo getEmployeeCardParams() {
		// 子类应该实现此方法，用来构建工具栏中显示的员工名称
		return new EmployeeCardInfo("", "");
	}

	protected void onPageableOptionsItemSelected(MenuItem item) {
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		String displayEmployeeName = "";
		// 组装名字
		if (listModel.currentItem() != null) {
			displayEmployeeName = getEmployeeCardParams().displayName;
		}

		menu.add(GROUP_ACTION, R.id.approve_detail_employee, GROUP_ACTION - 1, displayEmployeeName).setShowAsAction(
		        MenuItem.SHOW_AS_ACTION_ALWAYS);

		int toolIndex = GROUP_TOOLS;
		menu.add(GROUP_TOOLS, R.id.approve_detail_tool_previous, toolIndex++, "上一条").setShowAsAction(
		        MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(GROUP_TOOLS, R.id.approve_detail_tool_next, toolIndex, "下一条").setShowAsAction(
		        MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.approve_detail_employee: {
			// 显示名片

			if (employeeCard == null) {
				employeeCard = new EmployeeCardDialog(this,
				        NetworkUtil.getAbsoluteUrl(getEmployeeCardParams().cardInfoUrl),
				        R.style.employee_card_dialog_style);
			}
			employeeCard.show();
			break;
		}

		case R.id.approve_detail_tool_previous: {
			// 向前翻
			if (listModel.hasPrevious()) {
				listModel.previous();
				onPageableOptionsItemSelected(item);
				employeeCard = null;
			}
			break;
		}

		case R.id.approve_detail_tool_next: {
			// 向后翻
			if (listModel.hasNext()) {
				listModel.next();
				onPageableOptionsItemSelected(item);
				employeeCard = null;
			}
			break;
		}

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	protected class EmployeeCardInfo {
		String displayName;
		String cardInfoUrl;

		public EmployeeCardInfo() {
		}

		public EmployeeCardInfo(String displayName, String cardInfoUrl) {
			this.displayName = displayName;
			this.cardInfoUrl = cardInfoUrl;
		}

	}

}
