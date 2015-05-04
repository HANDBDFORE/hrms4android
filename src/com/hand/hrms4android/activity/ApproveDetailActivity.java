package com.hand.hrms4android.activity;

import java.net.URLEncoder;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.cfca.srcbulanview.SigActivity;
import com.cfist.mobile.ulan.UlanKey;
import com.cfist.mobile.ulan.util.Consts;
import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.dao.TodoListDao;
import com.hand.hrms4android.ems.R;
import com.hand.hrms4android.exception.ParseExpressionException;
import com.hand.hrms4android.listable.doman.TodoListDomain;
import com.hand.hrms4android.model.ApproveDetailActionModel;
import com.hand.hrms4android.model.LoginModel;
import com.hand.hrms4android.model.Model;
import com.hand.hrms4android.model.Model.LoadType;
import com.hand.hrms4android.network.NetworkUtil;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.persistence.DataBaseMetadata.TodoList;
import com.hand.hrms4android.pojo.ApproveAction;
import com.hand.hrms4android.util.Constrants;

public class ApproveDetailActivity extends BaseReceiptActivity<TodoListDomain> {
	private static final int REQUEST_ACTIVITY_OPINION = 1;
	private static final int REQUEST_ACTIVITY_DELIVER = 2;
	
	private static final int RESULT_SIGNATURE = 300;

	private List<ApproveAction> actions;
	private String urlKeyName;
	private String signature;
	private String p_record_id;
	
	/*加密*/
	private int connectType;
	String certType;
	String signHash;
	String pin;
	String signFormat;
	
	Bundle savedBundle;

	@Override
	protected void afterSuperOnCreateFinish(Bundle savedInstanceState) {

		if(listModel != null)
			loadResources(listModel.currentItem());
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == RESULT_SIGNATURE){
			if(data == null) return;
			if(data.getBooleanExtra(SigActivity.SIGNATURE_SUCCESS, false)){
				// 再退回
				Bundle bundle = savedBundle;
				// 将当前选中行的本地recordpk传回，用于标示
				bundle.putString(TodoList.ID, String.valueOf(listModel.currentItem().getId()));
				String signature_result = data.getStringExtra(SigActivity.SIGNATURE_RESULT); 
				bundle.putString("signature_result",signature_result);
				TodoListDao dao = new TodoListDao();
				String id = String.valueOf(listModel.currentItem().getId());
				dao.updateSignatureRecord(listModel.currentItem().getId(), signature_result);
				HrmsApplication.getApplication().setSignatureResult(signature_result);
				Intent result = new Intent();
				result.putExtras(bundle);
				setResult(RESULT_OK, result);
				finish();
			}else{
				HrmsApplication.getApplication().setPinCode(null);
				showErrorMsg();
			}
		}else if (requestCode == REQUEST_ACTIVITY_OPINION || requestCode == REQUEST_ACTIVITY_DELIVER) {
			if (resultCode == RESULT_OK) {
				
				if( signature != null && !signature.equals("null")){
					savedBundle = data.getExtras();
					String signatureString = signature+"{"+ data.getStringExtra("title") +"}";
					//检查PIN
					byte[] signatureData = signatureString.getBytes();
					//连接方式
					connectType = UlanKey.BLE;
					//证书类型
					certType = Consts.ALGORITHM_RSA2048;
					//hash算法
					signHash = Consts.ALGORITHM_SHA1;
					//签名格式
					signFormat = Consts.SignFormat_PKCS7Att;
					//Pin码
					pin = HrmsApplication.getApplication().getPinCode();
					
					
					Intent intent = new Intent();
					intent.setClass(ApproveDetailActivity.this, SigActivity.class);
					
					intent.putExtra(SigActivity.SIGNATURE_CONNECT_TYPE, connectType);
					if(certType == null) {
						intent.putExtra(SigActivity.SIGNATURE_ACTION, SigActivity.ACTION_SIGN_AUTO);
					}
					else {
						intent.putExtra(SigActivity.SIGNATURE_ACTION, SigActivity.ACTION_SIGN_MANUAL);
						intent.putExtra(SigActivity.SIGNATURE_CERT_TYPE, certType);
						intent.putExtra(SigActivity.SIGNATURE_HASH, signHash);
					}
													
					intent.putExtra(SigActivity.SIGNATURE_DATA, signatureData);
					intent.putExtra(SigActivity.SIGNATURE_KEYID, HrmsApplication.getApplication().getKeyId());
					intent.putExtra(SigActivity.SIGNATURE_FORMAT, signFormat);
					intent.putExtra(SigActivity.SIGNATURE_PINCODE, pin);
					ApproveDetailActivity.this.startActivityForResult(intent, RESULT_SIGNATURE);
					return;
				}
				// 再退回
				Bundle bundle = data.getExtras();
				// 将当前选中行的本地recordpk传回，用于标示
				bundle.putString(TodoList.ID, String.valueOf(listModel.currentItem().getId()));
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
	public void modelDidFinishedLoad(Model model) {
		actions = (List<ApproveAction>) model.getProcessData();
		signature = ((ApproveDetailActionModel) model).getSignature();
		p_record_id = ((ApproveDetailActionModel) model).getPRecordId();
		HrmsApplication.getApplication().setPRecordId(p_record_id);
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
			i.putExtra("sourceSystemName", listModel.currentItem().getSourceSystemName());
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
			// info.cardInfoUrl =
			// PlaceHolderReplacer.replaceForValue(listModel.currentItem(),
			// configUrl);
			info.cardInfoUrl = "";
		} catch (ParseExpressionException e) {
			e.printStackTrace();
			info.cardInfoUrl = "";
		}

		try {
			String actionItemTextKeyName = configReader.getAttr(new Expression(
			        "/config/application/activity[@name='approve_detail_activity']/view/employee_action_item", "text"));
			// info.displayName =
			// PlaceHolderReplacer.replaceForValue(listModel.currentItem(),
			// actionItemTextKeyName);
			// TODO 决定是否需要
			info.displayName = "";
		} catch (ParseExpressionException e) {
			e.printStackTrace();
			info.displayName = "";
		}
		return info;
	}

	private String getAbsolutePageUrl(TodoListDomain record) {
		StringBuilder sb = new StringBuilder(record.getScreenName());
		if (record.getScreenName().contains("?")) {
			sb.append("&");
		} else {
			sb.append("?");
		}

		sb.append("sourceSystemName=" + URLEncoder.encode(record.getSourceSystemName()));
		sb.append("&");
		sb.append("localId=" + URLEncoder.encode(record.getLocalId()));

		String token = PreferenceManager.getDefaultSharedPreferences(this).getString(Constrants.SYS_PREFRENCES_TOKEN,
		        "");
		if (token.length() > 0) {
			sb.append("&");
			sb.append("token=" + URLEncoder.encode(token));
		}
		return NetworkUtil.getAbsoluteUrl(sb.toString());
	}

	private void loadResources(TodoListDomain record) {
		// 拿到当前指向的记录
		loadingProgress.setVisibility(View.VISIBLE);
		setTitle(record.getItem1());
		this.model = new ApproveDetailActionModel(0, this);
		this.model.load(LoadType.Network, record);
		String url = getAbsolutePageUrl(record);
		contentWebView.loadUrl(url);
	}

	private ActionMenuItem menuItemFactory(ApproveAction action) {
		if (action.actionType.equalsIgnoreCase(ApproveAction.ACTION_TYPE_APPROVE)) {
			return new AgreeActionMenuItem(action);
		} else if (action.actionType.equalsIgnoreCase(ApproveAction.ACTION_TYPE_REJECT)) {
			return new RejectActionMenuItem(action);
		} else if (action.actionType.equalsIgnoreCase(ApproveAction.ACTION_TYPE_DELIVER)) {
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
			intent.putExtra(TodoList.ACTION, action.action);
			intent.putExtra(TodoList.ACTION_TYPE, action.actionType);
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
	
	private void showErrorMsg(){
		Toast.makeText(ApproveDetailActivity.this, "有错误发生，请检查BlueTooth是否打开", Toast.LENGTH_SHORT).show();
	}	
}
