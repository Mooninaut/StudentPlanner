package com.example.clement.studentplanner.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Clement on 8/12/2017.
 */

public class CourseProvider extends ContentProvider {
    public static final String AUTHORITY = "com.example.clement.studentplanner.courseprovider";
    public static final String BASE_PATH = "course";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    private static final int COURSE_ALL = 1;
    private static final int COURSE_ID = 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String CONTENT_ITEM_TYPE = "Course";
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, COURSE_ALL);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", COURSE_ID);
    }
    private SQLiteDatabase database;
    private StorageHelper helper;
    @Override
    public boolean onCreate() {
        return true;
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
        if (uriMatcher.match(uri) == COURSE_ID) {
            selection = StorageHelper.COLUMN_ID + "=" + uri.getLastPathSegment();
        }
        return getDatabase().query(
            StorageHelper.TABLE_COURSE,
            StorageHelper.COLUMNS_COURSE,
            selection,
            null,
            null,
            null,
            StorageHelper.COLUMN_ID + " ASC"
        );
    }
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = getDatabase().insert(
            StorageHelper.TABLE_COURSE,
            null,
            values
        );
        return Uri.parse(BASE_PATH + '/' + id);
    }
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return getDatabase().delete(StorageHelper.TABLE_COURSE, selection, selectionArgs);
    }
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return getDatabase().update(StorageHelper.TABLE_COURSE, values, selection, selectionArgs);
    }

    /**
     * Erases all data in the database, not just Courses.
     */
    public void erase() {
        helper.erase(getDatabase());
    }
}
