package com.hand.hrms4android.persistence;

public final class DataBaseMetadata {

	// todo_column_logical_pk text, action_id text, action_type text,
	// action_title text
	public static class TableActions {
		public static final String TABLENAME = "actions";

		public static final String COLUMN_ID = "id";

		public static final String COLUMN_TODO_LIST_ID = "todolistId";
		public static final String COLUMN_ACTION_ID = "actionId";
		public static final String COLUMN_ACTION_TYPE = "actionType";
		public static final String COLUMN_ACTION_TITLE = "actionTitle";
	}

	public static class TodoList {
		public static final String TABLENAME = "todo";

		public static final String ID = "_id";

		public static final String STATUS = "status";
		public static final String SERVER_MESSAGE = "serverMessage";
		public static final String ACTION = "action";
		public static final String ACTION_TYPE = "actionType";
		public static final String COMMENTS = "comments";

		public static final String LOCALID = "localId";
		public static final String ITEM1 = "item1";
		public static final String ITEM2 = "item2";
		public static final String ITEM3 = "item3";
		public static final String ITEM4 = "item4";
		public static final String SCREENNAME = "screenName";
		public static final String SOURCE_SYSTEM_NAME = "sourceSystemName";
		public static final String VERIFICATIONID = "verificationId";

		/**
		 * 转交人
		 */
		public static final String DELIVEREE = "deliveree";
	}

}
