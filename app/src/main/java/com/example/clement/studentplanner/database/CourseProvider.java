package com.example.clement.studentplanner.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static android.content.ContentResolver.getCurrentSyncs;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_COURSE;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_EVENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_TERM_ID;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_COURSE;

/**
 * Created by Clement on 8/12/2017.
 */

public class CourseProvider extends StudentContentProviderBase {
    public static final String AUTHORITY = "com.example.clement.studentplanner.courseprovider";
    public static final String BASE_PATH = "course";
    public static final Uri CONTENT_URI;
    public static final Uri EVENT_URI;
    public static final Uri TERM_URI;
    private static final int COURSE_ALL = 1;
    private static final int COURSE_ID = 2;
    private static final int COURSE_EVENT = 3;
    private static final int COURSE_TERM = 4;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String CONTENT_ITEM_TYPE = "Course";
    static {
        Uri.Builder builder = new Uri.Builder()
            .scheme(SCHEME_CONTENT)
            .authority(AUTHORITY)
            .path(BASE_PATH);
        CONTENT_URI = builder.build();
        EVENT_URI = builder.path(EventProvider.BASE_PATH).build();
        TERM_URI = builder.path(TermProvider.BASE_PATH).build();
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", COURSE_ID);
        uriMatcher.addURI(AUTHORITY, EventProvider.BASE_PATH, COURSE_EVENT);
        uriMatcher.addURI(AUTHORITY, TermProvider.BASE_PATH + "/#", COURSE_TERM);
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
        int match = uriMatcher.match(uri);
        projection = COLUMNS_COURSE;
        switch (match) {
            case COURSE_EVENT:
                projection = COLUMNS_EVENT;
                break;
            case COURSE_ID:
                selection = COLUMN_ID + "=" + ContentUris.parseId(uri);
                break;
            case COURSE_ALL:
                break;
            case COURSE_TERM:
                selection = COLUMN_TERM_ID + "=" + ContentUris.parseId(uri);
                break;
            default:
                return null;
        }
        cursor = getWritableDatabase().query(
            TABLE_COURSE,
            projection,
            selection,
            null,
            null,
            null,
            COLUMN_ID + " ASC"
        );
        cursor.setNotificationUri(resolver, CONTENT_URI);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

/*    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = getWritableDatabase().insert(
            TABLE_COURSE,
            null,
            values
        );
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }*/
/*    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted = getWritableDatabase().delete(TABLE_COURSE, selection, selectionArgs);
        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        }
        return rowsDeleted;
    }*/

    @NonNull
    @Override
    protected String getTableName() {
        return TABLE_COURSE;
    }

    @NonNull
    @Override
    protected Uri getContentUri() {
        return CONTENT_URI;
    }

    @NonNull
    @Override
    public UriMatcher getUriMatcher() {
        return uriMatcher;
    }

    @Override
    protected int getSingleRowMatchConstant() {
        return COURSE_ID;
    }
    /*    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsUpdated = getWritableDatabase().update(TABLE_COURSE, values, selection, selectionArgs);
        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        }
        return rowsUpdated;
    }*/

    /**
     * Erases all data in the database, not just Courses.
     */
/*    public void erase() {
        getHelper().erase(getWritableDatabase());
    }*/
}
