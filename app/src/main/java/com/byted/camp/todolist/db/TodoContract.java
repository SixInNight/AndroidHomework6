package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量
    public static final String SQL_CREATE_ENTERS =
            "CREATE TABLE " + Todo.TABLE_NAME + " (" +
                    Todo._ID + " INTEGER PRIMARY KEY," +
                    Todo.COLUMN_NAME_TIME + " TEXT," +
                    Todo.COLUMN_NAME_PLACE + " TEXT," +
                    Todo.COLUMN_NAME_ISSUE + " TEXT," +
                    Todo.COLUMN_NAME_RANK + " TEXT)";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + Todo.TABLE_NAME;

    public static class Todo implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_PLACE = "place";
        public static final String COLUMN_NAME_ISSUE = "issue";
        public static final String COLUMN_NAME_RANK = "rank";
    }

    private TodoContract() {
    }

}
