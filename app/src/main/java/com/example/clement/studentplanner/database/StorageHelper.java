package com.example.clement.studentplanner.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_FILE_NAME;

/**
 * Created by Clement on 8/6/2017.
 */

public class StorageHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 6;
    public static final String TABLE_TERM = "term";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
//    public static final String COLUMN_EVENT_ID = "event_id";
//    public static final String TERM_NUMBER = "number";
    public static final String COLUMN_START = "start";
    public static final String COLUMN_END = "end";
    public static final String[] COLUMNS_TERM = {
        COLUMN_ID, COLUMN_NAME, COLUMN_START, COLUMN_END
};
/*    private static final String CREATE_TERM_TABLE = String.format(
        "CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT);",
        TABLE_TERM,
        TERM_ID,
        TERM_NAME,
        TERM_START,
        TERM_END
    );*/
    public static final String TABLE_COURSE = "course";
//    public static final String COURSE_ID = "_id";
//    public static final String COURSE_NAME = "name";
//    public static final String COURSE_START = "start";
//    public static final String COURSE_END = "end";
    public static final String COLUMN_TERM_ID = "term_id";
    public static final String COLUMN_STATUS = "status";
    public static final String[] COLUMNS_COURSE = {
        COLUMN_ID, COLUMN_NAME, COLUMN_START, COLUMN_END, COLUMN_TERM_ID, COLUMN_STATUS
    };
    public static final String TABLE_ASSESSMENT = "assessment";
    public static final String COLUMN_COURSE_ID = "course_id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_NOTES = "notes";

    public static final String[] COLUMNS_ASSESSMENT = {
        COLUMN_ID, COLUMN_NAME, COLUMN_START, COLUMN_END, COLUMN_COURSE_ID, COLUMN_TYPE, COLUMN_NOTES
    };
    public static final String TABLE_PHOTO = "photo";
    public static final String COLUMN_ASSESSMENT_ID = "assessment_id";
    public static final String COLUMN_FILE_NAME = "file_name";
    private static final String[] CREATE_DATABASE;
    static {
        ArrayList<String> schema = new ArrayList<String>(20);
        schema.add("DROP VIEW IF EXISTS termView");
        schema.add("DROP VIEW IF EXISTS courseView");
        schema.add("DROP VIEW IF EXISTS assessmentView");
        schema.add("DROP TABLE IF EXISTS "+TABLE_PHOTO);
        schema.add("DROP TABLE IF EXISTS "+TABLE_ASSESSMENT);
        schema.add("DROP TABLE IF EXISTS "+TABLE_COURSE);
        schema.add("DROP TABLE IF EXISTS "+TABLE_TERM);
        schema.add("DROP TABLE IF EXISTS event");
//        schema.add("CREATE TABLE event (_id INTEGER PRIMARY KEY, name TEXT, start INTEGER, end INTEGER)");
//        schema.add("CREATE TABLE term (_id INTEGER PRIMARY KEY, event_id INTEGER REFERENCES event(_id))");

        schema.add("CREATE TABLE "+TABLE_TERM+" ("+COLUMN_ID+" INTEGER AUTOINCREMENT PRIMARY KEY, "+
            COLUMN_NAME+" TEXT, "+COLUMN_START+" INTEGER, "+COLUMN_END+" INTEGER)");
        // TODO // schema.add("INSERT INTO "+TABLE_TERM+"")
//        schema.add("CREATE TABLE course (_id INTEGER PRIMARY KEY, event_id INTEGER REFERENCES event(_id), term_id INTEGER REFERENCES term(_id), status INTEGER)");

        schema.add("CREATE TABLE "+TABLE_COURSE+ "("+COLUMN_ID+" INTEGER AUTOINCREMENT PRIMARY KEY, "+
            COLUMN_NAME+" TEXT, "+COLUMN_START+" INTEGER, "+COLUMN_END+" INTEGER, " +
            COLUMN_TERM_ID+" INTEGER REFERENCES "+TABLE_TERM+"("+COLUMN_ID+"), "+
            COLUMN_STATUS+" INTEGER)");

//        schema.add("CREATE TABLE assessment(_id INTEGER PRIMARY KEY, event_id INTEGER REFERENCES event(_id), course_id INTEGER REFERENCES course(_id), type INTEGER, notes TEXT)");

        schema.add("CREATE TABLE "+TABLE_ASSESSMENT+"("+COLUMN_ID+" INTEGER AUTOINCREMENT PRIMARY KEY, "+
            COLUMN_NAME+" TEXT, "+COLUMN_START+" INTEGER, "+COLUMN_END+" INTEGER, "+
            COLUMN_COURSE_ID+" INTEGER REFERENCES "+TABLE_COURSE+"("+COLUMN_ID+"), "+
            COLUMN_TYPE+" INTEGER, "+COLUMN_NOTES+" TEXT)");

        schema.add("CREATE TABLE "+TABLE_PHOTO+"("+COLUMN_ID+" INTEGER AUTOINCREMENT PRIMARY KEY, "+
            COLUMN_ASSESSMENT_ID+" INTEGER REFERENCES "+TABLE_ASSESSMENT+"("+COLUMN_ID+"), "+
            COLUMN_FILE_NAME+" TEXT)");
//        schema.add("CREATE VIEW termView AS SELECT term._id AS _id, event_id, name, start, end FROM event JOIN term ON event._id = term.event_id");
//        schema.add("CREATE VIEW courseView AS SELECT course._id AS _id, event_id, name, start, end, term_id, status FROM event JOIN course ON event._id = course.event_id");
//        schema.add("CREATE VIEW assessmentView AS SELECT assessment._id AS _id, event_id, name, start, end, course_id, type, notes FROM event JOIN assessment ON event._id = assessment.event_id");
        /*
         * SQLite does not directly permit updates on views. The following triggers allow updates to
         * views to be passed along to the underlying tables as though the views were read/write
         */
/*
        String[] tables = { "term", "course", "assessment"};
        String[] fields = { "name", "start", "end"};
        for (String table : tables) {
            for (String field : fields) {
                // From SQLite documentation
                schema.add("CREATE TRIGGER " +
                    table+"_"+field+"_change INSTEAD OF UPDATE OF " +
                    field + " ON " + table+"View BEGIN UPDATE event SET " +
                    field + " = NEW."+field + " WHERE _id = NEW._id; END;\n"
                );
            }
        }
        // From https://stackoverflow.com/a/34127908
        schema.add("CREATE TRIGGER term_event_insert INSTEAD OF INSERT ON termView BEGIN " +
            "INSERT INTO event (name, start, end) values (NEW.name, NEW.start, NEW.end);" +
            "INSERT INTO term (event_id) values (NEW._id); END;"
        );*/
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
    public StorageHelper(Context context) {
        super(context, "schedule", null, DATABASE_VERSION);
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
    public void erase(SQLiteDatabase db) {
        createDatabase(db);
    }
}
