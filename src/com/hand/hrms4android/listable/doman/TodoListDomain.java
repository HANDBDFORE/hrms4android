package com.hand.hrms4android.listable.doman;

import org.json.JSONException;
import org.json.JSONObject;

public class TodoListDomain {
	private String id;
	private String status, serverMessage, action, comments;

	private String localId;
	private String item1;
	private String item2;
	private String item3;
	private String item4;
	private String sourceSystemName;

	public TodoListDomain() {
	}

	/*
	 * 
	 * { "item1" : "TestWorkflow", "item2" : "2227-05-10", "item3" : "2652",
	 * "item4" : "TestWorkflow", "localId" : 0, "sourceSystemName" : "HR" }
	 */
	public TodoListDomain(JSONObject json) throws JSONException {
		this("-1", "", "", "", "", json.getString("localId"), json.getString("item1"), json.getString("item2"), json
		        .getString("item3"), json.getString("item4"), json.getString("sourceSystemName"));
	}

	public TodoListDomain(String id, String status, String serverMessage, String action, String comments,
	        String localId, String item1, String item2, String item3, String item4, String sourceSystemName) {
		super();
		this.id = id;
		this.status = status;
		this.serverMessage = serverMessage;
		this.action = action;
		this.comments = comments;
		this.localId = localId;
		this.item1 = item1;
		this.item2 = item2;
		this.item3 = item3;
		this.item4 = item4;
		this.sourceSystemName = sourceSystemName;
	}

	public TodoListDomain(TodoListDomain old) {
		this(old.getId(), old.getStatus(), old.getServerMessage(), old.getAction(), old.getComments(),
		        old.getLocalId(), old.getItem1(), old.getItem2(), old.getItem3(), old.getItem4(), old
		                .getSourceSystemName());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getServerMessage() {
		return serverMessage;
	}

	public void setServerMessage(String serverMessage) {
		this.serverMessage = serverMessage;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getLocalId() {
		return localId;
	}

	public void setLocalId(String localId) {
		this.localId = localId;
	}

	public String getItem1() {
		return item1;
	}

	public void setItem1(String item1) {
		this.item1 = item1;
	}

	public String getItem2() {
		return item2;
	}

	public void setItem2(String item2) {
		this.item2 = item2;
	}

	public String getItem3() {
		return item3;
	}

	public void setItem3(String item3) {
		this.item3 = item3;
	}

	public String getItem4() {
		return item4;
	}

	public void setItem4(String item4) {
		this.item4 = item4;
	}

	public String getSourceSystemName() {
		return sourceSystemName;
	}

	public void setSourceSystemName(String sourceSystemName) {
		this.sourceSystemName = sourceSystemName;
	}

}
