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

import com.example.clement.studentplanner.data.Term;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_EVENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_TERM;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NUMBER;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_TERM;

/**
 * Created by Clement on 8/6/2017.
 */

public class TermProvider extends StudentContentProviderBase {
    public static final String AUTHORITY = "com.example.clement.studentplanner.termprovider";
    public static final String PATH_BASE = "term";
    public static final String PATH_EVENT = "event";
    public static final String PATH_MAX = "max";
    public static final Uri CONTENT_URI;
    public static final Uri EVENT_URI;
    public static final Uri MAX_TERM_URI;
    private static final int TERM_ALL = 1;
    private static final int TERM_ID = 2;
    private static final int TERM_EVENT = 3;
    private static final int TERM_MAX = 4;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String CONTENT_ITEM_TYPE = "Term";
    static {
        Uri.Builder builder = new Uri.Builder()
            .scheme(SCHEME_CONTENT)
            .authority(AUTHORITY);
        CONTENT_URI = builder.path(PATH_BASE).build();
        EVENT_URI = builder.path(PATH_EVENT).build();
        MAX_TERM_URI = builder.path(PATH_MAX).build();
        Log.i(TermProvider.class.getSimpleName(), EVENT_URI.getPath());
        uriMatcher.addURI(AUTHORITY, PATH_BASE, TERM_ALL);
        uriMatcher.addURI(AUTHORITY, PATH_BASE + "/#", TERM_ID);
        uriMatcher.addURI(AUTHORITY, PATH_EVENT, TERM_EVENT);
        uriMatcher.addURI(AUTHORITY, PATH_MAX, TERM_MAX);
    }
    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        ContentResolver resolver = getContext().getContentResolver();
        projection = COLUMNS_EVENT;
        switch(uriMatcher.match(uri)) {
            case TERM_ID:
                selection = COLUMN_ID + " = " + ContentUris.parseId(uri);
                // deliberate fallthrough, not a bug
            case TERM_ALL:
                projection = COLUMNS_TERM;
                // deliberate fallthrough, not a bug
            case TERM_EVENT:
                cursor = getDatabase().query(
                    TABLE_TERM,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    COLUMN_ID + " ASC"
                );
                cursor.setNotificationUri(resolver, CONTENT_URI);
                break;
            case TERM_MAX:
                cursor = getDatabase().rawQuery(
                    "SELECT MAX("+COLUMN_NUMBER+") FROM "+TABLE_TERM,
                    null
                );
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

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = getDatabase().insert(
            TABLE_TERM,
            null,
            values
        );
        Uri changeUri = ContentUris.withAppendedId(CONTENT_URI, id);
        notifyChange(changeUri);
        return changeUri;
    }

    @NonNull
    public static ContentValues termToValues(@NonNull Term term) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NUMBER, term.getNumber());
        values.put(StorageHelper.COLUMN_NAME, term.getName());
        values.put(StorageHelper.COLUMN_START, term.getStartMillis());
        values.put(StorageHelper.COLUMN_END, term.getEndMillis());
//        values.put(StorageHelper.COLUMN_ID, term.getId());
        return values;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (uriMatcher.match(uri) == TERM_ID) {
            selection = COLUMN_ID + " = " + ContentUris.parseId(uri);
        }
        int rowsAffected = getDatabase().delete(
            TABLE_TERM,
            selection,
            null
        );
        if (rowsAffected > 0) {
            notifyChange(CONTENT_URI);
        }
        return rowsAffected;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsAffected = getDatabase().update(TABLE_TERM, values, selection, selectionArgs);
        if (rowsAffected > 0) {
            notifyChange(CONTENT_URI);
        }
        return rowsAffected;
    }
    public void erase() {
        getHelper().erase(getDatabase());
    }
}
