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
import android.util.Log;

import static android.content.ContentResolver.SCHEME_CONTENT;

/**
 * Created by Clement on 8/13/2017.
 */

public class AssessmentProvider extends StudentContentProviderBase {
    public static final String AUTHORITY = "com.example.clement.studentplanner.assessmentprovider";
    public static final String BASE_PATH = "assessment";
    public static final Uri CONTENT_URI;
    public static final Uri EVENT_URI;
    private static final int ASSESSMENT_ALL = 1;
    private static final int ASSESSMENT_ID = 2;
    private static final int ASSESSMENT_EVENT = 3;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String CONTENT_ITEM_TYPE = "Assessment";
    static {
        Uri.Builder builder = new Uri.Builder()
            .scheme(SCHEME_CONTENT)
            .authority(AUTHORITY)
            .path(BASE_PATH);
        CONTENT_URI = builder.build();
        builder = builder.path("event");
        EVENT_URI = builder.build();
        Log.i(AssessmentProvider.class.getSimpleName(), EVENT_URI.getPath());
        uriMatcher.addURI(AUTHORITY, BASE_PATH, ASSESSMENT_ALL);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", ASSESSMENT_ID);
        uriMatcher.addURI(AUTHORITY, "event", ASSESSMENT_EVENT);
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
        switch(uriMatcher.match(uri)) {
            case ASSESSMENT_ID:
                selection = StorageHelper.COLUMN_ID + "=" + ContentUris.parseId(uri);
                // deliberate fallthrough, not a bug
            case ASSESSMENT_ALL:
                cursor = getDatabase().query(
                    StorageHelper.TABLE_ASSESSMENT,
                    StorageHelper.COLUMNS_ASSESSMENT,
                    selection,
                    null,
                    null,
                    null,
                    StorageHelper.COLUMN_ID + " ASC"
                );
                cursor.setNotificationUri(resolver, CONTENT_URI);
                break;
            case ASSESSMENT_EVENT:
                cursor = getDatabase().query(
                    StorageHelper.TABLE_ASSESSMENT,
                    StorageHelper.COLUMNS_EVENT,
                    selection,
                    null,
                    null,
                    null,
                    StorageHelper.COLUMN_ID + " ASC"
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
