package com.example.clement.studentplanner.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.clement.studentplanner.data.Event.Type;

import java.util.ArrayList;

/**
 * Created by Clement on 8/6/2017.
 */

public class StorageHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 27;
    public static final String TABLE_TERM = "term";
    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_NUMBER = "number";
    public static final String COLUMN_START = "start";
    public static final String COLUMN_END = "end";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_PHOTO_FILE_URI = "file_uri";
    public static final String COLUMN_PHONE_NUMBER = "phone";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_TERMINUS = "terminus";
//    public static final String COLUMN_PARENT_URI = "parent";
    public static final String SELECT_BY_ID = COLUMN_ID + " = ?";

    public static final String[] COLUMNS_TERM = {
        COLUMN_ID, COLUMN_NAME, COLUMN_START, COLUMN_END, COLUMN_NUMBER
};
    public static final String TABLE_COURSE = "course";

    public static final String COLUMN_TERM_ID = TABLE_TERM + BaseColumns._ID;
    public static final String[] COLUMNS_COURSE = {
        COLUMN_ID, COLUMN_NAME, COLUMN_START, COLUMN_END, COLUMN_TERM_ID, COLUMN_STATUS//, COLUMN_TEXT
    };
    public static final String TABLE_ASSESSMENT = "assessment";
    public static final String COLUMN_COURSE_ID = TABLE_COURSE + BaseColumns._ID;

    public static final String[] COLUMNS_ASSESSMENT = {
        COLUMN_ID, COLUMN_NAME, COLUMN_START, COLUMN_END, COLUMN_COURSE_ID, COLUMN_TYPE//, COLUMN_TEXT
    };
    public static final String TABLE_NOTE = "note";
    public static final String COLUMN_ASSESSMENT_ID = TABLE_ASSESSMENT + BaseColumns._ID;
    public static final String[] COLUMNS_NOTE = {
        COLUMN_ID, COLUMN_COURSE_ID, COLUMN_ASSESSMENT_ID, COLUMN_TEXT, COLUMN_PHOTO_FILE_URI
    };

    public static final String TABLE_MENTOR = "mentor";
    public static final String COLUMN_MENTOR_ID = TABLE_MENTOR + BaseColumns._ID;
//    public static final String COLUMN_MENTOR_NAME = "name";
    public static final String SELECT_BY_COURSE_ID_MENTOR_ID = COLUMN_COURSE_ID + " = ? AND " + COLUMN_MENTOR_ID + " = ?";


    public static final String[] COLUMNS_MENTOR = {
        COLUMN_ID, COLUMN_NAME, COLUMN_PHONE_NUMBER, COLUMN_EMAIL
    };

    public static final String TABLE_COURSE_MENTOR = "course_mentor";
    public static final String[] COLUMNS_COURSE_MENTOR = {
        "ROWID as "+COLUMN_ID, COLUMN_COURSE_ID, COLUMN_MENTOR_ID
    };

    public static final String[] COLUMNS_EVENT = {
        COLUMN_ID, COLUMN_NAME, COLUMN_TIME, COLUMN_TERMINUS
    };
    public static final String TABLE_EVENT = "eventView";
    private static final String SELECT_EVENT_START = "SELECT "+COLUMN_ID+"*2 AS "+COLUMN_ID+", "
        +COLUMN_NAME+", "+COLUMN_START+" AS "+COLUMN_TIME+", '"+COLUMN_START+"' AS "+COLUMN_TERMINUS+" FROM ";
    private static final String SELECT_EVENT_END = "SELECT "+COLUMN_ID+"*2+1 AS "+COLUMN_ID+", "
        +COLUMN_NAME+", "+COLUMN_END+" AS "+COLUMN_TIME+", '"+COLUMN_END+"' AS "+COLUMN_TERMINUS+" FROM ";
    public static final int TERM_ID_OFFSET = 1_000_000;
    public static final int COURSE_ID_OFFSET = 2_000_000;
    public static final int ASSESSMENT_ID_OFFSET = 3_000_000;
    private static final String[] CREATE_DATABASE;

    public static final String DATABASE_FILE_NAME = "studentplanner.sqlite3";

    static {
        ArrayList<String> schema = new ArrayList<String>(30);
        schema.add("DROP VIEW IF EXISTS "+ TABLE_EVENT);
        schema.add("DROP TABLE IF EXISTS "+TABLE_NOTE);
        schema.add("DROP TABLE IF EXISTS "+TABLE_ASSESSMENT);
        schema.add("DROP TABLE IF EXISTS "+TABLE_COURSE);
        schema.add("DROP TABLE IF EXISTS "+TABLE_TERM);
        schema.add("DROP TABLE IF EXISTS "+TABLE_MENTOR);
        schema.add("DROP TABLE IF EXISTS "+TABLE_COURSE_MENTOR);

        schema.add("CREATE TABLE "+TABLE_TERM+" ("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            COLUMN_NAME+" TEXT, "+COLUMN_START+" INTEGER, "+COLUMN_END+" INTEGER, "+COLUMN_NUMBER+" INTEGER)");
        schema.add("INSERT INTO "+TABLE_TERM+" ("+COLUMN_ID+", "+COLUMN_NAME+", "+COLUMN_START+", "+
            COLUMN_END+", "+COLUMN_NUMBER+") VALUES ("+ TERM_ID_OFFSET +", 'A', 1, 1, 1);");

        schema.add("CREATE TABLE "+TABLE_COURSE+ "("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            COLUMN_NAME+" TEXT, "+COLUMN_START+" INTEGER, "+COLUMN_END+" INTEGER, " +
            COLUMN_TERM_ID+" INTEGER REFERENCES "+TABLE_TERM+"("+COLUMN_ID+"), "+
            COLUMN_STATUS+" INTEGER)"); // , "+COLUMN_TEXT+" TEXT
        schema.add("INSERT INTO "+TABLE_COURSE+" ("+COLUMN_ID+", "+COLUMN_NAME+", "+COLUMN_START+", "+
            COLUMN_END+", "+COLUMN_TERM_ID+", "+COLUMN_STATUS+") VALUES ("+ COURSE_ID_OFFSET +", 'B', 2, 2, "+TERM_ID_OFFSET+", 2);");

        schema.add("CREATE TABLE "+TABLE_ASSESSMENT+"("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            COLUMN_NAME+" TEXT, "+COLUMN_START+" INTEGER, "+COLUMN_END+" INTEGER, "+
            COLUMN_COURSE_ID+" INTEGER REFERENCES "+TABLE_COURSE+"("+COLUMN_ID+"), "+
            COLUMN_TYPE+" INTEGER)"); // , "+COLUMN_TEXT+" TEXT
        schema.add("INSERT INTO "+TABLE_ASSESSMENT+" ("+COLUMN_ID+", "+COLUMN_NAME+", "+COLUMN_START+", "+
            COLUMN_END+", "+COLUMN_COURSE_ID+", "+COLUMN_TYPE+") VALUES ("+ ASSESSMENT_ID_OFFSET +", 'C', 3, 3, "+COURSE_ID_OFFSET+", 3);");
        // ", "+COLUMN_TEXT+
        // , 'C'
        schema.add("CREATE TABLE "+ TABLE_NOTE +"("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_TEXT +" TEXT, "+
            COLUMN_ASSESSMENT_ID+" INTEGER REFERENCES "+TABLE_ASSESSMENT+"("+COLUMN_ID+"), "+
            COLUMN_COURSE_ID+" INTEGER REFERENCES "+TABLE_COURSE+"("+COLUMN_ID+"), "+COLUMN_PHOTO_FILE_URI +" TEXT)");

        schema.add("CREATE TABLE "+TABLE_MENTOR+"("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            COLUMN_NAME+" TEXT, "+COLUMN_PHONE_NUMBER+" TEXT, "+COLUMN_EMAIL+" TEXT)");

        schema.add("CREATE TABLE "+TABLE_COURSE_MENTOR+"("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            COLUMN_COURSE_ID+" INTEGER REFERENCES "+TABLE_COURSE+"("+COLUMN_ID+"), "+
            COLUMN_MENTOR_ID+" INTEGER REFERENCES "+TABLE_MENTOR+"("+COLUMN_ID+
            "), UNIQUE("+COLUMN_COURSE_ID+", "+COLUMN_MENTOR_ID+"))");
        schema.add("DELETE FROM "+TABLE_ASSESSMENT+" WHERE "+COLUMN_ID+" = "+ASSESSMENT_ID_OFFSET+";");
        schema.add("DELETE FROM "+TABLE_COURSE+" WHERE "+COLUMN_ID+" = "+COURSE_ID_OFFSET+";");
        schema.add("DELETE FROM "+TABLE_TERM+" WHERE "+COLUMN_ID+" = "+TERM_ID_OFFSET+";");

        schema.add("CREATE VIEW "+ TABLE_EVENT +" AS "+
            SELECT_EVENT_START + TABLE_TERM+" UNION "+SELECT_EVENT_END + TABLE_TERM+" UNION "+
            SELECT_EVENT_START + TABLE_COURSE+" UNION "+SELECT_EVENT_END + TABLE_COURSE + " UNION "+
            SELECT_EVENT_START + TABLE_ASSESSMENT+" UNION "+SELECT_EVENT_END + TABLE_ASSESSMENT+" ORDER BY "+COLUMN_TIME+" ASC;"
        );

        CREATE_DATABASE = schema.toArray(new String[schema.size()]);
    }
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
