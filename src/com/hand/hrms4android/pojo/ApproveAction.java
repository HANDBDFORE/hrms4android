package com.hand.hrms4android.pojo;

public class ApproveAction {
	public static final String ACTION_TYPE_APPROVE = "APPROVE";
	public static final String ACTION_TYPE_REJECT = "REJECT";
	public static final String ACTION_TYPE_DELIVER = "DELIVER";

	public int localId;
	public String recordLogicalPK;
	public String actionId;
	public String actionType;
	public String actionTitle;

	public ApproveAction() {
	}

	public ApproveAction(String recordLogicalPK, String actionId, String actionType, String actionTitle) {
		this.recordLogicalPK = recordLogicalPK;
		this.actionId = actionId;
		this.actionType = actionType;
		this.actionTitle = actionTitle;
	}

	public ApproveAction(int localId, String recordLogicalPK, String actionId, String actionType, String actionTitle) {
		super();
		this.localId = localId;
		this.recordLogicalPK = recordLogicalPK;
		this.actionId = actionId;
		this.actionType = actionType;
		this.actionTitle = actionTitle;
	}

}
