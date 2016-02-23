package com.hand.hrms4android.pojo;

public class ApproveAction {
	public static final String ACTION_TYPE_APPROVE = "approve";
	public static final String ACTION_TYPE_REJECT = "reject";
	public static final String ACTION_TYPE_DELIVER = "deliver";
	public static final String ACTION_TYPE_ASSIGN = "assign";

	public int id;
	public String todolistId;// 客户端表的id
	public String actionType;
	public String action;
	public String actionTitle;

	public ApproveAction(int localId, String todolistId, String actionType, String action, String actionTitle) {
		this.id = localId;
		this.todolistId = todolistId;
		this.actionType = actionType;
		this.action = action;
		this.actionTitle = actionTitle;
	}

	public ApproveAction() {
		super();
	}

}
