package com.hand.hrms4android.persistence;

public final class DataBaseMetadata {
	public static class TableTodoListColumnMetadata {
		/**
		 * 头表表名
		 */
		public static final String TABLENAME = "todo_column";

		public static final String COLUMN_TODO_COLUMN_KEY = "todo_column_key";
		public static final String COLUMN_TODO_COLUMN_VALUE_ID = "todo_column_value_id";

	}

	public static class TableTodoListValueMetadata {
		public static final String TABLENAME = "todo_value";
		public static final String COLUMN_TODO_VALUE_PHYSICAL_PK = "_todo_value_id";
		public static final String COLUMN_TODO_VALUE_LOGICAL_PK = "todo_value_0";
		public static final String COLUMN_TODO_VALUE_STATUS = "todo_value_1";
		public static final String COLUMN_TODO_VALUE_SERVERMESSAGE = "todo_value_2";
		public static final String COLUMN_TODO_VALUE_ACTION = "todo_value_3";

		public static final String COLUMN_TODO_VALUE_COMMENTS = "todo_value_4";

		/**
		 * 转交人
		 */
		public static final String COLUMN_TODO_VALUE_EMPLOYEE_ID = "todo_value_5";
	}

	// todo_column_logical_pk text, action_id text, action_type text,
	// action_title text
	public static class TableActions {
		public static final String TABLENAME = "actions";

		public static final String COLUMN_TODO_COLUMN_LOGICAL_PK = "record_key";
		public static final String COLUMN_ACTION_ID = "action_id";
		public static final String COLUMN_ACTION_TYPE = "action_type";
		public static final String COLUMN_ACTION_TITLE = "action_title";
	}

	public static class TodoListLogical {
		public static final String STATUS = "status";
		public static final String SERVER_MESSAGE = "serverMessage";
		public static final String ACTION = "action_id";
		public static final String COMMENTS = "comments";

		/**
		 * 转交人
		 */
		public static final String EMPLOYEE_ID = "employee_id";
	}

}
