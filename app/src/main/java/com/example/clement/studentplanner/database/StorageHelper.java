package com.example.clement.studentplanner.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Clement on 8/6/2017.
 */

public class StorageHelper extends SQLiteOpenHelper {
    public enum Type {
        TERM, COURSE, ASSESSMENT, NONE
    }

    public static final int DATABASE_VERSION = 15;
    public static final String TABLE_TERM = "term";
    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_NAME = "name";
//    public static final String COLUMN_EVENT_ID = "event_id";
    public static final String COLUMN_NUMBER = "number";
    public static final String COLUMN_START = "start";
    public static final String COLUMN_END = "end";
    public static final String COLUMN_TIME = "time";
    public static final String[] COLUMNS_TERM = {
        COLUMN_ID, COLUMN_NAME, COLUMN_START, COLUMN_END, COLUMN_NUMBER
};
    public static final String TABLE_COURSE = "course";

    public static final String COLUMN_TERM_ID = TABLE_TERM + BaseColumns._ID;
    public static final String COLUMN_STATUS = "status";
    public static final String[] COLUMNS_COURSE = {
        COLUMN_ID, COLUMN_NAME, COLUMN_START, COLUMN_END, COLUMN_TERM_ID, COLUMN_STATUS
    };
    public static final String TABLE_ASSESSMENT = "assessment";
    public static final String COLUMN_COURSE_ID = TABLE_COURSE + BaseColumns._ID;
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_NOTES = "notes";

    public static final String[] COLUMNS_ASSESSMENT = {
        COLUMN_ID, COLUMN_NAME, COLUMN_START, COLUMN_END, COLUMN_COURSE_ID, COLUMN_TYPE, COLUMN_NOTES
    };
    public static final String TABLE_PHOTO = "photo";
    public static final String COLUMN_ASSESSMENT_ID = TABLE_ASSESSMENT + BaseColumns._ID;
    public static final String COLUMN_FILE_NAME = "file_name";
    public static final String[] COLUMNS_PHOTO = {
        COLUMN_ID, COLUMN_ASSESSMENT_ID, COLUMN_FILE_NAME
    };

    public static final String[] COLUMNS_EVENT = {
        COLUMN_ID, COLUMN_NAME, COLUMN_TIME, COLUMN_TYPE
    };
    public static final String VIEW_EVENT = "eventView";
    private static final String SELECT_EVENT_START = "SELECT "+COLUMN_ID+"*2 AS "+COLUMN_ID+", "
        +COLUMN_NAME+", "+COLUMN_START+" AS "+COLUMN_TIME+", '"+COLUMN_START+"' AS "+COLUMN_TYPE+" FROM ";
    private static final String SELECT_EVENT_END = "SELECT "+COLUMN_ID+"*2+1 AS "+COLUMN_ID+", "
        +COLUMN_NAME+", "+COLUMN_END+" AS "+COLUMN_TIME+", '"+COLUMN_END+"' AS "+COLUMN_TYPE+" FROM ";
    public static final int TERM_ID_OFFSET = 10_000_000;
    public static final int COURSE_ID_OFFSET = 20_000_000;
    public static final int ASSESSMENT_ID_OFFSET = 30_000_000;
    private static final String[] CREATE_DATABASE;

    public static final String DATABASE_FILE_NAME = "studentplanner.sqlite3";

    static {
        ArrayList<String> schema = new ArrayList<String>(20);
        schema.add("DROP VIEW IF EXISTS "+VIEW_EVENT);
        schema.add("DROP TABLE IF EXISTS "+TABLE_PHOTO);
        schema.add("DROP TABLE IF EXISTS "+TABLE_ASSESSMENT);
        schema.add("DROP TABLE IF EXISTS "+TABLE_COURSE);
        schema.add("DROP TABLE IF EXISTS "+TABLE_TERM);
//        schema.add("DROP TABLE IF EXISTS event");
//        schema.add("CREATE TABLE event (_id INTEGER PRIMARY KEY, name TEXT, start INTEGER, end INTEGER)");
//        schema.add("CREATE TABLE term (_id INTEGER PRIMARY KEY, event_id INTEGER REFERENCES event(_id))");

        schema.add("CREATE TABLE "+TABLE_TERM+" ("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            COLUMN_NAME+" TEXT, "+COLUMN_START+" INTEGER, "+COLUMN_END+" INTEGER, "+COLUMN_NUMBER+" INTEGER)");
        schema.add("INSERT INTO "+TABLE_TERM+" ("+COLUMN_ID+", "+COLUMN_NAME+", "+COLUMN_START+", "+
            COLUMN_END+", "+COLUMN_NUMBER+") VALUES ("+ TERM_ID_OFFSET +", 'A', 1, 1, 1);");
//        schema.add("CREATE TABLE course (_id INTEGER PRIMARY KEY, event_id INTEGER REFERENCES event(_id), term_id INTEGER REFERENCES term(_id), status INTEGER)");

        schema.add("CREATE TABLE "+TABLE_COURSE+ "("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            COLUMN_NAME+" TEXT, "+COLUMN_START+" INTEGER, "+COLUMN_END+" INTEGER, " +
            COLUMN_TERM_ID+" INTEGER REFERENCES "+TABLE_TERM+"("+COLUMN_ID+"), "+
            COLUMN_STATUS+" INTEGER)");
        schema.add("INSERT INTO "+TABLE_COURSE+" ("+COLUMN_ID+", "+COLUMN_NAME+", "+COLUMN_START+", "+
            COLUMN_END+", "+COLUMN_TERM_ID+", "+COLUMN_STATUS+") VALUES ("+ COURSE_ID_OFFSET +", 'B', 2, 2, "+TERM_ID_OFFSET+", 2);");

//        schema.add("CREATE TABLE assessment(_id INTEGER PRIMARY KEY, event_id INTEGER REFERENCES event(_id), course_id INTEGER REFERENCES course(_id), type INTEGER, notes TEXT)");

        schema.add("CREATE TABLE "+TABLE_ASSESSMENT+"("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            COLUMN_NAME+" TEXT, "+COLUMN_START+" INTEGER, "+COLUMN_END+" INTEGER, "+
            COLUMN_COURSE_ID+" INTEGER REFERENCES "+TABLE_COURSE+"("+COLUMN_ID+"), "+
            COLUMN_TYPE+" INTEGER, "+COLUMN_NOTES+" TEXT)");
        schema.add("INSERT INTO "+TABLE_ASSESSMENT+" ("+COLUMN_ID+", "+COLUMN_NAME+", "+COLUMN_START+", "+
            COLUMN_END+", "+COLUMN_COURSE_ID+", "+COLUMN_TYPE+", "+COLUMN_NOTES+") VALUES ("+ ASSESSMENT_ID_OFFSET +", 'C', 3, 3, "+COURSE_ID_OFFSET+", 3, 'C');");
        schema.add("CREATE TABLE "+TABLE_PHOTO+"("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            COLUMN_ASSESSMENT_ID+" INTEGER REFERENCES "+TABLE_ASSESSMENT+"("+COLUMN_ID+"), "+
            COLUMN_FILE_NAME+" TEXT)");
        schema.add("DELETE FROM "+TABLE_ASSESSMENT+" WHERE "+COLUMN_ID+" = "+ASSESSMENT_ID_OFFSET+";");
        schema.add("DELETE FROM "+TABLE_COURSE+" WHERE "+COLUMN_ID+" = "+COURSE_ID_OFFSET+";");
        schema.add("DELETE FROM "+TABLE_TERM+" WHERE "+COLUMN_ID+" = "+TERM_ID_OFFSET+";");
        schema.add("CREATE VIEW "+VIEW_EVENT+" AS "+
            SELECT_EVENT_START + TABLE_TERM+" UNION "+SELECT_EVENT_END + TABLE_TERM+" UNION "+
            SELECT_EVENT_START + TABLE_COURSE+" UNION "+SELECT_EVENT_END + TABLE_COURSE + " UNION "+
            SELECT_EVENT_START + TABLE_ASSESSMENT+" UNION "+SELECT_EVENT_END + TABLE_ASSESSMENT+" ORDER BY "+COLUMN_TIME+" ASC;"
        );

        CREATE_DATABASE = schema.toArray(new String[schema.size()]);
    }
//    private static final String CREATE_COURSE_TABLE = String.format(
//        "CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s INTEGER, %s INTEGER);",
//        TABLE_COURSE, COURSE_ID, COURSE_NAME, COURSE_START, COURSE_END, COURSE_TERM_ID, COURSE_STATUS
//    );
    private static void createDatabase(SQLiteDatabase db) {
        Log.i(StorageHelper.class.getSimpleName(), "Creating database!");
        for (String sql : CREATE_DATABASE) {
            db.execSQL(sql);
        }
        Log.i(StorageHelper.class.getSimpleName(), "Database created!");
    }
    public StorageHelper(@NonNull Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        createDatabase(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        createDatabase(db);
    }
    /*public void erase(SQLiteDatabase db) {
        createDatabase(db);
    }*/
    @NonNull
    public static Type classify(long id) {
        if (id >= ASSESSMENT_ID_OFFSET) {
            return Type.ASSESSMENT;
        }
        if (id >= COURSE_ID_OFFSET) {
            return Type.COURSE;
        }
        if (id >= TERM_ID_OFFSET) {
            return Type.TERM;
        }
        return Type.NONE;
    }
}
