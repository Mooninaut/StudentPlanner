package com.example.clement.studentplanner.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Clement on 8/6/2017.
 */

public class TermProvider extends ContentProvider {
    public static final String AUTHORITY = "com.example.clement.studentplanner.termprovider";
    public static final String BASE_PATH = "term";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    private static final int TERM_ALL = 1;
    private static final int TERM_ID = 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String CONTENT_ITEM_TYPE = "Term";
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, TERM_ALL);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TERM_ID);
    }
    private SQLiteDatabase database;
    private StorageHelper helper;
    @Override
    public boolean onCreate() {
        helper = new StorageHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (uriMatcher.match(uri) == TERM_ID) {
            selection = StorageHelper.TERM_ID + "=" + uri.getLastPathSegment();
        }
        return database.query(
            StorageHelper.TERM_TABLE,
            StorageHelper.TERM_COLUMNS,
            selection,
            null,
            null,
            null,
            StorageHelper.TERM_ID + " ASC"
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
        long id = database.insert(
            StorageHelper.TERM_TABLE,
            null,
            values
        );
        return Uri.parse(BASE_PATH + '/' + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.delete(StorageHelper.TERM_TABLE, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.update(StorageHelper.TERM_TABLE, values, selection, selectionArgs);
    }
    public void erase() {
        helper.erase(database);
    }
}
