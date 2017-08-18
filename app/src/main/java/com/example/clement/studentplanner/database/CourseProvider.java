package com.example.clement.studentplanner.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.content.ContentResolver.SCHEME_CONTENT;
/**
 * Created by Clement on 8/12/2017.
 */

public class CourseProvider extends StudentContentProviderBase {
    public static final String AUTHORITY = "com.example.clement.studentplanner.courseprovider";
    public static final String BASE_PATH = "course";
    public static final Uri CONTENT_URI;
    public static final Uri EVENT_URI;
    private static final int COURSE_ALL = 1;
    private static final int COURSE_ID = 2;
    private static final int COURSE_EVENT = 3;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String CONTENT_ITEM_TYPE = "Course";
    static {
        Uri.Builder builder = new Uri.Builder()
            .scheme(SCHEME_CONTENT)
            .authority(AUTHORITY)
            .path(BASE_PATH);
        CONTENT_URI = builder.build();
        builder = builder.path("event");
        EVENT_URI = builder.build();
        uriMatcher.addURI(AUTHORITY, BASE_PATH, COURSE_ALL);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", COURSE_ID);
        uriMatcher.addURI(AUTHORITY, "event", COURSE_EVENT);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = uriMatcher.match(uri);
        switch(match) {
            case COURSE_ID:
                selection = StorageHelper.COLUMN_ID + "=" + uri.getLastPathSegment();
                // deliberate fallthrough, not a bug
            case COURSE_ALL:
                return getDatabase().query(
                    StorageHelper.TABLE_COURSE,
                    StorageHelper.COLUMNS_COURSE,
                    selection,
                    null,
                    null,
                    null,
                    StorageHelper.COLUMN_ID + " ASC"
                );
            case COURSE_EVENT:
                return getDatabase().query(
                    StorageHelper.TABLE_COURSE,
                    StorageHelper.COLUMNS_EVENT,
                    selection,
                    null,
                    null,
                    null,
                    StorageHelper.COLUMN_ID + " ASC"
                );
            default:
                return null;
        }
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
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return ContentUris.withAppendedId(CONTENT_URI, id);
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
        getHelper().erase(getDatabase());
    }
}
