package com.example.clement.studentplanner.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;

/**
 * Created by Clement on 8/17/2017.
 */

abstract class StudentContentProviderBase extends ContentProvider {
    private SQLiteDatabase writableDatabase;
    private SQLiteDatabase readableDatabase;
    private StorageHelper helper;
    protected abstract @NonNull String getTableName();
    protected abstract @NonNull Uri getContentUri();
    protected abstract @NonNull UriMatcher getUriMatcher();
    protected abstract int getSingleRowMatchConstant();
    /**
     * Lazily initialize the writableDatabase object
     */
    @NonNull
    protected final synchronized StorageHelper getHelper() {
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
    protected final synchronized SQLiteDatabase getWritableDatabase() {
        if (writableDatabase == null) {
            writableDatabase = getHelper().getWritableDatabase();
        }
        return writableDatabase;
    }
    @NonNull
    protected final synchronized SQLiteDatabase getReadableDatabase() {
        if (readableDatabase == null) {
            readableDatabase = getHelper().getReadableDatabase();
        }
        return readableDatabase;
    }
    protected void notifyChange(@NonNull Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsUpdated = getWritableDatabase().update(getTableName(), values, selection, selectionArgs);
        if (rowsUpdated > 0) {
            notifyChange(uri);
        }
        return rowsUpdated;
    }
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (getUriMatcher().match(uri) == getSingleRowMatchConstant()) {
                selection = COLUMN_ID + " = " + ContentUris.parseId(uri);
        }
        int rowsAffected = getWritableDatabase().delete(
            getTableName(),
            selection,
            null
        );
        if (rowsAffected > 0) {
            notifyChange(uri);
        }
        return rowsAffected;
    }
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = getWritableDatabase().insert(
            getTableName(),
            null,
            values
        );
        Uri newUri = ContentUris.withAppendedId(getContentUri(), id);
        notifyChange(newUri);
        return newUri;
    }
}
