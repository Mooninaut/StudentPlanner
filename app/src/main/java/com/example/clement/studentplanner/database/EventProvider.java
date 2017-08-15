package com.example.clement.studentplanner.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.content.ContentResolver.SCHEME_CONTENT;

/**
 * Created by Clement on 8/13/2017.
 */

public class EventProvider extends ContentProvider {
    public static final String AUTHORITY = "com.example.clement.studentplanner.eventprovider";
    public static final String BASE_PATH = "event";
    public static final String COURSE_PATH = "course";
    public static final String ASSESSMENT_PATH = "assessment";
    public static final String TERM_PATH = "term";
    public static final String EVENT_ID = "_id";
    public static final String EVENT_START = "start";
    public static final String EVENT_END = "end";
    public static final String EVENT_NAME = "name";
    public static final Uri CONTENT_URI = new Uri.Builder()
        .scheme(SCHEME_CONTENT)
        .authority(AUTHORITY)
        .path(BASE_PATH)
        .build();
    private static final int EVENT_ALL = 1;
    public static final int EVENT_COURSE_ID = 2;
    public static final int EVENT_TERM_ID = 3;
    public static final int EVENT_ASSESSMENT_ID = 4;
    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String CONTENT_ITEM_TYPE = "Event";
    public static final String[] EVENT_COLUMNS = {
        EVENT_ID, EVENT_NAME, EVENT_START, EVENT_END
    };
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, EVENT_ALL);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/" + COURSE_PATH + "/#", EVENT_COURSE_ID);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/" + TERM_PATH + "/#", EVENT_TERM_ID);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/" + ASSESSMENT_PATH + "/#", EVENT_ASSESSMENT_ID);
    }
    private TermProvider termProvider;
    private CourseProvider courseProvider;
    private AssessmentProvider assessmentProvider;

    private StorageHelper helper;
    private SQLiteDatabase database;
    @Override
    public boolean onCreate() {
//        helper = new StorageHelper(getContext());
        return true;
    }

    /**
     * Lazily initialize the providers
     */
    @NonNull
    private synchronized TermProvider getTermProvider() {
        if (termProvider == null) {
            termProvider = new TermProvider();
        }
        return termProvider;
    }
    @NonNull
    private synchronized CourseProvider getCourseProvider() {
        if (courseProvider == null) {
            courseProvider = new CourseProvider();
        }
        return courseProvider;
    }
    @NonNull
    private synchronized AssessmentProvider getAssessmentProvider() {
        if (assessmentProvider == null) {
            assessmentProvider = new AssessmentProvider();
        }
        return assessmentProvider;
    }
    /**
     * Lazily initialize the database object
     */
    @NonNull
    private synchronized StorageHelper getHelper() {
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
    private synchronized SQLiteDatabase getDatabase() {
        if (database == null) {
            database = getHelper().getWritableDatabase();
        }
        return database;
    }
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = uriMatcher.match(uri);
        Cursor cursor;
        switch(match) {
            case EVENT_TERM_ID:
                uri = ContentUris.withAppendedId(TermProvider.CONTENT_URI, ContentUris.parseId(uri));
                cursor = getTermProvider().query(uri, EVENT_COLUMNS, selection, selectionArgs, sortOrder);
                break;
            case EVENT_ALL:
                cursor = new MergeCursor(new Cursor[]{getDatabase().rawQuery("SELECT "+StorageHelper.COLUMN_ID+", "+StorageHelper.COLUMN_NAME+", "+
                    StorageHelper.COLUMN_START+", "+StorageHelper.COLUMN_END+" FROM "+StorageHelper.TABLE_TERM+";", null)}
                );
                break;
            case EVENT_COURSE_ID:
                cursor = getCourseProvider().query(uri, EVENT_COLUMNS, selection, selectionArgs, sortOrder);
                break;
            case EVENT_ASSESSMENT_ID:
                cursor = getAssessmentProvider().query(uri, EVENT_COLUMNS, selection, selectionArgs, sortOrder);
                break;

            default:
                cursor = null;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
