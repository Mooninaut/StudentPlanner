package com.example.clement.studentplanner.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Clement on 8/6/2017.
 */

public class StorageHelper extends SQLiteOpenHelper {
    public static final String TERM_TABLE = "term";
    public static final String TERM_ID = "_id";
    public static final String TERM_NAME = "name";
//    public static final String TERM_NUMBER = "number";
    public static final String TERM_START = "start";
    public static final String TERM_END = "end";
    public static final String[] TERM_COLUMNS = {
        TERM_ID, TERM_NAME, TERM_START, TERM_END
    };
    private static final String CREATE_DATABASE = String.format(
        "CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT);",
        TERM_TABLE,
        TERM_ID,
        TERM_NAME,
//        TERM_NUMBER,
        TERM_START,
        TERM_END
    );
    public StorageHelper(Context context) {
        super(context, "schedule", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void erase(SQLiteDatabase db) {
        db.execSQL("DROP TABLE " + TERM_TABLE + ";");
        onCreate(db);
    }
}
