package com.example.clement.studentplanner.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
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

abstract public class ContentProviderBase extends ContentProvider {
    private SQLiteDatabase writableDatabase;
    private SQLiteDatabase readableDatabase;
    private StorageHelper helper;
    public abstract @NonNull ProviderContract getContract();
    protected abstract @NonNull String getTableName();
    protected abstract @NonNull UriMatcher getUriMatcher();
    protected abstract int getSingleRowMatchConstant();

    public static final String SELECTION_ID = COLUMN_ID + " = ?";
    public static String[] toStringArray(String s) {
        return new String[] { s };
    }
    public static String[] toStringArray(long l) {
        return new String[] { Long.toString(l) };
    }
    public static String[] toStringArray(Uri uri) {
        return new String[] { uri.toString() };
    }
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
        Context context = getContext();
        if (context != null) {
            ContentResolver contentResolver = context.getContentResolver();
            if (contentResolver != null) {
                contentResolver.notifyChange(uri, null);
            }
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        long id = ContentUris.parseId(uri);
        selection = SELECTION_ID;
        selectionArgs = new String[] { Long.toString(id) };
        int rowsUpdated = getWritableDatabase().update(getTableName(), values, selection, selectionArgs);
        if (rowsUpdated > 0) {
            notifyChange(uri);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (getUriMatcher().match(uri) == getSingleRowMatchConstant()) {
            long id = ContentUris.parseId(uri);
            selection = SELECTION_ID;
            selectionArgs = new String[] { Long.toString(id) };
        }
        int rowsAffected = getWritableDatabase().delete(
            getTableName(),
            selection,
            selectionArgs
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
        Uri newUri = getContract().contentUri(id);
        notifyChange(newUri);
        return newUri;
    }
}
