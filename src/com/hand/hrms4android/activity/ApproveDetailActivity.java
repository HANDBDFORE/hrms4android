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
import com.hand.hrms4android.model.TodoListModel;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.hand.hrms4android.persistence.DataBaseMetadata;
import com.hand.hrms4android.pojo.ApproveAction;
import com.hand.hrms4android.util.PlaceHolderReplacer;
import com.hand.hrms4android.util.TempTransfer;
import com.hand.hrms4android.widget.EmployeeCardDialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;

public class ApproveDetailActivity extends ActionBarActivity {

	private static final int REQUEST_ACTIVITY_OPINION = 1;
	private static final int REQUEST_ACTIVITY_DELIVER = 2;
	private static final int GROUP_ACTION = 100;
	private static final int GROUP_TOOLS = 101;

	private WebView contentWebView;
	private RelativeLayout rootView;
	private Dialog employeeCard;
	private ConfigReader configReader;
	private TodoListModel todoListModel;
	private Map<String, String> currentRowData;
	private List<ApproveAction> actions;
	private String urlKeyName;
	private String actionItemTextKeyName;

	// 捕获手指位置
	private float fingerX, fingerY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_approve_detail);
		bindAllViews();
		buildResources();
	}

	private void bindAllViews() {
		// LinearLayout rootView = (LinearLayout)
		// findViewById(R.id.activity_approve_detail_container);
		contentWebView = (WebView) findViewById(R.id.activity_approve_detail_content_webview);
		rootView = (RelativeLayout) findViewById(R.id.activity_approve_detail_container);

	}

	private void buildResources() {
		// 拿到前个页面的model
		todoListModel = (TodoListModel) TempTransfer.container.get(TempTransfer.KEY_TODO_LIST_MODEL);
		// TempTransfer.container.put(TempTransfer.KEY_TODO_LIST_MODEL, null);

		configReader = XmlConfigReader.getInstance();

		// 设置缓存策略
		WebSettings webSettings = contentWebView.getSettings();
		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

		try {
			urlKeyName = configReader
			        .getAttr(new Expression(
			                "/config/activity[@name='todo_list_activity']/request/url[@name='todo_list_query_url']/detail_page_url_column",
			                "name"));

			actionItemTextKeyName = configReader.getAttr(new Expression(
			        "/config/activity[@name='approve_detail_activity']/view/employee_action_item", "text"));

			loadComponentValues();
		} catch (ParseExpressionException e) {
			// TODO 写读取失败的提示
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private void loadComponentValues() {
		// 拿到当前指向的记录
		currentRowData = todoListModel.currentItem();
		String pageURL = NetworkUtil.getAbsoluteUrl(currentRowData.get(urlKeyName));
		
		contentWebView.loadUrl(pageURL);
		this.model = new ApproveDetailActionModel(0, this);
		this.model.load(LoadType.Network, currentRowData);

	}

	@Override
	@SuppressWarnings("unchecked")
	public void modelDidFinishedLoad(Model model) {
		actions = (List<ApproveAction>) model.getProcessResult();
		super.invalidateOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		int menuItemBaseOrder = 0;
		String displayEmployeeName = "";
		// 组装名字
		if (currentRowData != null) {
			displayEmployeeName = PlaceHolderReplacer.replaceForValue(currentRowData, actionItemTextKeyName);
		}

		menu.add(GROUP_ACTION, R.id.approve_detail_employee, menuItemBaseOrder++, displayEmployeeName).setShowAsAction(
		        MenuItem.SHOW_AS_ACTION_ALWAYS);

		if (actions == null || actions.size() == 0) {
			return super.onCreateOptionsMenu(menu);
		} else {

			// for (int i = 0; i < actions.size(); i++) {
			// ApproveAction action = actions.get(i);
			//
			// MenuItem item = menu.add(GROUP_ACTION,
			// R.id.approve_detail_action, menuItemBaseOrder++,
			// action.actionTitle);
			// item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS |
			// MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			//
			// Intent intent = new Intent();
			// intent.putExtra(DataBaseMetadata.TodoListLogical.ACTION,
			// action.actionId);
			// item.setIntent(intent);
			//
			// if (action.actionType.equals(ApproveAction.ACTION_TYPE_APPROVE))
			// {
			// item.setIcon(R.drawable.ic_menu_mark);
			// } else if
			// (action.actionType.equals(ApproveAction.ACTION_TYPE_REJECT)) {
			// item.setIcon(R.drawable.ic_menu_block);
			// } else if
			// (action.actionType.equals(ApproveAction.ACTION_TYPE_DELIVER)) {
			// item.setIcon(R.drawable.ic_menu_cc);
			// } else {
			// item.setIcon(R.drawable.ic_compose);
			// }
			// }

			for (int i = 0; i < actions.size(); i++) {
				ApproveAction action = actions.get(i);

				MenuItem item = null;

				Intent intent = new Intent();
				intent.putExtra(DataBaseMetadata.TodoListLogical.ACTION, action.actionId);

				if (action.actionType.equals(ApproveAction.ACTION_TYPE_APPROVE)) {
					item = menu.add(GROUP_ACTION, R.id.approve_detail_action, menuItemBaseOrder++, action.actionTitle);
					item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
					item.setIcon(R.drawable.ic_approve_agree);
				} else if (action.actionType.equals(ApproveAction.ACTION_TYPE_REJECT)) {
					item = menu.add(GROUP_ACTION, R.id.approve_detail_action, menuItemBaseOrder++, action.actionTitle);
					item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
					item.setIcon(R.drawable.ic_approve_refuse);
				} else if (action.actionType.equals(ApproveAction.ACTION_TYPE_DELIVER)) {
					item = menu.add(GROUP_ACTION, R.id.approve_detail_action_deliver, menuItemBaseOrder++,
					        action.actionTitle);
					item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
					item.setIcon(R.drawable.ic_menu_cc);
					intent.putExtra(DataBaseMetadata.TodoListLogical.ACTION, "D");
				} else {
					item = menu.add(GROUP_ACTION, R.id.approve_detail_action, menuItemBaseOrder++, action.actionTitle);
					item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
					item.setIcon(R.drawable.ic_compose);
				}
				intent.putExtra(ApproveOpinionActivity.EXTRA_TITLE, item.getTitle());
				item.setIntent(intent);

			}

			menuItemBaseOrder += (actions.size() + 1);

			menu.add(GROUP_TOOLS, R.id.approve_detail_tool_previous, menuItemBaseOrder++, "上一条").setShowAsAction(
			        MenuItem.SHOW_AS_ACTION_IF_ROOM);
			menu.add(GROUP_TOOLS, R.id.approve_detail_tool_next, menuItemBaseOrder, "下一条").setShowAsAction(
			        MenuItem.SHOW_AS_ACTION_IF_ROOM);

			return true;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.approve_detail_employee: {
			// 显示名片
			try {
				String configUrl = configReader.getAttr(new Expression(
				        "/config/activity[@name='approve_detail_activity']/request/url[@name='employee_card_url']",
				        "value"));
				String completeUrl = PlaceHolderReplacer.replaceForValue(currentRowData, configUrl);

				if (employeeCard == null) {
					employeeCard = new EmployeeCardDialog(this, NetworkUtil.getAbsoluteUrl(completeUrl),
					        R.style.employee_card_dialog_style);

				}

				employeeCard.show();

			} catch (ParseExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}

		case R.id.approve_detail_tool_previous: {
			// 向前翻
			if (todoListModel.hasPrevious()) {
				todoListModel.previous();
				loadComponentValues();
				employeeCard = null;
			}
			break;
		}

		case R.id.approve_detail_tool_next: {
			// 向后翻
			if (todoListModel.hasNext()) {
				todoListModel.next();
				loadComponentValues();
				employeeCard = null;
			}
			break;
		}

		case R.id.approve_detail_action: {
			Intent i = item.getIntent();
			i.setClass(this, ApproveOpinionActivity.class);
			startActivityForResult(i, REQUEST_ACTIVITY_OPINION);
			break;
		}

		case R.id.approve_detail_action_deliver: {
			Intent i = item.getIntent();
			i.setClass(this, DeliverActivity.class);
			startActivityForResult(i, REQUEST_ACTIVITY_DELIVER);
			break;
		}

		case android.R.id.home: {
			setResult(RESULT_CANCELED);
			finish();
			break;
		}

		default:
			break;
		}

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ACTIVITY_OPINION || requestCode == REQUEST_ACTIVITY_DELIVER) {
			if (resultCode == RESULT_OK) {
				// 再退回
				Bundle bundle = data.getExtras();
				// 将当前选中行的本地recordpk传回，用于标示
				bundle.putString(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_PHYSICAL_PK,
				        currentRowData.get(DataBaseMetadata.TableTodoListValueMetadata.COLUMN_TODO_VALUE_PHYSICAL_PK));
				Intent result = new Intent();
				result.putExtras(bundle);
				setResult(RESULT_OK, result);

				finish();
			} else {

			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	// TODO 研究上下翻页的长按操作
	private class WebViewLongPressListener implements OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
			        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.leftMargin = (int) fingerX;
			params.topMargin = (int) fingerY;
			Button b = new Button(ApproveDetailActivity.this);
			b.setLayoutParams(params);
			rootView.addView(b);
			rootView.invalidate();

			return true;
		}
	}
}
