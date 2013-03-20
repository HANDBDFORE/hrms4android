package com.hand.hrms4android.activity;

import java.util.List;
import java.util.Map;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.hand.hrms4android.R;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.model.ApproveDetailActionModel;
import com.hand.hrms4android.model.Model;
import com.hand.hrms4android.model.Model.LoadType;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.persistence.DataBaseMetadata;
import com.hand.hrms4android.pojo.ApproveAction;
import com.hand.hrms4android.util.PlaceHolderReplacer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ApproveDetailActivity extends BaseReceiptActivity {
	private static final int REQUEST_ACTIVITY_OPINION = 1;
	private static final int REQUEST_ACTIVITY_DELIVER = 2;

	private List<ApproveAction> actions;
	private String urlKeyName;

	@Override
	protected void afterSuperOnCreateFinish(Bundle savedInstanceState) {
		try {
			urlKeyName = configReader
			        .getAttr(new Expression(
			                "/config/application/activity[@name='todo_list_activity']/request/url[@name='todo_list_query_url']/detail_page_url_column",
			                "name"));

			loadResources(listModel.currentItem());
		} catch (ParseExpressionException e) {
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ACTIVITY_OPINION || requestCode == REQUEST_ACTIVITY_DELIVER) {
			if (resultCode == RESULT_OK) {
				// 再退回
				Bundle bundle = data.getExtras();
				// 将当前选中行的本地recordpk传回，用于标示
				bundle.putString(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_PHYSICAL_PK, listModel
				        .currentItem().get(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_PHYSICAL_PK));
				Intent result = new Intent();
				result.putExtras(bundle);
				setResult(RESULT_OK, result);
				finish();
			} else {

			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void modelDidFinishedLoad(Model model) {
		actions = (List<ApproveAction>) model.getProcessResult();
		super.invalidateOptionsMenu();
	}

	@Override
	protected void onPageableOptionsItemSelected(MenuItem item) {
		loadResources(listModel.currentItem());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (actions == null || actions.size() == 0) {
			return super.onCreateOptionsMenu(menu);
		} else {
			for (int i = 0; i < actions.size(); i++) {
				this.menuItemFactory(actions.get(i)).generateMenuItem(i + 1, menu);
			}
			return super.onCreateOptionsMenu(menu);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.approve_detail_action: {
			Intent i = item.getIntent();
			i.setClass(this, ApproveOpinionActivity.class);
			startActivityForResult(i, REQUEST_ACTIVITY_OPINION);
			return true;
		}

		case R.id.approve_detail_action_deliver: {
			Intent i = item.getIntent();
			i.setClass(this, DeliverActivity.class);
			startActivityForResult(i, REQUEST_ACTIVITY_DELIVER);
			return true;
		}

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected EmployeeCardInfo getEmployeeCardParams() {
		EmployeeCardInfo info = new EmployeeCardInfo();
		try {
			String configUrl = configReader
			        .getAttr(new Expression(
			                "/config/application/activity[@name='approve_detail_activity']/request/url[@name='employee_card_url']",
			                "value"));
			info.cardInfoUrl = PlaceHolderReplacer.replaceForValue(listModel.currentItem(), configUrl);
		} catch (ParseExpressionException e) {
			e.printStackTrace();
			info.cardInfoUrl = "";
		}

		try {
			String actionItemTextKeyName = configReader.getAttr(new Expression(
			        "/config/application/activity[@name='approve_detail_activity']/view/employee_action_item", "text"));
			info.displayName = PlaceHolderReplacer.replaceForValue(listModel.currentItem(), actionItemTextKeyName);
		} catch (ParseExpressionException e) {
			e.printStackTrace();
			info.displayName = "";
		}
		return info;
	}

	private String getAbsolutePageUrl(Map<String, String> record) {
		return NetworkUtil.getAbsoluteUrl(listModel.currentItem().get(urlKeyName));
	}

	private void loadResources(Map<String, String> record) {
		// 拿到当前指向的记录
		loadingProgress.setVisibility(View.VISIBLE);
		contentWebView.loadUrl(getAbsolutePageUrl(record));
		this.model = new ApproveDetailActionModel(0, this);
		this.model.load(LoadType.Network, record);
	}

	private ActionMenuItem menuItemFactory(ApproveAction action) {
		if (action.actionType.equals(ApproveAction.ACTION_TYPE_APPROVE)) {
			return new AgreeActionMenuItem(action);
		} else if (action.actionType.equals(ApproveAction.ACTION_TYPE_REJECT)) {
			return new RejectActionMenuItem(action);
		} else if (action.actionType.equals(ApproveAction.ACTION_TYPE_DELIVER)) {
			return new DeliverActionMenuItem(action);
		} else
			return new OtherActionMenuItem(action);
	}

	abstract class ActionMenuItem {
		protected ApproveAction action;

		public ActionMenuItem(ApproveAction action) {
			this.action = action;
		}

		/**
		 * 获取对应的menuItem
		 * 
		 * @param index
		 * @param menu
		 * 
		 * @return
		 */
		abstract MenuItem generateMenuItem(int index, Menu menu);

		/**
		 * @param action
		 * @param item
		 * @return
		 */
		protected Intent prepareIntent(ApproveAction action) {
			Intent intent = new Intent();
			intent.putExtra(DataBaseMetadata.TodoListLogical.ACTION, action.actionId);
			intent.putExtra(ApproveOpinionActivity.EXTRA_TITLE, action.actionTitle);
			return intent;
		}
	}

	class AgreeActionMenuItem extends ActionMenuItem {
		public AgreeActionMenuItem(ApproveAction action) {
			super(action);
		}

		@Override
		MenuItem generateMenuItem(int index, Menu menu) {
			MenuItem item = null;
			item = menu.add(GROUP_ACTION, R.id.approve_detail_action, index, action.actionTitle);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			item.setIcon(R.drawable.ic_approve_agree);
			item.setIntent(prepareIntent(action));
			return item;
		}
	}

	class RejectActionMenuItem extends ActionMenuItem {
		public RejectActionMenuItem(ApproveAction action) {
			super(action);
		}

		@Override
		MenuItem generateMenuItem(int index, Menu menu) {
			MenuItem item = null;
			item = menu.add(GROUP_ACTION, R.id.approve_detail_action, index, action.actionTitle);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			item.setIcon(R.drawable.ic_approve_refuse);
			item.setIntent(prepareIntent(action));
			return item;
		}
	}

	class DeliverActionMenuItem extends ActionMenuItem {
		public DeliverActionMenuItem(ApproveAction action) {
			super(action);
		}

		@Override
		MenuItem generateMenuItem(int index, Menu menu) {
			MenuItem item = null;
			item = menu.add(GROUP_ACTION, R.id.approve_detail_action_deliver, index, action.actionTitle);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			item.setIcon(R.drawable.ic_menu_cc);
			Intent intent = prepareIntent(action);
			intent.putExtra(DataBaseMetadata.TodoListLogical.ACTION, "D");
			item.setIntent(intent);
			return item;
		}
	}

	class OtherActionMenuItem extends ActionMenuItem {
		public OtherActionMenuItem(ApproveAction action) {
			super(action);
		}

		@Override
		MenuItem generateMenuItem(int index, Menu menu) {
			MenuItem item = null;
			item = menu.add(GROUP_ACTION, R.id.approve_detail_action, index, action.actionTitle);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			item.setIcon(R.drawable.ic_compose);
			item.setIntent(prepareIntent(action));
			return item;
		}
	}
}
