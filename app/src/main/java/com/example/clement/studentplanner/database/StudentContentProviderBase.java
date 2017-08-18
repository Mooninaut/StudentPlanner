package com.example.clement.studentplanner.database;

import android.content.ContentProvider;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by Clement on 8/17/2017.
 */

abstract class StudentContentProviderBase extends ContentProvider {
    private SQLiteDatabase database;
    private StorageHelper helper;
    /**
     * Lazily initialize the database object
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
    protected final synchronized SQLiteDatabase getDatabase() {
        if (database == null) {
            database = getHelper().getWritableDatabase();
        }
        return database;
    }
    protected void notifyChange(@NonNull Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }
}
