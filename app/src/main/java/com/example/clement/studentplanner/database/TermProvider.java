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
import android.util.Log;

import com.example.clement.studentplanner.data.Term;

import static android.content.ContentResolver.SCHEME_CONTENT;

/**
 * Created by Clement on 8/6/2017.
 */

public class TermProvider extends ContentProvider {
    public static final String AUTHORITY = "com.example.clement.studentplanner.termprovider";
    public static final String BASE_PATH = "term";
    public static final Uri CONTENT_URI = new Uri.Builder()
        .scheme(SCHEME_CONTENT)
        .authority(AUTHORITY)
        .path(BASE_PATH)
        .build();
    private static final int TERM_ALL = 1;
    private static final int TERM_ID = 2;
    private static final int TERM_EVENT = 3;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String CONTENT_ITEM_TYPE = "Term";
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, TERM_ALL);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TERM_ID);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/event/#", TERM_EVENT);
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
    private synchronized SQLiteDatabase getDatabase() {
        if (database == null) {
            database = getStorageHelper().getWritableDatabase();
        }
        return database;
    }
    @NonNull
    private synchronized StorageHelper getStorageHelper() {
        if (helper == null) {
            Context context = getContext();
            if (context == null) {
                throw new NullPointerException();
            }
            helper = new StorageHelper(context);
        }
        return helper;
    }
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = uriMatcher.match(uri);
        switch(match) {
            case TERM_ID:
                selection = StorageHelper.COLUMN_ID + " = " + uri.getLastPathSegment();
                // deliberate fallthrough, not a bug
            case TERM_ALL:
                return getDatabase().query(
                    StorageHelper.TABLE_TERM,
                    StorageHelper.COLUMNS_TERM,
                    selection,
                    null,
                    null,
                    null,
                    StorageHelper.COLUMN_ID + " ASC"
                );
            case TERM_EVENT:
                return getDatabase().query(
                    StorageHelper.TABLE_TERM,
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

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = getDatabase().insert(
            StorageHelper.TABLE_TERM,
            null,
            values
        );
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    @NonNull
    public static ContentValues termToValues(@NonNull Term term) {
        ContentValues values = new ContentValues();
//        values.put(StorageHelper.COLUMN_ID, term.getId());
        values.put(StorageHelper.COLUMN_NAME, term.getName());
        values.put(StorageHelper.COLUMN_START, term.getStartMillis());
        values.put(StorageHelper.COLUMN_END, term.getEndMillis());
        return values;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (uriMatcher.match(uri) == TERM_ID) {
            selection = StorageHelper.COLUMN_ID + " = " + ContentUris.parseId(uri);
        }
        Log.i(this.getClass().getSimpleName(), "About to request database instance");
        return getDatabase().delete(
            StorageHelper.TABLE_TERM,
            selection,
            null
        );
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return getDatabase().update(StorageHelper.TABLE_TERM, values, selection, selectionArgs);
    }
    public void erase() {
        getStorageHelper().erase(getDatabase());
    }
}
