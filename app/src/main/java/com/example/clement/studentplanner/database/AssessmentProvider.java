package com.example.clement.studentplanner.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_ASSESSMENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_EVENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_COURSE_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_ASSESSMENT;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_COURSE;

/**
 * Created by Clement on 8/13/2017.
 */

public class AssessmentProvider extends StudentContentProviderBase {
    public static final String AUTHORITY = "com.example.clement.studentplanner.assessmentprovider";
    public static final String BASE_PATH = "assessment";
    public static final Uri CONTENT_URI;
    public static final Uri EVENT_URI;
    public static final Uri COURSE_URI;
    private static final int ASSESSMENT_ALL = 1;
    private static final int ASSESSMENT_ID = 2;
    private static final int ASSESSMENT_EVENT = 3;
    private static final int ASSESSMENT_COURSE = 4;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String CONTENT_ITEM_TYPE = "Assessment";
    static {
        Uri.Builder builder = new Uri.Builder()
            .scheme(SCHEME_CONTENT)
            .authority(AUTHORITY)
            .path(BASE_PATH);
        CONTENT_URI = builder.build();
        EVENT_URI = builder.path(EventProvider.BASE_PATH).build();
        COURSE_URI = builder.path(CourseProvider.BASE_PATH).build();
//        Log.i(AssessmentProvider.class.getSimpleName(), EVENT_URI.getPath());
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", ASSESSMENT_ID);
        uriMatcher.addURI(AUTHORITY, EventProvider.BASE_PATH, ASSESSMENT_EVENT);
        uriMatcher.addURI(AUTHORITY, CourseProvider.BASE_PATH + "/#", ASSESSMENT_COURSE);
        uriMatcher.addURI(AUTHORITY, BASE_PATH, ASSESSMENT_ALL);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        ContentResolver resolver = getContext().getContentResolver();
        projection = COLUMNS_ASSESSMENT;
        switch(uriMatcher.match(uri)) {
            case ASSESSMENT_ID:
                selection = COLUMN_ID + "=" + ContentUris.parseId(uri);
                break;
            case ASSESSMENT_ALL:
                break;
            case ASSESSMENT_EVENT:
                projection = COLUMNS_EVENT;
                break;
            case ASSESSMENT_COURSE:
                selection = COLUMN_COURSE_ID + "=" + ContentUris.parseId(uri);
                break;
            default:
                return null;
        }
        cursor = getWritableDatabase().query(
            TABLE_ASSESSMENT,
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

    @NonNull
    @Override
    protected String getTableName() {
        return TABLE_ASSESSMENT;
    }

    @NonNull
    @Override
    protected Uri getContentUri() {
        return CONTENT_URI;
    }

    @NonNull
    @Override
    protected UriMatcher getUriMatcher() {
        return uriMatcher;
    }

    @Override
    protected int getSingleRowMatchConstant() {
        return ASSESSMENT_ID;
    }

/*    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted = getWritableDatabase().delete(TABLE_ASSESSMENT, selection, selectionArgs);
        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        }
        return rowsDeleted;
    }*/

/*    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsUpdated = getWritableDatabase().update(TABLE_COURSE, values, selection, selectionArgs);
        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        }
        return rowsUpdated;
    }*/
}
