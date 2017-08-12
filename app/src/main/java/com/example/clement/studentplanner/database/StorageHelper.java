package com.example.clement.studentplanner.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Clement on 8/6/2017.
 */

public class StorageHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String TERM_TABLE = "term";
    public static final String TERM_ID = "_id";
    public static final String TERM_NAME = "name";
//    public static final String TERM_NUMBER = "number";
    public static final String TERM_START = "start";
    public static final String TERM_END = "end";
    public static final String[] TERM_COLUMNS = {
        TERM_ID, TERM_NAME, TERM_START, TERM_END
};
    private static final String CREATE_TERM_TABLE = String.format(
        "CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT);",
        TERM_TABLE,
        TERM_ID,
        TERM_NAME,
        TERM_START,
        TERM_END
    );
    public static final String COURSE_TABLE = "course";
    public static final String COURSE_ID = "_id";
    public static final String COURSE_NAME = "name";
    public static final String COURSE_START = "start";
    public static final String COURSE_END = "end";
    public static final String COURSE_TERM_ID = "term_id";
    public static final String COURSE_STATUS = "status";
    public static final String[] COURSE_COLUMNS = {
        COURSE_ID, COURSE_NAME, COURSE_START, COURSE_END, COURSE_TERM_ID, COURSE_STATUS
    };
    public static final String CREATE_COURSE_TABLE = String.format(
        "CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s INTEGER, %s INTEGER);",
        COURSE_TABLE, COURSE_ID, COURSE_NAME, COURSE_START, COURSE_END, COURSE_TERM_ID, COURSE_STATUS
    );
    public StorageHelper(Context context) {
        super(context, "schedule", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TERM_TABLE);
        db.execSQL(CREATE_COURSE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            db.execSQL(CREATE_COURSE_TABLE);
        }
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == 1) {
            eraseTable(COURSE_TABLE, db);
        }
    }
    public void erase(SQLiteDatabase db) {
        eraseTable(TERM_TABLE, db);
        eraseTable(COURSE_TABLE, db);
        onCreate(db);
    }
    private void eraseTable(String table, SQLiteDatabase db) {
        db.execSQL("DROP TABLE " + table + ";");
    }
}
