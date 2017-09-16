package com.example.clement.studentplanner.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_ASSESSMENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_COURSE;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_COURSE_MENTOR;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_EVENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_MENTOR;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_NOTE;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_TERM;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_COURSE_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_MENTOR_ID;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_ASSESSMENT;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_COURSE;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_COURSE_MENTOR;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_EVENT;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_MENTOR;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_NOTE;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_TERM;

/**
 * Created by Clement on 9/15/2017.
 */

public class OmniProvider extends ContentProvider {
    private static final String[] TABLES = {
        null, // 0
        TABLE_TERM, // 1
        TABLE_COURSE, // 2
        TABLE_ASSESSMENT, // 3
        TABLE_MENTOR, // 4
        TABLE_NOTE, // 5
        TABLE_EVENT, // 6
        TABLE_COURSE_MENTOR // 7
    };
    private static final String[][] COLUMNS = {
        null, // 0
        COLUMNS_TERM, // 1
        COLUMNS_COURSE, // 2
        COLUMNS_ASSESSMENT, // 3
        COLUMNS_MENTOR, // 4
        COLUMNS_NOTE, // 5
        COLUMNS_EVENT, // 6
        COLUMNS_COURSE_MENTOR // 7
    };
    private static final int MASK_TABLE = 0xFF;
    private static final int MASK_SELECTION = 0xFF00;
    private static final int TERM = 0x01;
    private static final int COURSE = 0x02;
    private static final int ASSESSMENT = 0x03;
    private static final int MENTOR = 0x04;
    private static final int NOTE = 0x05;
    private static final int EVENT = 0x06;
    private static final int COURSEMENTOR = 0x07;
    private static final int SELECT_ID = 0x0100;
    private static final int SELECT_ALL = 0x0200;
    private static final int SELECT_EVENT = 0x0300;
    private static final int SELECT_COURSE_ID = 0x0400;
    private static final int SELECT_NOT_COURSE_ID = 0x0500;
    private static final int TERM_ID = TERM | SELECT_ID;
    private static final int TERM_ALL = TERM | SELECT_ALL;
    private static final int TERM_EVENT = TERM | SELECT_EVENT;
    private static final int COURSE_ID = COURSE | SELECT_ID;
    private static final int COURSE_ALL = COURSE | SELECT_ALL;
    private static final int COURSE_EVENT = COURSE | SELECT_EVENT;
    private static final int ASSESSMENT_ID = ASSESSMENT | SELECT_ID;
    private static final int ASSESSMENT_ALL = ASSESSMENT | SELECT_ALL;
    private static final int ASSESSMENT_EVENT = ASSESSMENT | SELECT_EVENT;
    private static final int MENTOR_ID = MENTOR | SELECT_ID;
    private static final int MENTOR_ALL = MENTOR | SELECT_ALL;
    private static final int MENTOR_COURSE_ID = MENTOR | SELECT_COURSE_ID;
    private static final int MENTOR_NOT_COURSE_ID = MENTOR | SELECT_NOT_COURSE_ID;
    private static final int NOTE_ID = NOTE | SELECT_ID;
    private static final int NOTE_ALL = NOTE | SELECT_ALL;
    private static final int EVENT_ALL = EVENT | SELECT_ALL;
    private static final int EVENT_ID = EVENT | SELECT_ID;
    private static final int COURSEMENTOR_ALL = COURSEMENTOR | SELECT_ALL;
    private static final int COURSEMENTOR_ID = COURSEMENTOR | SELECT_ID;

    public static final String authority = "com.example.clement.studentplanner.provider";
    public static final Uri CONTENT_BASE = new Uri.Builder()
        .scheme(SCHEME_CONTENT).authority(authority).build();

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    public static final Uri CONTENT_TERM   = addMatchUri(TERM_ALL,   TABLE_TERM);
    public static final Uri CONTENT_COURSE = addMatchUri(COURSE_ALL, TABLE_COURSE);
    public static final Uri CONTENT_ASSESSMENT = addMatchUri(ASSESSMENT_ALL, TABLE_ASSESSMENT);
    public static final Uri CONTENT_NOTE   = addMatchUri(NOTE_ALL,   TABLE_NOTE);
    public static final Uri CONTENT_MENTOR = addMatchUri(MENTOR_ALL, TABLE_MENTOR);
    public static final Uri CONTENT_TERM_ID   = appendMatchUri(TERM_ID,   CONTENT_TERM,   "#");
    public static final Uri CONTENT_COURSE_ID = appendMatchUri(COURSE_ID, CONTENT_COURSE, "#");
    public static final Uri CONTENT_ASSESSMENT_ID = appendMatchUri(ASSESSMENT_ID, CONTENT_ASSESSMENT, "#");
    public static final Uri CONTENT_NOTE_ID   = appendMatchUri(NOTE_ID,   CONTENT_NOTE,   "#");
    public static final Uri CONTENT_MENTOR_ID = appendMatchUri(MENTOR_ID, CONTENT_MENTOR, "#");

    public static final Uri CONTENT_EVENT = addMatchUri(EVENT_ALL, TABLE_EVENT);
    public static final Uri CONTENT_EVENT_ID = appendMatchUri(EVENT_ID, CONTENT_EVENT, "#");

    public static final Uri CONTENT_COURSEMENTOR = addMatchUri(
        COURSEMENTOR_ALL, TABLE_COURSE_MENTOR);
    public static final Uri CONTENT_COURSEMENTOR_ID = appendMatchUri(
        COURSEMENTOR_ID, CONTENT_COURSEMENTOR, "#");

    public static final Uri CONTENT_MENTOR_COURSE_ID = appendMatchUri(
        MENTOR_COURSE_ID, CONTENT_MENTOR, TABLE_COURSE, "#");
    public static final Uri CONTENT_MENTOR_NOT_COURSE_ID = appendMatchUri(
        MENTOR_NOT_COURSE_ID, CONTENT_MENTOR, "not_"+TABLE_COURSE, "#");

    private static final SQLiteQueryBuilder COURSE_QUERY = new SQLiteQueryBuilder();
    static {
        COURSE_QUERY.setTables(TABLE_MENTOR+" LEFT JOIN "+TABLE_COURSE_MENTOR+" ON "
            +TABLE_MENTOR+"."+COLUMN_ID+" = "+TABLE_COURSE_MENTOR+"."+COLUMN_MENTOR_ID);

        COURSE_QUERY.appendWhere(TABLE_COURSE_MENTOR+"."+COLUMN_COURSE_ID+" = ?");
    }
    private Cursor mentorByCourseIdQuery(long id) {
        return COURSE_QUERY.query(getReadableDatabase(),
            COLUMNS_MENTOR,
            null,
            new String[] { Long.toString(id) },
            null,
            null,
            COLUMN_ID+" ASC");
    }

    private static final SQLiteQueryBuilder NO_COURSE_QUERY = new SQLiteQueryBuilder();

    static {
        NO_COURSE_QUERY.setTables(TABLE_MENTOR);
        NO_COURSE_QUERY.appendWhere("NOT EXISTS ( SELECT 'x' FROM "+TABLE_COURSE_MENTOR
            +" WHERE "+TABLE_MENTOR+"."+COLUMN_ID+" = "+TABLE_COURSE_MENTOR+"."+COLUMN_MENTOR_ID
            +" AND "+COLUMN_COURSE_ID+" = ?))");
    }

    private Cursor mentorByNotCourseIdQuery(long id) {
        return NO_COURSE_QUERY.query(getReadableDatabase(),
            COLUMNS_MENTOR,
            null,
            new String[] { id + "" },
            null,
            null,
            COLUMN_ID+" ASC");
    }

    private static Uri buildPath(Uri base, String... paths) {
        Uri.Builder builder = base.buildUpon();
        for(String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }
    private static Uri addMatchUri(int id, String... paths) {
        return appendMatchUri(id, CONTENT_BASE, paths);
    }
    private static Uri appendMatchUri(int id, Uri base, String... paths) {
        Uri uri = buildPath(base, paths);
        URI_MATCHER.addURI(uri.getAuthority(), uri.getPath(), id);
        return uri;
    }

    /**
     * Map an event ID back to the source object ID
     * @param eventId The event ID
     * @return The source object ID
     */
    public static long eventToSource(long eventId) {
        return eventId >> 1L;
    }
    public static long startToEvent(long startId) {
        return startId << 1L;
    }
    public static long endToEvent(long endId) {
        return (endId << 1L) | 1L;
    }

    public static final String SELECTION_ID = COLUMN_ID + " = ?";
    public static String[] toStringArray(String s) {
        return new String[] { s };
    }
    public static String[] toStringArray(long l) {
        return new String[] { Long.toString(l) };
    }
    public static String[] toStringArray(Uri uri) {
        return new String[] { uri.toString() };
    }

    private SQLiteDatabase writableDatabase;
    private SQLiteDatabase readableDatabase;
    private StorageHelper helper;


    /**
     * Lazily initialize the writableDatabase object
     */
    @NonNull
    protected final synchronized StorageHelper getHelper() {
        if (helper == null) {
            Context context = getContext();
            if (context == null) {
                throw new NullPointerException();
            }
            helper = new StorageHelper(context);
        }
        return helper;
    }

    @NonNull
    protected final synchronized SQLiteDatabase getWritableDatabase() {
        if (writableDatabase == null) {
            writableDatabase = getHelper().getWritableDatabase();
        }
        return writableDatabase;
    }

    @NonNull
    protected final synchronized SQLiteDatabase getReadableDatabase() {
        if (readableDatabase == null) {
            readableDatabase = getHelper().getReadableDatabase();
        }
        return readableDatabase;
    }

    protected void notifyChange(@NonNull Uri uri) {
        Context context = getContext();
        if (context != null) {
            ContentResolver contentResolver = context.getContentResolver();
            if (contentResolver != null) {
                contentResolver.notifyChange(uri, null);
            }
        }
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        Cursor cursor;
        long id;
        int match = URI_MATCHER.match(uri);
        switch (match & MASK_SELECTION) {
            case SELECT_ALL:
                break;
            case SELECT_EVENT:
                if (projection == null) {
                    projection = COLUMNS_EVENT;
                }
                break;
            case SELECT_ID:
                id = ContentUris.parseId(uri);
                selection = COLUMN_ID + " = ?";
//                if ((match & MASK_TABLE) == EVENT) {
//                    switch(StorageHelper.classify(eventToSource(id))) {
//                        case TERM:
//                            match = TERM_ID;
//                            break;
//                        case COURSE:
//                            match = COURSE_ID;
//                            break;
//                        case ASSESSMENT:
//                            match = ASSESSMENT_ID;
//                            break;
//                    }
//                    selectionArgs = new String[] { Long.toString(eventToSource(id)) };
//                }
//                else {
                selectionArgs = new String[] { Long.toString(id) };
//                }
                break;
            case SELECT_COURSE_ID:
                if (match == MENTOR_COURSE_ID) {
                    id = ContentUris.parseId(uri);
                    cursor = mentorByCourseIdQuery(id);
                    cursor.setNotificationUri(getContext().getContentResolver(), uri);
                    return cursor;
                }
                throw new IllegalArgumentException();
            case SELECT_NOT_COURSE_ID:
                if (match == MENTOR_NOT_COURSE_ID) {
                    id = ContentUris.parseId(uri);
                    cursor = mentorByNotCourseIdQuery(id);
                    cursor.setNotificationUri(getContext().getContentResolver(), uri);
                    return cursor;
                }
                throw new IllegalArgumentException();
            default:
                throw new IllegalArgumentException();
        }

        int tableId = match & MASK_TABLE;
        String table = TABLES[tableId];
        if (projection == null) {
            projection = COLUMNS[tableId];
        }

        cursor = getWritableDatabase().query(
            table,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        );
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = URI_MATCHER.match(uri);
        String table = TABLES[match & MASK_TABLE];
        long id = getWritableDatabase().insert(
            table,
            null,
            contentValues
        );
        Uri newUri = ContentUris.withAppendedId(uri, id);
        notifyChange(newUri);
        return newUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int match = URI_MATCHER.match(uri);
        String table = TABLES[match & MASK_TABLE];
        int rowsAffected = getWritableDatabase().delete(table, selection, selectionArgs);
        if (rowsAffected > 0) {
            notifyChange(uri);
        }
        return rowsAffected;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = URI_MATCHER.match(uri);
        String table = TABLES[match & MASK_TABLE];
        int rowsAffected = getWritableDatabase().update(
            table, contentValues, selection, selectionArgs);
        if (rowsAffected > 0) {
            notifyChange(uri);
        }
        return rowsAffected;
    }
}
