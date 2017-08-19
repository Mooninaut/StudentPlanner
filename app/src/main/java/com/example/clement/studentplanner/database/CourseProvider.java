package com.example.clement.studentplanner.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
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
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_COURSE;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_EVENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_COURSE;

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
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", COURSE_ID);
        uriMatcher.addURI(AUTHORITY, "event", COURSE_EVENT);
        uriMatcher.addURI(AUTHORITY, BASE_PATH, COURSE_ALL);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        ContentResolver resolver = getContext().getContentResolver();
        projection = COLUMNS_EVENT;
        switch(uriMatcher.match(uri)) {
            case COURSE_ID:
                selection = COLUMN_ID + "=" + uri.getLastPathSegment();
                // deliberate fallthrough, not a bug
            case COURSE_ALL:
                projection = COLUMNS_COURSE;
                // deliberate fallthrough, not a bug
            case COURSE_EVENT:
                cursor = getDatabase().query(
                    TABLE_COURSE,
                    projection, // COLUMNS_EVENT if COURSE_EVENT, otherwise COLUMNS_COURSE
                    selection,
                    null,
                    null,
                    null,
                    COLUMN_ID + " ASC"
                );
                cursor.setNotificationUri(resolver, CONTENT_URI);
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
        long id = getDatabase().insert(
            TABLE_COURSE,
            null,
            values
        );
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return getDatabase().delete(TABLE_COURSE, selection, selectionArgs);
    }
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return getDatabase().update(TABLE_COURSE, values, selection, selectionArgs);
    }

    /**
     * Erases all data in the database, not just Courses.
     */
    public void erase() {
        getHelper().erase(getDatabase());
    }
}
